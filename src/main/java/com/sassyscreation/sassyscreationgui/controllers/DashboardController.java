package com.sassyscreation.sassyscreationgui.controllers;

import com.sassyscreation.sassyscreationgui.dao.ProductDAO;
import com.sassyscreation.sassyscreationgui.model.Product;
import com.sassyscreation.sassyscreationgui.util.Transitions;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    @FXML private ComboBox<String> cbDashYear;
    @FXML private Label lblNavOrdersBadge;
    @FXML private Label lblOrders;
    @FXML private Label lblProducts;
    @FXML private Label lblRevenue;
    @FXML private Label lblPending;
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
    }

    @FXML
    private void goOrders() {
        Transitions.goTo(lblOrders, "ORDERS.fxml");
    }

    @FXML
    private void goStandSale() {
        Transitions.goTo(lblOrders, "STANDSALE.fxml");
    }

    @FXML
    private void goInventory() {
        Transitions.goTo(lblOrders, "INVENTORY_GRID.fxml");
    }

    @FXML
    private void handleLogout() {
        Transitions.goTo(lblOrders, "LOGIN.fxml");
    }

    @FXML
    private void handleOpenChangePassword() {
        openDialog("CHANGEPASSWORD.fxml");
    }

    @FXML
    private void handleOpenAbout() {
        openDialog("ABOUT.fxml");
    }

    @FXML
    private void handleOpenLowStock() {
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
}