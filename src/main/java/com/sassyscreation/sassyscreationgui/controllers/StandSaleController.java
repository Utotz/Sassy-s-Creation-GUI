package com.sassyscreation.sassyscreationgui.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.sassyscreation.sassyscreationgui.dao.ProductDAO;
import com.sassyscreation.sassyscreationgui.dao.StandSaleDAO;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
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

    // ---- top sellers ranking ----
    @FXML private VBox vbTopSellers;

    // ---- sales history filters (dropdowns above the table) ----
    @FXML private ComboBox<String> cbQtyFilter;
    @FXML private ComboBox<String> cbPriceFilter;

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
        final int id;
        final String name;
        final double price;
        int stock;

        Product(int id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }

    /** How many products the Top sellers list shows. */
    private static final int TOP_COUNT = 7;

    /** Dropdown choices. The position in the list is what the filter logic below checks. */
    private static final List<String> QTY_FILTERS = List.of(
            "All quantities", "1", "2 \u2013 5", "6 \u2013 10", "11 or more");
    private static final List<String> PRICE_FILTERS = List.of(
            "All prices", "Under \u20B130", "\u20B130 \u2013 \u20B149", "\u20B150 \u2013 \u20B199", "\u20B1100 or more");

    /** Fixed table order: newest date first (dates are ISO yyyy-MM-dd, so text order = date order). */
    private static final Comparator<Map<String, Object>> NEWEST_FIRST =
            (a, b) -> String.valueOf(b.get("Date")).compareTo(String.valueOf(a.get("Date")));

    private final Map<String, Product> products = new LinkedHashMap<>();
    private final ObservableList<Map<String, Object>> allSales = FXCollections.observableArrayList();
    /** What the table actually shows (allSales narrowed down by the two dropdowns). */
    private FilteredList<Map<String, Object>> filteredSales;
    /** filteredSales in fixed newest-first order; this is what the table is bound to. */
    private SortedList<Map<String, Object>> sortedSales;


    // =====================================================================
    //  LOGIC: DATABASE ACCESS FOR THIS SCREEN
    //  This screen never writes SQL. It asks two DAO objects (one per table):
    //    productDAO   -> getAllProducts()   (names, ids, prices, stock)
    //    standSaleDAO -> getAllSales(), insertSale(...)
    // =====================================================================
    private static final ProductDAO productDAO = new ProductDAO();
    private static final StandSaleDAO standSaleDAO = new StandSaleDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sidebarController.setActive("standsale");

        loadProducts();
        loadSales();
        setUpForm();
        setUpTable();
        setUpFilters();
        updateForm();
        updateSummary();
    }

    // =====================================================================
    //  LOGIC: LOAD PRODUCTS FROM THE DATABASE
    //  Fills the 'products' map (product name -> Product with id, price, stock).
    //  Runs at startup AND after every recorded sale, so the stock shown is always
    //  the database's stock. Product names are unique in the seed, so the name is
    //  used as the dropdown key; the id is what insertSale needs.
    // =====================================================================

    private void loadProducts() {
        products.clear();
        try {
            for (var p : productDAO.getAllProducts()) {
                products.put(p.getName(), new Product(
                        p.getProductId(), p.getName(),
                        p.getPrice().doubleValue(), p.getStockQuantity()));
            }
        } catch (SQLException e) {
            showWarning("Could not load products", e.getMessage());
        }
    }

    // =====================================================================
    //  LOGIC: LOAD SALES HISTORY FROM THE DATABASE
    //  Fills 'allSales' from StandSaleDAO.getAllSales() (newest first). Each row is
    //  a Map built by sale(...). Everything on screen (table, chips, top sellers)
    //  is computed from allSales, so reloading it refreshes the whole screen.
    // =====================================================================
    private void loadSales() {
        allSales.clear();
        try {
            for (var s : standSaleDAO.getAllSales()) {
                allSales.add(sale(s.getProductName(), s.getQuantity(),
                        s.getUnitPrice().doubleValue(), s.getSaleDate().toString()));
            }
        } catch (SQLException e) {
            showWarning("Could not load sales", e.getMessage());
        }
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
        setUpProductSearch();
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


    // =====================================================================
    //  LOGIC RESPONSIBLE FOR SEARCHING PRODUCTS IN THE STAND SALE DROPDOWN
    // =====================================================================
    // Last product name the user really chose (used to undo half-typed text).
    private String lastValidProduct;

    private void setUpProductSearch() {
        FilteredList<String> shown = new FilteredList<>(
                FXCollections.observableArrayList(products.keySet()), name -> true);
        cbSaleProduct.setItems(shown);
        cbSaleProduct.setEditable(true);
        TextField editor = cbSaleProduct.getEditor();

        // 1. Narrow the list while typing (matches anywhere in the name, any letter case)
        editor.textProperty().addListener((obs, oldText, text) -> {
            if (text == null || products.containsKey(text)) {
                return;   // an exact product name is a real choice, not a search
            }
            String q = text.trim().toLowerCase();
            shown.setPredicate(name -> q.isEmpty() || name.toLowerCase().contains(q));
            if (editor.isFocused() && !cbSaleProduct.isShowing()) {
                cbSaleProduct.show();
            }
        });

        // 2. A real choice shows the full list again and is remembered
        cbSaleProduct.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && products.containsKey(newV)) {
                lastValidProduct = newV;
                shown.setPredicate(name -> true);
            }
        });

        // 3. Clicking in selects all text, so typing replaces it.
        // 4. Leaving the box with half-typed text goes back to the last chosen product.
        editor.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) {
                Platform.runLater(editor::selectAll);
            } else if (!products.containsKey(editor.getText()) && lastValidProduct != null) {
                cbSaleProduct.setValue(lastValidProduct);
                editor.setText(lastValidProduct);
                shown.setPredicate(name -> true);
            }
        });

        // 5. Enter picks the first match
        editor.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER && !shown.isEmpty()
                    && !products.containsKey(editor.getText())) {
                cbSaleProduct.setValue(shown.get(0));
                cbSaleProduct.hide();
                e.consume();
            }
        });
    }

    // =====================================================================
    //  LOGIC: QUANTITY INPUT (1 to 999)
    //  Called when the quantity box loses focus and before recording a sale.
    //  Keeps a typed number inside 1..999; if the text is not a number it goes
    //  back to the last valid value.
    // =====================================================================
    private void commitSpinnerText() {
        try {
            int typed = Integer.parseInt(spQty.getEditor().getText().trim());
            typed = Math.max(1, Math.min(999, typed));
            spQty.getValueFactory().setValue(typed);
        } catch (NumberFormatException e) {
            spQty.getEditor().setText(String.valueOf(spQty.getValue()));
        }
    }

    // =====================================================================
    //  LOGIC: LIVE PREVIEW IN THE RECORD-A-SALE CARD
    //  Recalculates unit price, total and the stock note whenever the product or
    //  the quantity changes. Display only: nothing is saved here.
    // =====================================================================
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

    // =====================================================================
    //  LOGIC RESPONSIBLE FOR RECORDING A SALE (the Record Sale button)
    //    1. validate: a product is chosen, quantity >= 1, quantity <= stock shown
    //       (friendly warning first; the database still protects itself)
    //    2. StandSaleDAO.insertSale(productId, qty, today): ONE transaction that
    //       deducts the stock, reads the price from products, inserts the sale
    //    3. on SQLException: warning, nothing saved, screen unchanged
    //    4. reload products and sales from the database, then refresh the form,
    //       the chips and Top sellers (the screen always shows what was saved)
    // =====================================================================
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

        try {
            standSaleDAO.insertSale(p.id, qty, LocalDate.now());
        } catch (SQLException e) {
            showWarning("Could not record the sale", e.getMessage());
            return;   // nothing was saved, so leave the screen as it is
        }
        loadProducts();   // fresh stock from the database
        loadSales();      // fresh history from the database

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
    // SALES TABLE
    // =====================================================================


    // =====================================================================
    //  LOGIC: SALES HISTORY TABLE
    //  1) formats Unit and Total as pesos, 2) builds the Edit / Delete icon buttons,
    //  3) wires the data pipeline: allSales -> filteredSales (the two dropdowns)
    //     -> sortedSales (fixed newest-first order) -> table,
    //  4) the column headers are NOT clickable (sortable=false in the FXML), so there
    //     is no sort arrow; the order is decided only by NEWEST_FIRST.
    //  Header/cell alignment and the actions column are styled inline (see lockTableHeader).
    // =====================================================================

    private void setUpTable() {
        // Every text column uses the same centered cell look (same style as the header labels,
        // see lockTableHeader), so each header sits exactly over its values.
        colSaleDate.setCellFactory(col -> textCell(false));
        colSaleProduct.setCellFactory(col -> textCell(false));
        colSaleQty.setCellFactory(col -> textCell(false));
        colSalePrice.setCellFactory(col -> textCell(true));
        colSaleTotal.setCellFactory(col -> textCell(true));

        // Actions: icon-only edit + delete buttons, left-aligned in the (wider) Actions column
        colSaleActions.setCellFactory(col -> new TableCell<>() {
            {
                setStyle(ACTION_CELL_STYLE);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

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

                HBox box = new HBox(ACTION_GAP, edit, delete);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        // allSales -> filteredSales (the two dropdowns) -> sortedSales (newest first) -> table
        filteredSales = new FilteredList<>(allSales, s -> true);
        sortedSales = new SortedList<>(filteredSales, NEWEST_FIRST);
        tvSales.setItems(sortedSales);

        // Header: not draggable, not clickable, text aligned with the cells.
        // The header only exists once the table's skin is created, so do it then.
        tvSales.skinProperty().addListener((obs, o, n) -> Platform.runLater(this::lockTableHeader));
        Platform.runLater(this::lockTableHeader);

        // Re-align the header text whenever the table is resized or its rows change.
        tvSales.widthProperty().addListener((obs, o, n) -> scheduleCenterHeaders());
        sortedSales.addListener((ListChangeListener<Map<String, Object>>) c -> scheduleCenterHeaders());
    }

    private PauseTransition headerTimer;

    /** Runs centerHeaderText a moment later, once JavaFX has laid out the header and the rows. */
    private void scheduleCenterHeaders() {
        if (headerTimer == null) {
            headerTimer = new PauseTransition(Duration.millis(80));
            headerTimer.setOnFinished(e -> centerHeaderText());
        }
        headerTimer.playFromStart();
    }

    /** Width of each Edit/Delete button and the gap between them (px). Change these to nudge the icons. */
    private static final double ACTION_BTN_WIDTH = 40;
    private static final double ACTION_GAP = 6;

    /** Header labels and cells share this style: text centered in the column, so a header sits
     *  exactly over its values (the "t" of Qty over the number, not the "Q"). */
    private static final String TEXT_CELL_STYLE = "-fx-alignment: CENTER; -fx-padding: 0;";

    /** The Edit/Delete icons stay left-aligned in their wide column. */
    private static final String ACTION_CELL_STYLE = "-fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;";

    /** A plain text cell (or a peso-formatted one) with the shared alignment/padding. */
    private TableCell<Map<String, Object>, Object> textCell(boolean peso) {
        TableCell<Map<String, Object>, Object> cell = new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (peso) {
                    setText(peso(((Number) item).doubleValue()));
                } else {
                    setText(String.valueOf(item));
                }
            }
        };
        cell.setStyle(TEXT_CELL_STYLE);
        return cell;
    }

    /**
     * Makes the column header fixed: the whole header row ignores the mouse (no dragging to
     * reorder or resize, no clicking to sort, no resize cursor) and its labels get the same
     * centered style as the cells. Uses inline styles on purpose, so it does not depend on any
     * stylesheet being loaded or winning over styles.css.
     */
    private void lockTableHeader() {
        Node headerRow = tvSales.lookup(".column-header-background");
        if (headerRow != null) {
            headerRow.setMouseTransparent(true);
        }
        for (Node header : tvSales.lookupAll(".column-header")) {
            header.setStyle("-fx-padding: 0;");
        }
        for (Node label : tvSales.lookupAll(".column-header .label")) {
            label.setStyle(TEXT_CELL_STYLE);
        }

        // JavaFX leaves the header text slightly off the column's middle, while the cells are
        // exactly centered. Measure and correct it (also whenever a header changes width).
        for (Node n : tvSales.lookupAll(".column-header")) {
            if (n instanceof TableColumnHeader h && !(n instanceof NestedTableColumnHeader)
                    && h.getProperties().putIfAbsent("centerHook", Boolean.TRUE) == null) {
                h.widthProperty().addListener((o, a, b) -> scheduleCenterHeaders());
            }
        }
        scheduleCenterHeaders();
    }

    /**
     * Moves each header's text so its middle is exactly over the middle of the cells below it.
     * The rows start a little to the right of the header row, so the header text is measured
     * and shifted to the real position of that column's cells (the header's own box is not used).
     */
    private void centerHeaderText() {
        for (Node n : tvSales.lookupAll(".column-header")) {
            if (!(n instanceof TableColumnHeader h) || n instanceof NestedTableColumnHeader
                    || h.getWidth() <= 0) {
                continue;
            }
            Node label = h.lookup(".label");
            if (!(label instanceof Labeled labeled) || labeled.getText() == null
                    || labeled.getText().isBlank()) {
                continue;   // e.g. the empty Actions header
            }
            Node text = label.lookup(".text");
            if (text == null) {
                continue;
            }

            // where this column's cells really are
            Bounds cellBox = null;
            for (Node c : tvSales.lookupAll(".table-cell")) {
                if (c instanceof TableCell<?, ?> tc && tc.getTableColumn() == h.getTableColumn()
                        && tc.getWidth() > 0) {
                    cellBox = tc.localToScene(tc.getLayoutBounds());
                    break;
                }
            }
            double target = cellBox != null
                    ? cellBox.getCenterX()
                    : h.localToScene(h.getLayoutBounds()).getCenterX();

            label.setTranslateX(0);
            Bounds shown = text.localToScene(text.getBoundsInLocal());
            label.setTranslateX(target - shown.getCenterX());
        }
    }

    /**
     * Re-applies the newest-first order. Needed after an in-place edit, because the row maps are
     * not observable, so the SortedList cannot notice that a row's date changed.
     */
    private void resortSales() {
        sortedSales.setComparator(null);
        sortedSales.setComparator(NEWEST_FIRST);
    }

    // =====================================================================
    //  Filters (Qty + Unit price dropdowns)
    // =====================================================================


    // =====================================================================
    //  LOGIC RESPONSIBLE FOR FILTERING THE SALES HISTORY (Qty and Unit price dropdowns)
    //  The choice is checked by its POSITION in QTY_FILTERS / PRICE_FILTERS, and
    //  qtyMatches / priceMatches switch on that same position. If you reorder or add
    //  a dropdown item you MUST change the matching 'case' number too.
    // =====================================================================

    private void setUpFilters() {
        if (cbQtyFilter == null || cbPriceFilter == null) {
            return;   // FXML without the filter row
        }
        cbQtyFilter.setItems(FXCollections.observableArrayList(QTY_FILTERS));
        cbQtyFilter.getSelectionModel().selectFirst();
        cbPriceFilter.setItems(FXCollections.observableArrayList(PRICE_FILTERS));
        cbPriceFilter.getSelectionModel().selectFirst();

        cbQtyFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        cbPriceFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
    }

    /**
     * Re-checks every row against both dropdowns. A brand new predicate is set each time so the
     * list re-filters even when a row was edited in place (the row maps are not observable).
     */
    private void applyFilters() {
        if (filteredSales == null || cbQtyFilter == null || cbPriceFilter == null) {
            return;
        }
        int qtyChoice = Math.max(0, cbQtyFilter.getSelectionModel().getSelectedIndex());
        int priceChoice = Math.max(0, cbPriceFilter.getSelectionModel().getSelectedIndex());

        filteredSales.setPredicate(s ->
                qtyMatches(((Number) s.get("Qty")).intValue(), qtyChoice)
                        && priceMatches(((Number) s.get("Price")).doubleValue(), priceChoice));

        boolean filtering = qtyChoice != 0 || priceChoice != 0;
        tvSales.setPlaceholder(new Label(filtering ? "No sales match these filters" : "No sales recorded yet"));
    }

    private boolean qtyMatches(int qty, int choice) {
        return switch (choice) {
            case 1 -> qty == 1;
            case 2 -> qty >= 2 && qty <= 5;
            case 3 -> qty >= 6 && qty <= 10;
            case 4 -> qty >= 11;
            default -> true;   // 0 = All quantities
        };
    }

    private boolean priceMatches(double price, int choice) {
        return switch (choice) {
            case 1 -> price < 30;
            case 2 -> price >= 30 && price < 50;
            case 3 -> price >= 50 && price < 100;
            case 4 -> price >= 100;
            default -> true;   // 0 = All prices
        };
    }

    private Button iconButton(String icon, String tooltip, String... styleClasses) {
        Button b = new Button();
        b.setFocusTraversable(false);
        b.setMnemonicParsing(false);
        b.getStyleClass().addAll(styleClasses);
        // The default icon buttons are much wider than the icon, which pushed the pair against the
        // right edge. A fixed compact size keeps them next to each other (inline = always wins).
        b.setStyle("-fx-min-width: " + ACTION_BTN_WIDTH + "; -fx-pref-width: " + ACTION_BTN_WIDTH
                + "; -fx-max-width: " + ACTION_BTN_WIDTH + "; -fx-padding: 0;");
        Region graphic = new Region();
        graphic.getStyleClass().addAll("ico", icon);
        b.setGraphic(graphic);
        b.setTooltip(new Tooltip(tooltip));
        return b;
    }
    // =====================================================================
    //  LOGIC: EDIT A SALE - SCREEN ONLY, NOT SAVED
    //  ONGOING CONNECTION TO DB
    // =====================================================================

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

        applyFilters();   // the edited row may now match (or no longer match) the dropdowns
        resortSales();    // the date may have changed, keep newest first
        tvSales.refresh();
        updateForm();
        updateSummary();
    }
    // =====================================================================
    //  LOGIC: DELETE A SALE - SCREEN ONLY, NOT SAVED
    //  ONGOING
    // =====================================================================

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

    // =====================================================================
    //  LOGIC: SUMMARY CHIPS (Total Stand Sales / Total Revenue)
    //  Counts and adds up ALL loaded sales, then rebuilds Top sellers.
    //  Revenue is the sum of each row's Total (quantity x the price saved at sale time).
    // =====================================================================


    private void updateSummary() {
        double revenue = allSales.stream()
                .mapToDouble(s -> ((Number) s.get("Total")).doubleValue())
                .sum();

        // The "Total Stand Sales:" / "Total Revenue:" text is in the FXML; only set the values here
        lblSaleCount.setText(String.valueOf(allSales.size()));
        lblSaleRevenue.setText(peso(revenue));

        updateTopSellers();
    }

    // =====================================================================
    //  Top sellers (ranked by units sold)
    // =====================================================================


    // =====================================================================
    //  LOGIC: TOP SELLERS RANKING
    //  Adds up units and money per product name, sorts by units (highest first),
    //  then by revenue, and shows the first TOP_COUNT products. The bar length is
    //  each product's units divided by the best seller's units.
    // =====================================================================

    private void updateTopSellers() {
        Map<String, Integer> units = new HashMap<>();
        Map<String, Double> money = new HashMap<>();
        for (Map<String, Object> s : allSales) {
            String name = String.valueOf(s.get("Product"));
            units.merge(name, (Integer) s.get("Qty"), Integer::sum);
            money.merge(name, ((Number) s.get("Total")).doubleValue(), Double::sum);
        }

        List<String> ranked = new ArrayList<>(units.keySet());
        ranked.sort((a, b) -> {
            int byUnits = units.get(b) - units.get(a);
            return byUnits != 0 ? byUnits : Double.compare(money.get(b), money.get(a));
        });

        vbTopSellers.getChildren().clear();
        if (ranked.isEmpty()) {
            Label none = new Label("No sales yet");
            none.getStyleClass().add("page-sub");
            vbTopSellers.getChildren().add(none);
            return;
        }

        int best = units.get(ranked.get(0));
        int count = Math.min(TOP_COUNT, ranked.size());
        for (int i = 0; i < count; i++) {
            String name = ranked.get(i);
            vbTopSellers.getChildren().add(topSellerRow(i + 1, name, units.get(name), (double) units.get(name) / best));
        }
    }

    /** One row: rank circle, product name, units sold, and a bar next to the best seller. */
    private VBox topSellerRow(int rank, String name, int sold, double ratio) {
        Label badge = new Label(String.valueOf(rank));
        badge.setMinSize(32, 32);
        badge.setPrefSize(32, 32);
        badge.setMaxSize(32, 32);
        badge.setAlignment(Pos.CENTER);
        badge.setStyle("-fx-background-color: " + rankColor(rank) + "; -fx-background-radius: 16;"
                + "-fx-text-fill: " + (rank <= 3 ? "white" : "#7A1842") + ";"
                + "-fx-font-weight: bold; -fx-font-size: 15px;");

        Label product = new Label(name);
        product.setMaxWidth(Double.MAX_VALUE);
        product.setTextOverrun(OverrunStyle.ELLIPSIS);
        product.setStyle("-fx-font-size: 16px; -fx-text-fill: #2B2230;");
        HBox.setHgrow(product, Priority.ALWAYS);

        Label units = new Label(sold + " sold");
        units.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #C22A62;");

        HBox top = new HBox(12, badge, product, units);
        top.setAlignment(Pos.CENTER_LEFT);

        // grey track with a pink fill, the fill width is a share of the best seller
        Region track = new Region();
        track.setStyle("-fx-background-color: #F6DCE6; -fx-background-radius: 5;");
        Region fill = new Region();
        fill.setStyle("-fx-background-color: #C22A62; -fx-background-radius: 5;");
        for (Region r : new Region[]{track, fill}) {
            r.setMinHeight(8);
            r.setPrefHeight(8);
            r.setMaxHeight(8);
        }
        StackPane bar = new StackPane(track, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        fill.maxWidthProperty().bind(bar.widthProperty().multiply(ratio));
        VBox.setMargin(bar, new Insets(0, 0, 0, 44));

        return new VBox(8, top, bar);
    }

    private String rankColor(int rank) {
        return switch (rank) {
            case 1 -> "#C22A62";
            case 2 -> "#E64980";
            case 3 -> "#F48FB1";
            default -> "#F8BBD0";
        };
    }

    private String peso(double amount) {
        return String.format("\u20B1%,.2f", amount);
    }

    // =====================================================================
    //  Header
    // =====================================================================

    // =====================================================================
    //  LOGIC: EXPORT STAND SALES
    //  Opens the export dialog, keeps only the sales inside the chosen date range,
    //  and passes them as rows (Date, Product, Qty, Unit price, Total) to
    //  ExportDialogController.export(...). Reads allSales, so it exports what was loaded.
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