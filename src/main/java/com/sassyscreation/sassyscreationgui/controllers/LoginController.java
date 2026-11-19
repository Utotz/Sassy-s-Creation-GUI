package com.sassyscreation.sassyscreationgui.controllers;

<<<<<<< HEAD
import com.sassyscreation.sassyscreationgui.util.Transitions;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.prefs.Preferences;

public class LoginController {

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_SECONDS = 30;

    @FXML private VBox lockoutBox;
    @FXML private Label lblLockoutCountdown;
    @FXML private ProgressBar pbLockout;
    @FXML private TextField tfUsername;
    @FXML private CheckBox cbRemember;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfPasswordVisible;
    @FXML private CheckBox cbShowPassword;
    @FXML private Hyperlink linkForgot;
    @FXML private Label lblError;
=======
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    @FXML private Button btnLogin;
    @FXML private Button btnExit;
    @FXML private Label lblAttempts;

<<<<<<< HEAD
    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
    private int attempts = 0;
    private int secondsLeft = 0;
    private Timeline lockTimer;

    @FXML
    public void initialize() {
        // load the saved username
        String saved = prefs.get("username", "");
        if (!saved.isEmpty()) {
            tfUsername.setText(saved);
            cbRemember.setSelected(true);
        }

        // keep both password fields the same
        tfPasswordVisible.textProperty().bindBidirectional(pfPassword.textProperty());

        // login button only works when both fields have text
        tfUsername.textProperty().addListener((obs, oldVal, newVal) -> updateLoginButton());
        pfPassword.textProperty().addListener((obs, oldVal, newVal) -> updateLoginButton());
        updateLoginButton();
=======
    // ---- settings ----
    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }

    @FXML
    private void handleTogglePassword() {
        boolean show = cbShowPassword.isSelected();

        tfPasswordVisible.setVisible(show);
        tfPasswordVisible.setManaged(show);
        pfPassword.setVisible(!show);
        pfPassword.setManaged(!show);
<<<<<<< HEAD
=======

        (show ? tfPasswordVisible : pfPassword).requestFocus();
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot password");
<<<<<<< HEAD
        alert.setHeaderText(null);
        alert.setContentText("Please contact the shop owner to reset your password.");
=======
        alert.setHeaderText("Password reset");
        alert.setContentText("Please contact the system administrator to reset your password.");
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
        alert.showAndWait();
    }

    @FXML
<<<<<<< HEAD
    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText();

        if (checkLogin(username, password)) {
            saveUsername(username);
            openDashboard();
            return;
        }

        attempts++;
        pfPassword.clear();

        if (attempts >= MAX_ATTEMPTS) {
            startLockout();
        } else {
            int left = MAX_ATTEMPTS - attempts;
            lblError.setText("Wrong username or password. " + left + " attempt(s) left.");
            Transitions.fadeIn(lblError);
            Transitions.shake(btnLogin.getParent());
        }
    }

    @FXML
    private void handleExit() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/sassyscreation/sassyscreationgui/EXITCONFIRM.fxml"));
            Parent root = loader.load();
            Transitions.popIn(root);

            Stage dialog = new Stage();
            dialog.initOwner(btnExit.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: replace this with a real check against the database
    private boolean checkLogin(String username, String password) {
        return username.equals("admin") && password.equals("admin123");
    }

    private void saveUsername(String username) {
        if (cbRemember.isSelected()) {
            prefs.put("username", username);
        } else {
            prefs.remove("username");
        }
    }

    private void updateLoginButton() {
        boolean empty = tfUsername.getText().trim().isEmpty()
                || pfPassword.getText().isEmpty();
        btnLogin.setDisable(empty || secondsLeft > 0);
    }

    private void startLockout() {
        secondsLeft = LOCK_SECONDS;
        lblError.setText("");
        setFormDisabled(true);

        lockoutBox.setVisible(true);
        lockoutBox.setManaged(true);
        Transitions.fadeIn(lockoutBox);
        updateCountdown();

        lockTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            updateCountdown();

            if (secondsLeft <= 0) {
                endLockout();
            }
        }));
        lockTimer.setCycleCount(LOCK_SECONDS);
        lockTimer.play();
    }

    private void endLockout() {
        attempts = 0;
        secondsLeft = 0;

        lockoutBox.setVisible(false);
        lockoutBox.setManaged(false);
        setFormDisabled(false);
        updateLoginButton();
    }

    private void updateCountdown() {
        lblLockoutCountdown.setText("Try again in " + secondsLeft + " seconds");
        pbLockout.setProgress((double) secondsLeft / LOCK_SECONDS);
    }

    private void setFormDisabled(boolean disabled) {
        tfUsername.setDisable(disabled);
        pfPassword.setDisable(disabled);
        tfPasswordVisible.setDisable(disabled);
        cbShowPassword.setDisable(disabled);
        cbRemember.setDisable(disabled);
        linkForgot.setDisable(disabled);
        btnLogin.setDisable(disabled);
    }

    private void openDashboard() {
        // fade out the login, then show the dashboard
        Transitions.fadeOut(btnLogin.getScene().getRoot(), () -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/sassyscreation/sassyscreationgui/DASHBOARD.fxml"));
                Parent root = loader.load();
                Transitions.fadeIn(root);

                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setTitle("Sassy's Creation");
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
            } catch (Exception e) {
                // Exception (not just IOException) so a failed load never leaves a blank window
                btnLogin.getScene().getRoot().setOpacity(1);
                lblError.setText("Could not open the dashboard.");
                Transitions.fadeIn(lblError);
                e.printStackTrace();
            }
        });
=======
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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    }
}