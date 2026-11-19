package com.sassyscreation.sassyscreationgui.controllers;

<<<<<<< HEAD
import com.sassyscreation.sassyscreationgui.util.Transitions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
=======
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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

<<<<<<< HEAD
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdersController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // change these if your FXML files are named differently
    private static final String DASHBOARD_FXML = "DASHBOARD.fxml";
    private static final String STANDSALE_FXML = "STANDSALE.fxml";
    private static final String INVENTORY_FXML = "INVENTORY.fxml";
    private static final String ADDORDER_FXML = "ADDORDER.fxml";
    private static final String EXPORT_FXML = "EXPORT.fxml";
    private static final String CHANGEPASSWORD_FXML = "CHANGEPASSWORD.fxml";
    private static final String ABOUT_FXML = "ABOUT.fxml";
    private static final String LOGOUT_FXML = "LOGOUT.fxml";

    private static final String PENDING = "Pending";
    private static final String DONE = "Done";
    private static final String CANCELLED = "Cancelled";

    // sidebar
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Label lblNavOrdersBadge;
=======
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

<<<<<<< HEAD
    // header buttons
    @FXML private Button btnExportOrders;
    @FXML private Button btnAddOrder;

    // filter chips
    @FXML private ToggleGroup tgOrderStatus;
    @FXML private ToggleButton chipOrdAll;
    @FXML private ToggleButton chipOrdPending;
    @FXML private ToggleButton chipOrdDone;
    @FXML private ToggleButton chipOrdCancelled;
    @FXML private Label lblChipAllCount;
    @FXML private Label lblChipPendingCount;
    @FXML private Label lblChipDoneCount;
    @FXML private Label lblChipCancelledCount;

    // search + cards
    @FXML private TextField tfOrderSearch;
    @FXML private ScrollPane spOrderCards;
    @FXML private GridPane gpOrderCards;

    // every card in the FXML, so filtering can add/remove them without losing any
    private final List<Node> allCards = new ArrayList<>();

    @FXML
    public void initialize() {
        // remember every card that came from the FXML
        allCards.addAll(gpOrderCards.getChildren());

        // hook up the View / Edit / Delete button on each card
        for (Node card : allCards) {
            setupCardButtons(card);
        }

        // one chip must always stay selected
        tgOrderStatus.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                tgOrderStatus.selectToggle(oldToggle);
                return;
            }
            animateFilter();
        });

        // filter while typing (no fade, it would feel laggy)
        tfOrderSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        refreshCounts();
        applyFilter();
    }

    // ---------- sidebar navigation ----------

    @FXML
    private void goDashboard() {
        switchScene(DASHBOARD_FXML);
    }

    @FXML
    private void goOrders() {
        // already on this page
    }

    @FXML
    private void goStandSale() {
        switchScene(STANDSALE_FXML);
    }

    @FXML
    private void goInventory() {
        switchScene(INVENTORY_FXML);
    }

    // ---------- account menu ----------

    @FXML
    private void handleOpenChangePassword() {
        openDialog(CHANGEPASSWORD_FXML);
    }

    @FXML
    private void handleOpenAbout() {
        openDialog(ABOUT_FXML);
    }

    @FXML
    private void handleLogout() {
        // the LOGOUT dialog's own controller should take the user back to LOGIN.fxml
        openDialog(LOGOUT_FXML);
    }

    // ---------- header buttons ----------

    @FXML
    private void handleOpenExport() {
        openDialog(EXPORT_FXML);
    }

    @FXML
    private void handleOpenAddOrder() {
        openDialog(ADDORDER_FXML);
        // TODO: reload the cards from the database after the dialog closes
    }

    // ---------- card buttons ----------

    private void setupCardButtons(Node card) {
        for (Node node : card.lookupAll(".order-btn")) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button button = (Button) node;

            if (button.getStyleClass().contains("order-btn-del")) {
                button.setOnAction(e -> handleDeleteOrder(card));
            } else if (button.getStyleClass().contains("order-btn-edit")) {
                button.setOnAction(e -> handleEditOrder(card));
            } else {
                button.setOnAction(e -> handleViewOrder(card));
            }
        }
    }

    private void handleViewOrder(Node card) {
        // TODO: open a VIEWORDER dialog for this order
        showInfo("Order details", "Viewing " + getCustomer(card) + "'s order.");
    }

    private void handleEditOrder(Node card) {
        // TODO: open an EDITORDER dialog for this order
        showInfo("Edit order", "Editing " + getCustomer(card) + "'s order.");
    }

    private void handleDeleteOrder(Node card) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete order");
        alert.setHeaderText(null);
        alert.setContentText("Delete " + getCustomer(card) + "'s order?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: delete the order from the database
            allCards.remove(card);
            refreshCounts();
            applyFilter();
        }
    }

    // ---------- filtering ----------

    // same fade out -> swap -> fade in as the sidebar navigation
    private void animateFilter() {
        Transitions.fadeOut(gpOrderCards, () -> {
            applyFilter();
            Transitions.fadeIn(gpOrderCards);
        });
    }

    private void applyFilter() {
        String status = getSelectedStatus();
        String search = tfOrderSearch.getText().trim().toLowerCase();

        gpOrderCards.getChildren().clear();

        int index = 0;
        for (Node card : allCards) {
            if (!matchesStatus(card, status) || !matchesSearch(card, search)) {
                continue;
            }
            GridPane.setColumnIndex(card, index % 2);
            GridPane.setRowIndex(card, index / 2);
            gpOrderCards.getChildren().add(card);
            index++;
        }

        if (index == 0) {
            Label empty = new Label("No orders found");
            empty.getStyleClass().add("page-sub");
            GridPane.setColumnIndex(empty, 0);
            GridPane.setRowIndex(empty, 0);
            GridPane.setColumnSpan(empty, 2);
            gpOrderCards.getChildren().add(empty);
        }
    }

    private String getSelectedStatus() {
        Toggle selected = tgOrderStatus.getSelectedToggle();
        if (selected == chipOrdPending) {
            return PENDING;
        }
        if (selected == chipOrdDone) {
            return DONE;
        }
        if (selected == chipOrdCancelled) {
            return CANCELLED;
        }
        return "All";
    }

    private boolean matchesStatus(Node card, String status) {
        return status.equals("All") || status.equals(card.getUserData());
    }

    private boolean matchesSearch(Node card, String search) {
        if (search.isEmpty()) {
            return true;
        }
        // look through every label on the card (customer, product, color, date...)
        for (Node node : card.lookupAll(".label")) {
            if (node instanceof Label) {
                String text = ((Label) node).getText();
                if (text != null && text.toLowerCase().contains(search)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ---------- counts ----------

    private void refreshCounts() {
        int pending = countStatus(PENDING);
        int done = countStatus(DONE);
        int cancelled = countStatus(CANCELLED);

        lblChipAllCount.setText(String.valueOf(allCards.size()));
        lblChipPendingCount.setText(String.valueOf(pending));
        lblChipDoneCount.setText(String.valueOf(done));
        lblChipCancelledCount.setText(String.valueOf(cancelled));

        // the sidebar badge shows how many orders still need attention
=======
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

    // Shared (static) so EditOrderController can read and change the same orders
    static final ObservableList<Map<String, Object>> allOrders = FXCollections.observableArrayList();

    /** The order row currently being edited on the Edit Order screen (set before opening it). */
    static Map<String, Object> editingRow = null;

    private static boolean dataLoaded = false;
    private FilteredList<Map<String, Object>> filteredOrders;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!dataLoaded) {
            loadOrders();
            dataLoaded = true;
        }
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
        m.put("qty", 1);
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
                    setText(peso(((Number) item).doubleValue()));
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
                edit.setOnAction(e -> editOrder(row));

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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
        lblNavOrdersBadge.setText(String.valueOf(pending));
        lblNavOrdersBadge.setVisible(pending > 0);
    }

<<<<<<< HEAD
    private int countStatus(String status) {
        int count = 0;
        for (Node card : allCards) {
            if (status.equals(card.getUserData())) {
                count++;
            }
        }
        return count;
    }

    private String getCustomer(Node card) {
        Node label = card.lookup(".order-cust");
        return label instanceof Label ? ((Label) label).getText() : "this customer";
    }

    // ---------- scenes and dialogs ----------

    private void switchScene(String fxml) {
        // fade out this page, then show the next one
        Transitions.fadeOut(navOrders.getScene().getRoot(), () -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxml));
                Parent root = loader.load();
                Transitions.fadeIn(root);

                Stage stage = (Stage) navOrders.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
            } catch (IOException e) {
                navOrders.getScene().getRoot().setOpacity(1);
                e.printStackTrace();
            }
        });
    }

    private void openDialog(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxml));
            Parent root = loader.load();
            Transitions.popIn(root);

            Stage dialog = new Stage();
            dialog.initOwner(navOrders.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
=======
    private long countStatus(String status) {
        return allOrders.stream().filter(o -> status.equals(o.get("status"))).count();
    }

    private static String peso(double value) {
        return String.format("\u20B1%,.2f", value);
    }

    // =====================================================================
    //  Row actions
    // =====================================================================

    /** Shows the custom "Order #N" dialog (OrderDetailsController / ORDERDETAILS.fxml). */
    private void showOrderDetails(Map<String, Object> o) {
        boolean editRequested = OrderDetailsController.show(
                currentStage(),
                o.get("id"),
                String.valueOf(o.get("fb")),
                String.valueOf(o.get("product")),
                String.valueOf(o.get("color")),
                String.valueOf(o.get("type")),
                peso(((Number) o.get("price")).doubleValue()),
                String.valueOf(o.get("date")),
                String.valueOf(o.get("status")));

        if (editRequested) {
            editOrder(o);   // the user pressed "Edit order" in the details dialog
        }
    }

    /** Opens the Edit Order screen (EDITORDER.fxml / EditOrderController) for the chosen row. */
    private void editOrder(Map<String, Object> o) {
        editingRow = o;
        switchScreen("ORDERFORM.fxml", "Edit order");
    }

    private void deleteOrder(Map<String, Object> o) {
        boolean confirmed = DeleteOrderController.show(
                tvOrders.getScene().getWindow(),
                o.get("id"),
                String.valueOf(o.get("fb")),
                String.valueOf(o.get("product")),
                peso(((Number) o.get("price")).doubleValue()),
                String.valueOf(o.get("status")));

        if (confirmed) {
            // TODO: also delete the row from the database and restore the product stock
            allOrders.remove(o);
            updateSummary();
        }
    }

    // =====================================================================
    //  Header buttons
    // =====================================================================

    @FXML
    private void handleOpenAddOrder() {
        switchScreen("ADDORDER.fxml", "Add order");
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
        // Custom confirmation dialog (EXITCONFIRM.fxml) instead of the plain JavaFX Alert
        if (ExitDialogController.show(currentStage())) {
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
        ErrorDialogController.show(currentStage(), header, message);
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }
}