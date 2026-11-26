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
 * Controller for the "Delete this sale?" confirmation dialog (DELETESALE.fxml).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.DeleteSaleController"
 *
 * Usage (e.g. in StandSaleController):
 *
 *   boolean confirmed = DeleteSaleController.show(
 *           btnDelete.getScene().getWindow(),
 *           sale.getDate(), sale.getProduct(), sale.getQty(),
 *           "₱50.00", "₱500.00");
 *   if (confirmed) {
 *       // delete the sale from the database, restore the stock (+qty), then refresh the table
 *   }
 */
public class DeleteSaleController {

    // Must sit in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/DELETESALE.fxml";

    // Blur strength of the screen behind the dialog
    private static final double BLUR_RADIUS = 10;

    @FXML private StackPane root;
    @FXML private Label dDate;
    @FXML private Label dProduct;
    @FXML private Label dQty;
    @FXML private Label dPrice;
    @FXML private Label dTotal;
    @FXML private Label lblRestoreNote;
    @FXML private Button btnConfirmDelete;
    @FXML private Button btnCancelDelete;

    private boolean confirmed = false;
    private double dragX;
    private double dragY;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @param unitPrice and total are shown as given, so pass them already formatted (e.g. "₱50.00")
     * @return true only if the user clicked "Yes, delete"
     */
    public static boolean show(Window owner, String date, String product, Object qty,
                               String unitPrice, String total) {
        try {
            FXMLLoader loader = new FXMLLoader(DeleteSaleController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT); // only the card is visible
            DeleteSaleController controller = loader.getController();
            controller.setSale(date, product, qty, unitPrice, total);

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

            return controller.confirmed;
        } catch (Exception e) {
            // Any failure means "do not delete"
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        // Esc and Enter both mean "cancel", so a stray keypress can't delete a sale
        btnCancelDelete.setCancelButton(true);
        btnCancelDelete.setDefaultButton(true);

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

    public void setSale(String date, String product, Object qty, String unitPrice, String total) {
        dDate.setText(nz(date));
        dProduct.setText(nz(product));
        dQty.setText(String.valueOf(qty));
        dPrice.setText(nz(unitPrice));
        dTotal.setText(nz(total));
        lblRestoreNote.setText("Stock for " + nz(product) + " will be restored (+" + qty + ").");
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    // ---------------------------------------------------------------
    // Button handlers
    // ---------------------------------------------------------------

    @FXML
    private void handleConfirmDelete() {
        confirmed = true;
        close();
    }

    @FXML
    private void handleCancelDelete() {
        confirmed = false;
        close();
    }

    private void close() {
        ((Stage) btnCancelDelete.getScene().getWindow()).close();
    }
}