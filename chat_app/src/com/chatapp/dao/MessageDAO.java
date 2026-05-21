package com.chatapp.dao;

import com.chatapp.db.DBConnection;
import com.chatapp.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageDAO {
    public boolean saveMessage(Message message) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_text, image_path) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message.getSenderId());
            if (message.getReceiverId() > 0) {
                stmt.setInt(2, message.getReceiverId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, message.getText());
            stmt.setString(4, message.getImagePath());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
            return false;
        }
    }

    public List<Message> getRecentMessages(int limit) {
        String sql = "SELECT u.display_name AS sender_name, m.message_text, m.image_path, m.sent_at, " +
                "r.display_name AS receiver_name " +
                "FROM messages m " +
                "JOIN users u ON m.sender_id = u.id " +
                "LEFT JOIN users r ON m.receiver_id = r.id " +
                "ORDER BY m.sent_at DESC LIMIT ?";
        List<Message> messages = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(0, rs.getString("sender_name"), rs.getString("message_text"));
                    message.setReceiverName(rs.getString("receiver_name"));
                    message.setImagePath(rs.getString("image_path"));
                    message.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime());
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading message history: " + e.getMessage());
        }
        Collections.reverse(messages);
        return messages;
    }
}
