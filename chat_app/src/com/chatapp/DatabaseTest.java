package com.chatapp;

import com.chatapp.db.DBConnection;
import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Running database connection test...");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection OK.");
            } else {
                System.err.println("Database connection failed: connection is closed or null.");
            }
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
