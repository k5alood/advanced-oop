package com.materknhash.dao;

import com.materknhash.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for Dashboard statistics and chart data.
 */
public class DashboardDAO {

    public double getTotalSales() {
        String sql = "SELECT SUM(total_amount) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public int getTotalParts() {
        String sql = "SELECT SUM(quantity) FROM products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getLowStockCount() {
        String sql = "SELECT COUNT(*) FROM products WHERE quantity <= min_stock_level";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalProfit() {
        // Profit = (Sale Price - Buy Price) * Quantity
        String sql = "SELECT SUM((si.unit_price - p.buy_price) * si.quantity) " +
                     "FROM sale_items si JOIN products p ON si.product_id = p.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public Map<String, Double> getSalesChartData() {
        Map<String, Double> data = new LinkedHashMap<>();
        // In a real DB, we'd group by month. For this demo, we'll simulate some months if empty.
        String sql = "SELECT DATE_FORMAT(sale_date, '%b') as month, SUM(total_amount) as total " +
                     "FROM sales GROUP BY month ORDER BY sale_date ASC LIMIT 6";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                data.put(rs.getString("month"), rs.getDouble("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
        // Demo data for initial visualization if DB is empty
        if (data.isEmpty()) {
            data.put("Jan", 12500.0);
            data.put("Feb", 18200.0);
            data.put("Mar", 15400.0);
            data.put("Apr", 22600.0);
            data.put("May", 19800.0);
            data.put("Jun", 28500.0);
        }
        return data;
    }

    public List<String> getRecentActivities() {
        List<String> activities = new java.util.ArrayList<>();
        String sql = "(SELECT CONCAT('New Sale: ', total_amount, ' EGP') as msg, sale_date as dt FROM sales) " +
                     "UNION " +
                     "(SELECT CONCAT('Added Product: ', name) as msg, created_at as dt FROM products) " +
                     "ORDER BY dt DESC LIMIT 5";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(rs.getString("msg"));
            }
        } catch (SQLException e) { 
            // If tables don't exist yet, show welcome messages
            activities.add("Welcome to Metrkansh ERP!");
            activities.add("System is ready for use.");
        }
        
        if (activities.isEmpty()) {
            activities.add("New Sale: 15,000 EGP");
            activities.add("Added Product: Brake Pads");
            activities.add("System Online");
        }
        return activities;
    }
}
