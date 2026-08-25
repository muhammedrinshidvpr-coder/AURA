package aura.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import aura.enums.Role;
import aura.exception.AuraException;
import aura.model.User;
import aura.service.AuthService;
import aura.ui.admin.AdminDashboardFrame;
import aura.ui.student.StudentDashboardFrame;

/**
 * TKMCE-gated login/registration screen. Registration through the UI is
 * student-only — admin accounts are provisioned directly in the database,
 * not self-served, since AURA has only two roles and no admin-approval flow.
 */
public class LoginFrame extends JFrame {

    private static final String LOGIN_CARD = "LOGIN";
    private static final String REGISTER_CARD = "REGISTER";

    private final AuthService authService = new AuthService();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final JTextField loginEmailField = new JTextField();
    private final JPasswordField loginPasswordField = new JPasswordField();

    private final JTextField registerNameField = new JTextField();
    private final JTextField registerEmailField = new JTextField();
    private final JPasswordField registerPasswordField = new JPasswordField();

    public LoginFrame() {
        super("AURA — TKM College of Engineering");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);

        cardPanel.add(buildLoginPanel(), LOGIN_CARD);
        cardPanel.add(buildRegisterPanel(), REGISTER_CARD);
        add(cardPanel);
        cardLayout.show(cardPanel, LOGIN_CARD);
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("TKMCE Email:"));
        formPanel.add(loginEmailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(loginPasswordField);

        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(e -> handleLogin());
        JButton toRegisterButton = new JButton("New here? Register");
        toRegisterButton.addActionListener(e -> cardLayout.show(cardPanel, REGISTER_CARD));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(toRegisterButton);

        panel.add(new JLabel("AURA — Report. Track. Resolve. Improve.", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(registerNameField);
        formPanel.add(new JLabel("TKMCE Email:"));
        formPanel.add(registerEmailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(registerPasswordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegister());
        JButton toLoginButton = new JButton("Back to Login");
        toLoginButton.addActionListener(e -> cardLayout.show(cardPanel, LOGIN_CARD));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(toLoginButton);

        panel.add(new JLabel("Register a student account (@tkmce.ac.in)", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        try {
            User user = authService.login(email, password);
            openDashboardFor(user);
        } catch (AuraException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String name = registerNameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        try {
            User user = authService.register(name, email, password, Role.STUDENT);
            openDashboardFor(user);
        } catch (AuraException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Registration failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboardFor(User user) {
        dispose();
        if (user.getRole() == Role.ADMIN) {
            new AdminDashboardFrame().setVisible(true);
        } else {
            new StudentDashboardFrame().setVisible(true);
        }
    }
}
