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
 * Reusable "success" popup: Order saved, Product saved, Sale recorded, Password changed...
 *
 * Usage:
 *   SuccessDialogController.show(currentStage(), "Order saved", "The order was saved successfully.");
 */
public class SuccessDialogController {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    /** Change this if your FXML file has a different name. */
    private static final String FXML_NAME = "SUCCESSDIALOG.fxml";

    @FXML private Label lblSuccessTitle;
    @FXML private Label lblSuccessMessage;
    @FXML private Button btnOk;

    private Stage dialogStage;

    // =====================================================================
    //  Public API
    // =====================================================================

    /**
     * Shows the success dialog and waits until the user closes it.
     *
     * @param owner   the window that opens the dialog (can be null)
     * @param title   bold heading, e.g. "Order saved"
     * @param message description, e.g. "The order was saved successfully."
     */
    public static void show(Stage owner, String title, String message) {
        try {
            URL resource = SuccessDialogController.class.getResource(FXML_PATH + FXML_NAME);
            if (resource == null) {
                fallback(owner, title, message);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            SuccessDialogController controller = loader.getController();

            Stage dialog = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sassy's Creation");
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            // Enter is handled by the default button; Esc also closes
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

    /** Shortcut when the default message is fine. */
    public static void show(Stage owner, String title) {
        show(owner, title, null);
    }

    // =====================================================================
    //  Instance
    // =====================================================================

    @FXML
    private void initialize() {
        btnOk.setDefaultButton(true);
        // Hide the message label (and don't reserve space) when it is empty
        lblSuccessMessage.managedProperty().bind(lblSuccessMessage.visibleProperty());
    }

    private void setContent(String title, String message) {
        if (title != null && !title.isBlank()) {
            lblSuccessTitle.setText(title);
        }
        if (message != null && !message.isBlank()) {
            lblSuccessMessage.setText(message);
        } else {
            lblSuccessMessage.setVisible(false);
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

    /** Plain JavaFX alert in case the FXML can't be loaded, so the user still gets feedback. */
    private static void fallback(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle("Success");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
