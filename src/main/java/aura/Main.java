package aura;

import aura.enums.Role;
import aura.model.User;
import aura.ui.LoginFrame;
import aura.ui.admin.AdminDashboardFrame;
import aura.ui.common.UITheme;
import aura.ui.student.StudentDashboardFrame;
import aura.util.SessionContext;

import javax.swing.*;

/**
 * Main application entry point for AURA.
 * Initializes FlatDarkLaf look and feel and opens the appropriate GUI window.
 * Supports dev flags: --student, --admin, --login
 */
public class Main {
    public static void main(String[] args) {
        // Initialize modern FlatDarkLaf theme before any window is constructed
        UITheme.initLookAndFeel();

        String launchMode = "login";
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if ("--student".equalsIgnoreCase(arg)) {
                    launchMode = "student";
                } else if ("--admin".equalsIgnoreCase(arg)) {
                    launchMode = "admin";
                } else if ("--login".equalsIgnoreCase(arg)) {
                    launchMode = "login";
                }
            }
        }

        final String mode = launchMode;
        SwingUtilities.invokeLater(() -> {
            switch (mode) {
                case "student" -> {
                    // Seed dev student session (Muhammed Rinshid VP)
                    User devStudent = new User(1, "Muhammed Rinshid VP", "student1@tkmce.ac.in", "", Role.STUDENT);
                    SessionContext.setCurrentUser(devStudent);
                    StudentDashboardFrame studentFrame = new StudentDashboardFrame();
                    studentFrame.setVisible(true);
                }
                case "admin" -> {
                    // Seed dev admin session (Campus Maintenance)
                    User devAdmin = new User(5, "Campus Maintenance", "admin@tkmce.ac.in", "", Role.ADMIN);
                    SessionContext.setCurrentUser(devAdmin);
                    AdminDashboardFrame adminFrame = new AdminDashboardFrame();
                    adminFrame.setVisible(true);
                }
                default -> {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                }
            }
        });
    }
}
