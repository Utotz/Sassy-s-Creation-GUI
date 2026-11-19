package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Controller for the "Password changed" success screen.
 * Its only job is to send the user back to the login screen.
 */
public class PasswordChangedController {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    @FXML private Button btnDoneLogin;

    @FXML
    private void handleBackToLogin() {
        try {
            URL resource = getClass().getResource(FXML_PATH + "LOGIN.fxml");
            if (resource == null) {
                showError("Screen not found", "LOGIN.fxml could not be found in the resources folder.");
                return;
            }

            Parent root = new FXMLLoader(resource).load();

            Stage stage = (Stage) btnDoneLogin.getScene().getWindow();
            stage.setMaximized(false);
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open the login screen", e.getMessage());
        }
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "Unknown error. Check the console for details." : message);
        alert.showAndWait();
    }
}
