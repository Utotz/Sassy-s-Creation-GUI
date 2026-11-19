package com.sassyscreation.sassyscreationgui.controllers;

import com.sassyscreation.sassyscreationgui.util.Transitions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdersController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // change these if your FXML files are named differently
    private static final String DASHBOARD_FXML = "DASHBOARD.fxml";
    private static final String STANDSALE_FXML = "STANDSALE.fxml";
    private static final String INVENTORY_FXML = "INVENTORY.fxml";
    private static final String ADDORDER_FXML = "ADDORDER.fxml";
    private static final String EXPORT_FXML = "EXPORT.fxml";
    private static final String CHANGEPASSWORD_FXML = "CHANGEPASSWORD.fxml";
    private static final String ABOUT_FXML = "ABOUT.fxml";
    private static final String LOGOUT_FXML = "LOGOUT.fxml";

    private static final String PENDING = "Pending";
    private static final String DONE = "Done";
    private static final String CANCELLED = "Cancelled";

    // sidebar
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Label lblNavOrdersBadge;
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

    // header buttons
    @FXML private Button btnExportOrders;
    @FXML private Button btnAddOrder;

    // filter chips
    @FXML private ToggleGroup tgOrderStatus;
    @FXML private ToggleButton chipOrdAll;
    @FXML private ToggleButton chipOrdPending;
    @FXML private ToggleButton chipOrdDone;
    @FXML private ToggleButton chipOrdCancelled;
    @FXML private Label lblChipAllCount;
    @FXML private Label lblChipPendingCount;
    @FXML private Label lblChipDoneCount;
    @FXML private Label lblChipCancelledCount;

    // search + cards
    @FXML private TextField tfOrderSearch;
    @FXML private ScrollPane spOrderCards;
    @FXML private GridPane gpOrderCards;

    // every card in the FXML, so filtering can add/remove them without losing any
    private final List<Node> allCards = new ArrayList<>();

    @FXML
    public void initialize() {
        // remember every card that came from the FXML
        allCards.addAll(gpOrderCards.getChildren());

        // hook up the View / Edit / Delete button on each card
        for (Node card : allCards) {
            setupCardButtons(card);
        }

        // one chip must always stay selected
        tgOrderStatus.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                tgOrderStatus.selectToggle(oldToggle);
                return;
            }
            animateFilter();
        });

        // filter while typing (no fade, it would feel laggy)
        tfOrderSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        refreshCounts();
        applyFilter();
    }

    // ---------- sidebar navigation ----------

    @FXML
    private void goDashboard() {
        switchScene(DASHBOARD_FXML);
    }

    @FXML
    private void goOrders() {
        // already on this page
    }

    @FXML
    private void goStandSale() {
        switchScene(STANDSALE_FXML);
    }

    @FXML
    private void goInventory() {
        switchScene(INVENTORY_FXML);
    }

    // ---------- account menu ----------

    @FXML
    private void handleOpenChangePassword() {
        openDialog(CHANGEPASSWORD_FXML);
    }

    @FXML
    private void handleOpenAbout() {
        openDialog(ABOUT_FXML);
    }

    @FXML
    private void handleLogout() {
        // the LOGOUT dialog's own controller should take the user back to LOGIN.fxml
        openDialog(LOGOUT_FXML);
    }

    // ---------- header buttons ----------

    @FXML
    private void handleOpenExport() {
        openDialog(EXPORT_FXML);
    }

    @FXML
    private void handleOpenAddOrder() {
        openDialog(ADDORDER_FXML);
        // TODO: reload the cards from the database after the dialog closes
    }

    // ---------- card buttons ----------

    private void setupCardButtons(Node card) {
        for (Node node : card.lookupAll(".order-btn")) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button button = (Button) node;

            if (button.getStyleClass().contains("order-btn-del")) {
                button.setOnAction(e -> handleDeleteOrder(card));
            } else if (button.getStyleClass().contains("order-btn-edit")) {
                button.setOnAction(e -> handleEditOrder(card));
            } else {
                button.setOnAction(e -> handleViewOrder(card));
            }
        }
    }

    private void handleViewOrder(Node card) {
        // TODO: open a VIEWORDER dialog for this order
        showInfo("Order details", "Viewing " + getCustomer(card) + "'s order.");
    }

    private void handleEditOrder(Node card) {
        // TODO: open an EDITORDER dialog for this order
        showInfo("Edit order", "Editing " + getCustomer(card) + "'s order.");
    }

    private void handleDeleteOrder(Node card) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete order");
        alert.setHeaderText(null);
        alert.setContentText("Delete " + getCustomer(card) + "'s order?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: delete the order from the database
            allCards.remove(card);
            refreshCounts();
            applyFilter();
        }
    }

    // ---------- filtering ----------

    // same fade out -> swap -> fade in as the sidebar navigation
    private void animateFilter() {
        Transitions.fadeOut(gpOrderCards, () -> {
            applyFilter();
            Transitions.fadeIn(gpOrderCards);
        });
    }

    private void applyFilter() {
        String status = getSelectedStatus();
        String search = tfOrderSearch.getText().trim().toLowerCase();

        gpOrderCards.getChildren().clear();

        int index = 0;
        for (Node card : allCards) {
            if (!matchesStatus(card, status) || !matchesSearch(card, search)) {
                continue;
            }
            GridPane.setColumnIndex(card, index % 2);
            GridPane.setRowIndex(card, index / 2);
            gpOrderCards.getChildren().add(card);
            index++;
        }

        if (index == 0) {
            Label empty = new Label("No orders found");
            empty.getStyleClass().add("page-sub");
            GridPane.setColumnIndex(empty, 0);
            GridPane.setRowIndex(empty, 0);
            GridPane.setColumnSpan(empty, 2);
            gpOrderCards.getChildren().add(empty);
        }
    }

    private String getSelectedStatus() {
        Toggle selected = tgOrderStatus.getSelectedToggle();
        if (selected == chipOrdPending) {
            return PENDING;
        }
        if (selected == chipOrdDone) {
            return DONE;
        }
        if (selected == chipOrdCancelled) {
            return CANCELLED;
        }
        return "All";
    }

    private boolean matchesStatus(Node card, String status) {
        return status.equals("All") || status.equals(card.getUserData());
    }

    private boolean matchesSearch(Node card, String search) {
        if (search.isEmpty()) {
            return true;
        }
        // look through every label on the card (customer, product, color, date...)
        for (Node node : card.lookupAll(".label")) {
            if (node instanceof Label) {
                String text = ((Label) node).getText();
                if (text != null && text.toLowerCase().contains(search)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ---------- counts ----------

    private void refreshCounts() {
        int pending = countStatus(PENDING);
        int done = countStatus(DONE);
        int cancelled = countStatus(CANCELLED);

        lblChipAllCount.setText(String.valueOf(allCards.size()));
        lblChipPendingCount.setText(String.valueOf(pending));
        lblChipDoneCount.setText(String.valueOf(done));
        lblChipCancelledCount.setText(String.valueOf(cancelled));

        // the sidebar badge shows how many orders still need attention
        lblNavOrdersBadge.setText(String.valueOf(pending));
        lblNavOrdersBadge.setVisible(pending > 0);
    }

    private int countStatus(String status) {
        int count = 0;
        for (Node card : allCards) {
            if (status.equals(card.getUserData())) {
                count++;
            }
        }
        return count;
    }

    private String getCustomer(Node card) {
        Node label = card.lookup(".order-cust");
        return label instanceof Label ? ((Label) label).getText() : "this customer";
    }

    // ---------- scenes and dialogs ----------

    private void switchScene(String fxml) {
        // fade out this page, then show the next one
        Transitions.fadeOut(navOrders.getScene().getRoot(), () -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxml));
                Parent root = loader.load();
                Transitions.fadeIn(root);

                Stage stage = (Stage) navOrders.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
            } catch (IOException e) {
                navOrders.getScene().getRoot().setOpacity(1);
                e.printStackTrace();
            }
        });
    }

    private void openDialog(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxml));
            Parent root = loader.load();
            Transitions.popIn(root);

            Stage dialog = new Stage();
            dialog.initOwner(navOrders.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}