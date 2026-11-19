package com.sassyscreation.sassyscreationgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SassyCreation extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SassyCreation.class.getResource("LOGIN.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); // uses the 1180x720 size set in Scene Builder
        stage.setTitle("Sassy's Creation - Login");
        stage.setScene(scene);
        stage.show();
    }
}
