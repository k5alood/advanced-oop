package com.materknhash.controller;

import com.materknhash.dao.DashboardDAO;
import com.materknhash.model.User;
import com.materknhash.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the Dashboard screen.
 * Implements live data updates using Multithreading.
 */
public class DashboardController {

    @FXML
    private Label userNameLabel;
    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label totalPartsLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label totalProfitLabel;
    @FXML
    private LineChart<String, Number> salesChart;
    @FXML
    private VBox recentActivitiesList;
    @FXML
    private StackPane contentArea;
    @FXML
    private BorderPane mainBorderPane;

    private javafx.scene.Node dashboardHomeView;
    private boolean isFirstInit = true;

    @FXML
    private Button reportsBtn;
    @FXML
    private Button usersBtn;

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private com.materknhash.thread.StockMonitorThread monitorThread;

    @FXML
    public void initialize() {
        if (!isFirstInit) return; // Safety check to prevent infinite loop
        isFirstInit = false;

        try {
            // Load Dashboard Home by default
            showDashboard();
            
            if (SessionManager.getInstance().isLoggedIn()) {
                User user = SessionManager.getInstance().getCurrentUser();
                if (userNameLabel != null && user != null) {
                    userNameLabel.setText(user.getUsername());
                }

                // Role-Based Access Control (RBAC)
                if (user != null && user.getRole() != User.UserRole.ADMIN) {
                    if (reportsBtn != null) {
                        reportsBtn.setVisible(false);
                        reportsBtn.setManaged(false);
                    }
                    if (usersBtn != null) {
                        usersBtn.setVisible(false);
                        usersBtn.setManaged(false);
                    }
                    System.out.println("Access RESTRICTED for: " + user.getRole());
                }
            }

            // Initial Data Load
            refreshDashboardData();

            // Start dedicated Background Thread (Requirement 5)
            startStockMonitoring();
        } catch (Exception e) {
            System.err.println("Error during Dashboard initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshDashboardData() {
        // Fetch from DAO
        double sales = dashboardDAO.getTotalSales();
        double profit = dashboardDAO.getTotalProfit();
        int parts = dashboardDAO.getTotalParts();
        int lowStock = dashboardDAO.getLowStockCount();
        Map<String, Double> chartData = dashboardDAO.getSalesChartData();

        // Update UI (Ensuring it happens on JavaFX Application Thread)
        Platform.runLater(() -> {
            totalSalesLabel.setText(String.format("%,.0f EGP", sales));
            totalPartsLabel.setText(String.valueOf(parts));
            lowStockLabel.setText(String.valueOf(lowStock));
            totalProfitLabel.setText(String.format("%,.0f EGP", profit));

            updateChart(chartData);
            updateRecentActivities();
        });
    }

    private void updateRecentActivities() {
        if (recentActivitiesList == null)
            return;
        recentActivitiesList.getChildren().clear();

        List<String> activities = dashboardDAO.getRecentActivities();
        for (String activity : activities) {
            HBox item = new HBox(10);
            item.getStyleClass().add("activity-item");
            item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Create a colored icon circle based on activity type
            Circle icon = new Circle(15);
            if (activity.contains("Sale"))
                icon.setFill(javafx.scene.paint.Color.rgb(34, 197, 94)); // Green
            else if (activity.contains("Product"))
                icon.setFill(javafx.scene.paint.Color.rgb(59, 130, 246)); // Blue
            else
                icon.setFill(javafx.scene.paint.Color.rgb(100, 116, 139)); // Gray

            VBox textContainer = new VBox(2);
            Label msg = new Label(activity);
            msg.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

            Label time = new Label("Just now");
            time.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

            textContainer.getChildren().addAll(msg, time);
            item.getChildren().addAll(icon, textContainer);

            recentActivitiesList.getChildren().add(item);
        }
    }

    private void updateChart(Map<String, Double> data) {
        if (salesChart == null)
            return;

        salesChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales Revenue 2026");

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        salesChart.getData().add(series);
    }

    private void startStockMonitoring() {
        monitorThread = new com.materknhash.thread.StockMonitorThread();
        monitorThread.addListener(lowStockItems -> {
            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                lowStockLabel.setText(String.valueOf(lowStockItems.size()));
                System.out.println("Background Alert: " + lowStockItems.size() + " items are low in stock!");
            });
        });
        monitorThread.setDaemon(true); // Close with the app
        monitorThread.start();
    }

    @FXML
    private void showDashboard() {
        loadView("/com/materknhash/view/Home.fxml");
        refreshDashboardData();
    }

    @FXML
    private void showInventory() {
        loadView("/com/materknhash/view/Inventory.fxml");
    }

    @FXML
    private void showSales() {
        loadView("/com/materknhash/view/Sales.fxml");
    }

    @FXML
    private void showPurchases() {
        loadView("/com/materknhash/view/Inventory.fxml");
    } // Placeholder

    @FXML
    private void showSuppliers() {
        loadView("/com/materknhash/view/Supplier.fxml");
    }

    @FXML
    private void showReports() {
        loadView("/com/materknhash/view/Reports.fxml");
    }

    @FXML
    private void showUsers() {
        loadView("/com/materknhash/view/UserManagement.fxml");
    }

    @FXML
    private void showSettings() {
        loadView("/com/materknhash/view/Dashboard.fxml"); // Still a placeholder but will be fixed
    }

    private void loadView(String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            // If the FXML uses DashboardController, we want to use THIS instance
            if (fxmlPath.contains("Home.fxml")) {
                loader.setController(this);
            }
            contentArea.getChildren().setAll((javafx.scene.Node) loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        if (monitorThread != null)
            monitorThread.stopMonitoring();
        System.out.println("Logging out...");
        // Logic to return to Login screen
    }

    // Cleanup when controller is destroyed
    public void stop() {
        if (monitorThread != null)
            monitorThread.stopMonitoring();
    }
}
