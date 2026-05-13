package com.materknhash.dao;

import com.materknhash.util.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO for generating business reports and charts.
 */
public class ReportDAO {

    /**
     * Fetches sales distribution by product category.
     * Used for PieChart.
     */
    public Map<String, Double> getSalesByCategory() {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT p.category, SUM(si.unit_price * si.quantity) as total " +
                     "FROM sale_items si " +
                     "JOIN products p ON si.product_id = p.id " +
                     "GROUP BY p.category";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    /**
     * Fetches top 5 selling products by quantity.
     * Used for BarChart.
     */
    public Map<String, Integer> getTopSellingProducts() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT p.name, SUM(si.quantity) as total_qty " +
                     "FROM sale_items si " +
                     "JOIN products p ON si.product_id = p.id " +
                     "GROUP BY p.name ORDER BY total_qty DESC LIMIT 5";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("name"), rs.getInt("total_qty"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    /**
     * Fetches total revenue and profit over a period.
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
