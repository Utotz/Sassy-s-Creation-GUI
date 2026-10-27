module com.sassyscreation.sassyscreationgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.mariadb.jdbc;

    opens com.sassyscreation.sassyscreationgui to javafx.fxml;
    exports com.sassyscreation.sassyscreationgui;
}