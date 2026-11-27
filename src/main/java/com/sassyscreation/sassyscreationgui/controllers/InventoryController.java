package com.sassyscreation.sassyscreationgui.controllers;

import com.sassyscreation.sassyscreationgui.dao.ProductDAO;
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
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Locale;
import java.util.OptionalInt;
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
 *
 * "Edit" opens the EDITPRODUCT.fxml screen. EditProductController calls
 * InventoryController.updateProduct(...) when you press Save.
 *
 * "Restock" opens the RESTOCK.fxml popup (RestockController) and saves the added
 * quantity to the database.
 */
public class InventoryController implements Initializable {

    // ---------------------------------------------------------------
    // FXML file names (inside resources/com/sassyscreation/sassyscreationgui/)
    // ---------------------------------------------------------------
    private static final String FXML_PATH            = "/com/sassyscreation/sassyscreationgui/";
    private static final String IMAGE_PATH           = FXML_PATH + "images/";
    private static final String FXML_INVENTORY_LIST  = "INVENTORY_LIST.fxml";
    private static final String FXML_INVENTORY_GRID  = "INVENTORY_GRID.fxml";
    private static final String FXML_ADD_PRODUCT     = "ADDPRODUCT.fxml";

    /** Number of card columns in the grid view (matches the FXML column constraints). */
    private static final int GRID_COLUMNS = 3;

    /** Stock at or below this shows an orange "N left" badge. */
    private static final int LOW_STOCK = 5;

    /** Every stock badge (green / orange / red) uses this same width. */
    private static final double BADGE_WIDTH = 116;

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
        /** CSS class that supplies the grid photo (e.g. "pic-diamond-top"); null = use imagePath / icon. */
        private final String photoClass;
        /** From the database: image file name or full path; null = no picture. */
        private final String imagePath;

        public Product(int id, String name, String category, double price, int stock,
                       String photoClass, String imagePath) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
            this.photoClass = photoClass;
            this.imagePath = imagePath;
        }

        public String getImagePath() { return imagePath; }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public String getPhotoClass() { return photoClass; }
    }

    private static final ObservableList<Product> PRODUCTS = FXCollections.observableArrayList();
    private static final ProductDAO productDAO = new ProductDAO();

    public static void loadProductsFromDB() {
        PRODUCTS.clear();
        try {
            for (var dbProd : productDAO.getAllProducts()) {
                PRODUCTS.add(new Product(
                        dbProd.getProductId(),
                        dbProd.getName(),
                        dbProd.getCategory(),
                        dbProd.getPrice().doubleValue(),
                        dbProd.getStockQuantity(),
                        null,
                        dbProd.getImagePath()
                ));
            }
            PRODUCTS.sort(Comparator.comparingInt(Product::getId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by AddProductController when a new product is saved.
     * Inserts it into the database and reloads the list.
     *
     * @return true if it was saved
     */
    public static boolean addProduct(String name, String category, double price, int stock, String imagePath) {
        try {
            productDAO.insertProduct(name, category, BigDecimal.valueOf(price), stock, imagePath);
            loadProductsFromDB();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Called by EditProductController when an edited product is saved.
     * Replaces the product in the list (matched by id).
     *
     * @return true if the product was found
     */
    public static boolean updateProduct(int id, String name, String category, double price,
                                        int stock, String imagePath) {
        for (int i = 0; i < PRODUCTS.size(); i++) {
            Product old = PRODUCTS.get(i);
            if (old.getId() == id) {
                // TODO: UPDATE the product in the database
                PRODUCTS.set(i, new Product(id, name, category, price, stock, old.getPhotoClass(), imagePath));
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Product pictures (products.image_path)
    // ---------------------------------------------------------------

    /**
     * Turns a database image_path into a URL the stylesheet can load, or null if there is
     * no picture (empty path, or the file is not found).
     */
    private static String imageUrl(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        File f = new File(path);
        if (f.isAbsolute() && f.isFile()) {
            return f.toURI().toString();                         // photo added by the user
        }
        URL res = InventoryController.class.getResource(IMAGE_PATH + path.trim());
        System.out.println("IMG " + path + " -> " + (res == null ? "NOT FOUND" : res.toExternalForm()));
        return res == null ? null : res.toExternalForm();        // bundled image
    }

    /** Inline CSS that fills a node with the picture, cropped to cover. */
    private static String photoStyle(String url, int radius) {
        return "-fx-background-image: url('" + url.replace("'", "%27") + "');"
                + "-fx-background-size: cover;"
                + "-fx-background-position: center center;"
                + "-fx-background-repeat: no-repeat;"
                + "-fx-background-radius: " + radius + ";";
    }

    /** Filtered view of PRODUCTS used by both the table and the grid. */
    private final FilteredList<Product> filteredProducts = new FilteredList<>(PRODUCTS, p -> true);

    // ---------------------------------------------------------------
    // FXML fields
    // ---------------------------------------------------------------

    /** Set by <fx:include fx:id="sidebar"> in the FXML files. */
    @FXML private SidebarController sidebarController;

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
        sidebarController.setActive("inventory");

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

        loadProductsFromDB();
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
        graphicColumn(colProdStock, this::stockBadge);
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

    /** 44x44 tile: the product picture, or the category line-icon when there is none. */
    private Node listTile(Product p) {
        StackPane tile = new StackPane();
        tile.getStyleClass().add("prod-tile");
        String url = imageUrl(p.getImagePath());
        if (url != null) {
            tile.setStyle(photoStyle(url, 10));
        } else {
            tile.getChildren().add(glyph(p.getCategory(), 1.1));
        }
        tile.setMinSize(44, 44);
        tile.setPrefSize(44, 44);
        tile.setMaxSize(44, 44);
        return tile;
    }

    /** "Edit · Restock · Delete" links (list view actions column). */
    private Node listActions(Product p) {
        Button edit = button("Edit", "ico-edit", "Edit", "btn-icon", "btn-edit", "link-pink");
        edit.setOnAction(e -> editProduct(p));

        Label sep1 = new Label("\u00B7");
        sep1.getStyleClass().add("link-sep");

        Button restock = button("Restock", null, "Restock", "btn-icon", "link-pink");
        restock.setOnAction(e -> restockProduct(p));

        Label sep2 = new Label("\u00B7");
        sep2.getStyleClass().add("link-sep");

        Button delete = button("Delete", "ico-delete", "Delete", "btn-icon", "link-pink");
        delete.setOnAction(e -> deleteProduct(p));

        HBox box = new HBox(4, edit, sep1, restock, sep2, delete);
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
        String url = imageUrl(p.getImagePath());
        if (url != null) {
            photo.setStyle(photoStyle(url, 0));
        } else if (p.getPhotoClass() != null) {
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
        HBox priceRow = new HBox(6, price, spacer, stockBadge(p));
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

    /** "10 in stock" (green) / "4 left" (orange) / "Out of stock" (red) badge, all the same size. */
    private Label stockBadge(Product p) {
        Label l;
        if (p.getStock() <= 0) {
            l = new Label("Out of stock");
            l.getStyleClass().add("badge-cancelled");
        } else if (p.getStock() <= LOW_STOCK) {
            l = new Label(p.getStock() + " left");
            l.getStyleClass().add("badge-pending");
        } else {
            l = new Label(p.getStock() + " in stock");
            l.getStyleClass().add("badge-pending");   // same shape and padding as the other badges
            l.setStyle("-fx-background-color: #DDF3E4; -fx-text-fill: #1F7A3E;");   // turned green
        }
        l.setAlignment(Pos.CENTER);
        l.setMinWidth(BADGE_WIDTH);
        l.setPrefWidth(BADGE_WIDTH);
        l.setMaxWidth(BADGE_WIDTH);
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

    /** Opens the Restock popup (RESTOCK.fxml) and saves the added quantity to the database. */
    private void restockProduct(Product p) {
        OptionalInt qty = RestockController.show(ownerWindow(), p.getName(), p.getStock());
        if (qty.isEmpty()) {
            return;                                   // cancelled
        }
        try {
            productDAO.addStock(p.getId(), qty.getAsInt());
            loadProductsFromDB();                     // reload so the list shows the saved stock
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            error("Could not restock", rootCause(ex));
        }
    }

    /** Opens the Edit product screen (EDITPRODUCT.fxml) for this product. */
    private void editProduct(Product p) {
        EditProductController.open(currentStage(), p);
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
    // Helpers
    // =====================================================================
    private Window ownerWindow() {
        return tfProductSearch.getScene().getWindow();
    }

    private Stage currentStage() {
        return (Stage) tfProductSearch.getScene().getWindow();
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
            ScreenTransition.show(stage.getScene(), root);
            stage.setTitle("Sassy's Creation - " + title);
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