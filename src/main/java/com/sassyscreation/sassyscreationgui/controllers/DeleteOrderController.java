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
 * Controller for the "Delete this order?" confirmation dialog (DELETEORDER.fxml).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.DeleteOrderController"
 *
 * Usage (e.g. in OrdersController):
 *
 *   boolean confirmed = DeleteOrderController.show(
 *           btnDelete.getScene().getWindow(),
 *           order.getId(), order.getCustomer(), order.getProduct(),
 *           order.getPrice(), order.getStatus());
 *   if (confirmed) {
 *       // delete the order from the database + restore stock, then refresh the table
 *   }
 */
public class DeleteOrderController {

    // Must sit in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/DELETEORDER.fxml";

    // Blur strength of the screen behind the dialog
    private static final double BLUR_RADIUS = 10;

    @FXML private StackPane root;
    @FXML private Label dID;
    @FXML private Label dCustomer;
    @FXML private Label dProduct;
    @FXML private Label dPrice;
    @FXML private Label dStatus;
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
     * @return true only if the user clicked "Yes, delete"
     */
    public static boolean show(Window owner, Object id, String customer,
                               String product, String price, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(DeleteOrderController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT); // only the card is visible
            DeleteOrderController controller = loader.getController();
            controller.setOrder(id, customer, product, price, status);

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
        // Esc = cancel (safe default for a destructive dialog)
        btnCancelDelete.setCancelButton(true);
        // Start focus on Cancel so a stray Enter can't delete the order
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
    // Filling in the order details
    // ---------------------------------------------------------------

    public void setOrder(Object id, String customer, String product, String price, String status) {
        dID.setText(String.valueOf(id));
        dCustomer.setText(nz(customer));
        dProduct.setText(nz(product));
        dPrice.setText(nz(price));
        dStatus.setText(nz(status));
        lblRestoreNote.setText("Stock for " + nz(product) + " will be restored.");
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