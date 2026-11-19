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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryGridController {

    private static final String PATH = "/com/sassyscreation/sassyscreationgui/";
    private static final int COLUMNS = 3;
    private static final int LOW_STOCK = 5;

    // sidebar
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Label lblNavOrdersBadge;
    @FXML private Button btnLogout;
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

    // top actions
    @FXML private Button btnExportInventory;
    @FXML private Button btnAddProduct;

    // category chips
    @FXML private ToggleGroup tgCat;
    @FXML private ToggleButton chipAll;
    @FXML private ToggleButton chipBouquet;
    @FXML private ToggleButton chipCrochet;
    @FXML private ToggleButton chipKeychain;

    // search and view switch
    @FXML private TextField tfProductSearch;
    @FXML private ToggleGroup tgView;
    @FXML private ToggleButton btnViewList;
    @FXML private ToggleButton btnViewGrid;

    // footer labels (hidden in the layout, still kept up to date)
    @FXML private Label lblProductTotal;
    @FXML private Label lblCategoryCounts;

    // product grid
    @FXML private ScrollPane spProducts;
    @FXML private GridPane gpProducts;

    // every product card, even the ones that are filtered out
    private final List<VBox> allCards = new ArrayList<>();

    @FXML
    public void initialize() {

            Object[] fields = { gpProducts, spProducts, tgCat, tgView, chipAll, chipBouquet,
                    chipCrochet, chipKeychain, tfProductSearch, btnViewList, btnViewGrid,
                    lblProductTotal, lblCategoryCounts, btnAddProduct };
            String[] names = { "gpProducts", "spProducts", "tgCat", "tgView", "chipAll", "chipBouquet",
                    "chipCrochet", "chipKeychain", "tfProductSearch", "btnViewList", "btnViewGrid",
                    "lblProductTotal", "lblCategoryCounts", "btnAddProduct" };
            for (int i = 0; i < fields.length; i++) {
                if (fields[i] == null) System.out.println("NULL fx:id -> " + names[i]);
            }

            // ...rest of your existing initialize()
        // remember all the cards from the FXML
        for (Node node : gpProducts.getChildren()) {
            if (node instanceof VBox card) {
                allCards.add(card);
                setupCardButtons(card);
            }
        }

        // category chips: always keep one selected
        tgCat.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            } else {
                applyFilter();
            }
        });

        // search box
        tfProductSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        // list / grid switch: always keep one selected
        tgView.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            } else if (newVal == btnViewList) {
                openPage("INVENTORY_LIST.fxml");
            }
        });

        applyFilter();
    }

    // ---------- sidebar navigation ----------

    @FXML
    private void goDashboard() {
        openPage("DASHBOARD.fxml");
    }

    @FXML
    private void goOrders() {
        openPage("ORDERS.fxml");
    }

    @FXML
    private void goStandSale() {
        openPage("STANDSALE.fxml");
    }

    @FXML
    private void goInventory() {
        // already on the inventory page
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            openPage("LOGIN.fxml");
        }
    }

    @FXML
    private void handleOpenChangePassword() {
        openDialog("CHANGEPASSWORD.fxml");
    }

    @FXML
    private void handleOpenAbout() {
        openDialog("ABOUT.fxml");
    }

    // ---------- top actions ----------

    @FXML
    private void handleOpenAddProduct() {
        openDialog("ADDPRODUCT.fxml");
        // TODO: reload the products from the database after adding
    }

    @FXML
    private void handleOpenExport() {
        openDialog("EXPORT.fxml");
    }

    // ---------- filtering ----------

    private void applyFilter() {
        String category = getSelectedCategory();
        String search = tfProductSearch.getText().trim().toLowerCase();

        List<Node> shown = new ArrayList<>();
        for (VBox card : allCards) {
            String cardCategory = (String) card.getUserData();
            String name = getProductName(card);

            boolean categoryMatch = category.equals("All") || category.equals(cardCategory);
            boolean searchMatch = search.isEmpty() || name.toLowerCase().contains(search);

            if (categoryMatch && searchMatch) {
                shown.add(card);
            }
        }

        // put the visible cards back into the grid, 3 per row
        gpProducts.getChildren().setAll(shown);
        for (int i = 0; i < shown.size(); i++) {
            GridPane.setColumnIndex(shown.get(i), i % COLUMNS);
            GridPane.setRowIndex(shown.get(i), i / COLUMNS);
        }

        updateCounts();
    }

    private String getSelectedCategory() {
        Toggle selected = tgCat.getSelectedToggle();
        return selected == null ? "All" : ((ToggleButton) selected).getText();
    }

    private void updateCounts() {
        int bouquet = countCategory("Bouquet");
        int crochet = countCategory("Crochet");
        int keychain = countCategory("Keychain");

        lblProductTotal.setText("Total Products: " + allCards.size());
        lblCategoryCounts.setText("Bouquet " + bouquet + "  ·  Crochet " + crochet
                + "  ·  Keychain " + keychain);
    }

    private int countCategory(String category) {
        int count = 0;
        for (VBox card : allCards) {
            if (category.equals(card.getUserData())) {
                count++;
            }
        }
        return count;
    }

    // ---------- card buttons ----------

    private void setupCardButtons(VBox card) {
        for (Node node : card.lookupAll(".prod-btn")) {
            if (!(node instanceof Button btn)) {
                continue;
            }
            if (btn.getStyleClass().contains("prod-btn-edit")) {
                btn.setOnAction(e -> handleEdit(card));
            } else if (btn.getStyleClass().contains("prod-btn-del")) {
                btn.setOnAction(e -> handleDelete(card));
            } else {
                btn.setOnAction(e -> handleRestock(card));
            }
        }
    }

    private void handleEdit(VBox card) {
        // TODO: open an edit dialog for this product
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit product");
        alert.setHeaderText(null);
        alert.setContentText("Edit " + getProductName(card) + " (not connected yet).");
        alert.showAndWait();
    }

    private void handleRestock(VBox card) {
        Label badge = findStockBadge(card);
        if (badge == null) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("Restock");
        dialog.setHeaderText(null);
        dialog.setContentText("How many to add to " + getProductName(card) + "?");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        int add;
        try {
            add = Integer.parseInt(result.get().trim());
        } catch (NumberFormatException e) {
            showError("Please enter a whole number.");
            return;
        }
        if (add <= 0) {
            showError("Quantity must be more than 0.");
            return;
        }

        int total = add + parseStock(badge.getText());
        badge.setText(total >= LOW_STOCK ? total + " in stock" : total + " left");
        badge.getStyleClass().removeAll("badge-done", "badge-pending");
        badge.getStyleClass().add(total >= LOW_STOCK ? "badge-done" : "badge-pending");

        // TODO: save the new stock to the database
    }

    private void handleDelete(VBox card) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete product");
        alert.setHeaderText(null);
        alert.setContentText("Delete " + getProductName(card) + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            allCards.remove(card);
            applyFilter();
            // TODO: delete the product from the database
        }
    }

    private String getProductName(VBox card) {
        Node name = card.lookup(".prod-name");
        return name instanceof Label label ? label.getText() : "";
    }

    private Label findStockBadge(VBox card) {
        for (Node node : card.lookupAll(".label")) {
            if (node.getStyleClass().contains("badge-done")
                    || node.getStyleClass().contains("badge-pending")) {
                return (Label) node;
            }
        }
        return null;
    }

    private int parseStock(String text) {
        // text looks like "10 in stock" or "4 left"
        try {
            return Integer.parseInt(text.split(" ")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ---------- helpers ----------

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Inventory");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openPage(String fxml) {
        Transitions.goTo(btnAddProduct, fxml);
    }

    private void openDialog(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH + fxml));
            Parent root = loader.load();
            Transitions.popIn(root);

            Stage dialog = new Stage();
            dialog.initOwner(btnAddProduct.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (Throwable e) {
            System.out.println("LOADED " + fxml + " OK");;
        }
    }
}