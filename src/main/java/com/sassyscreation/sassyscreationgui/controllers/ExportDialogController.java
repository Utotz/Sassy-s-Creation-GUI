package com.sassyscreation.sassyscreationgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the "Export data" dialog (EXPORTDIALOG.fxml).
 *
 * Usage:
 *   Optional<ExportDialogController.ExportOptions> opt =
 *           ExportDialogController.show(window, "Orders", true);
 *   // build the rows, then:
 *   ExportDialogController.export(window, opt.get(), "orders", headers, rows);
 */
public class ExportDialogController {

    // Must sit in src/main/resources/com/sassyscreation/sassyscreationgui/
    private static final String FXML_PATH =
            "/com/sassyscreation/sassyscreationgui/EXPORTDIALOG.fxml";

    public enum Format { CSV, PDF }

    /** What the user chose. Empty from/to means "no limit". */
    public static class ExportOptions {
        public final Format format;
        public final LocalDate from;
        public final LocalDate to;
        public final boolean includeCancelled;

        ExportOptions(Format format, LocalDate from, LocalDate to, boolean includeCancelled) {
            this.format = format;
            this.from = from;
            this.to = to;
            this.includeCancelled = includeCancelled;
        }

        /** True if the ISO date (yyyy-MM-dd) is inside the chosen range. Unparseable dates are kept. */
        public boolean inRange(String isoDate) {
            try {
                LocalDate d = LocalDate.parse(isoDate);
                return (from == null || !d.isBefore(from)) && (to == null || !d.isAfter(to));
            } catch (Exception e) {
                return true;
            }
        }
    }

    @FXML private Label lblExportTarget;
    @FXML private ToggleGroup tgFormat;
    @FXML private ToggleButton tbCsv;
    @FXML private ToggleButton tbPdf;
    @FXML private DatePicker dpExportFrom;
    @FXML private DatePicker dpExportTo;
    @FXML private CheckBox cbIncludeCancelled;
    @FXML private Button btnCancelExport;
    @FXML private Button btnConfirmExport;

    private ExportOptions result;

    // ---------------------------------------------------------------
    // Opening the dialog
    // ---------------------------------------------------------------

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * @param target              text shown next to "Exporting" (e.g. "Orders")
     * @param showCancelledOption show the "Include cancelled orders" checkbox (orders only)
     * @return the chosen options, or empty if the user cancelled
     */
    public static Optional<ExportOptions> show(Window owner, String target, boolean showCancelledOption) {
        try {
            FXMLLoader loader = new FXMLLoader(ExportDialogController.class.getResource(FXML_PATH));
            Scene scene = new Scene(loader.load());
            ExportDialogController controller = loader.getController();
            controller.setup(target, showCancelledOption);

            Stage stage = new Stage();
            stage.setTitle("Export data");
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();

            return Optional.ofNullable(controller.result);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------

    @FXML
    private void initialize() {
        // Always keep one format selected
        tgFormat.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
            }
        });

        btnCancelExport.setCancelButton(true);
        btnConfirmExport.setDefaultButton(true);
    }

    private void setup(String target, boolean showCancelledOption) {
        lblExportTarget.setText(target == null ? "" : target);
        cbIncludeCancelled.setVisible(showCancelledOption);
        cbIncludeCancelled.setManaged(showCancelledOption);
    }

    // ---------------------------------------------------------------
    // Buttons
    // ---------------------------------------------------------------

    @FXML
    private void handleCancelExport() {
        result = null;
        close();
    }

    @FXML
    private void handleConfirmExport() {
        LocalDate from = dpExportFrom.getValue();
        LocalDate to = dpExportTo.getValue();

        if (from != null && to != null && from.isAfter(to)) {
            warn("The \"From\" date must be on or before the \"To\" date.");
            dpExportFrom.requestFocus();
            return;
        }

        Toggle selected = tgFormat.getSelectedToggle();
        Format format = selected == tbPdf ? Format.PDF : Format.CSV;
        boolean includeCancelled = !cbIncludeCancelled.isVisible() || cbIncludeCancelled.isSelected();

        result = new ExportOptions(format, from, to, includeCancelled);
        close();
    }

    // ---------------------------------------------------------------
    // Writing the file (called by the screen that opened the dialog)
    // ---------------------------------------------------------------

    /**
     * Asks where to save and writes the rows. PDF is not implemented yet.
     *
     * @param baseName default file name without extension (e.g. "orders")
     */
    public static void export(Window owner, ExportOptions options, String baseName,
                              List<String> headers, List<List<String>> rows) {
        if (options.format == Format.PDF) {
            alert(owner, Alert.AlertType.INFORMATION, "PDF export is not available yet. Please choose CSV.");
            return;
        }
        if (rows.isEmpty()) {
            alert(owner, Alert.AlertType.INFORMATION, "There is nothing to export for those filters.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save export");
        chooser.setInitialFileName(baseName + "-" + LocalDate.now() + ".csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
        File file = chooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }

        try (Writer w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            w.write('\uFEFF'); // BOM so Excel shows the peso sign correctly
            w.write(toCsvLine(headers));
            for (List<String> row : rows) {
                w.write(toCsvLine(row));
            }
        } catch (IOException e) {
            e.printStackTrace();
            alert(owner, Alert.AlertType.ERROR, "Could not save the file:\n" + e.getMessage());
            return;
        }
        alert(owner, Alert.AlertType.INFORMATION, "Exported " + rows.size() + " row(s) to:\n" + file.getAbsolutePath());
    }

    private static String toCsvLine(List<String> cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) sb.append(',');
            String c = cells.get(i) == null ? "" : cells.get(i);
            if (c.contains(",") || c.contains("\"") || c.contains("\n")) {
                c = "\"" + c.replace("\"", "\"\"") + "\"";
            }
            sb.append(c);
        }
        return sb.append("\r\n").toString();
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void warn(String msg) {
        alert(btnConfirmExport.getScene().getWindow(), Alert.AlertType.WARNING, msg);
    }

    private static void alert(Window owner, Alert.AlertType type, String msg) {
        Alert a = new Alert(type);
        a.initOwner(owner);
        a.setTitle("Export data");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void close() {
        ((Stage) btnCancelExport.getScene().getWindow()).close();
    }
}