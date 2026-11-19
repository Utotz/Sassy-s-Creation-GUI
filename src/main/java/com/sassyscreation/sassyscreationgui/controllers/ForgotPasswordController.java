package com.sassyscreation.sassyscreationgui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

/**
 * Controller for "Forgot password?" step 1 of 2 (FORGOTPASSWORD.fxml).
 * Attached through fx:controller in the FXML.
 */
public class ForgotPasswordController {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // CHANGE THESE if your files have different names
    private static final String FXML_LOGIN = "LOGIN.fxml";
    private static final String FXML_RESET = "RESETPASSWORD.fxml";   // step 2 (not built yet)

    private static final int MAX_ATTEMPTS = 3;

    @FXML private TextField tfFpUsername;
    @FXML private ComboBox<String> cbSecurityQuestion;
    @FXML private PasswordField pfSecurityAnswer;
    @FXML private Label lblFpError;
    @FXML private Button btnVerify;
    @FXML private Button btnBackToLogin;

    private int failedAttempts = 0;

    @FXML
    private void initialize() {
        // Error label takes no space until there is a message
        lblFpError.managedProperty().bind(lblFpError.visibleProperty());
        hideError();

        // Clear the error as soon as the user edits anything
        tfFpUsername.textProperty().addListener((obs, o, n) -> hideError());
        cbSecurityQuestion.valueProperty().addListener((obs, o, n) -> hideError());
        pfSecurityAnswer.textProperty().addListener((obs, o, n) -> hideError());

        // Enter in the answer field submits
        pfSecurityAnswer.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleVerifyIdentity();
            }
        });

        Platform.runLater(() -> tfFpUsername.requestFocus());
    }

    // =====================================================================
    //  Verify
    // =====================================================================

    @FXML
    private void handleVerifyIdentity() {
        String username = tfFpUsername.getText() == null ? "" : tfFpUsername.getText().trim();
        String question = cbSecurityQuestion.getValue();
        String answer = pfSecurityAnswer.getText() == null ? "" : pfSecurityAnswer.getText();

        if (username.isEmpty()) {
            showError("Please enter your username.");
            tfFpUsername.requestFocus();
            return;
        }
        if (question == null) {
            showError("Please select your security question.");
            cbSecurityQuestion.requestFocus();
            return;
        }
        if (answer.trim().isEmpty()) {
            showError("Please enter your answer.");
            pfSecurityAnswer.requestFocus();
            return;
        }

        if (verifyIdentity(username, question, answer)) {
            failedAttempts = 0;
            goToResetStep(username);
        } else {
            failedAttempts++;
            pfSecurityAnswer.clear();

            if (failedAttempts >= MAX_ATTEMPTS) {
                // Too many wrong tries: send them back to the login screen
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.initOwner(btnVerify.getScene().getWindow());
                a.setTitle("Forgot password");
                a.setHeaderText("Too many failed attempts");
                a.setContentText("Please try again later or contact the system administrator.");
                a.showAndWait();
                handleBackToLogin();
            } else {
                // Same message for every failure, so it doesn't reveal whether the username exists
                showError("The details you entered don't match our records.");
                pfSecurityAnswer.requestFocus();
            }
        }
    }

    /**
     * TEMPORARY: hard-coded record so you can test the screen
     * (username "admin", first question, answer "buddy").
     * Replace with a database lookup: load the user's saved question and HASHED answer,
     * then compare the hash of the normalized answer.
     */
    private boolean verifyIdentity(String username, String question, String answer) {
        boolean userOk = safeEquals(username, "admin");
        boolean questionOk = safeEquals(question, "What was the name of your first pet?");
        boolean answerOk = safeEquals(normalize(answer), "buddy");
        return userOk && questionOk && answerOk;   // no short-circuit, all are always compared
    }

    /** Answers are compared ignoring case and surrounding spaces. */
    private String normalize(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }

    /** Constant-time string comparison. */
    private boolean safeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }

    // =====================================================================
    //  Navigation
    // =====================================================================

    private void goToResetStep(String username) {
        URL resource = getClass().getResource(FXML_PATH + FXML_RESET);
        if (resource == null) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.initOwner(btnVerify.getScene().getWindow());
            a.setTitle("Forgot password");
            a.setHeaderText("Identity verified");
            a.setContentText("Step 2 (choose a new password) isn't built yet:\n"
                    + FXML_RESET + " was not found.");
            a.showAndWait();
            return;
        }
        // TODO: pass `username` to the step 2 controller so it knows whose password to change
        swapRoot(FXML_RESET, "Reset password");
    }

    @FXML
    private void handleBackToLogin() {
        swapRoot(FXML_LOGIN, "Login");
    }

    private void swapRoot(String fxml, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxml);
            if (resource == null) {
                showError(fxml + " was not found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = (Stage) btnBackToLogin.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - " + title);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + title + ".");
        }
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    private void showError(String message) {
        lblFpError.setText(message);
        lblFpError.setVisible(true);
    }

    private void hideError() {
        lblFpError.setText("");
        lblFpError.setVisible(false);
    }
}