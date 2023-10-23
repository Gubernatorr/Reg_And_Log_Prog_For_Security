module com.example.reg_and_log_prog_for_security {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.reg_and_log_prog_for_security to javafx.fxml;
    exports com.example.reg_and_log_prog_for_security;
    exports com.example.reg_and_log_prog_for_security.controllers;
    opens com.example.reg_and_log_prog_for_security.controllers to javafx.fxml;
}