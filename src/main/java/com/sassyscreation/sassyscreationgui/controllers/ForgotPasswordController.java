package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for the "Forgot password?" screen (step 1 of 2: verify it's you).
 *
 * IMPORTANT: the root element of the FXML (the first <StackPane ...>) must declare this controller:
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.ForgotPasswordController"
 *
 * Flow:
 *   LOGIN.fxml -> "Forgot password?" -> this screen -> RESETPASSWORD.fxml (step 2)
 *
 * When the answer is correct, the username is stored in {@link #verifiedUsername}
 * so the step 2 controller can read it: ForgotPasswordController.verifiedUsername
 */
public class ForgotPasswordController implements Initializable {

    private static final String FXML_PATH           = "/com/sassyscreation/sassyscreationgui/";
    private static final String FXML_LOGIN          = "LOGIN.fxml";
    private static final String FXML_RESET_PASSWORD = "RESETPASSWORD.fxml";   // <-- CHANGE to your step 2 file name

    /** Set when the identity check passes; read it in the step 2 (reset password) controller. */
    public static String verifiedUsername = null;

    // ---- fields from the FXML ----
    @FXML private TextField tfFpUsername;
    @FXML private ComboBox<String> cbSecurityQuestion;
    @FXML private PasswordField pfSecurityAnswer;
    @FXML private Label lblFpError;
    @FXML private Button btnVerify;
    @FXML private Button btnBackToLogin;

    // =====================================================================
    // Init
    // =====================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        verifiedUsername = null;
        clearError();

        // Hide the error as soon as the user changes something
        tfFpUsername.textProperty().addListener((obs, o, n) -> clearError());
        cbSecurityQuestion.valueProperty().addListener((obs, o, n) -> clearError());
        pfSecurityAnswer.textProperty().addListener((obs, o, n) -> clearError());

        // Pressing Enter in the answer box = Verify
        pfSecurityAnswer.setOnAction(e -> handleVerifyIdentity());
    }

    // =====================================================================
    // Verify
    // =====================================================================
    @FXML
    private void handleVerifyIdentity() {
        String username = tfFpUsername.getText() == null ? "" : tfFpUsername.getText().trim();
        String question = cbSecurityQuestion.getValue();
        String answer = pfSecurityAnswer.getText() == null ? "" : pfSecurityAnswer.getText().trim();

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
        if (answer.isEmpty()) {
            showError("Please enter your answer.");
            pfSecurityAnswer.requestFocus();
            return;
        }

        if (!checkAnswer(username, question, answer)) {
            showError("The username, question or answer is incorrect.");
            pfSecurityAnswer.clear();
            pfSecurityAnswer.requestFocus();
            return;
        }

        verifiedUsername = username;
        switchScreen(FXML_RESET_PASSWORD, "Reset password");
    }

    /**
     * TEMPORARY: sample check so the screen works before the database is connected.
     * Sample account: username "admin", first question, answer "sassy".
     *
     * Replace with a real lookup: find the user, compare the selected question, and compare the
     * answer against the stored HASHED answer (never store or compare plain-text answers).
     */
    private boolean checkAnswer(String username, String question, String answer) {
        return username.equalsIgnoreCase("admin")
                && question.equals("What was the name of your first pet?")
                && answer.toLowerCase(Locale.ROOT).equals("sassy");
    }

    // =====================================================================
    // Navigation
    // =====================================================================
    @FXML
    private void handleBackToLogin() {
        verifiedUsername = null;
        switchScreen(FXML_LOGIN, "Login");
    }

    // =====================================================================
    // Helpers
    // =====================================================================
    private void showError(String message) {
        lblFpError.setText(message);
    }

    private void clearError() {
        lblFpError.setText("");
    }

    private Stage currentStage() {
        return (Stage) btnBackToLogin.getScene().getWindow();
    }

    /** Replaces the content of the current window with another FXML screen. */
    private void switchScreen(String fxmlName, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                ErrorDialogController.show(currentStage(), "Screen not found",
                        fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = currentStage();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - " + title);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialogController.show(currentStage(), "Could not open " + title, rootCause(e));
        }
    }

    private String rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.toString();
    }
}
