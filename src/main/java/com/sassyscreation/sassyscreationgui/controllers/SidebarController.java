package com.sassyscreation.sassyscreationgui.controllers;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.net.URL;
import java.util.Optional;
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
        }
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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Log out");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to log out?");
        confirm.initOwner(ownerWindow());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            error("Could not open " + title, rootCause(e));
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