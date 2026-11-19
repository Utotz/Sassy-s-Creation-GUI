package com.sassyscreation.sassyscreationgui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;

/**
 * Controller for the "Exit Sassy's Creation?" confirmation dialog.
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.ExitDialogController"
 *
 * Usage 1 - ask only (you close the app yourself):
 *
 *   if (ExitDialogController.show(window)) {
 *       Platform.exit();
 *   }
 *
 * Usage 2 - ask and exit in one call:
 *
 *   ExitDialogController.confirmAndExit(window);
 *
 * Usage 3 - ask when the user clicks the window's X button (in MainApp.start):
 *
 *   stage.setOnCloseRequest(e -> {
 *       if (!ExitDialogController.show(stage)) {
 *           e.consume();          // "Stay" -> keep the app open
 *       }
 *   });
 *
 * The screens also use it as the "Are you sure you want to log out?" confirmation.
 */
public class ExitDialogController {

    // CHANGE THIS to match the actual file name of your exit-dialog FXML
    // (it must sit in src/main/resources/com/sassyscreation/sassyscreationgui/)
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/EXITCONFIRM.fxml";

    // ---- fields from the FXML ----
    @FXML private Button btnCancelExit;
    @FXML private Button btnConfirmExit;

    private boolean confirmed = false;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @return true if the user clicked "Yes, exit", false if they clicked "Stay" or closed the dialog
     */
    public static boolean show(Window owner) {
        try {
            URL url = ExitDialogController.class.getResource(FXML_PATH);
            if (url == null) {
                throw new IOException("FXML not found: " + FXML_PATH);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            ExitDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Exit");
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();

            return controller.confirmed;
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            ErrorDialogController.show(owner, "Could not open dialog", String.valueOf(e.getMessage()));
            return false;
        }
    }

    /** Shows the dialog and closes the whole application if the user confirms. */
    public static void confirmAndExit(Window owner) {
        if (show(owner)) {
            // TODO: save / clear anything (session, open connections) before closing
            Platform.exit();
        }
    }

    // ---------------------------------------------------------------
    // Button handlers (names match onAction in the FXML)
    // ---------------------------------------------------------------

    @FXML
    private void handleConfirmExit() {
        confirmed = true;
        close();
    }

    @FXML
    private void handleCancelExit() {
        confirmed = false;
        close();
    }

    private void close() {
        ((Stage) btnCancelExit.getScene().getWindow()).close();
    }
}