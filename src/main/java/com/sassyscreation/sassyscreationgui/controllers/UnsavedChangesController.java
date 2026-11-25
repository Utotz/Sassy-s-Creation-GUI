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
 * Controller for the "Unsaved changes" dialog (UNSAVEDCHANGES.fxml).
 *
 * The FXML root must declare
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.UnsavedChangesController"
 *
 * Usage:
 *   boolean yes = UnsavedChangesController.show(btnSomething.getScene().getWindow());
 *   if (yes) {
 *       // btnDiscard was clicked
 *   }
 */
public class UnsavedChangesController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/UNSAVEDCHANGES.fxml";

    @FXML private Button btnDiscard;
    @FXML private Button btnKeepEditing;

    private boolean confirmed = false;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /** Shows the dialog and blocks until it is closed. @return true only if the user confirmed. */
    public static boolean show(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(UnsavedChangesController.class.getResource(FXML_PATH));
            Parent root = loader.load();
            UnsavedChangesController controller = loader.getController();

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Unsaved changes");
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
        btnKeepEditing.setCancelButton(true);   // Esc = cancel
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleDiscard() {
        confirmed = true;
        close();
    }

    @FXML
    private void handleKeepEditing() {
        confirmed = false;
        close();
    }

    private void close() {
        ((Stage) btnDiscard.getScene().getWindow()).close();
    }
}
