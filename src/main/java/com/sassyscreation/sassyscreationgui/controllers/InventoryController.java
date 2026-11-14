package com.sassyscreation.sassyscreationgui.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller for the Product & Inventory screens.
 *
 * Works with BOTH FXML files (the list/table one and the grid/cards one) - fields that
 * only exist in one of them are simply null in the other.
 *
 * "Add product" opens the ADD_PRODUCT.fxml screen (no more built-in popup).
 * AddProductController calls InventoryController.addProduct(...) when you press Save.
 *
 * Dialogs: logout uses ExitDialogController, delete uses DeleteProductController,
 * warnings and errors use ErrorDialogController.
 */
public class InventoryController implements Initializable {

    // ---------------------------------------------------------------
    // FXML file names (inside resources/com/sassyscreation/sassyscreationgui/)
    // ---------------------------------------------------------------
    private static final String FXML_PATH           = "/com/sassyscreation/sassyscreationgui/";
    private static final String FXML_DASHBOARD      = "DASHBOARD.fxml";
    private static final String FXML_ORDERS         = "ORDERS.fxml";
    private static final String FXML_STAND_SALE     = "STANDSALE.fxml";
    private static final String FXML_INVENTORY_LIST = "INVENTORY_LIST.fxml";
    private static final String FXML_INVENTORY_GRID = "INVENTORY_GRID.fxml";   // <-- CHANGE to your grid file name
    private static final String FXML_ADD_PRODUCT    = "ADDPRODUCT.fxml";       // <-- CHANGE if yours is named differently
    private static final String FXML_LOGIN          = "LOGIN.fxml";
    private static final String FXML_ABOUT          = "ABOUT.fxml";
    private static final String FXML_CHANGE_PASSWORD = "CHANGEPASSWORD.fxml";

    // ---------------------------------------------------------------
    // Product model + shared (static) product list
    // ---------------------------------------------------------------
    public static class Product {
        private final int id;
        private String name;
        private String category;
        private double price;
        private int stock;

        public Product(int id, String name, String category, double price, int stock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
    }

    private static final ObservableList<Product> PRODUCTS = FXCollections.observableArrayList();
    private static int nextId = 1;

    static {
        // TEMPORARY seed data - replace with a database / DAO load
        String[][] seed = {
                {"Gypsophila", "Bouquet", "30", "12"},
                {"Light Green Leaf", "Bouquet", "30", "20"},
                {"Spiral Leaf", "Bouquet", "30", "20"},
                {"Dark Green Leaf", "Bouquet", "30", "20"},
                {"Mini Daisy (Filler)", "Bouquet", "30", "20"},
                {"Tulip - Fuzzy Wire", "Bouquet", "55", "15"},
                {"Lily - Fuzzy Wire", "Bouquet", "70", "15"},
                {"Tiger Lily - Fuzzy Wire", "Bouquet", "75", "15"},
                {"Garbera - Fuzzy Wire", "Bouquet", "55", "15"},
                {"Sunflower - Fuzzy Wire", "Bouquet", "55", "15"},
                {"Rose - Fuzzy Wire", "Bouquet", "90", "15"},
                {"Sundrop/Tangled - Fuzzy Wire", "Bouquet", "145", "10"},
                {"Stargazer - Fuzzy Wire", "Bouquet", "160", "10"},
                {"Tulip - Crochet", "Bouquet", "95", "10"},
                {"Sunflower - Crochet", "Bouquet", "120", "10"},
                {"Rose - Crochet", "Bouquet", "100", "10"},
                {"Stargazer - Crochet", "Bouquet", "200", "8"},
                {"Lily - Crochet", "Bouquet", "150", "8"},
                {"Rose - Satin Ribbon", "Bouquet", "60", "12"},
                {"Mixed Bouquet - Fuzzy Wire (Small)", "Bouquet", "250", "5"},
                {"Mixed Bouquet - Fuzzy Wire (Medium)", "Bouquet", "400", "5"},
                {"Mixed Bouquet - Crochet (Small)", "Bouquet", "350", "5"},
                {"Mixed Bouquet - Crochet (Medium)", "Bouquet", "550", "4"},
                {"Gradient Bouquet - Satin Ribbon", "Bouquet", "300", "4"},
                {"Octopus Keychain", "Keychain", "110", "10"},
                {"Jellyfish Keychain", "Keychain", "110", "10"},
                {"Sunflower Keychain", "Keychain", "50", "15"},
                {"Rose Keychain", "Keychain", "50", "5"},
                {"Mini Bouquet Keychain", "Keychain", "110", "10"},
                {"Baby Tulip Keychain", "Keychain", "40", "15"},
                {"Baby Mushroom Keychain", "Keychain", "80", "10"},
                {"Puff Flower Keychain", "Keychain", "45", "10"},
                {"Baby Dinosaur Keychain", "Keychain", "90", "10"},
                {"Mini Teddy Bear Keychain", "Keychain", "90", "10"},
                {"Customized Keychain", "Keychain", "0", "0"},
                {"Mini Tulip Keychain - Fuzzy Wire", "Keychain", "20", "20"},
                {"Mini Daisy Keychain - Fuzzy Wire", "Keychain", "20", "20"},
                {"Ribbon Keychain - Fuzzy Wire", "Keychain", "10", "20"},
                {"Crochet Bra", "Crochet", "200", "5"},
                {"Diamond Top", "Crochet", "300", "4"},
                {"Hair Band", "Crochet", "100", "8"},
                {"Swimwear - Customize", "Crochet", "0", "0"},
                {"Hat - Customize", "Crochet", "0", "0"},
                {"Bag - Customize", "Crochet", "0", "5"},
        };
        for (String[] r : seed) {
            addProduct(r[0], r[1], Double.parseDouble(r[2]), Integer.parseInt(r[3]));
        }
    }

    /** Called by AddProductController when a new product is saved. */
    public static void addProduct(String name, String category, double price, int stock) {
        // TODO: also INSERT into the database
        PRODUCTS.add(new Product(nextId++, name, category, price, stock));
    }

    // ---------------------------------------------------------------
    // FXML fields - sidebar
    // ---------------------------------------------------------------
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

    // ---- header / toolbar ----
    @FXML private Button btnExportInventory;
    @FXML private Button btnAddProduct;
    @FXML private TextField tfProductSearch;
    @FXML private ToggleButton btnViewList;
    @FXML private ToggleButton btnViewGrid;
    @FXML private ToggleGroup tgView;
    @FXML private ToggleGroup tgCat;
    @FXML private ToggleButton chipAll;
    @FXML private ToggleButton chipBouquet;
    @FXML private ToggleButton chipCrochet;
    @FXML private ToggleButton chipKeychain;
    @FXML private Label lblProductTotal;
    @FXML private Label lblCategoryCounts;

    // ---- list view only ----
    @FXML private TableView<Product> tvProducts;
    @FXML private TableColumn<Product, Product> colProdId;
    @FXML private TableColumn<Product, Product> colProdPhoto;
    @FXML private TableColumn<Product, Product> colProdName;
    @FXML private TableColumn<Product, Product> colProdCat;
    @FXML private TableColumn<Product, Product> colProdPrice;
    @FXML private TableColumn<Product, Product> colProdStock;
    @FXML private TableColumn<Product, Product> colProdActions;

    // ---- grid view only ----
    @FXML private ScrollPane spProducts;
    @FXML private GridPane gpProducts;

    // =====================================================================
    // Init
    // =====================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (tvProducts != null) {
            setupTable();
        }

        tfProductSearch.textProperty().addListener((obs, o, n) -> refresh());

        // always keep one category chip selected
        tgCat.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
                return;
            }
            refresh();
        });

        // List / Grid toggle switches between the two FXML screens
        tgView.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) {
                if (oldT != null) {
                    oldT.setSelected(true);
                }
                return;
            }
            if (newT == btnViewGrid && tvProducts != null) {
                switchScreen(FXML_INVENTORY_GRID, "Inventory");
            } else if (newT == btnViewList && gpProducts != null) {
                switchScreen(FXML_INVENTORY_LIST, "Inventory");
            }
        });

        refresh();
    }

    // =====================================================================
    // Table (list view)
    // =====================================================================
    private void setupTable() {
        textColumn(colProdId, p -> String.valueOf(p.getId()));
        graphicColumn(colProdPhoto, p -> photoBox(p, 44, 26, 10));
        textColumn(colProdName, Product::getName);
        textColumn(colProdCat, Product::getCategory);
        textColumn(colProdPrice, p -> peso(p.getPrice()));
        graphicColumn(colProdStock, this::stockLabel);
        graphicColumn(colProdActions, p -> actionsBox(p, 10));
    }

    private void textColumn(TableColumn<Product, Product> col, Function<Product, String> text) {
        col.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setGraphic(null);
                setText(empty || p == null ? null : text.apply(p));
            }
        });
    }

    private void graphicColumn(TableColumn<Product, Product> col, Function<Product, Node> graphic) {
        col.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(null);
                setGraphic(empty || p == null ? null : graphic.apply(p));
            }
        });
    }

    // =====================================================================
    // Refresh (search + category filter + summary)
    // =====================================================================
    private void refresh() {
        List<Product> list = filtered();
        updateSummary();
        if (tvProducts != null) {
            tvProducts.setItems(FXCollections.observableArrayList(list));
        }
        if (gpProducts != null) {
            buildGrid(list);
        }
    }

    private List<Product> filtered() {
        String q = tfProductSearch.getText() == null ? "" : tfProductSearch.getText().trim().toLowerCase();
        String cat = selectedCategory();
        return PRODUCTS.stream()
                .filter(p -> cat.equals("All") || p.getCategory().equals(cat))
                .filter(p -> q.isEmpty()
                        || p.getName().toLowerCase().contains(q)
                        || p.getCategory().toLowerCase().contains(q)
                        || String.valueOf(p.getId()).equals(q))
                .collect(Collectors.toList());
    }

    private String selectedCategory() {
        Toggle t = tgCat.getSelectedToggle();
        return t instanceof ToggleButton tb ? tb.getText() : "All";
    }

    private void updateSummary() {
        long bouquet = PRODUCTS.stream().filter(p -> p.getCategory().equals("Bouquet")).count();
        long crochet = PRODUCTS.stream().filter(p -> p.getCategory().equals("Crochet")).count();
        long keychain = PRODUCTS.stream().filter(p -> p.getCategory().equals("Keychain")).count();
        lblProductTotal.setText("Total Products: " + PRODUCTS.size());
        lblCategoryCounts.setText("Bouquet " + bouquet + "  \u00B7  Crochet " + crochet
                + "  \u00B7  Keychain " + keychain);
    }

    // =====================================================================
    // Grid view
    // =====================================================================
    private void buildGrid(List<Product> list) {
        gpProducts.getChildren().clear();
        int i = 0;
        for (Product p : list) {
            VBox card = buildCard(p);
            GridPane.setHgrow(card, Priority.ALWAYS);
            gpProducts.add(card, i % 5, i / 5);
            i++;
        }
    }

    private VBox buildCard(Product p) {
        StackPane head = new StackPane();
        head.setMinHeight(140);
        head.setPrefHeight(140);
        head.setMaxHeight(140);
        head.setMaxWidth(Double.MAX_VALUE);
        head.setStyle("-fx-background-color: " + bgColor(p.getCategory()) + "; -fx-background-radius: 14 14 0 0;");
        head.getChildren().add(icon(p.getCategory(), 64));

        Label idBadge = new Label("#" + p.getId());
        idBadge.setStyle("-fx-background-color: white; -fx-text-fill: #72243E; -fx-background-radius: 10;"
                + " -fx-padding: 2 8 2 8; -fx-font-size: 11px;");
        StackPane.setAlignment(idBadge, Pos.TOP_LEFT);
        StackPane.setMargin(idBadge, new Insets(8, 0, 0, 8));
        head.getChildren().add(idBadge);

        if (p.getStock() <= 0) {
            Label low = new Label("Low stock");
            low.setStyle("-fx-background-color: #FCEBEB; -fx-text-fill: #A32D2D; -fx-background-radius: 10;"
                    + " -fx-padding: 2 8 2 8; -fx-font-size: 11px;");
            StackPane.setAlignment(low, Pos.TOP_RIGHT);
            StackPane.setMargin(low, new Insets(8, 8, 0, 0));
            head.getChildren().add(low);
        }

        Label name = new Label(p.getName());
        name.setMaxWidth(Double.MAX_VALUE);
        name.setMinHeight(36);
        name.setWrapText(true);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2a0f1c;");

        Label cat = new Label(p.getCategory());
        cat.getStyleClass().add("hint-label");

        Label price = new Label(peso(p.getPrice()));
        price.setStyle("-fx-font-weight: bold; -fx-text-fill: #C22A62; -fx-font-size: 15px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label stockHint = new Label("Stock:");
        stockHint.getStyleClass().add("hint-label");
        HBox priceRow = new HBox(6, price, spacer, stockHint, stockLabel(p));
        priceRow.setAlignment(Pos.CENTER_LEFT);

        HBox actions = actionsBox(p, 8);
        VBox.setMargin(actions, new Insets(4, 0, 0, 0));

        VBox body = new VBox(4, name, cat, priceRow, actions);
        body.setPadding(new Insets(10, 12, 12, 12));

        VBox card = new VBox(head, body);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setUserData(p.getCategory());
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14;"
                + " -fx-border-color: #F4C0D1; -fx-border-radius: 14;");
        return card;
    }

    // =====================================================================
    // Shared UI pieces (photo, stock, action buttons)
    // =====================================================================
    private Node photoBox(Product p, double box, double iconSize, double radius) {
        StackPane sp = new StackPane(icon(p.getCategory(), iconSize));
        sp.setMinSize(box, box);
        sp.setPrefSize(box, box);
        sp.setMaxSize(box, box);
        sp.setStyle("-fx-background-color: " + bgColor(p.getCategory()) + "; -fx-background-radius: " + radius + ";");
        return sp;
    }

    private Label stockLabel(Product p) {
        Label l = new Label(String.valueOf(p.getStock()));
        if (p.getStock() <= 0) {
            l.getStyleClass().add("badge-low");
        } else {
            l.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a0f1c;");
        }
        return l;
    }

    private HBox actionsBox(Product p, double spacing) {
        Button restock = new Button();
        restock.setFocusTraversable(false);
        restock.getStyleClass().add("btn-icon");
        restock.setGraphic(ico("ico-add"));
        restock.setTooltip(new Tooltip("Restock"));
        restock.setOnAction(e -> restockProduct(p));

        Button edit = new Button("Edit");
        edit.setFocusTraversable(false);
        edit.getStyleClass().addAll("btn-icon", "btn-edit");
        edit.setGraphic(ico("ico-edit"));
        edit.setTooltip(new Tooltip("Edit"));
        edit.setOnAction(e -> editProduct(p));

        Button delete = new Button();
        delete.setFocusTraversable(false);
        delete.getStyleClass().addAll("btn-icon", "btn-icon-danger");
        delete.setGraphic(ico("ico-delete"));
        delete.setTooltip(new Tooltip("Delete"));
        delete.setOnAction(e -> deleteProduct(p));

        HBox box = new HBox(spacing, restock, edit, delete);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Region ico(String name) {
        Region r = new Region();
        r.getStyleClass().addAll("ico", name);
        return r;
    }

    // =====================================================================
    // Row / card actions
    // =====================================================================
    private void restockProduct(Product p) {
        TextInputDialog d = new TextInputDialog("1");
        d.setTitle("Restock");
        d.setHeaderText("Restock " + p.getName());
        d.setContentText("Quantity to add:");
        d.initOwner(ownerWindow());
        Optional<String> r = d.showAndWait();
        if (r.isEmpty()) {
            return;
        }
        try {
            int qty = Integer.parseInt(r.get().trim());
            if (qty < 1) {
                throw new NumberFormatException();
            }
            p.stock += qty;
            // TODO: UPDATE the stock in the database
            refresh();
        } catch (NumberFormatException ex) {
            warn("Invalid quantity", "Enter a whole number of 1 or more.");
        }
    }

    private void editProduct(Product p) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit product");
        dialog.setHeaderText("Edit " + p.getName());
        dialog.initOwner(ownerWindow());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField name = new TextField(p.getName());
        ComboBox<String> category = new ComboBox<>(
                FXCollections.observableArrayList("Bouquet", "Crochet", "Keychain"));
        category.setValue(p.getCategory());
        TextField price = new TextField(String.format(Locale.US, "%.2f", p.getPrice()));
        TextField stock = new TextField(String.valueOf(p.getStock()));

        GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(10);
        g.setPadding(new Insets(16));
        g.addRow(0, new Label("Name"), name);
        g.addRow(1, new Label("Category"), category);
        g.addRow(2, new Label("Price (PHP)"), price);
        g.addRow(3, new Label("Stock"), stock);
        dialog.getDialogPane().setContent(g);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        String newName = name.getText() == null ? "" : name.getText().trim();
        if (newName.isEmpty()) {
            warn("Missing name", "Please enter a product name.");
            return;
        }
        double newPrice;
        int newStock;
        try {
            newPrice = Double.parseDouble(price.getText().trim().replace(",", ""));
            if (newPrice < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            warn("Invalid price", "Enter a price of 0 or more (e.g. 55.00).");
            return;
        }
        try {
            newStock = Integer.parseInt(stock.getText().trim());
            if (newStock < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            warn("Invalid stock", "Stock must be a whole number of 0 or more.");
            return;
        }

        p.name = newName;
        p.category = category.getValue() == null ? p.getCategory() : category.getValue();
        p.price = newPrice;
        p.stock = newStock;
        // TODO: UPDATE the product in the database
        refresh();
    }

    private void deleteProduct(Product p) {
        try {
            boolean confirmed = DeleteProductController.show(
                    ownerWindow(), p.getId(), p.getName(), p.getCategory(),
                    peso(p.getPrice()), p.getStock());
            if (confirmed) {
                // TODO: DELETE the product from the database
                PRODUCTS.remove(p);
                refresh();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            error("Could not open delete dialog", rootCause(ex));
        }
    }

    // =====================================================================
    // Header buttons
    // =====================================================================
    @FXML
    private void handleOpenAddProduct() {
        switchScreen(FXML_ADD_PRODUCT, "Add Product");
    }

    @FXML
    private void handleOpenExport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export inventory");
        fc.setInitialFileName("inventory.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"));
        File file = fc.showSaveDialog(ownerWindow());
        if (file == null) {
            return;
        }
        try (PrintWriter w = new PrintWriter(file, StandardCharsets.UTF_8)) {
            w.println("ID,Name,Category,Price,Stock");
            for (Product p : PRODUCTS) {
                w.println(p.getId() + ",\"" + p.getName().replace("\"", "\"\"") + "\","
                        + p.getCategory() + "," + String.format(Locale.US, "%.2f", p.getPrice())
                        + "," + p.getStock());
            }
            info("Export complete", "Saved " + PRODUCTS.size() + " products to:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            error("Export failed", e.getMessage());
        }
    }

    // =====================================================================
    // Navigation (sidebar)
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
        // Custom confirmation dialog (EXITCONFIRM.fxml) instead of the plain JavaFX Alert
        if (ExitDialogController.show(ownerWindow())) {
            // TODO: clear your session / current user here
            currentStage().setMaximized(false);
            switchScreen(FXML_LOGIN, "Login");
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================
    private Window ownerWindow() {
        return navInventory.getScene().getWindow();
    }

    private Stage currentStage() {
        return (Stage) navInventory.getScene().getWindow();
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
            stage.getScene().setRoot(root);
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

    private static String peso(double value) {
        return "\u20B1" + String.format(Locale.US, "%,.2f", value);
    }

    private static String bgColor(String category) {
        return switch (category) {
            case "Keychain" -> "#E3DDF7";
            case "Crochet" -> "#FFE3B3";
            default -> "#F8BBD0";
        };
    }

    // =====================================================================
    // Category icons (drawn in code so new products look the same as old ones)
    // =====================================================================
    private static Pane icon(String category, double s) {
        double k = s / 26.0;
        boolean big = s > 30;

        Pane pane = new Pane();
        pane.setMinSize(s, s);
        pane.setPrefSize(s, s);
        pane.setMaxSize(s, s);
        pane.setMouseTransparent(true);

        switch (category) {
            case "Keychain" -> {
                Color c = Color.web("#5B4BB7");
                double w = big ? 4.8 : 2.5;
                Circle ring = new Circle(7.3 * k, 13 * k, 4.2 * k);
                ring.setFill(Color.TRANSPARENT);
                ring.setStroke(c);
                ring.setStrokeWidth(w);
                pane.getChildren().add(ring);
                double[][] lines = {
                        {11.4, 13, 23.4, 13}, {18.7, 13, 18.7, 17.7}, {22.1, 13, 22.1, 17.7}};
                for (double[] l : lines) {
                    Line ln = new Line(l[0] * k, l[1] * k, l[2] * k, l[3] * k);
                    ln.setStroke(c);
                    ln.setStrokeWidth(w);
                    ln.setStrokeLineCap(StrokeLineCap.ROUND);
                    pane.getChildren().add(ln);
                }
            }
            case "Crochet" -> {
                Color c = Color.web("#8A5A00");
                double w = big ? 3.8 : 2.0;
                Circle ball = new Circle(13 * k, 13 * k, 8.8 * k);
                ball.setFill(Color.TRANSPARENT);
                ball.setStroke(c);
                ball.setStrokeWidth(w);
                pane.getChildren().add(ball);
                for (int i = 0; i < 3; i++) {
                    Arc arc = new Arc(13 * k, 13 * k, 8.8 * k, 4.2 * k, 0, 180);
                    arc.setType(ArcType.OPEN);
                    arc.setFill(Color.TRANSPARENT);
                    arc.setStroke(c);
                    arc.setStrokeWidth(w);
                    arc.setRotate(i * 60);
                    pane.getChildren().add(arc);
                }
                Line needle = new Line(20.3 * k, 19.8 * k, 25.0 * k, 24.2 * k);
                needle.setStroke(c);
                needle.setStrokeWidth(w);
                needle.setStrokeLineCap(StrokeLineCap.ROUND);
                pane.getChildren().add(needle);
            }
            default -> {
                Color petal = Color.web("#C22A62");
                for (int i = 0; i < 6; i++) {
                    double a = Math.toRadians(i * 60);
                    Ellipse e = new Ellipse(s / 2 + 6.1 * k * Math.cos(a), s / 2 + 6.1 * k * Math.sin(a),
                            5.7 * k, 3.8 * k);
                    e.setRotate(i * 60);
                    e.setFill(petal);
                    pane.getChildren().add(e);
                }
                pane.getChildren().add(new Circle(s / 2, s / 2, 2.9 * k, Color.web("#FFE08A")));
            }
        }
        return pane;
    }

    // =====================================================================
    // Messages
    // =====================================================================

    /** Warnings and errors use the custom error dialog (ERRORDIALOG.fxml). */
    private void warn(String header, String msg) {
        ErrorDialogController.show(ownerWindow(), header, msg);
    }

    private void error(String header, String msg) {
        ErrorDialogController.show(ownerWindow(), header, msg);
    }

    /** Plain information message (for example "Export complete"). */
    private void info(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Inventory");
        a.setHeaderText(header);
        a.setContentText(msg);
        a.initOwner(ownerWindow());
        a.showAndWait();
    }
}