package com.sassyscreation.sassyscreationgui.controllers;

import com.sassyscreation.sassyscreationgui.util.Transitions;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryListController {

    private static final String PATH = "/com/sassyscreation/sassyscreationgui/";
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

    // product table
    @FXML private TableView<Map<String, Object>> tvProducts;
    @FXML private TableColumn<Map<String, Object>, Object> colProdId;
    @FXML private TableColumn<Map<String, Object>, Object> colProdPhoto;
    @FXML private TableColumn<Map<String, Object>, Object> colProdName;
    @FXML private TableColumn<Map<String, Object>, Object> colProdCat;
    @FXML private TableColumn<Map<String, Object>, Object> colProdPrice;
    @FXML private TableColumn<Map<String, Object>, Object> colProdStock;
    @FXML private TableColumn<Map<String, Object>, Object> colProdActions;

    private ObservableList<Map<String, Object>> products;       // every product
    private FilteredList<Map<String, Object>> filteredProducts; // what the table shows

    @FXML
    public void initialize() {
        // keep the full list, show a filtered + sorted copy in the table
        products = tvProducts.getItems();
        filteredProducts = new FilteredList<>(products, p -> true);
        SortedList<Map<String, Object>> sorted = new SortedList<>(filteredProducts);
        sorted.comparatorProperty().bind(tvProducts.comparatorProperty());
        tvProducts.setItems(sorted);

        setupColumns();

        // wire the Edit / Restock buttons of every row
        for (Map<String, Object> row : products) {
            setupRowButtons(row);
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
            } else if (newVal == btnViewGrid) {
                openPage("INVENTORY_GRID.fxml");
            }
        });

        applyFilter();
    }

    // ---------- table setup ----------

    private void setupColumns() {
        // these columns hold ready-made nodes, so show them as the cell graphic
        for (TableColumn<Map<String, Object>, Object> col : List.of(
                colProdPhoto, colProdStock, colProdActions)) {
            col.setCellFactory(c -> new TableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    setGraphic(!empty && item instanceof Node ? (Node) item : null);
                }
            });
        }

        // sort the price by its number, not by its text ("₱1,000.00")
        colProdPrice.setComparator(Comparator.comparingDouble(p -> parsePrice(String.valueOf(p))));
        colProdStock.setSortable(false);
    }

    private void setupRowButtons(Map<String, Object> row) {
        Node actions = (Node) row.get("actions");
        for (Node node : actions.lookupAll(".btn-icon")) {
            if (!(node instanceof Button btn)) {
                continue;
            }
            if (btn.getStyleClass().contains("btn-edit")) {
                btn.setOnAction(e -> handleEdit(row));
            } else {
                btn.setOnAction(e -> handleRestock(row));
            }
        }
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

        filteredProducts.setPredicate(row -> {
            String rowCategory = (String) row.get("Category");
            String rowName = (String) row.get("Name");

            boolean categoryMatch = category.equals("All") || category.equals(rowCategory);
            boolean searchMatch = search.isEmpty() || rowName.toLowerCase().contains(search);
            return categoryMatch && searchMatch;
        });

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

        // numbers inside the chips
        setChipCount(chipAll, products.size());
        setChipCount(chipBouquet, bouquet);
        setChipCount(chipCrochet, crochet);
        setChipCount(chipKeychain, keychain);

        lblProductTotal.setText("Total Products: " + products.size());
        lblCategoryCounts.setText("Bouquet " + bouquet + "  ·  Crochet " + crochet
                + "  ·  Keychain " + keychain);
    }

    private int countCategory(String category) {
        int count = 0;
        for (Map<String, Object> row : products) {
            if (category.equals(row.get("Category"))) {
                count++;
            }
        }
        return count;
    }

    private void setChipCount(ToggleButton chip, int count) {
        if (chip.getGraphic() instanceof Label label) {
            label.setText(String.valueOf(count));
        }
    }

    // ---------- row actions ----------

    private void handleEdit(Map<String, Object> row) {
        // TODO: open an edit dialog for this product
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit product");
        alert.setHeaderText(null);
        alert.setContentText("Edit " + row.get("Name") + " (not connected yet).");
        alert.showAndWait();
    }

    private void handleRestock(Map<String, Object> row) {
        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("Restock");
        dialog.setHeaderText(null);
        dialog.setContentText("How many to add to " + row.get("Name") + "?");

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

        Label stock = (Label) row.get("stock");
        int total = add + parseStock(stock.getText());
        stock.setText(total >= LOW_STOCK ? total + " in stock" : total + " left");
        stock.getStyleClass().removeAll("stock-plain", "badge-pending");
        stock.getStyleClass().add(total >= LOW_STOCK ? "stock-plain" : "badge-pending");
        tvProducts.refresh();

        // TODO: save the new stock to the database
    }

    private int parseStock(String text) {
        // text looks like "10 in stock" or "4 left"
        try {
            return Integer.parseInt(text.split(" ")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parsePrice(String text) {
        // text looks like "₱1,000.00"
        try {
            return Double.parseDouble(text.replace("₱", "").replace(",", ""));
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
            URL url = getClass().getResource(PATH + fxml);
            if (url == null) {
                throw new IllegalStateException("FXML not found: " + PATH + fxml);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initOwner(btnAddProduct.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));

            // start the pop-in only once the window is visible
            dialog.setOnShown(e -> Transitions.popIn(root));

            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + fxml + ":\n" + e);
        }
    }
}