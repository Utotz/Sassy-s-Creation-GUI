package com.sassyscreation.sassyscreationgui.controllers;

<<<<<<< HEAD
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
=======
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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

<<<<<<< HEAD
import java.io.IOException;
=======
import java.net.URL;
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
<<<<<<< HEAD

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
=======
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
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    @FXML private MenuButton mbAccount;
    @FXML private MenuItem miChangePassword;
    @FXML private MenuItem miAbout;
    @FXML private MenuItem miLogout;

<<<<<<< HEAD
    // header
    @FXML private Button btnExportSales;

    // record a sale card
=======
    // ---- header ----
    @FXML private Button btnExportSales;

    // ---- record a sale form ----
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    @FXML private ComboBox<String> cbSaleProduct;
    @FXML private Spinner<Integer> spQty;
    @FXML private TextField tfUnitPrice;
    @FXML private Label lblSaleTotal;
    @FXML private Label lblStockInfo;
    @FXML private Button btnRecordSale;

<<<<<<< HEAD
    // totals above the table
    @FXML private Label lblSaleCount;
    @FXML private Label lblSaleRevenue;

    // sales history table
    @FXML private TableView<Map<String, Object>> tvSales;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleDate;
=======
    // ---- summary bar ----
    @FXML private Label lblSaleCount;
    @FXML private Label lblSaleRevenue;

    // ---- sales history table ----
    @FXML private TableView<Map<String, Object>> tvSales;
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
    @FXML private TableColumn<Map<String, Object>, Object> colSaleProduct;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleQty;
    @FXML private TableColumn<Map<String, Object>, Object> colSalePrice;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleTotal;
<<<<<<< HEAD
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
=======
    @FXML private TableColumn<Map<String, Object>, Object> colSaleDate;
    @FXML private TableColumn<Map<String, Object>, Object> colSaleActions;

    /** A sellable product with its price and remaining stock. */
    static class Product {
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
        final String name;
        final double price;
        int stock;

        Product(String name, double price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }
<<<<<<< HEAD
}
=======

    // Shared (static) so EditSaleController can read and change the same data
    static final Map<String, Product> products = new LinkedHashMap<>();
    static final ObservableList<Map<String, Object>> allSales = FXCollections.observableArrayList();

    /** The sale row currently being edited on the Edit Sale screen (set before opening it). */
    static Map<String, Object> editingRow = null;

    private static boolean dataLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!dataLoaded) {
            loadProducts();
            loadSales();
            dataLoaded = true;
        }
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

    /** Opens the Edit Sale screen (EDITSALE.fxml / EditSaleController) for the chosen row. */
    private void editSale(Map<String, Object> row) {
        editingRow = row;
        switchScreen("EDITSALE.fxml", "Edit Sale");
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
        if (ExitDialogController.show(currentStage())) {
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
        ErrorDialogController.show(currentStage(), header, message);
    }

    private void showError(String header, String message) {
        ErrorDialogController.show(currentStage(), header, message);
    }
}
>>>>>>> 56fe2cdd06299c465f617b8428f3d5c0261d12fa
