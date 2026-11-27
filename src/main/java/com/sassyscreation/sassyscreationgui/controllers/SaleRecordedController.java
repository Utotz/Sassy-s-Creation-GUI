package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Controller for the "Sale recorded" popup (SALERECORDED.fxml).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.SaleRecordedController"
 *
 * Usage (in StandSaleController, after the sale is saved):
 *
 *   SaleRecordedController.show(currentStage(), p.name, qty, peso(qty * p.price));
 */
public class SaleRecordedController {

    // Must sit in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/SALERECORDED.fxml";

    // Blur strength of the screen behind the dialog
    private static final double BLUR_RADIUS = 10;

    @FXML private StackPane root;
    @FXML private Label dProduct;
    @FXML private Label dQty;
    @FXML private Label dTotal;
    @FXML private Label lblStockNote;
    @FXML private Button btnDone;

    private double dragX;
    private double dragY;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @param total the total already formatted (e.g. "₱90.00")
     */
    public static void show(Window owner, String product, int qty, String total) {
        try {
            FXMLLoader loader = new FXMLLoader(SaleRecordedController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT); // only the card is visible
            SaleRecordedController controller = loader.getController();
            controller.setSale(product, qty, total);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); // no title bar, no window background
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);

            // Blur the screen behind the dialog while it is open
            Node ownerRoot = owner == null || owner.getScene() == null ? null : owner.getScene().getRoot();
            Effect oldEffect = ownerRoot == null ? null : ownerRoot.getEffect();
            if (ownerRoot != null) {
                ownerRoot.setEffect(new GaussianBlur(BLUR_RADIUS));
            }
            try {
                stage.showAndWait();
            } finally {
                if (ownerRoot != null) {
                    ownerRoot.setEffect(oldEffect);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        // Enter and Esc both close the dialog
        btnDone.setDefaultButton(true);
        btnDone.setCancelButton(true);

        // Drag the window
        root.setOnMousePressed(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            dragX = e.getScreenX() - stage.getX();
            dragY = e.getScreenY() - stage.getY();
        });
        root.setOnMouseDragged(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(e.getScreenX() - dragX);
            stage.setY(e.getScreenY() - dragY);
        });
    }

    // ---------------------------------------------------------------
    // Filling in the sale details
    // ---------------------------------------------------------------

    public void setSale(String product, int qty, String total) {
        dProduct.setText(nz(product));
        dQty.setText(String.valueOf(qty));
        dTotal.setText(nz(total));
        lblStockNote.setText("Stock for " + nz(product) + " was deducted automatically.");
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    // ---------------------------------------------------------------
    // Button handler
    // ---------------------------------------------------------------

    @FXML
    private void handleDone() {
        ((Stage) btnDone.getScene().getWindow()).close();
    }
}
