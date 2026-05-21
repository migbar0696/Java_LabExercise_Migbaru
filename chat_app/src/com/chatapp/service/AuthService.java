package com.chatapp.service;

import com.chatapp.dao.UserDAO;
import com.chatapp.model.User;
import com.chatapp.util.PasswordUtils;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }

        String hashed = PasswordUtils.hashPassword(password);
        return hashed.equals(user.getPasswordHash()) ? user : null;
    }
}
