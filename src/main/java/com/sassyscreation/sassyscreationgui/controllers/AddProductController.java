package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the "Add Product" screen (ADD_PRODUCT.fxml - use your own file name).
 */
public class AddProductController implements Initializable {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    private static final int STOCK_MIN = 0;
    private static final int STOCK_MAX = 9999;

    // ---- sidebar ----
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Label lblNavOrdersBadge;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Button btnLogout;

    // ---- account menu ----
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

    // ---- header ----
    @FXML private Button btnBackInv;

    // ---- photo ----
    @FXML private StackPane boxProductPhoto;
    @FXML private ImageView ivProductPhoto;
    @FXML private Label lblPhotoHint;

    // ---- form ----
    @FXML private TextField tfProdName;
    @FXML private ComboBox<String> cbProdCategory;
    @FXML private TextField tfProdPrice;
    @FXML private Spinner<Integer> spStock;
    @FXML private Button btnCancelProduct;
    @FXML private Button btnSaveProduct;

    /** Path of the chosen photo, or null if none was picked. */
    private String photoPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Make sure the spinner has a value factory (0 - 9999, start at 0)
        if (spStock.getValueFactory() == null) {
            spStock.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(STOCK_MIN, STOCK_MAX, 0));
        }

        // Only allow digits and one decimal point (max 2 decimals) in the price field
        tfProdPrice.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("\\d*(\\.\\d{0,2})?")) {
                tfProdPrice.setText(oldText);
            }
        });

        // Only allow digits (max 4) in the stock spinner's editor
        spStock.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("\\d{0,4}")) {
                spStock.getEditor().setText(oldText);
            }
        });

        // Commit the typed spinner value when the field loses focus
        spStock.focusedProperty().addListener((obs, was, isFocused) -> {
            if (!isFocused) {
                readStock();
            }
        });

        // Badge: don't reserve space when hidden
        lblNavOrdersBadge.managedProperty().bind(lblNavOrdersBadge.visibleProperty());

        // Click the photo box to choose an image
        boxProductPhoto.setCursor(Cursor.HAND);
        boxProductPhoto.setOnMouseClicked(e -> choosePhoto());

        // TODO: call setOrdersBadge(<pending order count from DB>)
    }

    /** Updates the sidebar badge on "Orders"; hides it when the count is 0. */
    public void setOrdersBadge(int count) {
        lblNavOrdersBadge.setText(String.valueOf(count));
        lblNavOrdersBadge.setVisible(count > 0);
    }

    // =====================================================================
    //  Photo
    // =====================================================================

    private void choosePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose product photo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

        File file = chooser.showOpenDialog(currentStage());
        if (file == null) {
            return;
        }

        Image image = new Image(file.toURI().toString(), 160, 150, true, true);
        if (image.isError()) {
            showError("Invalid image", "That file could not be loaded as an image.");
            return;
        }

        ivProductPhoto.setImage(image);
        lblPhotoHint.setVisible(false);
        photoPath = file.getAbsolutePath();
    }

    // =====================================================================
    //  Save
    // =====================================================================

    @FXML
    private void handleSaveProduct() {
        String name = tfProdName.getText() == null ? "" : tfProdName.getText().trim();
        String category = cbProdCategory.getValue();
        String priceText = tfProdPrice.getText() == null ? "" : tfProdPrice.getText().trim();

        if (name.isEmpty()) {
            showWarning("Missing name", "Please enter a product name.");
            tfProdName.requestFocus();
            return;
        }
        if (category == null) {
            showWarning("Missing category", "Please select a category.");
            cbProdCategory.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showWarning("Invalid price", "Enter a price of 0 or more (e.g. 55.00).");
            tfProdPrice.requestFocus();
            return;
        }

        int stock = readStock();
        if (stock < 0) {
            showWarning("Invalid stock",
                    "Stock must be a whole number from " + STOCK_MIN + " to " + STOCK_MAX + ".");
            spStock.requestFocus();
            return;
        }

        // TODO: also INSERT the product into the database (photoPath is available if you store it)
        InventoryController.addProduct(name, category, price, stock);

        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.initOwner(currentStage());
        ok.setTitle("Product saved");
        ok.setHeaderText(null);
        ok.setContentText("\"" + name + "\" was added to the inventory.");
        ok.showAndWait();

        goInventory();
    }

    /**
     * Reads the spinner's text (it is editable), writes the clean value back
     * into the editor, and returns it. Returns -1 if invalid.
     */
    private int readStock() {
        try {
            String text = spStock.getEditor().getText() == null ? "" : spStock.getEditor().getText().trim();
            int value = text.isEmpty() ? 0 : Integer.parseInt(text);
            if (value < STOCK_MIN || value > STOCK_MAX) {
                return -1;
            }
            spStock.getValueFactory().setValue(value);
            spStock.getEditor().setText(String.valueOf(value));
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // =====================================================================
    //  Navigation (sidebar + Back / Cancel)
    // =====================================================================

    @FXML
    private void goDashboard() {
        switchScreen("DASHBOARD.fxml", "Dashboard");
    }

    @FXML
    private void goOrders() {
        switchScreen("ORDERS.fxml", "Orders");
    }

    @FXML
    private void goStandSale() {
        switchScreen("STANDSALE.fxml", "Stand Sale");
    }

    /** Used by the sidebar, the Back button and the Cancel button. */
    @FXML
    private void goInventory() {
        switchScreen("INVENTORY_LIST.fxml", "Inventory");
    }

    // =====================================================================
    //  Account menu
    // =====================================================================

    @FXML
    private void handleOpenChangePassword() {
        openDialog("CHANGEPASSWORD.fxml", "Change password", false);
    }

    @FXML
    private void handleOpenAbout() {
        // borderless = true -> no Windows title bar, rounded transparent window
        openDialog("ABOUT.fxml", "About", true);
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(currentStage());
        confirm.setTitle("Log out");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to log out?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentStage().setMaximized(false);
            switchScreen("LOGIN.fxml", "Login");
        }
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    private Stage currentStage() {
        return (Stage) btnSaveProduct.getScene().getWindow();
    }

    private void switchScreen(String fxmlName, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                showError("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = currentStage();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - " + title);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + title, rootCause(e));
        }
    }

    /** @param borderless true = no title bar, transparent background (for rounded dialogs like About) */
    private void openDialog(String fxmlName, String title, boolean borderless) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                showError("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();

            Stage dialog = new Stage(borderless ? StageStyle.TRANSPARENT : StageStyle.DECORATED);
            dialog.initOwner(currentStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sassy's Creation - " + title);
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (borderless) {
                scene.setFill(Color.TRANSPARENT);
            }
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + title, rootCause(e));
        }
    }

    private String rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.toString();
    }

    private void showWarning(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(currentStage());
        alert.setTitle("Add Product");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(currentStage());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "Unknown error. Check the console for details." : message);
        alert.showAndWait();
    }
}