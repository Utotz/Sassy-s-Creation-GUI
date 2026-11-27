package com.sassyscreation.sassyscreationgui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the "Low stock items" dialog (LOWSTOCK.fxml).
 * Same look as About, and it blurs the page behind it.
 *
 * Usage:
 *   List<LowStockController.StockItem> items = List.of(
 *           new LowStockController.StockItem("Rose Keychain", "Keychain", 5), ...);
 *   boolean goInventory = LowStockController.show(window, items);
 *   if (goInventory) { goInventory(); }
 */
public class LowStockController {

    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/LOWSTOCK.fxml";

    private static final int DEFAULT_THRESHOLD = 5;
    private static final int THRESHOLD_MAX = 999;

    // ---- background blur while the dialog is open ----
    private static final double BLUR_RADIUS = 14;
    private static final double BLUR_FADE_MS = 220;

    /** One product to check. Pass ALL products; the dialog filters them. */
    public static class StockItem {
        public final String name;
        public final String category;
        public final int stock;

        public StockItem(String name, String category, int stock) {
            this.name = name;
            this.category = category;
            this.stock = stock;
        }
    }

    @FXML private StackPane root;
    @FXML private ComboBox<String> cbLowCategory;
    @FXML private Spinner<Integer> spThreshold;
    @FXML private TableView<Map<String, Object>> tvLowStock;
    @FXML private TableColumn<Map<String, Object>, Object> colLowName;
    @FXML private TableColumn<Map<String, Object>, Object> colLowCat;
    @FXML private TableColumn<Map<String, Object>, Object> colLowStock;
    @FXML private Label lblLowStockCount;
    @FXML private Button btnCloseLowStock;
    @FXML private Button btnGoInventory;

    private List<StockItem> items = List.of();
    private boolean goToInventory = false;
    private double dragOffsetX;
    private double dragOffsetY;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog (borderless, blurred background) and blocks until it is closed.
     *
     * @return true if the user clicked "Go to Inventory"
     */
    public static boolean show(Window owner, List<StockItem> items) {
        Node ownerRoot = (owner != null && owner.getScene() != null) ? owner.getScene().getRoot() : null;
        Effect previousEffect = ownerRoot != null ? ownerRoot.getEffect() : null;
        try {
            FXMLLoader loader = new FXMLLoader(LowStockController.class.getResource(FXML_PATH));
            Parent root = loader.load();

            LowStockController controller = loader.getController();
            controller.setItems(items);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.setTitle("Sassy's Creation - Low stock");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.sizeToScene();

            // blur the page behind the dialog (fades in smoothly)
            if (ownerRoot != null) {
                GaussianBlur blur = new GaussianBlur(0);
                ownerRoot.setEffect(blur);
                new Timeline(new KeyFrame(Duration.millis(BLUR_FADE_MS),
                        new KeyValue(blur.radiusProperty(), BLUR_RADIUS, Interpolator.EASE_OUT))).play();
            }

            stage.showAndWait();

            return controller.goToInventory;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ownerRoot != null) {
                ownerRoot.setEffect(previousEffect);   // remove blur when the dialog closes
            }
        }
    }

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        cbLowCategory.setValue("All");

        spThreshold.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, THRESHOLD_MAX, DEFAULT_THRESHOLD));

        // Digits only (max 3) in the threshold editor
        spThreshold.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("\\d{0,3}")) {
                spThreshold.getEditor().setText(oldText);
            }
        });
        spThreshold.getEditor().focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                commitThreshold();
            }
        });

        // Stock column: red badge for 0, amber badge for low
        colLowStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                int stock = ((Number) item).intValue();
                Label badge = new Label(String.valueOf(stock));
                badge.getStyleClass().add(stock <= 0 ? "badge-low" : "badge-pending");
                setGraphic(badge);
                setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        cbLowCategory.valueProperty().addListener((obs, o, n) -> refresh());
        spThreshold.valueProperty().addListener((obs, o, n) -> refresh());

        btnCloseLowStock.setCancelButton(true);   // Esc closes

        // Drag the undecorated window (only by the card background, not by controls)
        root.setOnMousePressed(e -> {
            if (isOnControl(e.getTarget())) {
                return;
            }
            Stage stage = (Stage) root.getScene().getWindow();
            dragOffsetX = e.getScreenX() - stage.getX();
            dragOffsetY = e.getScreenY() - stage.getY();
        });
        root.setOnMouseDragged(e -> {
            if (isOnControl(e.getTarget())) {
                return;
            }
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });
    }

    /** True if the mouse is on a button, table, combo box, spinner... (a Label still counts as background). */
    private boolean isOnControl(Object target) {
        Node n = target instanceof Node ? (Node) target : null;
        while (n != null && n != root) {
            if (n instanceof Control && !(n instanceof Label)) {
                return true;
            }
            n = n.getParent();
        }
        return false;
    }

    private void setItems(List<StockItem> items) {
        this.items = items == null ? List.of() : items;
        refresh();
    }

    // ---------------------------------------------------------------
    // Filtering
    // ---------------------------------------------------------------

    private int commitThreshold() {
        int value;
        try {
            value = Integer.parseInt(spThreshold.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            value = spThreshold.getValue() == null ? DEFAULT_THRESHOLD : spThreshold.getValue();
        }
        value = Math.max(0, Math.min(THRESHOLD_MAX, value));
        spThreshold.getValueFactory().setValue(value);
        spThreshold.getEditor().setText(String.valueOf(value));
        return value;
    }

    private void refresh() {
        int threshold = spThreshold.getValue() == null ? DEFAULT_THRESHOLD : spThreshold.getValue();
        String category = cbLowCategory.getValue();
        boolean allCategories = category == null || "All".equals(category);

        List<StockItem> matches = new ArrayList<>();
        for (StockItem item : items) {
            if (item.stock <= threshold && (allCategories || category.equals(item.category))) {
                matches.add(item);
            }
        }
        // Emptiest first, then by name
        matches.sort(Comparator.<StockItem>comparingInt(i -> i.stock)
                .thenComparing(i -> i.name, String.CASE_INSENSITIVE_ORDER));

        List<Map<String, Object>> rows = new ArrayList<>();
        int out = 0;
        for (StockItem item : matches) {
            Map<String, Object> row = new HashMap<>();
            row.put("Name", item.name);
            row.put("Category", item.category);
            row.put("stock", item.stock);
            rows.add(row);
            if (item.stock <= 0) {
                out++;
            }
        }
        tvLowStock.setItems(FXCollections.observableArrayList(rows));
        lblLowStockCount.setText(out + " out of stock \u00B7 " + (matches.size() - out) + " low");
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleCloseLowStock() {
        close();
    }

    @FXML
    private void handleGoToInventory() {
        goToInventory = true;
        close();
    }

    private void close() {
        ((Stage) btnCloseLowStock.getScene().getWindow()).close();
    }
}