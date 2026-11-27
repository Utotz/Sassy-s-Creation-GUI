package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Controller for the "Change order status?" confirmation (STATUSCONFIRM.fxml).
 *
 * The FXML root must declare
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.StatusConfirmController"
 *
 * Usage (e.g. in OrdersController when the status combo changes):
 *   boolean ok = StatusConfirmController.show(owner, 12, "Maria", "Flower Bouquet",
 *           "Pending", "Cancelled");
 *   if (ok) {
 *       // update the status in the database (restore stock if cancelled), then refresh
 *   }
 */
public class StatusConfirmController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/STATUSCONFIRM.fxml";

    @FXML private Label dID;
    @FXML private Label dCustomer;
    @FXML private Label dProduct;
    @FXML private Label dOldStatus;
    @FXML private Label dNewStatus;
    @FXML private Label lblStatusNote;
    @FXML private Button btnConfirmStatus;
    @FXML private Button btnCancelStatus;

    private boolean confirmed = false;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /** Shows the dialog and blocks until closed. @return true only if the user confirmed. */
    public static boolean show(Window owner, Object orderId, String customer, String product,
                               String oldStatus, String newStatus) {
        try {
            FXMLLoader loader = new FXMLLoader(StatusConfirmController.class.getResource(FXML_PATH));
            Parent root = loader.load();
            StatusConfirmController controller = loader.getController();
            controller.setOrder(orderId, customer, product, oldStatus, newStatus);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Change status");
            stage.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            enableDrag(root, stage);

            stage.showAndWait();

            return controller.confirmed;
        } catch (Exception e) {
            // Any failure means "do not change"
            e.printStackTrace();
            return false;
        }
    }

    /** Lets the user drag the undecorated window by its background. */
    private static void enableDrag(Parent root, Stage stage) {
        final double[] offset = new double[2];
        root.setOnMousePressed(e -> {
            offset[0] = e.getScreenX() - stage.getX();
            offset[1] = e.getScreenY() - stage.getY();
        });
        root.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - offset[0]);
            stage.setY(e.getScreenY() - offset[1]);
        });
    }

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        btnCancelStatus.setCancelButton(true);
        lblStatusNote.managedProperty().bind(lblStatusNote.visibleProperty());
    }

    private void setOrder(Object orderId, String customer, String product,
                          String oldStatus, String newStatus) {
        dID.setText("#" + orderId);
        dCustomer.setText(customer);
        dProduct.setText(product);
        dOldStatus.setText(oldStatus);
        dNewStatus.setText(newStatus);

        // TODO: adjust these notes to match your real stock rules
        if ("Cancelled".equalsIgnoreCase(newStatus)) {
            lblStatusNote.setText("The reserved stock will be returned to inventory.");
            lblStatusNote.setVisible(true);
        } else if ("Cancelled".equalsIgnoreCase(oldStatus)) {
            lblStatusNote.setText("Stock will be deducted again for this order.");
            lblStatusNote.setVisible(true);
        } else {
            lblStatusNote.setVisible(false);
        }
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleConfirmStatus() {
        confirmed = true;
        close();
    }

    @FXML
    private void handleCancelStatus() {
        confirmed = false;
        close();
    }

    private void close() {
        ((Stage) btnConfirmStatus.getScene().getWindow()).close();
    }
}
