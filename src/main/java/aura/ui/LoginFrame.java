package aura.ui;

import aura.config.OAuthConfig;
import aura.model.User;
import aura.service.AuthService;
import aura.ui.admin.AdminDashboardFrame;
import aura.ui.student.StudentDashboardFrame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Login screen for password, Google OAuth, and offline development sign-in. */
public class LoginFrame extends JFrame {
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final AuthService authService;

    public LoginFrame() {
        this(new AuthService());
    }

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        initializeUi();
    }

    private void initializeUi() {
        setTitle("AURA - Sign In");
        setSize(460, OAuthConfig.isDevMode() ? 340 : 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Institutional email:"), constraints);
        constraints.gridx = 1;
        panel.add(emailField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Password:"), constraints);
        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        JButton passwordLogin = new JButton("Sign in with password");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(passwordLogin, constraints);

        JButton googleLogin = new JButton("Sign in with Google");
        googleLogin.setEnabled(OAuthConfig.isConfigured());
        googleLogin.setToolTipText(googleLogin.isEnabled()
                ? "Opens your default browser for institutional Google sign-in"
                : "Configure oauth.properties to enable Google sign-in");
        constraints.gridy = 3;
        panel.add(googleLogin, constraints);

        if (OAuthConfig.isDevMode()) {
            JPanel mockPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            JButton mockStudent = new JButton("Mock Student");
            JButton mockAdmin = new JButton("Mock Admin");
            mockPanel.add(mockStudent);
            mockPanel.add(mockAdmin);
            constraints.gridy = 4;
            panel.add(mockPanel, constraints);
            mockStudent.addActionListener(event -> openDashboard(authService.loginMock("STUDENT")));
            mockAdmin.addActionListener(event -> openDashboard(authService.loginMock("ADMIN")));
        }

        constraints.gridy = OAuthConfig.isDevMode() ? 5 : 4;
        panel.add(statusLabel, constraints);
        add(panel, BorderLayout.CENTER);

        passwordLogin.addActionListener(event -> passwordLogin());
        googleLogin.addActionListener(event -> googleLogin());
        getRootPane().setDefaultButton(passwordLogin);
    }

    private void passwordLogin() {
        String password = new String(passwordField.getPassword());
        User user = authService.authenticate(emailField.getText(), password);
        passwordField.setText("");
        if (user == null) {
            statusLabel.setText("Sign-in failed. Use a valid TKMCE account and password.");
            return;
        }
        openDashboard(user);
    }

    private void googleLogin() {
        statusLabel.setText("Waiting for Google sign-in in your browser...");
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.loginWithGoogle();
            }

            @Override
            protected void done() {
                try {
                    openDashboard(get());
                } catch (Exception exception) {
                    statusLabel.setText("Google sign-in failed: " + exception.getMessage());
                }
            }
        }.execute();
    }

    private void openDashboard(User user) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                new AdminDashboardFrame().setVisible(true);
            } else {
                new StudentDashboardFrame(user).setVisible(true);
            }
        });
    }
}
