package com.chatapp.ui;

import com.chatapp.dao.MessageDAO;
import com.chatapp.dao.UserDAO;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.service.MessageService;
import com.chatapp.socket.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ChatWindow extends JFrame {
    private User currentUser;
    private JTextArea chatArea;
    private JTextField messageField;
    private DefaultListModel<String> onlineUsersModel;
    private JComboBox<String> recipientCombo;
    private ChatClient chatClient;
    private MessageService messageService;
    private MessageDAO messageDAO;
    private UserDAO userDAO;

    public ChatWindow(User currentUser) {
        this.currentUser = currentUser;
        this.messageService = new MessageService();
        this.messageDAO = new MessageDAO();
        this.userDAO = new UserDAO();
        setTitle("Chat App - Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        initComponents();
        loadRecentMessages();
        initializeClient();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 63, 65));
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getDisplayName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 80, 80));
        logoutButton.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        onlineUsersModel = new DefaultListModel<>();
        recipientCombo = new JComboBox<>();
        recipientCombo.addItem("All");
        recipientCombo.setPreferredSize(new Dimension(140, 30));
        JList<String> onlineUsersList = new JList<>(onlineUsersModel);
        onlineUsersList.setBorder(BorderFactory.createTitledBorder("Online Users"));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createTitledBorder("Chat"));

        JPanel messagePanel = new JPanel(new BorderLayout(8, 8));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel recipientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        recipientPanel.add(new JLabel("Send to:"));
        recipientPanel.add(recipientCombo);
        messagePanel.add(recipientPanel, BorderLayout.NORTH);
        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(0, 36));
        JButton sendButton = new JButton("Send");
        JButton attachButton = new JButton("Attach Image");
        sendButton.setPreferredSize(new Dimension(100, 36));
        attachButton.setPreferredSize(new Dimension(140, 36));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(attachButton);
        actionPanel.add(sendButton);
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(actionPanel, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(chatScrollPane, BorderLayout.CENTER);
        centerPanel.add(messagePanel, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JScrollPane(onlineUsersList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(180, 0));

        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(sendButton);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        attachButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploadImage();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (chatClient != null) {
                    chatClient.close();
                }
                dispose();
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            }
        });
    }

    private void initializeClient() {
        chatClient = new ChatClient("localhost", 5000, currentUser.getUsername());
        if (!chatClient.connect()) {
            JOptionPane.showMessageDialog(this, "Unable to connect to chat server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        chatClient.setMessageListener(new ChatClient.MessageListener() {
            @Override
            public void onMessageReceived(String message) {
                SwingUtilities.invokeLater(() -> handleServerMessage(message));
            }

            @Override
            public void onConnectionError(String errorMessage) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ChatWindow.this, "Connection error: " + errorMessage, "Connection Error", JOptionPane.ERROR_MESSAGE));
            }
        });
        chatClient.startListening();
        chatArea.append("Connected to chat server.\n");
    }

    private void loadRecentMessages() {
        List<Message> history = messageDAO.getRecentMessages(50);
        if (history.isEmpty()) {
            chatArea.append("No previous messages found. Start the conversation!\n\n");
            return;
        }

        chatArea.append("=== Recent Messages ===\n");
        for (Message message : history) {
            chatArea.append(message.format() + "\n");
        }
        chatArea.append("=== End of History ===\n\n");
    }

    private void handleServerMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        if (message.startsWith("USER_LIST::")) {
            setOnlineUsers(message.substring("USER_LIST::".length()));
            return;
        }

        if (message.startsWith("USER_JOIN::")) {
            String username = message.substring("USER_JOIN::".length());
            addOnlineUser(username);
            chatArea.append(username + " joined the chat.\n");
            return;
        }

        if (message.startsWith("USER_LEAVE::")) {
            String username = message.substring("USER_LEAVE::".length());
            removeOnlineUser(username);
            chatArea.append(username + " left the chat.\n");
            return;
        }

        if (message.startsWith("PUBLIC::")) {
            String[] parts = message.split("::", 3);
            if (parts.length == 3) {
                chatArea.append(parts[1] + ": " + parts[2] + "\n");
            }
            return;
        }

        if (message.startsWith("PRIVATE::")) {
            String[] parts = message.split("::", 4);
            if (parts.length == 4) {
                String sender = parts[1];
                String recipient = parts[2];
                String text = parts[3];
                if (sender.equals(currentUser.getUsername()) || recipient.equals(currentUser.getUsername())) {
                    chatArea.append(sender + " -> " + recipient + " [private]: " + text + "\n");
                }
            }
            return;
        }

        chatArea.append(message + "\n");
    }

    private void setOnlineUsers(String users) {
        onlineUsersModel.clear();
        recipientCombo.removeAllItems();
        recipientCombo.addItem("All");
        if (users == null || users.isBlank()) {
            return;
        }
        for (String user : users.split(",")) {
            String trimmed = user.trim();
            if (!trimmed.isBlank() && !trimmed.equals(currentUser.getUsername())) {
                onlineUsersModel.addElement(trimmed);
                recipientCombo.addItem(trimmed);
            }
        }
    }

    private void addOnlineUser(String username) {
        if (username == null || username.isBlank() || username.equals(currentUser.getUsername())) {
            return;
        }
        if (!onlineUsersModel.contains(username)) {
            onlineUsersModel.addElement(username);
        }
        boolean found = false;
        for (int i = 0; i < recipientCombo.getItemCount(); i++) {
            if (recipientCombo.getItemAt(i).equals(username)) {
                found = true;
                break;
            }
        }
        if (!found) {
            recipientCombo.addItem(username);
        }
    }

    private void removeOnlineUser(String username) {
        onlineUsersModel.removeElement(username);
        recipientCombo.removeItem(username);
    }

    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select image to upload");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();
        if (selectedFile == null || !selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, "Selected file is not available.", "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File destFile = new File(uploadDir, System.currentTimeMillis() + "_" + selectedFile.getName());
        try {
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String recipient = (String) recipientCombo.getSelectedItem();
            boolean isPrivate = recipient != null && !"All".equals(recipient);
            Message outgoingMessage = new Message(currentUser.getId(), currentUser.getDisplayName(), "[Image]");
            outgoingMessage.setImagePath(destFile.getAbsolutePath());
            String formattedMessage;
            if (isPrivate) {
                User targetUser = userDAO.findByUsername(recipient);
                if (targetUser == null) {
                    JOptionPane.showMessageDialog(this, "Recipient is not available.", "Send Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                outgoingMessage.setReceiverId(targetUser.getId());
                outgoingMessage.setReceiverName(recipient);
                messageDAO.saveMessage(outgoingMessage);
                formattedMessage = "PRIVATE::" + currentUser.getUsername() + "::" + recipient + "::[Image]";
            } else {
                messageDAO.saveMessage(outgoingMessage);
                formattedMessage = "PUBLIC::" + currentUser.getUsername() + "::[Image]";
            }
            chatClient.sendMessage(formattedMessage);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to upload image: " + e.getMessage(), "Upload Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty() || chatClient == null) {
            return;
        }
        String recipient = (String) recipientCombo.getSelectedItem();
        boolean isPrivate = recipient != null && !"All".equals(recipient);
        Message outgoingMessage = new Message(currentUser.getId(), currentUser.getDisplayName(), text);
        String formattedMessage;
        if (isPrivate) {
            User targetUser = userDAO.findByUsername(recipient);
            if (targetUser == null) {
                JOptionPane.showMessageDialog(this, "Recipient is not available.", "Send Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            outgoingMessage.setReceiverId(targetUser.getId());
            outgoingMessage.setReceiverName(recipient);
            messageDAO.saveMessage(outgoingMessage);
            formattedMessage = "PRIVATE::" + currentUser.getUsername() + "::" + recipient + "::" + text;
        } else {
            messageDAO.saveMessage(outgoingMessage);
            formattedMessage = "PUBLIC::" + currentUser.getUsername() + "::" + text;
        }
        chatClient.sendMessage(formattedMessage);
        messageField.setText("");
    }
}
