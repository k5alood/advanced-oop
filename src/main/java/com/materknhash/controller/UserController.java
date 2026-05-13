package com.materknhash.controller;

import com.materknhash.dao.UserDAO;
import com.materknhash.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserController {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, User.UserRole> colRole;
    @FXML
    private ComboBox<User.UserRole> roleFilter;

    @FXML private TableColumn<User, Void> colActions;

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
        
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑");
            {
                deleteBtn.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if (userDAO.delete(u.getId())) loadUsers();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        userTable.setItems(userList);
    }

    @FXML
    private void handleAddUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        ComboBox<User.UserRole> role = new ComboBox<>(FXCollections.observableArrayList(User.UserRole.values()));
        role.setValue(User.UserRole.EMPLOYEE);

        grid.add(new Label("Username:"), 0, 0); grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(password, 1, 1);
        grid.add(new Label("Role:"), 0, 2); grid.add(role, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                User u = new User();
                u.setUsername(username.getText());
                u.setPassword(password.getText());
                u.setRole(role.getValue());
                return u;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            if (userDAO.add(user)) loadUsers();
        });
    }

    private void loadUsers() {
        userList.setAll(userDAO.getAll());
    }
}
