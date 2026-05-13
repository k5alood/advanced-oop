package com.materknhash.controller;

import com.materknhash.dao.SupplierDAO;
import com.materknhash.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * Controller for Supplier Management.
 */
public class SupplierController {

    @FXML private TextField nameField, contactField, phoneField, emailField;
    @FXML private TextArea addressField;
    @FXML private TextField searchField;

    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Integer> colId;
    @FXML private TableColumn<Supplier, String> colName, colContact, colPhone, colEmail;
    @FXML private TableColumn<Supplier, Void> colActions;

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private Supplier selectedSupplier = null;

    @FXML
    public void initialize() {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑");
            private final HBox container = new HBox(deleteBtn);
            {
                deleteBtn.getStyleClass().add("action-btn-delete");
                deleteBtn.setOnAction(e -> {
                    Supplier s = getTableView().getItems().get(getIndex());
                    if (supplierDAO.delete(s.getId())) loadData();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedSupplier = newSelection;
                fillForm(newSelection);
            }
        });
    }

    private void loadData() {
        supplierList.setAll(supplierDAO.getAll());
        FilteredList<Supplier> filteredData = new FilteredList<>(supplierList, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(s -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return s.getName().toLowerCase().contains(lower) || s.getPhone().contains(lower);
            });
        });
        supplierTable.setItems(filteredData);
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        if (name.isEmpty()) return;

        Supplier s = (selectedSupplier != null) ? selectedSupplier : new Supplier();
        s.setName(name);
        s.setContactPerson(contactField.getText());
        s.setPhone(phoneField.getText());
        s.setEmail(emailField.getText());
        s.setAddress(addressField.getText());

        boolean success = (selectedSupplier != null) ? supplierDAO.update(s) : supplierDAO.add(s);
        if (success) {
            handleClear();
            loadData();
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        contactField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        selectedSupplier = null;
    }

    private void fillForm(Supplier s) {
        nameField.setText(s.getName());
        contactField.setText(s.getContactPerson());
        phoneField.setText(s.getPhone());
        emailField.setText(s.getEmail());
        addressField.setText(s.getAddress());
    }
}
