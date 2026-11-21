package com.sassyscreation.sassyscreationgui.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
    @FXML private Label lblRevenue;
    @FXML private Label lblPending;

    // ---- revenue bars + reminder ----
    @FXML private BarChart<String, Number> revenueChart;   // plain chart, hidden
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

    // bar sizes (match the Scene Builder design)
    private static final double BAR_WIDTH = 40.0;
    private static final double BAR_MAX_HEIGHT = 220.0;
    private static final double BAR_MIN_HEIGHT = 38.0;

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
        lblRevenue.setText("\u20B11,210.00");
        lblPending.setText("\u20B11,560.00");
        sidebarController.setOrdersBadge(pendingOrders);
        lblReminderTitle.setText(pendingOrders + (pendingOrders == 1 ? " order" : " orders") + " awaiting pickup");
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
    //  Navigation (banner and quick actions)
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

    @FXML
    private void handleOpenLowStock() {
        openDialog("LOWSTOCK.fxml", "Low stock");
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

    /** Opens an FXML screen in a normal pop-up window (with title bar). */
    private void openDialog(String fxmlName, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                showError("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();

            Stage dialog = new Stage(StageStyle.DECORATED);
            dialog.initOwner(currentStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sassy's Creation - " + title);

            Scene scene = new Scene(root);
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