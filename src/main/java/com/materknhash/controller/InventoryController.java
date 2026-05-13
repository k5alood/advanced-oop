package com.materknhash.controller;

import com.materknhash.dao.ProductDAO;
import com.materknhash.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
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
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Spare Part");
        dialog.setHeaderText("Enter details for the new spare part.");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField name = new TextField();
        TextField code = new TextField();
        TextField category = new TextField();
        TextField price = new TextField();
        TextField qty = new TextField();

        grid.add(new Label("Part Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Part Number:"), 0, 1);
        grid.add(code, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(category, 1, 2);
        grid.add(new Label("Price (EGP):"), 0, 3);
        grid.add(price, 1, 3);
        grid.add(new Label("Initial Quantity:"), 0, 4);
        grid.add(qty, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Product p = new Product();
                p.setName(name.getText());
                p.setPartNumber(code.getText());
                p.setCategory(category.getText());
                p.setPrice(Double.parseDouble(price.getText()));
                p.setQuantity(Integer.parseInt(qty.getText()));
                p.setMinStockLevel(5); // Default
                return p;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            if (productDAO.add(product)) {
                loadData();
            }
        });
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
