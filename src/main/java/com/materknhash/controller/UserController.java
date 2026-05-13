package com.materknhash.controller;

import com.materknhash.dao.UserDAO;
import com.materknhash.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, User.UserRole> colRole;
    @FXML private ComboBox<User.UserRole> roleFilter;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadUsers();
        roleFilter.setItems(FXCollections.observableArrayList(User.UserRole.values()));
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        userTable.setItems(userList);
    }

    private void loadUsers() {
        userList.setAll(userDAO.getAll());
    }
}
