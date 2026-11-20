package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

public class PasswordChangedController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";
    private static final String FXML_LOGIN = "LOGIN.fxml";

    @FXML private StackPane rootPane;
    @FXML private Button btnDoneLogin;

    @FXML
    private void initialize() {
        LoginBackground.apply(rootPane);
    }

    @FXML
    private void handleBackToLogin() {
        try {
            URL resource = getClass().getResource(FXML_PATH + FXML_LOGIN);
            if (resource == null) {
                System.out.println(FXML_LOGIN + " was not found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = (Stage) btnDoneLogin.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}