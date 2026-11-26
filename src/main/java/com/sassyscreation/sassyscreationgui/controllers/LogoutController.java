package com.sassyscreation.sassyscreationgui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * "Log out" confirmation dialog (LOGOUT.fxml). Same look as About, blurs the page behind it.
 *
 *   if (LogoutController.show(window)) { ...log out... }
 */
public class LogoutController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/LOGOUT.fxml";
    private static final double BLUR_RADIUS = 14;
    private static final double BLUR_FADE_MS = 220;

    @FXML private StackPane root;
    @FXML private Button btnConfirmLogout;
    @FXML private Button btnCancelLogout;

    private boolean confirmed = false;
    private double dragOffsetX;
    private double dragOffsetY;

    private final EventHandler<KeyEvent> escHandler = e -> {
        if (e.getCode() == KeyCode.ESCAPE) handleCancelLogout();
    };

    /** Shows the dialog (with blurred background) and blocks. @return true only if confirmed. */
    public static boolean show(Window owner) {
        Node ownerRoot = (owner != null && owner.getScene() != null) ? owner.getScene().getRoot() : null;
        Effect previousEffect = ownerRoot != null ? ownerRoot.getEffect() : null;
        try {
            FXMLLoader loader = new FXMLLoader(LogoutController.class.getResource(FXML_PATH));
            Parent root = loader.load();
            LogoutController controller = loader.getController();

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Sassy's Creation - Log out");
            stage.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            if (ownerRoot != null) {
                GaussianBlur blur = new GaussianBlur(0);
                ownerRoot.setEffect(blur);
                new Timeline(new KeyFrame(Duration.millis(BLUR_FADE_MS),
                        new KeyValue(blur.radiusProperty(), BLUR_RADIUS, Interpolator.EASE_OUT))).play();
            }

            stage.showAndWait();
            return controller.confirmed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ownerRoot != null) {
                ownerRoot.setEffect(previousEffect);   // remove blur when dialog closes
            }
        }
    }

    @FXML
    private void initialize() {
        // drag the undecorated window
        root.setOnMousePressed(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            dragOffsetX = e.getScreenX() - stage.getX();
            dragOffsetY = e.getScreenY() - stage.getY();
        });
        root.setOnMouseDragged(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });

        // Esc = cancel
        if (root.getScene() != null) {
            root.getScene().addEventHandler(KeyEvent.KEY_PRESSED, escHandler);
        }
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) oldScene.removeEventHandler(KeyEvent.KEY_PRESSED, escHandler);
            if (newScene != null) newScene.addEventHandler(KeyEvent.KEY_PRESSED, escHandler);
        });
    }

    @FXML
    private void handleConfirmLogout() {
        confirmed = true;
        close();
    }

    @FXML
    private void handleCancelLogout() {
        confirmed = false;
        close();
    }

    private void close() {
        ((Stage) btnConfirmLogout.getScene().getWindow()).close();
    }
}