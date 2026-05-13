package com.materknhash.dao;

import com.materknhash.model.Sale;
import com.materknhash.model.SaleItem;
import com.materknhash.util.DatabaseConnection;
import java.sql.*;

/**
 * DAO for Sales. Handles transactional logic for invoices.
 */
public class SaleDAO {

    /**
     * Saves a complete sale transaction.
     * Implements Transactional JDBC (Requirement 3 + 9).
     */
    public boolean processSale(Sale sale) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert Sale Header
            String saleSql = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
            PreparedStatement salePstmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
            salePstmt.setDouble(1, sale.getTotalAmount());
            salePstmt.setInt(2, sale.getUserId());
            salePstmt.executeUpdate();

            // Get generated sale ID
            ResultSet rs = salePstmt.getGeneratedKeys();
            int saleId = 0;
            if (rs.next()) saleId = rs.getInt(1);

            // 2. Insert Sale Items & Update Stock
            String itemSql = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            String stockSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            
            PreparedStatement itemPstmt = conn.prepareStatement(itemSql);
            PreparedStatement stockPstmt = conn.prepareStatement(stockSql);

            for (SaleItem item : sale.getItems()) {
                // Insert Item
                itemPstmt.setInt(1, saleId);
                itemPstmt.setInt(2, item.getProductId());
                itemPstmt.setInt(3, item.getQuantity());
                itemPstmt.setDouble(4, item.getUnitPrice());
                itemPstmt.addBatch();

                // Update Stock
                stockPstmt.setInt(1, item.getQuantity());
                stockPstmt.setInt(2, item.getProductId());
                stockPstmt.addBatch();
            }

            itemPstmt.executeBatch();
            stockPstmt.executeBatch();

            conn.commit(); // Commit Transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
