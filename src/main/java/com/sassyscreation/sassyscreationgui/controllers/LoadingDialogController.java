package com.sassyscreation.sassyscreationgui.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Controller for the "Please wait..." loading dialog (falling flowers animation).
 *
 * The FXML root declares
 *   fx:controller="com.sassyscreation.sassyscreationgui.controllers.LoadingDialogController"
 *
 * The animated GIF is loaded from resources/drawables/loading_flowers.gif
 * (the FXML uses @../../../drawables/loading_flowers.gif, relative to the FXML folder).
 *
 * Usage 1 - run slow work in the background while the dialog is showing (recommended):
 *
 *   Task<Boolean> task = new Task<>() {
 *       @Override protected Boolean call() {
 *           return loginService.check(username, password);   // slow work, NOT on the UI thread
 *       }
 *   };
 *   LoadingDialogController.run(window, "Logging in...", task,
 *           ok -> { if (ok) openDashboard(); else showWrongPassword(); },
 *           err -> ErrorDialogController.show(window, "Login failed", err.getMessage()));
 *
 * Usage 2 - open and close it yourself:
 *
 *   LoadingDialogController loading = LoadingDialogController.show(window, "Saving...");
 *   // ... later ...
 *   loading.close();
 */
public class LoadingDialogController {

    // CHANGE THIS to match the actual file name of your loading-dialog FXML
    // (it must sit in src/main/resources/com/sassyscreation/sassyscreationgui/)
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/LOADING.fxml";

    /** The dialog stays at least this long so the animation never just flashes on screen. */
    private static final double MIN_SHOW_MILLIS = 700;

    // ---- fields from the FXML ----
    @FXML private ProgressBar piLoading;   // hidden, kept so old references still work
    @FXML private Label lblLoading;

    private Stage stage;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /** Shows the dialog with "Please wait..." and returns immediately (it does not block). */
    public static LoadingDialogController show(Window owner) {
        return show(owner, "Please wait...");
    }

    /**
     * Shows the dialog and returns immediately (it does not block).
     * Always call close() on the returned object when the work is done.
     * If the FXML cannot be loaded, an error dialog is shown and a harmless "empty" object is returned,
     * so your code keeps working.
     */
    public static LoadingDialogController show(Window owner, String message) {
        try {
            URL url = LoadingDialogController.class.getResource(FXML_PATH);
            if (url == null) {
                throw new IOException("FXML not found: " + FXML_PATH);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT);
            LoadingDialogController controller = loader.getController();
            controller.setMessage(message);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            // The user cannot dismiss it (for example with Alt+F4); only close() hides it
            stage.setOnCloseRequest(e -> e.consume());
            controller.stage = stage;
            stage.show();
            return controller;
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            ErrorDialogController.show(owner, "Could not open loading screen", String.valueOf(e.getMessage()));
            return new LoadingDialogController();   // no-op instance
        }
    }

    /**
     * Shows the loading dialog, runs the task on a background thread, then closes the dialog
     * and calls onSuccess (with the result) or onError (with the exception) on the UI thread.
     *
     * @param onSuccess may be null
     * @param onError   may be null
     */
    public static <T> void run(Window owner, String message, Task<T> task,
                               Consumer<T> onSuccess, Consumer<Throwable> onError) {
        LoadingDialogController dialog = show(owner, message);
        long start = System.nanoTime();

        task.setOnSucceeded(e -> finish(dialog, start, () -> {
            if (onSuccess != null) {
                onSuccess.accept(task.getValue());
            }
        }));
        task.setOnFailed(e -> finish(dialog, start, () -> {
            Throwable error = task.getException();
            if (onError != null) {
                onError.accept(error);
            } else if (error != null) {
                error.printStackTrace();
            }
        }));
        task.setOnCancelled(e -> finish(dialog, start, null));

        Thread worker = new Thread(task, "loading-task");
        worker.setDaemon(true);
        worker.start();
    }

    /** Closes the dialog (after the minimum display time), then runs "after" on the UI thread. */
    private static void finish(LoadingDialogController dialog, long startNanos, Runnable after) {
        double elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000.0;
        double remaining = MIN_SHOW_MILLIS - elapsedMillis;

        Runnable closeAndContinue = () -> {
            dialog.close();
            if (after != null) {
                after.run();
            }
        };

        if (remaining <= 0) {
            closeAndContinue.run();
        } else {
            PauseTransition wait = new PauseTransition(Duration.millis(remaining));
            wait.setOnFinished(e -> closeAndContinue.run());
            wait.play();
        }
    }

    // ---------------------------------------------------------------
    // Using the dialog
    // ---------------------------------------------------------------

    /** Changes the text under the animation. Safe to call from any thread. */
    public void setMessage(String message) {
        if (lblLoading == null) {
            return;
        }
        String text = message == null || message.isBlank() ? "Please wait..." : message;
        if (Platform.isFxApplicationThread()) {
            lblLoading.setText(text);
        } else {
            Platform.runLater(() -> lblLoading.setText(text));
        }
    }

    /** Closes the dialog. Safe to call from any thread, and more than once. */
    public void close() {
        if (stage == null) {
            return;
        }
        Stage s = stage;
        stage = null;
        if (Platform.isFxApplicationThread()) {
            s.close();
        } else {
            Platform.runLater(s::close);
        }
    }
}
