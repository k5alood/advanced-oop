package com.materknhash.network;

import com.materknhash.model.Sale;
import java.io.*;
import java.net.*;

/**
 * Client that sends Sales data to the Server via Sockets.
 */
public class InvoiceClient {
    private final String host;
    private final int port;

    public InvoiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String sendSale(Sale sale) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Send real system data
            out.writeObject(sale);
            out.flush();

            // Read response
            return in.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }
}
