package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;

/**
 * Controller for the "Change password" dialog.
 *
 * The FXML has no fx:controller attribute, so this controller is attached in code
 * by {@link #show(Window)}. Call that from the miChangePassword handler in
 * DashboardController, OrdersController, InventoryController, StandSaleController, etc.
 * (do NOT open this FXML with a plain openDialog(...) or the buttons will do nothing).
 */
public class ChangePasswordController {

    private static final String FXML_FOLDER = "/com/sassyscreation/sassyscreationgui/";

    // CHANGE THESE to match the actual file names in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String CHANGE_PASSWORD_FXML = "CHANGEPASSWORD.fxml";
    private static final String PASSWORD_CHANGED_FXML = "PASSWORDCHANGED.fxml";

    // ---- fields from the FXML ----
    @FXML private PasswordField pfCurrentPassword;
    @FXML private PasswordField pfChangeNew;
    @FXML private PasswordField pfChangeConfirm;
    @FXML private Label lblChangeError;
    @FXML private Button btnCancelPassword;
    @FXML private Button btnSavePassword;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /** Opens the dialog as a modal window on top of {@code owner}. */
    public static void show(Window owner) {
        try {
            URL resource = ChangePasswordController.class.getResource(FXML_FOLDER + CHANGE_PASSWORD_FXML);
            if (resource == null) {
                showAlert("Screen not found", CHANGE_PASSWORD_FXML + " could not be found in the resources folder.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(new ChangePasswordController());
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Change password");
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Could not open Change password", e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Button handlers (names match onAction in the FXML)
    // ---------------------------------------------------------------

    @FXML
    private void handleCancelPassword() {
        close();
    }

    @FXML
    private void handleSavePassword() {
        String current = pfCurrentPassword.getText();
        String newPass = pfChangeNew.getText();
        String confirm = pfChangeConfirm.getText();

        // Validation
        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        if (newPass.length() < 8 || !newPass.matches(".*\\d.*")) {
            showError("New password must be at least 8 characters and include a number.");
            return;
        }
        if (!newPass.equals(confirm)) {
            showError("New password and confirmation do not match.");
            return;
        }
        if (newPass.equals(current)) {
            showError("New password must be different from the current one.");
            return;
        }
        if (!verifyCurrentPassword(current)) {
            showError("Current password is incorrect.");
            return;
        }

        if (updatePassword(newPass)) {
            showPasswordChanged();
        } else {
            showError("Could not update the password. Please try again.");
        }
    }

    // ---------------------------------------------------------------
    // TODO: connect these two to your database / login logic
    // ---------------------------------------------------------------

    /** Return true if {@code password} matches the logged-in user's current password. */
    private boolean verifyCurrentPassword(String password) {
        // TODO: use the same check LoginController uses
        // (query your users table, compare hash, etc.)
        return true;
    }

    /** Save the new password (ideally hashed). Return true on success. */
    private boolean updatePassword(String newPassword) {
        // TODO: UPDATE users SET password = ? WHERE ...
        return true;
    }

    // ---------------------------------------------------------------
    // Success screen
    // ---------------------------------------------------------------

    /**
     * Closes this dialog and shows the full-window "Password changed" screen
     * in the main window that opened the dialog.
     */
    private void showPasswordChanged() {
        Stage dialog = (Stage) btnSavePassword.getScene().getWindow();
        Window owner = dialog.getOwner();

        try {
            URL resource = getClass().getResource(FXML_FOLDER + PASSWORD_CHANGED_FXML);
            if (resource == null) {
                // Fallback so the user always gets a confirmation
                dialog.close();
                showInfo("Password changed", "Your password was changed successfully.");
                return;
            }

            Parent root = new FXMLLoader(resource).load();
            dialog.close();

            if (owner instanceof Stage main) {
                main.setMaximized(false);
                main.getScene().setRoot(root);
                main.setTitle("Sassy's Creation - Password changed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dialog.close();
            showInfo("Password changed", "Your password was changed successfully.");
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void showError(String message) {
        lblChangeError.setText(message);
    }

    private void close() {
        ((Stage) btnCancelPassword.getScene().getWindow()).close();
    }

    private void showInfo(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Change password");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "Unknown error. Check the console for details." : message);
        alert.showAndWait();
    }
}