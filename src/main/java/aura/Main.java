package aura;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.io.InputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("AURA started!");

        // Setup FlatLaf look and feel
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AURA");
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel status = new JLabel("Starting...");
            frame.add(status);
            frame.setVisible(true);

            // Load db.properties
            Properties props = new Properties();
            try (InputStream in = Main.class.getResourceAsStream("/db.properties")) {
                if (in == null) {
                    status.setText("db.properties not found!");
                    return;
                }
                props.load(in);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                // Force load PostgreSQL driver
                Class.forName("org.postgresql.Driver");

                // Try connecting
                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    System.out.println("Connected to Supabase!");
                    status.setText("Connected to Supabase!");
                } catch (Exception e) {
                    e.printStackTrace();
                    status.setText("Connection failed: " + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                status.setText("Could not load db.properties");
            }
        });
    }
}
