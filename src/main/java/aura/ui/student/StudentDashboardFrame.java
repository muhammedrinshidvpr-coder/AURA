package aura.ui.student;

import aura.model.User;
import aura.service.AuthService;
import aura.service.HypeService;
import aura.service.SubmissionService;
import aura.ui.LoginFrame;
import aura.ui.common.ModernButton;
import aura.ui.common.UITheme;
import aura.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Student Portal desktop window.
 * Adheres to Slide 21 mockup of docs/AURA.pdf.
 */
public class StudentDashboardFrame extends JFrame {
    private final SubmissionService submissionService;
    private final HypeService hypeService;
    private final AuthService authService;

    private StudentFeedPanel trendingPanel;
    private StudentFeedPanel recentPanel;
    private StudentFeedPanel mySubmissionsPanel;

    public StudentDashboardFrame() {
        this(new SubmissionService(), new HypeService(), new AuthService());
    }

    public StudentDashboardFrame(SubmissionService submissionService, HypeService hypeService, AuthService authService) {
        super("AURA — Student Portal | TKMCE");
        this.submissionService = submissionService;
        this.hypeService = hypeService;
        this.authService = authService;

        setSize(980, 720);
        setMinimumSize(new Dimension(840, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_BASE);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_BASE);

        // --- Top Header ---
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(UITheme.BG_SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(14, 20, 14, 20)
        ));

        // Brand Title
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("⚡ AURA");
        logoLabel.setFont(UITheme.FONT_HEADER);
        logoLabel.setForeground(UITheme.PRIMARY_LIGHT);

        JLabel subTitleLabel = new JLabel("Student Portal | TKMCE");
        subTitleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        subTitleLabel.setForeground(UITheme.TEXT_MUTED);

        brandPanel.add(logoLabel);
        brandPanel.add(subTitleLabel);
        header.add(brandPanel, BorderLayout.WEST);

        // Header Actions: User Pill, + Report Button, Logout
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        User current = SessionContext.getCurrentUser();
        String studentName = current != null ? current.getName() : "Student";
        JLabel userPill = new JLabel("👤 " + studentName);
        userPill.setFont(UITheme.FONT_BODY_BOLD);
        userPill.setForeground(UITheme.TEXT_MAIN);
        userPill.setBorder(BorderFactory.createCompoundBorder(
                UITheme.createSubtleBorder(),
                new EmptyBorder(6, 12, 6, 12)
        ));

        ModernButton refreshBtn = new ModernButton("↻ Refresh", ModernButton.Style.GHOST);
        refreshBtn.addActionListener(e -> refreshAllFeeds());

        ModernButton reportBtn = new ModernButton("+ Report Issue", ModernButton.Style.PRIMARY);
        reportBtn.addActionListener(e -> {
            SubmissionFormDialog dialog = new SubmissionFormDialog(this, submissionService, this::refreshAllFeeds);
            dialog.setVisible(true);
        });

        ModernButton logoutBtn = new ModernButton("Logout", ModernButton.Style.GHOST);
        logoutBtn.addActionListener(e -> handleLogout());

        actionPanel.add(userPill);
        actionPanel.add(refreshBtn);
        actionPanel.add(reportBtn);
        actionPanel.add(logoutBtn);
        header.add(actionPanel, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // --- Tabbed Feed Panels ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(UITheme.BG_SURFACE);
        tabbedPane.setForeground(UITheme.TEXT_MUTED);

        trendingPanel = new StudentFeedPanel(submissionService, hypeService, StudentFeedPanel.FeedMode.TRENDING);
        recentPanel = new StudentFeedPanel(submissionService, hypeService, StudentFeedPanel.FeedMode.RECENT);
        mySubmissionsPanel = new StudentFeedPanel(submissionService, hypeService, StudentFeedPanel.FeedMode.MY_SUBMISSIONS);

        tabbedPane.addTab("🔥 Trending Issues", trendingPanel);
        tabbedPane.addTab("🕒 Recent Submissions", recentPanel);
        tabbedPane.addTab("📂 My Submissions (Anonymity Vault)", mySubmissionsPanel);

        tabbedPane.addChangeListener(e -> refreshAllFeeds());

        root.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void refreshAllFeeds() {
        if (trendingPanel != null) trendingPanel.refresh();
        if (recentPanel != null) recentPanel.refresh();
        if (mySubmissionsPanel != null) mySubmissionsPanel.refresh();
    }

    private void handleLogout() {
        authService.logout();
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
