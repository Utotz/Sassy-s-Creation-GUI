package com.sassyscreation.sassyscreationgui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * One controller for BOTH inventory screens:
 *   INVENTORY_LIST.fxml (table view) and INVENTORY_GRID.fxml (card view).
 * Fields that don't exist in the loaded FXML simply stay null.
 */
public class InventoryController implements Initializable {

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

    // ---- header buttons ----
    @FXML private Button btnExportInventory;
    @FXML private Button btnAddProduct;

    // ---- search, view toggle, category chips ----
    @FXML private TextField tfProductSearch;
    @FXML private ToggleButton btnViewList;
    @FXML private ToggleButton btnViewGrid;
    @FXML private ToggleGroup tgView;
    @FXML private ToggleButton chipAll;
    @FXML private ToggleButton chipBouquet;
    @FXML private ToggleButton chipCrochet;
    @FXML private ToggleButton chipKeychain;
    @FXML private ToggleGroup tgCat;

    // ---- summary bar ----
    @FXML private Label lblProductTotal;
    @FXML private Label lblCategoryCounts;

    // ---- GRID screen only ----
    @FXML private ScrollPane spProducts;
    @FXML private GridPane gpProducts;

    // ---- LIST screen only ----
    @FXML private TableView<Map<String, Object>> tvProducts;
    @FXML private TableColumn<Map<String, Object>, Object> colProdId;
    @FXML private TableColumn<Map<String, Object>, Object> colProdPhoto;
    @FXML private TableColumn<Map<String, Object>, Object> colProdName;
    @FXML private TableColumn<Map<String, Object>, Object> colProdCat;
    @FXML private TableColumn<Map<String, Object>, Object> colProdPrice;
    @FXML private TableColumn<Map<String, Object>, Object> colProdStock;
    @FXML private TableColumn<Map<String, Object>, Object> colProdActions;

    // ---- settings ----
    private static final int GRID_COLUMNS = 5;
    private static final int LOW_STOCK_LEVEL = 0;   // stock at or below this shows as low / out of stock

    // Shared by both screens so edits survive switching between List and Grid.
    private static final ObservableList<Map<String, Object>> ALL_PRODUCTS = FXCollections.observableArrayList();
    private static String lastSearch = "";
    private static String lastCategory = "All";

    private FilteredList<Map<String, Object>> filtered;
    private boolean gridMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridMode = gpProducts != null;

        if (ALL_PRODUCTS.isEmpty()) {
            loadProducts();
        }

        filtered = new FilteredList<>(ALL_PRODUCTS, p -> true);

        if (tvProducts != null) {
            setUpTable();
        }

        // Restore the search text and category chip from the previous screen
        tfProductSearch.setText(lastSearch);
        selectChip(lastCategory);

        setUpListeners();
        applyFilters();
        updateSummary();
    }

    // =====================================================================
    //  Data
    // =====================================================================

    /**
     * TEMPORARY: sample products that match the design.
     * Replace with a database query (SELECT from your products table).
     */
    private void loadProducts() {
        add(1, "Gypsophila", "Bouquet", 30, 12);
        add(2, "Light Green Leaf", "Bouquet", 30, 20);
        add(3, "Spiral Leaf", "Bouquet", 30, 20);
        add(4, "Dark Green Leaf", "Bouquet", 30, 20);
        add(5, "Mini Daisy (Filler)", "Bouquet", 30, 20);
        add(6, "Tulip - Fuzzy Wire", "Bouquet", 55, 15);
        add(7, "Lily - Fuzzy Wire", "Bouquet", 70, 15);
        add(8, "Tiger Lily - Fuzzy Wire", "Bouquet", 75, 15);
        add(9, "Garbera - Fuzzy Wire", "Bouquet", 55, 15);
        add(10, "Sunflower - Fuzzy Wire", "Bouquet", 55, 15);
        add(11, "Rose - Fuzzy Wire", "Bouquet", 90, 15);
        add(12, "Sundrop/Tangled - Fuzzy Wire", "Bouquet", 145, 10);
        add(13, "Stargazer - Fuzzy Wire", "Bouquet", 160, 10);
        add(14, "Tulip - Crochet", "Bouquet", 95, 10);
        add(15, "Sunflower - Crochet", "Bouquet", 120, 10);
        add(16, "Rose - Crochet", "Bouquet", 100, 10);
        add(17, "Stargazer - Crochet", "Bouquet", 200, 8);
        add(18, "Lily - Crochet", "Bouquet", 150, 8);
        add(19, "Rose - Satin Ribbon", "Bouquet", 60, 12);
        add(20, "Mixed Bouquet - Fuzzy Wire (Small)", "Bouquet", 250, 5);
        add(21, "Mixed Bouquet - Fuzzy Wire (Medium)", "Bouquet", 400, 5);
        add(22, "Mixed Bouquet - Crochet (Small)", "Bouquet", 350, 5);
        add(23, "Mixed Bouquet - Crochet (Medium)", "Bouquet", 550, 4);
        add(24, "Gradient Bouquet - Satin Ribbon", "Bouquet", 300, 4);
        add(25, "Octopus Keychain", "Keychain", 110, 10);
        add(26, "Jellyfish Keychain", "Keychain", 110, 10);
        add(27, "Sunflower Keychain", "Keychain", 50, 15);
        add(28, "Rose Keychain", "Keychain", 50, 5);
        add(29, "Mini Bouquet Keychain", "Keychain", 110, 10);
        add(30, "Baby Tulip Keychain", "Keychain", 40, 15);
        add(31, "Baby Mushroom Keychain", "Keychain", 80, 10);
        add(32, "Puff Flower Keychain", "Keychain", 45, 10);
        add(33, "Baby Dinosaur Keychain", "Keychain", 90, 10);
        add(34, "Mini Teddy Bear Keychain", "Keychain", 90, 10);
        add(35, "Customized Keychain", "Keychain", 0, 0);
        add(36, "Mini Tulip Keychain - Fuzzy Wire", "Keychain", 20, 20);
        add(37, "Mini Daisy Keychain - Fuzzy Wire", "Keychain", 20, 20);
        add(38, "Ribbon Keychain - Fuzzy Wire", "Keychain", 10, 20);
        add(39, "Crochet Bra", "Crochet", 200, 5);
        add(40, "Diamond Top", "Crochet", 300, 4);
        add(41, "Hair Band", "Crochet", 100, 8);
        add(42, "Swimwear - Customize", "Crochet", 0, 0);
        add(43, "Hat - Customize", "Crochet", 0, 0);
        add(44, "Bag - Customize", "Crochet", 0, 5);
    }

    private void add(int id, String name, String category, double price, int stock) {
        ALL_PRODUCTS.add(product(id, name, category, price, stock));
    }

    // Keys must match the MapValueFactory keys in INVENTORY_LIST.fxml (case-sensitive)
    private static Map<String, Object> product(int id, String name, String category, double price, int stock) {
        Map<String, Object> m = new HashMap<>();
        m.put("ID", id);
        m.put("Name", name);
        m.put("Category", category);
        m.put("Price", price);
        m.put("stock", stock);
        m.put("photo", "photo");       // placeholders so those columns have a value
        m.put("actions", "actions");
        return m;
    }

    // =====================================================================
    //  Filters, toggles, summary
    // =====================================================================

    private void selectChip(String category) {
        switch (category) {
            case "Bouquet" -> chipBouquet.setSelected(true);
            case "Crochet" -> chipCrochet.setSelected(true);
            case "Keychain" -> chipKeychain.setSelected(true);
            default -> chipAll.setSelected(true);
        }
    }

    private void setUpListeners() {
        tfProductSearch.textProperty().addListener((obs, o, n) -> {
            lastSearch = n == null ? "" : n;
            applyFilters();
        });

        // A chip can't be un-selected: clicking the selected one keeps it selected
        tgCat.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) {
                if (oldT != null) {
                    oldT.setSelected(true);
                }
                return;
            }
            lastCategory = ((ToggleButton) newT).getText();
            applyFilters();
        });

        // List / Grid toggle switches to the other screen
        tgView.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) {
                if (oldT != null) {
                    oldT.setSelected(true);
                }
                return;
            }
            if (gridMode && newT == btnViewList) {
                switchScreen("INVENTORY_LIST.fxml", "Inventory");
            } else if (!gridMode && newT == btnViewGrid) {
                switchScreen("INVENTORY_GRID.fxml", "Inventory");
            }
        });
    }

    private void applyFilters() {
        String text = lastSearch.trim().toLowerCase();
        String category = lastCategory;

        filtered.setPredicate(p -> {
            boolean matchesCategory = category.equals("All") || category.equals(p.get("Category"));
            boolean matchesText = text.isEmpty()
                    || String.valueOf(p.get("Name")).toLowerCase().contains(text);
            return matchesCategory && matchesText;
        });

        if (gridMode) {
            rebuildGrid();
        }
    }

    private void updateSummary() {
        lblProductTotal.setText("Total Products: " + ALL_PRODUCTS.size());
        lblCategoryCounts.setText("Bouquet " + countCategory("Bouquet")
                + "  \u00B7  Crochet " + countCategory("Crochet")
                + "  \u00B7  Keychain " + countCategory("Keychain"));
    }

    private long countCategory(String category) {
        return ALL_PRODUCTS.stream().filter(p -> category.equals(p.get("Category"))).count();
    }

    /** Call after any change to the product data. */
    private void refreshView() {
        updateSummary();
        applyFilters();
        if (tvProducts != null) {
            tvProducts.refresh();
        }
    }

    // =====================================================================
    //  LIST screen: table
    // =====================================================================

    private void setUpTable() {
        // Photo: coloured tile with a category icon
        colProdPhoto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                Map<String, Object> row = getTableRow() == null ? null : getTableRow().getItem();
                if (empty || row == null) {
                    setGraphic(null);
                    return;
                }
                String category = (String) row.get("Category");
                StackPane tile = new StackPane(categoryIcon(category, 0.4));
                tile.setMinSize(44, 44);
                tile.setPrefSize(44, 44);
                tile.setMaxSize(44, 44);
                tile.setStyle("-fx-background-color: " + categoryColor(category) + "; -fx-background-radius: 10;");
                setGraphic(tile);
            }
        });

        colProdPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : peso(((Number) item).doubleValue()));
            }
        });

        colProdStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                int stock = ((Number) item).intValue();
                if (stock <= LOW_STOCK_LEVEL) {
                    Label badge = new Label(String.valueOf(stock));
                    badge.getStyleClass().add("badge-low");
                    setGraphic(badge);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(String.valueOf(stock));
                }
            }
        });

        colProdActions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                Map<String, Object> row = getTableRow() == null ? null : getTableRow().getItem();
                if (empty || row == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(actionButtons(row, 10));
            }
        });

        // Replaces the sample rows hard-coded in the FXML
        SortedList<Map<String, Object>> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tvProducts.comparatorProperty());
        tvProducts.setItems(sorted);
    }

    // =====================================================================
    //  GRID screen: cards
    // =====================================================================

    private void rebuildGrid() {
        gpProducts.getChildren().clear();

        int index = 0;
        for (Map<String, Object> p : filtered) {
            gpProducts.add(buildCard(p), index % GRID_COLUMNS, index / GRID_COLUMNS);
            index++;
        }
    }

    private VBox buildCard(Map<String, Object> p) {
        String category = (String) p.get("Category");
        int stock = ((Number) p.get("stock")).intValue();

        // photo area
        StackPane photo = new StackPane();
        photo.setMinHeight(140);
        photo.setPrefHeight(140);
        photo.setMaxHeight(140);
        photo.setMaxWidth(Double.MAX_VALUE);
        photo.setStyle("-fx-background-color: " + categoryColor(category) + "; -fx-background-radius: 14 14 0 0;");
        photo.getChildren().add(categoryIcon(category, 1.0));

        Label idTag = new Label("#" + p.get("ID"));
        idTag.setStyle("-fx-background-color: white; -fx-text-fill: #72243E; -fx-background-radius: 10; "
                + "-fx-padding: 2 8 2 8; -fx-font-size: 11px;");
        StackPane.setAlignment(idTag, Pos.TOP_LEFT);
        StackPane.setMargin(idTag, new Insets(8, 0, 0, 8));
        photo.getChildren().add(idTag);

        if (stock <= LOW_STOCK_LEVEL) {
            Label low = new Label("Low stock");
            low.setStyle("-fx-background-color: #FCEBEB; -fx-text-fill: #A32D2D; -fx-background-radius: 10; "
                    + "-fx-padding: 2 8 2 8; -fx-font-size: 11px;");
            StackPane.setAlignment(low, Pos.TOP_RIGHT);
            StackPane.setMargin(low, new Insets(8, 8, 0, 0));
            photo.getChildren().add(low);
        }

        // details
        Label name = new Label(String.valueOf(p.get("Name")));
        name.setWrapText(true);
        name.setMaxWidth(Double.MAX_VALUE);
        name.setMinHeight(36);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2a0f1c;");

        Label cat = new Label(category);
        cat.getStyleClass().add("hint-label");

        Label price = new Label(peso(((Number) p.get("Price")).doubleValue()));
        price.setStyle("-fx-font-weight: bold; -fx-text-fill: #C22A62; -fx-font-size: 15px;");

        Label stockTitle = new Label("Stock:");
        stockTitle.getStyleClass().add("hint-label");

        Label stockValue = new Label(String.valueOf(stock));
        if (stock <= LOW_STOCK_LEVEL) {
            stockValue.getStyleClass().add("badge-low");
        } else {
            stockValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a0f1c;");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox priceRow = new HBox(6, price, spacer, stockTitle, stockValue);
        priceRow.setAlignment(Pos.CENTER_LEFT);

        HBox actions = actionButtons(p, 8);
        VBox.setMargin(actions, new Insets(4, 0, 0, 0));

        VBox info = new VBox(4, name, cat, priceRow, actions);
        info.setPadding(new Insets(10, 12, 12, 12));

        VBox card = new VBox(photo, info);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; "
                + "-fx-border-color: #F4C0D1; -fx-border-radius: 14;");
        GridPane.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // =====================================================================
    //  Shared helpers for both views
    // =====================================================================

    private HBox actionButtons(Map<String, Object> product, double spacing) {
        Button restock = iconButton("ico-add", "Restock", "btn-icon");
        restock.setOnAction(e -> restockProduct(product));

        Button edit = iconButton("ico-edit", "Edit", "btn-icon", "btn-edit");
        edit.setText("Edit");
        edit.setOnAction(e -> editProduct(product));

        Button delete = iconButton("ico-delete", "Delete", "btn-icon", "btn-icon-danger");
        delete.setOnAction(e -> deleteProduct(product));

        HBox box = new HBox(spacing, restock, edit, delete);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
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

    private String categoryColor(String category) {
        if ("Keychain".equals(category)) {
            return "#E3DDF7";
        }
        if ("Crochet".equals(category)) {
            return "#FFE3B3";
        }
        return "#F8BBD0";   // Bouquet
    }

    /** Simple drawn icon per category (designed on a 64x64 canvas, then scaled). */
    private Pane categoryIcon(String category, double scale) {
        Pane pane = new Pane();
        pane.setMinSize(64, 64);
        pane.setPrefSize(64, 64);
        pane.setMaxSize(64, 64);
        pane.setMouseTransparent(true);

        if ("Keychain".equals(category)) {
            Color c = Color.web("#5B4BB7");
            Circle ring = new Circle(17.9, 32, 10.2);
            ring.setFill(Color.TRANSPARENT);
            ring.setStroke(c);
            ring.setStrokeWidth(4.8);
            pane.getChildren().addAll(ring,
                    line(28.2, 32, 57.6, 32, c, 4.8),
                    line(46.1, 32, 46.1, 43.5, c, 4.8),
                    line(54.4, 32, 54.4, 43.5, c, 4.8));
        } else if ("Crochet".equals(category)) {
            Color c = Color.web("#8A5A00");
            Circle ball = new Circle(32, 32, 21.8);
            ball.setFill(Color.TRANSPARENT);
            ball.setStroke(c);
            ball.setStrokeWidth(3.8);
            pane.getChildren().addAll(ball,
                    yarnArc(0, c), yarnArc(60, c), yarnArc(120, c),
                    line(49.9, 48.6, 61.4, 59.5, c, 3.8));
        } else {
            double[][] petals = {
                    {47, 32, 0}, {39.5, 45, 60}, {24.5, 45, 120},
                    {17, 32, 180}, {24.5, 19, 240}, {39.5, 19, 300}};
            for (double[] pt : petals) {
                Ellipse petal = new Ellipse(pt[0], pt[1], 14, 9.4);
                petal.setFill(Color.web("#C22A62"));
                petal.setRotate(pt[2]);
                pane.getChildren().add(petal);
            }
            pane.getChildren().add(new Circle(32, 32, 7, Color.web("#FFE08A")));
        }

        pane.setScaleX(scale);
        pane.setScaleY(scale);
        return pane;
    }

    private Line line(double x1, double y1, double x2, double y2, Color color, double width) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(color);
        l.setStrokeWidth(width);
        l.setStrokeLineCap(StrokeLineCap.ROUND);
        return l;
    }

    private Arc yarnArc(double rotation, Color color) {
        Arc arc = new Arc(32, 32, 21.8, 10.2, 0, 180);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(color);
        arc.setStrokeWidth(3.8);
        arc.setRotate(rotation);
        return arc;
    }

    private String peso(double amount) {
        return String.format("\u20B1%,.2f", amount);
    }

    // =====================================================================
    //  Product actions
    // =====================================================================

    @FXML
    private void handleOpenAddProduct() {
        showProductDialog("Add product", null).ifPresent(r -> {
            int nextId = ALL_PRODUCTS.stream()
                    .mapToInt(p -> ((Number) p.get("ID")).intValue())
                    .max().orElse(0) + 1;

            // TODO: also INSERT the product into the database
            ALL_PRODUCTS.add(product(nextId,
                    (String) r.get("Name"),
                    (String) r.get("Category"),
                    (Double) r.get("Price"),
                    (Integer) r.get("stock")));
            refreshView();
        });
    }

    private void editProduct(Map<String, Object> product) {
        showProductDialog("Edit product", product).ifPresent(r -> {
            // TODO: also UPDATE the product in the database
            product.putAll(r);
            refreshView();
        });
    }

    private void restockProduct(Map<String, Object> product) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Restock");
        dialog.setHeaderText("Restock " + product.get("Name"));
        dialog.setContentText("Quantity to add:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        try {
            int qty = Integer.parseInt(result.get().trim());
            if (qty < 1) {
                throw new NumberFormatException();
            }
            // TODO: also UPDATE the stock in the database
            product.put("stock", ((Number) product.get("stock")).intValue() + qty);
            refreshView();
        } catch (NumberFormatException e) {
            showWarning("Invalid quantity", "Please enter a whole number of 1 or more.");
        }
    }

    private void deleteProduct(Map<String, Object> product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete product");
        confirm.setHeaderText("Delete \"" + product.get("Name") + "\"?");
        confirm.setContentText("This cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: also DELETE the product from the database
            ALL_PRODUCTS.remove(product);
            refreshView();
        }
    }

    /** Pop-up form used for both Add and Edit. Returns Name, Category, Price and stock. */
    private Optional<Map<String, Object>> showProductDialog(String title, Map<String, Object> existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(currentStage());
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfName = new TextField(existing == null ? "" : String.valueOf(existing.get("Name")));
        tfName.setPromptText("Product name");

        ComboBox<String> cbCategory = new ComboBox<>(
                FXCollections.observableArrayList("Bouquet", "Crochet", "Keychain"));
        cbCategory.setValue(existing == null ? "Bouquet" : (String) existing.get("Category"));

        TextField tfPrice = new TextField(existing == null ? ""
                : String.format("%.2f", ((Number) existing.get("Price")).doubleValue()));
        tfPrice.setPromptText("0.00");

        TextField tfStock = new TextField(existing == null ? "0" : String.valueOf(existing.get("stock")));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10, 10, 0, 10));
        form.addRow(0, new Label("Name"), tfName);
        form.addRow(1, new Label("Category"), cbCategory);
        form.addRow(2, new Label("Price (\u20B1)"), tfPrice);
        form.addRow(3, new Label("Stock"), tfStock);
        dialog.getDialogPane().setContent(form);

        Map<String, Object> result = new HashMap<>();

        // Validate when OK is pressed; keep the dialog open if something is wrong
        Button ok = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        ok.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                String name = tfName.getText().trim();
                double price = Double.parseDouble(
                        tfPrice.getText().replace("\u20B1", "").replace(",", "").trim());
                int stock = Integer.parseInt(tfStock.getText().trim());

                if (name.isEmpty() || price < 0 || stock < 0) {
                    throw new NumberFormatException();
                }
                result.put("Name", name);
                result.put("Category", cbCategory.getValue());
                result.put("Price", price);
                result.put("stock", stock);
            } catch (NumberFormatException ex) {
                showWarning("Check the form",
                        "Enter a name, a price of 0 or more, and a whole-number stock of 0 or more.");
                event.consume();
            }
        });

        Optional<ButtonType> choice = dialog.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    // =====================================================================
    //  Header
    // =====================================================================

    @FXML
    private void handleOpenExport() {
        openDialog("EXPORTDIALOG.fxml", "Export inventory");
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
        switchScreen("STANDSALE.fxml", "Stand Sale");
    }

    @FXML
    private void goInventory() {
        // Already on the inventory screen
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
        return (Stage) navInventory.getScene().getWindow();
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
        alert.setTitle("Inventory");
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

    /** Called by AddProductController to add a new product. */
    public static void addProduct(String name, String category, double price, int stock) {
        int nextId = ALL_PRODUCTS.stream()
                .mapToInt(p -> ((Number) p.get("ID")).intValue())
                .max().orElse(0) + 1;
        // TODO: also INSERT the product into the database
        ALL_PRODUCTS.add(product(nextId, name, category, price, stock));
    }
}