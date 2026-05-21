package com.chatapp;

import com.chatapp.ui.LoginForm;
import com.chatapp.util.UIUtils;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIUtils.applyTheme();
            new LoginForm().setVisible(true);
        });
    }
}
