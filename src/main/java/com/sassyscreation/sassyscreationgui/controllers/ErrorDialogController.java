package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

/**
 * Controller for the reusable error popup (ERRORDIALOG.fxml).
 *
 * The FXML root must declare
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.ErrorDialogController"
 *
 * Usage:
 *   ErrorDialogController.show(currentStage(), "Could not save", "The database is not reachable.");
 */
public class ErrorDialogController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/ERRORDIALOG.fxml";

    @FXML private Label lblErrorTitle;
    @FXML private Label lblErrorMessage;
    @FXML private Button btnOk;

    private Stage dialogStage;

    // ---------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------

    public static void show(Stage owner, String title, String message) {
        try {
            URL resource = ErrorDialogController.class.getResource(FXML_PATH);
            if (resource == null) {
                fallback(owner, title, message);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            ErrorDialogController controller = loader.getController();

            Stage dialog = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sassy's Creation");
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    dialog.close();
                }
            });
            dialog.setScene(scene);

            controller.dialogStage = dialog;
            controller.setContent(title, message);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            fallback(owner, title, message);
        }
    }

    public static void show(Stage owner, String message) {
        show(owner, null, message);
    }

    // ---------------------------------------------------------------
    // Instance
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        btnOk.setDefaultButton(true);
        lblErrorMessage.managedProperty().bind(lblErrorMessage.visibleProperty());
    }

    private void setContent(String title, String message) {
        if (title != null && !title.isBlank()) {
            lblErrorTitle.setText(title);
        }
        if (message != null && !message.isBlank()) {
            lblErrorMessage.setText(message);
        } else {
            lblErrorMessage.setVisible(false);
        }
    }

    @FXML
    private void handleOk() {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            ((Stage) btnOk.getScene().getWindow()).close();
        }
    }

    /** Plain JavaFX alert in case the FXML can't be loaded. */
    private static void fallback(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
