module com.sassyscreation.sassyscreationgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.mariadb.jdbc;

    requires java.prefs;
    opens com.sassyscreation.sassyscreationgui to javafx.fxml;
    exports com.sassyscreation.sassyscreationgui;
    exports com.sassyscreation.sassyscreationgui.controllers;
    opens com.sassyscreation.sassyscreationgui.controllers to javafx.fxml;
}