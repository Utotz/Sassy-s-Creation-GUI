# AGENT_HANDOFF.md — Sassy's Creation (Java GUI) — living handoff (detailed edition)

**Handoff version: 17**
**Last updated:** 2026-11-26 (Asia/Manila), second session of the day, written by a Claude chat session (the order-side backend, the Orders screen read, the visible ID columns, the user's decisions on IDs and Edit, and the teammate's new commits are recorded here)
**State described:** `origin/submain` = `a619441` (pushed; [VERIFIED-GIT] by a fresh clone and [VERIFIED-USER] by the push output `a950627..a619441`). **Two code edits that the user reports having made after that push (Inventory default sort, Dashboard weekly-chart fit) are local, not verified, and their commit state is unknown** (section 0). This file (v17) is the only other change not in `a619441`, until the user commits it.
**Replaces:** v16 (all of its content is kept below and updated). v16 replaced v15 (the detailed edition), which replaced v14, which replaced `SASSYS_CREATION_JAVA_GUI_HANDOFF_v13.md` (about 356 KB, kept by the user **outside** the repo). The sessions that wrote v14 to v17 **could not read v13**; v15 added the history that survives in the assistant's notes (section M). If a detail seems missing, ask the user for the matching v13 section.

> One file, edited in place, version number goes up every session. Do **not** create a second handoff file. This file lives on `submain` only (`main` is still the initial commit and has no handoff).

---

## 0. WHERE WE LEFT OFF (read this first, 2026-11-26, end of the second session)

- **The defense of the whole system is "next week"** (user, 2026-11-24; the date and what it must show are still unknown, Q1). Prefer finishing and verifying small slices over starting large ones.
- **Pushed (`origin/submain` = `a619441`)** [VERIFIED-GIT fresh clone; push output `a950627..a619441` VERIFIED-USER]. Since v16 (`c38c0cf`):
  - `c975173` **Order model + `OrderDAO.getAllOrders()` + read-only `OrderDAOTest`.** This commit **also contains the two visible-ID FXML edits** (Inventory list: the ID column is no longer hidden; Stand Sale: a new first column `colSaleId`, key `"SaleId"`), although its message does not say so (C50). It is pushed history: do not rewrite it.
  - `a950627` `OrderDAO` gains `getCompletedOrders()`, `getCompletedRevenue()`, `getStatusSummary()` and one private `mapOrder(ResultSet)` shared by the list methods; the test prints them all.
  - `a619441` **The Orders screen reads the database:** `OrdersController.loadOrders()` calls `OrderDAO.getAllOrders()`; each card's price is the order **total** (quantity x unit price); a database failure is shown through `Platform.runLater(() -> showError(...))` because `showError` needs the window and the screen has none yet inside `initialize`.
- **Order-side backend is verified** [VERIFIED-USER console text, run twice]: `OrderDAOTest` printed 4 orders newest first, `Status summary: {Pending=1, Done=2, Cancelled=1}`, 2 completed orders and `Completed revenue: 330.00` (= 30.00 + 2 x 150.00). The `orders` table was empty before (`COUNT(*)` = 0, empty `GROUP BY` result [VERIFIED-USER screenshot]); the user then inserted 4 **TEST rows** (E.2).
- **Visible IDs are verified** [VERIFIED-USER screenshots]: the Inventory list shows real product ids (33, 31, 30, 44, 39, 35, 4, ...), and Stand Sale shows `sale_id` (the throwaway sale is id **3**, after the two earlier test sales: consistent with ids never being reused).
- **NOT yet shown, still owed:** (a) the **Orders screen** (the user pushed `a619441` before showing it; the app did start, see the run log and the other screens, but the Orders cards and chips have never been shown); (b) the two local edits below.
- **The user's two local edits (reported 2026-11-26 as "done"; NOT in `a619441` [VERIFIED-GIT]; unverified; commit state unknown).** The user said he did "everything" up to but **not** including the "Order of work" commit steps, so assume they are **uncommitted in his working tree** and check with `git status` and `git diff`:
  1. **Inventory default sort by ID ascending (L55):** one line in `InventoryController.loadProductsFromDB()`, after the `for` loop inside the `try`: `PRODUCTS.sort(Comparator.comparingInt(Product::getId));`. Expected: ids 1, 2, 3 ... 44 in the list and the grid.
  2. **Dashboard weekly chart fit (L56, D64):** `DASHBOARD.fxml` `barSample` `spacing="40.0"` -> `"0.0"`; `DashboardController` constants `BAR_WIDTH` 26.0 -> 20.0 and `BAR_PAIR_GAP` 4.0 -> 3.0; in `renderRevenueBars`, after `column.setAlignment(Pos.BOTTOM_CENTER);` add `HBox.setHgrow(column, Priority.ALWAYS);` and `column.setMaxWidth(Double.MAX_VALUE);` (import `javafx.scene.layout.Priority`). Expected: both week arrows and all 7 days visible inside the chart card.
- **What the user decided today (2026-11-26)** [DECISION by the user]: (1) he accepted the order **orders slice before Edit**; (2) **Edit sale must be wired and work**, not hidden (answers Q7 = option B); (3) **IDs must be visible in every section and stay with their item for good** (L53): a deleted product that is added again gets its **old id back**, a genuinely new product gets a **new, higher id**, ids are never reused; he chose **soft delete** (a product deleted in the app stays gone after a restart, but its row stays in the database); (4) the Inventory list must be ordered **by ID ascending** by default (L55); (5) **after his Raven tasks he wants to finish the Dashboard, especially the graph, connected to the backend and the database, and asked to be reminded.** **Remind him** when T13 to T15 are done (F.4).
- **The teammate pushed again.** `tempniDejjo` is at `931c5ad`, **3 commits that `submain` does not have** (`3aa3590`, `ce95c44`, `931c5ad`, 2026-11-25/26). Trial merge into `a619441` [VERIFIED-GIT]: **one conflict, `STANDSALE.fxml`** (our new ID column against their column changes). Details in E.1. Not merged yet: **T14**.
- **Test data now:** `orders` has 4 TEST rows (E.2). At the user's last Stand Sale screenshot `stand_sales` held one throwaway sale (id 3, Baby Dinosaur Keychain x1, 90.00) and that product's stock was 9; whether he has deleted it since is unknown. Delete it with the **app's** Delete (it gives the stock back), never in phpMyAdmin. **Never run `StandSaleDAOTest`** (D59).
- **Still open from v16:** Stand Sale **Edit is still screen-only** (D54, T11); the Delete icon is **still partly clipped** on `a619441` [VERIFIED-USER screenshot, D55]; Top sellers does not scroll (D56). The teammate's new `STANDSALE.fxml` makes the Actions column 220 px wide, which may fix D55: **check after T14.**
- **Next, in this order (F.3, Batch 6):** (1) **T13** verify, then commit, the Orders screen and the two local edits; (2) **T14** merge the teammate's `931c5ad` on a local branch; (3) **T15** soft delete for products and the id rule. Then T11 (Edit) and the Dashboard wiring (F.4).
- **Commit tip for the user's next handoff commit:** his working tree holds uncommitted code edits, so stage **only** this file (`git add AGENT_HANDOFF.md`), not `git add .` (A.2 step 8).

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
   `git add .` is acceptable only after the user has read `git status` (rule C.4). **If `git status` also lists modified code files (uncommitted edits), stage only the handoff: `git add AGENT_HANDOFF.md`, otherwise the code edits ride along in the handoff commit (2026-11-26).**
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
16. **Evidence format (user request, 2026-11-25).** Screenshots use many tokens. Ask for **pasted terminal or SQL text** whenever text is enough, and for a screenshot **only when the layout itself matters** (clipping, scrolling, alignment). "Done, no errors" is enough evidence for a build result.

### C.2 Architecture rules
- Layers: `controllers` (screens) → `dao` (SQL only) → `db` (connection); `model` holds data classes. **No SQL in controllers or FXML; no business rules in event handlers.** A service layer is planned only when stock rules need transactions across DAOs (ask first). *(Today the stock rule lives inside `StandSaleDAO.insertSale` and `StandSaleDAO.deleteSale`, each one transaction: decisions L45 and L51.)*
- Money is `BigDecimal` in Java and `DECIMAL(10,2)` in SQL. Dates are `LocalDate` / `DATE`. *(The Stand Sale screen still keeps money as `double`: defect D62.)*
- Database constraints back up the Java validation (`CHECK`, `FOREIGN KEY ... ON DELETE RESTRICT`).
- `database/schema.sql` **drops tables** (`DROP TABLE IF EXISTS`): **never run it, `seed.sql` or `add_image_path.sql` to "test" something.** Changes to a live database are done with additive SQL (for example only the `CREATE TABLE` block).
- Do not edit the GUI teammate's FXML or styles without asking. No new dependencies; do not touch `pom.xml`.

### C.3 Coding defaults
Parameterized SQL (never concatenate input); try-with-resources for connections; validate in the right layer plus database constraints; keep secrets out of source control; prefer simple code the user can defend over clever abstractions. Avoid features the course has not taught (streams, `super`, etc.) unless there is a reason, and **ask before** using one.

### C.4 Git rules
- **One short-lived local slice branch per slice** → check → `git switch submain` → `git merge --ff-only <slice>` → push. *(On 2026-11-24 the slices were committed straight on `submain` after the evidence was shown: see C43 and Q9. On 2026-11-25 the Delete slice used branch `standsale-delete` and an `--ff-only` merge, adopted by action.)*
- Integration/backup branches stay **local** and are never pushed (`merge-dejjo-2` was one). Never push `main` (it is still the initial commit). Never push or merge into the teammate's branches (`GUI`, `tempniDejjo`).
- Before merging a teammate branch: `git fetch origin`, then a **trial merge** (`git merge-tree --write-tree --name-only origin/submain origin/<branch>`), then a throwaway local branch. After every merge check `git diff --stat -- pom.xml` (it can silently take the teammate's older versions).
- When a merge conflicts only in `target/` (compiled output): resolve by **deleting** it (`git rm -r -f target`, `-f` because some files are staged), never by "accept theirs" (decision L47).
- `git add .` is fine **after** reading `git status` (no stray files such as `target/`, `.idea/`, `product-images/`).
- **If `git push` fails with a remote error** (for example GitHub's "Internal Server Error", 2026-11-25): the commit is still safe locally. Check what GitHub really has with `git ls-remote origin refs/heads/submain`, then simply run `git push` again (wait a few minutes and look at githubstatus.com if it repeats). **Never `git push --force`, `git reset` or re-commit** because of a remote error. **Delete a slice branch only after the push is confirmed** (C48).

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

## D. STRUCTURE AND CODE STYLE (verified on `a619441`)

```
src/main/java/com/sassyscreation/sassyscreationgui/
  Launcher.java (9 lines), SassyCreation.java (19)   entry points (mainClass = Launcher)
  ProductDAOTest.java (19), StandSaleDAOTest.java (37), OrderDAOTest.java (36)   manual test runners (main + println), root package
  db/DBConnection.java (17)                 getConnection(); hardcoded local dev connection
  model/   Product (58), StandSale (54), Order (72)     immutable data classes
  dao/     ProductDAO (75), StandSaleDAO (164), OrderDAO (140)   SQL only, one DAO per table
  controllers/  22 files                    screen controllers + helpers (plural "controllers" is the teammate's convention)
                StandSaleController.java is 740 lines, OrdersController 528, InventoryController 787, DashboardController 577
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
- A DAO that returns several kinds of list shares one private row-mapping method (`OrderDAO.mapOrder`) instead of repeating the column mapping.
- Test runner: `XxxDAOTest.java` in the root package, `main` prints results and catches `SQLException`. A test that writes data must say so in a `WARNING:` banner (D59).
- Comments: banner style C.5.
- Do not create a second class with a name that already exists (`Product` is already duplicated, see D49).

---

## E. CURRENT STATE (as of 2026-11-26, pushed state `a619441`)

### E.1 Repository
| Branch | Head | Notes |
|---|---|---|
| `main` | `300d9aa` | Initial commit, untouched (only `README.md`). Has no handoff file. |
| **`submain`** | **`a619441`** | Integration branch, pushed. Since v14 (`651fa59` = the v14 file): `37c10a2` merge of the teammate's `dfb656b` (made on local branch `merge-dejjo-2`), `57428b0` "Widen stand sale actions column", `8ba9563` `insertSale` + test, `81d50de` Stand Sale loads from the database (Step A), `ed1471d` searchable dropdown, `6515631` Step B (Record Sale saves), `7d046f8` banner comments, `00901ff` this file as v15, `746adb3` Delete sale, `c38c0cf` this file as v16, `c975173` + `a950627` + `a619441` the order-side backend, the visible ID columns and the Orders screen read (section 0). `target/` is **not** tracked (0 files). |
| **Working tree** | — | **Not known to be clean.** The user reports two code edits made after the `a619441` push (section 0); his `git status` at the commit of `a619441` listed only `OrdersController.java` as modified, so the edits came later. Which branch he is on is unknown (probably `submain`). Local-only branches that exist: `show-ids` (= `a619441`) and `orders-read` (`c975173`); both are merged and may be deleted (`git branch -d`, which refuses if anything is unmerged). |
| `tempniDejjo` | `931c5ad` | The teammate's working branch, **3 commits ahead of `submain`** (`3aa3590` "all controllers but need polish+not fixed standsale" 2026-11-25, `ce95c44` "all controllers need polishing but fix standsale" and `931c5ad` "ayos na change pass design" 2026-11-26, author Dejjo). **Not merged yet (T14).** Read 2026-11-26 [VERIFIED-GIT]: 21 files changed against the merge base, namely new dialog controllers and screens (Change Password, Error, Exit confirm, Loading, Logout, Order details, Restock, Status confirm, Unsaved changes; names from the diffstat, contents not read), `StandSaleController` (about +198 lines, contents **not read**) and `STANDSALE.fxml` (Actions column 220 px wide; columns set non-sortable and non-reorderable). **Not touched by them:** `OrdersController`, `DashboardController`, `InventoryController`, `pom.xml`. **`target/` is no longer tracked** on this branch (0 files; D39). Trial merge into `a619441` (`git merge-tree`): **one conflict, `STANDSALE.fxml`**; `StandSaleController` merges without conflict, which only means no overlapping lines, not that it works. The earlier `dfb656b` is fully merged. |
| `merge-dejjo-3` | — | **Planned, local-only, not created yet** (T14). |
| `merge-dejjo-2` | `57428b0` | **Local only**, identical to `submain` at that point. Optional cleanup: `git branch -d merge-dejjo-2` (refuses if anything were unmerged). |
| `GUI` `2860443`, `JAVA+DB` `567a3de`, `inventory-read` `3f84784` | | Old/redundant (ancestors of `submain`). |

**The 2026-11-24 merge [VERIFIED-USER, terminal output]:** `git merge origin/tempniDejjo` on `merge-dejjo-2` reported **15 modify/delete conflicts, all under `target/classes/...`** (compiled output that `submain` had deleted). Resolved with `git rm -r -f target` (a plain `git rm -r target` refused because two new `DashboardController$...class` files were staged). `git status` then showed no unmerged paths and **no** `target/` entries (the expected "deletions" never appeared, C44). The merge brought: `DashboardController` (about +296 lines), `InventoryController`, `SidebarController`, `StandSaleController` (about +78), `DASHBOARD.fxml`, `STANDSALE.fxml`, and one test photo in `product-images/`. `pom.xml` was **not** in the changed-files list.

### E.2 Database `sassys_creation_db` (XAMPP MariaDB 10.4.32)
- **`products`**: `product_id`, `name`, `category ENUM('Bouquet','Crochet','Keychain')`, `price`, `stock_quantity`, `low_stock_threshold` (default 3), `is_customizable`, `image_path` (nullable); `chk_price`, `chk_stock`. 44 seeded products (Inventory shows "44 products", Bouquet 24 / Crochet 6 / Keychain 14 [VERIFIED-USER screenshot]). **Stock lives only here**: `stand_sales` has no stock column by design (C46).
- **`orders`**: `order_id`, `customer_name`, `product_id` (FK, RESTRICT), `color`, `custom_description`, `quantity` (>0), `unit_price` (>=0), `arrangement ENUM('Pickup','Meetup')`, `schedule_date`, `status ENUM('Pending','Done','Cancelled')`. **No completed-date or total column.** **Rows since 2026-11-26: 4 TEST rows** (see the test-data bullet).
- **`stand_sales`**: `sale_id` AUTO_INCREMENT PK, `product_id` (FK, RESTRICT), `quantity` (>0), `unit_price` (>=0, the price **at the time of sale**), `sale_date` DATE. Total = `quantity * unit_price`, not stored.
- **`users`: does not exist yet.**
- **Test data added 2026-11-26 (this supersedes the "cleaned" bullet below for `orders`, and for `stand_sales` at the time of the last screenshot):** `orders` holds 4 TEST rows [VERIFIED-USER phpMyAdmin screenshots and console text]: id 1 TEST Ana, product 2, qty 1, 30.00, 2026-11-20, Done; id 2 TEST Ben, product 35 (Customized Keychain), qty 2, **150.00** (changed from 0.00 by an `UPDATE`, because customizable products have catalog price 0 and the price is agreed per order), 2026-11-22, Done; id 3 TEST Cara, product 2, qty 1, 30.00, 2026-11-24, Pending; id 4 TEST Dan, product 42 (Swimwear - Customize), qty 1, 0.00, 2026-11-25, Cancelled. Delete them with `DELETE FROM orders WHERE customer_name LIKE 'TEST %';` when they are no longer wanted (never `TRUNCATE`); the ids they used stay used. `stand_sales` held one throwaway sale (id 3) at the last screenshot [VERIFIED-USER screenshot]; its present state is unknown.
- **Id rule (L53):** ids are never reused and never reset. `AUTO_INCREMENT` keeps counting past deleted rows (the counter lives in the server; not tested across a restart in this project). **Never** run `TRUNCATE`, `ALTER TABLE ... AUTO_INCREMENT = 1`, `schema.sql` or `seed.sql` on a live table: each can reset the counter. **`products.name` has no UNIQUE constraint** [VERIFIED-GIT `schema.sql`], which the re-add rule needs (T15).
- **Test data: cleaned (2026-11-25).** `stand_sales` has 0 rows; product 2 (Light Green Leaf) has stock 20 and Baby Dinosaur Keychain has stock 10 [USER-REPORTED]. The Light Green Leaf test sale was removed through the app's own Delete (by accident), which also gave its stock back, so it doubled as the first Delete test; the throwaway Baby Dinosaur sale was then recorded and deleted on purpose. Ids are never reused, so the next sale gets an id higher than every id used so far (1 and 2 were test rows): normal. Never use `TRUNCATE` (C41) and never run `schema.sql` / `seed.sql`.
- **Photos:** the `image_path` column exists in the user's database. The Inventory list view shows a photo for Light Green Leaf [VERIFIED-USER screenshot], so `image_path` is filled at least for product 2. Whether the whole `add_image_path.sql` was run was **not stated**, and many of its mappings are guesses (D41). Image files are flat in `src/main/resources/.../images/`: all 43 filenames named in that script exist there (checked on `submain`); product 35 has no mapping on purpose.
- **IntelliJ's Database tool** (the "Database" panel on the right) lost its login after the 2026-11-24 merge (errors "Unable to resolve table 'products'", `Access denied for user 'User'@'localhost'`); probable cause (inference): the merge removed the tracked `.idea` data-source files. **Fixed by the user** by setting the data source user in its Properties to the same login `DBConnection` uses, then Test Connection [VERIFIED-USER]. This is per computer; teammates configure their own (D60). It does not affect the running app.
- A teammate's database gets `stand_sales` by running **only the `CREATE TABLE stand_sales (...)` block** from `schema.sql` in phpMyAdmin, never the whole file. (An additive `database/stand_sales.sql` does not exist yet.)

### E.3 What is real and what is sample (read from the code on `a619441`)
- **Database-backed:**
  - Inventory read (`ProductDAO.getAllProducts`, `ORDER BY name`; the Inventory list/grid shows that order unless the local sort edit of section 0 is applied) and Add product (`insertProduct`).
  - **Orders list** (since `a619441`): `OrdersController.loadOrders()` -> `OrderDAO.getAllOrders()`; cards show "Order #id" and the order total; the status chips count the **loaded list** (`allOrders`), not a query. The other `OrderDAO` queries (`getCompletedOrders`, `getCompletedRevenue`, `getStatusSummary`) are verified by the test but **no screen calls them yet**.
  - **Visible ID columns** (Inventory list, Stand Sale table). The Inventory **grid** is cards, not a table, and has no ID (Q12).
  - **Stand Sale**: the product dropdown (44 real products, price and stock from the database, **type-to-search**), the sales history, the two chips, Top sellers, Export (they all compute from the loaded `allSales`), **Record Sale** (validation → `StandSaleDAO.insertSale`, one transaction → reload) and **Delete sale** (confirmation dialog → `StandSaleDAO.deleteSale`, one transaction that gives the stock back and deletes the row → reload). Pushed as `6515631` and `746adb3`.
- **Memory only:** Inventory Edit / Restock / Delete (TODO in the code); **the Stand Sale Edit icon** (`editSale` / `applyEdit`, TODO "also UPDATE the sale and the stock in the database"): it changes the screen and the in-memory stock only and is undone by the next reload (D54).
- **Sample data:** Dashboard (hardcoded, "TEMPORARY sample data", also in `dfb656b`), Orders **Delete / Edit / Add** (the Orders list is database-backed since `a619441`, but these three actions change the screen only: D66), the sidebar **Orders badge** and the Dashboard **Reminder** card (4; Pickup 3, Delivery 1 [VERIFIED-USER screenshots] while the database has 1 Pending order: D65), AddOrder (3 sample products), Login (hardcoded demo credentials in `LoginController`), Forgot password (hardcoded answer), Change password (accepts any current password, saves nothing).
- **Forced maximize:** `LoginController` calls `stage.setMaximized(true)` after login (line 204 on the teammate's branch; comment "remove this line if you don't want it maximized"). Kept on purpose: a big window suits the defense. Layouts must therefore fit a maximized window **and** scroll when they do not (D55, D56).
- Stand Sale in the teammate's `dfb656b`: Qty and Unit-price filter dropdowns (position-based, see the banner in `BANNER_COMMENTS.txt`), a wider left column, the totals chips next to the filters.
- Dashboard cards: "Revenue" equals the stand-sale sample total and "Pending" equals the pending-order sample total, so order revenue has no card yet. `dfb656b` rewrote the Dashboard: **re-read it before assigning Dashboard work.** Read on `a619441`: the Dashboard uses a **fixed demo date** (`DEMO_TODAY = 2026-05-03`, so "this week" is Apr 27 to May 3, 2026 [VERIFIED-USER screenshot]) and sample numbers; `renderStatusGauge(pending, done, cancelled)` takes plain integers, so `OrderDAO.getStatusSummary()` can feed it, but the gauge is per year, so a year-aware query will be needed (not built).

### E.4 Verified (with evidence)
- **Merge, pushes** [VERIFIED-USER terminal output]: `37c10a2` (merge), fast-forward of `submain` and push `651fa59..57428b0`; push `57428b0..8ba9563`. The `1 file changed` word-diff showed the Actions column change `prefWidth="80.0"` → `minWidth="100.0" prefWidth="100.0" maxWidth="100.0"`.
- **`insertSale` test** [VERIFIED-USER console + phpMyAdmin]: "Test 1 OK. New sale id: 1", "Test 2 OK (expected failure): Not enough stock, or the product does not exist.", exit code 0. Product 2 stock 20 → 19 (exactly 1 less), `stand_sales` count 0 → 1, `unit_price` 30.00 equals `products.price`, and the 999999 attempt changed nothing (rollback proven).
- **Step A** [VERIFIED-USER screenshot]: dropdown lists real products (Baby Dinosaur Keychain 90.00, stock 10), history empty ("No sales recorded yet"), chips 0 / ₱0.00, Top sellers "No sales yet".
- **Searchable dropdown** [USER-REPORTED "its all goods and working"]: five behaviours were tested by the user (select-all on click, narrowing while typing, full list when opened, revert on leaving with half-typed text, Enter picks the first match); only the overall result was reported.
- **Step B** [VERIFIED-USER screenshots + phpMyAdmin, 2026-11-24]: pushed as `6515631` [VERIFIED-USER terminal, 2026-11-25].
- **Banner comments and v15** [VERIFIED-USER terminal, 2026-11-25]: `7d046f8` (6 files changed, 139 insertions, 18 deletions) and `00901ff` (1 file changed); both pushed (`6515631..7d046f8`, `7d046f8..00901ff`).
- **Delete sale** [VERIFIED-USER terminal and screenshot; the test results are USER-REPORTED]: commit `746adb3` (2 files changed, 69 insertions, 10 deletions); `git merge --ff-only` = Fast-forward `00901ff..746adb3`; push `00901ff..746adb3` on the **second** attempt (the first was rejected by GitHub's "Internal Server Error"); `git status` clean and up to date; `git log --oneline -3` shows `746adb3` on top. The agent also cloned `submain` and read the pushed code: `deleteSale` exists in the DAO and in the controller and the "DATABASE ACCESS" banner lists it [VERIFIED-GIT].
- **Edit dialog facts** [VERIFIED-GIT, read on `746adb3`]: `EditSaleController` shows a **read-only** unit price (`editable="false"`); the same product keeps the price the sale was made at, a different product uses its catalog price; it calls back `onSave(product, qty, unitPrice, date)`.
- The app starts, shows Login, and opens Dashboard, Inventory (44 products, grid and list view, search "ligh" finds Light Green Leaf), Stand Sale [VERIFIED-USER screenshots].
- IntelliJ shows 0 problems in `StandSaleDAO.java` after the brace fix [USER-REPORTED "no errors now"].

- **Order-side backend** [VERIFIED-USER console text, 2026-11-26]: `OrderDAOTest` (read-only) printed `All orders: 4` (ids 4, 3, 2, 1, newest date first, `Swimwear - Customize`, `Light Green Leaf`, `Customized Keychain`, `Light Green Leaf`), `Status summary: {Pending=1, Done=2, Cancelled=1}`, `Completed orders: 2` (ids 2 and 1), `Completed revenue: 330.00`; exit code 0. The same four order lines printed before and after the `mapOrder` refactor, so the refactor changed no result.
- **Pushes** [VERIFIED-USER terminal, 2026-11-26]: `a950627` on `submain` (his `git log` showed `HEAD -> show-ids, origin/submain, submain`), then `git merge --ff-only show-ids` = Fast-forward `a950627..a619441` and `git push` `a950627..a619441`. A fresh clone by the agent shows `origin/submain` = `a619441` [VERIFIED-GIT]. The agent also listed all remote branches: `main` `300d9aa`, `submain` `a619441`, `tempniDejjo` `931c5ad`, plus the old `GUI`, `JAVA+DB`, `inventory-read`.
- **Screens** [VERIFIED-USER screenshots, 2026-11-26]: Inventory list shows the ID column with real ids; Stand Sale shows the ID column, one sale (id 3), chips "Total Stand Sales: 1" and "Total Revenue: 90.00", Top sellers "1 sold", the Delete icon still partly clipped (D55); the Dashboard weekly chart overflows its card and hides Sunday and the right arrow behind the Reminder card (D64). The run log shows only warnings (a restricted-method warning and "Loading FXML document with JavaFX API of version 26 by JavaFX runtime of version 25.0.5"), no errors: harmless runtime warnings.
- **Dashboard overflow diagnosis** [VERIFIED-GIT code + measurement from the screenshot; the fix is NOT verified]: the chart needs about 760 px (7 columns x (26 + 4 + 26) + 6 x 40 spacing + 24 padding, plus two 44 px arrows and 16 px of spacing) but the card measured about 640 px wide (about 604 px inside) in the user's window, so about 156 px spilled out and the later-drawn Reminder card painted over it. The cut-off legend comes from the same shortage.

### E.5 Not verified / unknown
The **Orders screen** (4 cards, chips All 4 / Pending 1 / Done 2 / Cancelled 1) has never been shown; whether the user's two local edits (Inventory sort, Dashboard chart fit) work and whether they are committed; whether the throwaway sale (id 3) has been deleted; whether the full `add_image_path.sql` ran (E.2); the Spinner test on Stand Sale / Add order / Add product (pending since 2026-11-16, result not stated); the phpMyAdmin results of the Delete test (reported as expected, not pasted); the AddOrder behaviour in the merged build; **whether the teammate's `StandSaleController` changes (about +198 lines, not read) keep Record and Delete working** (T14); which defense is "next week" (Q1); who owns which files (Q2); whether the layout message reached the teammate (Q8); whether Inventory grid cards should show an ID (Q12); the contents of v13 (not readable by the sessions that wrote v14 to v17). *(The `orders` table contents are now known: 4 TEST rows, E.2. The Delete icon is not in this list: it is still partly clipped, D55.)*

---

## F. TASKS

### F.1 The team's task list (from the user, 2026-11-22; re-sent 2026-11-26) and what the code says (updated 2026-11-26, second session)
**The user's own reading (2026-11-26): "as far as I know, the table for stand sale only is the one I accomplished."** The statuses below follow that, and add what the code shows.

| Owner | Item (as written) | Status / notes |
|---|---|---|
| Raven | id in inventory and stand sale | **Visible now** (pushed in `c975173`) [VERIFIED-USER screenshots]: the Inventory list shows real product ids and Stand Sale shows `sale_id`; Orders cards already show "Order #id". **Still owed:** (1) the **permanence rule** (L53: ids never reused, a re-added product keeps its old id; soft delete, **T15**); (2) the Inventory **grid** cards have no ID (Q12). The user wants IDs in **all** sections. |
| Raven | table for stand sale records only | **Done (the one item the user counts).** `stand_sales` exists; the screen reads it, records sales and deletes sales in the database. |
| Raven | table for completed orders and revenue | **Backend done and verified** (L38: a query on `orders`): `OrderDAO.getCompletedOrders()` and `getCompletedRevenue()` (330.00 on the test data) [VERIFIED-USER console text]. **No screen shows it yet** (Dashboard / Orders wiring, F.4). |
| Raven | table summary done pending cancel orders | **Backend done and verified** (L38): `OrderDAO.getStatusSummary()` = `{Pending=1, Done=2, Cancelled=1}` [VERIFIED-USER console text]. The Orders chips count the loaded list (they match today); the Dashboard gauge is still sample data (D65); a year-aware version will be needed for the Dashboard (not built). |
| Ravs | product dropdown | **Stand Sale: done** (real products, searchable; built by the user). AddOrder (`cbOrderProduct`) still uses 3 sample products. Which dropdown the item means is still unconfirmed (Q5). |
| Ravs | change password | Needs a `users` table first (Raven's side). Today it accepts any current password. |
| Ravs | standsale button arrow date | **Unclear from the code.** The Record form has no date control (it records today's date); the only `DatePicker` is in the Edit Sale dialog. Ask (Q5). |
| Ravs | standsale newest oldest | The DAO sorts newest first and the table defaults to Date descending; the Date column header can be clicked to sort. Whether a separate toggle is wanted is unconfirmed. |
| Ravs | green stock inventory | In the Inventory **list** view the normal-stock badge uses `stock-plain`; the grid view uses green `badge-done`. In the 2026-11-24 screenshots both views show a green "in stock" badge, so this item **may already be done in `dfb656b`: re-check before assigning.** |
| Ravs | order rev dashboard; revenue by day (orders and stand sales) chart; filter by month (dashboard stand rev and order rev) | Depend on Raven's queries (L38, L39). `dfb656b` draws two-tier bars (stand vs order) on sample data. Stand sales can now feed it from the database. |
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
- 2026-11-24 **Step B, Record Sale saves to the database:** pushed as `6515631`. **[VERIFIED-USER screenshots + terminal]**
- 2026-11-25 banner comments `7d046f8` and this file as v15 `00901ff`. **[VERIFIED-USER terminal]**
- 2026-11-25 **T8:** Step B, banners and v15 pushed. **[VERIFIED-USER terminal]**
- 2026-11-25 **T9 Delete sale:** `StandSaleDAO.deleteSale`, controller wiring and the `"SaleId"` row key: `746adb3`. **[VERIFIED-USER terminal; test results USER-REPORTED, one screenshot]**
- 2026-11-25 test data removed through the app (E.2). **[USER-REPORTED]**
- 2026-11-26 **T12 done (order-side read) and pushed:** `Order`, `OrderDAO` (`getAllOrders`, `getCompletedOrders`, `getCompletedRevenue`, `getStatusSummary`, `mapOrder`), `OrderDAOTest`, the Orders screen read: `c975173`, `a950627`, `a619441`. **[VERIFIED-USER console text and push output; the Orders screen itself is not yet shown]**
- 2026-11-26 **Visible ID columns** (Inventory list, Stand Sale) pushed inside `c975173`. **[VERIFIED-USER screenshots]**

### F.3 NEXT BATCH (Batch 6, for Raven): 3 tasks
*(Order = the assistant's recommendation; the user may reorder. One slice at a time, each with its evidence, C.1 #5 and #13.)*

1. **T13: verify, then commit, three things that exist but were never shown.**
   - **(a) The Orders screen (pushed in `a619441`).** Run the app, open Orders, send **one screenshot** (it is a layout check) and the text "0 errors" for the build. Expected: **4 cards**, newest first: TEST Dan (Cancelled), TEST Cara (Pending), TEST Ben (Done, 300.00), TEST Ana (Done, 30.00); chips All 4, Pending 1, Done 2, Cancelled 1; the filter chips still work. **Do not use Delete, Edit or Add on Orders** (D66).
   - **(b) The Inventory default sort** (the user's local edit, section 0). Run, then screenshot the list view: ids ascending 1, 2, 3 ... Check the grid view too. Commit it **alone**.
   - **(c) The Dashboard weekly-chart fit** (the user's local edit, section 0). Screenshot at the usual window size **and** maximized: both arrows and all 7 days must be visible inside the card. Commit it **alone**. If it still overflows at the smallest window (about 1000 px; the estimate is that it can overflow by about 45 px there), say so and decide with the user whether to accept it or raise the window's minimum width.
   - **Commands (PowerShell, one per line):** `git branch --show-current`, `git status`, then for each verified edit stage **its own file(s) by path** (not `git add .`, because the tree may hold the other edit): for (b) `git add src\main\java\com\sassyscreation\sassyscreationgui\controllers\InventoryController.java` and `git commit -m "Inventory: default sort by ID ascending"`; for (c) `git add src\main\java\com\sassyscreation\sassyscreationgui\controllers\DashboardController.java src\main\resources\com\sassyscreation\sassyscreationgui\DASHBOARD.fxml` and `git commit -m "Dashboard: weekly chart fits its card"`. Then `git push`. If the user is on a slice branch, `git switch submain`, `git merge --ff-only <branch>`, `git push`; **delete a slice branch only after the push is confirmed** (C.4, C48).
   - **What NOT to do:** do not commit before the screenshot; do not use `git add .` while two unrelated edits are in the tree; do not change `ORDER BY name` in `ProductDAO` (L55); do not touch `OrdersController`'s Delete/Edit/Add yet.
2. **T14: merge the teammate's `tempniDejjo` (`931c5ad`), only after T13 is pushed.** Work on a throwaway **local** branch (C.4); never push the teammate's branches.
   - **Steps:** `git fetch origin`; `git --no-pager log --oneline -3 origin/tempniDejjo` (expect `931c5ad` on top); `git status` clean on `submain`; `git switch -c merge-dejjo-3`; `git merge origin/tempniDejjo`. **Expected: a conflict in `STANDSALE.fxml` only.** If anything else conflicts, `git merge --abort` and read the output first.
   - **Resolve `STANDSALE.fxml`** (IntelliJ shows the markers): keep **their** side (the part after `=======`), delete the three marker lines, and add our ID column as the **first** column in their style: `<TableColumn fx:id="colSaleId" prefWidth="50.0" reorderable="false" sortable="false" text="ID"><cellValueFactory><MapValueFactory key="SaleId" /></cellValueFactory></TableColumn>`. Check no marker is left: `Select-String -Path src\main\resources\com\sassyscreation\sassyscreationgui\STANDSALE.fxml -Pattern "^<<<<<<<|^=======|^>>>>>>>"` (prints nothing). **Do not "accept theirs" for the whole file** (it drops the ID column).
   - **Then:** `git status` (expect no `target/` entries), `git add .`, `git commit -m "Merge teammate's tempniDejjo (931c5ad)"`, `git diff --stat -- pom.xml` (**expect no output**, D40).
   - **Test before touching `submain`** (git merges `StandSaleController` cleanly, which does not prove it works; its +198 lines were never read): build "0 errors"; Stand Sale loads products and sales and shows the ID column; **Record a sale** (stock drops, row appears); **Delete it** (stock returns); the Delete icon is fully visible **maximized and at the default size** (D55); Top sellers scrolls with many products (D56); Inventory and Orders still load; the new dialogs open (Change password, Logout, ...); Edit is still screen-only. One screenshot of Stand Sale (layout check).
   - **Then** `git switch submain`, `git merge --ff-only merge-dejjo-3`, `git push`. `merge-dejjo-3` stays local. Read the teammate's `StandSaleController` diff with the user afterwards (L35, D45): he must be able to explain it.
   - **What NOT to do:** do not merge on `submain` directly; do not push `merge-dejjo-3`; do not resolve by "accept theirs"; do not edit the teammate's FXML beyond adding the ID column (Q2).
3. **T15: soft delete for products and the id rule (Raven's "id" item, L53; this is also W2, product Delete -> database, D34).** *[Design RECOMMENDED; the user chose soft delete but has not agreed the details; confirm before coding, C.1 #5.]*
   - **Goal:** a product deleted in the app is gone from every screen, also after a restart; the database row stays; adding the **same product** again gives it its **old id**; a **new** product gets a **new, higher id**; ids are never reused.
   - **Options:** **A soft delete (chosen by the user):** add `products.is_active`; Delete sets it to 0; adding a name that already exists reactivates that row. **B hard delete + registry table** (name -> id remembered): rejected for now (second table, two-step logic, breaks if a product is renamed).
   - **Trade-offs:** A needs an additive `ALTER TABLE` and every product read must filter `is_active = 1`; deleted products stay visible in phpMyAdmin; but sales/orders history stays correct and the FK `RESTRICT` block on deleting a referenced product is no longer needed (domain rule in H changes when this is built).
   - **Steps (one at a time, each verified):** (1) read-only check for duplicate names: `SELECT name, COUNT(*) FROM products GROUP BY name HAVING COUNT(*) > 1;` (expect no rows); (2) additive SQL in phpMyAdmin, **not** `schema.sql`: `ALTER TABLE products ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1;` then `ALTER TABLE products ADD CONSTRAINT uq_products_name UNIQUE (name);`; ask the user whether to add the same lines to `schema.sql` / an additive `database/products_soft_delete.sql` so a teammate's fresh database matches; (3) `ProductDAO`: `getAllProducts()` gets `WHERE is_active = 1`; new `softDeleteProduct(int productId)` (`UPDATE products SET is_active = 0 WHERE product_id = ?`); `insertProduct` first looks the name up: **inactive row found -> reactivate it with the new category, price, stock and image (same `product_id`)**; **active row found -> refuse with "A product with this name already exists"**; **none -> INSERT** (new id). MariaDB's default collation ignores case, so "dark green leaf" matches "Dark Green Leaf" (state this to the user); (4) wire Inventory's existing Delete action (memory-only today, D34) to `softDeleteProduct`, then `loadProductsFromDB()`.
   - **Facts to keep true:** the `JOIN products` in `StandSaleDAO` and `OrderDAO` has **no** `is_active` filter on purpose, so old sales and orders still show a deleted product's name; the Stand Sale dropdown uses `getAllProducts()` and so hides deleted products (correct).
   - **How to verify:** with a throwaway product: delete it, restart the app, it is still gone; `SELECT product_id, name, is_active FROM products WHERE name = '<name>';` shows the **same row** with 0; add the same name again -> same `product_id`, `is_active` = 1; add a different new name -> `product_id` = the highest id ever used + 1; an old sale of that product still shows its name.
   - **What NOT to do:** do not run `schema.sql` / `seed.sql`; do not `TRUNCATE` or `ALTER TABLE ... AUTO_INCREMENT = 1`; do not delete rows by hand in phpMyAdmin; do not wire Edit/Restock in the same slice (W3).

### F.4 Queue after Batch 6 (order follows the team list and the defense; revise after Q1)
- **Dashboard wiring (the user's own goal, 2026-11-26): "finish the dashboard, especially the graph, connected to the backend and the database". REMIND HIM when T13 to T15 are done.** Steps, one slice each: (1) make "today" and the year selector real (the Dashboard uses `DEMO_TODAY = 2026-05-03`); (2) the Orders-by-status gauge from `OrderDAO.getStatusSummary()` (add a year-aware version; the gauge is per year) and the sidebar badge / Reminder counts from the real Pending orders (D65); (3) the cards: revenue = stand sales + Done orders (D51; `getCompletedRevenue()` exists, a by-day and by-month version does not); (4) the weekly two-series chart (stand vs order revenue by day; `stand_sales` by `sale_date`, `orders` Done by `schedule_date`, L39) and the month filter (Ravs' items). Re-read `DashboardController` first; it changed in `dfb656b`, and D53 (whole-peso integers) applies.
- **Orders actions to the database (W4 to W6; D66):** Delete (confirm, then `DELETE`), status change (Pending -> Done / Cancelled) and Add order; note the domain rule: regular orders deduct 1 unit, customizable orders deduct no stock (H). Until then the Orders screen is read-only in effect: do not demo them on real rows.
- **Batch 7:** `users` table + Login + Change password (JDK `PBKDF2`, **no new library without asking**).
- **Still open from v13:** W3 Edit/Restock -> DB, one shared `Product` class (D49), the guided read-through of teammate-written code (L35, D45), the paper (ERD, test plan, likely defense questions). (W2 product Delete -> DB is T15.)
- **Lower priority, still owed:** the Stand Sale money values as `double` instead of `BigDecimal` (D62); AddOrder's product dropdown still uses 3 sample products (Q5).
- **Tasks moved here from Batch 5 (kept verbatim):**
- **T11: Edit sale in Stand Sale. Q7 ANSWERED by the user 2026-11-26: option B, Edit must be wired and work (not hidden).** Work on a slice branch (`standsale-edit`, C.4).
   - **Goal:** nothing on the Stand Sale screen looks saved when it is not (D54). Delete already saves; Edit is the last screen-only action.
   - **Facts read from the code on `746adb3`:** `editSale(row)` opens `EditSaleController` with the row's product name, quantity, unit price and date plus the product list; its unit price is **read-only**: the same product keeps the price the sale was made at, a different product uses the catalog price. On save it calls `applyEdit(row, name, qty, unitPrice, date)`, which gives the old quantity back to the old product and takes the new quantity from the new one **in memory only**, then updates the row. No FXML change is needed for either option.
   - **Options:** **A** hide the Edit icon until Edit is wired. In `setUpTable()` the Actions cell builds `new HBox(10, edit, delete)`; build the HBox with `delete` only, leave `editSale` / `applyEdit` unused, and change their banner to "NOT IN USE (Edit icon hidden until it saves to the database)". **B** wire Edit: add `StandSaleDAO.updateSale(int saleId, int newProductId, int newQty, LocalDate newDate)` as one transaction: (1) read the sale's current product, quantity and `unit_price` **from the database**; (2) give the old quantity back to the old product; (3) take the new quantity from the new product with the guard `... AND stock_quantity >= ?` (0 rows → "Not enough stock" → rollback); (4) price rule that mirrors the dialog, L40 and L45: **same product → keep the stored `unit_price`; different product → read its current price from `products`**; (5) `UPDATE stand_sales SET product_id = ?, quantity = ?, unit_price = ?, sale_date = ? WHERE sale_id = ?`; (6) `commit()`, `rollback()` on any `SQLException`. The controller passes `row.get("SaleId")` and `products.get(name).id`, then reloads like Record and Delete do. **C** hide both icons (superseded now that Delete works).
   - **Trade-offs:** A takes minutes and cannot corrupt stock, but the user loses a visible feature he wants. B is about 60 lines in the pattern the user has already built twice (`insertSale`, `deleteSale`), but it is the riskiest of the three Stand Sale writes: two products' stock and one row change together. **Give the old quantity back before taking the new one**, otherwise editing the same product from 5 to 6 is refused when only 1 is left.
   - **Recommendation (assistant, not agreed; updated 2026-11-26): B, with A as the fallback** if B cannot be finished and verified before the defense. v15 recommended A; this changes because Delete took one small slice and went smoothly, Edit reuses the same pieces, and hiding a feature the user listed as a to-do is not what he asked for. **The user decided B on 2026-11-26** ("i don't want the edit to be defected, i want it to function"); A stays only as the assistant's fallback if B cannot be verified before the defense, and only with the user's agreement.
   - **What to understand first:** why restore-then-deduct is one transaction; why the price comes from the database and not from the dialog; what the stock guard protects.
   - **How to verify (B):** with a throwaway sale: (a) same product, quantity up and down, (b) change product, (c) quantity above the new product's stock (expect the warning and **no** change), (d) date only. After each, `SELECT * FROM stand_sales;` and the stock of **both** products in phpMyAdmin (paste the text), then delete the throwaway sale and check stock returns to the start values.
   - **What NOT to do:** do not demo or test Edit on real rows before it is wired; do not edit `EDITSALE.fxml` (teammate's file); do not change the dialog's price rule.

- **T10 (partly folded into T14): sync with the teammate and settle ownership (Q2, Q8).** The user relays the message in K.3 (updated 2026-11-26: it now says that Delete saves, that Edit is still screen-only, and asks not to change the Edit/Delete code without telling him). Ask who edits `StandSaleController` and `DashboardController` from now on, ask the teammate to pull `submain` **before** editing either, and ask for the two `STANDSALE.fxml` fixes (D55 clipped Delete icon, D56 Top sellers scroll). Then re-check both on a maximized window and at the default size (a screenshot is justified here: it is a layout check, C.1 #16). **Do not edit the teammate's FXML ourselves.**

---

## G. OPEN QUESTIONS FOR THE USER (ask only what is still open)
- **Q1. Which defense is "next week" and what must it show?** Partly answered 2026-11-24: "this whole system's defense is next week". Still unknown: the date, which screens and documents, whether code is inspected. It sets every priority.
- **Q2. Who edits what from now on?** Partly answered 2026-11-24: the controllers are edited by the user or Ravi. Still open: who owns `DashboardController` and the FXML files, and whether Dejjo = Ravi = Ravs.
- **Q3. Is the repository meant to be public?** (It is readable anonymously today; this file avoids secrets and full names either way.)
- **Q4. `product-images/` policy.** The folder is now tracked with one test photo and `.gitignore` has no line for it. What should the app store in `image_path` for newly added products, and should the test photo be removed from git? (D36, D57)
- **Q5. Ravs' unclear items:** which product dropdown, and what is "standsale button arrow date"?
- **Q6. When a product is deleted, keep its photo file?** (Recommended: yes, keep it.)
- **Q7. Edit sale (and the icon). ANSWERED 2026-11-26: option B.** The user wants Edit **wired and working**, not hidden ("i don't want the edit to be defected, i want it to function because it is already in there"). Delete is wired (`746adb3`). Plan: T11 in F.4 (comes after T13 to T15 in the recommended order; the user may move it earlier).
- **Q8.** Did the layout message (K.3) go to the teammate, and did they answer? (Still open; the message now also mentions Delete.)
- **Q9. Answered by action (2026-11-25):** the Delete slice used a slice branch (`standsale-delete`) and `--ff-only`; keep doing that unless the user says otherwise.
- **Q10. Resolved by events (2026-11-25):** the test sale was removed through the app and the stock is back; nothing left to clean. *(New test rows were added on 2026-11-26: E.2.)*
- **Q11. Answered 2026-11-26:** must a "deleted" product be physically gone from phpMyAdmin, or only from the app screens? The user chose **only from the app screens** (soft delete, L53).
- **Q12. Should the Inventory grid (card) view show the product ID too?** The list view does; the cards have none. It is an edit in the teammate's `InventoryController`; ask first (Q2). Open.
- **Q13. Dashboard "today":** once wired, should "this week" and the year selector follow today's real date? (Recommended: yes; today it is fixed to 2026-05-03.) Open.

---

## H. DECISIONS (binding; the full v13 list L1–L37 is in the archive)

**Still binding from v13:** L1 keep the teammate's plural `controllers/` · L2 FXML stays flat · L3 `mainClass` = `Launcher` · L6 do not touch Spinner `min/max/initialValue` in FXML · L7 keep `exports ...sassyscreationgui;` · L11 `BigDecimal`/`DECIMAL`, stock rules later in a service with transactions · L16 never run `schema.sql`/`seed.sql` to test a connection · L23 delete no FXML before the defense · L25/L29–L31 no blind merges; `pom.xml` and `.gitignore` = ours; additive SQL only · L32 integration branches stay local · L34 gate rule · L35 a guided read-through of the teammate-written code precedes more wiring (still recommended; D45).

**Domain rules:** 44-product seed (all 44 products of the original C system); `is_customizable` TRUE only for original ids 35, 42, 43, 44; categories only Bouquet/Crochet/Keychain (no Material category); deleting a product that orders or sales reference is **blocked** (FK `RESTRICT`) with a friendly message *(true in the pushed code today; **to be superseded by L53** when the soft-delete slice T15 is built: a deleted product is only hidden, so nothing needs blocking)*; customizable orders may have quantity > 1 with an agreed per-unit price (total = quantity × unit price), the customization text is stored on the order, and **no** ready-made stock is deducted; regular orders are 1 unit and deduct 1 (this supersedes the 2026-10-24 "no quantity column" decision for customizable items only); no raw-material inventory; **low stock when `stock_quantity <= low_stock_threshold`** (default 3). Stand sales may have any quantity within the stock and always deduct stock.

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
| L48 | **Stand Sale Edit and Delete icons.** **Delete: wired** (`746adb3`, L51). **Edit: still unwired**; the user listed it as a to-do (2026-11-26). Options and the updated recommendation are in T11 (B with A as the fallback). | **Delete done. Edit pending the user (Q7).** |
| L49 | **Banner comments** mark specific logic (C.5); scope = backend files + `StandSaleController`; shipped as `BANNER_COMMENTS.txt`. | **[DECISION]** user request 2026-11-24. |
| L50 | IntelliJ's Database tool uses the same login as `DBConnection` (set in the data source Properties); never committed, and no password is written in this file. | **[DECISION by action]** fixed 2026-11-24. |
| L51 | **Delete sale design:** `StandSaleDAO.deleteSale(int saleId)` runs in one transaction on one connection: `SELECT product_id, quantity FROM stand_sales WHERE sale_id = ?` (no row → "Sale not found" exception), `UPDATE products SET stock_quantity = stock_quantity + ?`, `DELETE FROM stand_sales WHERE sale_id = ?` (0 rows → exception), `commit()`, `rollback()` on any `SQLException`. The quantity and product come **from the database**, not from the screen row. The controller reloads products and sales afterwards (L44). Table rows carry the id under the key `"SaleId"`. | **[RECOMMENDED, built, tested by the user, pushed `746adb3`]** Alternative: remove the row from memory only (what it did before; the screen and the database disagree). Defense line: "the stock is given back and the sale is removed together or not at all (atomicity); the amount to give back is read from the database, so a stale screen cannot restore the wrong quantity." |
| L52 | **Evidence by text, not screenshots, when text is enough** (user, 2026-11-25: screenshots use too many tokens). Screenshots only for layout checks. Now rule C.1 #16. | **[DECISION]** user request. |
| L53 | **Ids are permanent and visible everywhere.** Every section shows its id (Inventory, Stand Sale, Orders). Ids are never reused: no `TRUNCATE`, no `ALTER TABLE ... AUTO_INCREMENT = 1`, no `schema.sql` on a live table. A product deleted in the app is **soft deleted** (`is_active = 0`, the row stays); adding the same product (same name) again **reactivates the same row, so it keeps its old id**; a genuinely new product gets a new, higher id. Sales and orders have no natural identity, so for them "never reused" is the whole rule. | **[DECISION by the user, 2026-11-26]**, details RECOMMENDED (T15). Alternative rejected for now: hard delete + a registry table (B). Defense line: "history stays correct and ids identify one thing forever." |
| L54 | **Order of work and Edit:** the order-side slice (T12) went **before** Edit sale, and **Edit sale must be wired and work** (Q7 = B), not hidden. | **[DECISION by the user, 2026-11-26]** |
| L55 | **Inventory default order = ID ascending**, done in `InventoryController.loadProductsFromDB()` (`PRODUCTS.sort(Comparator.comparingInt(Product::getId))`), **not** by changing `ORDER BY name` in `ProductDAO`. | **[DECISION by the user for the behaviour; implementation RECOMMENDED, applied by the user, unverified]** Reason: `StandSaleController` also calls `getAllProducts()` for its dropdown, which should stay A to Z. Covers the list and the grid; a header click still sorts. |
| L56 | **Dashboard weekly chart fits its card:** slimmer bars (`BAR_WIDTH` 20, `BAR_PAIR_GAP` 3), no fixed spacing between day columns, each day column grows equally (`HBox.setHgrow(column, Priority.ALWAYS)`). | **[RECOMMENDED, applied by the user, unverified]** Alternatives: a fully resizable chart (more code, harder to explain), changing the 60/40 split with the Reminder card, raising the window's minimum width. Known limit: about 45 px of overflow may remain at the smallest window (estimate). |
| L57 | **Orders card price = order total** (`Order.getTotal()` = quantity x unit price, derived, never stored; same rule as `StandSale`, L40). | **[BUILT, `a619441`]** A customizable order can have quantity > 1. |

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
| C48 | The agent's command block for the Delete slice put `git push` and then `git branch -d standsale-delete` in one run, without saying to stop if the push failed. | The push was rejected by GitHub (remote "Internal Server Error") and the branch was deleted anyway. No harm: `746adb3` was already on local `submain` and the retry of `git push` worked. Rule (C.4): delete a slice branch only **after** the push is confirmed, and do not force-push or reset over a remote error. |
| C49 | An agent offered `ALTER TABLE stand_sales AUTO_INCREMENT = 1` as an optional "cosmetic" cleanup (C41, and the cleanup notes around it). | It **contradicts the user's id rule (L53)**: an emptied table would restart at 1 and reuse ids. C41's other advice stands (use `DELETE`, never untick foreign-key checks); the `ALTER ... AUTO_INCREMENT` part is withdrawn. |
| C50 | The agent told the user the visible-ID FXML edits would appear as modified in `git status`. | They did not: they had already been pushed inside `c975173` (most likely because `git add .` was used after the FXML edits were saved; the commit message mentions only the Orders code). The agent found it with `git show --stat` on the remote. Lesson: look at what a commit contains before predicting `git status`. The history is not rewritten. |

---

## J. OPEN DEFECTS AND RISKS (v13/v14 items kept; status updated 2026-11-26)
| ID | Defect / risk | Status |
|---|---|---|
| D34 | Inventory **Delete / Edit / Restock change memory only** (and are wiped on reload). | Open (W2/W3). |
| D35 | Teammate's Inventory code swallows exceptions (`catch (Exception e) { e.printStackTrace(); }`): a DB failure shows an empty screen with no message. | Open (fix inside W2). |
| D36 | `product-images/` is created under the project folder on Add product; the stored `image_path` format is unknown. | Open (Q4); see D57. |
| D39 | The teammate's branches still track `target/` and `.idea` files and carry a UTF-16 `.gitignore`. | **Partly resolved 2026-11-26:** `tempniDejjo` at `931c5ad` tracks **no** `target/` files [VERIFIED-GIT], so the next merge should not conflict there. `.idea` and the UTF-16 `.gitignore` were not re-checked. |
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
| **D54** | Stand Sale **Edit changes the screen and the in-memory stock only**; the next reload undoes it, and an Edit shows stock the database never got. (Delete had the same defect until `746adb3`.) | **Half resolved 2026-11-25:** Delete saves (`746adb3`). **Edit still open** (T11, L48; the user wants it wired, Q7 = B). Do not demo Edit on real rows. **The Orders screen has the same defect for Delete/Edit/Add (D66).** |
| **D55** | The **Delete icon is still partly clipped** at the right edge of the sales table, even maximized, after the Actions column was set to 100 px (confirmed again by the 2026-11-25 screenshots, which also show the Product name truncated, "Baby Dinosaur Keych..."); at the default window size the Date, Qty and filter headers are also squeezed ("Filt...", "2026-..."). | Open (teammate's `STANDSALE.fxml`, T10). Now that Delete works, this icon matters more. **Still clipped in the user's 2026-11-26 screenshot on `a619441`** [VERIFIED-USER]; the teammate's `931c5ad` sets the Actions column to 220 px, which may fix it: **verify after T14.** |
| **D56** | The Stand Sale **left column does not scroll**: with more than a few Top sellers the list is cut off at the bottom (`ScrollPane` is imported in the FXML but unused). It may look fine on a taller screen. | Open (T10). Fix: wrap the left `VBox` in a transparent `ScrollPane`. |
| **D57** | `product-images/<number>_n.jpg` (about 550 KB, a test photo) came into `submain` with the teammate's commit; `.gitignore` has no `product-images` line. | Open (Q4). |
| **D58** | Stale comments "TEMPORARY: sample products / sample sales ... Replace with a database query" above `loadProducts()` and `loadSales()` in `StandSaleController`. | **Resolved** by the banner comments (`7d046f8`); the pushed code at `746adb3` has no "TEMPORARY" comment left in `StandSaleController` [VERIFIED-GIT]. Keep banners true: the Edit banner must change when Edit changes (T11). |
| **D59** | `StandSaleDAOTest` writes a real sale and deducts stock each time it runs. | Mitigated by a WARNING banner; never ask the user to re-run it. |
| **D60** | The IntelliJ Database tool login is per computer and was lost after a merge. | Open for teammates: set the data source user in Properties. |
| **D61** | The Qty and Unit-price filter dropdowns (teammate's logic) have not been tested by the user against real database rows. | Open. Their boundaries: Qty 1 / 2–5 / 6–10 / 11+; Price under 30 / 30–49 / 50–99 / 100+ (the 49 and 99 edges treat prices such as 49.50 by `>= 30 && < 50`). |
| **D62** | The Stand Sale screen keeps money as `double` (`Product.price`, row `Price`/`Total`, Top sellers sums) while the database is `DECIMAL` and C.2 says `BigDecimal`. Rounding could differ by a centavo; a likely defense question. | Open (low priority; Batch 5+). |
| **D63** | A `git push` can fail because of GitHub (2026-11-25: "Internal Server Error"); an agent must not treat it as a problem with the commit. | Mitigated: C.4 "If `git push` fails with a remote error" and C48. |
| **D64** | **Dashboard weekly revenue chart is wider than its card** and spills under the Reminder card: Sunday and the **right (next week) arrow are hidden**; the legend is cut ("Stand...", "Order..."). Cause: a fixed minimum content width of about 760 px against a card of about 604 px inside (E.4). Found from the user's circled screenshot. | **Fix applied by the user per L56, unverified, uncommitted** (T13c). Limit: may still overflow by about 45 px at the smallest window (estimate). |
| **D65** | The **sidebar Orders badge and the Dashboard Reminder card show sample numbers** (4; Pickup 3, Delivery 1) while the database has 1 Pending order; the gauge, cards and chart are sample data on a fixed demo date. | Open (Dashboard wiring, F.4). Related to D51. |
| **D66** | The Orders screen now shows **real rows**, but **Delete, Edit and Add on Orders change the screen only** (no DAO call; the next reload undoes them): the same defect D54 had on Stand Sale, now on live data. | Open (F.4, W4 to W6). Do not demo them on real rows. |
| **D67** | `products.name` has **no UNIQUE constraint**, and Inventory Delete is memory-only (D34), so the "re-added product keeps its id" rule (L53) cannot be enforced yet and duplicate names are possible. | Open (**T15**). |
| **D68** | `c975173` mixes backend code and the two FXML ID edits under a message that names only the Orders code (C50). | Accepted (pushed history); noted so a future `git log` reader is not confused. |

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
git clone --no-single-branch https://github.com/Utotz/Sassy-s-Creation-GUI.git sassy   # all remote branches in one go (used 2026-11-26)
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
git ls-remote origin refs/heads/submain      # what GitHub really has (use it after a failed push)
git diff --stat -- pom.xml
git --no-pager diff -U0 --word-diff=porcelain
```
SQL sanity checks (phpMyAdmin SQL tab, read-only):
```sql
SELECT COUNT(*) FROM products;                                   -- expect 44
SELECT * FROM stand_sales;                                       -- 0 rows after the 2026-11-25 cleanup, unless sales were recorded since
SELECT product_id, name, price, stock_quantity FROM products WHERE stock_quantity >= 2 ORDER BY product_id LIMIT 5;
SELECT product_id, name, image_path FROM products ORDER BY product_id;   -- photo check
```

### K.3 Draft message to the GUI teammate (the user relays it; neutral wording)
> Quick sync on `submain`. It now has your `dfb656b`, plus the backend: `stand_sales`, `StandSaleDAO.insertSale` and `deleteSale`, and the Stand Sale screen reading the database (searchable product dropdown; Record Sale and Delete sale save). Please **pull `submain` before editing `StandSaleController` or `DashboardController`**, and tell me who edits them. Three small things in `STANDSALE.fxml`: (1) the Delete icon is still partly cut off at the right edge of the table, so the Actions column needs more room; (2) the left column needs a transparent `ScrollPane` around it, because Top sellers is cut off at the bottom when there are many products; (3) each product choice must keep its `product_id` (the backend's `insertSale` needs it, and I already carry it in `StandSaleController`). Also: please run `git rm -r --cached target` on your branch and push, so `target/` stops conflicting, and keep `pom.xml` at JavaFX 25.0.5 / MariaDB 3.5.7. Everyone creates the `stand_sales` table by running only its `CREATE TABLE` block from `database/schema.sql`, never the whole file. In IntelliJ's Database tool, set your own data source login in its Properties. The Edit icon in Stand Sale is still screen-only, so please do not change the Edit/Delete code in `StandSaleController` without telling me.
>
> **Update 2026-11-26 (send after T14):** `submain` now also has your three newest commits (`931c5ad`), merged. It adds an **ID column** to the Stand Sale table in `STANDSALE.fxml` (`colSaleId`, the first column): please keep it. The Orders screen now reads the database (`OrdersController.loadOrders`) and the Inventory list shows its ID column; Orders Delete/Edit/Add still change the screen only, so please do not change that code without telling me. Please pull `submain` before editing `OrdersController`, `InventoryController` or `DashboardController` (the weekly chart's bar sizes changed).

### K.4 Evidence labels
`[VERIFIED-USER]` pasted output or screenshot · `[VERIFIED-GIT]` read from the remote by an agent (pushed commits only) · `[VERIFIED-BUILD]` compiled or run by an agent (state the JDK) · `[USER-REPORTED]` said by the user, no output shown · `[UNVERIFIED]` cannot be proven from the sandbox · `[DECISION]` agreed · `[RECOMMENDED]` advice, accepted or not yet carried out (say which).

### K.5 Where the old detail lives (archive v13, not in the repo)
Merge guide (Appendix E), the reviewer's fix list (Appendix F), exact pre-merge file listings (Appendix B), seed data from the C system (Appendix A; also `database/seed.sql`), decision log L1–L37, defects D1–D46, session logs. **The agent that wrote v15 had no access to v13.** Ask the user to upload v13 (or one section of it) **only** when a task needs it.

### K.6 Companion file
`BANNER_COMMENTS.txt` (22 ready-to-paste banners and 2 comment replacements, anchors verified on `ed1471d`). It was a one-time instruction sheet, not part of the repo. **Applied and committed as `7d046f8` (2026-11-25)**; it can be deleted.

---

## L. CHANGELOG (newest first; compress rows older than 5 versions)
| Version | Date | Change |
|---|---|---|
| **17** | 2026-11-26 | State `a619441` (+ two unverified local edits). Recorded: the order-side backend and its verified output (`c975173`, `a950627`, `a619441`: `Order`, `OrderDAO` list/completed/revenue/status summary, Orders screen read; the 4 TEST orders); visible ID columns (pushed inside `c975173`, C50, D68); the user's decisions (ids permanent + soft delete L53, Edit must be wired L54, Inventory sorted by ID L55, Dashboard to be finished after his tasks, reminder requested); the Dashboard chart overflow diagnosed and fixed locally (L56, D64); the teammate's 3 new commits on `tempniDejjo` read and trial-merged (one conflict, `STANDSALE.fxml`); decisions L53-L57, corrections C49-C50, defects D64-D68 (D39 partly resolved, D54/D55 updated), questions Q7 answered, Q11 closed, Q12-Q13 opened; A.2 step 8 (stage only this file when code edits are uncommitted); next batch T13-T15, T11 and the Dashboard wiring queued in F.4. |
| 16 | 2026-11-26 | State `746adb3`. Recorded: Step B `6515631`, banner comments `7d046f8` and v15 `00901ff` pushed; **Delete sale** (`deleteSale` DAO + controller + `"SaleId"` row key) `746adb3`, tested (throwaway sale; accidental Delete of the Leaf test sale doubled as a test); test data gone; the GitHub push rejection and its handling (C.4, C48, D63); the Edit dialog's price rule read from the code; the user's own task-list reading (only the stand-sale table is his accomplished item); Edit sale added to the to-do (T11, recommendation updated to B with A as the fallback); decisions L51–L52, rule C.1 #16, correction C48, defect D63; Q9 and Q10 closed; D54 half resolved, D58 resolved; next batch T10–T12. |
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
- **2026-11-26 (second session)** The order-side backend was built and verified (`Order`, `OrderDAO`, four TEST orders, revenue 330.00), the Orders screen reads the database, the ID columns became visible (`c975173` to `a619441`); the user decided that ids are permanent (soft delete for products) and that Edit sale must work; the Dashboard chart overflow was diagnosed (D64); the teammate pushed three new commits (`931c5ad`); this file became v17 (section 0, L row 17).
- **2026-11-25/26** Step B, banner comments and v15 pushed; **Delete sale** built, tested and pushed (`746adb3`); a GitHub 500 on the first push was handled by retrying (C48); test data cleaned; Edit sale and the order-side queries are next (section 0, T10–T12, L row 16).
