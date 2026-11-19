package com.sassyscreation.sassyscreationgui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCKOUT_SECONDS = 30;
    private static final String PREF_USERNAME = "savedUsername";

    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);

    private final BooleanProperty lockedOut = new SimpleBooleanProperty(false);
    private int failedAttempts = 0;
    private Timeline lockoutTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblError.setText("");

        // Keep the visible and hidden password fields in sync
        tfPasswordVisible.textProperty().bindBidirectional(pfPassword.textProperty());

        // Log in is disabled while a field is empty OR while locked out
        btnLogin.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> lockedOut.get()
                                || tfUsername.getText().trim().isEmpty()
                                || pfPassword.getText().isEmpty(),
                        tfUsername.textProperty(), pfPassword.textProperty(), lockedOut));

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
        if (lockedOut.get()) {
            return; // ignore Enter key / clicks during lockout
        }

        String username = tfUsername.getText().trim();
        String password = pfPassword.getText();

        if (authenticate(username, password)) {
            failedAttempts = 0;
            lblError.setText("");
            lblAttempts.setText(MAX_ATTEMPTS + " attempts allowed before a " + LOCKOUT_SECONDS + "-second lockout");

            if (cbRemember.isSelected()) {
                prefs.put(PREF_USERNAME, username);
            } else {
                prefs.remove(PREF_USERNAME);
            }

            openMainScreen();
        } else {
            failedAttempts++;
            pfPassword.clear();   // do this BEFORE setting the error (clearing resets lblError)

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

        TextField active = show ? tfPasswordVisible : pfPassword;
        active.requestFocus();
        active.positionCaret(active.getText().length()); // keep caret at the end
    }

    @FXML
    private void handleForgotPassword() {
        try {
            URL resource = getClass().getResource(FXML_PATH + "FORGOTPASSWORD.fxml");
            if (resource == null) {
                lblError.setText("FORGOTPASSWORD.fxml was not found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - Forgot password");
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Could not open the forgot password screen.");
        }
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
        boolean userOk = safeEquals(username, "admin");
        boolean passOk = safeEquals(password, "admin123");
        return userOk && passOk;   // no short-circuit, so both are always compared
    }

    /** Constant-time string comparison. */
    private boolean safeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }

    /** Loads DASHBOARD.fxml and swaps it into the current window. */
    private void openMainScreen() {
        try {
            URL resource = getClass().getResource(FXML_PATH + "DASHBOARD.fxml");
            if (resource == null) {
                lblError.setText("DASHBOARD.fxml was not found in the resources folder.");
                return;
            }

            Parent root = new FXMLLoader(resource).load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sassy's Creation - Dashboard");
            stage.setMaximized(true);   // remove this line if you don't want it maximized
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Could not open the dashboard.");
        }
    }

    private void startLockout() {
        if (lockoutTimer != null) {
            lockoutTimer.stop();
        }

        lockedOut.set(true);
        setInputsDisabled(true);
        lblError.setText("");
        lblAttempts.setText("");
        lockoutBox.setVisible(true);
        lockoutBox.setManaged(true);

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
        lockedOut.set(false);
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
        linkForgot.setDisable(disabled);
    }
}