package scratch;

import aura.enums.Role;
import aura.model.User;
import aura.ui.LoginFrame;
import aura.ui.admin.AdminDashboardFrame;
import aura.ui.common.UITheme;
import aura.ui.student.StudentDashboardFrame;
import aura.util.SessionContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderScreenshots {
    public static void main(String[] args) {
        // Initialize theme
        UITheme.initLookAndFeel();

        String outDir = "C:\\Users\\lenovo\\.gemini\\antigravity-ide\\brain\\9ef1946b-9c7c-4216-a3a4-3936195c9b55";

        try {
            // 1. Render Login Frame
            System.out.println("Rendering LoginFrame...");
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setSize(500, 680);
            loginFrame.doLayout();
            renderComponent(loginFrame, new File(outDir, "login_frame.png"));
            loginFrame.dispose();

            // 2. Render Student Dashboard
            System.out.println("Rendering StudentDashboardFrame...");
            User devStudent = new User(1, "Muhammed Rinshid VP", "student1@tkmce.ac.in", "", Role.STUDENT);
            SessionContext.setCurrentUser(devStudent);
            StudentDashboardFrame studentFrame = new StudentDashboardFrame();
            studentFrame.setSize(1100, 750);
            studentFrame.doLayout();
            renderComponent(studentFrame, new File(outDir, "student_dashboard.png"));
            studentFrame.dispose();

            // 3. Render Admin Dashboard
            System.out.println("Rendering AdminDashboardFrame...");
            User devAdmin = new User(5, "Campus Maintenance", "admin@tkmce.ac.in", "", Role.ADMIN);
            SessionContext.setCurrentUser(devAdmin);
            AdminDashboardFrame adminFrame = new AdminDashboardFrame();
            adminFrame.setSize(1200, 800);
            adminFrame.doLayout();
            renderComponent(adminFrame, new File(outDir, "admin_dashboard.png"));
            adminFrame.dispose();

            System.out.println("ALL SCREENSHOTS RENDERED SUCCESSFULLY!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderComponent(JFrame frame, File outputFile) throws Exception {
        frame.addNotify();
        frame.validate();
        int width = Math.max(frame.getWidth(), 800);
        int height = Math.max(frame.getHeight(), 600);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        frame.paint(g2d);
        g2d.dispose();

        ImageIO.write(image, "PNG", outputFile);
        System.out.println("Saved: " + outputFile.getAbsolutePath() + " (" + width + "x" + height + ")");
    }
}
