package com.sassyscreation.sassyscreationgui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the "Edit Sale" screen (EDITSALE.fxml).
 *
 * IMPORTANT: the root element of the FXML must declare this controller:
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.EditSaleController"
 *
 * The sale to edit is passed in through StandSaleController.editingRow
 * (StandSaleController.editSale(...) sets it, then opens this screen).
 */
public class EditSaleController implements Initializable {

    private static final String FXML_PATH            = "/com/sassyscreation/sassyscreationgui/";
    private static final String FXML_DASHBOARD       = "DASHBOARD.fxml";
    private static final String FXML_ORDERS          = "ORDERS.fxml";
    private static final String FXML_STAND_SALE      = "STANDSALE.fxml";
    private static final String FXML_INVENTORY       = "INVENTORY_LIST.fxml";
    private static final String FXML_LOGIN           = "LOGIN.fxml";
    private static final String FXML_ABOUT           = "ABOUT.fxml";
    private static final String FXML_CHANGE_PASSWORD = "CHANGEPASSWORD.fxml";

    // ---- sidebar ----
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Label lblNavOrdersBadge;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Button btnLogout;

    // ---- top bar ----
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

    // ---- form ----
    @FXML private Button btnBackSales;
    @FXML private ComboBox<String> cbEditSaleProduct;
    @FXML private Spinner<Integer> spEditQty;
    @FXML private TextField tfEditUnitPrice;
    @FXML private DatePicker dpSaleDate;
    @FXML private Label lblEditSaleTotal;
    @FXML private Button btnCancelSale;
    @FXML private Button btnSaveSale;

    // ---- the sale being edited (original values) ----
    private Map<String, Object> row;
    private String oldProduct;
    private int oldQty;
    private double oldPrice;

    // =====================================================================
    // Init
    // =====================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        row = StandSaleController.editingRow;
        if (row == null) {
            // Nothing to edit (screen opened directly) -> go back to Stand Sale
            Platform.runLater(this::goStandSale);
            return;
        }

        oldProduct = String.valueOf(row.get("Product"));
        oldQty = ((Number) row.get("Qty")).intValue();
        oldPrice = ((Number) row.get("Price")).doubleValue();

        // Product list
        cbEditSaleProduct.setItems(FXCollections.observableArrayList(StandSaleController.products.keySet()));
        if (!cbEditSaleProduct.getItems().contains(oldProduct)) {
            cbEditSaleProduct.getItems().add(oldProduct);
        }
        cbEditSaleProduct.setValue(oldProduct);

        // Quantity
        spEditQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, oldQty));
        spEditQty.getEditor().focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                commitSpinnerText();
            }
        });

        // Date
        dpSaleDate.setValue(parseDate(String.valueOf(row.get("Date"))));

        // Live updates
        cbEditSaleProduct.valueProperty().addListener((obs, o, n) -> updateTotals());
        spEditQty.valueProperty().addListener((obs, o, n) -> updateTotals());

        updateTotals();
    }

    private LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private void commitSpinnerText() {
        try {
            int typed = Integer.parseInt(spEditQty.getEditor().getText().trim());
            typed = Math.max(1, Math.min(999, typed));
            spEditQty.getValueFactory().setValue(typed);
        } catch (NumberFormatException e) {
            spEditQty.getEditor().setText(String.valueOf(spEditQty.getValue()));
        }
    }

    /** Unit price: the sale's original price if the product is unchanged, else the new product's price. */
    private double currentUnitPrice() {
        String name = cbEditSaleProduct.getValue();
        if (name == null) {
            return 0;
        }
        if (name.equals(oldProduct)) {
            return oldPrice;
        }
        StandSaleController.Product p = StandSaleController.products.get(name);
        return p == null ? 0 : p.price;
    }

    private void updateTotals() {
        int qty = spEditQty.getValue() == null ? 1 : spEditQty.getValue();
        double price = currentUnitPrice();
        tfEditUnitPrice.setText(String.format(Locale.US, "%.2f", price));
        lblEditSaleTotal.setText(peso(qty * price));
    }

    // =====================================================================
    // Save
    // =====================================================================
    @FXML
    private void handleSaveSale() {
        commitSpinnerText();

        String newName = cbEditSaleProduct.getValue();
        if (newName == null) {
            warn("No product selected", "Please choose a product.");
            return;
        }
        LocalDate date = dpSaleDate.getValue();
        if (date == null) {
            warn("Missing date", "Please pick the date of the sale.");
            return;
        }
        int newQty = spEditQty.getValue();
        if (newQty < 1) {
            warn("Invalid quantity", "Quantity must be at least 1.");
            return;
        }

        StandSaleController.Product newP = StandSaleController.products.get(newName);
        StandSaleController.Product oldP = StandSaleController.products.get(oldProduct);
        if (newP == null) {
            warn("Product not found", "\"" + newName + "\" is no longer in the product list.");
            return;
        }

        // Stock available to this sale: current stock, plus what this sale already took (same product)
        int available = newP.stock + (newName.equals(oldProduct) ? oldQty : 0);
        if (newQty > available) {
            warn("Not enough stock", "Only " + available + " of \"" + newName + "\" available for this sale.");
            return;
        }

        // TODO: also UPDATE the sale and the stock in the database
        if (oldP != null) {
            oldP.stock += oldQty;      // give the old quantity back
        }
        newP.stock -= newQty;          // take the new quantity

        double price = currentUnitPrice();
        row.put("Product", newName);
        row.put("Qty", newQty);
        row.put("Price", price);
        row.put("Total", newQty * price);
        row.put("Date", date.toString());

        goStandSale();
    }

    // =====================================================================
    // Navigation (sidebar, Back, Cancel)
    // =====================================================================
    @FXML private void goStandSale() { leave(FXML_STAND_SALE, "Stand Sale"); }
    @FXML private void goDashboard() { leave(FXML_DASHBOARD, "Dashboard"); }
    @FXML private void goOrders()    { leave(FXML_ORDERS, "Orders"); }
    @FXML private void goInventory() { leave(FXML_INVENTORY, "Inventory"); }

    private void leave(String fxmlName, String title) {
        StandSaleController.editingRow = null;
        switchScreen(fxmlName, title);
    }

    // =====================================================================
    // Account menu
    // =====================================================================
    @FXML
    private void handleOpenChangePassword() {
        openDialog(FXML_CHANGE_PASSWORD, "Change password", false);
    }

    @FXML
    private void handleOpenAbout() {
        openDialog(FXML_ABOUT, "About", true);
    }

    @FXML
    private void handleLogout() {
        if (ExitDialogController.show(ownerWindow())) {
            currentStage().setMaximized(false);
            leave(FXML_LOGIN, "Login");
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================
    private Window ownerWindow() {
        return navStandSale.getScene().getWindow();
    }

    private Stage currentStage() {
        return (Stage) navStandSale.getScene().getWindow();
    }

    private void switchScreen(String fxmlName, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                error("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = currentStage();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - " + title);
        } catch (Exception e) {
            e.printStackTrace();
            error("Could not open " + title, rootCause(e));
        }
    }

    /** @param borderless true = no title bar, transparent background (for rounded dialogs like About) */
    private void openDialog(String fxmlName, String title, boolean borderless) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                error("Screen not found", fxmlName + " could not be found in the resources folder.");
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
            error("Could not open " + title, rootCause(e));
        }
    }

    private String rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.toString();
    }

    private static String peso(double amount) {
        return "\u20B1" + String.format(Locale.US, "%,.2f", amount);
    }

    private void warn(String header, String msg) {
        ErrorDialogController.show(ownerWindow(), header, msg);
    }

    private void error(String header, String msg) {
        ErrorDialogController.show(ownerWindow(), header, msg);
    }
}