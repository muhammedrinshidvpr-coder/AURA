package aura.ui.admin;

import aura.model.Submission;
import aura.model.User;
import aura.service.AuthService;
import aura.service.ReportService;
import aura.service.SubmissionService;
import aura.service.TrackingService;
import aura.ui.LoginFrame;
import aura.ui.common.ModernButton;
import aura.ui.common.StatCard;
import aura.ui.common.UITheme;
import aura.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

/**
 * Main Administrator Governance Console window.
 * Adheres to Slide 23 mockup of docs/AURA.pdf.
 */
public class AdminDashboardFrame extends JFrame {
    private final SubmissionService submissionService;
    private final TrackingService trackingService;
    private final ReportService reportService;
    private final AuthService authService;

    private StatCard pendingCard;
    private StatCard activeCard;
    private StatCard topHypeCard;
    private AdminTriageTablePanel triagePanel;

    public AdminDashboardFrame() {
        this(new SubmissionService(), new TrackingService(), new ReportService(), new AuthService());
    }

    public AdminDashboardFrame(SubmissionService submissionService, TrackingService trackingService,
                               ReportService reportService, AuthService authService) {
        super("AURA — Admin Governance Console | TKMCE");
        this.submissionService = submissionService;
        this.trackingService = trackingService;
        this.reportService = reportService;
        this.authService = authService;

        setSize(1040, 760);
        setMinimumSize(new Dimension(900, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_BASE);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_BASE);

        // --- Top Navigation Header ---
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(UITheme.BG_SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("⚡ AURA");
        logoLabel.setFont(UITheme.FONT_HEADER);
        logoLabel.setForeground(UITheme.PRIMARY_LIGHT);

        JLabel subTitleLabel = new JLabel("Administrative Governance Console");
        subTitleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        subTitleLabel.setForeground(UITheme.TEXT_MUTED);

        brandPanel.add(logoLabel);
        brandPanel.add(subTitleLabel);
        header.add(brandPanel, BorderLayout.WEST);

        // User Pill and Logout
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);

        User admin = SessionContext.getCurrentUser();
        String adminName = admin != null ? admin.getName() : "Campus Maintenance";
        JLabel adminPill = new JLabel("🛠️ " + adminName);
        adminPill.setFont(UITheme.FONT_BODY_BOLD);
        adminPill.setForeground(UITheme.TEXT_MAIN);
        adminPill.setBorder(BorderFactory.createCompoundBorder(
                UITheme.createSubtleBorder(),
                new EmptyBorder(6, 12, 6, 12)
        ));

        ModernButton refreshBtn = new ModernButton("↻ Refresh", ModernButton.Style.GHOST);
        refreshBtn.addActionListener(e -> refreshAllData());

        ModernButton logoutBtn = new ModernButton("Logout", ModernButton.Style.GHOST);
        logoutBtn.addActionListener(e -> handleLogout());

        rightActions.add(adminPill);
        rightActions.add(refreshBtn);
        rightActions.add(logoutBtn);
        header.add(rightActions, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // --- Center Content: KPI Row + Tabs ---
        JPanel centerContent = new JPanel(new BorderLayout(0, 14));
        centerContent.setOpaque(false);
        centerContent.setBorder(new EmptyBorder(16, 16, 16, 16));

        // KPI Row (3 StatCards)
        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        kpiGrid.setOpaque(false);

        pendingCard = new StatCard("⏳", "0", "Pending Review", new Color(245, 158, 11));
        activeCard = new StatCard("⚡", "0", "Active / In Progress", UITheme.PRIMARY);
        topHypeCard = new StatCard("🔥", "None", "Top Hyped Issue", UITheme.ACCENT_FLAME);

        kpiGrid.add(pendingCard);
        kpiGrid.add(activeCard);
        kpiGrid.add(topHypeCard);

        centerContent.add(kpiGrid, BorderLayout.NORTH);

        // Tabs: Triage Queue vs Reports
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);

        triagePanel = new AdminTriageTablePanel(submissionService, trackingService, this::refreshKpiMetrics);
        ReportExportPanel reportPanel = new ReportExportPanel(reportService);

        tabbedPane.addTab("📋 Triage & Action Queue", triagePanel);
        tabbedPane.addTab("📊 Reports & Analytics", reportPanel);

        centerContent.add(tabbedPane, BorderLayout.CENTER);
        root.add(centerContent, BorderLayout.CENTER);

        setContentPane(root);
        refreshKpiMetrics();
    }

    public void refreshAllData() {
        refreshKpiMetrics();
        if (triagePanel != null) {
            triagePanel.refreshTable();
        }
    }

    public void refreshKpiMetrics() {
        pendingCard.setValue(String.valueOf(reportService.getPendingCount()));
        activeCard.setValue(String.valueOf(reportService.getActiveCount()));

        Optional<Submission> top = reportService.getTopHypedSubmission();
        if (top.isPresent()) {
            Submission s = top.get();
            String title = s.getTitle();
            if (title.length() > 22) {
                title = title.substring(0, 19) + "...";
            }
            topHypeCard.setValue("🔥 " + s.getHypeCount() + " (" + title + ")");
        } else {
            topHypeCard.setValue("None");
        }
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
