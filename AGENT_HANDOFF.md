# AGENT_HANDOFF.md — Sassy's Creation (Java GUI) — living handoff (detailed edition)

**Handoff version: 15**
**Last updated:** 2026-11-24 (Asia/Manila) — written by a Claude chat session
**State described:** `origin/submain` = `ed1471d`. **Plus** one change that exists only in the user's working tree and is **not pushed yet**: *Step B* (Record Sale calls `insertSale`). It was shown working on screen on 2026-11-24 (section 0, E.4). Plus this file and the banner comments, once the user commits them.
**Replaces:** v14 (the compact 299-line edition). v14 itself replaced `SASSYS_CREATION_JAVA_GUI_HANDOFF_v13.md` (about 356 KB, kept by the user **outside** the repo). The session that wrote v15 **could not read v13**; v15 keeps everything v14 contained, adds the history that survives in the assistant's notes of earlier chats (section M) and everything from the 2026-11-24 session. If a detail seems missing, ask the user for the matching v13 section.

> One file, edited in place, version number goes up every session. Do **not** create a second handoff file. This file lives on `submain` only (`main` is still the initial commit and has no handoff).

---

## 0. WHERE WE LEFT OFF (read this first, 2026-11-24)

- **The defense of the whole system is "next week"** (user, 2026-11-24). What it must show is still unknown (Q1).
- **Pushed this session:** the teammate's `dfb656b` merged into `submain` (merge `37c10a2`, then `57428b0` with the user's one-line fix that widens the Stand Sale Actions column); `StandSaleDAO.insertSale` (`8ba9563`); Stand Sale loads its products and sales from the database (`81d50de`, "Step A"); searchable product dropdown (`ed1471d`).
- **Done and working on the user's computer, NOT pushed:** *Step B*. In `StandSaleController.handleRecordSale()` the three old in-memory lines (`// TODO: also INSERT...`, `p.stock -= qty;`, `allSales.add(...)`) were replaced, as the agent instructed, by `standSaleDAO.insertSale(p.id, qty, LocalDate.now())` inside a `try/catch (SQLException)` (warning and `return` on failure), followed by `loadProducts(); loadSales();` (the user's exact local file was not shown to the agent, so ask for it or a screenshot before editing that method). Evidence (screenshots, 2026-11-24): the sale row appears, the chips show 1 sale and ₱30.00, Top sellers shows 1 sold, the stock note shows 19, Inventory shows 19, phpMyAdmin `stand_sales` holds the row, and the "Not enough stock" warning appears. The user said "all goods".
- **Left in the database on purpose:** one test sale (`sale_id` 2, `product_id` 2, quantity 1, price 30.00, date 2026-11-24) and product 2's stock at 19 instead of 20. The user **paused the cleanup**. Cleanup SQL is in E.2. **Do not ask him to re-run `StandSaleDAOTest`**: it writes real data.
- **Next, in this order:** (1) commit and push Step B (gate already met, see T8); (2) commit the banner comments as their own commit; (3) commit this file; then Batch 4 (F.3): decide Delete/Edit sale, and the sync with the teammate.
- **Open since this session:** the user has not said whether the layout message went to the teammate (K.3); the Delete icon is still partly clipped (D55); the Top sellers list does not scroll (D56).

---

## A. HOW TO USE THIS FILE (every agent, every session)

**Token rule: do not read the whole file by default.** Read 0, A, B, C, E, F, G in that order. Sections D, H–L and M are reference: open them only when the task needs them. (This edition is long on purpose, see L43.)

### A.1 Start of session
1. Read 0, A, B, C, E, F, G.
2. **Verify before trusting.** If you can run git: `git fetch origin`, then compare `git log -1 origin/submain` with "State described" above and read any newer commits first (the GUI teammate pushes often). If you cannot run git, ask the user for these four outputs and nothing else: `git status`, `git log --oneline -5`, `git fetch origin`, `git log --oneline -3 origin/tempniDejjo`.
3. Tell the user in 5 lines or fewer: what is verified, what is unverified, what comes next. **Do not ask for anything already written here.**
4. Check the available skills before starting (rule C.1 #9).

### A.2 End of session (only if something changed)
1. Raise the version (N → N+1) and the "Last updated" line at the top. Rewrite section 0 so it describes where the session stopped.
2. Add **one row** to the Changelog (section L): what changed and the evidence.
3. Edit **only the sections that changed**: E (state), F (tasks), H (decisions), I (corrections), J (defects), M (history, append one line). Do not rewrite untouched sections.
4. Move finished tasks from F.3 to F.2 (one line each, with an evidence tag and the commit id).
5. Write the **next batch of at most 3 tasks** in F.3. Every task must follow B (goal), C (rules) and D (structure and style).
6. **Size:** this is the detailed edition (L43): there is no hard line budget, but keep sections 0, E, F and G short enough to read in about five minutes, and compress the oldest Changelog rows. **Never delete a decision, a correction or an open defect**: mark it "superseded" or "resolved" instead.
7. **Agents that cannot write to the repo** (chat agents): output the changed sections as a **HANDOFF PATCH** (section letter plus the full new text of that section) or the whole file, and tell the user which to paste over. **Agents with file tools:** use small edits in place.
8. The **user** commits (agents cannot push). Give the commands (PowerShell, one per line):
   ```powershell
   git status
   git add .
   git commit -m "Update AGENT_HANDOFF to v<N>"
   git push
   ```
   `git add .` is acceptable only after the user has read `git status` (rule C.4).
9. **Never put secrets, e-mail addresses, passwords, full personal names or other personal data in this file.** The repository is readable without a login (an anonymous `git clone` worked on 2026-11-24), so treat it as public (open question Q3). Team members appear under the handles already used here.

---

## B. PROJECT AND GOAL (do not change without the user's decision)

- **Project:** "Sassy's Creation: A Java GUI-Based Sales, Order, and Inventory Management System with MySQL Database" (approved title; GUI Application; Section 2C; submitted 2026-10-15). It is the group's original **C-language capstone (CC103) rebuilt in Java with a GUI**. It is **not** a new project: do not start over. The original system's features are described in the course's "PD" documents (PD1 to PD6); an earlier, incomplete Android Studio version ("SassyCreationStudio v3") was used **only** as a reference for structure and design.
- **Stack:** Java 25, JavaFX 25.0.5, Maven, IntelliJ IDEA, MariaDB 10.4.32 via XAMPP (MySQL-compatible; kept on purpose for the "MySQL Database" title, decided 2026-10-25), MariaDB Connector/J 3.5.7. `pom.xml` is leader-edited: do not change it. Scene Builder is used for the FXML screens.
- **Goals (in priority order):**
  1. A working JavaFX desktop app: login, dashboard, orders, stand sales, inventory, backed by MariaDB (`sassys_creation_db`).
  2. **The user can explain, modify, troubleshoot and defend every line** at the oral defense. On 2026-11-24 he said the defense of the whole system is "next week"; which screens, documents and questions it covers is unknown (Q1).
  3. The project feeds a mini-thesis and defense (ERD, test plan, likely defense questions). **No invented citations or results.**
  4. Keep the pink/crimson visual identity (the GUI teammate's Design 2.0 is the design on `submain`).
- **Team (3):**
  - **The user** = group leader and the **only backend member**. His name in the team task list is **"Raven"**.
  - **The GUI teammate** (Git author **"Dejjo"**) works on branch `tempniDejjo`.
  - **"Ravi"** is the teammate the user sends tasks to (user, 2026-11-24). He said the controllers are edited by **either him or Ravi**. **"Coca"** is the third member (the papers, "Ange" in the task list; the match Coca = Ange is likely but not confirmed). The task list also names **"Ravs"** (GUI items). **Whether Dejjo = Ravi = Ravs is not confirmed by the user: do not assume it, and do not assume anyone's gender.**
- **Language:** English only for all guidance (not Tagalog/Filipino).

---

## C. RULES (binding for every agent)

### C.1 How the AI interacts with the user
1. **Thinking partner, not a yes-machine and not a code dumper.** Both sides propose and challenge; neither auto-agrees nor auto-rejects; options are judged by reasoning, constraints and trade-offs. If an earlier reply of yours was wrong, **say so directly, correct it and explain what changes** (and log it in section I).
2. **Meaningful design decisions** use: Goal / Options / Trade-offs / Recommendation / What to understand first / How to verify. Skip it for tiny actions. When disagreeing: say so plainly, name the specific problem, give an alternative, let the user respond. If the user overrides and it is safe and feasible, implement it and note the trade-off; if it would materially break the project, say so.
3. **Teach.** The user is a beginner in many areas. Teach from the basics, define terms and abbreviations on first use, say what a change does, **why it belongs in that file** and how to verify it. Prefer small steps he can type and understand over dumping a whole app (a full code block is fine when it is clearest, but break it down and say what to replace or keep). Use examples from his actual project.
4. **Evidence.** Inspect the actual files, screenshots or state **before** editing or advising. Never assume a file, dependency or setting exists. **Never say a step worked without shown or reported evidence** (build, run, SQL, commit, push, merge, test). Distinguish compile error / IDE warning / runtime warning / runtime exception. Give exact UI directions (panel, menu, file, what should visibly change) based on his screenshots.
5. **Change control.** One small reversible change at a time, with the expected result; wait for his result or screenshot before stacking unrelated changes. No overwriting a whole file/POM/class for one setting. No duplicate dependencies or classes. **Ask before** adding a library, framework or pattern, or widening scope. Verify a dependency version resolves before recommending it. Build **one vertical slice** (a real end-to-end feature) at a time, not many screens with fake data.
6. **Honesty and records.** Do not invent features, requirements, business rules, citations or test results. Separate verified facts from assumptions and plans. Keep this file's decisions, corrections and defects up to date.
7. **The user types the code.** The agent may generate code, explain and check it, but must not silently write the user's project files, create ZIPs or replace files wholesale. (This file and the banner-comment guide are exceptions, at his request.) **When you tell him to add a method, show the lines around it (or the whole file) so it lands in the right place** (see C45).
8. **Risky operations** (Git history, merges, deleting files, SQL that drops data): think through the consequences first and **say what NOT to do before** the recommended step.
9. **Skills:** check the available skills before starting a task and use the appropriate ones (they can be combined).
10. **Reply style:** practical, direct, honest, thorough. Do not end a reply by merely offering to do work: **make the next actionable step explicit.** Do not call the user by name unless he asks. He often communicates through screenshots. Answer long pasted output by naming the few lines that matter, not by restating it.
11. **Tasks are delivered 3 at a time** (user's request, 2026-11-22). Each task states: what, why, **exact file path**, code (if any, with what to replace or keep), expected result, how to verify, what NOT to do.
12. **Commands must be copy-safe.** The terminal is **Windows PowerShell**: it rejects `&&` (use separate lines or `;`), paths are Windows paths, and commands are given **without** the `PS C:\...>` prompt ("paste only the command"). Say what output to expect. Prefer `git add .` over staging many paths one by one (user preference), after `git status`.
13. **Gate before irreversible steps.** Do not tell the user to commit, merge or push a slice until the checks you listed have been shown (decision L34).
14. If the user uploads a handoff or master-prompt `.md`, read it fully and continue from its stated state without re-asking details already written there.
15. **Code comments.** The user wants banner comments that mark specific logic (for example "logic responsible for searching in the Stand Sale"). Follow C.5 whenever you add or change logic, and do not leave comments that describe old behaviour (defect D58).

### C.2 Architecture rules
- Layers: `controllers` (screens) → `dao` (SQL only) → `db` (connection); `model` holds data classes. **No SQL in controllers or FXML; no business rules in event handlers.** A service layer is planned only when stock rules need transactions across DAOs (ask first). *(Today the stock rule lives inside `StandSaleDAO.insertSale` as one transaction, decision L45.)*
- Money is `BigDecimal` in Java and `DECIMAL(10,2)` in SQL. Dates are `LocalDate` / `DATE`. *(The Stand Sale screen still keeps money as `double`: defect D62.)*
- Database constraints back up the Java validation (`CHECK`, `FOREIGN KEY ... ON DELETE RESTRICT`).
- `database/schema.sql` **drops tables** (`DROP TABLE IF EXISTS`): **never run it, `seed.sql` or `add_image_path.sql` to "test" something.** Changes to a live database are done with additive SQL (for example only the `CREATE TABLE` block).
- Do not edit the GUI teammate's FXML or styles without asking. No new dependencies; do not touch `pom.xml`.

### C.3 Coding defaults
Parameterized SQL (never concatenate input); try-with-resources for connections; validate in the right layer plus database constraints; keep secrets out of source control; prefer simple code the user can defend over clever abstractions. Avoid features the course has not taught (streams, `super`, etc.) unless there is a reason, and **ask before** using one.

### C.4 Git rules
- **One short-lived local slice branch per slice** → check → `git switch submain` → `git merge --ff-only <slice>` → push. *(On 2026-11-24 the slices were committed straight on `submain` after the evidence was shown: see C43 and Q9.)*
- Integration/backup branches stay **local** and are never pushed (`merge-dejjo-2` was one). Never push `main` (it is still the initial commit). Never push or merge into the teammate's branches (`GUI`, `tempniDejjo`).
- Before merging a teammate branch: `git fetch origin`, then a **trial merge** (`git merge-tree --write-tree --name-only origin/submain origin/<branch>`), then a throwaway local branch. After every merge check `git diff --stat -- pom.xml` (it can silently take the teammate's older versions).
- When a merge conflicts only in `target/` (compiled output): resolve by **deleting** it (`git rm -r -f target`, `-f` because some files are staged), never by "accept theirs" (decision L47).
- `git add .` is fine **after** reading `git status` (no stray files such as `target/`, `.idea/`, `product-images/`).

### C.5 Code comment convention (decision L49)
- **Banner style = the style already used in `StandSaleController`** (the teammate's section banners):
  ```java
      // =====================================================================
      //  LOGIC: <what this piece is responsible for, in capitals>
      //  <2 to 8 lines: how it works, what it depends on, what must not be broken>
      // =====================================================================
  ```
  A banner starts with `LOGIC:` (or `WARNING:`) so it can be found with Ctrl+Shift+F "LOGIC". Name the responsibility: "LOGIC RESPONSIBLE FOR SEARCHING PRODUCTS IN THE STAND SALE DROPDOWN", "LOGIC RESPONSIBLE FOR RECORDING A SALE AND DEDUCTING STOCK (TRANSACTION)".
- Put a banner above each **specific logic block** (search, filter, record, load, transaction, ranking, export), not above every method.
- Comments say **why and what depends on what**, not what the next line obviously does. Mark unfinished behaviour honestly (`SCREEN ONLY, NOT SAVED`), and delete comments that became false.
- **Scope today:** the backend files and `StandSaleController`. Do **not** add banners to the teammate's other controllers or FXML until Q2 is settled (merge-conflict risk). The ready-to-paste banners are in `BANNER_COMMENTS.txt` (delivered with this file; each anchor was verified on `ed1471d`).
- Comments change no behaviour; commit them **separately** from logic changes.

---

## D. STRUCTURE AND CODE STYLE (verified on `ed1471d`)

```
src/main/java/com/sassyscreation/sassyscreationgui/
  Launcher.java (9 lines), SassyCreation.java (19)   entry points (mainClass = Launcher)
  ProductDAOTest.java (19), StandSaleDAOTest.java (31)   manual test runners (main + println), root package
  db/DBConnection.java (17)                 getConnection(); hardcoded local dev connection
  model/   Product (58), StandSale (48)     immutable data classes
  dao/     ProductDAO (63), StandSaleDAO (96)   SQL only, one DAO per table
  controllers/  22 files                    screen controllers + helpers (plural "controllers" is the teammate's convention)
                StandSaleController.java is 652 lines
src/main/resources/com/sassyscreation/sassyscreationgui/
  31 flat FXML files, styles.css, text-large.css, images/ (flat product photos + card/), fonts, drawables
database/  schema.sql (DESTRUCTIVE), seed.sql (44 products), test-seed.sql, add_image_path.sql
product-images/  one stray test photo tracked on submain (came in with the teammate's commit): defect D57
module-info.java: exports root + controllers, opens controllers to javafx.fxml (dao/model stay internal;
  no PropertyValueFactory in src/main, so no `opens ...model` is needed today)
```

**Style samples to copy (from `ProductDAO`, `Product`, `StandSale`, `StandSaleDAO`):**
- 4-space indent, K&R braces, one-line getters, `this.` in constructors, `toString()` on models.
- Model classes: `private final` fields, one constructor, getters, no setters; derived values (for example `getTotal()`) are methods, not columns. One-line `/** ... */` for non-obvious fields.
- DAO methods: `public List<X> getAllX() throws SQLException`, `insertX(...)`, `deleteById(int id)`; SQL in a `String` split with `+` (trailing space before each line break); `PreparedStatement` with `?`; `try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = ...; ResultSet rs = ...)`. A **transaction** (`insertSale`) uses `setAutoCommit(false)`, `commit()` and `rollback()` in a `catch`, on one connection.
- SQL names: `snake_case`; constraints named `chk_*` and `fk_*`.
- Test runner: `XxxDAOTest.java` in the root package, `main` prints results and catches `SQLException`. A test that writes data must say so in a `WARNING:` banner (D59).
- Comments: banner style C.5.
- Do not create a second class with a name that already exists (`Product` is already duplicated, see D49).

---

## E. CURRENT STATE (as of 2026-11-24)

### E.1 Repository
| Branch | Head | Notes |
|---|---|---|
| `main` | `300d9aa` | Initial commit, untouched (only `README.md`). Has no handoff file. |
| **`submain`** | **`ed1471d`** | Integration branch, pushed. Since v14 (`651fa59` = the v14 file): `37c10a2` merge of the teammate's `dfb656b` (made on local branch `merge-dejjo-2`), `57428b0` "Widen stand sale actions column", `8ba9563` `insertSale` + test, `81d50de` Stand Sale loads from the database (Step A), `ed1471d` searchable dropdown. `target/` is **not** tracked (0 files). |
| **Working tree (not pushed)** | — | *Step B* in `StandSaleController.handleRecordSale()` (see section 0). Expected `git status`: only `StandSaleController.java` modified, before the banner comments and this file are added. |
| `tempniDejjo` | `dfb656b` | The teammate's working branch. **Fully merged into `submain`.** No newer push seen on 2026-11-24. It still tracks compiled `target/` files, so every future merge of it conflicts there (D39). |
| `merge-dejjo-2` | `57428b0` | **Local only**, identical to `submain` at that point. Optional cleanup: `git branch -d merge-dejjo-2` (refuses if anything were unmerged). |
| `GUI` `2860443`, `JAVA+DB` `567a3de`, `inventory-read` `3f84784` | | Old/redundant (ancestors of `submain`). |

**The 2026-11-24 merge [VERIFIED-USER, terminal output]:** `git merge origin/tempniDejjo` on `merge-dejjo-2` reported **15 modify/delete conflicts, all under `target/classes/...`** (compiled output that `submain` had deleted). Resolved with `git rm -r -f target` (a plain `git rm -r target` refused because two new `DashboardController$...class` files were staged). `git status` then showed no unmerged paths and **no** `target/` entries (the expected "deletions" never appeared, C44). The merge brought: `DashboardController` (about +296 lines), `InventoryController`, `SidebarController`, `StandSaleController` (about +78), `DASHBOARD.fxml`, `STANDSALE.fxml`, and one test photo in `product-images/`. `pom.xml` was **not** in the changed-files list.

### E.2 Database `sassys_creation_db` (XAMPP MariaDB 10.4.32)
- **`products`**: `product_id`, `name`, `category ENUM('Bouquet','Crochet','Keychain')`, `price`, `stock_quantity`, `low_stock_threshold` (default 3), `is_customizable`, `image_path` (nullable); `chk_price`, `chk_stock`. 44 seeded products (Inventory shows "44 products", Bouquet 24 / Crochet 6 / Keychain 14 [VERIFIED-USER screenshot]). **Stock lives only here**: `stand_sales` has no stock column by design (C46).
- **`orders`**: `order_id`, `customer_name`, `product_id` (FK, RESTRICT), `color`, `custom_description`, `quantity` (>0), `unit_price` (>=0), `arrangement ENUM('Pickup','Meetup')`, `schedule_date`, `status ENUM('Pending','Done','Cancelled')`. **No completed-date or total column.**
- **`stand_sales`**: `sale_id` AUTO_INCREMENT PK, `product_id` (FK, RESTRICT), `quantity` (>0), `unit_price` (>=0, the price **at the time of sale**), `sale_date` DATE. Total = `quantity * unit_price`, not stored.
- **`users`: does not exist yet.**
- **Test data left on purpose (user paused the cleanup, 2026-11-24):** `stand_sales` holds one row (`sale_id` 2, product 2, quantity 1, `unit_price` 30.00, 2026-11-24) and product 2 (Light Green Leaf) has `stock_quantity` 19 (it was 20). `sale_id` 1 was an earlier test row, deleted. To clean up later (check `SELECT * FROM stand_sales;` first, it must show only that row):
  ```sql
  DELETE FROM stand_sales WHERE product_id = 2;
  UPDATE products SET stock_quantity = 20 WHERE product_id = 2;
  ```
  Never use `TRUNCATE` (C41) and never run `schema.sql`/`seed.sql`. The next real sale will get `sale_id` 3 or higher (ids are not reused): normal.
- **Photos:** the `image_path` column exists in the user's database. The Inventory list view shows a photo for Light Green Leaf [VERIFIED-USER screenshot], so `image_path` is filled at least for product 2. Whether the whole `add_image_path.sql` was run was **not stated**, and many of its mappings are guesses (D41). Image files are flat in `src/main/resources/.../images/`: all 43 filenames named in that script exist there (checked on `submain`); product 35 has no mapping on purpose.
- **IntelliJ's Database tool** (the "Database" panel on the right) lost its login after the 2026-11-24 merge (errors "Unable to resolve table 'products'", `Access denied for user 'User'@'localhost'`); probable cause (inference): the merge removed the tracked `.idea` data-source files. **Fixed by the user** by setting the data source user in its Properties to the same login `DBConnection` uses, then Test Connection [VERIFIED-USER]. This is per computer; teammates configure their own (D60). It does not affect the running app.
- A teammate's database gets `stand_sales` by running **only the `CREATE TABLE stand_sales (...)` block** from `schema.sql` in phpMyAdmin, never the whole file. (An additive `database/stand_sales.sql` does not exist yet.)

### E.3 What is real and what is sample (read from the code on `ed1471d`, plus Step B)
- **Database-backed:**
  - Inventory read (`ProductDAO.getAllProducts`) and Add product (`insertProduct`).
  - **Stand Sale**: the product dropdown (44 real products, price and stock from the database, **type-to-search**), the sales history, the two chips, Top sellers, Export (they all compute from the loaded `allSales`), and **Record Sale** (Step B, local only until pushed): validation → `StandSaleDAO.insertSale` (one transaction) → reload.
- **Memory only:** Inventory Edit / Restock / Delete (TODO in the code); **Stand Sale Edit and Delete icons** (`editSale`, `deleteSale`: TODO "also UPDATE/DELETE ... in the database"; they change the screen and the in-memory stock only and are undone by the next reload, D54).
- **Sample data:** Dashboard (hardcoded, "TEMPORARY sample data", also in `dfb656b`), Orders (6 sample rows), AddOrder (3 sample products), Login (hardcoded demo credentials in `LoginController`), Forgot password (hardcoded answer), Change password (accepts any current password, saves nothing).
- **Forced maximize:** `LoginController` calls `stage.setMaximized(true)` after login (line 204 on the teammate's branch; comment "remove this line if you don't want it maximized"). Kept on purpose: a big window suits the defense. Layouts must therefore fit a maximized window **and** scroll when they do not (D55, D56).
- Stand Sale in the teammate's `dfb656b`: Qty and Unit-price filter dropdowns (position-based, see the banner in `BANNER_COMMENTS.txt`), a wider left column, the totals chips next to the filters.
- Dashboard cards: "Revenue" equals the stand-sale sample total and "Pending" equals the pending-order sample total, so order revenue has no card yet. `dfb656b` rewrote the Dashboard: **re-read it before assigning Dashboard work.**

### E.4 Verified (with evidence)
- **Merge, pushes** [VERIFIED-USER terminal output]: `37c10a2` (merge), fast-forward of `submain` and push `651fa59..57428b0`; push `57428b0..8ba9563`. The `1 file changed` word-diff showed the Actions column change `prefWidth="80.0"` → `minWidth="100.0" prefWidth="100.0" maxWidth="100.0"`.
- **`insertSale` test** [VERIFIED-USER console + phpMyAdmin]: "Test 1 OK. New sale id: 1", "Test 2 OK (expected failure): Not enough stock, or the product does not exist.", exit code 0. Product 2 stock 20 → 19 (exactly 1 less), `stand_sales` count 0 → 1, `unit_price` 30.00 equals `products.price`, and the 999999 attempt changed nothing (rollback proven).
- **Step A** [VERIFIED-USER screenshot]: dropdown lists real products (Baby Dinosaur Keychain 90.00, stock 10), history empty ("No sales recorded yet"), chips 0 / ₱0.00, Top sellers "No sales yet".
- **Searchable dropdown** [USER-REPORTED "its all goods and working"]: five behaviours were tested by the user (select-all on click, narrowing while typing, full list when opened, revert on leaving with half-typed text, Enter picks the first match); only the overall result was reported.
- **Step B** [VERIFIED-USER screenshots + phpMyAdmin]: see section 0. Not yet committed.
- The app starts, shows Login, and opens Dashboard, Inventory (44 products, grid and list view, search "ligh" finds Light Green Leaf), Stand Sale [VERIFIED-USER screenshots].
- IntelliJ shows 0 problems in `StandSaleDAO.java` after the brace fix [USER-REPORTED "no errors now"].

### E.5 Not verified / unknown
Whether the full `add_image_path.sql` ran (E.2); the Spinner test on Stand Sale / Add order / Add product (pending since 2026-11-16, result not stated); whether the Delete icon is fully visible after the 100 px Actions column (the 2026-11-24 maximized screenshot shows it **partly clipped**, D55); the Dashboard, Orders and AddOrder behaviour in the merged build; which defense is "next week" (Q1); who owns which files (Q2); whether the layout message reached the teammate; the contents of v13 (not readable by the session that wrote v15).

---

## F. TASKS

### F.1 The team's task list (from the user, 2026-11-22) and what the code says (updated 2026-11-24)
| Owner | Item (as written) | Status / notes |
|---|---|---|
| Raven | id in inventory and stand sale | **Partial.** `stand_sales.sale_id` exists in the DB and the model. The Inventory table has an ID column but it is hidden (`visible="false"`); the Stand Sale table has no ID column yet. The Stand Sale rows do **not** carry `sale_id` yet (needed for Delete, T9). |
| Raven | table for stand sale records only | **Done, DB and screen.** `stand_sales` exists and the Stand Sale screen reads it. |
| Raven | table for completed orders and revenue | **Decided: a query/view on `orders`** (L38): `status = 'Done'`, revenue = `SUM(quantity * unit_price)`. To do. |
| Raven | table summary done / pending / cancelled | **Decided: a query** (L38): `COUNT(*) ... GROUP BY status`. Feeds the Dashboard gauge and the Orders chips. To do. |
| Ravs | product dropdown | **Stand Sale: done** (real products, searchable). AddOrder (`cbOrderProduct`) still uses 3 sample products. Which dropdown the item means is still unconfirmed (Q5). |
| Ravs | change password | Needs a `users` table first (Raven's side). Today it accepts any current password. |
| Ravs | standsale button arrow date | **Unclear from the code.** The Record form has no date control (it records today's date); the only `DatePicker` is in Edit Sale. Ask (Q5). |
| Ravs | standsale newest / oldest | The DAO sorts newest first and the table defaults to Date descending; the Date column header can be clicked to sort. Whether a separate toggle is wanted is unconfirmed. |
| Ravs | green stock inventory | In the Inventory **list** view the normal-stock badge uses `stock-plain`; the grid view uses green `badge-done`. In the 2026-11-24 screenshots both views show a green "in stock" badge, so this item **may already be done in `dfb656b`: re-check before assigning.** |
| Ravs | order revenue on the dashboard; revenue-by-day chart (orders + stand sales); month filter | Depend on Raven's queries (L38, L39). `dfb656b` draws two-tier bars (stand vs order) on sample data. Stand sales can now feed it from the database. |
| Ange (Coca) | papers | Needs the final ERD after `users` and any later table changes. |

### F.2 Completed (one line each)
- C-to-Java cleanup, fonts, entry point `Launcher` (v13 §3). **[VERIFIED-USER]**
- Teammate's `tempniDejjo` (to `e09e4e7`) merged into `submain`, `pom.xml`/`.gitignore` kept, `target/` and `.idea` untracked, `image_path` added: `427312f`. **[VERIFIED-USER]**
- Inventory read and Add product work in the merged app. **[USER-REPORTED]**
- 2026-11-22: team task list analysed against the code; decisions L38–L40 made. `stand_sales` table, `StandSale`, `StandSaleDAO.getAllSales()`, `StandSaleDAOTest`: `c3ad3b8`. **[VERIFIED-USER push]**
- 2026-11-22: this file written as v14 (`651fa59`). **[VERIFIED-USER push]**
- 2026-11-24 **T5 evidence gate:** the DAO test ran with exit code 0 (the output that v14 said was missing is now shown), and the user answered that the controllers are edited by him or Ravi and that the defense is next week. **[VERIFIED-USER]**
- 2026-11-24 **T6:** the teammate's `dfb656b` reached `submain` via `merge-dejjo-2` (`37c10a2`, `57428b0`), conflicts only in `target/`. **[VERIFIED-USER terminal output]**
- 2026-11-24 **T7:** `StandSaleDAO.insertSale` and its test: `8ba9563`. **[VERIFIED-USER console + phpMyAdmin]**
- 2026-11-24 **Stand Sale reads the database** (Step A): `81d50de`. **[VERIFIED-USER screenshot]**
- 2026-11-24 **Searchable product dropdown:** `ed1471d`. **[USER-REPORTED]**
- 2026-11-24 **Step B, Record Sale saves to the database:** working in the user's tree, **not pushed**. **[VERIFIED-USER screenshots]**

### F.3 NEXT BATCH (Batch 4, for Raven): 3 tasks
1. **T8: push Step B, then the two documentation commits.** Evidence for Step B is in section 0, so the gate (L34) is met.
   ```powershell
   git branch --show-current
   git status
   ```
   Expect `submain` and `StandSaleController.java` as the only modified file (if `AGENT_HANDOFF.md` or other files appear, commit Step B first, see below). Then:
   ```powershell
   git add .
   git commit -m "Stand Sale: Record Sale saves to the database (insertSale)"
   git push
   ```
   Then apply `BANNER_COMMENTS.txt` (comments only, rebuild, 0 errors) and commit it as its own commit ("Add banner comments for each logic section"), then replace `AGENT_HANDOFF.md` with this file and commit ("Update AGENT_HANDOFF to v15"). Three commits, three pushes or one push at the end: the user's choice. **Do not** run `StandSaleDAOTest` again.
2. **T9: Delete sale (and what to do with Edit): decision L48 is pending, present it to the user.**
   - **Goal:** nothing on the Stand Sale screen should look saved when it is not (D54).
   - **Options:** A wire **Delete** now and hide the Edit icon until Edit is wired; B wire both; C hide both icons; D leave them (not acceptable for the defense).
   - **Trade-offs:** Delete is one transaction (mirror of `insertSale`); Edit needs the old and the new product's stock and the date, so it is larger and riskier. Hiding an icon is a Java change in `setUpTable()` (the `HBox` in the Actions cell), not an FXML change.
   - **Recommendation (assistant, not yet agreed):** A.
   - **Slice, small steps:** (1) carry `saleId` in each table row (add a `"SaleId"` key in `sale(...)`/`loadSales()`; the keys that the FXML reads stay unchanged); (2) `StandSaleDAO.deleteSale(int saleId)`: on one connection `SELECT product_id, quantity FROM stand_sales WHERE sale_id = ?`, `UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?`, `DELETE FROM stand_sales WHERE sale_id = ?`, commit, rollback on any `SQLException`; (3) `StandSaleController.deleteSale` calls it after the confirmation dialog, then `loadProducts(); loadSales();`; (4) test with a throwaway sale, check stock and row, clean up. Work on a slice branch (`standsale-delete`) or get his explicit OK to work on `submain` (Q9). Teach: why the restore and the delete must be one transaction.
3. **T10: sync the screens with the teammate and settle ownership (Q2).** The user relays the message in K.3 (it now also asks for the two `STANDSALE.fxml` fixes, D55 and D56, and for `product_id` to be carried by the dropdown). Ask who edits `StandSaleController` and `DashboardController` **after today's push**, and ask the teammate to pull `submain` before editing either. Then re-check the Delete icon and the Top sellers list on a maximized window and at the default size.

### F.4 Queue after Batch 4 (order follows the team list and the defense; revise after Q1)
- **Batch 5:** `OrderDAO` read + the revenue and status-summary queries (L38, L39) → real Dashboard numbers; wire AddOrder's product dropdown to `ProductDAO`; then Ravs' order-revenue card, two-series chart and month filter (stand sales can already feed it).
- **Batch 6:** `users` table + Login + Change password (JDK `PBKDF2`, **no new library without asking**).
- **Still open from v13:** W2 product **Delete → database** (D34, recommended by L33; the user followed the team list on 2026-11-22), W3 Edit/Restock → DB, W4–W6 Orders frames and orders to the DB, one shared `Product` class (D49), the guided read-through of teammate-written code (L35, D45), the paper (ERD, test plan, likely defense questions).

---

## G. OPEN QUESTIONS FOR THE USER (ask only what is still open)
- **Q1. Which defense is "next week" and what must it show?** Partly answered 2026-11-24: "this whole system's defense is next week". Still unknown: the date, which screens and documents, whether code is inspected. It sets every priority.
- **Q2. Who edits what from now on?** Partly answered 2026-11-24: the controllers are edited by the user or Ravi. Still open: who owns `DashboardController` and the FXML files, and whether Dejjo = Ravi = Ravs.
- **Q3. Is the repository meant to be public?** (It is readable anonymously today; this file avoids secrets and full names either way.)
- **Q4. `product-images/` policy.** The folder is now tracked with one test photo and `.gitignore` has no line for it. What should the app store in `image_path` for newly added products, and should the test photo be removed from git? (D36, D57)
- **Q5. Ravs' unclear items:** which product dropdown, and what is "standsale button arrow date"?
- **Q6. When a product is deleted, keep its photo file?** (Recommended: yes, keep it.)
- **Q7 (new).** Delete/Edit sale: wire Delete first and hide Edit (recommended), wire both, or hide both? (L48, T9)
- **Q8 (new).** Did the layout message (K.3) go to the teammate, and did they answer?
- **Q9 (new).** Slices on a slice branch (rule C.4) or straight on `submain`, as done on 2026-11-24 (C43)?
- **Q10 (new).** Is the two-line test sale (E.2) still wanted, or may the cleanup run?

---

## H. DECISIONS (binding; the full v13 list L1–L37 is in the archive)

**Still binding from v13:** L1 keep the teammate's plural `controllers/` · L2 FXML stays flat · L3 `mainClass` = `Launcher` · L6 do not touch Spinner `min/max/initialValue` in FXML · L7 keep `exports ...sassyscreationgui;` · L11 `BigDecimal`/`DECIMAL`, stock rules later in a service with transactions · L16 never run `schema.sql`/`seed.sql` to test a connection · L23 delete no FXML before the defense · L25/L29–L31 no blind merges; `pom.xml` and `.gitignore` = ours; additive SQL only · L32 integration branches stay local · L34 gate rule · L35 a guided read-through of the teammate-written code precedes more wiring (still recommended; D45).

**Domain rules:** 44-product seed (all 44 products of the original C system); `is_customizable` TRUE only for original ids 35, 42, 43, 44; categories only Bouquet/Crochet/Keychain (no Material category); deleting a product that orders or sales reference is **blocked** (FK `RESTRICT`) with a friendly message; customizable orders may have quantity > 1 with an agreed per-unit price (total = quantity × unit price), the customization text is stored on the order, and **no** ready-made stock is deducted; regular orders are 1 unit and deduct 1 (this supersedes the 2026-10-24 "no quantity column" decision for customizable items only); no raw-material inventory; **low stock when `stock_quantity <= low_stock_threshold`** (default 3). Stand sales may have any quantity within the stock and always deduct stock.

**Decisions from 2026-11-22 (v14):**
| # | Decision | Reason / alternative / defense line |
|---|---|---|
| L38 | Completed orders + revenue and the done/pending/cancelled summary are **queries or views on `orders`**, not separate tables. | One source of truth, nothing to keep in sync. Alternative: separate tables (duplicated data). Defense: "derived data is not stored twice (normalization)." |
| L39 | Order revenue by day/month uses **`orders.schedule_date`**; no `completed_at` column for now. | No schema change near the defense. Limitation to state in the paper: a Done order counts on its scheduled date, not the day it was marked Done. Alternative: add `completed_at`. |
| L40 | `stand_sales` design: price stored at sale time, **no total column**, FK `RESTRICT`, `CHECK (quantity > 0)` and `CHECK (unit_price >= 0)`, `sale_id` ordering as the tiebreaker, newest first. `StandSale` carries `productName` from a JOIN. | Mirrors `orders`. |
| L41 | This file is the working handoff in the repo root, edited in place each session; v13 stays outside the repo as the archive. | The user asked for fewer tokens (v14). |
| L42 | Tasks are delivered 3 at a time, each followed by evidence. | User request. |

**New (2026-11-24):**
| # | Decision | Status / reason / alternative / defense line |
|---|---|---|
| L43 | This file is now the **detailed edition**: no 450-line budget; all earlier context kept; section 0 and M added. | **[DECISION]** user request 2026-11-24. Trade-off: longer file; mitigation: read 0, A, B, C, E, F, G only. |
| L44 | **Stand Sale data flow:** products from `ProductDAO.getAllProducts()` (keyed by unique name; the inner `Product` now carries the `id`), sales from `StandSaleDAO.getAllSales()`, Record Sale → `insertSale` → **reload both**, so the screen always shows what the database holds. | **[RECOMMENDED, built and working]** Alternative: add the row in memory after the insert (faster, can drift). Cost of the chosen way: two small queries per sale. |
| L45 | **`insertSale(int productId, int quantity, LocalDate saleDate)` returns the new `sale_id`** and runs in one transaction: guarded `UPDATE products ... AND stock_quantity >= ?` (0 rows → "Not enough stock, or the product does not exist." → rollback), price **read from `products` inside the transaction** (not sent by the screen), `INSERT INTO stand_sales`, `commit`, `rollback` on any `SQLException`. | **[RECOMMENDED, tested by the user]** Option A (the screen sends the price) was rejected: a wrong price from the screen would be saved. Defense line: "stock and sale are saved together or not at all (atomicity)." |
| L46 | **Searchable product dropdown without a library:** the existing `ComboBox` made editable with a `FilteredList` (names that contain the typed text, ignoring case; exact name = real choice; leaving with half-typed text reverts; Enter picks the first match; click selects all). ControlsFX `SearchableComboBox` rejected for now (new dependency, modular project, version unverified on Java 25). | **[RECOMMENDED, user reported it working]** |
| L47 | When merging the teammate's branch, `target/` conflicts are resolved by **deleting** `target/` (`git rm -r -f target`), never by accepting theirs. The teammate is to run `git rm -r --cached target` on `tempniDejjo` and push. | **[DECISION by action]** the user did it; the teammate request is part of K.3. |
| L48 | **Stand Sale Edit and Delete icons:** unwired. Recommendation: wire Delete (restore stock + delete, one transaction) and hide Edit until it is wired. | **Pending the user (Q7).** |
| L49 | **Banner comments** mark specific logic (C.5); scope = backend files + `StandSaleController`; shipped as `BANNER_COMMENTS.txt`. | **[DECISION]** user request 2026-11-24. |
| L50 | IntelliJ's Database tool uses the same login as `DBConnection` (set in the data source Properties); never committed, and no password is written in this file. | **[DECISION by action]** fixed 2026-11-24. |

---

## I. CORRECTIONS (agent errors; newest last; v13's C1–C40 are in the archive)
| # | What was said | Correction |
|---|---|---|
| C29 | (v13) "her branch / she" for the GUI teammate. | Never name or gender the teammate; say "the GUI teammate (Git author Dejjo)". |
| C41 | An agent said `TRUNCATE TABLE stand_sales` was safe because nothing references the table. | The user got an error in phpMyAdmin (the message was not shown) and resolved it himself; the cause is unknown. Use `DELETE FROM stand_sales;` for cleanup (optionally `ALTER TABLE stand_sales AUTO_INCREMENT = 1;` for cosmetic ids). Never untick "Enable foreign key checks" to force a statement through. |
| C42 | An agent told the user to commit and push the `stand_sales` slice straight to `submain`, and the push happened before the DAO test output was shown. | This bypassed the slice-branch pattern (C.4) and the gate (L34). It was low risk only because the change was additive. Future slices use a branch and show evidence first. |
| C43 | On 2026-11-24 the agent again told the user to commit the `insertSale`, Step A and dropdown slices **directly on `submain`**. | The evidence gate was met each time (test output, screenshots), but it is not the slice-branch pattern of C.4. Ask the user (Q9) and follow his answer. |
| C44 | The agent said that after resolving the merge `git status` would list `target/...` deletions. | It listed none: `submain` already had `target/` deleted, so nothing remained to record. The user noticed and was right. Say "expect no `target/` entries". |
| C45 | The agent told the user to paste `insertSale` "after `getAllSales()`" without showing where that method ends. | It landed inside `getAllSales()` (27 compile errors); a second attempt left it below `return sales;` with the closing brace missing. Fixed by giving the whole file. Rule: when adding a method, show the surrounding lines or the full file, and teach brace matching (click a `{` and IntelliJ highlights its `}`). |
| C46 | The agent said to check the stored unit price with `SELECT COUNT(*) FROM stand_sales`. | `COUNT(*)` only counts rows. The user was confused, and also looked for a stock value in `stand_sales` (stock lives only in `products`). Use `SELECT * FROM stand_sales;` and say which table holds which column. |
| C47 | The agent said widening the Actions column to 100 px would make the Delete icon visible. | The maximized screenshot of 2026-11-24 shows it still partly clipped at the right edge (D55). Evidence first, then claims. |

---

## J. OPEN DEFECTS AND RISKS (v13/v14 items kept; status updated 2026-11-24)
| ID | Defect / risk | Status |
|---|---|---|
| D34 | Inventory **Delete / Edit / Restock change memory only** (and are wiped on reload). | Open (W2/W3). |
| D35 | Teammate's Inventory code swallows exceptions (`catch (Exception e) { e.printStackTrace(); }`): a DB failure shows an empty screen with no message. | Open (fix inside W2). |
| D36 | `product-images/` is created under the project folder on Add product; the stored `image_path` format is unknown. | Open (Q4); see D57. |
| D39 | The teammate's branches still track `target/` and `.idea` files and carry a UTF-16 `.gitignore`. | Open (T10): every merge of `tempniDejjo` conflicts in `target/` until the teammate runs `git rm -r --cached target`. |
| D40 | `pom.xml` can silently take the teammate's older versions in a merge. | Open: check after every merge. For the 2026-11-24 merge, `pom.xml` was not in the changed-files list. |
| D41 | `add_image_path.sql` mappings are mostly guesses (for example Mini Teddy Bear Keychain → `coffee-bouquet.jpg`, Bag - Customize → `thumbler-holder.jpg`). | Open. |
| D42 | The merged app had no shown run evidence. | **Mostly resolved 2026-11-24** (the app runs, Inventory shows 44 products, Stand Sale works). Still owed: the Spinner test. |
| D43 | `InventoryController` calls a static DAO and keeps a nested `Product`. | Open. |
| D45 | The user owns about 700 lines of teammate-written `InventoryController` plus other teammate code he must be able to explain. | Open: guided read-through (L35). |
| D47 | The teammate's `dfb656b` edited `StandSaleController` and `DashboardController` (conflict risk). | **Resolved 2026-11-24** (merged). New risk: the teammate may keep editing the same files (Q2, T10). |
| D48 | `StandSaleDAOTest` run output was not shown. | **Resolved 2026-11-24.** |
| D49 | **Four classes named `Product`** (`model.Product` plus private copies in the Inventory, AddOrder and StandSale controllers) and a private `Sale`-style row map in `StandSaleController`. Likely defense question. | Open (Batch 5+). |
| D50 | Low-stock rule mismatch: Inventory hardcodes `LOW_STOCK = 5`; the database default is 3 and the agreed rule is `<= low_stock_threshold`. | Open. |
| D51 | Dashboard "Revenue" card shows stand-sale revenue only; order revenue is missing. | Open (Batch 5). |
| D52 | Login is hardcoded and Change password accepts any current password. | Open (Batch 6). |
| D53 | Dashboard chart used whole-peso integers (`Map<String,Integer>`), which would drop centavos; re-check on the merged `DashboardController`. | Open. |
| **D54** | Stand Sale **Edit and Delete change the screen and the in-memory stock only**; the next reload undoes them, and a Delete shows stock the database never got. | Open (T9, L48). Do not demo them on real rows. |
| **D55** | The **Delete icon is still partly clipped** at the right edge of the sales table, even maximized, after the Actions column was set to 100 px; at the default window size the Date, Qty and filter headers are also squeezed ("Filt...", "2026-..."). | Open (teammate's `STANDSALE.fxml`, T10). |
| **D56** | The Stand Sale **left column does not scroll**: with more than a few Top sellers the list is cut off at the bottom (`ScrollPane` is imported in the FXML but unused). It may look fine on a taller screen. | Open (T10). Fix: wrap the left `VBox` in a transparent `ScrollPane`. |
| **D57** | `product-images/<number>_n.jpg` (about 550 KB, a test photo) came into `submain` with the teammate's commit; `.gitignore` has no `product-images` line. | Open (Q4). |
| **D58** | Stale comments "TEMPORARY: sample products / sample sales ... Replace with a database query" above `loadProducts()` and `loadSales()` in `StandSaleController`. | Fixed by `BANNER_COMMENTS.txt` (REPLACE 1 and 2) once applied. |
| **D59** | `StandSaleDAOTest` writes a real sale and deducts stock each time it runs. | Mitigated by a WARNING banner; never ask the user to re-run it. |
| **D60** | The IntelliJ Database tool login is per computer and was lost after a merge. | Open for teammates: set the data source user in Properties. |
| **D61** | The Qty and Unit-price filter dropdowns (teammate's logic) have not been tested by the user against real database rows. | Open. Their boundaries: Qty 1 / 2–5 / 6–10 / 11+; Price under 30 / 30–49 / 50–99 / 100+ (the 49 and 99 edges treat prices such as 49.50 by `>= 30 && < 50`). |
| **D62** | The Stand Sale screen keeps money as `double` (`Product.price`, row `Price`/`Total`, Top sellers sums) while the database is `DECIMAL` and C.2 says `BigDecimal`. Rounding could differ by a centavo; a likely defense question. | Open (low priority; Batch 5+). |

---

## K. REFERENCE (open on demand)

### K.1 Read-only commands in an agent sandbox (works while the repo is readable without a login)
```bash
git clone --branch submain --single-branch https://github.com/Utotz/Sassy-s-Creation-GUI.git sassy
git remote set-branches --add origin tempniDejjo        # a single-branch clone knows one branch only
git fetch origin
git ls-remote --heads origin
git rev-list --left-right --count origin/submain...origin/tempniDejjo
git merge-tree --write-tree --name-only origin/submain origin/tempniDejjo     # trial merge, changes nothing
```
Agents see **pushed commits only** and cannot push. `web_fetch` of GitHub pages is blocked by the site; `git clone` and `git fetch` worked on 2026-11-24, and unauthenticated GitHub API calls are rate-limited, so prefer git. Anything only in the user's working tree (like Step B before it is pushed) is invisible to the agent: ask for a screenshot or the file.

### K.2 User commands (PowerShell, one per line, no prompt)
```powershell
git status
git branch --show-current
git branch -vv
git fetch origin
git log --oneline -5
git log --oneline origin/tempniDejjo -3
git diff --stat -- pom.xml
git --no-pager diff -U0 --word-diff=porcelain
```
SQL sanity checks (phpMyAdmin SQL tab, read-only):
```sql
SELECT COUNT(*) FROM products;                                   -- expect 44
SELECT * FROM stand_sales;                                       -- the test row until the cleanup in E.2
SELECT product_id, name, price, stock_quantity FROM products WHERE stock_quantity >= 2 ORDER BY product_id LIMIT 5;
SELECT product_id, name, image_path FROM products ORDER BY product_id;   -- photo check
```

### K.3 Draft message to the GUI teammate (the user relays it; neutral wording)
> Quick sync on `submain`. It now has your `dfb656b`, plus the backend: `stand_sales`, `StandSaleDAO.insertSale`, and the Stand Sale screen reading the database (searchable product dropdown, Record Sale saves). Please **pull `submain` before editing `StandSaleController` or `DashboardController`**, and tell me who edits them. Three small things in `STANDSALE.fxml`: (1) the Delete icon is still partly cut off at the right edge of the table, so the Actions column needs more room; (2) the left column needs a transparent `ScrollPane` around it, because Top sellers is cut off at the bottom when there are many products; (3) each product choice must keep its `product_id` (the backend's `insertSale` needs it, and I already carry it in `StandSaleController`). Also: please run `git rm -r --cached target` on your branch and push, so `target/` stops conflicting, and keep `pom.xml` at JavaFX 25.0.5 / MariaDB 3.5.7. Everyone creates the `stand_sales` table by running only its `CREATE TABLE` block from `database/schema.sql`, never the whole file. In IntelliJ's Database tool, set your own data source login in its Properties.

### K.4 Evidence labels
`[VERIFIED-USER]` pasted output or screenshot · `[VERIFIED-GIT]` read from the remote by an agent (pushed commits only) · `[VERIFIED-BUILD]` compiled or run by an agent (state the JDK) · `[USER-REPORTED]` said by the user, no output shown · `[UNVERIFIED]` cannot be proven from the sandbox · `[DECISION]` agreed · `[RECOMMENDED]` advice, accepted or not yet carried out (say which).

### K.5 Where the old detail lives (archive v13, not in the repo)
Merge guide (Appendix E), the reviewer's fix list (Appendix F), exact pre-merge file listings (Appendix B), seed data from the C system (Appendix A; also `database/seed.sql`), decision log L1–L37, defects D1–D46, session logs. **The agent that wrote v15 had no access to v13.** Ask the user to upload v13 (or one section of it) **only** when a task needs it.

### K.6 Companion file
`BANNER_COMMENTS.txt` (22 ready-to-paste banners and 2 comment replacements, anchors verified on `ed1471d`). It is a one-time instruction sheet, not part of the repo; once applied it can be deleted.

---

## L. CHANGELOG (newest first; compress rows older than 5 versions)
| Version | Date | Change |
|---|---|---|
| **15** | 2026-11-24 | **Detailed edition** (L43). Added section 0 (where we left off) and M (history); kept all of v14. Recorded: teammate's `dfb656b` merged (`37c10a2`, `57428b0`), `insertSale` (`8ba9563`), Stand Sale reads the database (`81d50de`), searchable dropdown (`ed1471d`), Step B working but unpushed; the IntelliJ data-source fix; the layout findings (D55, D56); decisions L43–L50, corrections C43–C47, defects D54–D62, questions Q7–Q10; code-comment convention C.5; next batch T8–T10. |
| 14 | 2026-11-22 | Replaced v13 as the working handoff (compact, in the repo). Added: team task list mapped to the code (F.1), `stand_sales` slice (`c3ad3b8`), teammate's `dfb656b` and the trial merge result, decisions L38–L42, corrections C41–C42, defects D47–D53, next batch T5–T7. |
| 13 | 2026-11-22 | Full-detail edition (about 356 KB, 3,287 lines), state at `427312f`. Kept as an archive outside the repo. |

---

## M. HISTORY (one line per milestone; from the assistant's notes of earlier chats, not re-verified against v13)
- **Origin.** The group's C-language capstone (CC103) "Sassy's Creation" is being rebuilt as a Java GUI app for the course "Database & OOP". An earlier Android Studio version (SassyCreationStudio v3) is incomplete and serves only as a reference for structure and design; the features come from the PD documents. A client persona agreed to be the "client" of the system.
- **2026-10-15** Title proposal submitted; **approved**, listed in the approved-titles sheet (Section 2C, group of 3). The form deadline no longer applies.
- **2026-10-23/24** Chose **IntelliJ IDEA** (over NetBeans) with JavaFX + Scene Builder, Maven and JDBC. Created the project; the starter app ran; `DatabaseTest` printed "Database connected successfully!" (JavaFX 25.0.5, MariaDB driver 3.5.7). First handoff (feature spec from the PDs, schema proposal, layered package structure mapped to the C files, Android palette and screens, seed data, 13 open decisions) and a master prompt for new agents.
- **2026-10-24 decisions:** 1 unit per order (later superseded for customizable items, 2026-11-10), deleting a referenced product is blocked with a message, no Material category, build Product & Inventory first. The user is group leader and backend and also guides the teammates on the paper and the front end.
- **2026-10-25** Keep XAMPP MariaDB for the "MySQL Database" title while it stays MySQL-compatible; database created, still empty.
- **2026-10-26** The user is the only backend member. `products` table created; `schema.sql` and `seed.sql` imported through phpMyAdmin (3 TEST rows first); a negative price was rejected by `chk_price` (#4025), so CHECK constraints work on 10.4.32.
- **2026-10-27** Database sharing = **Option B**: `schema.sql` + `seed.sql` in a `database/` folder, shared through GitHub and imported through phpMyAdmin; the XAMPP data folder is never committed; no migration library; the `DROP TABLE` data-loss behaviour is documented. The user was stuck on where to create packages and on `.java` vs `.sql` files.
- **2026-10-30** GitHub repository connected from IntelliJ; branches `main`, `GUI`, `java+db`, `submain` (integration). The teammates know only a little Git and often forget commit/push/pull. A 30-page "Team Git and Daily Flow" PDF was produced.
- **2026-11-06** Second and third handoffs (968 and 2,208 lines). Roles: the user stays on the backend; one teammate on the GUI; the other on front and back end. The user uploaded his project and the GUI progress and assets and asked for a v4 handoff.
- **2026-11-10** (v5) Stock rules: `stock_quantity`, `low_stock_threshold` (default 3), `is_customizable` (ids 35, 42, 43, 44); the seed must keep all 44 original products; customizable orders may have quantity > 1 with a per-unit agreed price.
- **2026-11-11** Focus: wire the four FXML frames LOGIN, DASHBOARD, LOADING, LOGOUT (clickable, moving between frames, loading frame on login and logout).
- **2026-11-13** (v7) Canonical project folder fixed; remote = the GitHub repository; plan: commit and push `JAVA+DB`, create local `submain`, fast-forward from `GUI`, merge `JAVA+DB`, test, then promote. The local merge into `submain` was done with `git add .`.
- **2026-11-16** (v8/v9) A reviewer's fix list; the user asked for fixes as instructions he applies himself, not zips. Pushed `submain` (`7198e0a`); cleanup branch `cleanup-structure`: `javafx:run` opens LOGIN (`dd5b4d1`), Dashboard logo (`3b5c4c1`), 4 starter files deleted (`5626c9e`).
- **2026-11-20** First real database slice = Inventory READ (`ProductDAO.getAllProducts()`), then the Orders frames. The user said the defense is "next week". Confirmed phpMyAdmin shows the 44 seed products.
- **2026-11-22** Team task list (Raven / Ravs / Ange) analysed against the code; decisions L38–L40; `stand_sales` slice `c3ad3b8`; v13 (3,287 lines, state `427312f`); this compact handoff v14 (`651fa59`).
- **2026-11-24** This session: see section 0, E.4, F.2 and L (v15).
