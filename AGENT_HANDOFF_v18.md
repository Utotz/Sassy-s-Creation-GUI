# AGENT_HANDOFF - Sassy's Creation Java GUI ("C to Java Sassy's Creation")

**Version:** v18  
**Written:** 2026-11-27  
**Repo:** github.com/Utotz/Sassy-s-Creation-GUI  
**Local project:** C:\Users\User\IdeaProjects\SassysCreationGUI  
**Last verified state:** `submain` = `origin/submain` = `a702a7d` (pushed 2026-11-27)

## 0. How to use this file (read first)

1. Read this whole file before doing anything. Do not ask Ott again for details written here.
2. Inspect the real files, screenshots or pasted text before editing or advising. This file is a map, not proof.
3. Edit this file in place at the end of each session, bump the version, and add to the session log (section 8), the decision log (section 9) and the open items (sections 10 and 11).
4. Give new tasks that fit the goal (section 1), the rules (sections 12 and 13), the structure (section 5) and the way Ott works.
5. Anything marked **UNVERIFIED** has no evidence yet. Do not treat it as done.

**Provenance note:** v18 was assembled from the v17 session summary Ott pasted, the saved project notes, and the v18 session's own evidence (screenshots, pasted terminal output). The text of the v17 file itself was not available when v18 was written. If v17 holds sections or rules that are missing here, merge them in rather than dropping them. Earlier task history (T1 to T12) is therefore not repeated in section 8.

---

## 1. Goal

Rebuild Ott's original C-based Sassy's Creation capstone as a Java GUI application: a sales, order and inventory management system with a MySQL-compatible database.

- Approved title (Section 2C, submitted 2026-10-15): "Sassy's Creation: A Java GUI-Based Sales, Order, and Inventory Management System with MySQL Database". Type: GUI application.
- Client: Alyssa Delos Santos (agreed to be the client).
- The project also feeds a mini-thesis/paper and a defense (ERD, test plan, likely defense questions). No invented citations or results.
- Defense date: said on 2026-11-20 to be "next week". **Exact date not confirmed.** Ask Ott; it decides task order.

## 2. Team and roles

| Person | Role |
|---|---|
| Nefthali Achilles Raven Guido ("Ott", "Raven") | Group leader and the only backend member. Owns database and DAOs, and guides the others. |
| Ravi Jed Matocdo ("Ravi"; "Ravs" in the 2026-11-22 task split, assumed the same person; Git author shows as "Dejjo", branch `tempniDejjo`) | GUI/front end (FXML, controllers' UI parts). |
| Angelina Coca ("Ange") | Papers. |

- Ravi and Ange know only a little Git/GitHub and often forget commit/push/pull.
- 2026-11-22 task split as Ott wrote it: Raven = ID in inventory and stand sale, stand sale records table, completed orders + revenue, done/pending/cancelled summary. Ravs = product dropdown, change password, stand sale date arrow, newest/oldest, green stock, order revenue on dashboard, revenue-by-day chart, dashboard month filter. Ange = papers.

## 3. Toolchain and environment

- IntelliJ IDEA (Community; "Unlock Ultimate" button visible), Maven, JavaFX with Scene Builder, JDBC.
- Earlier recorded: JavaFX 25.0.5 resolved, MariaDB Connector/J 3.5.7, JDK 25.0.4.1 (C:\Program Files (x86)\Java\jdk-25.0.4.1+1). Check `pom.xml` for the current versions.
- Run the app with `javafx:run` (Maven tool window: Plugins > javafx > javafx:run), which points at `Launcher`. The run configuration "Current File" cannot launch a DAO class: pick the Maven goal.
- Database: XAMPP 8.2.12, MariaDB 10.4.32, database `sassys_creation_db` on localhost:3306, user `root`, empty password. Kept as "MySQL database" because it is MySQL-compatible.
- IntelliJ's Database tool window can show a stale copy of the structure after an `ALTER TABLE`: right-click the data source > Refresh. A red "Unable to resolve column" from this is an IDE inspection, not a compile error.
- Git is used from PowerShell. Ott prefers `git add .`.

## 4. Git: repo, branches, last known state

| Branch | State |
|---|---|
| `submain` | Integration branch. `a702a7d` pushed 2026-11-27 (fast-forward of T15). |
| `products-soft-delete` | T15 work, same commit `a702a7d`. Keep until Ravi confirms his side. |
| `merge-dejjo-3` | Used for the T13/T14 merge, at `5b51cda`. |
| `tempniDejjo` (Ravi) | `origin/tempniDejjo` = `5b51cda` at last fetch, so he has merged `submain` up to then. He does not yet have T15. |
| `main` | Stable branch. Promote from `submain` only after tests. |

- Recent commits, newest first: `a702a7d` T15 soft delete + restore on re-add; `8c2b5f3` T15 hide inactive products, add is_active column script; `5b51cda` merge of Ravi's `a9eea9a` (T13/T14 included).
- Rules: no committing compiled `target/` files (an earlier merge had 15 conflicts that were only `target/`); no ZIP or manual file copying between people; do not modify teammates' branches directly.
- Working tree on `submain` after the push: clean as of the last command output. **UNVERIFIED** for later changes.

## 5. Structure and conventions

- Package root: `com.sassyscreation.sassyscreationgui`. Sub-packages: `controllers`, `dao`, `db`, `model`.
- FXML and images: `src/main/resources/com/sassyscreation/sassyscreationgui/` (images under `images/`).
- DAOs: `ProductDAO`, `StandSaleDAO`, `OrderDAO`. Models: `Product`, `StandSale`, `Order`. Connection: `db/DBConnection.getConnection()`.
- Controllers seen: `InventoryController` (static `PRODUCTS` list, `loadProductsFromDB()`), `AddProductController`, `EditProductController`, `DeleteProductController`, `RestockController`, `StandSaleController`, `OrdersController`, `SidebarController`. Screen switching goes through `ScreenTransition`.
- Test classes: `DatabaseTest`, `ProductDAOTest`, `StandSaleDAOTest`, `OrderDAOTest`. **`StandSaleDAOTest` writes real data: do not re-run.** `OrderDAOTest` is read-only.
- Banner comments mark logic sections in code (e.g. "LOGIC: READ ALL PRODUCTS"). Keep that style for new logic.
- The DAO owns SQL; controllers call the DAO and then reload the list so the screen matches the database.
- SQL scripts in `database/`: `schema.sql` (drops tables: never run on a live database), `seed.sql`, `test-seed.sql`, `add_image_path.sql`, `products_soft_delete.sql` (run once only).

## 6. Database state

- `products`: 44 original products (ids 1 to 44), plus a hidden test product `ZZ Delete Test` (id 45, `is_active = 0`) in **Ott's local database only**. Columns include `product_id`, `name`, `category`, `price`, `stock_quantity`, `low_stock_threshold` (default 3), `is_customizable` (TRUE for ids 35, 42, 43, 44), `image_path`, and `is_active TINYINT(1) NOT NULL DEFAULT 1` (T15).
- Constraints: `chk_price` (negative price rejected, tested), and `uq_products_name` UNIQUE on `name` (T15). Name comparison follows the default collation, so differences in letter case likely count as the same name.
- `stand_sales`: exists; empty at last report (test sale removed, stock restored).
- `orders`: 4 TEST rows (order_id 1 to 4: Ana Done, Ben Done, Cara Pending, Dan Cancelled; products 2, 35, 2, 42). Revenue from completed orders printed 330.00 in `OrderDAOTest`.
- Sharing the database with teammates: SQL files in git, imported through phpMyAdmin. XAMPP data folder is never committed.
- Ravi's database: **UNVERIFIED** whether it has `is_active`. He must check and, only if the column is missing, run `products_soft_delete.sql` once (see section 11, item 1).

## 7. Verified working (evidence in chat)

- Inventory loads 44 products, sorted by ID (T13, reported by Ott).
- `ProductDAO.getAllProducts()` filters `WHERE is_active = 1`: hiding product 42 in phpMyAdmin gave 43 in Inventory and restoring it gave 44.
- Delete in Inventory calls `ProductDAO.softDeleteProduct()` and the product stays gone after restart (phpMyAdmin showed id 45, `is_active = 0`).
- Re-adding the same name restores the same id (id 45 kept, `is_active = 1`, one row). Adding a duplicate while active fails with the generic "Could not save product".
- Stand Sale product list stops offering a hidden product, immediately and after restart.
- Stand Sale record/delete, Inventory restock, Orders read-only DAO methods: reported working in earlier sessions.

## 8. Session log

### v18 session (2026-11-27)

- Reviewed branches and the v17 handoff: its T14 instructions were outdated because Ravi had pushed more work.
- T13 (Inventory sort by ID) and T14 (Dashboard chart fit) pushed on `submain` (`5b51cda`) after merging Ravi's `tempniDejjo` (`a9eea9a`) on `merge-dejjo-3`, no conflicts, no `pom.xml` change. An accidental paste of old terminal output into PowerShell did no damage (merge intact, status clean).
- T15 product soft delete:
  - `database/products_soft_delete.sql` run once on Ott's database.
  - `ProductDAO.getAllProducts()` filters active products.
  - `ProductDAO.softDeleteProduct(int)`: `UPDATE ... SET is_active = 0 WHERE product_id = ? AND is_active = 1`, returns true only if a row changed.
  - `ProductDAO.insertProduct()` now looks for a hidden product with the same name first. If found, it updates that row (name, category, price, stock, image) and sets `is_active = 1`, returning the old id. Otherwise it inserts normally. An active duplicate still fails on `uq_products_name`.
  - `InventoryController.deleteProduct()` calls `softDeleteProduct`, then reloads the list. If nothing changed, it shows a "Not deleted" warning.
  - Merged by fast-forward and pushed: `submain` = `a702a7d`.
- A message to Ravi with the safe order (check column, check duplicate names, run SQL once, then merge `submain`) was drafted. **UNVERIFIED** whether it was sent.

## 9. Decision log

| Decision | Reason | Alternatives | How to defend it |
|---|---|---|---|
| Soft delete for products (`is_active`) | Orders and sales point to products; a deleted product must stay gone after restart; ids must never be reused | Hard `DELETE` (breaks or loses history, loses the id) | Old records keep a valid product; the id is permanent |
| Re-adding a deleted name restores the old id | Ott's rule: a deleted and re-added product keeps its id, only a new product gets a new higher id | `ON DUPLICATE KEY UPDATE` (rejected: it would silently overwrite an active product) | An active duplicate still fails visibly through `uq_products_name` |
| Restore takes the new form values (price, stock, category, image) | Simplest behavior consistent with "re-adding" | Keep old stock/price | **Assumed, not explicitly confirmed by Ott.** Confirm if it matters |
| Active filter lives in `ProductDAO.getAllProducts()` | One rule for Inventory and Stand Sale | Filter in each controller | No repeated logic |
| History joins (`StandSaleDAO` `JOIN products`, `OrderDAO` `JOIN products`) stay unfiltered | Old sales and orders must still show their product | Add `is_active = 1` there | Past records stay readable. **Not yet tested with a sold-then-deleted product** |
| Completed orders, revenue and status summary are SQL queries on `orders` | No extra physical tables (2026-11-22) | Separate tables | Fewer places to go out of sync |
| Order revenue by day/month uses `orders.schedule_date` | No `completed_at` column for now | Add the column | Matches current schema |
| Regular orders are 1 unit and deduct 1 stock; customizable orders may have quantity > 1 with a per-unit agreed price and no ready-made stock deduction | From the v5 handoff | n/a | Reflects how the shop works |
| No Material category (only Bouquet, Crochet, Keychain) | 2026-10-24 decision | n/a | Matches the shop's products |
| Every section shows an ID that never changes | Ott's request 2026-11-26 | n/a | Needed for traceability |
| Branch flow: feature branch, fast-forward or merge into `submain`, test, push, promote to `main` after tests | Team keeps `main` stable | n/a | Teammates are new to Git |

## 10. Known problems and gaps

- Not saved to the database yet: Inventory **Edit**, Stand Sale **Edit**, Orders **Add, Edit, Delete**.
- Add Order product picker is hardcoded (3 items: Customized Keychain, Hat - Customize, Flower Bouquet). When Orders is connected, it must load only active products.
- Dashboard still uses sample data. Ott wants it finished, with the revenue graph on real database data (a reminder he asked for).
- Forgot password step 2: `RESETPASSWORD.fxml` has a broken tag, found by reading the file in the v17 session. **UNVERIFIED**: Ott has not tried that screen.
- Add Product shows the same generic "could not be saved" for a duplicate name as for any database failure. A friendlier duplicate-name message would be a small later change.
- In `InventoryController.deleteProduct()`, the catch block says "Could not open delete dialog" even for a database error.
- When Inventory Edit is wired, renaming a product to a name used by a hidden product will hit `uq_products_name`. Decide the behavior then.
- Photos: `add_image_path.sql` UPDATEs may still need to be run in some databases (image_path was empty in Ott's database on 2026-11-24). **UNVERIFIED** now.
- Whether history screens show a sale or order of a product that was hidden later: **UNVERIFIED** (no such data was tested).
- `AGENT_HANDOFF.md` at the repo root still holds the v17 text until Ott replaces or merges it with this file.

## 11. Next steps (in this order, unless Ott's defense date changes it)

1. **Ravi's database and merge.** He runs `SHOW COLUMNS FROM products LIKE 'is_active';`. If no row: run `SELECT name, COUNT(*) FROM products GROUP BY name HAVING COUNT(*) > 1;` (expect no rows), then `products_soft_delete.sql` once, then merges `origin/submain` into `tempniDejjo` and confirms Inventory loads. Never pull before the database step.
2. **Run once on `submain`**: Inventory shows 44. (The code is identical to the tested branch, but the check was not reported.)
3. **Ask Ott the defense date**, then choose: Orders slice (connect Add/Edit/Delete to the database, picker loads active products), Stand Sale Edit (Ott chose to wire it, not hide it), or the Dashboard on real data (graph). The earlier plan was Orders slice first, then Edit sale, then the Dashboard.
4. Test a sold-then-hidden product against the history screens.
5. Optional cleanup: friendlier duplicate-name message, fix the delete catch message, check `RESETPASSWORD.fxml`.
6. Promote `submain` to `main` only after tests; keep the paper/defense materials in step.

## 12. How the AI works with Ott

- Always write in English.
- Ott is a beginner in many areas but must be able to explain, modify, troubleshoot and defend the code as his own. Teach from the basics, define terms and abbreviations on first use, say what a change does, why it belongs in that file, and how to verify it.
- Collaborative: a thinking partner, not a yes-machine. Both sides propose and challenge ideas. If earlier advice was wrong, say so directly, correct it and explain what changes.
- For meaningful design decisions use: Goal / Options / Trade-offs / Recommendation / What to understand first / How to verify. Skip for tiny actions. When disagreeing, name the specific problem, give an alternative and let Ott respond. If he overrides and it is safe, implement it and note the trade-off. If it would materially break the project, say so.
- **Evidence first.** Inspect actual files, screenshots or pasted text before editing. Never assume a file, dependency or setting exists. Never say a step worked without his shown or reported evidence. Distinguish compile error, IDE warning, runtime warning and runtime exception. Give exact UI directions (panel, menu, file, where in the code, what should visibly change).
- **Change control.** One small reversible change at a time with the expected result, then wait for his result. Do not overwrite a whole file for one setting. No duplicate dependencies or classes. Ask before adding libraries, frameworks, patterns or expanding scope. Verify a dependency version actually resolves before recommending it. Build one vertical slice at a time rather than many screens with fake data.
- For anything that changes data, settings or Git state: think through the consequences first and say what NOT to do before the recommended step.
- Do not invent features, requirements, business rules, citations or test results. Separate verified facts from assumptions and plans. Treat this handoff and the requirements files as the source of truth.
- Keep a concise decision log and a known/unknown/completed list across sessions (sections 7, 9, 10).
- Ott often sends screenshots; text pasted from the terminal or console is preferred when possible because it is cheaper and more exact.
- Send next tasks about 3 at a time, aligned with the goal, structure and style. Do not end a response by only offering help: make the next actionable step clear. Do not address him by name unless he asks.
- Tasks must be explained well enough to be understood and copied, with the real project files as examples.

## 13. Coding rules and defaults

- Parameterized SQL only (never concatenate input). Try-with-resources for connections.
- Validate in the right layer plus database constraints. Keep secrets out of source control.
- Prefer simple code Ott can defend over clever abstractions.
- Never commit `target/`, XAMPP data, or secrets. Do not run `schema.sql` on a live database (it drops tables).
- Banner comments mark logic sections.
- Git in PowerShell, `git add .`, one feature branch per task, confirm `git status` before committing.

## 14. Unknown or to verify next session

- Defense date.
- Whether the message to Ravi was sent, and the state of his database.
- Whether `git status` on `submain` is still clean and the app runs on `submain` (Inventory 44).
- Whether v17 holds content missing here (merge it).
- Whether the restored product should keep its new form values or its old values (section 9).
- Contents of other branches and any commits Ravi has pushed after `5b51cda`.

## 15. Start of next session

1. Read this file, then ask for current `git status`, `git log --oneline -5` and the branch name, as text.
2. Fetch to see Ravi's newer commits: `git fetch origin` and `git log --oneline submain..origin/tempniDejjo`.
3. Continue from section 11. Bump this file to v19 at the end of the session.
