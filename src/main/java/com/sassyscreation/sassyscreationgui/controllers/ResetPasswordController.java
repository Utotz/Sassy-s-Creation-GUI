package com.sassyscreation.sassyscreationgui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Controller for "Forgot password?" step 2 of 2 (RESETPASSWORD.fxml).
 */
public class ResetPasswordController {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // CHANGE THESE if your files have different names
    private static final String FXML_LOGIN = "LOGIN.fxml";
    private static final String FXML_DONE = "PASSWORDCHANGED.fxml";

    private static final int MIN_LENGTH = 8;

    @FXML private StackPane rootPane;
    @FXML private PasswordField pfNewPassword;
    @FXML private TextField tfNewPasswordVisible;
    @FXML private PasswordField pfConfirmPassword;
    @FXML private CheckBox cbShowNewPassword;
    @FXML private ProgressBar pbStrength;
    @FXML private Label lblStrength;
    @FXML private Label lblResetError;
    @FXML private Button btnSaveNewPassword;
    @FXML private Button btnCancelReset;

    /** Whose password is being changed (set by the forgot password step). */
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    private void initialize() {
        LoginBackground.apply(rootPane);

        // Error label takes no space until there is a message
        lblResetError.managedProperty().bind(lblResetError.visibleProperty());
        hideError();

        // Keep the visible and hidden password fields in sync
        tfNewPasswordVisible.textProperty().bindBidirectional(pfNewPassword.textProperty());

        pfNewPassword.textProperty().addListener((obs, o, n) -> {
            hideError();
            updateStrength(n);
        });
        pfConfirmPassword.textProperty().addListener((obs, o, n) -> hideError());

        // Enter in the confirm field submits
        pfConfirmPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSaveNewPassword();
            }
        });

        updateStrength("");
        Platform.runLater(() -> pfNewPassword.requestFocus());
    }

    // =====================================================================
    //  Handlers
    // =====================================================================

    @FXML
    private void handleToggleNewPassword() {
        boolean show = cbShowNewPassword.isSelected();

        tfNewPasswordVisible.setVisible(show);
        tfNewPasswordVisible.setManaged(show);
        pfNewPassword.setVisible(!show);
        pfNewPassword.setManaged(!show);

        TextField active = show ? tfNewPasswordVisible : pfNewPassword;
        active.requestFocus();
        active.positionCaret(active.getText().length());
    }

    @FXML
    private void handleSaveNewPassword() {
        String newPass = pfNewPassword.getText() == null ? "" : pfNewPassword.getText();
        String confirm = pfConfirmPassword.getText() == null ? "" : pfConfirmPassword.getText();

        if (newPass.isEmpty()) {
            showError("Please enter a new password.");
            pfNewPassword.requestFocus();
            return;
        }
        if (newPass.length() < MIN_LENGTH || !newPass.matches(".*\\d.*")) {
            showError("Password must be at least 8 characters and include a number.");
            pfNewPassword.requestFocus();
            return;
        }
        if (confirm.isEmpty()) {
            showError("Please confirm your new password.");
            pfConfirmPassword.requestFocus();
            return;
        }
        if (!newPass.equals(confirm)) {
            showError("The passwords do not match.");
            pfConfirmPassword.clear();
            pfConfirmPassword.requestFocus();
            return;
        }

        if (savePassword(username, newPass)) {
            swapRoot(FXML_DONE, "Password changed");
        } else {
            showError("Could not save the new password. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        swapRoot(FXML_LOGIN, "Login");
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    /**
     * TEMPORARY: does nothing yet.
     * Replace with a database update that stores a HASHED password
     * (for example BCrypt) for the given username.
     */
    private boolean savePassword(String username, String newPassword) {
        return true;
    }

    private void updateStrength(String password) {
        if (password == null || password.isEmpty()) {
            pbStrength.setProgress(0);
            pbStrength.setStyle("");
            lblStrength.setText("Strength: –");
            return;
        }

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[^A-Za-z0-9].*")) score++;

        String label;
        String color;
        if (score <= 2) {
            label = "Weak";
            color = "#C0392B";
        } else if (score == 3) {
            label = "Fair";
            color = "#E0A100";
        } else if (score == 4) {
            label = "Good";
            color = "#7BAE3A";
        } else {
            label = "Strong";
            color = "#2E8B3E";
        }

        pbStrength.setProgress(score / 5.0);
        pbStrength.setStyle("-fx-accent: " + color + ";");
        lblStrength.setText("Strength: " + label);
    }

    private void swapRoot(String fxml, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxml);
            if (resource == null) {
                showError(fxml + " was not found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = (Stage) btnCancelReset.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - " + title);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + title + ".");
        }
    }

    private void showError(String message) {
        lblResetError.setText(message);
        lblResetError.setVisible(true);
    }

    private void hideError() {
        lblResetError.setText("");
        lblResetError.setVisible(false);
    }
}