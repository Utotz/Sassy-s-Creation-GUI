package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Controller for the "Change password" dialog (CHANGEPASSWORD.fxml).
 * Attached through fx:controller in the FXML, so any controller can open it
 * with a plain FXMLLoader (see openModal / openDialog).
 */
public class ChangePasswordController {

    private static final int MIN_LENGTH = 8;

    @FXML private PasswordField pfCurrentPassword;
    @FXML private PasswordField pfChangeNew;
    @FXML private PasswordField pfChangeConfirm;
    @FXML private Label lblChangeError;
    @FXML private Button btnCancelPassword;
    @FXML private Button btnSavePassword;

    @FXML
    private void initialize() {
        // Error label takes no space until there is something to show
        lblChangeError.managedProperty().bind(lblChangeError.visibleProperty());
        hideError();

        // Clear the error as soon as the user edits any field
        pfCurrentPassword.textProperty().addListener((obs, o, n) -> hideError());
        pfChangeNew.textProperty().addListener((obs, o, n) -> hideError());
        pfChangeConfirm.textProperty().addListener((obs, o, n) -> hideError());

        // Enter in the last field submits
        pfChangeConfirm.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSavePassword();
            }
        });
    }

    @FXML
    private void handleCancelPassword() {
        close();
    }

    @FXML
    private void handleSavePassword() {
        String current = pfCurrentPassword.getText() == null ? "" : pfCurrentPassword.getText();
        String newPass = pfChangeNew.getText() == null ? "" : pfChangeNew.getText();
        String confirm = pfChangeConfirm.getText() == null ? "" : pfChangeConfirm.getText();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        if (newPass.length() < MIN_LENGTH || !newPass.matches(".*\\d.*")) {
            showError("New password must be at least " + MIN_LENGTH + " characters and include a number.");
            pfChangeNew.requestFocus();
            return;
        }
        if (!newPass.equals(confirm)) {
            showError("New password and confirmation do not match.");
            pfChangeConfirm.requestFocus();
            return;
        }
        if (newPass.equals(current)) {
            showError("New password must be different from the current one.");
            pfChangeNew.requestFocus();
            return;
        }
        if (!verifyCurrentPassword(current)) {
            showError("Current password is incorrect.");
            pfCurrentPassword.requestFocus();
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
        // NOTE: returns true for now, so ANY current password is accepted until you implement this.
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
        lblChangeError.setVisible(true);
    }

    private void hideError() {
        lblChangeError.setText("");
        lblChangeError.setVisible(false);
    }

    private void close() {
        ((Stage) btnCancelPassword.getScene().getWindow()).close();
    }
}