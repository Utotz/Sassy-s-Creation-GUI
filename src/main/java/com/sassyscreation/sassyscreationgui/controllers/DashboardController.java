package com.sassyscreation.sassyscreationgui.controllers;

<<<<<<< HEAD
import com.sassyscreation.sassyscreationgui.dao.ProductDAO;
import com.sassyscreation.sassyscreationgui.model.Product;
import com.sassyscreation.sassyscreationgui.util.Transitions;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
=======
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
<<<<<<< HEAD
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
=======
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.net.URL;
import java.time.Year;
import java.util.ArrayList;
=======
import java.net.URL;
import java.util.Comparator;
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD

public class DashboardController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    @FXML private ComboBox<String> cbDashYear;
    @FXML private Label lblNavOrdersBadge;
=======
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

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

    // ---- header ----
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbDashYear;

    // ---- banner ----
    @FXML private Button btnReviewOrders;

    // ---- stat cards ----
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    @FXML private Label lblOrders;
    @FXML private Label lblProducts;
    @FXML private Label lblRevenue;
    @FXML private Label lblPending;
<<<<<<< HEAD
    @FXML private Label lblReminderTitle;
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private PieChart statusChart;
    @FXML private TableView<Map<String, Object>> tvLowStock;
    @FXML private Node barSample;
    @FXML private Node pieSample;
    @FXML private Node lowStockSample;

    private final ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        // hide the design preview nodes (safe even if one is missing in the FXML)
        hide(barSample);
        hide(pieSample);
        hide(lowStockSample);

        // make sure the year box has years and a selected value
        if (cbDashYear != null && cbDashYear.getItems().isEmpty()) {
            int thisYear = Year.now().getValue();
            for (int y = thisYear; y >= thisYear - 2; y--) {
                cbDashYear.getItems().add(String.valueOf(y));
            }
        }

        // the chart should not animate in from nothing
        if (revenueChart != null) {
            revenueChart.setAnimated(false);
            revenueChart.setLegendVisible(false);
        }

        if (cbDashYear == null) {
            loadData();
            return;
        }

        // reload the numbers when the year changes
        cbDashYear.valueProperty().addListener((obs, oldVal, newVal) -> loadData());

        if (cbDashYear.getValue() == null) {
            cbDashYear.getSelectionModel().selectFirst(); // this fires loadData()
        } else {
            loadData();
        }
    }

    // ---------- navigation ----------

    @FXML
    private void goDashboard() {
        // already on the dashboard
=======

    // ---- revenue bars + reminder ----
    @FXML private BarChart<String, Number> revenueChart;   // plain chart, now hidden
    @FXML private HBox barSample;                          // pill bars are drawn into this
    @FXML private Label lblReminderTitle;

    // ---- low stock table ----
    @FXML private Hyperlink linkLowStockAll;
    @FXML private TableView<Map<String, Object>> tvLowStock;
    @FXML private TableColumn<Map<String, Object>, Object> colLowName;
    @FXML private TableColumn<Map<String, Object>, Object> colLowCat;
    @FXML private TableColumn<Map<String, Object>, Object> colLowStock;
    @FXML private VBox lowStockSample;         // design-time preview only

    // ---- orders by status (semicircle gauge) ----
    @FXML private Label lblOrdersYear;
    @FXML private HBox pieSample;              // holds the gauge
    @FXML private PieChart statusChart;        // plain pie chart, now hidden
    @FXML private Arc arcPending;
    @FXML private Arc arcDone;
    @FXML private Arc arcCancelled;
    @FXML private Label lblGaugePct;
    @FXML private Label lblLegendPending;
    @FXML private Label lblLegendDone;
    @FXML private Label lblLegendCancelled;

    // ---- quick actions ----
    @FXML private Button btnQuickOrder;
    @FXML private Button btnQuickSale;
    @FXML private Button btnQuickProduct;

    // bar sizes (match the Scene Builder design)
    private static final double BAR_WIDTH = 40.0;
    private static final double BAR_MAX_HEIGHT = 220.0;
    private static final double BAR_MIN_HEIGHT = 38.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // The pill bars and the gauge are the real display now,
        // so hide the plain BarChart / PieChart instead.
        hide(revenueChart);
        hide(statusChart);
        hide(lowStockSample);

        setUpLowStockTable();

        // Reload the numbers/charts whenever the year changes
        cbDashYear.valueProperty().addListener((obs, oldYear, newYear) -> {
            if (newYear != null) {
                loadDashboardData(newYear);
            }
        });

        loadDashboardData(cbDashYear.getValue() != null ? cbDashYear.getValue() : "2026");
    }

    // =====================================================================
    //  Data
    // =====================================================================

    /**
     * TEMPORARY: sample numbers that match the design.
     * Replace the values below with real database queries for the chosen year.
     */
    private void loadDashboardData(String year) {
        int totalOrders = 6;
        int pendingOrders = 4;
        int doneOrders = 1;
        int cancelledOrders = 1;

        lblOrders.setText(String.valueOf(totalOrders));
        lblProducts.setText("44");
        lblRevenue.setText("\u20B11,210.00");
        lblPending.setText("\u20B11,560.00");
        lblNavOrdersBadge.setText(String.valueOf(pendingOrders));
        lblReminderTitle.setText(pendingOrders + " orders awaiting pickup");
        lblOrdersYear.setText("Year " + year);

        // Stand revenue by day (label -> pesos)
        Map<String, Integer> revenueByDay = new LinkedHashMap<>();
        revenueByDay.put("Sat\n04-25", 150);
        revenueByDay.put("Sun\n04-26", 230);
        revenueByDay.put("Mon\n04-27", 130);
        revenueByDay.put("Wed\n04-29", 60);
        revenueByDay.put("Fri\n05-01", 60);
        revenueByDay.put("Sat\n05-02", 380);
        revenueByDay.put("Sun\n05-03", 200);
        renderRevenueBars(revenueByDay);

        // Orders by status
        renderStatusGauge(pendingOrders, doneOrders, cancelledOrders);

        // Low stock list
        loadLowStock();
    }

    // =====================================================================
    //  Low stock table
    // =====================================================================

    /** Tells each column which key of the row map to show (no FXML MapValueFactory needed). */
    private void setUpLowStockTable() {
        if (tvLowStock == null) {
            return;
        }
        colLowName.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().get("name")));
        colLowCat.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().get("category")));
        colLowStock.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().get("stock")));
    }

    /**
     * TEMPORARY: sample low-stock items.
     * Replace with a database query (products where stock <= threshold).
     */
    private void loadLowStock() {
        if (tvLowStock == null) {
            return;
        }
        tvLowStock.setItems(FXCollections.observableArrayList(
                lowStockItem("Sunflower - Fuzzy Wire", "Flowers", 6),
                lowStockItem("Baby Mushroom Keychain", "Keychains", 8),
                lowStockItem("Gypsophila", "Flowers", 12)));
    }

    private Map<String, Object> lowStockItem(String name, String category, int stock) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("category", category);
        m.put("stock", stock);
        return m;
    }

    // =====================================================================
    //  Pill bars (Stand Revenue by Day)
    // =====================================================================

    /**
     * Draws one pill-shaped bar per day. The highest day gets the darkest bar,
     * then mid, then light, and every other day gets the hatched bar.
     */
    private void renderRevenueBars(Map<String, Integer> data) {
        barSample.getChildren().clear();

        int max = data.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (max <= 0) {
            return;
        }

        List<Integer> ranked = data.values().stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int value = entry.getValue();
            double height = Math.max(BAR_MIN_HEIGHT, BAR_MAX_HEIGHT * value / max);

            Rectangle bar = new Rectangle(BAR_WIDTH, height);
            bar.setArcWidth(BAR_WIDTH);
            bar.setArcHeight(BAR_WIDTH);
            bar.getStyleClass().addAll("sample-bar", tierClass(ranked.indexOf(value)));
            Tooltip.install(bar, new Tooltip("\u20B1" + value));

            Label day = new Label(entry.getKey());
            day.setAlignment(Pos.CENTER);
            day.setTextAlignment(TextAlignment.CENTER);
            day.setStyle("-fx-font-size: 11px; -fx-text-fill: #6F6570;");

            VBox column = new VBox(5.0, bar, day);
            column.setAlignment(Pos.BOTTOM_CENTER);
            barSample.getChildren().add(column);
        }
    }

    private String tierClass(int rank) {
        return switch (rank) {
            case 0 -> "sample-dark";
            case 1 -> "sample-mid";
            case 2 -> "sample-light";
            default -> "sample-hatch";
        };
    }

    // =====================================================================
    //  Semicircle gauge (Orders by Status)
    // =====================================================================

    /**
     * The gauge is the top half of a circle (180 degrees). It is filled from the
     * right: cancelled, then done, then pending takes the rest, so pending sits on the left.
     */
    private void renderStatusGauge(int pending, int done, int cancelled) {
        int total = pending + done + cancelled;

        double pendingLen = 0;
        double doneLen = 0;
        double cancelledLen = 0;
        if (total > 0) {
            pendingLen = 180.0 * pending / total;
            doneLen = 180.0 * done / total;
            cancelledLen = 180.0 * cancelled / total;
        }

        arcCancelled.setStartAngle(0);
        arcCancelled.setLength(cancelledLen);

        arcDone.setStartAngle(cancelledLen);
        arcDone.setLength(doneLen);

        arcPending.setStartAngle(cancelledLen + doneLen);
        arcPending.setLength(pendingLen);

        lblGaugePct.setText(total == 0 ? "0%" : Math.round(100.0 * pending / total) + "%");
        lblLegendPending.setText("Pending  " + pending);
        lblLegendDone.setText("Done  " + done);
        lblLegendCancelled.setText("Cancelled  " + cancelled);
    }

    private void hide(javafx.scene.Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    // =====================================================================
    //  Navigation (sidebar, banner, quick actions)
    // =====================================================================

    @FXML
    private void goDashboard() {
        // Already on the dashboard
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }

    @FXML
    private void goOrders() {
<<<<<<< HEAD
        Transitions.goTo(lblOrders, "ORDERS.fxml");
=======
        switchScreen("ORDERS.fxml", "Orders");
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }

    @FXML
    private void goStandSale() {
<<<<<<< HEAD
        Transitions.goTo(lblOrders, "STANDSALE.fxml");
=======
        switchScreen("STANDSALE.fxml", "Stand Sale");
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }

    @FXML
    private void goInventory() {
<<<<<<< HEAD
        Transitions.goTo(lblOrders, "INVENTORY_GRID.fxml");
    }

    @FXML
    private void handleLogout() {
        Transitions.goTo(lblOrders, "LOGIN.fxml");
    }

    @FXML
    private void handleOpenChangePassword() {
        openDialog("CHANGEPASSWORD.fxml");
=======
        switchScreen("INVENTORY_LIST.fxml", "Inventory");
    }

    // =====================================================================
    //  Account menu
    // =====================================================================

    @FXML
    private void handleOpenChangePassword() {
        openDialog("CHANGEPASSWORD.fxml", "Change password");
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }

    @FXML
    private void handleOpenAbout() {
<<<<<<< HEAD
        openDialog("ABOUT.fxml");
=======
        // borderless = true -> no Windows title bar, rounded transparent window
        openDialog("ABOUT.fxml", "About", true);
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }

    @FXML
    private void handleOpenLowStock() {
<<<<<<< HEAD
        Transitions.goTo(lblOrders, "INVENTORY_GRID.fxml");
    }

    // ---------- data ----------

    private void loadData() {
        // products come from the database
        // (catch everything: a DB problem must never stop the dashboard from opening)
        List<Product> products = new ArrayList<>();
        try {
            products = productDAO.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load the products from the database.");
        }

        try {
            showLowStock(products);

            // orders, products listed, stand revenue, pending value, pending orders
            // TODO: replace the demo numbers with real order and stand sale queries
            setStats(6, products.size(), new BigDecimal("1210.00"), new BigDecimal("1500.00"), 4);

            // revenue by day (LinkedHashMap keeps the order)
            // TODO: replace with a real query filtered by year
            Map<String, Double> days = new LinkedHashMap<>();
            days.put("Mon", 120.0);
            days.put("Tue", 340.0);
            days.put("Wed", 90.0);
            days.put("Thu", 410.0);
            days.put("Fri", 250.0);
            showRevenue(days);

            // pending, done, cancelled
            showStatus(4, 2, 0);
        } catch (Exception e) {
            // prints the real cause (usually an fx:id that doesn't match) in the console
            e.printStackTrace();
        }
    }

    private void setStats(int orders, int products, BigDecimal revenue,
                          BigDecimal pendingValue, int pendingOrders) {
        lblOrders.setText(String.valueOf(orders));
        lblProducts.setText(String.valueOf(products));
        lblRevenue.setText("₱" + String.format("%,.2f", revenue));
        lblPending.setText("₱" + String.format("%,.2f", pendingValue));
        lblReminderTitle.setText(pendingOrders + " orders awaiting pickup");

        // sidebar badge only shows when there are pending orders
        lblNavOrdersBadge.setText(String.valueOf(pendingOrders));
        lblNavOrdersBadge.setVisible(pendingOrders > 0);
    }

    private void showRevenue(Map<String, Double> days) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Double> day : days.entrySet()) {
            series.getData().add(new XYChart.Data<>(day.getKey(), day.getValue()));
        }

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }

    private void showStatus(int pending, int done, int cancelled) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Pending " + pending, pending),
                new PieChart.Data("Done " + done, done),
                new PieChart.Data("Cancelled " + cancelled, cancelled));

        statusChart.setData(data);
    }

    private void showLowStock(List<Product> products) {
        ObservableList<Map<String, Object>> rows = FXCollections.observableArrayList();

        for (Product p : products) {
            if (p.getStockQuantity() <= p.getLowStockThreshold()) {
                Map<String, Object> row = new HashMap<>();
                row.put("Name", p.getName());
                row.put("Category", p.getCategory());
                row.put("stock", stockLabel(p));
                rows.add(row);
            }
        }

        tvLowStock.setItems(rows);
    }

    private Label stockLabel(Product p) {
        String note = p.getStockQuantity() == 0 ? "Out of stock" : "Low stock";

        Label label = new Label(p.getStockQuantity() + " · " + note);
        label.getStyleClass().add("badge-low");
        return label;
    }

    // ---------- helpers ----------

    private void hide(Node node) {
        if (node == null) {
            return;
        }
        node.setVisible(false);
        node.setManaged(false);
    }

    private void showError(String message) {
        // runLater: an alert can't open while the screen is still loading or animating
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dashboard");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private Stage getStage() {
        return (Stage) lblOrders.getScene().getWindow();
    }

    private void openDialog(String fileName) {
        try {
            URL url = getClass().getResource(FXML_PATH + fileName);
            if (url == null) {
                throw new IllegalStateException("FXML not found: " + FXML_PATH + fileName);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initOwner(getStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));

            // start the pop-in only once the window is visible
            dialog.setOnShown(e -> Transitions.popIn(root));

            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + fileName + ":\n" + e);
        }
    }
=======
        openDialog("LOWSTOCK.fxml", "Low stock");
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
        return (Stage) navDashboard.getScene().getWindow();
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
     * Opens an FXML screen in a pop-up window on top of the dashboard.
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
    }
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
}