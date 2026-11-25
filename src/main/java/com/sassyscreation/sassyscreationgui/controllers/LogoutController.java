package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Controller for the "Log out" confirmation dialog (LOGOUT.fxml).
 *
 * The FXML root must declare
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.LogoutController"
 *
 * Usage:
 *   boolean yes = LogoutController.show(btnSomething.getScene().getWindow());
 *   if (yes) {
 *       // btnConfirmLogout was clicked
 *   }
 */
public class LogoutController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/LOGOUT.fxml";

    @FXML private Button btnConfirmLogout;
    @FXML private Button btnCancelLogout;

    private boolean confirmed = false;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /** Shows the dialog and blocks until it is closed. @return true only if the user confirmed. */
    public static boolean show(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(LogoutController.class.getResource(FXML_PATH));
            Parent root = loader.load();
            LogoutController controller = loader.getController();

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Log out");
            stage.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            enableDrag(root, stage);

            stage.showAndWait();

            return controller.confirmed;
        } catch (Exception e) {
            // Any failure means "do not confirm"
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
        btnCancelLogout.setCancelButton(true);   // Esc = cancel
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleConfirmLogout() {
        confirmed = true;
        close();
    }

    @FXML
    private void handleCancelLogout() {
        confirmed = false;
        close();
    }

    private void close() {
        ((Stage) btnConfirmLogout.getScene().getWindow()).close();
    }
}
