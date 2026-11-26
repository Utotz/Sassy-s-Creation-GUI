package com.sassyscreation.sassyscreationgui.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Controller for the "Change password" dialog (CHANGEPASSWORD.fxml).
 * Attached through fx:controller in the FXML, so any controller can open it
 * with a plain FXMLLoader (see openModal / openDialog).
 */
public class ChangePasswordController {

    private static final int MIN_LENGTH = 8;

    private final EventHandler<KeyEvent> escHandler = e -> {
        if (e.getCode() == KeyCode.ESCAPE) handleCancelPassword();
    };

    @FXML private StackPane root;
    @FXML private PasswordField pfCurrentPassword;
    @FXML private PasswordField pfChangeNew;
    @FXML private PasswordField pfChangeConfirm;
    @FXML private Label lblChangeError;
    @FXML private Button btnCancelPassword;
    @FXML private Button btnSavePassword;

    private double dragX;
    private double dragY;

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

        setupWindow();
    }

    // Makes the window show only the card (no title bar, no white background)
    private void setupWindow() {
        // Drag the window
        root.setOnMousePressed(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            dragX = e.getScreenX() - stage.getX();
            dragY = e.getScreenY() - stage.getY();
        });
        root.setOnMouseDragged(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(e.getScreenX() - dragX);
            stage.setY(e.getScreenY() - dragY);
        });

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.removeEventHandler(KeyEvent.KEY_PRESSED, escHandler);
            }
            if (newScene == null) return;

            newScene.setFill(Color.TRANSPARENT);
            newScene.addEventHandler(KeyEvent.KEY_PRESSED, escHandler);

            // The stage style can only be set before the window is shown
            makeTransparent(newScene.getWindow());
            newScene.windowProperty().addListener((o, oldWin, win) -> makeTransparent(win));
        });
    }

    private void makeTransparent(Window window) {
        if (window instanceof Stage stage && !stage.isShowing()) {
            try {
                stage.initStyle(StageStyle.TRANSPARENT);
            } catch (IllegalStateException ex) {
                // already shown, keep the current style
            }
            // Set the fill again when shown, in case the opener changed it
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
                stage.getScene().setFill(Color.TRANSPARENT);
                root.setStyle("-fx-background-color: transparent; -fx-padding: 44;");
            });
        }
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