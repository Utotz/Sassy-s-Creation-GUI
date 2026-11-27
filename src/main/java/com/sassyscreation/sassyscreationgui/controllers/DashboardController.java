package com.sassyscreation.sassyscreationgui.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // ---- sidebar (set by <fx:include fx:id="sidebar"> in DASHBOARD.fxml) ----
    @FXML private SidebarController sidebarController;

    // ---- header ----
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbDashYear;

    // ---- banner ----
    @FXML private Button btnReviewOrders;

    // ---- stat cards ----
    @FXML private Label lblOrders;
    @FXML private Label lblProducts;
    @FXML private Label lblRevenue;        // Stand Revenue (Monthly)
    @FXML private Label lblRevenueNote;
    @FXML private Label lblOrdersMonth;    // Orders (Monthly)
    @FXML private Label lblOrdersNote;

    // ---- weekly revenue chart + week navigation ----
    @FXML private BarChart<String, Number> revenueChart;   // plain chart, hidden
    @FXML private HBox barSample;                          // pill bars are drawn into this
    @FXML private Button btnPrevWeek;
    @FXML private Button btnNextWeek;
    @FXML private Label lblWeekTitle;
    @FXML private Label lblWeekRange;

    // ---- reminder ----
    @FXML private Label lblPickupCount;    // "Orders for pickup" number
    @FXML private Label lblDeliveryCount;  // "Orders for delivery" number
    @FXML private Label lblReminderCount;  // red circle next to "Reminder"
    @FXML private Hyperlink linkReminderAll;

    // ---- low stock table ----
    @FXML private Hyperlink linkLowStockAll;
    @FXML private TableView<Map<String, Object>> tvLowStock;
    @FXML private TableColumn<Map<String, Object>, Object> colLowName;
    @FXML private TableColumn<Map<String, Object>, Object> colLowCat;
    @FXML private TableColumn<Map<String, Object>, Object> colLowStock;
    @FXML private VBox lowStockSample;         // design-time preview only

    // ---- orders by status (semicircle gauge) ----
    @FXML private Hyperlink linkOrdersAll;
    @FXML private Label lblOrdersYear;
    @FXML private HBox pieSample;              // holds the gauge
    @FXML private PieChart statusChart;        // plain pie chart, hidden
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

    // bar sizes
    private static final double BAR_WIDTH = 20.0;
    private static final double BAR_MAX_HEIGHT = 200.0;
    private static final double BAR_PAIR_GAP = 3.0;

    private static final String PESO = "\u20B1";
    private static final String TIER_STAND = "sample-dark";   // dark pink bar  = stand revenue
    private static final String TIER_ORDER = "sample-light";  // light pink bar = order revenue
    private static final String TIER_EMPTY = "sample-hatch";  // hatched dot    = nothing that day

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter MD_FMT = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
    private static final DateTimeFormatter MDY_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_NAME_FMT = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_NUM_FMT = DateTimeFormatter.ofPattern("MM-dd", Locale.ENGLISH);

    // ---- week currently shown in the chart (always a Monday) ----
    private LocalDate weekStart;
    private LocalDate maxWeekStart;   // the newest week you can navigate to

    // =====================================================================
    //  TEMPORARY sample data (same rows as the Stand Sale and Orders screens).
    //  Replace the lists + today() with real database queries later.
    // =====================================================================
    private record StandSale(LocalDate date, int total) { }

    /** method is "Pickup" or "Delivery". The ids and customer names are placeholders. */
    private record OrderSale(String id, String customer, LocalDate date, int price, String status, String method) { }

    private static final List<StandSale> SAMPLE_STAND_SALES = List.of(
            new StandSale(LocalDate.of(2026, 5, 3), 150),
            new StandSale(LocalDate.of(2026, 5, 2), 500),
            new StandSale(LocalDate.of(2026, 5, 1), 30),
            new StandSale(LocalDate.of(2026, 4, 29), 30),
            new StandSale(LocalDate.of(2026, 4, 27), 110),
            new StandSale(LocalDate.of(2026, 4, 26), 150),
            new StandSale(LocalDate.of(2026, 4, 26), 80),
            new StandSale(LocalDate.of(2026, 4, 25), 100),
            new StandSale(LocalDate.of(2026, 4, 25), 60));

    private static final List<OrderSale> SAMPLE_ORDERS = List.of(
            new OrderSale("ORD-001", "Customer A", LocalDate.of(2026, 4, 28), 400, "Cancelled", "Pickup"),
            new OrderSale("ORD-002", "Customer B", LocalDate.of(2026, 4, 29), 100, "Pending", "Pickup"),
            new OrderSale("ORD-003", "Customer C", LocalDate.of(2026, 4, 30), 110, "Pending", "Delivery"),
            new OrderSale("ORD-004", "Customer D", LocalDate.of(2026, 5, 1), 350, "Pending", "Pickup"),
            new OrderSale("ORD-005", "Customer E", LocalDate.of(2026, 4, 28), 40, "Done", "Pickup"),
            new OrderSale("ORD-006", "Customer F", LocalDate.of(2026, 5, 10), 1000, "Pending", "Pickup"));

    /**
     * TEMPORARY: products used by the "Low stock warnings" card AND the "Low stock items" dialog.
     * Replace with a database query for ALL products (the dialog filters them by its threshold).
     */
    private static final List<LowStockController.StockItem> SAMPLE_PRODUCTS = List.of(
            new LowStockController.StockItem("Sunflower - Fuzzy Wire", "Flowers", 6),
            new LowStockController.StockItem("Baby Mushroom Keychain", "Keychains", 8),
            new LowStockController.StockItem("Gypsophila", "Flowers", 12));

    /** The sample data is from late April / early May 2026, so "today" is fixed there for now. */
    private static final LocalDate DEMO_TODAY = LocalDate.of(2026, 5, 3);

    /** TODO: when the real database is connected, return LocalDate.now() here. */
    private LocalDate today() {
        return DEMO_TODAY;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sidebarController.setActive("dashboard");

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
        sidebarController.setOrdersBadge(pendingOrders);
        loadReminder();
        updateReminderCount(pendingOrders);
        lblOrdersYear.setText("Year " + year);

        // The monthly cards and the weekly chart follow the selected year:
        // same month / same week of that year as "today".
        LocalDate anchor = today().withYear(parseYear(year));
        loadMonthlyCards(YearMonth.from(anchor));

        maxWeekStart = mondayOf(anchor);
        weekStart = maxWeekStart;
        renderWeek();

        // Orders by status
        renderStatusGauge(pendingOrders, doneOrders, cancelledOrders);

        // Low stock list
        loadLowStock();
    }

    private int parseYear(String year) {
        try {
            return Integer.parseInt(year.trim());
        } catch (NumberFormatException e) {
            return today().getYear();
        }
    }

    private LocalDate mondayOf(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private String money(int pesos) {
        return String.format(Locale.US, PESO + "%,.2f", (double) pesos);
    }

    private boolean countsAsOrderRevenue(OrderSale order) {
        return !"Cancelled".equalsIgnoreCase(order.status());   // cancelled orders earn nothing
    }

    // =====================================================================
    //  Monthly stat cards
    // =====================================================================

    /** Stand Revenue (Monthly) and Orders (Monthly). */
    private void loadMonthlyCards(YearMonth month) {
        int standTotal = 0;
        int standCount = 0;
        for (StandSale s : SAMPLE_STAND_SALES) {
            if (YearMonth.from(s.date()).equals(month)) {
                standTotal += s.total();
                standCount++;
            }
        }

        int orderTotal = 0;
        int orderCount = 0;
        for (OrderSale o : SAMPLE_ORDERS) {
            if (YearMonth.from(o.date()).equals(month) && countsAsOrderRevenue(o)) {
                orderTotal += o.price();
                orderCount++;
            }
        }

        String monthName = month.format(MONTH_FMT);
        lblRevenue.setText(money(standTotal));
        lblRevenueNote.setText(standCount + (standCount == 1 ? " stand sale" : " stand sales") + " in " + monthName);
        lblOrdersMonth.setText(money(orderTotal));
        lblOrdersNote.setText(orderCount + (orderCount == 1 ? " order" : " orders") + " in " + monthName);
    }

    // =====================================================================
    //  Reminder card
    // =====================================================================

    /** The red circle with the number next to "Reminder" (hidden when 0). */
    private void updateReminderCount(int pending) {
        lblReminderCount.setText(String.valueOf(pending));
        boolean show = pending > 0;
        lblReminderCount.setVisible(show);
        lblReminderCount.setManaged(show);
    }

    /** Fills "Orders for pickup" and "Orders for delivery" from the pending orders. */
    private void loadReminder() {
        int pickup = 0;
        int delivery = 0;
        for (OrderSale o : SAMPLE_ORDERS) {
            if ("Pending".equalsIgnoreCase(o.status())) {
                if ("Delivery".equalsIgnoreCase(o.method())) {
                    delivery++;
                } else {
                    pickup++;
                }
            }
        }
        lblPickupCount.setText(String.valueOf(pickup));
        lblDeliveryCount.setText(String.valueOf(delivery));
    }

    // =====================================================================
    //  Low stock table
    // =====================================================================

    /** Tells each column which key of the row map to show, and draws the stock badge. */
    private void setUpLowStockTable() {
        if (tvLowStock == null) {
            return;
        }
        colLowName.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().get("name")));
        colLowCat.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().get("category")));
        colLowStock.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().get("stock")));

        // Red "badge-low" pill, like the design preview in the FXML
        colLowStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                int stock = (item instanceof Number n) ? n.intValue() : 0;
                Label badge = new Label(stock <= 0 ? "0 \u00B7 Out of stock" : stock + " left");
                badge.getStyleClass().add("badge-low");
                setText(null);
                setGraphic(badge);
            }
        });
    }

    /**
     * Fills the dashboard card from the product list.
     * TEMPORARY: replace SAMPLE_PRODUCTS with a database query.
     */
    private void loadLowStock() {
        if (tvLowStock == null) {
            return;
        }
        tvLowStock.setItems(FXCollections.observableArrayList(
                SAMPLE_PRODUCTS.stream()
                        .map(p -> lowStockItem(p.name, p.category, p.stock))
                        .toList()));
    }

    private Map<String, Object> lowStockItem(String name, String category, int stock) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("category", category);
        m.put("stock", stock);
        return m;
    }

    // =====================================================================
    //  Weekly chart (Stand Revenue and Order Revenue)
    // =====================================================================

    @FXML
    private void handlePrevWeek() {
        weekStart = weekStart.minusWeeks(1);
        renderWeek();
    }

    @FXML
    private void handleNextWeek() {
        if (weekStart.isBefore(maxWeekStart)) {
            weekStart = weekStart.plusWeeks(1);
            renderWeek();
        }
    }

    /** Draws the week starting at weekStart (Mon to Sun) and updates the labels under the chart. */
    private void renderWeek() {
        int[] stand = new int[7];
        int[] orders = new int[7];

        for (StandSale s : SAMPLE_STAND_SALES) {
            long day = ChronoUnit.DAYS.between(weekStart, s.date());
            if (day >= 0 && day < 7) {
                stand[(int) day] += s.total();
            }
        }
        for (OrderSale o : SAMPLE_ORDERS) {
            long day = ChronoUnit.DAYS.between(weekStart, o.date());
            if (day >= 0 && day < 7 && countsAsOrderRevenue(o)) {
                orders[(int) day] += o.price();
            }
        }

        renderRevenueBars(stand, orders);

        // what week + the dates it covers
        LocalDate weekEnd = weekStart.plusDays(6);
        int weekNumber = weekStart.get(WeekFields.ISO.weekOfWeekBasedYear());
        boolean thisWeek = weekStart.equals(mondayOf(today()));
        lblWeekTitle.setText("Week " + weekNumber + (thisWeek ? " (this week)" : ""));

        String from = (weekStart.getYear() == weekEnd.getYear()) ? weekStart.format(MD_FMT) : weekStart.format(MDY_FMT);
        lblWeekRange.setText(from + " \u2013 " + weekEnd.format(MDY_FMT));

        // can't go past the newest week
        btnNextWeek.setDisable(!weekStart.isBefore(maxWeekStart));
    }

    /**
     * Draws two pill bars per day: dark = stand revenue, light = order revenue.
     * Both use the same scale so they can be compared.
     */
    private void renderRevenueBars(int[] stand, int[] orders) {
        barSample.getChildren().clear();

        int max = 0;
        for (int i = 0; i < 7; i++) {
            max = Math.max(max, Math.max(stand[i], orders[i]));
        }

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);

            HBox pair = new HBox(BAR_PAIR_GAP,
                    makeBar(stand[i], max, TIER_STAND, "Stand revenue"),
                    makeBar(orders[i], max, TIER_ORDER, "Order revenue"));
            pair.setAlignment(Pos.BOTTOM_CENTER);
            pair.setFillHeight(false);

            Label day = new Label(date.format(DAY_NAME_FMT) + "\n" + date.format(DAY_NUM_FMT));
            day.setAlignment(Pos.CENTER);
            day.setTextAlignment(TextAlignment.CENTER);
            day.setStyle("-fx-font-size: 13px; -fx-text-fill: #6F6570;");

            VBox column = new VBox(5.0, pair, day);
            column.setAlignment(Pos.BOTTOM_CENTER);
            HBox.setHgrow(column, Priority.ALWAYS);
            column.setMaxWidth(Double.MAX_VALUE);
            barSample.getChildren().add(column);
        }
    }

    private Rectangle makeBar(int value, int max, String tierClass, String tooltipLabel) {
        boolean empty = value <= 0 || max <= 0;
        double height = empty ? BAR_WIDTH : Math.max(BAR_WIDTH, BAR_MAX_HEIGHT * value / max);

        Rectangle bar = new Rectangle(BAR_WIDTH, height);
        bar.setArcWidth(BAR_WIDTH);
        bar.setArcHeight(BAR_WIDTH);
        bar.getStyleClass().addAll("sample-bar", empty ? TIER_EMPTY : tierClass);
        Tooltip.install(bar, new Tooltip(tooltipLabel + ": " + PESO + Math.max(value, 0)));
        return bar;
    }

    // =====================================================================
    //  Semicircle gauge (Orders by Status)
    // =====================================================================

    /**
     * The gauge is the top half of a circle (180 degrees). It is filled from the
     * right: cancelled, then done, then pending takes the rest, so pending sits on the left.
     */
    private void renderStatusGauge(int pending, int done, int cancelled) {
        // Don't crash the whole dashboard if the fx:ids are missing in DASHBOARD.fxml
        if (arcPending == null || arcDone == null || arcCancelled == null
                || lblGaugePct == null || lblLegendPending == null
                || lblLegendDone == null || lblLegendCancelled == null) {
            System.err.println("Dashboard gauge: add fx:id to the Arcs and labels in DASHBOARD.fxml "
                    + "(arcPending, arcDone, arcCancelled, lblGaugePct, lblLegendPending, "
                    + "lblLegendDone, lblLegendCancelled).");
            return;
        }

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

    private void hide(Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    // =====================================================================
    //  Navigation (banner, quick actions, "View all" links)
    // =====================================================================

    @FXML
    private void goOrders() {
        switchScreen("ORDERS.fxml", "Orders");
    }

    @FXML
    private void goStandSale() {
        switchScreen("STANDSALE.fxml", "Stand Sale");
    }

    @FXML
    private void goInventory() {
        switchScreen("INVENTORY_LIST.fxml", "Inventory");
    }

    /** Opens the borderless "Low stock items" dialog (blurred background, same style as About). */
    @FXML
    private void handleOpenLowStock() {
        boolean openInventory = LowStockController.show(currentStage(), SAMPLE_PRODUCTS);
        if (openInventory) {
            goInventory();
        }
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    private Stage currentStage() {
        return (Stage) txtSearch.getScene().getWindow();
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
            ScreenTransition.show(stage.getScene(), root);
            stage.setTitle("Sassy's Creation - " + title);
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