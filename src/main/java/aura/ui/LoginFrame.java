package aura.ui;

import aura.model.User;
import aura.service.AuthService;
import aura.ui.admin.AdminDashboardFrame;
import aura.ui.student.StudentDashboardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginFrame — simple login UI implemented to validate the app shell.
 */
public class LoginFrame extends JFrame {
n    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private AuthService authService;
    public LoginFrame() {
        authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("AURA - Login");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Email:"), c);
        c.gridx = 1; c.gridy = 0; emailField = new JTextField(20); panel.add(emailField, c);

        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Password:"), c);
        c.gridx = 1; c.gridy = 1; passwordField = new JPasswordField(20); panel.add(passwordField, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        panel.add(loginButton, c);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; statusLabel = new JLabel(" ", SwingConstants.CENTER); panel.add(statusLabel, c);
        add(panel);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });
    }

    private void onLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        statusLabel.setText("");
        if (!authService.validateCredentials(email, password)) {
            statusLabel.setText("Invalid email or password format.");
            return;
        }
        User user = authService.authenticate(email, password);
        if (user == null) {
            statusLabel.setText("Authentication failed. Check credentials.");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            dispose();
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                AdminDashboardFrame admin = new AdminDashboardFrame();
                admin.setVisible(true);
            } else {
                StudentDashboardFrame student = new StudentDashboardFrame(user);
                student.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
}
