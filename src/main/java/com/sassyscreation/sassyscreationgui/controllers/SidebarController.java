package com.sassyscreation.sassyscreationgui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of the shared left navigation panel (SIDEBAR.fxml).
 *
 * Every screen includes it with:
 *     <fx:include fx:id="sidebar" source="SIDEBAR.fxml" styleClass="page-inventory" />
 *
 * - styleClass "page-dashboard | page-orders | page-standsale | page-inventory" highlights the menu item.
 * - A page controller can reach this controller by declaring:   @FXML private SidebarController sidebarController;
 *   (the field name is the include's fx:id + "Controller"), e.g. sidebarController.setOrdersBadge(4);
 */
public class SidebarController implements Initializable {

    private static final String FXML_PATH            = "/com/sassyscreation/sassyscreationgui/";
    private static final String FXML_DASHBOARD       = "DASHBOARD.fxml";
    private static final String FXML_ORDERS          = "ORDERS.fxml";
    private static final String FXML_STAND_SALE      = "STANDSALE.fxml";
    private static final String FXML_INVENTORY_LIST  = "INVENTORY_LIST.fxml";
    private static final String FXML_LOGIN           = "LOGIN.fxml";
    private static final String FXML_ABOUT           = "ABOUT.fxml";
    private static final String FXML_CHANGE_PASSWORD = "CHANGEPASSWORD.fxml";

    // ---- background blur while a dialog is open ----
    private static final double BLUR_RADIUS = 14;     // higher = stronger blur
    private static final double BLUR_FADE_MS = 220;   // fade-in time

    // ---- sliding pink bar next to the active menu item ----
    private static final double BAR_WIDTH = 4;
    private static final double BAR_X = 0;      // move left/right to line up with the old bar
    private static final double BAR_GAP = 4;    // space above/below the bar inside the row
    private static final double SLIDE_MS = 320;
    private static final String OLD_BAR_OFF = "-fx-border-color: transparent; -fx-border-width: 0;";

    /** Where the bar was on the previous screen, so the new screen can slide it from there. */
    private static double lastY = Double.NaN;

    private VBox navBox;
    private Region bar;
    private Node activeRow;
    private boolean barPlaced = false;
    private Timeline slide;

    @FXML private VBox sidebarRoot;
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Button btnLogout;
    @FXML private Label lblNavOrdersBadge;
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // the including page sets styleClass="page-xxx" on the sidebar root AFTER it is loaded,
        // so listen for it and (re)apply the highlight
        sidebarRoot.getStyleClass().addListener((ListChangeListener<String>) c -> applyActiveFromStyleClass());

        // the pink bar is drawn by us now, so turn off the one from the CSS
        for (Button b : new Button[]{navDashboard, navOrders, navStandSale, navInventory}) {
            b.setStyle(OLD_BAR_OFF);
        }
        navBox = (VBox) navDashboard.getParent();
        bar = new Region();
        bar.setManaged(false);
        bar.setMouseTransparent(true);
        bar.setVisible(false);
        bar.setLayoutX(BAR_X);
        bar.setStyle("-fx-background-color: #C22A62; -fx-background-radius: 3;");
        navBox.getChildren().add(bar);

        applyActiveFromStyleClass();
    }

    // =====================================================================
    // Public API for page controllers
    // =====================================================================

    /** @param page "dashboard", "orders", "standsale" or "inventory" */
    public void setActive(String page) {
        for (Button b : new Button[]{navDashboard, navOrders, navStandSale, navInventory}) {
            b.getStyleClass().remove("nav-active");
        }
        if (page == null) {
            return;
        }
        Button target = switch (page.toLowerCase()) {
            case "dashboard" -> navDashboard;
            case "orders" -> navOrders;
            case "standsale" -> navStandSale;
            case "inventory" -> navInventory;
            default -> null;
        };
        if (target != null) {
            target.getStyleClass().add("nav-active");
            activeRow = rowOf(target);
            moveBar();
        }
    }

    /** The direct child of the menu box that holds this button (the Orders button sits in a StackPane). */
    private Node rowOf(Button b) {
        Node n = b;
        while (n.getParent() != navBox) {
            n = n.getParent();
        }
        return n;
    }

    /** Slides the pink bar to the active row (from where it was on the previous screen). */
    private void moveBar() {
        if (activeRow == null || bar == null) {
            return;
        }
        Region row = (Region) activeRow;

        // not laid out yet: wait until the row has a height, then try again
        if (row.getHeight() <= 0) {
            row.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV) {
                    if (newV.doubleValue() > 0) {
                        row.heightProperty().removeListener(this);
                        Platform.runLater(SidebarController.this::moveBar);
                    }
                }
            });
            return;
        }

        double y = row.getLayoutY() + BAR_GAP;
        bar.resize(BAR_WIDTH, row.getHeight() - 2 * BAR_GAP);
        bar.setVisible(true);

        // start from the previous screen's spot (first time) or where the bar is now
        double from = barPlaced ? bar.getTranslateY() : lastY;
        if (Double.isNaN(from)) {
            from = y;
        }
        bar.setTranslateY(from);
        if (slide != null) {
            slide.stop();
        }
        if (from != y) {
            slide = new Timeline(new KeyFrame(Duration.millis(SLIDE_MS),
                    new KeyValue(bar.translateYProperty(), y, Interpolator.EASE_BOTH)));
            slide.play();
        }

        barPlaced = true;
        lastY = y;
    }

    /** Updates the red number next to "Orders" (hidden when 0). */
    public void setOrdersBadge(int count) {
        lblNavOrdersBadge.setText(String.valueOf(count));
        lblNavOrdersBadge.setVisible(count > 0);
    }

    private void applyActiveFromStyleClass() {
        for (String c : sidebarRoot.getStyleClass()) {
            if (c.startsWith("page-")) {
                setActive(c.substring("page-".length()));
                return;
            }
        }
    }

    // =====================================================================
    // Navigation
    // =====================================================================
    @FXML private void goDashboard() { switchScreen(FXML_DASHBOARD, "Dashboard"); }
    @FXML private void goOrders()    { switchScreen(FXML_ORDERS, "Orders"); }
    @FXML private void goStandSale() { switchScreen(FXML_STAND_SALE, "Stand Sale"); }
    @FXML private void goInventory() { switchScreen(FXML_INVENTORY_LIST, "Inventory"); }

    // =====================================================================
    // Account menu
    // =====================================================================
    @FXML
    private void handleOpenChangePassword() {
        openDialog(FXML_CHANGE_PASSWORD, "Change password", false);
    }

    @FXML
    private void handleOpenAbout() {
        openDialog(FXML_ABOUT, "About", true);
    }

    @FXML
    private void handleLogout() {
        // custom dialog (LOGOUT.fxml) with blurred background, same style as About
        if (LogoutController.show(ownerWindow())) {
            // TODO: clear your session / current user here
            currentStage().setMaximized(false);
            switchScreen(FXML_LOGIN, "Login");
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================
    private Window ownerWindow() {
        return navDashboard.getScene().getWindow();
    }

    private Stage currentStage() {
        return (Stage) navDashboard.getScene().getWindow();
    }

    private void switchScreen(String fxmlName, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                error("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = currentStage();
            // smooth fade instead of an instant swap
            ScreenTransition.show(stage.getScene(), root);
            stage.setTitle("Sassy's Creation - " + title);
        } catch (Exception e) {
            e.printStackTrace();
            error("Could not open " + title, rootCause(e));
        }
    }

    /** @param borderless true = no title bar, transparent background (for rounded dialogs like About) */
    private void openDialog(String fxmlName, String title, boolean borderless) {
        Node ownerRoot = currentStage().getScene().getRoot();
        Effect previousEffect = ownerRoot.getEffect();   // remember any existing effect
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                error("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();

            Stage dialog = new Stage(borderless ? StageStyle.TRANSPARENT : StageStyle.DECORATED);
            dialog.initOwner(currentStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sassy's Creation - " + title);
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (borderless) {
                scene.setFill(Color.TRANSPARENT);
            }
            dialog.setScene(scene);

            // blur the page behind the dialog (fades in smoothly)
            GaussianBlur blur = new GaussianBlur(0);
            ownerRoot.setEffect(blur);
            new Timeline(new KeyFrame(Duration.millis(BLUR_FADE_MS),
                    new KeyValue(blur.radiusProperty(), BLUR_RADIUS, Interpolator.EASE_OUT))).play();

            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            error("Could not open " + title, rootCause(e));
        } finally {
            ownerRoot.setEffect(previousEffect);          // remove the blur when the dialog closes
        }
    }

    private String rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.toString();
    }

    private void error(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.setContentText(msg == null ? "Unknown error. Check the console for details." : msg);
        a.initOwner(ownerWindow());
        a.showAndWait();
    }
}