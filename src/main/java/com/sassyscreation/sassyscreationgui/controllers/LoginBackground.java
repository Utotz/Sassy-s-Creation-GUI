package com.sassyscreation.sassyscreationgui.controllers;

import javafx.scene.layout.StackPane;

import java.net.URL;

/** Applies the same flower background used by the login screen. */
public final class LoginBackground {

    private LoginBackground() {}

    public static void apply(StackPane root) {
        URL bgUrl = LoginBackground.class.getResource("/drawables/login-bg.jpg");
        if (bgUrl != null) {
            root.setStyle("-fx-background-image: url('" + bgUrl.toExternalForm() + "');"
                    + "-fx-background-size: cover;"
                    + "-fx-background-position: center center;"
                    + "-fx-background-repeat: no-repeat;");
        } else {
            System.out.println("login-bg.jpg not found in resources/drawables");
        }
    }
}