package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
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

    /**
     * Makes the spinner's text field invisible as a "box": no background, border or focus ring,
     * so nothing pokes out beside the arrow buttons. The spinner itself keeps its own look.
     */
    private static final String EDITOR_STYLE =
            "-fx-background-color: transparent;"
                    + "-fx-background-insets: 0;"
                    + "-fx-background-radius: 0;"
                    + "-fx-border-color: transparent;"
                    + "-fx-border-width: 0;"
                    + "-fx-focus-color: transparent;"
                    + "-fx-faint-focus-color: transparent;"
                    + "-fx-padding: 0 10 0 10;";

    private static final String SPINNER_STYLE =
            "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

    @FXML private Label dName;
    @FXML private Label dStock;
    @FXML private Spinner<Integer> spRestockQty;
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

    /** Lets the user drag the undecorated window by its background (not while selecting text). */
    private static void enableDrag(Parent root, Stage stage) {
        final double[] offset = new double[2];
        root.setOnMousePressed(e -> {
            offset[0] = e.getScreenX() - stage.getX();
            offset[1] = e.getScreenY() - stage.getY();
        });
        root.setOnMouseDragged(e -> {
            if (isInTextInput(e.getTarget())) {
                return;
            }
            stage.setX(e.getScreenX() - offset[0]);
            stage.setY(e.getScreenY() - offset[1]);
        });
    }

    private static boolean isInTextInput(Object target) {
        Node n = (target instanceof Node) ? (Node) target : null;
        while (n != null) {
            if (n instanceof TextInputControl || n instanceof Spinner) {
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
        spRestockQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(QTY_MIN, QTY_MAX, 1));
        spRestockQty.setEditable(true);

        // stop the text field from drawing its own box / focus ring beside the arrows
        spRestockQty.setStyle(SPINNER_STYLE);
        spRestockQty.getEditor().setStyle(EDITOR_STYLE);

        // live "stock after restock" preview
        spRestockQty.valueProperty().addListener((obs, o, n) -> updateNewStock());
        // commit typed text when focus is lost
        spRestockQty.focusedProperty().addListener((obs, was, is) -> {
            if (!is) {
                commitSpinner();
            }
        });
        spRestockQty.getEditor().focusedProperty().addListener((obs, was, is) -> {
            if (!is) {
                commitSpinner();
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

    private void updateNewStock() {
        Integer qty = spRestockQty.getValue();
        dNewStock.setText(String.valueOf(currentStock + (qty == null ? 0 : qty)));
    }

    /** Parses what is typed in the spinner, clamps it, and writes it back. */
    private int commitSpinner() {
        int value;
        try {
            value = Integer.parseInt(spRestockQty.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            value = spRestockQty.getValue() == null ? QTY_MIN : spRestockQty.getValue();
        }
        value = Math.max(QTY_MIN, Math.min(QTY_MAX, value));
        spRestockQty.getValueFactory().setValue(value);
        spRestockQty.getEditor().setText(String.valueOf(value));
        return value;
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleConfirmRestock() {
        result = OptionalInt.of(commitSpinner());
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