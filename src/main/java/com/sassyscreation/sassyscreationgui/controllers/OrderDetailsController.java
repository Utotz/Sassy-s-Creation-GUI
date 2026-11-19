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
import java.net.URL;

/**
 * Controller for the "Order #N" details dialog (Orders screen).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.OrderDetailsController"
 *
 * Usage (e.g. in OrdersController, when the user clicks a row or the "view" button):
 *
 *   boolean editRequested = OrderDetailsController.show(
 *           table.getScene().getWindow(),
 *           order.getId(), order.getCustomer(), order.getProduct(), order.getColor(),
 *           order.getArrangement(), "\u20B1" + String.format("%,.2f", order.getPrice()),
 *           order.getDate(), order.getStatus());
 *   if (editRequested) {
 *       // the user pressed "Edit order" -> open your edit order screen / dialog here
 *   }
 *
 * Price is shown as given, so pass it already formatted (e.g. "₱100.00").
 */
public class OrderDetailsController {

    // CHANGE THIS to match the actual file name of your order-details FXML
    // (it must sit in src/main/resources/com/sassyscreation/sassyscreationgui/)
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/ORDERDETAILS.fxml";

    // ---- fields from the FXML ----
    @FXML private Label lblDetailTitle;
    @FXML private Label dCustomer;
    @FXML private Label dProduct;
    @FXML private Label dColor;
    @FXML private Label dArrangement;
    @FXML private Label dPrice;
    @FXML private Label dDate;
    @FXML private Label dStatus;
    @FXML private Button btnCloseDetails;
    @FXML private Button btnEditFromDetails;

    private boolean editRequested = false;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @return true if the user clicked "Edit order", false if they clicked "Close" or closed the window
     */
    public static boolean show(Window owner, Object orderId, String customer, String product,
                               String color, String arrangement, String price,
                               String date, String status) {
        try {
            URL url = OrderDetailsController.class.getResource(FXML_PATH);
            if (url == null) {
                throw new IOException("FXML not found: " + FXML_PATH);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            OrderDetailsController controller = loader.getController();
            controller.setOrder(orderId, customer, product, color, arrangement, price, date, status);

            Stage stage = new Stage();
            stage.setTitle("Order details");
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();

            return controller.editRequested;
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            ErrorDialogController.show(owner, "Could not open order details", String.valueOf(e.getMessage()));
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Filling in the order details
    // ---------------------------------------------------------------

    public void setOrder(Object orderId, String customer, String product, String color,
                         String arrangement, String price, String date, String status) {
        lblDetailTitle.setText("Order #" + orderId);
        dCustomer.setText(orDash(customer));
        dProduct.setText(orDash(product));
        dColor.setText(orDash(color));
        dArrangement.setText(orDash(arrangement));
        dPrice.setText(orDash(price));
        dDate.setText(orDash(date));
        dStatus.setText(orDash(status));
    }

    /** Shows "-" for an empty value instead of a blank space. */
    private static String orDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    // ---------------------------------------------------------------
    // Button handlers (names match onAction in the FXML)
    // ---------------------------------------------------------------

    @FXML
    private void handleCloseDetails() {
        editRequested = false;
        close();
    }

    @FXML
    private void handleEditFromDetails() {
        editRequested = true;
        close();
    }

    private void close() {
        ((Stage) btnCloseDetails.getScene().getWindow()).close();
    }
}
