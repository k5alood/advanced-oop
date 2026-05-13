package com.materknhash.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;

public class ReportController {
    @FXML private PieChart revenuePieChart;
    @FXML private BarChart<String, Number> growthBarChart;
    @FXML private TableView<?> topProductsTable;

    @FXML
    public void initialize() {
        setupDummyData();
    }

    private void setupDummyData() {
        // Pie Chart
        revenuePieChart.getData().addAll(
            new PieChart.Data("Brake Pads", 40),
            new PieChart.Data("Oil Filters", 25),
            new PieChart.Data("Spark Plugs", 20),
            new PieChart.Data("Others", 15)
        );

        // Bar Chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue 2026");
        series.getData().add(new XYChart.Data<>("Jan", 15000));
        series.getData().add(new XYChart.Data<>("Feb", 22000));
        series.getData().add(new XYChart.Data<>("Mar", 18000));
        growthBarChart.getData().add(series);
    }
}
