package com.sassyscreation.sassyscreationgui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;

public class StandSaleController implements Initializable {

    // ---- sidebar (set by <fx:include fx:id="sidebar"> in STANDSALE.fxml) ----
    @FXML private SidebarController sidebarController;

    // ---- header ----
    @FXML private Button btnExportSales;

    // ---- record a sale form ----
    @FXML private ComboBox<String> cbSaleProduct;
    @FXML private Spinner<Integer> spQty;
    @FXML private TextField tfUnitPrice;
    @FXML private Label lblSaleTotal;
    @FXML private Label lblStockInfo;
    @FXML private Button btnRecordSale;

    // ---- summary chips (the FXML holds the "Total Stand Sales:" / "Total Revenue:" text; these hold only the values) ----
    @FXML private Label lblSaleCount;
    @FXML private Label lblSaleRevenue;

    // ---- sales history table ----
    @FXML private TableView<Map<String, Object>> tvSales;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleDate;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleProduct;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleQty;
    @FXML private TableColumn<Map<String, Object>, Object> colSalePrice;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleTotal;
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
        sidebarController.setActive("standsale");

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
        cbSaleProduct.getSelectionModel().selectFirst();

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

        // Actions: icon-only edit + delete buttons (matches the design)
        colSaleActions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                Map<String, Object> row = getTableRow() == null ? null : getTableRow().getItem();
                if (empty || row == null) {
                    setGraphic(null);
                    return;
                }

                Button edit = iconButton("ico-edit-o", "Edit", "btn-icon", "btn-edit");
                edit.setOnAction(e -> editSale(row));

                Button delete = iconButton("ico-delete-o", "Delete", "btn-icon", "btn-icon-danger");
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

        // Newest sales first, like the design
        colSaleDate.setSortType(TableColumn.SortType.DESCENDING);
        tvSales.getSortOrder().setAll(colSaleDate);
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
        LocalDate date;
        try {
            date = LocalDate.parse(String.valueOf(row.get("Date")));
        } catch (Exception e) {
            date = LocalDate.now();
        }

        EditSaleController.SaleData data = new EditSaleController.SaleData(
                String.valueOf(row.get("Product")),
                (Integer) row.get("Qty"),
                ((Number) row.get("Price")).doubleValue(),
                date);

        List<EditSaleController.ProductOption> options = new ArrayList<>();
        for (Product p : products.values()) {
            options.add(new EditSaleController.ProductOption(p.name, p.price, p.stock));
        }

        EditSaleController.open(tvSales.getScene(), data, options,
                (name, qty, unitPrice, newDate) -> applyEdit(row, name, qty, unitPrice, newDate));
    }

    /** Gives the old quantity back to the old product, takes the new quantity from the new one. */
    private void applyEdit(Map<String, Object> row, String newName, int newQty,
                           double unitPrice, LocalDate newDate) {
        Product oldProduct = products.get(String.valueOf(row.get("Product")));
        if (oldProduct != null) {
            oldProduct.stock += (Integer) row.get("Qty");
        }
        Product newProduct = products.get(newName);
        if (newProduct != null) {
            newProduct.stock -= newQty;
        }

        // TODO: also UPDATE the sale and the stock in the database
        row.put("Product", newName);
        row.put("Qty", newQty);
        row.put("Price", unitPrice);
        row.put("Total", newQty * unitPrice);
        row.put("Date", newDate.toString());

        tvSales.refresh();
        tvSales.sort();
        updateForm();
        updateSummary();
    }

    /** Shows the custom "Delete this sale?" dialog (DeleteSaleController / DELETESALE.fxml). */
    private void deleteSale(Map<String, Object> row) {
        int qty = (Integer) row.get("Qty");
        String productName = String.valueOf(row.get("Product"));

        boolean confirmed = DeleteSaleController.show(
                tvSales.getScene().getWindow(),
                String.valueOf(row.get("Date")),
                productName,
                qty,
                peso(((Number) row.get("Price")).doubleValue()),
                peso(((Number) row.get("Total")).doubleValue()));

        if (confirmed) {
            // TODO: also DELETE the sale and restore the stock in the database
            Product p = products.get(productName);
            if (p != null) {
                p.stock += qty;
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

        // The "Total Stand Sales:" / "Total Revenue:" text is in the FXML; only set the values here
        lblSaleCount.setText(String.valueOf(allSales.size()));
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
        Optional<ExportDialogController.ExportOptions> choice =
                ExportDialogController.show(currentStage(), "Stand sales", false);
        if (choice.isEmpty()) {
            return;
        }
        ExportDialogController.ExportOptions opts = choice.get();

        List<List<String>> rows = new ArrayList<>();
        for (Map<String, Object> s : allSales) {
            if (!opts.inRange(String.valueOf(s.get("Date")))) continue;

            rows.add(List.of(
                    String.valueOf(s.get("Date")),
                    String.valueOf(s.get("Product")),
                    String.valueOf(s.get("Qty")),
                    String.format("%.2f", ((Number) s.get("Price")).doubleValue()),
                    String.format("%.2f", ((Number) s.get("Total")).doubleValue())));
        }

        ExportDialogController.export(currentStage(), opts, "stand-sales",
                List.of("Date", "Product", "Qty", "Unit price (PHP)", "Total (PHP)"),
                rows);
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    private Stage currentStage() {
        return (Stage) btnRecordSale.getScene().getWindow();
    }

    private void showWarning(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Stand Sale");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}