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
 * Controller for the "Delete this sale?" confirmation dialog (Stand Sale screen).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.DeleteSaleController"
 *
 * Usage (e.g. in StandSaleController):
 *
 *   boolean confirmed = DeleteSaleController.show(
 *           btnDelete.getScene().getWindow(),
 *           sale.getDate(), sale.getProduct(), sale.getQty(),
 *           sale.getUnitPrice(), sale.getTotal());
 *   if (confirmed) {
 *       // delete the sale from the database, restore the stock (+qty), then refresh the table
 *   }
 */
public class DeleteSaleController {

    // CHANGE THIS to match the actual file name of your delete-sale FXML
    // (it must sit in src/main/resources/com/sassyscreation/sassyscreationgui/)
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/DELETESALE.fxml";

    // ---- fields from the FXML ----
    @FXML private Label dDate;
    @FXML private Label dProduct;
    @FXML private Label dQty;
    @FXML private Label dPrice;
    @FXML private Label dTotal;
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
     * @param unitPrice and total are shown as given, so pass them already formatted (e.g. "₱50.00")
     * @return true if the user clicked "Yes, delete", false otherwise
     */
    public static boolean show(Window owner, String date, String product, Object qty,
                               String unitPrice, String total) {
        try {
            FXMLLoader loader = new FXMLLoader(DeleteSaleController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            DeleteSaleController controller = loader.getController();
            controller.setSale(date, product, qty, unitPrice, total);

            Stage stage = new Stage();
            stage.setTitle("Delete sale");
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
    // Filling in the sale details
    // ---------------------------------------------------------------

    public void setSale(String date, String product, Object qty, String unitPrice, String total) {
        dDate.setText(date);
        dProduct.setText(product);
        dQty.setText(String.valueOf(qty));
        dPrice.setText(unitPrice);
        dTotal.setText(total);
        lblRestoreNote.setText("Stock for " + product + " will be restored (+" + qty + ").");
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
