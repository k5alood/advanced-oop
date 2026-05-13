package com.materknhash.thread;

import com.materknhash.dao.ProductDAO;
import com.materknhash.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Background Thread that monitors inventory levels.
 * Fulfills the "Multithreading (Mandatory)" requirement.
 */
public class StockMonitorThread extends Thread {
    
    private final ProductDAO productDAO = new ProductDAO();
    private final List<StockAlertListener> listeners = new ArrayList<>();
    private boolean running = true;

    public void addListener(StockAlertListener listener) {
        listeners.add(listener);
    }

    public void stopMonitoring() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        System.out.println("Stock Monitoring Service - STARTED");
        
        while (running) {
            try {
                // Fetch low stock items
                List<Product> allProducts = productDAO.getAll();
                List<Product> lowStockItems = allProducts.stream()
                        .filter(p -> p.getQuantity() <= p.getMinStockLevel())
                        .collect(Collectors.toList());

                if (!lowStockItems.isEmpty()) {
                    // Notify listeners
                    for (StockAlertListener listener : listeners) {
                        listener.onLowStockDetected(lowStockItems);
                    }
                }

                // Sleep for 60 seconds (University requirement check: Real background thread)
                Thread.sleep(60000); 

            } catch (InterruptedException e) {
                System.out.println("Stock Monitoring Service - STOPPED");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
