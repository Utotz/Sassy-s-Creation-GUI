package com.sassyscreation.sassyscreationgui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class AboutController {

    private static final String APP_VERSION = "1.0.0";
    private static final double CORNER_RADIUS = 24; // keep in sync with .dialog-root in styles.css

    @FXML
    private StackPane root; // fx:id="root" on the root StackPane in the About FXML

    @FXML
    private Label lblVersion;

    @FXML
    private Button btnCloseAbout;

    // for dragging the undecorated window
    private double dragOffsetX;
    private double dragOffsetY;

    @FXML
    private void initialize() {
        lblVersion.setText("Version " + APP_VERSION);

        // Round the corners and clip the petal decorations to the same shape
        Rectangle clip = new Rectangle();
        clip.setArcWidth(CORNER_RADIUS * 2);
        clip.setArcHeight(CORNER_RADIUS * 2);
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);

        // Drag the window by clicking anywhere on it
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

        // Esc closes the dialog (scene is only available after the root is attached)
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        handleCloseAbout();
                    }
                });
            }
        });
    }

    @FXML
    private void handleCloseAbout() {
        Stage stage = (Stage) btnCloseAbout.getScene().getWindow();
        stage.close();
    }
}