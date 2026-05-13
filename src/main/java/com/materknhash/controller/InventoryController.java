package com.materknhash.controller;

import com.materknhash.dao.ProductDAO;
import com.materknhash.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.Optional;

/**
 * Controller for the Inventory Management screen.
 * Handles CRUD operations and real-time searching.
 */
public class InventoryController {

    @FXML
    private TableView<Product> inventoryTable;
    @FXML
    private TableColumn<Product, Integer> colId;
    @FXML
    private TableColumn<Product, String> colCode;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, String> colCategory;
    @FXML
    private TableColumn<Product, Integer> colQuantity;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, String> colSupplier;
    @FXML
    private TableColumn<Product, Void> colActions;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalItemsLabel;
    @FXML
    private Label lowStockLabel;

    private final ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();
        setupSearch();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("partNumber"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        // Format Price to EGP
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(tc -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f EGP", price));
                }
            }
        });

        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        // Custom Actions Column (Edit/Delete buttons)
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");
            private final HBox container = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("action-btn-edit");
                deleteBtn.getStyleClass().add("action-btn-delete");
                editBtn.setOnAction(event -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void loadData() {
        productList.setAll(productDAO.getAll());
        inventoryTable.setItems(productList);
        updateSummary();
    }

    private void setupSearch() {
        FilteredList<Product> filteredData = new FilteredList<>(productList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty())
                    return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return product.getName().toLowerCase().contains(lowerCaseFilter) ||
                        product.getPartNumber().toLowerCase().contains(lowerCaseFilter) ||
                        product.getCategory().toLowerCase().contains(lowerCaseFilter);
            });
        });
        inventoryTable.setItems(filteredData);
    }

    @FXML
    private void showAddDialog() {
        // In a real app, this would open a new FXML window or a Dialog
        System.out.println("Opening Add New Part Dialog...");
    }

    private void handleEdit(Product product) {
        System.out.println("Editing: " + product.getName());
    }

    private void handleDelete(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete " + product.getName() + "?");
        alert.setContentText("Are you sure? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (productDAO.delete(product.getId())) {
                loadData();
            }
        }
    }

    private void updateSummary() {
        totalItemsLabel.setText("Total Items: " + productList.size());
        long lowStock = productList.stream().filter(p -> p.getQuantity() <= p.getMinStockLevel()).count();
        lowStockLabel.setText("Low Stock Alerts: " + lowStock);
    }
}
