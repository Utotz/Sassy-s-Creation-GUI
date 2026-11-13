package com.sassyscreation.sassyscreationgui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrdersController implements Initializable {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

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

    // ---- search / filter / buttons ----
    @FXML private TextField tfOrderSearch;
    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private Button btnExportOrders;
    @FXML private Button btnAddOrder;

    // ---- summary bar ----
    @FXML private Label lblOrderTotal;
    @FXML private Label lblStatusCounts;

    // ---- table ----
    @FXML private TableView<Map<String, Object>> tvOrders;
    @FXML private TableColumn<Map<String, Object>, Object> colOrderId;
    @FXML private TableColumn<Map<String, Object>, Object> colFb;
    @FXML private TableColumn<Map<String, Object>, Object> colProduct;
    @FXML private TableColumn<Map<String, Object>, Object> colColor;
    @FXML private TableColumn<Map<String, Object>, Object> colPrice;
    @FXML private TableColumn<Map<String, Object>, Object> colType;
    @FXML private TableColumn<Map<String, Object>, Object> colDate;
    @FXML private TableColumn<Map<String, Object>, Object> colStatus;
    @FXML private TableColumn<Map<String, Object>, Object> colActions;

    private final ObservableList<Map<String, Object>> allOrders = FXCollections.observableArrayList();
    private FilteredList<Map<String, Object>> filteredOrders;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadOrders();
        setUpTable();
        setUpFilters();
        updateSummary();
    }

    // =====================================================================
    //  Data
    // =====================================================================

    /**
     * TEMPORARY: sample orders that match the design.
     * Replace this with a database query (SELECT from your orders table).
     */
    private void loadOrders() {
        allOrders.setAll(
                order(1, "alyssa.customer1", "Mixed Bouquet - Fuzzy Wire (Medium)", "Pink and White", 400.00, "Pickup", "2026-04-28", "Cancelled"),
                order(2, "juana.santos", "Rose - Crochet", "Red", 100.00, "Pickup", "2026-04-29", "Pending"),
                order(3, "maria.reyes", "Octopus Keychain", "Blue", 110.00, "Meetup", "2026-04-30", "Pending"),
                order(4, "pedro.cruz", "Mixed Bouquet - Crochet (Small)", "Purple", 350.00, "Pickup", "2026-05-01", "Pending"),
                order(5, "ana.garcia", "Baby Tulip Keychain", "Yellow", 40.00, "Meetup", "2026-04-28", "Done"),
                order(6, "raven", "Diamond Top", "Pink", 1000.00, "Pickup", "2026-05-10", "Pending"));
    }

    private Map<String, Object> order(int id, String customer, String product, String color,
                                      double price, String type, String date, String status) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("fb", customer);
        m.put("product", product);
        m.put("color", color);
        m.put("price", price);
        m.put("type", type);
        m.put("date", date);
        m.put("status", status);
        m.put("actions", "actions");   // placeholder so the Actions column has a value
        return m;
    }

    // =====================================================================
    //  Table setup
    // =====================================================================

    private void setUpTable() {
        // Price shown as pesos
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("\u20B1%,.2f", ((Number) item).doubleValue()));
                }
            }
        });

        // Status shown as a coloured badge
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item.toString());
                    badge.getStyleClass().add("badge-" + item.toString().toLowerCase());
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Actions: view / edit / delete buttons
        colActions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                Map<String, Object> row = getTableRow() == null ? null : getTableRow().getItem();
                if (empty || row == null) {
                    setGraphic(null);
                    return;
                }

                Button view = iconButton("ico-view", "View details", "btn-icon");
                view.setOnAction(e -> showOrderDetails(row));

                Button edit = iconButton("ico-edit", "Edit", "btn-icon", "btn-edit");
                edit.setText("Edit");
                edit.setOnAction(e -> openAddOrder(row));

                Button delete = iconButton("ico-delete", "Delete", "btn-icon", "btn-icon-danger");
                delete.setOnAction(e -> deleteOrder(row));

                HBox box = new HBox(10, view, edit, delete);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        // Filtering + sorting pipeline
        filteredOrders = new FilteredList<>(allOrders, o -> true);
        SortedList<Map<String, Object>> sorted = new SortedList<>(filteredOrders);
        sorted.comparatorProperty().bind(tvOrders.comparatorProperty());
        tvOrders.setItems(sorted);
    }

    private Button iconButton(String icon, String tooltip, String... styleClasses) {
        Button b = new Button();
        b.setFocusTraversable(false);
        b.setMnemonicParsing(false);
        b.getStyleClass().addAll(styleClasses);
        Region graphic = new Region();
        graphic.getStyleClass().addAll("ico", icon);
        b.setGraphic(graphic);
        b.setTooltip(new Tooltip(tooltip));
        return b;
    }

    private void setUpFilters() {
        cbStatusFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        tfOrderSearch.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void applyFilters() {
        String text = tfOrderSearch.getText() == null ? "" : tfOrderSearch.getText().trim().toLowerCase();
        String status = cbStatusFilter.getValue();
        boolean allStatus = status == null || status.equals("All status");

        filteredOrders.setPredicate(o -> {
            boolean matchesStatus = allStatus || status.equals(o.get("status"));
            boolean matchesText = text.isEmpty()
                    || String.valueOf(o.get("fb")).toLowerCase().contains(text)
                    || String.valueOf(o.get("product")).toLowerCase().contains(text);
            return matchesStatus && matchesText;
        });
    }

    private void updateSummary() {
        long pending = countStatus("Pending");
        long done = countStatus("Done");
        long cancelled = countStatus("Cancelled");

        lblOrderTotal.setText("Total Orders: " + allOrders.size());
        lblStatusCounts.setText("Pending " + pending + "  \u00B7  Done " + done + "  \u00B7  Cancelled " + cancelled);
        lblNavOrdersBadge.setText(String.valueOf(pending));
        lblNavOrdersBadge.setVisible(pending > 0);
    }

    private long countStatus(String status) {
        return allOrders.stream().filter(o -> status.equals(o.get("status"))).count();
    }

    // =====================================================================
    //  Row actions
    // =====================================================================

    private void showOrderDetails(Map<String, Object> o) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order details");
        alert.setHeaderText("Order #" + o.get("id"));
        alert.setContentText(
                "Customer: " + o.get("fb") + "\n"
                        + "Product: " + o.get("product") + "\n"
                        + "Color: " + o.get("color") + "\n"
                        + "Price: " + String.format("\u20B1%,.2f", ((Number) o.get("price")).doubleValue()) + "\n"
                        + "Type: " + o.get("type") + "\n"
                        + "Date: " + o.get("date") + "\n"
                        + "Status: " + o.get("status"));
        alert.showAndWait();
    }

    private void openAddOrder(Map<String, Object> o) {
        // TODO: pass the order into the form's controller once ADDORDER.fxml has one
        openDialog("ADDORDER.fxml", "Edit order");
    }

    private void deleteOrder(Map<String, Object> o) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete order");
        confirm.setHeaderText("Delete order #" + o.get("id") + "?");
        confirm.setContentText(o.get("fb") + " - " + o.get("product") + "\nThis cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: also delete the row from the database
            allOrders.remove(o);
            updateSummary();
        }
    }

    // =====================================================================
    //  Header buttons
    // =====================================================================

    @FXML
    private void handleOpenAddOrder() {   // keep your existing method name
        try {
            URL url = getClass().getResource("/com/sassyscreation/sassyscreationgui/ADDORDER.fxml");
            Parent root = new FXMLLoader(url).load();
            navOrders.getScene().setRoot(root);   // use any node field the controller already has
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenExport() {
        openDialog("EXPORTDIALOG.fxml", "Export orders");
    }

    // =====================================================================
    //  Navigation (sidebar)
    // =====================================================================

    @FXML
    private void goDashboard() {
        switchScreen("DASHBOARD.fxml", "Dashboard");
    }

    @FXML
    private void goOrders() {
        // Already on the orders screen
    }

    @FXML
    private void goStandSale() {
        switchScreen("STANDSALE.fxml", "Stand Sale");
    }

    @FXML
    private void goInventory() {
        switchScreen("INVENTORY_LIST.fxml", "Inventory");
    }

    // =====================================================================
    //  Account menu
    // =====================================================================

    @FXML
    private void handleOpenChangePassword() {
        openDialog("CHANGEPASSWORD.fxml", "Change password");
    }

    @FXML
    private void handleOpenAbout() {
        // borderless = true -> no Windows title bar, rounded transparent window
        openDialog("ABOUT.fxml", "About", true);
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
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
        return (Stage) navOrders.getScene().getWindow();
    }

    /** Replaces the content of the current window with another FXML screen. */
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
            showError("Could not open " + title, e.getMessage());
        }
    }

    /** Opens an FXML screen in a normal pop-up window (with title bar). */
    private void openDialog(String fxmlName, String title) {
        openDialog(fxmlName, title, false);
    }

    /**
     * Opens an FXML screen in a pop-up window on top of this screen.
     *
     * @param borderless true = no title bar, transparent background (for rounded dialogs like About)
     */
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

            Scene scene = new Scene(root);
            if (borderless) {
                scene.setFill(Color.TRANSPARENT);
            }
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + title, e.getMessage());
        }
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "Unknown error. Check the console for details." : message);
        alert.showAndWait();
    }
}