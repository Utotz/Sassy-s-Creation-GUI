package com.sassyscreation.sassyscreationgui.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Controller for the Product & Inventory screens.
 *
 * Works with BOTH FXML files (the list/table one and the grid/cards one) - fields that
 * only exist in one of them are simply null in the other.
 *
 * "Add product" opens the ADDPRODUCT.fxml screen. AddProductController calls
 * InventoryController.addProduct(...) when you press Save.
 */
public class InventoryController implements Initializable {

    // ---------------------------------------------------------------
    // FXML file names (inside resources/com/sassyscreation/sassyscreationgui/)
    // ---------------------------------------------------------------
    private static final String FXML_PATH            = "/com/sassyscreation/sassyscreationgui/";
    private static final String FXML_DASHBOARD       = "DASHBOARD.fxml";
    private static final String FXML_ORDERS          = "ORDERS.fxml";
    private static final String FXML_STAND_SALE      = "STANDSALE.fxml";
    private static final String FXML_INVENTORY_LIST  = "INVENTORY_LIST.fxml";
    private static final String FXML_INVENTORY_GRID  = "INVENTORY_GRID.fxml";   // <-- CHANGE to your grid file name
    private static final String FXML_ADD_PRODUCT     = "ADDPRODUCT.fxml";       // <-- CHANGE if yours is named differently
    private static final String FXML_LOGIN           = "LOGIN.fxml";
    private static final String FXML_ABOUT           = "ABOUT.fxml";
    private static final String FXML_CHANGE_PASSWORD = "CHANGEPASSWORD.fxml";

    /** Number of card columns in the grid view (matches the FXML column constraints). */
    private static final int GRID_COLUMNS = 3;

    /** Stock at or below this shows an orange "N left" badge. */
    private static final int LOW_STOCK = 5;

    private static final String ACCENT = "#C22A62";

    // ---------------------------------------------------------------
    // Product model + shared (static) product list
    // ---------------------------------------------------------------
    public static class Product {
        private final int id;
        private String name;
        private String category;
        private double price;
        private int stock;
        /** CSS class that supplies the grid photo (e.g. "pic-diamond-top"); null = draw a category icon. */
        private final String photoClass;

        public Product(int id, String name, String category, double price, int stock, String photoClass) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
            this.photoClass = photoClass;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public String getPhotoClass() { return photoClass; }
    }

    private static final ObservableList<Product> PRODUCTS = FXCollections.observableArrayList();
    private static int nextId = 1;

    static {
        // TEMPORARY seed data (matches the design) - replace with a database / DAO load
        // {name, category, price, stock, photo css class}
        String[][] seed = {
                {"Little Blossom Serenade - Fuzzy Wire", "Bouquet", "100", "10", "pic-little-blossom-serenade"},
                {"Single Lily / Tulip - Fuzzy Wire", "Bouquet", "85", "10", "pic-single-lily-tulip"},
                {"Mini Sunflower - Fuzzy Wire", "Bouquet", "35", "10", "pic-mini-sunflower-35"},
                {"Mini Sunflower 2 - Fuzzy Wire", "Bouquet", "50", "10", "pic-mini-sunflower-50"},
                {"Mini Bouquet - Fuzzy Wire", "Bouquet", "55", "10", "pic-mini-bouquet-fuzzy-wire"},
                {"Sunflower Bouquet - Fuzzy Wire", "Bouquet", "150", "10", "pic-sunflower-bouquet"},
                {"Sunblush Bouquet - Fuzzy Wire", "Bouquet", "155", "10", "pic-sunblush-bouquet"},
                {"Rosa Bouquet - Fuzzy Wire", "Bouquet", "155", "10", "pic-rosa-bouquet"},
                {"Tangled - Fuzzy Wire", "Bouquet", "180", "10", "pic-tangled"},
                {"Tangled Grand - Fuzzy Wire", "Bouquet", "1000", "10", "pic-tangled-grand"},
                {"Blooming Grace Bouquet - Fuzzy Wire", "Bouquet", "280", "10", "pic-blooming-grace-bouquet"},
                {"Single Stargazer Bouquet - Fuzzy Wire", "Bouquet", "300", "10", "pic-single-stargazer-bouquet"},
                {"Single Stargazer Bouquet Version 2 - Fuzzy Wire", "Bouquet", "450", "10", "pic-single-stargazer-v2"},
                {"Flor\u00E8a Bouquet - Fuzzy Wire", "Bouquet", "350", "10", "pic-florea-bouquet"},
                {"Flor\u00E8a Bouquet Version 2 - Fuzzy Wire", "Bouquet", "500", "10", "pic-florea-bouquet-v2"},
                {"Flor\u00E8a Bouquet Version 3 - Fuzzy Wire", "Bouquet", "650", "10", "pic-florea-bouquet-v3"},
                {"Stargazer Embrace Bouquet - Fuzzy Wire", "Bouquet", "650", "10", "pic-stargazer-embrace-bouquet"},
                {"Thumbelina Cone Bouquet - Fuzzy Wire", "Bouquet", "500", "10", "pic-thumbelina-cone-bouquet"},
                {"Big Rose Bouquet - Fuzzy Wire", "Bouquet", "580", "10", "pic-big-rose-bouquet"},
                {"Coffee Bouquet - Fuzzy Wire", "Bouquet", "300", "10", "pic-coffee-bouquet"},
                {"Red Tulip Bouquet - Fuzzy Wire", "Bouquet", "300", "10", "pic-red-tulip-bouquet"},
                {"Set Gift - Crochet", "Bouquet", "300", "10", "pic-set-gift"},
                {"Tulip - Crochet", "Bouquet", "135", "10", "pic-crochet-tulip-bouquet"},
                {"Single Tulip - Crochet", "Bouquet", "150", "10", "pic-crochet-single-tulip"},
                {"Sophia - Crochet", "Bouquet", "750", "10", "pic-sophia-bouquet"},
                {"Rose Serenade - Crochet", "Bouquet", "250", "10", "pic-rose-serenade"},
                {"Single Tulip - Satin", "Bouquet", "100", "10", "pic-satin-single-tulip"},
                {"Rosette Charm - Satin Ribbon", "Bouquet", "300", "10", "pic-rosette-charm"},
                {"Jelly First Keychain", "Keychain", "100", "10", "pic-jelly-first-keychain"},
                {"Mini Bouquet Keychain", "Keychain", "100", "10", "pic-mini-bouquet-keychain"},
                {"Puff Flower in a Pouch Keychain", "Keychain", "40", "10", "pic-puff-flower-pouch"},
                {"Mini Flower in a Pouch Keychain", "Keychain", "25", "10", "pic-mini-flower-pouch"},
                {"Mini Labubu Keychain", "Keychain", "85", "10", "pic-mini-labubu"},
                {"Moana Inspired Keychain", "Keychain", "200", "10", "pic-moana-inspired-keychain"},
                {"Frog Headband", "Crochet", "80", "10", "pic-frog-headband"},
                {"Thumbler Holder", "Crochet", "250", "10", "pic-thumbler-holder"},
                {"Diamond Top", "Crochet", "200", "4", "pic-diamond-top"},
                {"Swim Bra", "Crochet", "200", "10", "pic-swim-bra"},
                {"Mini Tulip in a Card", "Crochet", "70", "10", "pic-mini-tulip-in-card"},
                {"Tulip Pot - Crochet", "Crochet", "200", "10", "pic-crochet-tulip-pot"},
                {"Sunflower Pot - Crochet", "Crochet", "200", "10", "pic-crochet-sunflower-pot"},
                {"Cactus Pot - Crochet", "Crochet", "200", "10", "pic-crochet-cactus-pot"},
                {"Pot Set (Tulip, Sunflower, Cactus) - Crochet", "Crochet", "200", "10", "pic-crochet-pot-set"},
        };
        for (String[] r : seed) {
            PRODUCTS.add(new Product(nextId++, r[0], r[1], Double.parseDouble(r[2]), Integer.parseInt(r[3]), r[4]));
        }
    }

    /** Called by AddProductController when a new product is saved. */
    public static void addProduct(String name, String category, double price, int stock) {
        // TODO: also INSERT into the database
        PRODUCTS.add(new Product(nextId++, name, category, price, stock, null));
    }

    /** Filtered view of PRODUCTS used by both the table and the grid. */
    private final FilteredList<Product> filteredProducts = new FilteredList<>(PRODUCTS, p -> true);

    // ---------------------------------------------------------------
    // FXML fields - sidebar
    // ---------------------------------------------------------------
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Label lblNavOrdersBadge;
    @FXML private Button navStandSale;
    @FXML private Button navInventory;
    @FXML private Button btnLogout;

    // ---- account menu ----
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
    /** Optional: add fx:id="lblInventorySub" to the "... · 43 products" subtitle label to keep its count live. */
    @FXML private Label lblInventorySub;

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
        graphicColumn(colProdPhoto, this::listTile);
        textColumn(colProdName, Product::getName);
        textColumn(colProdCat, Product::getCategory);
        textColumn(colProdPrice, p -> peso(p.getPrice()));
        graphicColumn(colProdStock, p -> stockBadge(p, "stock-plain"));
        graphicColumn(colProdActions, this::listActions);

        // Cells hold the whole Product, so tell the columns how to sort
        colProdId.setComparator(Comparator.comparingInt(Product::getId));
        colProdCat.setComparator(Comparator.comparing(Product::getCategory));
        colProdPrice.setComparator(Comparator.comparingDouble(Product::getPrice));
        colProdStock.setComparator(Comparator.comparingInt(Product::getStock));

        SortedList<Product> sorted = new SortedList<>(filteredProducts);
        sorted.comparatorProperty().bind(tvProducts.comparatorProperty());
        tvProducts.setItems(sorted);
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

    /** 44x44 tile with the category line-icon (list view photo column). */
    private Node listTile(Product p) {
        StackPane tile = new StackPane(glyph(p.getCategory(), 1.1));
        tile.getStyleClass().add("prod-tile");
        tile.setMinSize(44, 44);
        tile.setPrefSize(44, 44);
        tile.setMaxSize(44, 44);
        return tile;
    }

    /** "Edit · Restock" links (list view actions column). */
    private Node listActions(Product p) {
        Button edit = button("Edit", "ico-edit", "Edit", "btn-icon", "btn-edit", "link-pink");
        edit.setOnAction(e -> editProduct(p));

        Label sep = new Label("\u00B7");
        sep.getStyleClass().add("link-sep");

        Button restock = button("Restock", null, "Restock", "btn-icon", "link-pink");
        restock.setOnAction(e -> restockProduct(p));

        HBox box = new HBox(4, edit, sep, restock);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // =====================================================================
    // Refresh (search + category filter + summary)
    // =====================================================================
    private void refresh() {
        String q = tfProductSearch.getText() == null ? "" : tfProductSearch.getText().trim().toLowerCase();
        String cat = selectedCategory();

        // a new predicate every time forces a re-filter (also needed after in-place edits)
        filteredProducts.setPredicate(p ->
                (cat.equals("All") || p.getCategory().equals(cat))
                        && (q.isEmpty()
                        || p.getName().toLowerCase().contains(q)
                        || p.getCategory().toLowerCase().contains(q)
                        || String.valueOf(p.getId()).equals(q)));

        updateSummary();
        if (tvProducts != null) {
            tvProducts.refresh();
        }
        if (gpProducts != null) {
            buildGrid();
        }
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

        // list view chips carry a count label as their graphic (the grid chips don't)
        setChipCount(chipAll, PRODUCTS.size());
        setChipCount(chipBouquet, bouquet);
        setChipCount(chipCrochet, crochet);
        setChipCount(chipKeychain, keychain);

        // keep the "... · N products" subtitle in step (needs fx:id="lblInventorySub")
        if (lblInventorySub != null) {
            String t = lblInventorySub.getText() == null ? "" : lblInventorySub.getText();
            int i = t.indexOf(" \u00B7 ");
            String prefix = i < 0 ? t : t.substring(0, i);
            lblInventorySub.setText(prefix + " \u00B7 " + PRODUCTS.size() + " products");
        }
    }

    private void setChipCount(ToggleButton chip, long count) {
        if (chip != null && chip.getGraphic() instanceof Label l) {
            l.setText(String.valueOf(count));
        }
    }

    // =====================================================================
    // Grid view
    // =====================================================================
    private void buildGrid() {
        gpProducts.getChildren().clear();

        if (filteredProducts.isEmpty()) {
            Label empty = new Label("No products found");
            empty.getStyleClass().add("page-sub");
            GridPane.setColumnSpan(empty, GRID_COLUMNS);
            gpProducts.add(empty, 0, 0);
            return;
        }

        int i = 0;
        for (Product p : filteredProducts) {
            VBox card = buildCard(p);
            GridPane.setHgrow(card, Priority.ALWAYS);
            gpProducts.add(card, i % GRID_COLUMNS, i / GRID_COLUMNS);
            i++;
        }
    }

    private VBox buildCard(Product p) {
        // photo: CSS class from the design, or a category icon for products without one
        StackPane photo = new StackPane();
        photo.getStyleClass().add("prod-photo");
        photo.setMinHeight(140);
        photo.setPrefHeight(140);
        photo.setMaxHeight(140);
        photo.setMaxWidth(Double.MAX_VALUE);
        if (p.getPhotoClass() != null) {
            photo.getStyleClass().add(p.getPhotoClass());
        } else {
            photo.setStyle("-fx-background-color: " + bgColor(p.getCategory()) + ";");
            photo.getChildren().add(glyph(p.getCategory(), 2.6));
        }

        Label name = new Label(p.getName());
        name.getStyleClass().add("prod-name");
        name.setMaxWidth(Double.MAX_VALUE);
        name.setWrapText(true);

        Label category = new Label(p.getCategory());
        category.getStyleClass().add("page-sub");

        Label price = new Label(peso(p.getPrice()));
        price.getStyleClass().add("prod-price");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox priceRow = new HBox(6, price, spacer, stockBadge(p, "badge-done"));
        priceRow.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(priceRow, new Insets(2, 0, 0, 0));

        Button edit = button("Edit", "ico-edit", "Edit", "btn-ghost", "prod-btn", "prod-btn-edit");
        edit.setOnAction(e -> editProduct(p));
        Button restock = button("Restock", null, "Restock", "btn-ghost", "prod-btn");
        restock.setOnAction(e -> restockProduct(p));
        Button delete = button(null, "ico-delete", "Delete", "btn-ghost", "prod-btn", "prod-btn-del");
        delete.setOnAction(e -> deleteProduct(p));

        HBox actions = new HBox(6, edit, restock, delete);
        actions.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(actions, new Insets(6, 0, 0, 0));

        VBox body = new VBox(3, name, category, priceRow, actions);
        body.setPadding(new Insets(10, 12, 12, 12));

        VBox card = new VBox(photo, body);
        card.getStyleClass().add("prod-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setUserData(p.getCategory());
        return card;
    }

    // =====================================================================
    // Shared UI pieces
    // =====================================================================

    /** "10 in stock" / "4 left" / "Out of stock" badge. */
    private Label stockBadge(Product p, String normalClass) {
        Label l;
        if (p.getStock() <= 0) {
            l = new Label("Out of stock");
            l.getStyleClass().add("badge-cancelled");
        } else if (p.getStock() <= LOW_STOCK) {
            l = new Label(p.getStock() + " left");
            l.getStyleClass().add("badge-pending");
        } else {
            l = new Label(p.getStock() + " in stock");
            l.getStyleClass().add(normalClass);
        }
        return l;
    }

    private Button button(String text, String icon, String tooltip, String... styleClasses) {
        Button b = new Button();
        if (text != null) {
            b.setText(text);
        }
        b.setFocusTraversable(false);
        b.setMnemonicParsing(false);
        b.getStyleClass().addAll(styleClasses);
        if (icon != null) {
            b.setGraphic(ico(icon));
        }
        b.setTooltip(new Tooltip(tooltip));
        return b;
    }

    private Region ico(String name) {
        Region r = new Region();
        r.getStyleClass().addAll("ico", name);
        return r;
    }

    private static String bgColor(String category) {
        return switch (category) {
            case "Keychain" -> "#E3DDF7";
            case "Crochet" -> "#FFE3B3";
            default -> "#F8BBD0";
        };
    }

    // =====================================================================
    // Category line-icons (same shapes as the list design, drawn in code)
    // =====================================================================
    private static Pane glyph(String category, double scale) {
        Pane pane = new Pane();
        pane.setMinSize(24, 24);
        pane.setPrefSize(24, 24);
        pane.setMaxSize(24, 24);
        pane.setMouseTransparent(true);
        pane.setScaleX(scale);
        pane.setScaleY(scale);

        switch (category) {
            case "Keychain" -> pane.getChildren().addAll(
                    path("M7 11a5 5 0 0 1 10 0v2"),
                    path("M7 13v4M10 13v5M14 13v5M17 13v4"));
            case "Crochet" -> pane.getChildren().addAll(
                    path("M4 16a8 8 0 0 1 16 0z"),
                    path("M2 16h20"));
            default -> pane.getChildren().addAll(
                    circle(12, 12, 2),
                    circle(12, 6, 2.5),
                    circle(17.5, 10, 2.5),
                    circle(15.5, 16.5, 2.5),
                    circle(8.5, 16.5, 2.5),
                    circle(6.5, 10, 2.5));
        }
        return pane;
    }

    private static Shape outlined(Shape s) {
        s.setFill(Color.TRANSPARENT);
        s.setStroke(Color.web(ACCENT));
        s.setStrokeWidth(1.7);
        s.setStrokeLineCap(StrokeLineCap.ROUND);
        s.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return s;
    }

    private static Shape path(String content) {
        SVGPath sp = new SVGPath();
        sp.setContent(content);
        return outlined(sp);
    }

    private static Shape circle(double cx, double cy, double r) {
        return outlined(new Circle(cx, cy, r));
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

        // The list design has no delete button, so deleting lives here too
        ButtonType deleteType = new ButtonType("Delete product", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, deleteType);

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
        if (result.isEmpty()) {
            return;
        }
        if (result.get() == deleteType) {
            deleteProduct(p);
            return;
        }
        if (result.get() != ButtonType.OK) {
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

    // =====================================================================
    // Alerts
    // =====================================================================
    private void warn(String header, String msg) { alert(Alert.AlertType.WARNING, "Inventory", header, msg); }
    private void info(String header, String msg) { alert(Alert.AlertType.INFORMATION, "Inventory", header, msg); }
    private void error(String header, String msg) { alert(Alert.AlertType.ERROR, "Error", header, msg); }

    private void alert(Alert.AlertType type, String title, String header, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(msg == null ? "Unknown error. Check the console for details." : msg);
        a.initOwner(ownerWindow());
        a.showAndWait();
    }
}