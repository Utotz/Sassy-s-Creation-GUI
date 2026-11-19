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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class StandSaleController {

    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // change these if your FXML files are named differently
    private static final String DASHBOARD_FXML = "DASHBOARD.fxml";
    private static final String ORDERS_FXML = "ORDERS.fxml";
    private static final String INVENTORY_FXML = "INVENTORY_GRID.fxml";
    private static final String EXPORT_FXML = "EXPORT.fxml";
    private static final String CHANGEPASSWORD_FXML = "CHANGEPASSWORD.fxml";
    private static final String ABOUT_FXML = "ABOUT.fxml";
    private static final String LOGOUT_FXML = "LOGOUT.fxml";

    // sidebar
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

    // header
    @FXML private Button btnExportSales;

    // record a sale card
    @FXML private ComboBox<String> cbSaleProduct;
    @FXML private Spinner<Integer> spQty;
    @FXML private TextField tfUnitPrice;
    @FXML private Label lblSaleTotal;
    @FXML private Label lblStockInfo;
    @FXML private Button btnRecordSale;

    // totals above the table
    @FXML private Label lblSaleCount;
    @FXML private Label lblSaleRevenue;

    // sales history table
    @FXML private TableView<Map<String, Object>> tvSales;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleDate;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleProduct;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleQty;
    @FXML private TableColumn<Map<String, Object>, Object> colSalePrice;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleTotal;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleActions;

    // TODO: replace this with the products from the database (ProductDAO)
    private final Map<String, Product> products = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        loadSampleProducts();

        // fill the product dropdown
        cbSaleProduct.getItems().addAll(products.keySet());

        // hook up the Edit / Delete buttons on the rows that came from the FXML
        for (Map<String, Object> row : tvSales.getItems()) {
            setupRowButtons(row);
        }

        // changing the product or quantity updates the price, total and stock text
        cbSaleProduct.valueProperty().addListener((obs, oldVal, newVal) -> updateSaleSummary());
        spQty.valueProperty().addListener((obs, oldVal, newVal) -> updateSaleSummary());

        // typing a quantity updates the total too, and it is saved when the field loses focus
        spQty.getEditor().textProperty().addListener((obs, oldVal, newVal) -> updateSaleSummary());
        spQty.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                commitQty();
            }
        });

        cbSaleProduct.getSelectionModel().selectFirst();
        updateSaleSummary();
        refreshTotals();
    }

    // ---------- sidebar navigation ----------

    @FXML
    private void goDashboard() {
        switchScene(DASHBOARD_FXML);
    }

    @FXML
    private void goOrders() {
        switchScene(ORDERS_FXML);
    }

    @FXML
    private void goStandSale() {
        // already on this page
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

    // ---------- header ----------

    @FXML
    private void handleOpenExport() {
        openDialog(EXPORT_FXML);
    }

    // ---------- record a sale ----------

    @FXML
    private void handleRecordSale() {
        commitQty();

        Product product = getSelectedProduct();
        if (product == null) {
            showInfo("Record sale", "Please select a product first.");
            return;
        }

        int qty = spQty.getValue();
        if (qty > product.stock) {
            showInfo("Record sale", "Only " + product.stock + " of " + product.name + " left in stock.");
            return;
        }

        // TODO: save the sale to the database and deduct the stock there
        product.stock -= qty;

        Map<String, Object> row = new HashMap<>();
        row.put("Date", LocalDate.now().toString());
        row.put("Product", product.name);
        row.put("Qty", String.valueOf(qty));
        row.put("Price", formatMoney(product.price));
        row.put("Total", formatMoney(product.price * qty));
        row.put("actions", buildActions());
        setupRowButtons(row);

        tvSales.getItems().add(0, row);

        spQty.getValueFactory().setValue(1);
        updateSaleSummary();
        refreshTotals();
    }

    private void updateSaleSummary() {
        Product product = getSelectedProduct();
        int qty = readQty();

        if (product == null) {
            tfUnitPrice.clear();
            lblSaleTotal.setText(formatMoney(0));
            lblStockInfo.setText("Select a product to see its stock");
            return;
        }

        tfUnitPrice.setText(formatMoney(product.price));
        lblSaleTotal.setText(formatMoney(product.price * qty));

        if (product.stock <= 0) {
            lblStockInfo.setText("Out of stock");
        } else if (qty > product.stock) {
            lblStockInfo.setText("Not enough stock. Only " + product.stock + " left.");
        } else {
            lblStockInfo.setText("Current stock: " + product.stock + " (deducted automatically)");
        }

        btnRecordSale.setDisable(product.stock <= 0 || qty > product.stock);
    }

    // ---------- row buttons ----------

    private HBox buildActions() {
        Button edit = new Button();
        edit.getStyleClass().addAll("btn-icon", "btn-edit");
        edit.setFocusTraversable(false);
        edit.setMnemonicParsing(false);
        edit.setGraphic(icon("ico-edit-o"));
        edit.setTooltip(new Tooltip("Edit"));

        Button delete = new Button();
        delete.getStyleClass().addAll("btn-icon", "btn-icon-danger");
        delete.setFocusTraversable(false);
        delete.setMnemonicParsing(false);
        delete.setGraphic(icon("ico-delete-o"));
        delete.setTooltip(new Tooltip("Delete"));

        HBox box = new HBox(10, edit, delete);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return box;
    }

    private Region icon(String shapeClass) {
        Region region = new Region();
        region.getStyleClass().addAll("ico", shapeClass);
        return region;
    }

    private void setupRowButtons(Map<String, Object> row) {
        Object actions = row.get("actions");
        if (!(actions instanceof Node)) {
            return;
        }

        for (Node node : ((Node) actions).lookupAll(".btn-icon")) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button button = (Button) node;

            if (button.getStyleClass().contains("btn-icon-danger")) {
                button.setOnAction(e -> handleDeleteSale(row));
            } else {
                button.setOnAction(e -> handleEditSale(row));
            }
        }
    }

    private void handleEditSale(Map<String, Object> row) {
        // TODO: open an EDITSALE dialog for this sale
        showInfo("Edit sale", "Editing the " + row.get("Product") + " sale from " + row.get("Date") + ".");
    }

    private void handleDeleteSale(Map<String, Object> row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete sale");
        alert.setHeaderText(null);
        alert.setContentText("Delete the " + row.get("Product") + " sale from " + row.get("Date") + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: delete the sale from the database and put the stock back there
            Product product = products.get(String.valueOf(row.get("Product")));
            if (product != null) {
                product.stock += parseInt(String.valueOf(row.get("Qty")));
            }

            tvSales.getItems().remove(row);
            updateSaleSummary();
            refreshTotals();
        }
    }

    // ---------- totals ----------

    private void refreshTotals() {
        double revenue = 0;
        for (Map<String, Object> row : tvSales.getItems()) {
            revenue += parseMoney(String.valueOf(row.get("Total")));
        }

        lblSaleCount.setText(String.valueOf(tvSales.getItems().size()));
        lblSaleRevenue.setText(formatMoney(revenue));
    }

    // ---------- helpers ----------

    private Product getSelectedProduct() {
        String name = cbSaleProduct.getValue();
        return name == null ? null : products.get(name);
    }

    // reads the number typed in the spinner without crashing on bad text
    private int readQty() {
        String text = spQty.getEditor().getText();
        int qty = parseInt(text == null ? "" : text.trim());
        return qty < 1 ? 1 : qty;
    }

    private void commitQty() {
        spQty.getValueFactory().setValue(readQty());
        spQty.getEditor().setText(String.valueOf(spQty.getValue()));
    }

    private int parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseMoney(String text) {
        try {
            return Double.parseDouble(text.replace("₱", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatMoney(double amount) {
        return String.format("₱%,.2f", amount);
    }

    // TODO: remove this once the products come from the database
    private void loadSampleProducts() {
        products.put("Gypsophila", new Product("Gypsophila", 30, 12));
        products.put("Rose Keychain", new Product("Rose Keychain", 50, 20));
        products.put("Sunflower - Fuzzy Wire", new Product("Sunflower - Fuzzy Wire", 55, 8));
        products.put("Baby Mushroom Keychain", new Product("Baby Mushroom Keychain", 80, 6));
        products.put("Mini Daisy Keychain - Fuzzy Wire", new Product("Mini Daisy Keychain - Fuzzy Wire", 20, 15));
    }

    // ---------- scenes and dialogs ----------

    private void switchScene(String fxml) {
        // fade out this page, then show the next one
        Transitions.fadeOut(navStandSale.getScene().getRoot(), () -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxml));
                Parent root = loader.load();
                Transitions.fadeIn(root);

                Stage stage = (Stage) navStandSale.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
            } catch (IOException e) {
                navStandSale.getScene().getRoot().setOpacity(1);
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
            dialog.initOwner(navStandSale.getScene().getWindow());
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

    // a simple holder for the sample data above
    private static class Product {
        final String name;
        final double price;
        int stock;

        Product(String name, double price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }
}
