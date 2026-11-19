package com.sassyscreation.sassyscreationgui.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class AboutController {

    private static final String APP_VERSION = "1.0.0";
    private static final double CORNER_RADIUS = 24; // keep in sync with .dialog-root in styles.css

    private final EventHandler<KeyEvent> escHandler = e -> {
        if (e.getCode() == KeyCode.ESCAPE) handleCloseAbout();
    };

    @FXML private StackPane root;
    @FXML private Label lblVersion;
    @FXML private Button btnCloseAbout;

    private double dragOffsetX;
    private double dragOffsetY;

    @FXML
    private void initialize() {
        lblVersion.setText("Version " + APP_VERSION);

        // Rounded clip for the root and its decorations
        Rectangle clip = new Rectangle();
        clip.setArcWidth(CORNER_RADIUS * 2);
        clip.setArcHeight(CORNER_RADIUS * 2);
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);

        // Drag the undecorated window
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

        // Esc closes the dialog (handles scene being set now or later)
        if (root.getScene() != null) {
            root.getScene().addEventHandler(KeyEvent.KEY_PRESSED, escHandler);
        }
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.removeEventHandler(KeyEvent.KEY_PRESSED, escHandler);
            }
            if (newScene != null) {
                newScene.addEventHandler(KeyEvent.KEY_PRESSED, escHandler);
            }
        });
    }

    @FXML
    private void handleCloseAbout() {
        Stage stage = (Stage) btnCloseAbout.getScene().getWindow();
        stage.close();
    }
}