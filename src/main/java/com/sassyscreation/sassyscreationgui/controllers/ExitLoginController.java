package com.sassyscreation.sassyscreationgui.controllers;

import com.sassyscreation.sassyscreationgui.util.Transitions;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ExitLoginController {

    @FXML private Button btnConfirmExit;
    @FXML private Button btnCancelExit;

    @FXML
    private void handleConfirmExit() {
        // close the whole app
        Platform.exit();
    }

    @FXML
    private void handleCancelExit() {
        // fade out, then close only the dialog
        Stage stage = (Stage) btnCancelExit.getScene().getWindow();
        Transitions.fadeOut(btnCancelExit.getScene().getRoot(), stage::close);
    }
}