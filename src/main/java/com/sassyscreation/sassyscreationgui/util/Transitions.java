package com.sassyscreation.sassyscreationgui.util;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;

public class Transitions {

    private static final Duration FAST = Duration.millis(180);
    private static final Duration NORMAL = Duration.millis(300);
    private static final Duration SLIDE = Duration.millis(320);
    private static final double SLIDE_DISTANCE = 40;
    private static final String ANIM_KEY = "pageAnim";
    private static final String PAGE_PATH = "/com/sassyscreation/sassyscreationgui/";

    // ---------- full page navigation (replaces Navigator) ----------

    // loads a page first, then slides it in over the old one (never a white gap)
    public static void goTo(Node from, String fxml) {
        Scene scene = from.getScene();
        if (scene == null) {
            return;
        }

        // load first, so a failure leaves you on the current page with a visible error
        Parent newRoot;
        try {
            URL url = Transitions.class.getResource(PAGE_PATH + fxml);
            if (url == null) {
                throw new IllegalStateException("FXML not found: " + PAGE_PATH + fxml);
            }
            newRoot = FXMLLoader.load(url);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Could not open " + fxml + ":\n" + e);
            alert.show();
            return;
        }

        Parent oldRoot = scene.getRoot();

        // holder uses the same stylesheets so it never shows plain white
        StackPane holder = new StackPane();
        holder.getStylesheets().addAll(oldRoot.getStylesheets());
        holder.getStyleClass().add("root-pane");
        scene.setRoot(holder);
        holder.getChildren().addAll(oldRoot, newRoot);

        newRoot.setOpacity(0);
        newRoot.setTranslateX(SLIDE_DISTANCE);

        FadeTransition fade = new FadeTransition(SLIDE, newRoot);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition move = new TranslateTransition(SLIDE, newRoot);
        move.setToX(0);
        move.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition anim = new ParallelTransition(fade, move);
        anim.setOnFinished(e -> {
            holder.getChildren().clear();
            newRoot.setOpacity(1);
            newRoot.setTranslateX(0);
            scene.setRoot(newRoot);
        });
        anim.play();
    }

    // ---------- in-page navigation (only if you use a content host StackPane) ----------

    // call once when setting up the content host, stops pages drawing outside it
    public static void clipToBounds(StackPane host) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(host.widthProperty());
        clip.heightProperty().bind(host.heightProperty());
        host.setClip(clip);
    }

    // new page slides in and fades in while the old one slides out and fades out
    public static void slide(StackPane host, Node next) {
        // stop an unfinished transition (fast clicking between tabs)
        Object running = host.getProperties().remove(ANIM_KEY);
        if (running instanceof Animation a) {
            a.stop();
        }

        // after an interrupted transition, keep only the newest page
        ObservableList<Node> kids = host.getChildren();
        while (kids.size() > 1) {
            reset(kids.remove(0));
        }

        Node current = kids.isEmpty() ? null : kids.get(0);

        if (current == next) {
            reset(next);
            return;
        }

        reset(current);

        // new page starts slightly to the right and invisible
        next.setOpacity(0);
        next.setTranslateX(SLIDE_DISTANCE);
        kids.add(next);

        FadeTransition fadeIn = new FadeTransition(SLIDE, next);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slideIn = new TranslateTransition(SLIDE, next);
        slideIn.setToX(0);
        slideIn.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition all = new ParallelTransition(fadeIn, slideIn);

        if (current != null) {
            final Node old = current;

            FadeTransition fadeOut = new FadeTransition(SLIDE, old);
            fadeOut.setToValue(0);
            fadeOut.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slideOut = new TranslateTransition(SLIDE, old);
            slideOut.setToX(-SLIDE_DISTANCE);
            slideOut.setInterpolator(Interpolator.EASE_OUT);

            all.getChildren().addAll(fadeOut, slideOut);

            all.setOnFinished(e -> {
                host.getChildren().remove(old);
                reset(old); // so it looks normal when shown again
                host.getProperties().remove(ANIM_KEY);
            });
        } else {
            all.setOnFinished(e -> host.getProperties().remove(ANIM_KEY));
        }

        host.getProperties().put(ANIM_KEY, all);
        all.play();
    }

    private static void reset(Node n) {
        if (n == null) return;
        n.setOpacity(1);
        n.setTranslateX(0);
    }

    // ---------- general effects ----------

    // fade a node in from invisible
    public static void fadeIn(Node node) {
        node.setOpacity(0);

        FadeTransition fade = new FadeTransition(NORMAL, node);
        fade.setToValue(1);
        fade.play();
    }

    // fade a node out, then run the next step
    public static void fadeOut(Node node, Runnable next) {
        FadeTransition fade = new FadeTransition(FAST, node);
        fade.setToValue(0);
        fade.setOnFinished(e -> next.run());
        fade.play();
    }

    // fade and grow a little, used for dialogs
    public static void popIn(Node node) {
        node.setOpacity(0);
        node.setScaleX(0.92);
        node.setScaleY(0.92);

        FadeTransition fade = new FadeTransition(FAST, node);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(FAST, node);
        scale.setToX(1);
        scale.setToY(1);

        new ParallelTransition(fade, scale).play();
    }

    // quick left and right shake for wrong input
    public static void shake(Node node) {
        TranslateTransition move = new TranslateTransition(Duration.millis(50), node);
        move.setByX(10);
        move.setCycleCount(6);
        move.setAutoReverse(true);
        move.setOnFinished(e -> node.setTranslateX(0));
        move.play();
    }
}