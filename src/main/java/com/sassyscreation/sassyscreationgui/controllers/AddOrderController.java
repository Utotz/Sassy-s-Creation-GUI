package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class AddOrderController {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    private static final String FXML_ORDERS = "ORDERS.fxml";
    private static final String STOCK_NOTE_DEFAULT = "Stock is deducted when the order is saved";

    private static final int QTY_MIN = 1;
    private static final int QTY_MAX = 999;

    // ---- Sidebar (set by <fx:include fx:id="sidebar"> in ADDORDER.fxml) ----
    @FXML private SidebarController sidebarController;

    // ---- Header ----
    @FXML private Label lblStockNote;
    @FXML private Button btnBackOrders;

    // ---- Form ----
    @FXML private TextField tfFbName;
    @FXML private ComboBox<Product> cbOrderProduct;
    @FXML private TextField tfColor;
    @FXML private Spinner<Integer> spOrderQty;
    @FXML private TextField tfPrice;
    @FXML private DatePicker dpDate;
    @FXML private ToggleButton tbPickup;
    @FXML private ToggleButton tbMeetup;
    @FXML private ToggleGroup tgArr;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Button btnCancelOrder;
    @FXML private Button btnSaveOrder;

    // =====================================================================
    // Placeholder model - replace with your real Product class / DAO.
    // =====================================================================
    public static class Product {
        private final int id;
        private final String name;
        private final BigDecimal price;
        private final int stock;

        public Product(int id, String name, BigDecimal price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
        public int getStock() { return stock; }

        @Override
        public String toString() { return name; }
    }

    // =====================================================================
    // Init
    // =====================================================================
    @FXML
    private void initialize() {
        sidebarController.setActive("orders");

        dpDate.setValue(LocalDate.now());
        cbStatus.setValue("Pending");

        // Make sure one arrangement is always selected (no empty toggle state)
        tgArr.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
            }
        });

        // Show product name in combo box
        cbOrderProduct.setConverter(new StringConverter<>() {
            @Override public String toString(Product p) { return p == null ? "" : p.getName(); }
            @Override public Product fromString(String s) { return null; }
        });
        cbOrderProduct.getItems().setAll(loadProducts());

        // When product changes: fill default price + show stock note
        cbOrderProduct.valueProperty().addListener((obs, oldP, p) -> onProductSelected(p));

        // Commit typed spinner text when focus is lost
        spOrderQty.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                commitSpinner();
            }
        });

        // TODO: call sidebarController.setOrdersBadge(<pending order count from DB>)
    }

    private List<Product> loadProducts() {
        // TEMPORARY sample data - replace with a real database / DAO call
        return List.of(
                new Product(1, "Customized Keychain", new BigDecimal("80.00"), 10),
                new Product(2, "Hat - Customize", new BigDecimal("350.00"), 5),
                new Product(3, "Flower Bouquet", new BigDecimal("500.00"), 3));
    }

    private void onProductSelected(Product p) {
        if (p == null) {
            lblStockNote.setText(STOCK_NOTE_DEFAULT);
            return;
        }
        tfPrice.setText(p.getPrice().toPlainString());
        lblStockNote.setText("In stock: " + p.getStock() + "  \u2022  " + STOCK_NOTE_DEFAULT);
    }

    /** Parses whatever is typed in the spinner, clamps it, and writes it back. */
    private void commitSpinner() {
        int value;
        try {
            value = Integer.parseInt(spOrderQty.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            value = QTY_MIN;
        }
        value = Math.max(QTY_MIN, Math.min(QTY_MAX, value));
        spOrderQty.getValueFactory().setValue(value);
        spOrderQty.getEditor().setText(String.valueOf(value));
    }

    // =====================================================================
    // Save
    // =====================================================================
    @FXML
    private void handleSaveOrder() {
        commitSpinner();

        String customer = tfFbName.getText() == null ? "" : tfFbName.getText().trim();
        Product product = cbOrderProduct.getValue();
        String color    = tfColor.getText() == null ? "" : tfColor.getText().trim();
        int qty         = spOrderQty.getValue() == null ? 0 : spOrderQty.getValue();
        LocalDate date  = dpDate.getValue();
        String status   = cbStatus.getValue();

        if (customer.isEmpty()) {
            warn("Please enter the customer name.");
            tfFbName.requestFocus();
            return;
        }
        if (product == null) {
            warn("Please select a product.");
            cbOrderProduct.requestFocus();
            return;
        }
        if (qty < QTY_MIN) {
            warn("Quantity must be at least 1.");
            spOrderQty.requestFocus();
            return;
        }
        if (qty > product.getStock()) {
            warn("Not enough stock. Available: " + product.getStock());
            spOrderQty.requestFocus();
            return;
        }
        if (date == null) {
            warn("Please pick a date.");
            dpDate.requestFocus();
            return;
        }
        if (status == null || status.isBlank()) {
            warn("Please select a status.");
            cbStatus.requestFocus();
            return;
        }

        // Price: defaults to catalog price when left blank
        BigDecimal price;
        String priceText = tfPrice.getText() == null ? "" : tfPrice.getText().trim().replace(",", "");
        if (priceText.isEmpty()) {
            price = product.getPrice();
        } else {
            try {
                price = new BigDecimal(priceText);
            } catch (NumberFormatException e) {
                warn("Agreed price must be a valid number.");
                tfPrice.requestFocus();
                return;
            }
            if (price.signum() < 0) {
                warn("Agreed price cannot be negative.");
                tfPrice.requestFocus();
                return;
            }
        }

        String arrangement = tbMeetup.isSelected() ? "Meetup" : "Pickup";

        // TODO: persist the order and deduct stock in ONE transaction, e.g.
        // orderDao.save(customer, product.getId(), color, qty, price, date, arrangement, status);
        // inventoryDao.deductStock(product.getId(), qty);

        success("Order saved", "The order for " + customer + " was saved successfully.");
        goOrders();
    }

    // =====================================================================
    // Navigation (Back and Cancel buttons)
    // =====================================================================
    @FXML
    private void goOrders() {
        navigate(FXML_ORDERS);
    }

    private Stage currentStage() {
        return (Stage) btnSaveOrder.getScene().getWindow();
    }

    private void navigate(String fxml) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxml);
            if (resource == null) {
                error(fxml + " was not found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Scene scene = btnSaveOrder.getScene();
            ScreenTransition.show(scene, root);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            error("Could not open " + fxml + ":\n" + rootCause(e));
        }
    }

    private String rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.toString();
    }

    // =====================================================================
    // Dialogs
    // =====================================================================

    /** Custom success popup (SUCCESSDIALOG.fxml). */
    private void success(String title, String msg) {
        SuccessDialogController.show(currentStage(), title, msg);
    }

    private void warn(String msg)  { show(Alert.AlertType.WARNING, "Check your input", msg); }
    private void error(String msg) { show(Alert.AlertType.ERROR, "Error", msg); }

    private void show(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initOwner(btnSaveOrder.getScene().getWindow());
        alert.showAndWait();
    }
}