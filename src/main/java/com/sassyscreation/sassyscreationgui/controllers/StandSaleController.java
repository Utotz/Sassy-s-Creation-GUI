package com.sassyscreation.sassyscreationgui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class StandSaleController implements Initializable {

    /** Folder (inside resources) where all the FXML files live. */
    private static final String FXML_PATH = "/com/sassyscreation/sassyscreationgui/";

    // ---- sidebar ----
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Label lblNavOrdersBadge;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Button btnLogout;

    // ---- top bar ----
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

    // ---- header ----
    @FXML private Button btnExportSales;

    // ---- record a sale form ----
    @FXML private ComboBox<String> cbSaleProduct;
    @FXML private Spinner<Integer> spQty;
    @FXML private TextField tfUnitPrice;
    @FXML private Label lblSaleTotal;
    @FXML private Label lblStockInfo;
    @FXML private Button btnRecordSale;

    // ---- summary bar ----
    @FXML private Label lblSaleCount;
    @FXML private Label lblSaleRevenue;

    // ---- sales history table ----
    @FXML private TableView<Map<String, Object>> tvSales;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleProduct;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleQty;
    @FXML private TableColumn<Map<String, Object>, Object> colSalePrice;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleTotal;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleDate;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleActions;

    /** A sellable product with its price and remaining stock. */
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

    private final Map<String, Product> products = new LinkedHashMap<>();
    private final ObservableList<Map<String, Object>> allSales = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProducts();
        loadSales();
        setUpForm();
        setUpTable();
        updateForm();
        updateSummary();
    }

    // =====================================================================
    //  Data
    // =====================================================================

    /**
     * TEMPORARY: sample products. Replace with a database query
     * (name, unit price and current stock from your inventory table).
     */
    private void loadProducts() {
        addProduct("Rose Keychain", 50.00, 20);
        addProduct("Mini Daisy Keychain - Fuzzy Wire", 20.00, 15);
        addProduct("Gypsophila", 30.00, 12);
        addProduct("Baby Mushroom Keychain", 80.00, 8);
        addProduct("Sunflower - Fuzzy Wire", 55.00, 6);
    }

    private void addProduct(String name, double price, int stock) {
        products.put(name, new Product(name, price, stock));
    }

    /**
     * TEMPORARY: sample sales history that matches the design.
     * Replace with a database query (SELECT from your stand sales table).
     */
    private void loadSales() {
        allSales.setAll(
                sale("Rose Keychain", 2, 50.00, "2026-04-25"),
                sale("Mini Daisy Keychain - Fuzzy Wire", 3, 20.00, "2026-04-25"),
                sale("Gypsophila", 5, 30.00, "2026-04-26"),
                sale("Baby Mushroom Keychain", 1, 80.00, "2026-04-26"),
                sale("Sunflower - Fuzzy Wire", 2, 55.00, "2026-04-27"),
                sale("Gypsophila", 1, 30.00, "2026-04-29"),
                sale("Gypsophila", 1, 30.00, "2026-05-01"),
                sale("Rose Keychain", 10, 50.00, "2026-05-02"),
                sale("Gypsophila", 5, 30.00, "2026-05-03"));
    }

    // Keys must match the MapValueFactory keys in the FXML (case-sensitive)
    private Map<String, Object> sale(String product, int qty, double price, String date) {
        Map<String, Object> m = new HashMap<>();
        m.put("Product", product);
        m.put("Qty", qty);
        m.put("Price", price);
        m.put("Total", qty * price);
        m.put("Date", date);
        m.put("actions", "actions");   // placeholder so the Actions column has a value
        return m;
    }

    // =====================================================================
    //  Record a sale form
    // =====================================================================

    private void setUpForm() {
        cbSaleProduct.setItems(FXCollections.observableArrayList(products.keySet()));
        if (cbSaleProduct.getValue() == null || !products.containsKey(cbSaleProduct.getValue())) {
            cbSaleProduct.getSelectionModel().selectFirst();
        }

        spQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));

        // Keep typed numbers in the spinner when the field loses focus
        spQty.getEditor().focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                commitSpinnerText();
            }
        });

        cbSaleProduct.valueProperty().addListener((obs, o, n) -> updateForm());
        spQty.valueProperty().addListener((obs, o, n) -> updateForm());
    }

    private void commitSpinnerText() {
        try {
            int typed = Integer.parseInt(spQty.getEditor().getText().trim());
            typed = Math.max(1, Math.min(999, typed));
            spQty.getValueFactory().setValue(typed);
        } catch (NumberFormatException e) {
            spQty.getEditor().setText(String.valueOf(spQty.getValue()));
        }
    }

    /** Refreshes unit price, total and the stock note for the selected product and quantity. */
    private void updateForm() {
        Product p = products.get(cbSaleProduct.getValue());
        if (p == null) {
            tfUnitPrice.setText("");
            lblSaleTotal.setText(peso(0));
            lblStockInfo.setText("Select a product");
            return;
        }

        int qty = spQty.getValue() == null ? 1 : spQty.getValue();
        tfUnitPrice.setText(String.format("%.2f", p.price));
        lblSaleTotal.setText(peso(qty * p.price));

        if (p.stock <= 0) {
            lblStockInfo.setText("Out of stock");
        } else {
            lblStockInfo.setText("Current stock: " + p.stock + " (deducted automatically)");
        }
    }

    @FXML
    private void handleRecordSale() {
        commitSpinnerText();

        Product p = products.get(cbSaleProduct.getValue());
        if (p == null) {
            showWarning("No product selected", "Please choose a product first.");
            return;
        }

        int qty = spQty.getValue();
        if (qty < 1) {
            showWarning("Invalid quantity", "Quantity must be at least 1.");
            return;
        }
        if (qty > p.stock) {
            showWarning("Not enough stock",
                    "Only " + p.stock + " of \"" + p.name + "\" left in stock.");
            return;
        }

        // TODO: also INSERT the sale and UPDATE the stock in the database
        p.stock -= qty;
        allSales.add(0, sale(p.name, qty, p.price, LocalDate.now().toString()));

        spQty.getValueFactory().setValue(1);
        updateForm();
        updateSummary();

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Sale recorded");
        info.setHeaderText(null);
        info.setContentText(qty + " x " + p.name + " recorded: " + peso(qty * p.price));
        info.showAndWait();
    }

    // =====================================================================
    //  Sales table
    // =====================================================================

    private void setUpTable() {
        colSalePrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : peso(((Number) item).doubleValue()));
            }
        });

        colSaleTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : peso(((Number) item).doubleValue()));
            }
        });

        colSaleActions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                Map<String, Object> row = getTableRow() == null ? null : getTableRow().getItem();
                if (empty || row == null) {
                    setGraphic(null);
                    return;
                }

                Button edit = iconButton("ico-edit", "Edit", "btn-icon", "btn-edit");
                edit.setText("Edit");
                edit.setOnAction(e -> editSale(row));

                Button delete = iconButton("ico-delete", "Delete", "btn-icon", "btn-icon-danger");
                delete.setOnAction(e -> deleteSale(row));

                HBox box = new HBox(10, edit, delete);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        // Replaces the sample rows hard-coded in the FXML
        SortedList<Map<String, Object>> sorted = new SortedList<>(allSales);
        sorted.comparatorProperty().bind(tvSales.comparatorProperty());
        tvSales.setItems(sorted);
    }

    private Button iconButton(String icon, String tooltip, String... styleClasses) {
        Button b = new Button();
        b.setFocusTraversable(false);
        b.setMnemonicParsing(false);
        b.getStyleClass().addAll(styleClasses);
        Region graphic = new Region();
        graphic.getStyleClass().addAll("ico", icon);
        b.setGraphic(graphic);
        b.setTooltip(new Tooltip(tooltip));
        return b;
    }

    private void editSale(Map<String, Object> row) {
        int oldQty = (Integer) row.get("Qty");
        String name = String.valueOf(row.get("Product"));

        TextInputDialog dialog = new TextInputDialog(String.valueOf(oldQty));
        dialog.setTitle("Edit sale");
        dialog.setHeaderText("Edit quantity for " + name);
        dialog.setContentText("Quantity sold:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        int newQty;
        try {
            newQty = Integer.parseInt(result.get().trim());
        } catch (NumberFormatException e) {
            showWarning("Invalid quantity", "Please enter a whole number.");
            return;
        }
        if (newQty < 1) {
            showWarning("Invalid quantity", "Quantity must be at least 1.");
            return;
        }

        Product p = products.get(name);
        int diff = newQty - oldQty;
        if (p != null) {
            if (diff > p.stock) {
                showWarning("Not enough stock", "Only " + p.stock + " more available.");
                return;
            }
            p.stock -= diff;   // negative diff gives stock back
        }

        // TODO: also UPDATE the sale and the stock in the database
        double price = ((Number) row.get("Price")).doubleValue();
        row.put("Qty", newQty);
        row.put("Total", newQty * price);

        tvSales.refresh();
        updateForm();
        updateSummary();
    }

    private void deleteSale(Map<String, Object> row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete sale");
        confirm.setHeaderText("Delete this sale?");
        confirm.setContentText(row.get("Qty") + " x " + row.get("Product")
                + "\nThe quantity will be returned to stock.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: also DELETE the sale and restore the stock in the database
            Product p = products.get(String.valueOf(row.get("Product")));
            if (p != null) {
                p.stock += (Integer) row.get("Qty");
            }
            allSales.remove(row);
            updateForm();
            updateSummary();
        }
    }

    private void updateSummary() {
        double revenue = allSales.stream()
                .mapToDouble(s -> ((Number) s.get("Total")).doubleValue())
                .sum();

        lblSaleCount.setText("Total Stand Sales: " + allSales.size());
        lblSaleRevenue.setText(peso(revenue));
    }

    private String peso(double amount) {
        return String.format("\u20B1%,.2f", amount);
    }

    // =====================================================================
    //  Header
    // =====================================================================

    @FXML
    private void handleOpenExport() {
        openDialog("EXPORTDIALOG.fxml", "Export sales");
    }

    // =====================================================================
    //  Navigation (sidebar)
    // =====================================================================

    @FXML
    private void goDashboard() {
        switchScreen("DASHBOARD.fxml", "Dashboard");
    }

    @FXML
    private void goOrders() {
        switchScreen("ORDERS.fxml", "Orders");
    }

    @FXML
    private void goStandSale() {
        // Already on the stand sale screen
    }

    @FXML
    private void goInventory() {
        switchScreen("INVENTORY_LIST.fxml", "Inventory");
    }

    // =====================================================================
    //  Account menu
    // =====================================================================

    @FXML
    private void handleOpenChangePassword() {
        openDialog("CHANGEPASSWORD.fxml", "Change password");
    }

    @FXML
    private void handleOpenAbout() {
        // borderless = true -> no Windows title bar, rounded transparent window
        openDialog("ABOUT.fxml", "About", true);
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Log out");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to log out?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentStage().setMaximized(false);
            switchScreen("LOGIN.fxml", "Login");
        }
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    private Stage currentStage() {
        return (Stage) navStandSale.getScene().getWindow();
    }

    /** Replaces the content of the current window with another FXML screen. */
    private void switchScreen(String fxmlName, String title) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                showError("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();
            Stage stage = currentStage();
            stage.getScene().setRoot(root);
            stage.setTitle("Sassy's Creation - " + title);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + title, e.getMessage());
        }
    }

    /** Opens an FXML screen in a normal pop-up window (with title bar). */
    private void openDialog(String fxmlName, String title) {
        openDialog(fxmlName, title, false);
    }

    /**
     * Opens an FXML screen in a pop-up window on top of this screen.
     *
     * @param borderless true = no title bar, transparent background (for rounded dialogs like About)
     */
    private void openDialog(String fxmlName, String title, boolean borderless) {
        try {
            URL resource = getClass().getResource(FXML_PATH + fxmlName);
            if (resource == null) {
                showError("Screen not found", fxmlName + " could not be found in the resources folder.");
                return;
            }
            Parent root = new FXMLLoader(resource).load();

            Stage dialog = new Stage(borderless ? StageStyle.TRANSPARENT : StageStyle.DECORATED);
            dialog.initOwner(currentStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sassy's Creation - " + title);

            Scene scene = new Scene(root);
            if (borderless) {
                scene.setFill(Color.TRANSPARENT);
            }
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open " + title, e.getMessage());
        }
    }

    private void showWarning(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Stand Sale");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "Unknown error. Check the console for details." : message);
        alert.showAndWait();
    }
}