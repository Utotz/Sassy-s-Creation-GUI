package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Locale;

/**
 * Controller for the "Edit product" screen (EDITPRODUCT.fxml).
 *
 * It is a full page (with the sidebar), not a popup.
 *
 * Usage (in InventoryController.editProduct):
 *   EditProductController.open(currentStage(), product);
 *
 * Save calls InventoryController.updateProduct(...) and goes back to the inventory.
 */
public class EditProductController {

    // Must sit in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";
    private static final String IMAGE_PATH = FXML_PATH + "images/";
    private static final String FXML_EDIT = "EDITPRODUCT.fxml";
    private static final String FXML_INVENTORY = "INVENTORY_LIST.fxml";

    private static final int MAX_STOCK = 9999;

    // ---- sidebar (set by <fx:include fx:id="sidebar"> in EDITPRODUCT.fxml) ----
    @FXML private SidebarController sidebarController;

    @FXML private Label lblEditTitle;
    @FXML private Label lblPhotoHint;
    @FXML private StackPane boxProductPhoto;
    @FXML private ImageView ivProductPhoto;
    @FXML private TextField tfProdName;
    @FXML private ComboBox<String> cbProdCategory;
    @FXML private TextField tfProdPrice;
    @FXML private Spinner<Integer> spStock;
    @FXML private Button btnBackInv;
    @FXML private Button btnCancelProduct;
    @FXML private Button btnSaveProduct;

    private int productId;
    private String imagePath;   // the photo that will be saved (old one unless changed)

    // ---------------------------------------------------------------
    // Opening the screen
    // ---------------------------------------------------------------

    /** Replaces the current screen with the edit form, filled with this product. */
    public static void open(Stage stage, InventoryController.Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(EditProductController.class.getResource(FXML_PATH + FXML_EDIT));
            Parent root = loader.load();
            EditProductController controller = loader.getController();
            controller.setProduct(product);

            ScreenTransition.show(stage.getScene(), root);
            stage.setTitle("Sassy's Creation - Edit Product");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open Edit Product");
            alert.setContentText(String.valueOf(e));
            alert.showAndWait();
        }
    }

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        sidebarController.setActive("inventory");

        // Keep a typed stock number when the box loses focus
        spStock.getEditor().focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                commitStockText();
            }
        });

        // Click the photo to choose a new one
        boxProductPhoto.setOnMouseClicked(e -> choosePhoto());
        boxProductPhoto.setStyle("-fx-cursor: hand;");

        // With a photo, show the hint only while the mouse is over it
        boxProductPhoto.setOnMouseEntered(e -> lblPhotoHint.setVisible(true));
        boxProductPhoto.setOnMouseExited(e -> lblPhotoHint.setVisible(ivProductPhoto.getImage() == null));
    }

    // ---------------------------------------------------------------
    // Filling in the product
    // ---------------------------------------------------------------

    private void setProduct(InventoryController.Product p) {
        productId = p.getId();
        imagePath = p.getImagePath();

        lblEditTitle.setText("Edit Product #" + p.getId());
        tfProdName.setText(p.getName());
        cbProdCategory.setValue(p.getCategory());
        tfProdPrice.setText(String.format(Locale.US, "%.2f", p.getPrice()));
        spStock.getValueFactory().setValue(p.getStock());

        showPhoto(loadImage(imagePath));
    }

    // ---------------------------------------------------------------
    // Photo
    // ---------------------------------------------------------------

    private void choosePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose product photo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = chooser.showOpenDialog(btnSaveProduct.getScene().getWindow());
        if (file == null) {
            return;
        }
        imagePath = file.getAbsolutePath();
        showPhoto(new Image(file.toURI().toString()));
    }

    private void showPhoto(Image image) {
        ivProductPhoto.setImage(image);
        lblPhotoHint.setVisible(image == null);
        lblPhotoHint.setText(image == null ? "No photo ·\nClick to add" : "Current photo ·\nChange");
    }

    /** Loads the photo from a saved path (a file on disk, or a bundled image). Null if none. */
    private Image loadImage(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        File file = new File(path);
        if (file.isAbsolute() && file.isFile()) {
            return new Image(file.toURI().toString());
        }
        URL res = getClass().getResource(IMAGE_PATH + path.trim());
        return res == null ? null : new Image(res.toExternalForm());
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleSaveProduct() {
        commitStockText();

        String name = tfProdName.getText() == null ? "" : tfProdName.getText().trim();
        if (name.isEmpty()) {
            warn("Missing name", "Please enter a product name.");
            tfProdName.requestFocus();
            return;
        }

        String category = cbProdCategory.getValue();
        if (category == null) {
            warn("Missing category", "Please choose a category.");
            cbProdCategory.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(tfProdPrice.getText().trim().replace(",", ""));
            if (price < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            warn("Invalid price", "Enter a price of 0 or more (e.g. 55.00).");
            tfProdPrice.requestFocus();
            return;
        }

        int stock = spStock.getValue() == null ? 0 : spStock.getValue();

        if (!InventoryController.updateProduct(productId, name, category, price, stock, imagePath)) {
            warn("Could not save", "The product was not found.");
            return;
        }
        goInventory();
    }

    /** Back to the inventory list (Back, Cancel and after saving). */
    @FXML
    private void goInventory() {
        try {
            Parent root = new FXMLLoader(getClass().getResource(FXML_PATH + FXML_INVENTORY)).load();
            Stage stage = (Stage) btnBackInv.getScene().getWindow();
            ScreenTransition.show(stage.getScene(), root);
            stage.setTitle("Sassy's Creation - Inventory");
        } catch (Exception e) {
            e.printStackTrace();
            warn("Could not open Inventory", String.valueOf(e));
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /** Keeps a typed stock number between 0 and MAX_STOCK; bad text goes back to the last value. */
    private void commitStockText() {
        try {
            int typed = Integer.parseInt(spStock.getEditor().getText().trim());
            typed = Math.max(0, Math.min(MAX_STOCK, typed));
            spStock.getValueFactory().setValue(typed);
        } catch (NumberFormatException e) {
            spStock.getEditor().setText(String.valueOf(spStock.getValue()));
        }
    }

    private void warn(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(btnSaveProduct.getScene().getWindow());
        alert.setTitle("Edit Product");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
