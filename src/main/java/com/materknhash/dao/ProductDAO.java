package com.materknhash.dao;

import com.materknhash.model.Product;
import com.materknhash.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Product entity.
 * Implements BaseDAO and provides Search functionality.
 */
public class ProductDAO implements BaseDAO<Product> {

    @Override
    public boolean add(Product p) {
        String sql = "INSERT INTO products (part_number, name, category, price, quantity, min_stock_level, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getPartNumber());
            pstmt.setString(2, p.getName());
            pstmt.setString(3, p.getCategory());
            pstmt.setDouble(4, p.getPrice());
            pstmt.setInt(5, p.getQuantity());
            pstmt.setInt(6, p.getMinStockLevel());
            pstmt.setInt(7, p.getSupplierId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Product p) {
        String sql = "UPDATE products SET part_number=?, name=?, category=?, price=?, quantity=?, min_stock_level=?, supplier_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getPartNumber());
            pstmt.setString(2, p.getName());
            pstmt.setString(3, p.getCategory());
            pstmt.setDouble(4, p.getPrice());
            pstmt.setInt(5, p.getQuantity());
            pstmt.setInt(6, p.getMinStockLevel());
            pstmt.setInt(7, p.getSupplierId());
            pstmt.setInt(8, p.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public Product getById(int id) {
        String sql = "SELECT p.*, s.name as supplier_name FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapResultSetToProduct(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name as supplier_name FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToProduct(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Product> search(String query) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name as supplier_name FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                     "WHERE p.name LIKE ? OR p.part_number LIKE ? OR p.category LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            pstmt.setString(1, q);
            pstmt.setString(2, q);
            pstmt.setString(3, q);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToProduct(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setPartNumber(rs.getString("part_number"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getDouble("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setMinStockLevel(rs.getInt("min_stock_level"));
        p.setSupplierId(rs.getInt("supplier_id"));
        p.setSupplierName(rs.getString("supplier_name"));
        return p;
    }
}
