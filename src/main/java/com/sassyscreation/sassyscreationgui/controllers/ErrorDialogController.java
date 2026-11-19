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
 * Controller for the reusable error dialog (red X icon + title + message + OK).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.ErrorDialogController"
 *
 * Use it for: empty fields, invalid price or quantity, not enough stock,
 * wrong username or password, product already exists, and so on.
 *
 * Usage:
 *
 *   ErrorDialogController.show(window, "Not enough stock",
 *           "Only 3 of \"Rose Keychain\" left in stock.");
 *
 *   ErrorDialogController.show(window, "Please fill in all required fields.");
 */
public class ErrorDialogController {

    // CHANGE THIS to match the actual file name of your error-dialog FXML
    // (it must sit in src/main/resources/com/sassyscreation/sassyscreationgui/)
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/ERRORDIALOG.fxml";

    private static final String DEFAULT_TITLE = "Please check your input";

    // ---- fields from the FXML ----
    @FXML private Label lblErrorTitle;
    @FXML private Label lblErrorMessage;
    @FXML private Button btnOk;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /** Shows the dialog with a title and a message, and blocks until OK is pressed. */
    public static void show(Window owner, String title, String message) {
        try {
            URL url = ErrorDialogController.class.getResource(FXML_PATH);
            if (url == null) {
                throw new IOException("FXML not found: " + FXML_PATH);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            ErrorDialogController controller = loader.getController();
            controller.setMessage(title, message);

            Stage stage = new Stage();
            stage.setTitle("Error");
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    /** Shows the dialog with the default title and the given message. */
    public static void show(Window owner, String message) {
        show(owner, DEFAULT_TITLE, message);
    }

    // ---------------------------------------------------------------
    // Filling in the text
    // ---------------------------------------------------------------

    public void setMessage(String title, String message) {
        lblErrorTitle.setText(title == null || title.isBlank() ? DEFAULT_TITLE : title);
        lblErrorMessage.setText(message == null || message.isBlank()
                ? "Something went wrong. Please try again." : message);
    }

    // ---------------------------------------------------------------
    // Button handler (name matches onAction in the FXML)
    // ---------------------------------------------------------------

    @FXML
    private void handleOk() {
        ((Stage) btnOk.getScene().getWindow()).close();
    }
}