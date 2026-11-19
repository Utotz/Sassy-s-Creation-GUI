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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrdersController implements Initializable {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    private static final DateTimeFormatter CARD_DATE = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);

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

    // ---- header buttons ----
    @FXML private Button btnExportOrders;
    @FXML private Button btnAddOrder;

    // ---- status chips ----
    @FXML private ToggleGroup tgOrderStatus;
    @FXML private ToggleButton chipOrdAll;
    @FXML private ToggleButton chipOrdPending;
    @FXML private ToggleButton chipOrdDone;
    @FXML private ToggleButton chipOrdCancelled;
    @FXML private Label lblChipAllCount;
    @FXML private Label lblChipPendingCount;
    @FXML private Label lblChipDoneCount;
    @FXML private Label lblChipCancelledCount;

    // ---- search (old combo box is hidden in the FXML but kept so the id still resolves) ----
    @FXML private TextField tfOrderSearch;
    @FXML private ComboBox<String> cbStatusFilter;

    // ---- summary bar (hidden in the FXML, still updated) ----
    @FXML private Label lblOrderTotal;
    @FXML private Label lblStatusCounts;

    // ---- order cards ----
    @FXML private ScrollPane spOrderCards;
    @FXML private GridPane gpOrderCards;

    // ---- hidden table (kept for compatibility) ----
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
        // Badge: don't reserve space when hidden
        lblNavOrdersBadge.managedProperty().bind(lblNavOrdersBadge.visibleProperty());

        loadOrders();
        setUpTable();      // creates filteredOrders
        setUpFilters();
        refreshCards();
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
        m.put("actions", "actions");
        return m;
    }

    // =====================================================================
    //  Hidden table setup (kept so the pipeline and fx:ids still work)
    // =====================================================================

    private void setUpTable() {
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : peso(item));
            }
        });

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

        filteredOrders = new FilteredList<>(allOrders, o -> true);
        SortedList<Map<String, Object>> sorted = new SortedList<>(filteredOrders);
        sorted.comparatorProperty().bind(tvOrders.comparatorProperty());
        tvOrders.setItems(sorted);
    }

    // =====================================================================
    //  Filters (chips + search)
    // =====================================================================

    private void setUpFilters() {
        // Never allow "no chip selected" - clicking the active chip keeps it selected.
        tgOrderStatus.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) {
                if (oldT != null) {
                    oldT.setSelected(true);
                }
                return;
            }
            applyFilters();
        });

        tfOrderSearch.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    /** Status of the selected chip, or null for "All". */
    private String selectedStatus() {
        Toggle t = tgOrderStatus.getSelectedToggle();
        if (t == chipOrdPending) return "Pending";
        if (t == chipOrdDone) return "Done";
        if (t == chipOrdCancelled) return "Cancelled";
        return null;
    }

    private void applyFilters() {
        String text = tfOrderSearch.getText() == null ? "" : tfOrderSearch.getText().trim().toLowerCase();
        String status = selectedStatus();

        filteredOrders.setPredicate(o -> {
            boolean matchesStatus = status == null || status.equals(o.get("status"));
            boolean matchesText = text.isEmpty()
                    || String.valueOf(o.get("fb")).toLowerCase().contains(text)
                    || String.valueOf(o.get("product")).toLowerCase().contains(text);
            return matchesStatus && matchesText;
        });

        refreshCards();
    }

    // =====================================================================
    //  Order cards
    // =====================================================================

    /** Rebuilds the card grid from the currently filtered orders. */
    private void refreshCards() {
        gpOrderCards.getChildren().clear();

        if (filteredOrders.isEmpty()) {
            Label empty = new Label("No orders found");
            empty.getStyleClass().add("page-sub");
            GridPane.setColumnSpan(empty, 2);
            gpOrderCards.add(empty, 0, 0);
            return;
        }

        int i = 0;
        for (Map<String, Object> o : filteredOrders) {
            VBox card = buildCard(o);
            GridPane.setHgrow(card, Priority.ALWAYS);
            gpOrderCards.add(card, i % 2, i / 2);
            i++;
        }
    }

    private VBox buildCard(Map<String, Object> o) {
        String customer = String.valueOf(o.get("fb"));
        String status = String.valueOf(o.get("status"));

        // header: avatar, customer + order no, status badge
        Label avatar = new Label(initials(customer));
        avatar.getStyleClass().add("order-avatar");

        Label cust = new Label(customer);
        cust.getStyleClass().add("order-cust");
        Label orderNo = new Label("Order #" + o.get("id"));
        orderNo.getStyleClass().add("page-sub");
        VBox names = new VBox(cust, orderNo);
        HBox.setHgrow(names, Priority.ALWAYS);

        Label badge = new Label(status);
        badge.getStyleClass().add("badge-" + status.toLowerCase());

        HBox header = new HBox(12, avatar, names, badge);
        header.setAlignment(Pos.CENTER_LEFT);

        Region line = new Region();
        line.getStyleClass().add("dash-line");

        // product + colour
        Label product = new Label(String.valueOf(o.get("product")));
        product.getStyleClass().add("order-product");
        product.setWrapText(true);
        Label color = new Label(String.valueOf(o.get("color")));
        color.getStyleClass().add("page-sub");
        VBox productBox = new VBox(2, product, color);

        // type / date and price
        Label typeDate = new Label(o.get("type") + " \u00B7 " + prettyDate(String.valueOf(o.get("date"))));
        typeDate.getStyleClass().add("page-sub");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label price = new Label(peso(o.get("price")));
        price.getStyleClass().add("order-price");
        HBox meta = new HBox(typeDate, spacer, price);
        meta.setAlignment(Pos.CENTER_LEFT);

        // actions
        Button view = cardButton("ico-view", "View", "View details", "btn-ghost", "order-btn");
        view.setOnAction(e -> showOrderDetails(o));

        Button edit = cardButton("ico-edit", "Edit", "Edit", "btn-ghost", "order-btn", "order-btn-edit");
        edit.setOnAction(e -> openAddOrder(o));

        Button delete = cardButton("ico-delete", null, "Delete", "btn-ghost", "order-btn", "order-btn-del");
        delete.setOnAction(e -> deleteOrder(o));

        HBox actions = new HBox(8, view, edit, delete);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, header, line, productBox, meta, actions);
        card.getStyleClass().add("order-card");
        card.setUserData(status);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private Button cardButton(String icon, String text, String tooltip, String... styleClasses) {
        Button b = new Button();
        if (text != null) b.setText(text);
        b.setFocusTraversable(false);
        b.setMnemonicParsing(false);
        b.getStyleClass().addAll(styleClasses);
        Region graphic = new Region();
        graphic.getStyleClass().addAll("ico", icon);
        b.setGraphic(graphic);
        b.setTooltip(new Tooltip(tooltip));
        return b;
    }

    private Button iconButton(String icon, String tooltip, String... styleClasses) {
        return cardButton(icon, null, tooltip, styleClasses);
    }

    private String initials(String name) {
        String[] parts = name.trim().split("[._\\s]+");
        String s;
        if (parts.length >= 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
            s = "" + parts[0].charAt(0) + parts[1].charAt(0);
        } else {
            String p = parts[0];
            s = p.length() >= 2 ? p.substring(0, 2) : p;
        }
        return s.toUpperCase();
    }

    private String prettyDate(String iso) {
        try {
            return LocalDate.parse(iso).format(CARD_DATE);
        } catch (Exception e) {
            return iso;
        }
    }

    private String peso(Object amount) {
        return String.format("\u20B1%,.2f", ((Number) amount).doubleValue());
    }

    // =====================================================================
    //  Summary (chip counts, sidebar badge, hidden footer labels)
    // =====================================================================

    private void updateSummary() {
        long pending = countStatus("Pending");
        long done = countStatus("Done");
        long cancelled = countStatus("Cancelled");

        lblChipAllCount.setText(String.valueOf(allOrders.size()));
        lblChipPendingCount.setText(String.valueOf(pending));
        lblChipDoneCount.setText(String.valueOf(done));
        lblChipCancelledCount.setText(String.valueOf(cancelled));

        lblOrderTotal.setText("Total Orders: " + allOrders.size());
        lblStatusCounts.setText("Pending " + pending + "  \u00B7  Done " + done + "  \u00B7  Cancelled " + cancelled);

        lblNavOrdersBadge.setText(String.valueOf(pending));
        lblNavOrdersBadge.setVisible(pending > 0);
    }

    private long countStatus(String status) {
        return allOrders.stream().filter(o -> status.equals(o.get("status"))).count();
    }

    // =====================================================================
    //  Row / card actions
    // =====================================================================

    private void showOrderDetails(Map<String, Object> o) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(currentStage());
        alert.setTitle("Order details");
        alert.setHeaderText("Order #" + o.get("id"));
        alert.setContentText(
                "Customer: " + o.get("fb") + "\n"
                        + "Product: " + o.get("product") + "\n"
                        + "Color: " + o.get("color") + "\n"
                        + "Price: " + peso(o.get("price")) + "\n"
                        + "Type: " + o.get("type") + "\n"
                        + "Date: " + o.get("date") + "\n"
                        + "Status: " + o.get("status"));
        alert.showAndWait();
    }

    private void openAddOrder(Map<String, Object> o) {
        // ADDORDER.fxml is a full page (with sidebar), so open it as a screen, not a dialog.
        // TODO: pass the order into AddOrderController so it can pre-fill the form for editing.
        switchScreen("ADDORDER.fxml", "Edit order");
    }

    private void deleteOrder(Map<String, Object> o) {
        boolean confirmed = DeleteOrderController.show(
                currentStage(),
                o.get("id"),
                String.valueOf(o.get("fb")),
                String.valueOf(o.get("product")),
                peso(o.get("price")),
                String.valueOf(o.get("status")));

        if (!confirmed) {
            return;
        }

        // TODO: in ONE transaction, delete the order from the database and restore the stock
        allOrders.remove(o);
        refreshCards();
        updateSummary();
    }

    // =====================================================================
    //  Header buttons
    // =====================================================================

    @FXML
    private void handleOpenAddOrder() {
        switchScreen("ADDORDER.fxml", "New order");
    }

    @FXML
    private void handleOpenExport() {
        openDialog("EXPORTDIALOG.fxml", "Export orders", false);
    }

    // =====================================================================
    //  Navigation (sidebar)
    // =====================================================================

    @FXML private void goDashboard() { switchScreen("DASHBOARD.fxml", "Dashboard"); }

    @FXML private void goOrders() { /* already on the orders screen */ }

    @FXML private void goStandSale() { switchScreen("STANDSALE.fxml", "Stand Sale"); }

    @FXML private void goInventory() { switchScreen("INVENTORY_LIST.fxml", "Inventory"); }

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

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(currentStage());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "Unknown error. Check the console for details." : message);
        alert.showAndWait();
    }
}