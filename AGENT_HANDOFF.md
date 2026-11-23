# AGENT_HANDOFF.md — Sassy's Creation (Java GUI) — living handoff

**Handoff version: 14**
**Last updated:** 2026-11-22 (Asia/Manila) — written by a Claude chat session
**State described:** `origin/submain` = `c3ad3b8` (+ this file, once the user commits it)
**Replaces:** `SASSYS_CREATION_JAVA_GUI_HANDOFF_v13.md` as the day-to-day state. v13 (about 356 KB) is the full archive and is kept **outside** the repo by the user.

> One file, edited in place, version number goes up every session. Do **not** create a second handoff file.

---

## A. HOW TO USE THIS FILE (every agent, every session)

**Token rule: do not read the whole file by default.** Read A, B, C, E, F, G in that order (about 5 minutes). Sections D, H–L are reference: open them only when the task needs them.

### A.1 Start of session
1. Read A, B, C, E, F, G.
2. **Verify before trusting.** If you can run git: `git fetch origin`, then compare `git log -1 origin/submain` with "State described" above and read any newer commits first (the GUI teammate pushes often). If you cannot run git, ask the user for these four outputs and nothing else: `git status`, `git log --oneline -5`, `git fetch origin`, `git log --oneline -3 origin/tempniDejjo`.
3. Tell the user in 5 lines or fewer: what is verified, what is unverified, what comes next. **Do not ask for anything already written here.**
4. Check the available skills before starting (rule C.1 #9).

### A.2 End of session (only if something changed)
1. Raise the version (N → N+1) and the "Last updated" line at the top.
2. Add **one row** to the Changelog (section L): what changed and the evidence.
3. Edit **only the sections that changed**: E (state), F (tasks), H (decisions), I (corrections), J (defects). Do not rewrite untouched sections.
4. Move finished tasks from F.3 to F.2 (one line each, with an evidence tag and the commit id).
5. Write the **next batch of at most 3 tasks** in F.3. Every task must follow B (goal), C (rules) and D (structure and style).
6. **Size budget: 450 lines.** If the file is over budget, compress the oldest Completed/Changelog rows to one line each. **Never delete a decision, a correction or an open defect**: mark it "superseded" instead.
7. **Agents that cannot write to the repo** (chat agents): output only the changed sections as a **HANDOFF PATCH** (section letter plus the full new text of that section) and tell the user which sections to paste over. **Agents with file tools:** use small edits in place.
8. The **user** commits (agents cannot push). Give the commands (PowerShell, one per line):
   ```powershell
   git status
   git add .
   git commit -m "Update AGENT_HANDOFF to v<N>"
   git push
   ```
   `git add .` is acceptable only after the user has read `git status` (rule C.4).
9. **Never put secrets, e-mail addresses, passwords or personal data in this file.** The repository may be public (open question Q3).

---

## B. PROJECT AND GOAL (do not change without the user's decision)

- **Project:** "Sassy's Creation: A Java GUI-Based Sales, Order, and Inventory Management System with MySQL Database" (approved title; GUI Application; Section 2C). It is the group's original **C-language capstone rebuilt in Java**. It is **not** a new project: do not start over.
- **Stack:** Java 25, JavaFX 25.0.5, Maven, IntelliJ IDEA, MariaDB 10.4.32 via XAMPP (MySQL-compatible; kept for the "MySQL Database" title), MariaDB Connector/J 3.5.7. `pom.xml` is leader-edited: do not change it.
- **Goals (in priority order):**
  1. A working JavaFX desktop app: login, dashboard, orders, stand sales, inventory, backed by MariaDB (`sassys_creation_db`).
  2. **The user can explain, modify, troubleshoot and defend every line** at the oral defense. He said on 2026-11-20 that the defense is "next week"; which defense and what it must show is unknown (Q1).
  3. The project feeds a mini-thesis and defense (ERD, test plan, likely defense questions). **No invented citations or results.**
  4. Keep the pink/crimson visual identity (the GUI teammate's Design 2.0 is the design on `submain`).
- **Team (3):**
  - **The user** = group leader and the **only backend member**. His name in the team task list is **"Raven"**.
  - **The GUI teammate** (Git author **"Dejjo"**) works on branch `tempniDejjo`.
  - The user's task list also names **"Ravs"** (GUI items) and **"Ange"** (papers). Whether Dejjo = Ravs is **not confirmed by the user**: do not assume it, and do not assume anyone's gender.
- **Language:** English only for all guidance (not Tagalog/Filipino).

---

## C. RULES (binding for every agent)

### C.1 How the AI interacts with the user
1. **Thinking partner, not a yes-machine and not a code dumper.** Both sides propose and challenge; neither auto-agrees nor auto-rejects; options are judged by reasoning, constraints and trade-offs. If an earlier reply of yours was wrong, **say so directly, correct it and explain what changes** (and log it in section I).
2. **Meaningful design decisions** use: Goal / Options / Trade-offs / Recommendation / What to understand first / How to verify. Skip it for tiny actions. When disagreeing: say so plainly, name the specific problem, give an alternative, let the user respond. If the user overrides and it is safe and feasible, implement it and note the trade-off; if it would materially break the project, say so.
3. **Teach.** The user is a beginner in many areas. Teach from the basics, define terms and abbreviations on first use, say what a change does, **why it belongs in that file** and how to verify it. Prefer small steps he can type and understand over dumping a whole app. Use examples from his actual project.
4. **Evidence.** Inspect the actual files, screenshots or state **before** editing or advising. Never assume a file, dependency or setting exists. **Never say a step worked without shown or reported evidence** (build, run, SQL, commit, push, merge, test). Distinguish compile error / IDE warning / runtime warning / runtime exception. Give exact UI directions (panel, menu, file, what should visibly change) based on his screenshots.
5. **Change control.** One small reversible change at a time, with the expected result; wait for his result or screenshot before stacking unrelated changes. No overwriting a whole file/POM/class for one setting. No duplicate dependencies or classes. **Ask before** adding a library, framework or pattern, or widening scope. Verify a dependency version resolves before recommending it. Build **one vertical slice** (a real end-to-end feature) at a time, not many screens with fake data.
6. **Honesty and records.** Do not invent features, requirements, business rules, citations or test results. Separate verified facts from assumptions and plans. Keep this file's decisions, corrections and defects up to date.
7. **The user types the code.** The agent may generate code, explain and check it, but must not silently write the user's project files, create ZIPs or replace files wholesale. (This file is the one exception, at his request.)
8. **Risky operations** (Git history, merges, deleting files, SQL that drops data): think through the consequences first and **say what NOT to do before** the recommended step.
9. **Skills:** check the available skills before starting a task and use the appropriate ones (they can be combined).
10. **Reply style:** practical, direct, honest, thorough. Do not end a reply by merely offering to do work: **make the next actionable step explicit.** Do not call the user by name unless he asks. He often communicates through screenshots. Answer long pasted output by naming the few lines that matter, not by restating it.
11. **Tasks are delivered 3 at a time** (user's request, 2026-11-22). Each task states: what, why, **exact file path**, code (if any, with what to replace or keep), expected result, how to verify, what NOT to do.
12. **Commands must be copy-safe.** The terminal is **Windows PowerShell**: it rejects `&&` (use separate lines or `;`), paths are Windows paths, and commands are given **without** the `PS C:\...>` prompt ("paste only the command"). Say what output to expect.
13. **Gate before irreversible steps.** Do not tell the user to commit, merge or push a slice until the checks you listed have been shown (decision L34).
14. If the user uploads a handoff or master-prompt `.md`, read it fully and continue from its stated state without re-asking details already written there.

### C.2 Architecture rules
- Layers: `controllers` (screens) → `dao` (SQL only) → `db` (connection); `model` holds data classes. **No SQL in controllers or FXML; no business rules in event handlers.** A service layer is planned only when stock rules need transactions across DAOs (ask first).
- Money is `BigDecimal` in Java and `DECIMAL(10,2)` in SQL. Dates are `LocalDate` / `DATE`.
- Database constraints back up the Java validation (`CHECK`, `FOREIGN KEY ... ON DELETE RESTRICT`).
- `database/schema.sql` **drops tables** (`DROP TABLE IF EXISTS`): **never run it, `seed.sql` or `add_image_path.sql` to "test" something.** Changes to a live database are done with additive SQL (for example only the `CREATE TABLE` block).
- Do not edit the GUI teammate's FXML or styles without asking. No new dependencies; do not touch `pom.xml`.

### C.3 Coding defaults
Parameterized SQL (never concatenate input); try-with-resources for connections; validate in the right layer plus database constraints; keep secrets out of source control; prefer simple code the user can defend over clever abstractions. Avoid features the course has not taught (streams, `super`, etc.) unless there is a reason, and **ask before** using one.

### C.4 Git rules
- **One short-lived local slice branch per slice** → check → `git switch submain` → `git merge --ff-only <slice>` → push.
- Integration/backup branches stay **local** and are never pushed. Never push `main` (it is still the initial commit). Never push or merge into the teammate's branches (`GUI`, `tempniDejjo`).
- Before merging a teammate branch: `git fetch origin`, then a **trial merge** (`git merge-tree --write-tree --name-only origin/submain origin/<branch>`), then a throwaway local branch. After every merge check `git diff --stat -- pom.xml` (it can silently take the teammate's older versions).
- `git add .` is fine **after** reading `git status` (no stray files such as `target/`, `.idea/`, `product-images/`).

---

## D. STRUCTURE AND CODE STYLE (verified on `c3ad3b8`)

```
src/main/java/com/sassyscreation/sassyscreationgui/
  Launcher.java, SassyCreation.java        entry points (mainClass = Launcher)
  ProductDAOTest.java, StandSaleDAOTest.java   manual test runners (main + println), root package
  db/DBConnection.java                     getConnection(); hardcoded local dev connection
  model/   Product, StandSale              immutable data classes
  dao/     ProductDAO, StandSaleDAO        SQL only, one DAO per table
  controllers/  22 files                   screen controllers + helpers (plural "controllers" is the teammate's convention)
src/main/resources/com/sassyscreation/sassyscreationgui/
  31 flat FXML files, styles.css, text-large.css, images/, fonts, drawables
database/  schema.sql (DESTRUCTIVE), seed.sql (44 products), test-seed.sql, add_image_path.sql
module-info.java: exports root + controllers, opens controllers to javafx.fxml (dao/model stay internal;
  no PropertyValueFactory in src/main, so no `opens ...model` is needed today)
```

**Style samples to copy (from `ProductDAO`, `Product`, `StandSale`):**
- 4-space indent, K&R braces, one-line getters, `this.` in constructors, `toString()` on models.
- Model classes: `private final` fields, one constructor, getters, no setters; derived values (for example `getTotal()`) are methods, not columns. One-line `/** ... */` for non-obvious fields.
- DAO methods: `public List<X> getAllX() throws SQLException`, `insertX(...)`, `deleteById(int id)`; SQL in a `String` split with `+` (trailing space before each line break); `PreparedStatement` with `?`; `try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = ...; ResultSet rs = ...)`.
- SQL names: `snake_case`; constraints named `chk_*` and `fk_*`.
- Test runner: `XxxDAOTest.java` in the root package, `main` prints results and catches `SQLException`.
- Do not create a second class with a name that already exists (`Product` is already duplicated, see D49).

---

## E. CURRENT STATE (as of 2026-11-22)

### E.1 Repository
| Branch | Head | Notes |
|---|---|---|
| `main` | `300d9aa` | Initial commit, untouched. |
| **`submain`** | **`c3ad3b8`** | Integration branch. Contains the teammate's `tempniDejjo` up to `e09e4e7` (merged 2026-11-21, `427312f`) **plus** the `stand_sales` slice (`c3ad3b8`, pushed by the user 2026-11-22). |
| `tempniDejjo` | `dfb656b` | Teammate's working branch. **1 commit not in `submain`:** "fix dash board stansale 50%" (changes `DashboardController` about 296 lines, `StandSaleController` about 78 lines with Qty/Price filter dropdowns, `STANDSALE.fxml`, `DASHBOARD.fxml`, `SidebarController`, `InventoryController`, one image). Still tracks **183 `target/` files**; its `pom.xml` has JavaFX 25.0.2 / MariaDB 3.5.2. |
| `GUI` `2860443`, `JAVA+DB` `567a3de`, `inventory-read` `3f84784` | | Old/redundant (ancestors of `submain`). |

**Trial merge `tempniDejjo` → `submain` (2026-11-22, `git merge-tree`, changed nothing) [VERIFIED-GIT]:** 15 conflicts, **all `target/classes/...` modify/delete** (build output that `submain` deleted). **No source-file conflicts were reported**, and `pom.xml` was not in the conflict list (the teammate did not change it since `e09e4e7`; confirm with `git diff --stat -- pom.xml` after a real merge).

### E.2 Database `sassys_creation_db` (XAMPP MariaDB 10.4.32)
- **`products`**: `product_id`, `name`, `category ENUM('Bouquet','Crochet','Keychain')`, `price`, `stock_quantity`, `low_stock_threshold` (default 3), `is_customizable`, `image_path` (nullable); `chk_price`, `chk_stock`. 44 seeded products.
- **`orders`**: `order_id`, `customer_name`, `product_id` (FK, RESTRICT), `color`, `custom_description`, `quantity` (>0), `unit_price` (>=0), `arrangement ENUM('Pickup','Meetup')`, `schedule_date`, `status ENUM('Pending','Done','Cancelled')`. **No completed-date or total column.**
- **`stand_sales`** (new): `sale_id` AUTO_INCREMENT PK, `product_id` (FK, RESTRICT), `quantity` (>0), `unit_price` (>=0, the price **at the time of sale**), `sale_date` DATE. Total = `quantity * unit_price`, not stored.
- **No `users` table.**
- The `image_path` column was added on the user's database with an additive `ALTER`; the `UPDATE` mappings in `add_image_path.sql` were **not** run and many are guesses.
- A teammate's database gets `stand_sales` by running **only the `CREATE TABLE stand_sales (...)` block** from `schema.sql` in phpMyAdmin, never the whole file. (An additive `database/stand_sales.sql` does not exist yet.)

### E.3 What is real and what is sample (read from the code)
- **Database-backed:** Inventory read (`ProductDAO.getAllProducts`) and Add product (`insertProduct`). `StandSaleDAO.getAllSales()` exists but **no screen uses it yet**.
- **Memory only:** Inventory Edit / Restock / Delete (TODO in the code). Changes vanish on reload, including right after a product is added.
- **Sample data:** Dashboard (hardcoded; comment "TEMPORARY sample data", also in `dfb656b`), Orders (6 sample rows), Stand Sale (its own 5 sample products and 9 sample sales, private nested `Product` and `Sale` classes), AddOrder (3 sample products), Login (hardcoded demo credentials in `LoginController`), Forgot password (hardcoded answer), Change password (accepts any current password, saves nothing).
- Dashboard cards on `submain`: "Revenue" (₱1,210) equals the stand-sale sample total and "Pending" (₱1,560) equals the pending-order sample total, so order revenue has no card yet. `dfb656b` rewrites the Dashboard: **re-read it before assigning Dashboard work.**

### E.4 Verified (with evidence)
- Pushed `c3ad3b8` (schema.sql +13 lines: DROP + CREATE; `StandSale.java` 49 lines; `StandSaleDAO.java` 41; `StandSaleDAOTest.java` 19). **[VERIFIED-USER]** push output and **[VERIFIED-GIT]** files read from the remote and equal to what was handed out.
- `stand_sales` created in the user's database; valid insert accepted, `quantity = 0` and unknown `product_id` rejected **[USER-REPORTED]**.
- `StandSale`, `StandSaleDAO`, `StandSaleDAOTest` compile with `javac` on JDK 21 **[VERIFIED-BUILD, caveat: the user's JDK is 25]**.

### E.5 Not verified / unknown
`StandSaleDAOTest` run output (**not shown**); the merged app's run evidence (rebuild, 44-product Inventory, Spinner test) is **still owed** (D42); IntelliJ build on Java 25 of the new files; which defense is "next week" (Q1); who owns which files (Q2).

---

## F. TASKS

### F.1 The team's task list (from the user, 2026-11-22) and what the code says
| Owner | Item (as written) | Status / notes |
|---|---|---|
| Raven | id in inventory and stand sale | **Partial.** `stand_sales.sale_id` exists in the DB and the model. The Inventory table already has an ID column but it is hidden (`visible="false"`); the Stand Sale table has no ID column yet. |
| Raven | table for stand sale records only | **Done at database level** (`stand_sales`). The screen table already shows only stand sales, but it still uses sample data. |
| Raven | table for completed orders and revenue | **Decided: a query/view on `orders`** (L38): `status = 'Done'`, revenue = `SUM(quantity * unit_price)`. To do. |
| Raven | table summary done / pending / cancelled | **Decided: a query** (L38): `COUNT(*) ... GROUP BY status`. Feeds the Dashboard gauge and the Orders chips. To do. |
| Ravs | product dropdown | Which dropdown is **unknown**: Stand Sale (`cbSaleProduct`) and AddOrder (`cbOrderProduct`) both use sample products. Needs `ProductDAO.getAllProducts()`. Ask. |
| Ravs | change password | Needs a `users` table first (Raven's side). Today it accepts any current password. |
| Ravs | standsale button arrow date | **Unclear from the code.** The Record form has no date control; the only `DatePicker` is in Edit Sale. Ask. |
| Ravs | standsale newest / oldest | Default sort is already newest first; no toggle. `StandSaleDAO.getAllSales()` sorts `sale_date DESC, sale_id DESC`. |
| Ravs | green stock inventory | In the Inventory **list** view the normal-stock badge uses `stock-plain`; the grid view uses green `badge-done` (`stockBadge(p, "stock-plain")` near the list column setup). Probably a one-word change; **untested**. |
| Ravs | order revenue on the dashboard; revenue-by-day chart (orders + stand sales); month filter | Depend on Raven's queries (L38, L39). **`dfb656b` already draws two-tier bars (stand vs order) on sample data: check it first.** |
| Ange | papers | Needs the final ERD after `users` and any later table changes. |

### F.2 Completed (one line each)
- C-to-Java cleanup, fonts, entry point `Launcher` (v13 §3). **[VERIFIED-USER]**
- Teammate's `tempniDejjo` (to `e09e4e7`) merged into `submain`, `pom.xml`/`.gitignore` kept, `target/` and `.idea` untracked, `image_path` added: `427312f`. **[VERIFIED-USER]**
- Inventory read and Add product work in the merged app. **[USER-REPORTED]**
- 2026-11-22: team task list analysed against the code; decisions L38–L40 made.
- 2026-11-22: `stand_sales` table, `StandSale` model, `StandSaleDAO.getAllSales()`, `StandSaleDAOTest`: `c3ad3b8`. **[VERIFIED-USER push; run output not shown]**

### F.3 NEXT BATCH (Batch 3, for Raven) — 3 tasks
1. **T5 — Evidence gate (no code).**
   (a) Run `StandSaleDAOTest` (insert two test rows first, as in the earlier steps, then `DELETE FROM stand_sales;`) and paste the Run console. Expected: two `StandSale{...}` lines, newest date first.
   (b) Paste `git fetch origin` and `git log --oneline -3 origin/tempniDejjo`. Expected top line: `dfb656b`, or something newer.
   (c) Answer Q1 and Q2 (section G).
2. **T6 — Decide how `dfb656b` reaches `submain` before anyone edits Stand Sale or Dashboard.** The teammate is changing `StandSaleController` and `DashboardController` right now, the same files the next slice wires to the database. Options: (1) the user merges `tempniDejjo` into `submain` with the trial-merge recipe (expected: only `target/` conflicts, resolved by keeping them deleted); (2) the teammate merges `submain` into `tempniDejjo` and stops tracking `target/`, then the user fast-forwards. Present the Goal/Options/Trade-offs/Recommendation table to the user; draft the neutral message to the teammate from section K. **Do not edit any teammate-owned screen before this is settled.**
3. **T7 — Backend slice `StandSaleDAO.insertSale(...)` (no GUI changes).** One transaction on one connection: `setAutoCommit(false)`; `UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?`; if 0 rows were updated → rollback and report "not enough stock"; else `INSERT INTO stand_sales ...`; commit; on any `SQLException` → rollback. Test in `StandSaleDAOTest`: success (stock goes down, a row exists), insufficient stock (nothing changes), and a rollback check. Work on a slice branch (`standsale-insert`), commit only after the evidence is shown (L34). Teach: what a transaction is, `commit` / `rollback`, why the stock update has a guard.

### F.4 Queue after Batch 3 (order follows the team list and the defense; revise after Q1)
- **Batch 4:** wire Stand Sale to the DAO (replace the nested `Product`/`Sale` with `model` classes, add the ID column, record = `insertSale`); `deleteSale` / `updateSale` with stock restored. (Ravs: newest/oldest toggle, arrow-date item once clarified.)
- **Batch 5:** `OrderDAO` read + the revenue and status-summary queries (L38, L39) → real Dashboard numbers; then Ravs' order-revenue card, two-series chart and month filter.
- **Batch 6:** `users` table + Login + Change password (JDK `PBKDF2`, **no new library without asking**).
- **Still open from v13:** W2 product **Delete → database** (D34, recommended by L33; the user followed the team list instead on 2026-11-22), W3 Edit/Restock → DB, W4–W6 Orders frames and orders to the DB, one shared `Product` class (D49).

---

## G. OPEN QUESTIONS FOR THE USER (ask only what is still open)
- **Q1. Which defense is "next week" and what must it show?** (date, screens, documents, is code inspected?) It sets every priority.
- **Q2. Who edits what from now on?** `StandSaleController`, `DashboardController`, `InventoryController`, `ProductDAO`, `Product`: the user or the GUI teammate? Is Dejjo the same person as "Ravs"?
- **Q3. Is the repository meant to be public?** (This file avoids secrets either way.)
- **Q4. `product-images/` policy.** `.gitignore` does **not** list it yet; what does the app store in `image_path`? (D36)
- **Q5. Ravs' unclear items:** which product dropdown, and what is "standsale button arrow date"?
- **Q6. When a product is deleted, keep its photo file?** (Recommended: yes, keep it.)

---

## H. DECISIONS (binding; the full v13 list L1–L37 is in the archive)

**Still binding from v13:** L1 keep the teammate's plural `controllers/` · L2 FXML stays flat · L3 `mainClass` = `Launcher` · L6 do not touch Spinner `min/max/initialValue` in FXML · L7 keep `exports ...sassyscreationgui;` · L11 `BigDecimal`/`DECIMAL`, stock rules later in a service with transactions · L16 never run `schema.sql`/`seed.sql` to test a connection · L23 delete no FXML before the defense · L25/L29–L31 no blind merges; `pom.xml` and `.gitignore` = ours; additive SQL only · L32 integration branches stay local · L34 gate rule · L35 a guided read-through of the teammate-written code precedes more wiring (still recommended; D45).

**Domain rules:** 44-product seed; `is_customizable` TRUE only for original ids 35, 42, 43, 44; categories only Bouquet/Crochet/Keychain; deleting a product that orders or sales reference is **blocked** (FK `RESTRICT`) with a friendly message; customizable orders may have quantity > 1 with an agreed per-unit price and **no** stock deduction; regular orders are 1 unit and deduct 1; **low stock when `stock_quantity <= low_stock_threshold`** (default 3).

**New (2026-11-22):**
| # | Decision | Reason / alternative / defense line |
|---|---|---|
| L38 | Completed orders + revenue and the done/pending/cancelled summary are **queries or views on `orders`**, not separate tables. | One source of truth, nothing to keep in sync. Alternative: separate tables (duplicated data). Defense: "derived data is not stored twice (normalization)." |
| L39 | Order revenue by day/month uses **`orders.schedule_date`**; no `completed_at` column for now. | No schema change near the defense. Limitation to state in the paper: a Done order counts on its scheduled date, not the day it was marked Done. Alternative: add `completed_at`. |
| L40 | `stand_sales` design: price stored at sale time, **no total column**, FK `RESTRICT`, `CHECK (quantity > 0)` and `CHECK (unit_price >= 0)`, `sale_id` ordering as the tiebreaker, newest first. `StandSale` carries `productName` from a JOIN. | Mirrors `orders`. The `productName` choice was left open for the user to challenge (alternative: model with table columns only plus a name lookup). |
| L41 | This file is the **compact living handoff** in the repo root, edited in place each session; v13 stays outside the repo as the archive. | The user asked for fewer tokens. Deviates from v13's L37 (append-only): the risk of losing detail (v13 C39) is covered by keeping v13 and by never deleting decisions, corrections or open defects. |
| L42 | Tasks are delivered 3 at a time, each followed by evidence. | User request. |

---

## I. CORRECTIONS (agent errors; newest last; v13's C1–C40 are in the archive)
| # | What was said | Correction |
|---|---|---|
| C29 | (v13) "her branch / she" for the GUI teammate. | Never name or gender the teammate; say "the GUI teammate (Git author Dejjo)". |
| C41 | An agent said `TRUNCATE TABLE stand_sales` was safe because nothing references the table. | The user got an error in phpMyAdmin (the message was not shown) and resolved it himself; the cause is unknown. Use `DELETE FROM stand_sales;` for cleanup (optionally `ALTER TABLE stand_sales AUTO_INCREMENT = 1;` for cosmetic ids). Never untick "Enable foreign key checks" to force a statement through. |
| C42 | An agent told the user to commit and push the `stand_sales` slice straight to `submain`, and the push happened before the DAO test output was shown. | This bypassed the slice-branch pattern (C.4) and the gate (L34). It was low risk only because the change was additive. Future slices use a branch and show evidence first. |

---

## J. OPEN DEFECTS AND RISKS (still-open v13 items condensed; new ones from D47)
| ID | Defect / risk | Status |
|---|---|---|
| D34 | Inventory **Delete / Edit / Restock change memory only** (and are wiped on reload). | Open (W2/W3). |
| D35 | Teammate's Inventory code swallows exceptions (`catch (Exception e) { e.printStackTrace(); }`): a DB failure shows an empty screen with no message. | Open (fix inside W2). |
| D36 | `product-images/` is created under the project folder on Add product; `.gitignore` does not list it; the stored `image_path` format is unknown. | Open (Q4). |
| D39 | The teammate's branches still track `target/` and `.idea` files and carry a UTF-16 `.gitignore`. | Open (T6). |
| D40 | `pom.xml` can silently take the teammate's older versions in a merge. | Open: check after every merge. |
| D41 | `add_image_path.sql` mappings are mostly guesses (for example Mini Teddy Bear Keychain → `coffee-bouquet.jpg`). | Open. |
| D42 | The merged app has no shown run evidence (rebuild, 44-product Inventory, Spinner test). | **Open, owed.** |
| D43 | `InventoryController` calls a static DAO and keeps a nested `Product`. | Open. |
| D45 | The user owns about 700 lines of teammate-written `InventoryController` plus other teammate code he must be able to explain. | Open: guided read-through (L35). |
| **D47** | The teammate's `dfb656b` edits `StandSaleController` and `DashboardController` (the files the next slice wires). Conflict risk. | **Open (T6).** |
| **D48** | `StandSaleDAOTest` run output was not shown. | Open (T5). |
| **D49** | **Four classes named `Product`** (`model.Product` plus private copies in the Inventory, AddOrder and StandSale controllers) and a private `Sale` in `StandSaleController`. Likely defense question. | Open (Batch 4+). |
| **D50** | Low-stock rule mismatch: Inventory hardcodes `LOW_STOCK = 5`; the database default is 3 and the agreed rule is `<= low_stock_threshold`. | Open. |
| **D51** | Dashboard "Revenue" card shows stand-sale revenue only; order revenue is missing. | Open (Batch 5). |
| **D52** | Login is hardcoded and Change password accepts any current password. | Open (Batch 6). |
| **D53** | Dashboard chart on `submain` used whole-peso integers (`Map<String,Integer>`), which would drop centavos; re-check on `dfb656b`. | Open. |

---

## K. REFERENCE (open on demand)

### K.1 Read-only commands in an agent sandbox (works while the repo is public)
```bash
git clone --branch submain --single-branch https://github.com/Utotz/Sassy-s-Creation-GUI.git sassy
git remote set-branches --add origin tempniDejjo        # a single-branch clone knows one branch only
git fetch origin
git ls-remote --heads origin
git rev-list --left-right --count origin/submain...origin/tempniDejjo
git merge-tree --write-tree --name-only origin/submain origin/tempniDejjo     # trial merge, changes nothing
```
Agents see **pushed commits only** and cannot push. `web_fetch` of GitHub pages is blocked; use `git`.

### K.2 User commands (PowerShell, one per line, no prompt)
```powershell
git status
git branch -vv
git fetch origin
git log --oneline -5
git log --oneline origin/tempniDejjo -3
git diff --stat -- pom.xml
```
SQL sanity checks (phpMyAdmin SQL tab, read-only):
```sql
SELECT COUNT(*) FROM products;       -- expect 44
SELECT COUNT(*) FROM stand_sales;    -- expect 0 after test cleanup
```

### K.3 Draft message to the GUI teammate (the user relays it; neutral wording)
> Quick sync on `submain`. I added the `stand_sales` table, a `StandSale` model and `StandSaleDAO` (read). I saw your newest push on `tempniDejjo` (Stand Sale filters and Dashboard). To avoid conflicts: (1) who edits `StandSaleController` and `DashboardController` from now on? I will wire them to the database next, so please tell me before you change them again. (2) Your branch still tracks `target/` files and uses a UTF-16 `.gitignore`; can you stop tracking `target/`? (3) Please keep `pom.xml` at JavaFX 25.0.5 / MariaDB 3.5.7. (4) Everyone must create the `stand_sales` table by running only its `CREATE TABLE` block from `database/schema.sql`, not the whole file.

### K.4 Evidence labels
`[VERIFIED-USER]` pasted output or screenshot · `[VERIFIED-GIT]` read from the remote by an agent (pushed commits only) · `[VERIFIED-BUILD]` compiled or run by an agent (state the JDK) · `[USER-REPORTED]` said by the user, no output shown · `[UNVERIFIED]` cannot be proven from the sandbox · `[DECISION]` agreed · `[RECOMMENDED]` advice not yet carried out.

### K.5 Where the old detail lives (archive v13, not in the repo)
Merge guide (Appendix E), the reviewer's fix list (Appendix F), exact pre-merge file listings (Appendix B), seed data from the C system (Appendix A; also `database/seed.sql`), decision log L1–L37, defects D1–D46, session logs. Ask the user to upload v13 (or one section of it) **only** when a task needs it.

---

## L. CHANGELOG (newest first; compress rows older than 5 versions)
| Version | Date | Change |
|---|---|---|
| **14** | 2026-11-22 | Replaced v13 as the working handoff (compact, in the repo). Added: team task list mapped to the code (F.1), `stand_sales` slice (`c3ad3b8`), teammate's `dfb656b` and the trial merge result, decisions L38–L42, corrections C41–C42, defects D47–D53, next batch T5–T7. |
| 13 | 2026-11-22 | Full-detail edition (about 356 KB), state at `427312f`. Kept as an archive outside the repo. |
