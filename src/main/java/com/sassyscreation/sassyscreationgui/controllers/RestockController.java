package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.OptionalInt;

/**
 * Controller for the "Restock product" popup (RESTOCK.fxml).
 *
 * The FXML root must declare
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.RestockController"
 *
 * Usage (e.g. in InventoryController.restockProduct):
 *   OptionalInt qty = RestockController.show(owner, p.getName(), p.getStock());
 *   if (qty.isPresent()) {
 *       // add qty.getAsInt() to the product's stock in the database, then refresh
 *   }
 *
 * While the popup is open, the window behind it is blurred.
 */
public class RestockController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/RESTOCK.fxml";

    private static final int QTY_MIN = 1;
    private static final int QTY_MAX = 9999;

    /** Blur strength applied to the window behind the popup. */
    private static final double BLUR_RADIUS = 10;

    // pill look (normal / while the number is being edited)
    private static final String PILL_BASE =
            "-fx-background-color: #FDEFF3; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-width: 1; ";
    private static final String PILL_NORMAL = PILL_BASE + "-fx-border-color: #F0D3DC;";
    private static final String PILL_FOCUSED = PILL_BASE + "-fx-border-color: #C22A62;";

    @FXML private Label dName;
    @FXML private Label dStock;
    @FXML private HBox qtyBox;
    @FXML private TextField tfRestockQty;
    @FXML private Button btnQtyMinus;
    @FXML private Button btnQtyPlus;
    @FXML private Label dNewStock;
    @FXML private Button btnCancelRestock;
    @FXML private Button btnConfirmRestock;

    private int currentStock;
    private OptionalInt result = OptionalInt.empty();

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /** Shows the dialog and blocks until closed. @return the quantity to add, or empty if cancelled. */
    public static OptionalInt show(Window owner, String productName, int currentStock) {
        // blur whatever is behind the popup (restored when the popup closes)
        Node ownerRoot = (owner != null && owner.getScene() != null) ? owner.getScene().getRoot() : null;
        Effect previousEffect = ownerRoot == null ? null : ownerRoot.getEffect();

        try {
            FXMLLoader loader = new FXMLLoader(RestockController.class.getResource(FXML_PATH));
            Parent root = loader.load();
            RestockController controller = loader.getController();
            controller.setProduct(productName, currentStock);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Restock");
            stage.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            enableDrag(root, stage);

            if (ownerRoot != null) {
                ownerRoot.setEffect(new GaussianBlur(BLUR_RADIUS));
            }

            stage.showAndWait();

            return controller.result;
        } catch (Exception e) {
            e.printStackTrace();
            return OptionalInt.empty();
        } finally {
            if (ownerRoot != null) {
                ownerRoot.setEffect(previousEffect);
            }
        }
    }

    /** Lets the user drag the undecorated window by its background (not on the number or buttons). */
    private static void enableDrag(Parent root, Stage stage) {
        final double[] offset = new double[2];
        root.setOnMousePressed(e -> {
            offset[0] = e.getScreenX() - stage.getX();
            offset[1] = e.getScreenY() - stage.getY();
        });
        root.setOnMouseDragged(e -> {
            if (isOnInput(e.getTarget())) {
                return;
            }
            stage.setX(e.getScreenX() - offset[0]);
            stage.setY(e.getScreenY() - offset[1]);
        });
    }

    private static boolean isOnInput(Object target) {
        Node n = (target instanceof Node) ? (Node) target : null;
        while (n != null) {
            if (n instanceof TextInputControl || n instanceof Button) {
                return true;
            }
            n = n.getParent();
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        tfRestockQty.setText(String.valueOf(QTY_MIN));

        // digits only, max 4
        tfRestockQty.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("\\d{0,4}")) {
                tfRestockQty.setText(oldText);
                return;
            }
            updateNewStock();
        });

        // clamp the typed number when focus is lost; pink border while editing
        tfRestockQty.focusedProperty().addListener((obs, was, is) -> {
            qtyBox.setStyle(is ? PILL_FOCUSED : PILL_NORMAL);
            if (!is) {
                setQty(typedQty());
            }
        });

        // Up / Down arrows act like + and -
        tfRestockQty.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                handleQtyPlus();
                e.consume();
            } else if (e.getCode() == KeyCode.DOWN) {
                handleQtyMinus();
                e.consume();
            }
        });

        btnCancelRestock.setCancelButton(true);
    }

    private void setProduct(String name, int stock) {
        this.currentStock = stock;
        dName.setText(name);
        dStock.setText(String.valueOf(stock));
        updateNewStock();
    }

    // ---------------------------------------------------------------
    // Quantity
    // ---------------------------------------------------------------

    /** What is typed right now (0 if empty), not clamped. */
    private int typedQty() {
        try {
            return Integer.parseInt(tfRestockQty.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Clamps to QTY_MIN..QTY_MAX and writes it into the field. */
    private int setQty(int value) {
        value = Math.max(QTY_MIN, Math.min(QTY_MAX, value));
        tfRestockQty.setText(String.valueOf(value));
        return value;
    }

    private void updateNewStock() {
        dNewStock.setText(String.valueOf(currentStock + typedQty()));
    }

    @FXML
    private void handleQtyMinus() {
        setQty(typedQty() - 1);
    }

    @FXML
    private void handleQtyPlus() {
        setQty(typedQty() + 1);
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleConfirmRestock() {
        result = OptionalInt.of(setQty(typedQty()));
        close();
    }

    @FXML
    private void handleCancelRestock() {
        result = OptionalInt.empty();
        close();
    }

    private void close() {
        ((Stage) btnCancelRestock.getScene().getWindow()).close();
    }
}