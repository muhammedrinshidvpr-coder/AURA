package aura;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingUtilities;
import aura.ui.LoginFrame;

/**
 * Main entrypoint that applies FlatLaf look & feel and starts the LoginFrame.
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Apply FlatLaf dark theme; falls back silently if unavailable
            javax.swing.UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
}
