package com.sassyscreation.sassyscreationgui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LoginController implements Initializable {

    // ---- fx:ids from LOGIN.fxml ----
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfPasswordVisible;
    @FXML private CheckBox cbRemember;
    @FXML private CheckBox cbShowPassword;
    @FXML private Hyperlink linkForgot;
    @FXML private Label lblError;
    @FXML private VBox lockoutBox;
    @FXML private Label lblLockoutCountdown;
    @FXML private ProgressBar pbLockout;
    @FXML private Button btnLogin;
    @FXML private Button btnExit;
    @FXML private Label lblAttempts;

    // ---- settings ----
    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCKOUT_SECONDS = 30;
    private static final String PREF_USERNAME = "savedUsername";

    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);

    private int failedAttempts = 0;
    private Timeline lockoutTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblError.setText("");

        // Keep the visible and hidden password fields in sync
        tfPasswordVisible.textProperty().bindBidirectional(pfPassword.textProperty());

        // Disable the Log in button until both fields are filled
        btnLogin.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> tfUsername.getText().trim().isEmpty() || pfPassword.getText().isEmpty(),
                        tfUsername.textProperty(), pfPassword.textProperty()));

        // Clear the error message once the user starts typing again
        tfUsername.textProperty().addListener((obs, o, n) -> lblError.setText(""));
        pfPassword.textProperty().addListener((obs, o, n) -> lblError.setText(""));

        // Restore remembered username
        String saved = prefs.get(PREF_USERNAME, "");
        if (!saved.isEmpty()) {
            tfUsername.setText(saved);
            cbRemember.setSelected(true);
            Platform.runLater(() -> pfPassword.requestFocus());
        }
    }

    // ---- handlers referenced in the FXML ----

    @FXML
    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText();

        if (authenticate(username, password)) {
            failedAttempts = 0;
            lblError.setText("");

            if (cbRemember.isSelected()) {
                prefs.put(PREF_USERNAME, username);
            } else {
                prefs.remove(PREF_USERNAME);
            }

            openMainScreen();
        } else {
            failedAttempts++;
            pfPassword.clear();

            if (failedAttempts >= MAX_ATTEMPTS) {
                startLockout();
            } else {
                int left = MAX_ATTEMPTS - failedAttempts;
                lblError.setText("Invalid username or password.");
                lblAttempts.setText(left + (left == 1 ? " attempt" : " attempts") + " remaining");
            }
        }
    }

    @FXML
    private void handleTogglePassword() {
        boolean show = cbShowPassword.isSelected();

        tfPasswordVisible.setVisible(show);
        tfPasswordVisible.setManaged(show);
        pfPassword.setVisible(!show);
        pfPassword.setManaged(!show);

        (show ? tfPasswordVisible : pfPassword).requestFocus();
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot password");
        alert.setHeaderText("Password reset");
        alert.setContentText("Please contact the system administrator to reset your password.");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    // ---- helpers ----

    /**
     * TEMPORARY: hard-coded credentials so you can test the screen.
     * Replace this with a real check (database query + hashed passwords).
     */
    private boolean authenticate(String username, String password) {
        return username.equals("admin") && password.equals("admin123");
    }

    private void openMainScreen() {
        // TODO: load your dashboard/main FXML here and swap the scene, e.g.
        // Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        // Stage stage = (Stage) btnLogin.getScene().getWindow();
        // stage.getScene().setRoot(root);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText("Login successful!");
        alert.showAndWait();
    }

    private void startLockout() {
        setInputsDisabled(true);
        lblError.setText("");
        lockoutBox.setVisible(true);
        lockoutBox.setManaged(true);
        lblAttempts.setText("");

        final int[] remaining = {LOCKOUT_SECONDS};
        updateLockoutUi(remaining[0]);

        lockoutTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remaining[0]--;
            if (remaining[0] <= 0) {
                endLockout();
            } else {
                updateLockoutUi(remaining[0]);
            }
        }));
        lockoutTimer.setCycleCount(LOCKOUT_SECONDS);
        lockoutTimer.play();
    }

    private void updateLockoutUi(int secondsLeft) {
        lblLockoutCountdown.setText("Try again in " + secondsLeft + (secondsLeft == 1 ? " second" : " seconds"));
        pbLockout.setProgress((double) secondsLeft / LOCKOUT_SECONDS);
    }

    private void endLockout() {
        if (lockoutTimer != null) {
            lockoutTimer.stop();
        }
        failedAttempts = 0;
        lockoutBox.setVisible(false);
        lockoutBox.setManaged(false);
        lblAttempts.setText(MAX_ATTEMPTS + " attempts allowed before a " + LOCKOUT_SECONDS + "-second lockout");
        setInputsDisabled(false);
        tfUsername.requestFocus();
    }

    private void setInputsDisabled(boolean disabled) {
        tfUsername.setDisable(disabled);
        pfPassword.setDisable(disabled);
        tfPasswordVisible.setDisable(disabled);
        cbRemember.setDisable(disabled);
        cbShowPassword.setDisable(disabled);
        // btnLogin is already bound to the text fields; clearing the password keeps it disabled during lockout
    }
}