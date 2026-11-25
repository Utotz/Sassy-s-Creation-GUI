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
 * Controller for the "Order details" popup (ORDERDETAILS.fxml).
 *
 * The FXML root must declare
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.OrderDetailsController"
 *
 * Usage (e.g. inside OrdersController.showOrderDetails):
 *   boolean edit = OrderDetailsController.show(owner, 12, "Maria", "Flower Bouquet",
 *           "Red", "Pickup", "₱500.00", "2026-11-23", "Pending");
 *   if (edit) {
 *       // open the order form for editing
 *   }
 */
public class OrderDetailsController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/ORDERDETAILS.fxml";

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
     * Shows the dialog and blocks until it is closed. Values are shown as given,
     * so pass price/date already formatted.
     *
     * @return true if the user clicked "Edit"
     */
    public static boolean show(Window owner, Object orderId, String customer, String product,
                               String color, String arrangement, String price,
                               String date, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(OrderDetailsController.class.getResource(FXML_PATH));
            Parent root = loader.load();
            OrderDetailsController controller = loader.getController();
            controller.setOrder(orderId, customer, product, color, arrangement, price, date, status);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Order details");
            stage.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            enableDrag(root, stage);

            stage.showAndWait();

            return controller.editRequested;
        } catch (Exception e) {
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
        btnCloseDetails.setCancelButton(true);   // Esc closes
    }

    private void setOrder(Object orderId, String customer, String product, String color,
                          String arrangement, String price, String date, String status) {
        lblDetailTitle.setText("Order #" + orderId);
        dCustomer.setText(nz(customer));
        dProduct.setText(nz(product));
        dColor.setText(nz(color));
        dArrangement.setText(nz(arrangement));
        dPrice.setText(nz(price));
        dDate.setText(nz(date));
        dStatus.setText(nz(status));
    }

    private static String nz(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleCloseDetails() {
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
