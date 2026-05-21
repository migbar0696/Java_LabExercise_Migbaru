package com.chatapp.util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }

        UIManager.put("control", new Color(245, 245, 245));
        UIManager.put("info", new Color(245, 245, 245));
        UIManager.put("nimbusBase", new Color(60, 63, 65));
        UIManager.put("nimbusBlueGrey", new Color(95, 100, 105));
        UIManager.put("nimbusFocus", new Color(115, 164, 209));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
    }
}
