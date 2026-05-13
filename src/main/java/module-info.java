module com.materknhash {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.materknhash.core to javafx.graphics, javafx.fxml;
    opens com.materknhash.controller to javafx.fxml;
    opens com.materknhash.model to javafx.base;

    exports com.materknhash.core;
    exports com.materknhash.model;
    exports com.materknhash.controller;
    exports com.materknhash.dao;
    exports com.materknhash.service;
}
