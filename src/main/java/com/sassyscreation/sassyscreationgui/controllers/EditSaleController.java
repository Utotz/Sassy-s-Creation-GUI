package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the "Edit Sale" page (EDITSALE.fxml).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.EditSaleController"
 *
 * Open it with {@link #open(Scene, SaleData, List, SaveListener)}. The page remembers the
 * Stand Sale screen it came from, so Back / Cancel / Save return to that same screen
 * (with its in-memory data intact) instead of reloading it.
 */
public class EditSaleController {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // CHANGE THIS if your edit-sale FXML has a different file name
    private static final String FXML_EDIT_SALE = "EDITSALE.fxml";

    private static final int QTY_MIN = 1;
    private static final int QTY_MAX = 999;

    // =====================================================================
    //  Data passed in / out
    // =====================================================================

    /** The sale being edited. */
    public static class SaleData {
        public final String product;
        public final int qty;
        public final double unitPrice;
        public final LocalDate date;

        public SaleData(String product, int qty, double unitPrice, LocalDate date) {
            this.product = product;
            this.qty = qty;
            this.unitPrice = unitPrice;
            this.date = date;
        }
    }

    /** A product that can be picked, with its catalog price and CURRENT stock. */
    public static class ProductOption {
        public final String name;
        public final double price;
        public final int stock;

        public ProductOption(String name, double price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }

    /** Called when the user saves valid changes. */
    @FunctionalInterface
    public interface SaveListener {
        void onSave(String product, int qty, double unitPrice, LocalDate date);
    }

    // =====================================================================
    //  FXML fields
    // =====================================================================

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
    @FXML private Button btnBackSales;

    // ---- form ----
    @FXML private ComboBox<String> cbEditSaleProduct;
    @FXML private Spinner<Integer> spEditQty;
    @FXML private TextField tfEditUnitPrice;
    @FXML private DatePicker dpSaleDate;
    @FXML private Label lblEditSaleTotal;
    @FXML private Button btnCancelSale;
    @FXML private Button btnSaveSale;

    // ---- state ----
    private SaleData sale;
    private List<ProductOption> options = List.of();
    private SaveListener listener;
    private Parent returnRoot;
    private double unitPrice;

    // =====================================================================
    //  Opening the page
    // =====================================================================

    /**
     * Replaces the content of {@code scene} with the Edit Sale page.
     *
     * @param scene    the scene currently showing the Stand Sale screen
     * @param sale     the sale to edit
     * @param options  all selectable products with their current stock
     * @param listener called when the user clicks "Save sale" with valid input
     */
    public static void open(Scene scene, SaleData sale, List<ProductOption> options, SaveListener listener) {
        try {
            URL resource = EditSaleController.class.getResource(FXML_PATH + FXML_EDIT_SALE);
            if (resource == null) {
                showAlert(scene, Alert.AlertType.ERROR, "Screen not found",
                        FXML_EDIT_SALE + " could not be found in the resources folder.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            EditSaleController controller = loader.getController();
            controller.returnRoot = scene.getRoot();
            controller.setSale(sale, options, listener);

            scene.setRoot(root);
            ((Stage) scene.getWindow()).setTitle("Sassy's Creation - Edit sale");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(scene, Alert.AlertType.ERROR, "Could not open Edit sale", rootCause(e));
        }
    }

    // =====================================================================
    //  Init
    // =====================================================================

    @FXML
    private void initialize() {
        // Badge: don't reserve space when hidden
        lblNavOrdersBadge.managedProperty().bind(lblNavOrdersBadge.visibleProperty());
        // TODO: set the badge from the real pending-order count

        if (spEditQty.getValueFactory() == null) {
            spEditQty.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(QTY_MIN, QTY_MAX, 1));
        }

        // Digits only (max 3) in the quantity editor
        spEditQty.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("\\d{0,3}")) {
                spEditQty.getEditor().setText(oldText);
            }
        });

        // Commit typed text when the editor loses focus
        spEditQty.getEditor().focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                commitSpinner();
            }
        });

        spEditQty.valueProperty().addListener((obs, o, n) -> updateTotal());
        cbEditSaleProduct.valueProperty().addListener((obs, o, n) -> onProductChanged());
    }

    /** Fills the form. Called by {@link #open} right after the FXML loads. */
    private void setSale(SaleData sale, List<ProductOption> options, SaveListener listener) {
        this.sale = sale;
        this.options = options == null ? List.of() : options;
        this.listener = listener;

        cbEditSaleProduct.getItems().clear();
        for (ProductOption o : this.options) {
            cbEditSaleProduct.getItems().add(o.name);
        }
        // The original product may no longer be in the catalog; keep it selectable
        if (!cbEditSaleProduct.getItems().contains(sale.product)) {
            cbEditSaleProduct.getItems().add(sale.product);
        }

        unitPrice = sale.unitPrice;
        cbEditSaleProduct.setValue(sale.product);
        spEditQty.getValueFactory().setValue(sale.qty);
        spEditQty.getEditor().setText(String.valueOf(sale.qty));
        dpSaleDate.setValue(sale.date != null ? sale.date : LocalDate.now());

        tfEditUnitPrice.setText(String.format("%.2f", unitPrice));
        updateTotal();
    }

    // =====================================================================
    //  Form logic
    // =====================================================================

    private void onProductChanged() {
        String name = cbEditSaleProduct.getValue();
        if (sale == null || name == null) {
            return;
        }
        if (name.equals(sale.product)) {
            unitPrice = sale.unitPrice;            // keep the price the sale was made at
        } else {
            ProductOption o = findOption(name);
            unitPrice = o == null ? 0 : o.price;   // different product: use its catalog price
        }
        tfEditUnitPrice.setText(String.format("%.2f", unitPrice));
        updateTotal();
    }

    private void updateTotal() {
        int qty = spEditQty.getValue() == null ? QTY_MIN : spEditQty.getValue();
        lblEditSaleTotal.setText(peso(qty * unitPrice));
    }

    private ProductOption findOption(String name) {
        for (ProductOption o : options) {
            if (o.name.equals(name)) {
                return o;
            }
        }
        return null;
    }

    /** Parses the typed text, clamps it, writes it back, and returns the value. */
    private int commitSpinner() {
        int value;
        try {
            value = Integer.parseInt(spEditQty.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            value = spEditQty.getValue() == null ? QTY_MIN : spEditQty.getValue();
        }
        value = Math.max(QTY_MIN, Math.min(QTY_MAX, value));
        spEditQty.getValueFactory().setValue(value);
        spEditQty.getEditor().setText(String.valueOf(value));
        return value;
    }

    // =====================================================================
    //  Save
    // =====================================================================

    @FXML
    private void handleSaveSale() {
        int qty = commitSpinner();
        String name = cbEditSaleProduct.getValue();
        LocalDate date = dpSaleDate.getValue();

        if (name == null) {
            warn("Please select a product.");
            cbEditSaleProduct.requestFocus();
            return;
        }
        if (qty < QTY_MIN) {
            warn("Quantity must be at least 1.");
            spEditQty.requestFocus();
            return;
        }
        if (date == null) {
            warn("Please pick a date.");
            dpSaleDate.requestFocus();
            return;
        }

        // Stock available to this sale = current stock (+ what this sale already holds, if same product)
        ProductOption option = findOption(name);
        int current = option == null ? 0 : option.stock;
        int available = current + (name.equals(sale.product) ? sale.qty : 0);
        if (qty > available) {
            warn("Not enough stock. Available for this sale: " + available);
            spEditQty.requestFocus();
            return;
        }

        // TODO: also UPDATE the sale and the stock in the database (in ONE transaction)
        if (listener != null) {
            listener.onSave(name, qty, unitPrice, date);
        }
        returnToStandSale();
    }

    // =====================================================================
    //  Navigation
    // =====================================================================

    /** Used by the sidebar, Back and Cancel. */
    @FXML
    private void goStandSale() {
        returnToStandSale();
    }

    @FXML private void goDashboard() { switchScreen("DASHBOARD.fxml", "Dashboard"); }
    @FXML private void goOrders()    { switchScreen("ORDERS.fxml", "Orders"); }
    @FXML private void goInventory() { switchScreen("INVENTORY_LIST.fxml", "Inventory"); }

    private void returnToStandSale() {
        if (returnRoot != null) {
            Scene scene = btnSaveSale.getScene();
            scene.setRoot(returnRoot);
            currentStage().setTitle("Sassy's Creation - Stand Sale");
        } else {
            switchScreen("STANDSALE.fxml", "Stand Sale");
        }
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
        return (Stage) btnSaveSale.getScene().getWindow();
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

    private static String peso(double amount) {
        return String.format("\u20B1%,.2f", amount);
    }

    private static String rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.toString();
    }

    private void warn(String message) {
        showAlert(btnSaveSale.getScene(), Alert.AlertType.WARNING, "Check your input", message);
    }

    private void error(String header, String message) {
        showAlert(btnSaveSale.getScene(), Alert.AlertType.ERROR, header, message);
    }

    private static void showAlert(Scene scene, Alert.AlertType type, String header, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(scene.getWindow());
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Edit sale");
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "Unknown error. Check the console for details." : message);
        alert.showAndWait();
    }
}