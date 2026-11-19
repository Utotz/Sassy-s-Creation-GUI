package com.sassyscreation.sassyscreationgui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the "Low stock items" dialog (opened from the dashboard).
 *
 * IMPORTANT: the root element of the FXML (the first <StackPane ...>) must declare this controller:
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.LowStockController"
 *
 * Reads the products from InventoryController.getProducts(), so it always matches the inventory.
 * "Go to Inventory" closes this dialog and switches the main window to the inventory screen.
 */
public class LowStockController implements Initializable {

    private static final String FXML_PATH           = "/com/sassyscreation/sassyscreationgui/";
    private static final String FXML_INVENTORY_LIST = "INVENTORY_LIST.fxml";

    /** Starting value of the "Stock at or below" box. */
    private static final int DEFAULT_THRESHOLD = 5;

    // ---- fields from the FXML ----
    @FXML private ComboBox<String> cbLowCategory;
    @FXML private Spinner<Integer> spThreshold;
    @FXML private TableView<Map<String, Object>> tvLowStock;
    @FXML private TableColumn<Map<String, Object>, Object> colLowName;
    @FXML private TableColumn<Map<String, Object>, Object> colLowCat;
    @FXML private TableColumn<Map<String, Object>, Object> colLowStock;
    @FXML private Label lblLowStockCount;
    @FXML private Button btnCloseLowStock;
    @FXML private Button btnGoInventory;

    // =====================================================================
    // Init
    // =====================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Spinner: 0 - 999, starts at the default threshold
        spThreshold.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999, DEFAULT_THRESHOLD));
        spThreshold.getEditor().focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                commitSpinnerText();
            }
        });

        if (cbLowCategory.getValue() == null) {
            cbLowCategory.setValue("All");
        }

        // Sort the stock column by the number inside its badge
        colLowStock.setComparator(Comparator.comparingInt(LowStockController::badgeNumber));

        // Reload whenever the filter changes
        cbLowCategory.valueProperty().addListener((obs, o, n) -> reload());
        spThreshold.valueProperty().addListener((obs, o, n) -> reload());

        reload();
    }

    private void commitSpinnerText() {
        try {
            int typed = Integer.parseInt(spThreshold.getEditor().getText().trim());
            typed = Math.max(0, Math.min(999, typed));
            spThreshold.getValueFactory().setValue(typed);
        } catch (NumberFormatException e) {
            spThreshold.getEditor().setText(String.valueOf(spThreshold.getValue()));
        }
    }

    // =====================================================================
    // Data
    // =====================================================================

    /** Rebuilds the table for the chosen category and threshold. */
    private void reload() {
        String category = cbLowCategory.getValue() == null ? "All" : cbLowCategory.getValue();
        int threshold = spThreshold.getValue() == null ? DEFAULT_THRESHOLD : spThreshold.getValue();

        List<InventoryController.Product> matches = new ArrayList<>();
        for (InventoryController.Product p : InventoryController.getProducts()) {
            boolean categoryOk = category.equals("All") || p.getCategory().equals(category);
            if (categoryOk && p.getStock() <= threshold) {
                matches.add(p);
            }
        }
        // lowest stock first, then by name
        matches.sort(Comparator.comparingInt(InventoryController.Product::getStock)
                .thenComparing(InventoryController.Product::getName));

        List<Map<String, Object>> rows = new ArrayList<>();
        int outOfStock = 0;
        int low = 0;
        for (InventoryController.Product p : matches) {
            rows.add(row(p));
            if (p.getStock() <= 0) {
                outOfStock++;
            } else {
                low++;
            }
        }

        tvLowStock.setItems(FXCollections.observableArrayList(rows));
        lblLowStockCount.setText(outOfStock + " out of stock  \u00B7  " + low + " low");
    }

    /** Keys match the MapValueFactory keys in the FXML (case-sensitive): Name, Category, stock. */
    private Map<String, Object> row(InventoryController.Product p) {
        Label badge = new Label(String.valueOf(p.getStock()));
        badge.getStyleClass().add(p.getStock() <= 0 ? "badge-low" : "badge-pending");

        Map<String, Object> m = new HashMap<>();
        m.put("Name", p.getName());
        m.put("Category", p.getCategory());
        m.put("stock", badge);
        return m;
    }

    private static int badgeNumber(Object cellValue) {
        if (cellValue instanceof Label label) {
            try {
                return Integer.parseInt(label.getText().trim());
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        return 0;
    }

    // =====================================================================
    // Buttons
    // =====================================================================
    @FXML
    private void handleCloseLowStock() {
        ((Stage) btnCloseLowStock.getScene().getWindow()).close();
    }

    /** Closes this dialog and opens the inventory screen in the main window. */
    @FXML
    private void handleGoToInventory() {
        Stage dialog = (Stage) btnGoInventory.getScene().getWindow();
        Window owner = dialog.getOwner();
        dialog.close();

        if (!(owner instanceof Stage mainStage)) {
            return;
        }
        try {
            URL resource = getClass().getResource(FXML_PATH + FXML_INVENTORY_LIST);
            if (resource == null) {
                ErrorDialogController.show(mainStage, "Screen not found",
                        FXML_INVENTORY_LIST + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            mainStage.getScene().setRoot(root);
            mainStage.setTitle("Sassy's Creation - Inventory");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialogController.show(mainStage, "Could not open Inventory", rootCause(e));
        }
    }

    private String rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.toString();
    }
}
