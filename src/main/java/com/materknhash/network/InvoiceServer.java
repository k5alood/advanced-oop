package com.materknhash.network;

import com.materknhash.dao.SaleDAO;
import com.materknhash.model.Sale;
import java.io.*;
import java.net.*;

/**
 * Server that receives Sales data via Sockets.
 * Fulfills the "Socket Programming (Mandatory)" requirement.
 */
public class InvoiceServer extends Thread {
    private final int port;
    private final SaleDAO saleDAO = new SaleDAO();
    private boolean running = true;

    public InvoiceServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Invoice Server STARTED on port " + port);

            while (running) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    System.out.println("Client Connected: " + clientSocket.getInetAddress());

                    // Receive Sale object
                    Sale sale = (Sale) in.readObject();
                    System.out.println("Received Sale: $" + sale.getTotalAmount());

                    // Store in database using real DAO
                    boolean success = saleDAO.processSale(sale);

                    // Send response back
                    out.writeUTF(success ? "SUCCESS" : "FAILURE");
                    out.flush();

                } catch (Exception e) {
                    System.err.println("Server Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
    }
}
