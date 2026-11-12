package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Controller for the "Delete this order?" confirmation dialog.
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

    // CHANGE THIS to match the actual file name of your delete-order FXML
    // (it must sit in src/main/resources/com/sassyscreation/sassyscreationgui/)
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/delete-order.fxml";

    // ---- fields from the FXML ----
    @FXML private Label dID;
    @FXML private Label dCustomer;
    @FXML private Label dProduct;
    @FXML private Label dPrice;
    @FXML private Label dStatus;
    @FXML private Label lblRestoreNote;
    @FXML private Button btnConfirmDelete;
    @FXML private Button btnCancelDelete;

    private boolean confirmed = false;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @return true if the user clicked "Yes, delete", false otherwise
     */
    public static boolean show(Window owner, Object id, String customer,
                               String product, String price, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(DeleteOrderController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            DeleteOrderController controller = loader.getController();
            controller.setOrder(id, customer, product, price, status);

            Stage stage = new Stage();
            stage.setTitle("Delete order");
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();

            return controller.confirmed;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Filling in the order details
    // ---------------------------------------------------------------

    public void setOrder(Object id, String customer, String product, String price, String status) {
        dID.setText(String.valueOf(id));
        dCustomer.setText(customer);
        dProduct.setText(product);
        dPrice.setText(price);
        dStatus.setText(status);
        lblRestoreNote.setText("Stock for " + product + " will be restored.");
    }

    // ---------------------------------------------------------------
    // Button handlers (names match onAction in the FXML)
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
