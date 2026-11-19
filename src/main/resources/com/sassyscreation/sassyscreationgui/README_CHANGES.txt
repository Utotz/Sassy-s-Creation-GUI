Sassy's Creation - v3 restyle
- ONLY DASHBOARD and LOGIN are unchanged.
- 13 page screens got styleClass "page-v3" on their root (ORDERS, STANDSALE, INVENTORY_LIST, INVENTORY_GRID, ADDORDER, ORDERFORM, ADDPRODUCT, PRODUCTFORM, EDITSALE, FORGOTPASSWORD, RESETPASSWORD, PASSWORDCHANGED) - and LOADING.
- 15 dialogs got "page-v3" (CHANGEPASSWORD, ORDERDETAILS, LOWSTOCK, RESTOCK, EXPORTDIALOG, ABOUT, LOGOUT, EXITCONFIRM, DELETEORDER, DELETEPRODUCT, DELETESALE, STATUSCONFIRM, UNSAVEDCHANGES, ERRORDIALOG, SUCCESSDIALOG).
- All Spinners got styleClass "split-arrows-horizontal" (shows - and + beside the number).
- styles.css: new block at the bottom ("v3 THEME"). No fx:id or onAction was changed.
Not done (needs Java/controller code): order cards instead of the Orders table, photo thumbnails in the Inventory list.

Round 3
- LOADING: loading_flowers.gif replaced with a flower that blooms, closes and blooms again (loops forever). Same file name, so LOADING.fxml needs no change.
- STANDSALE: new layout - "Record a sale" card on the left, stat chips + sales table on the right. All fx:id / onAction kept (lblSaleCount, lblSaleRevenue, lblSaleTotal, lblStockInfo, spQty, tfUnitPrice, cbSaleProduct, btnRecordSale, tvSales).
- LOGOUT, EXITCONFIRM, UNSAVEDCHANGES, ERRORDIALOG, SUCCESSDIALOG, DELETEORDER, DELETEPRODUCT, DELETESALE, STATUSCONFIRM: centred title/text and stacked full-width buttons (main action on top).
- LOWSTOCK: keeps its layout (same as before) with the v3 radius/colour tokens.
- Search bars (ORDERS, INVENTORY_LIST, INVENTORY_GRID) and the Edit buttons already exist in these FXML files (search-box, btn-edit); the earlier drawings simply left them out and the canvas now shows them.
- Not done: product icons in the Inventory List (needs Java: a TableColumn cell factory choosing an icon per product).

Round 4
- LOGIN: root now has "page-v3", so the login card has the same corners / border / padding as FORGOTPASSWORD.
- CHANGEPASSWORD: flower logo + brand + centred title (same look as RESETPASSWORD). Key icon removed from the title (the flower replaces it); the key icon is still on the account-menu item. Two screens on purpose:
    RESETPASSWORD  = forgot-password flow (no current password, verified by security question)
    CHANGEPASSWORD = logged-in dialog (asks for the CURRENT password first)
- EXPORTDIALOG: icon and title centred.
- ORDERFORM (edit order): new red "Delete order" button (fx:id btnDeleteOrder, onAction #handleDeleteOrder) pinned to the right. NEEDS Java: add handleDeleteOrder() in the controller and open DELETEORDER from it. ADDORDER does not get it (nothing to delete yet).
- ABOUT: flower logo kept, Close button is now full width.
- Icons: all ico-* Regions are still in every FXML (nothing removed). The canvas HTML was missing them on the sidebar, dialog headers and some buttons - added there.

Round 5
- RESTOCK, ORDERDETAILS, LOWSTOCK: header icon + title centred (same as EXPORTDIALOG).
- CHANGEPASSWORD: key icon is back, inline next to the title (flower logo stays on top).

Round 6
- SIDEBAR (DASHBOARD, ORDERS, STANDSALE, EDITSALE, INVENTORY_LIST, INVENTORY_GRID, ADDORDER, ORDERFORM, ADDPRODUCT, PRODUCTFORM):
    the small "Handmade Crochet and Bouquets / Sassy's" box at the bottom-left is now the ADMIN OFFICER card
    (avatar + "Admin Officer" + role, opens upward: Change password / About / Log out).
    The old top bar (avatar + Admin Officer menu) is removed, so the page title now starts at the very top (like the FTEES reference).
    Same fx:id / onAction as before: mbAccount, miChangePassword, miAbout, miLogout (no controller change needed).
- LOGIN: new split-card layout copied from the faculty-eval sign-in: white brand panel (left) + rose "Sign In" form (right),
    gold "Log in" button, "or" divider, outlined Exit button. Background = flowers-only crop of the bouquet photo (login-bg.jpg).
    All fx:id / onAction kept (tfUsername, cbRemember, pfPassword, tfPasswordVisible, cbShowPassword, linkForgot, lblError,
    lockoutBox, lblLockoutCountdown, pbLockout, btnLogin, btnExit, lblAttempts).
- styles.css: new block at the bottom ("v5"). New file: login-bg.jpg (keep it next to styles.css).

Round 7
- FONTS: every font-family is now Poppins (the old "Inter" / "SF Pro Rounded" rules were replaced). Poppins files are in /fonts.
- LOGIN background: login-bg.jpg is blurred evenly over the whole picture (not just the middle).
- FORGOTPASSWORD, RESETPASSWORD, PASSWORDCHANGED: same split card, same size and same position as LOGIN. All fx:id / onAction kept.
- BLURRED FLOWER BACKGROUND on DASHBOARD, ORDERS, STANDSALE, INVENTORY_LIST, INVENTORY_GRID and the add/edit forms
  (class "bg-blur" on the root, picture page-bg.jpg - keep it next to styles.css).
- COLOURS: no more yellow/orange on the Log in button (now white with rose text) or the Admin Officer avatar (now white with a rose letter).
  Focus borders, check boxes, links and the lockout banner on the login screens are white / pink as well.

Round 8 (v7)
- SIGN IN / FORGOT PASSWORD / RESET PASSWORD / PASSWORD CHANGED:
    card is centred in the window (StackPane.alignment=CENTER), the footer line "Sales and Order Management System . Handmade Crochet and Bouquets" is removed,
    the lock icon in the "ADMIN OFFICER PORTAL" pill is now a person icon (new .ico-person in styles.css; the lock stays on the lockout banner),
    colours are back to the original crimson (same palette as every other screen) - the dusty-rose experiment was reverted.
    the form inside the pink panel is vertically centred (VBox alignment CENTER_LEFT on .lg-right), so the space above and below is equal.
- DASHBOARD, ORDERS, STAND SALE, INVENTORY (list + grid) and the add/edit forms: the blurred flower background is gone (class bg-blur removed,
    rule + page-bg.jpg deleted) - the original blush gradient is back. The page title now has 56px of top space (was 22) so it is no longer pushed to the top edge.
- DIALOGS: white card now has a drop shadow (.dialog-root.page-v3 .dialog-card). Dialog windows are 480 wide (were 460) so the shadow is not cut off.
- LOW STOCK: narrower (window 540, card 480; was 620 / 580). Category box 100 wide so the filter row fits.
- EXPORT DATA: Cancel / Export are inside the big white box and share its width (equal halves).
- CHANGE PASSWORD: flower logo and "SASSY'S CREATION" line removed; a key icon in a round badge now sits on top (.icon-badge). No key beside the title any more.
- No fx:id / onAction changed. No controller change needed.
- SIDEBAR (Dashboard, Orders, Stand Sale, Inventory, forms): white frame now has a shadow + right border, and the inner panel is a clearer blush (#FCE9F0)
    with a border, so it no longer blends into the page background (.dash-v2 .sidebar / .side-panel in the v7 block of styles.css).

Round 9 (v8) - FXML matched to "all 30 screens" HTML design
Files changed: ADDORDER, ADDPRODUCT, CHANGEPASSWORD, DASHBOARD, EDITSALE, EXITCONFIRM, INVENTORY_GRID, INVENTORY_LIST, LOADING, LOGOUT, LOWSTOCK, ORDERS, PRODUCTFORM, STANDSALE (+ new "v8" block at the bottom of styles.css).
No fx:id or onAction was removed or renamed (checked by script). Sample data was kept; only layout/format changed.
- LOADING: bold "Please wait..." + sub line "A flower blooms again and again".
- LOGOUT / EXITCONFIRM: small round icon badge on top, dark title (was pink), tighter spacing. Typographic apostrophe.
- CHANGEPASSWORD: dark title; "At least 8 characters..." hint now sits under "New password"; Cancel / Save password share the width equally.
- LOWSTOCK: warning icon is inline left of the title (was above); filter row packed (no gap), single-spaced count line.
- ADDORDER, EDITSALE, ADDPRODUCT, PRODUCTFORM: Cancel / Save buttons are OUTSIDE the white card, right-aligned. "Select date" placeholder on date pickers.
- EDITSALE: 2x2 grid (Product | Qty Sold, Unit Price | Date), dashed divider, "Total" left / big amount right.
- ADDPRODUCT / PRODUCTFORM: dashed photo box on the left (boxProductPhoto, ivProductPhoto, lblPhotoHint - new, no onAction yet), name full width, Category | Price, Stock under Category. Edit screen: category placeholder, stock 5 like the design.
- STANDSALE: table columns are Date, Product, Qty, Unit, Total, (actions, no header); row actions are icon-only; product / unit price show their placeholders.
- ORDERS: header "Pickups and meetups from your customers", buttons "Export data" / "New order", filter chips (All / Pending / Done / Cancelled with counts), search box with icon on the left, and a 2-column grid of ORDER CARDS (gpOrderCards inside spOrderCards).
    The old TableView (tvOrders), total bar and status dropdown are still there but hidden (managed=false) so the controller still loads.
    NEEDS JAVA: fill gpOrderCards from the orders list, wire the chips (chipOrdAll/Pending/Done/Cancelled) and the card buttons.
- INVENTORY_LIST: title "Product and inventory", sub "... . 44 products", only "Add product" top-right, chips with counts, search on the right, table = Product (photo + name), Category, Price, Stock ("N in stock" / amber "N left" / red "0 . Out of stock"), Actions ("Edit . Restock"). ID column, Export button, List/Grid switch and total bar are hidden (not deleted).
- INVENTORY_GRID: 3 cards per row (was 5), "Product photo" placeholder area, name, category, price + stock badge, buttons Edit / Restock / delete. Same sub-title style "Grid view . 44 products".
- DASHBOARD: search box hidden (not in design); typographic apostrophes.

Round 10 (v9) - remaining dialogs match LOGOUT / EXITCONFIRM
Files changed: UNSAVEDCHANGES, DELETEORDER, DELETEPRODUCT, DELETESALE, STATUSCONFIRM, ERRORDIALOG, SUCCESSDIALOG, RESTOCK, ORDERDETAILS, EXPORTDIALOG (+ small "icon-badge-sm" size rules at the bottom of styles.css).
- Plain icon above a coloured title is replaced by the round pink icon badge (.icon-badge-sm) + dark title (.dialog-title-dark), centred.
- Same icon as before in each dialog (triangle, sync, X, check, box, receipt, download). Window heights +30 where the badge needs room.
- ABOUT (flower logo) and LOWSTOCK (inline icon, as in the design) were left as they are.
- No fx:id / onAction changed.

Round 11 (v10) - dialogs are pop-outs
- All dialogs: pink background and corner flowers removed. Root is transparent (.dialog-root.page-v3), so only the white card with its shadow remains.
- NEEDS JAVA to be really see-through (otherwise JavaFX paints the window white):
    stage.initStyle(StageStyle.TRANSPARENT);   // before show()
    scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
- Pages (DASHBOARD, ORDERS, STANDSALE, EDITSALE, INVENTORY_LIST/GRID, ADDORDER, ORDERFORM, ADDPRODUCT, PRODUCTFORM) already contain the same 3 corner flower groups as the dashboard - nothing changed there.

Round 12 (v11) - one set of corners, inventory list = design, stand sale, scroll bars
- CORNERS: the Dashboard used 24 / pill 30 while every other page used 14 / 10 / 6, so screens looked like different apps.
  All screens now use the Dashboard tokens:
    card, table, dialog, order card, product card, photo box ... 24
    inner panel (low-stock box)                               ... 18
    inputs, combo, date, spinner, search, chips, login fields ... 18
    buttons (incl. Log in / Exit on the login screens)        ... pill 30
    icon tile, avatar, tags / badges                          ... 12
  Table header now rounds with the table (24 24 0 0); header cells and rows no longer paint square corners over it.
  To change the look later, edit the radius numbers in the "v3 THEME" block + the "v11" block at the bottom of styles.css.
- INVENTORY_LIST: every product tile is now the pink tile from the design (class prod-tile, radius 12) with the design icons:
    Bouquet = flower (6 circles), Keychain = octopus-style icon, Crochet = hat.  (old lavender / peach tiles removed)
  Stock: "N in stock" plain, amber "N left", red "0 . Out of stock" (tags are 12 radius like the rest).
  Fixed: out-of-stock rows had TWO <stock> entries ("0" and "0 . Out of stock"); now one.
- STANDSALE: sales are listed newest first (like the design) and the column widths follow the design proportions.
- SCROLL BARS: no more plain white bar. Slim pink thumb on a soft blush groove, no arrow buttons, darker pink on hover/drag.
  Applies to the dashboard scroll, order cards, product grid, tables and drop-down lists (global .scroll-bar rules).
- No fx:id / onAction changed (checked by script). All 30 FXML files parse.

Round 13 (v12)
- CORNERS now follow the design (photos 2 and 4): inputs AND buttons are rounded rectangles, radius 14 (no more pills), everywhere incl. Dashboard + login.
  Cards / tables / dialogs 24, inner panels 18, tiles 12, tags 8. Only circles (avatars, bell, chart bars, count badge) stay round.
- LOGIN: "Show password" checkbox replaced by a clickable eye icon inside the password field (same fx:id cbShowPassword / onAction, so no controller change).
  Grey eye = hidden, pink crossed eye = visible.
- STANDSALE rows: outline pencil / trash icons like the design (new classes ico-edit-o / ico-delete-o).

Round 14 (v14)
- STANDSALE: "Total Stand Sales:" is now a plain label + bold number (lblSaleCount holds ONLY the number, e.g. "9") - controller must call lblSaleCount.setText(String.valueOf(count)).
- All tables: header text same size as the rows and regular weight (as in the design); smaller - / + in the quantity spinner.

Round 15 (v15)
- Typographic apostrophes (Sassy’s) in all screen texts, as in the HTML. Text of all 30 screens checked against the HTML: matches.

Round 16 (v16)
- CALENDAR (Add order, Edit order, Export data, any DatePicker): the pop-up is restyled in styles.css - white rounded card, blush month bar with crimson arrows,
    round day cells, crimson selected day, outlined "today", greyed days of other months. No new dialog / no controller change needed.
- EXPORT DATA: "Include cancelled orders" is now a soft rounded row with a rounded crimson check box (class check-row).
- CORNERS: every input now has an explicit background inset in every state (hover / focus / showing), so no half-square corners.
    Dashboard "View all" pill and the Year button share the same radius 14 + border. Table rows (hover / selected) are inset and rounded,
    so they can no longer poke out of the rounded table corners (Low stock on the dashboard, the Low stock dialog, all page tables).
- INVENTORY LIST / GRID: List | Grid switch is visible again (top right, next to the search box). Same fx:id: btnViewList, btnViewGrid, tgView.
- LOADING: loading_flowers.gif now has ONE flower (was 3 stacked flowers). Same animation / timing. The old file is kept in the zip's parent as loading_flowers_ORIGINAL.gif.
- LOGIN: the eye icon had a 22 x 22 box for a 22 x 15 shape - fixed (22 x 15, and 22 x 18.9 for the crossed eye).
- LOW STOCK dialog: "Stock at or below" starts at 0 (preview rows = the 3 out-of-stock items).
- ORDERDETAILS, RESTOCK (and LOWSTOCK): the two buttons are centred and share the width equally, like EXPORTDIALOG.
- STAND SALE: the "Record a sale" card and the sales table stop at their content (no empty white box below); corners stay rounded.
    NEEDS ONE LINE OF JAVA so the table height follows the real row count (FXML only knows the 9 sample rows = 452 px):
        tvSales.setMaxHeight(Math.max(140, tvSales.getItems().size() * 44.0 + 52));   // call after every reload of the sales list
- No fx:id / onAction changed (checked by script). All 30 FXML files parse.

Round 17 (v17)
- INVENTORY GRID: photo / header shapes use radius 23 = the INNER curve of the card / table border, so nothing overshoots the border any more.
    Small Edit / Restock / delete buttons on the cards use radius 10 (they were turning into pills).
    If the controller builds the cards in Java, add the same clip to each photo box:
        Rectangle clip = new Rectangle(); clip.setArcWidth(46); clip.setArcHeight(46); clip.setHeight(170);
        clip.widthProperty().bind(photoBox.widthProperty()); photoBox.setClip(clip);
- INVENTORY LIST + GRID: category chips and "Add product" are 44 high like the search box and the List | Grid switch (chips were nearly pills).
    Table header follows the table's inner corner (23).
- LOADING: flower is about 40% smaller, canvas 240 x 110 (ImageView fitHeight 110, window 250 high).
- LOW STOCK: the three "5" rows are back; only the "Stock at or below" spinner starts at 0.

Round 18 (v18)
- FILTER CHIPS (Inventory list + grid: All / Bouquet / Crochet / Keychain; also Orders: All / Pending / Done / Cancelled) and the Pickup / Meetup buttons:
    one background, inset 0, radius 14 in every state (normal, hover, focused, selected) and no square focus ring, so the corners are the same everywhere.

Round 19 (v19)
- INVENTORY_GRID: removed the rounded <clip> that v17 added to each photo box - it made the whole photo area (pink background + "Product photo") disappear.
    The photo box is back to the pink placeholder with rounded top corners from CSS (radius 23). Rounding a REAL photo needs the Java clip shown in Round 17.

Round 20 (v20)
- LOADING: loading_flowers.gif redrawn from scratch - ONE flower with 8 separate petals (outlined, lighter tips) and a yellow centre.
    Spins slightly while it blooms, holds, then closes and blooms again. Canvas 240 x 110, same file name, no FXML change.

Round 21 (v21)
- INVENTORY_LIST: the numbers on the category chips are now a small rounded badge to the right of the word (All 44 / Bouquet 24 / Crochet 6 / Keychain 14),
    pink on a blush badge, translucent white when the chip is selected. Chip text is now only the word ("Bouquet"), the number is the chip's graphic (class chip-count).
    If the controller sets the chip text with the count (e.g. chipBouquet.setText("Bouquet " + n)), change it to chipBouquet.setText("Bouquet") and
    ((Label) chipBouquet.getGraphic()).setText(String.valueOf(n)).

Round 22 (v22)
- LOADING: back to the ORIGINAL loading_flowers.gif and the original LOADING.fxml (same as v15).

Round 23 (v23)
- INVENTORY_GRID: product photos are now CSS backgrounds (class pic-<name> on each .prod-photo box, rules at the bottom of styles.css) instead of
    ImageView + <clip> with ${phN.width} bindings. Those bindings made Scene Builder show a blank photo area and stretch the first column
    (one huge card + cut-off second column). Now: 3 equal columns, photo fills the box (cover), no bindings. No fx:id / onAction changed.
- Photo corners: JavaFX does not round a CSS background image. If the photos' top corners poke past the card's rounded border in the running app,
    use the Round 17 Java clip on each photo box (or on the ImageView you create in the controller).

Round 7 (v26)
- INVENTORY_GRID photos: every product photo was re-cropped (subject centred, 2.8:1) and saved with ROUNDED TOP CORNERS into images/card/*.png. styles.css .pic-* classes now point there. Original images/*.jpg untouched. Works in Scene Builder, no Java needed.
- .prod-photo uses background-size 100% 100% (matches the crop exactly); if you change the photo height (140) the crop will look squeezed.
- Cards have a soft pink drop shadow (.prod-card); grid padding increased so the shadow is not cut off.
