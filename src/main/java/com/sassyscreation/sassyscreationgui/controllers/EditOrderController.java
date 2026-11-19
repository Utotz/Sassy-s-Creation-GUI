package com.sassyscreation.sassyscreationgui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the "Edit Order" screen (ORDERFORM.fxml).
 *
 * IMPORTANT: the root element of the FXML (the first <HBox ...>) must declare this controller:
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.EditOrderController"
 *
 * OPTIONAL: to show the real order number in the page title, give the title label an id:
 *   <Label fx:id="lblEditTitle" styleClass="page-title" text="Edit Order #2" />
 *
 * The order to edit is passed in through OrdersController.editingRow
 * (OrdersController.editOrder(...) sets it, then opens this screen).
 */
public class EditOrderController implements Initializable {

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

    // ---- header ----
    @FXML private Label lblEditTitle;      // optional: only set if you add fx:id="lblEditTitle" in the FXML
    @FXML private Label lblStockNote;
    @FXML private Button btnBackOrders;

    // ---- form ----
    @FXML private TextField tfFbName;
    @FXML private ComboBox<String> cbOrderProduct;
    @FXML private TextField tfColor;
    @FXML private Spinner<Integer> spOrderQty;
    @FXML private TextField tfPrice;
    @FXML private DatePicker dpDate;
    @FXML private ToggleGroup tgArr;
    @FXML private ToggleButton tbPickup;
    @FXML private ToggleButton tbMeetup;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Button btnCancelOrder;
    @FXML private Button btnSaveOrder;

    private Map<String, Object> row;
    private boolean loading = true;

    // =====================================================================
    // Init
    // =====================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        row = OrdersController.editingRow;
        if (row == null) {
            // Nothing to edit (screen opened directly) -> go back to Orders
            Platform.runLater(this::goOrders);
            return;
        }

        updateOrdersBadge();

        if (lblEditTitle != null) {
            lblEditTitle.setText("Edit Order #" + row.get("id"));
        }

        // Product list comes from the inventory
        String product = String.valueOf(row.get("product"));
        List<String> names = new ArrayList<>();
        for (InventoryController.Product p : InventoryController.getProducts()) {
            names.add(p.getName());
        }
        if (!names.contains(product)) {
            names.add(product);   // keep the order's product even if it is not in the catalog
        }
        cbOrderProduct.setItems(FXCollections.observableArrayList(names));
        cbOrderProduct.setValue(product);

        // Other fields
        tfFbName.setText(String.valueOf(row.get("fb")));
        tfColor.setText(String.valueOf(row.get("color")));
        tfPrice.setText(String.format(Locale.US, "%.2f", ((Number) row.get("price")).doubleValue()));
        dpDate.setValue(parseDate(String.valueOf(row.get("date"))));

        int qty = row.get("qty") instanceof Number n ? Math.max(1, n.intValue()) : 1;
        spOrderQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, qty));
        spOrderQty.getEditor().focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                commitSpinnerText();
            }
        });

        // Status
        String status = String.valueOf(row.get("status"));
        if (!cbStatus.getItems().contains(status)) {
            cbStatus.getItems().add(status);
        }
        cbStatus.setValue(status);

        // Arrangement (one option always stays selected)
        if ("Meetup".equalsIgnoreCase(String.valueOf(row.get("type")))) {
            tbMeetup.setSelected(true);
        } else {
            tbPickup.setSelected(true);
        }
        tgArr.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
            }
        });

        // When the user picks another product, suggest that product's catalog price
        cbOrderProduct.valueProperty().addListener((obs, o, n) -> {
            if (!loading && n != null) {
                Double catalog = catalogPrice(n);
                if (catalog != null) {
                    tfPrice.setText(String.format(Locale.US, "%.2f", catalog));
                }
            }
        });

        loading = false;
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
            int typed = Integer.parseInt(spOrderQty.getEditor().getText().trim());
            typed = Math.max(1, Math.min(999, typed));
            spOrderQty.getValueFactory().setValue(typed);
        } catch (NumberFormatException e) {
            spOrderQty.getEditor().setText(String.valueOf(spOrderQty.getValue()));
        }
    }

    /** Catalog (inventory) price of a product, or null if it is not in the catalog. */
    private Double catalogPrice(String productName) {
        for (InventoryController.Product p : InventoryController.getProducts()) {
            if (p.getName().equals(productName)) {
                return p.getPrice();
            }
        }
        return null;
    }

    /** Keeps the sidebar badge in step with the number of pending orders. */
    private void updateOrdersBadge() {
        long pending = OrdersController.allOrders.stream()
                .filter(o -> "Pending".equals(o.get("status")))
                .count();
        lblNavOrdersBadge.setText(String.valueOf(pending));
        lblNavOrdersBadge.setVisible(pending > 0);
    }

    // =====================================================================
    // Save
    // =====================================================================
    @FXML
    private void handleSaveOrder() {
        commitSpinnerText();

        String customer = tfFbName.getText() == null ? "" : tfFbName.getText().trim();
        if (customer.isEmpty()) {
            warn("Missing customer", "Please enter the customer name.");
            tfFbName.requestFocus();
            return;
        }

        String product = cbOrderProduct.getValue();
        if (product == null) {
            warn("No product selected", "Please choose a product.");
            return;
        }

        int qty = spOrderQty.getValue();
        if (qty < 1) {
            warn("Invalid quantity", "Quantity must be at least 1.");
            return;
        }

        // Price: blank = catalog price
        double price;
        String priceText = tfPrice.getText() == null ? ""
                : tfPrice.getText().trim().replace(",", "").replace("\u20B1", "");
        if (priceText.isEmpty()) {
            Double catalog = catalogPrice(product);
            if (catalog == null) {
                warn("Missing price", "Please enter the agreed price.");
                tfPrice.requestFocus();
                return;
            }
            price = catalog;
        } else {
            try {
                price = Double.parseDouble(priceText);
                if (price < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                warn("Invalid price", "Enter a price of 0 or more (e.g. 100.00).");
                tfPrice.requestFocus();
                return;
            }
        }

        LocalDate date = dpDate.getValue();
        if (date == null) {
            warn("Missing date", "Please pick the order date.");
            return;
        }

        String status = cbStatus.getValue();
        if (status == null) {
            warn("No status selected", "Please choose a status.");
            return;
        }

        Toggle selected = tgArr.getSelectedToggle();
        String type = selected instanceof ToggleButton tb ? tb.getText() : "Pickup";

        String color = tfColor.getText() == null ? "" : tfColor.getText().trim();

        // TODO: also UPDATE the order in the database (and adjust the product stock if you track it per order)
        row.put("fb", customer);
        row.put("product", product);
        row.put("color", color);
        row.put("price", price);
        row.put("type", type);
        row.put("date", date.toString());
        row.put("status", status);
        row.put("qty", qty);

        goOrders();
    }

    // =====================================================================
    // Navigation (sidebar, Back, Cancel)
    // =====================================================================
    @FXML private void goOrders()    { leave(FXML_ORDERS, "Orders"); }
    @FXML private void goDashboard() { leave(FXML_DASHBOARD, "Dashboard"); }
    @FXML private void goStandSale() { leave(FXML_STAND_SALE, "Stand Sale"); }
    @FXML private void goInventory() { leave(FXML_INVENTORY, "Inventory"); }

    private void leave(String fxmlName, String title) {
        OrdersController.editingRow = null;
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
        return navOrders.getScene().getWindow();
    }

    private Stage currentStage() {
        return (Stage) navOrders.getScene().getWindow();
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

    private void warn(String header, String msg) {
        ErrorDialogController.show(ownerWindow(), header, msg);
    }

    private void error(String header, String msg) {
        ErrorDialogController.show(ownerWindow(), header, msg);
    }
}
