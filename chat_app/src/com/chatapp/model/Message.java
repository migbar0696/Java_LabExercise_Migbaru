package com.chatapp.model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private int senderId;
    private String senderName;
    private String text;
    private String imagePath;
    private String receiverName;
    private int receiverId;
    private LocalDateTime sentAt;

    public Message(int senderId, String senderName, String text) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.sentAt = LocalDateTime.now();
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getText() {
        return text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public String format() {
        String timestamp = sentAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String body = text;
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            body += " [Image: " + imageFile.getName() + "]";
        }
        if (receiverName != null && !receiverName.isEmpty()) {
            return String.format("%s -> %s [%s]: %s", senderName, receiverName, timestamp, body);
        }
        return String.format("%s [%s]: %s", senderName, timestamp, body);
    }
}
