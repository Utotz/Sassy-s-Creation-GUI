package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Controller for the "Change password" dialog.
 *
 * The FXML has no fx:controller attribute, so this controller is attached in code
 * by {@link #show(Window)}. Call that from the miChangePassword handler in
 * DashboardController, OrdersController, InventoryController, StandSaleController, etc.
 */
public class ChangePasswordController {

    // CHANGE THIS to match the actual file name of your change-password FXML
    // (it must sit in src/main/resources/com/sassyscreation/sassyscreationgui/)
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/change-password.fxml";

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
            FXMLLoader loader = new FXMLLoader(ChangePasswordController.class.getResource(FXML_PATH));
            loader.setController(new ChangePasswordController());
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Change password");
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
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
            close();
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
    // Helpers
    // ---------------------------------------------------------------

    private void showError(String message) {
        lblChangeError.setText(message);
    }

    private void close() {
        ((Stage) btnCancelPassword.getScene().getWindow()).close();
    }
}
