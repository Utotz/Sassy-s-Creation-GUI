package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controller for the "Delete this product?" confirmation dialog (DELETEPRODUCT.fxml).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.DeleteProductController"
 *
 * Usage (e.g. in InventoryController):
 *
 *   boolean confirmed = DeleteProductController.show(
 *           btnDelete.getScene().getWindow(),
 *           product.getId(), product.getName(), product.getCategory(),
 *           "₱" + product.getPrice(), product.getStock());
 *   if (confirmed) {
 *       // delete the product from the database, then refresh the table
 *   }
 */
public class DeleteProductController {

    // Must sit in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/DELETEPRODUCT.fxml";

    @FXML private Label dID;
    @FXML private Label dName;
    @FXML private Label dCategory;
    @FXML private Label dPrice;
    @FXML private Label dStock;
    @FXML private Button btnConfirmDelete;
    @FXML private Button btnCancelDelete;

    private boolean confirmed = false;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @return true only if the user clicked "Yes, delete"
     */
    public static boolean show(Window owner, Object id, String name, String category,
                               String price, Object stock) {
        try {
            FXMLLoader loader = new FXMLLoader(DeleteProductController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            DeleteProductController controller = loader.getController();
            controller.setProduct(id, name, category, price, stock);

            Stage stage = new Stage();
            stage.setTitle("Delete product");
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();

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
        // Esc and Enter both mean "cancel", so a stray keypress can't delete a product
        btnCancelDelete.setCancelButton(true);
        btnCancelDelete.setDefaultButton(true);
    }

    // ---------------------------------------------------------------
    // Filling in the product details
    // ---------------------------------------------------------------

    public void setProduct(Object id, String name, String category, String price, Object stock) {
        dID.setText(String.valueOf(id));
        dName.setText(nz(name));
        dCategory.setText(nz(category));
        dPrice.setText(nz(price));
        dStock.setText(String.valueOf(stock));
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