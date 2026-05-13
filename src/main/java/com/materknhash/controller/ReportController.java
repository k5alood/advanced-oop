package com.materknhash.controller;

import com.materknhash.dao.ReportDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;

import java.util.Map;

/**
 * Controller for the Reports screen.
 * Demonstrates Chart Integration (Requirement 8) and Multithreading.
 */
public class ReportController {

    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Integer> productsBarChart;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private Text revenueText, profitText;

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    public void initialize() {
        generateReports();
    }

    @FXML
    private void generateReports() {
        // Run data fetching in a background thread to keep UI responsive (Multithreading Requirement)
        new Thread(() -> {
            Map<String, Double> categoryData = reportDAO.getSalesByCategory();
            Map<String, Integer> productData = reportDAO.getTopSellingProducts();
            double revenue = reportDAO.getTotalRevenue();

            // Update UI on FX Application Thread
            Platform.runLater(() -> {
                updatePieChart(categoryData);
                updateBarChart(productData);
                revenueText.setText(String.format("$%,.2f", revenue));
                profitText.setText(String.format("$%,.2f", revenue * 0.25)); // Estimated profit
            });
        }).start();
    }

    private void updatePieChart(Map<String, Double> data) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        data.forEach((cat, val) -> pieData.add(new PieChart.Data(cat, val)));
        categoryPieChart.setData(pieData);
    }

    private void updateBarChart(Map<String, Integer> data) {
        productsBarChart.getData().clear();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Units Sold");

        data.forEach((name, qty) -> series.getData().add(new XYChart.Data<>(name, qty)));
        productsBarChart.getData().add(series);
    }
}
