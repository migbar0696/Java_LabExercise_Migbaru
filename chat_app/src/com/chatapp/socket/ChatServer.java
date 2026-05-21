package com.chatapp.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChatServer {
    private int port;
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private boolean running;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public void sendPrivateMessage(String message, String recipient, String sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                String username = client.getUsername();
                if (recipient.equals(username) || sender.equals(username)) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public void broadcastUserJoined(String username) {
        broadcastMessage("USER_JOIN::" + username);
    }

    public void broadcastUserLeft(String username) {
        broadcastMessage("USER_LEAVE::" + username);
    }

    public void registerClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
        String username = clientHandler.getUsername();
        if (username != null) {
            broadcastUserJoined(username);
            clientHandler.sendMessage("USER_LIST::" + getOnlineUsers());
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        String username = clientHandler.getUsername();
        if (username != null) {
            broadcastUserLeft(username);
        }
        System.out.println("Client disconnected. Remaining clients: " + clients.size());
    }

    private String getOnlineUsers() {
        synchronized (clients) {
            return clients.stream()
                    .map(ClientHandler::getUsername)
                    .filter(name -> name != null && !name.isEmpty())
                    .collect(Collectors.joining(","));
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
