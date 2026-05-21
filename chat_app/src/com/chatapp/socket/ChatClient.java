package com.chatapp.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private String host;
    private int port;
    private String username;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener listener;

    public ChatClient(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("USER_JOIN::" + username);
            return true;
        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
            return false;
        }
    }

    public void startListening() {
        if (in == null || listener == null) {
            return;
        }
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    listener.onMessageReceived(message);
                }
            } catch (IOException e) {
                listener.onConnectionError(e.getMessage());
            }
        }).start();
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public interface MessageListener {
        void onMessageReceived(String message);
        void onConnectionError(String errorMessage);
    }
}
