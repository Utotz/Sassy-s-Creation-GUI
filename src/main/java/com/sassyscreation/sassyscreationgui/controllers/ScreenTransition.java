package com.sassyscreation.sassyscreationgui.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Smooth screen change: the old content fades out, then the new content
 * fades in and slides up a little. The sidebar stays in place.
 *
 * Use it instead of scene.setRoot(root):
 *     ScreenTransition.show(scene, root);
 */
public final class ScreenTransition {

    private static final double OUT_MS = 120;
    private static final double IN_MS = 240;
    private static final double SLIDE = 14;

    /** true while an animation is playing (ignores extra clicks) */
    private static boolean running = false;

    private ScreenTransition() {
    }

    public static void show(Scene scene, Parent newRoot) {
        if (running) {
            return;
        }
        Parent oldRoot = scene.getRoot();

        // if both screens have the sidebar, only the content animates
        boolean keepSidebar = hasSidebar(oldRoot) && hasSidebar(newRoot);
        List<Node> oldParts = parts(oldRoot, keepSidebar);
        List<Node> newParts = parts(newRoot, keepSidebar);

        running = true;

        ParallelTransition out = new ParallelTransition();
        for (Node n : oldParts) {
            FadeTransition fade = new FadeTransition(Duration.millis(OUT_MS), n);
            fade.setToValue(0);
            out.getChildren().add(fade);
        }

        out.setOnFinished(e -> {
            try {
                for (Node n : newParts) {
                    n.setOpacity(0);
                    n.setTranslateY(SLIDE);
                }
                scene.setRoot(newRoot);
                playIn(newParts);
            } catch (RuntimeException ex) {
                running = false;
                throw ex;
            }
        });

        if (oldParts.isEmpty()) {
            out.getOnFinished().handle(null);
        } else {
            out.play();
        }
    }

    private static void playIn(List<Node> parts) {
        ParallelTransition in = new ParallelTransition();
        for (Node n : parts) {
            FadeTransition fade = new FadeTransition(Duration.millis(IN_MS), n);
            fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(IN_MS), n);
            slide.setToY(0);
            in.getChildren().addAll(fade, slide);
        }
        in.setOnFinished(e -> running = false);
        in.play();
    }

    /** Sidebar = first child with the "sidebar" style class. */
    private static boolean hasSidebar(Parent root) {
        if (root instanceof Pane p && !p.getChildren().isEmpty()) {
            return p.getChildren().get(0).getStyleClass().contains("sidebar");
        }
        return false;
    }

    /** The nodes to animate: everything after the sidebar, or the whole root. */
    private static List<Node> parts(Parent root, boolean keepSidebar) {
        List<Node> list = new ArrayList<>();
        if (keepSidebar && root instanceof Pane p) {
            list.addAll(p.getChildren().subList(1, p.getChildren().size()));
        } else {
            list.add(root);
        }
        return list;
    }
}
