package com.chatapp.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message = in.readLine();
            if (message != null && message.startsWith("USER_JOIN::")) {
                username = message.substring("USER_JOIN::".length());
                server.registerClient(this);
            } else {
                username = clientSocket.getRemoteSocketAddress().toString();
                server.registerClient(this);
                if (message != null) {
                    server.broadcastMessage(message);
                }
            }

            while ((message = in.readLine()) != null) {
                if (message.startsWith("PRIVATE::")) {
                    String[] parts = message.split("::", 4);
                    if (parts.length == 4) {
                        server.sendPrivateMessage(message, parts[2], parts[1]);
                    }
                } else {
                    server.broadcastMessage(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        } finally {
            close();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException ignored) {
        }
        server.removeClient(this);
    }
}
