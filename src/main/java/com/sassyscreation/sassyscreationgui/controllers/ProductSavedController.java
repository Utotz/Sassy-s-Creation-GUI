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
 * Controller for the "Product saved" popup (PRODUCTSAVED.fxml).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.ProductSavedController"
 *
 * Usage (in AddProductController, right after InventoryController.addProduct(...) returns true;
 * replaces the plain "Product saved" Alert):
 *
 *   ProductSavedController.show(stage, name, category, "\u20B1" + String.format("%,.2f", price), stock);
 */
public class ProductSavedController {

    // Must sit in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/PRODUCTSAVED.fxml";

    // Blur strength of the screen behind the dialog
    private static final double BLUR_RADIUS = 10;

    @FXML private StackPane root;
    @FXML private Label dName;
    @FXML private Label dCategory;
    @FXML private Label dPrice;
    @FXML private Label dStock;
    @FXML private Label lblSavedNote;
    @FXML private Button btnDone;

    private double dragX;
    private double dragY;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @param price the price already formatted (e.g. "₱90.00")
     */
    public static void show(Window owner, String name, String category, String price, int stock) {
        try {
            FXMLLoader loader = new FXMLLoader(ProductSavedController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT); // only the card is visible
            ProductSavedController controller = loader.getController();
            controller.setProduct(name, category, price, stock);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); // no title bar, no window background
            if (owner != null) {
                stage.initOwner(owner);
            }
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
    // Filling in the product details
    // ---------------------------------------------------------------

    public void setProduct(String name, String category, String price, int stock) {
        dName.setText(nz(name));
        dCategory.setText(nz(category));
        dPrice.setText(nz(price));
        dStock.setText(String.valueOf(stock));
        lblSavedNote.setText("\"" + nz(name) + "\" was added to the inventory.");
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
