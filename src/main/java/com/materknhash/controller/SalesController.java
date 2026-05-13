package com.materknhash.controller;

import com.materknhash.dao.ProductDAO;
import com.materknhash.dao.SaleDAO;
import com.materknhash.model.Product;
import com.materknhash.model.Sale;
import com.materknhash.model.SaleItem;
import com.materknhash.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.time.LocalDate;

/**
 * Controller for the Sales/Invoice screen.
 */
public class SalesController {

    @FXML private TextField customerNameField, customerPhoneField, productSearchField;
    @FXML private ListView<Product> productListView;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TableView<SaleItem> invoiceTable;
    @FXML private TableColumn<SaleItem, String> colItem;
    @FXML private TableColumn<SaleItem, Double> colPrice, colTotal;
    @FXML private TableColumn<SaleItem, Integer> colQty;
    @FXML private Text subtotalLabel, taxLabel, grandTotalLabel, invoiceDateLabel;

    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final ObservableList<SaleItem> invoiceItems = FXCollections.observableArrayList();
    private final ObservableList<Product> availableProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        invoiceDateLabel.setText("Date: " + LocalDate.now());
        setupInvoiceTable();
        loadProducts();
        setupSearch();
        
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    private void setupInvoiceTable() {
        colItem.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        invoiceTable.setItems(invoiceItems);
    }

    private void loadProducts() {
        availableProducts.setAll(productDAO.getAll());
        productListView.setItems(availableProducts);
    }

    private void setupSearch() {
        productSearchField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                productListView.setItems(availableProducts);
            } else {
                productListView.setItems(FXCollections.observableArrayList(
                    productDAO.search(newValue)
                ));
            }
        });
    }

    @FXML
    private void addToInvoice() {
        Product selected = productListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int qty = quantitySpinner.getValue();
        if (qty > selected.getQuantity()) {
            showAlert("Out of stock", "Only " + selected.getQuantity() + " items available.");
            return;
        }

        SaleItem item = new SaleItem(selected.getId(), selected.getName(), qty, selected.getPrice());
        invoiceItems.add(item);
        calculateTotals();
    }

    private void calculateTotals() {
        double subtotal = invoiceItems.stream().mapToDouble(SaleItem::getTotalPrice).sum();
        double tax = subtotal * 0.14;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("%.2f", subtotal));
        taxLabel.setText(String.format("%.2f", tax));
        grandTotalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    private void handleCompleteSale() {
        if (invoiceItems.isEmpty()) return;

        Sale sale = new Sale();
        sale.setTotalAmount(Double.parseDouble(subtotalLabel.getText()) + Double.parseDouble(taxLabel.getText()));
        sale.setUserId(SessionManager.getInstance().getCurrentUser().getId());
        sale.setItems(new java.util.ArrayList<>(invoiceItems));

        // Use Socket Client to send data to Server (Requirement 6)
        com.materknhash.network.InvoiceClient client = new com.materknhash.network.InvoiceClient("localhost", 5000);
        String response = client.sendSale(sale);

        if ("SUCCESS".equals(response)) {
            showAlert("Success", "Transaction processed by SERVER and stock updated.");
            clearInvoice();
            loadProducts();
        } else {
            showAlert("Socket Error", "Could not connect to Server: " + response);
        }
    }

    @FXML
    private void clearInvoice() {
        invoiceItems.clear();
        calculateTotals();
        customerNameField.clear();
        customerPhoneField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
