package aura;

import aura.ui.LoginFrame;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.SwingUtilities;

/**
 * Main entrypoint that applies FlatLaf look and feel and starts the LoginFrame.
 */
public class Main {

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
