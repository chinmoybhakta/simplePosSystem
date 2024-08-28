module com.example.shopmanagement007 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;

    opens com.example.shopmanagement007.model to javafx.base;
    opens com.example.shopmanagement007 to javafx.fxml;
    exports com.example.shopmanagement007;
}