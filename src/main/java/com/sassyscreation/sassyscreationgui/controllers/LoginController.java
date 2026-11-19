package com.sassyscreation.sassyscreationgui.controllers;

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
    @FXML private Button btnLogin;
    @FXML private Button btnExit;
    @FXML private Label lblAttempts;

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
    }

    @FXML
    private void handleTogglePassword() {
        boolean show = cbShowPassword.isSelected();

        tfPasswordVisible.setVisible(show);
        tfPasswordVisible.setManaged(show);
        pfPassword.setVisible(!show);
        pfPassword.setManaged(!show);
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact the shop owner to reset your password.");
        alert.showAndWait();
    }

    @FXML
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
    }
}