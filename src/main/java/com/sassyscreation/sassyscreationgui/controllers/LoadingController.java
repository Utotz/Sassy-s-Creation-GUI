package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Controller for the "Please wait..." popup (LOADING.fxml).
 *
 * The FXML root must declare
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.LoadingController"
 *
 * Unlike the other dialogs this one does NOT block: open() returns right away
 * so you can do your work and then call close().
 *
 * Usage:
 *   LoadingController loading = LoadingController.open(window, "Saving order...");
 *   // ... do the work (ideally on a background thread / Task) ...
 *   loading.close();
 */
public class LoadingController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/LOADING.fxml";

    // piLoading is hidden in the FXML (kept only so references don't break)
    @FXML private ProgressBar piLoading;
    @FXML private Label lblLoading;

    private Stage dialogStage;

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    /** Opens the loading popup (non-blocking). Returns null if it could not be loaded. */
    public static LoadingController open(Window owner, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(LoadingController.class.getResource(FXML_PATH));
            Parent root = loader.load();
            LoadingController controller = loader.getController();

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            controller.dialogStage = stage;
            controller.setMessage(message);
            stage.show();
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LoadingController open(Window owner) {
        return open(owner, null);
    }

    public void setMessage(String message) {
        if (message != null && !message.isBlank()) {
            lblLoading.setText(message);
        }
    }

    /** Only useful if you un-hide piLoading in the FXML. 0..1, or -1 for indeterminate. */
    public void setProgress(double value) {
        piLoading.setProgress(value);
    }

    public void close() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
