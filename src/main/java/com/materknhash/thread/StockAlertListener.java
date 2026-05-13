package com.materknhash.thread;

import java.util.List;
import com.materknhash.model.Product;

/**
 * Interface for components that want to receive stock alerts.
 * Demonstrates the Observer Pattern (OOP).
 */
public interface StockAlertListener {
    void onLowStockDetected(List<Product> lowStockItems);
}
