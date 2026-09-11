package aura.ui.admin;

import aura.enums.SubmissionStatus;
import aura.model.Submission;
import aura.model.User;
import aura.service.SubmissionService;
import aura.service.TrackingService;
import aura.ui.common.ModernButton;
import aura.ui.common.StatusBadge;
import aura.ui.common.UITheme;
import aura.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Administrative Triage Table panel with status transition actions and filter controls.
 */
public class AdminTriageTablePanel extends JPanel {
    private final SubmissionService submissionService;
    private final TrackingService trackingService;
    private final Runnable onDataChangedCallback;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final List<Submission> currentSubmissions = new ArrayList<>();
    private SubmissionStatus activeFilter = null; // null = all

    public AdminTriageTablePanel(SubmissionService submissionService, TrackingService trackingService, Runnable onDataChangedCallback) {
        this.submissionService = submissionService;
        this.trackingService = trackingService;
        this.onDataChangedCallback = onDataChangedCallback;

        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 16, 16));

        // --- Filter Buttons Bar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topBar.setOpaque(false);

        topBar.add(createFilterButton("All Submissions", null));
        topBar.add(createFilterButton("Pending Triage", SubmissionStatus.PENDING));
        topBar.add(createFilterButton("In Progress", SubmissionStatus.IN_PROGRESS));
        topBar.add(createFilterButton("Resolved Archive", SubmissionStatus.RESOLVED));

        add(topBar, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columnNames = {"ID", "Title", "Type", "Category", "Location", "Priority", "Hype", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(UITheme.FONT_BODY);
        table.setBackground(UITheme.BG_SURFACE);
        table.setForeground(UITheme.TEXT_MAIN);
        table.setSelectionBackground(UITheme.BG_CARD_HOVER);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(UITheme.FONT_SMALL_BOLD);
        table.getTableHeader().setBackground(UITheme.BG_CARD);
        table.getTableHeader().setForeground(UITheme.TEXT_MUTED);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);

        // Custom Renderer for Status column
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof SubmissionStatus status) {
                    return new StatusBadge(status);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(UITheme.createSubtleBorder());
        scrollPane.getViewport().setBackground(UITheme.BG_SURFACE);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Action Toolbar ---
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionToolbar.setOpaque(false);

        ModernButton approveBtn = new ModernButton("⚡ Start Work (In Progress)", ModernButton.Style.PRIMARY);
        approveBtn.addActionListener(e -> handleStatusChange(SubmissionStatus.IN_PROGRESS));

        ModernButton resolveBtn = new ModernButton("✓ Mark Resolved", ModernButton.Style.SUCCESS);
        resolveBtn.addActionListener(e -> handleStatusChange(SubmissionStatus.RESOLVED));

        ModernButton rejectBtn = new ModernButton("✕ Reject", ModernButton.Style.DANGER);
        rejectBtn.addActionListener(e -> handleStatusChange(SubmissionStatus.REJECTED));

        ModernButton noteBtn = new ModernButton("📝 Resolution Notes", ModernButton.Style.GHOST);
        noteBtn.addActionListener(e -> handleOpenNotes());

        actionToolbar.add(approveBtn);
        actionToolbar.add(resolveBtn);
        actionToolbar.add(rejectBtn);
        actionToolbar.add(noteBtn);

        add(actionToolbar, BorderLayout.SOUTH);

        refreshTable();
    }

    private ModernButton createFilterButton(String text, SubmissionStatus status) {
        ModernButton btn = new ModernButton(text, ModernButton.Style.GHOST);
        btn.setFont(UITheme.FONT_SMALL_BOLD);
        btn.addActionListener(e -> {
            this.activeFilter = status;
            refreshTable();
        });
        return btn;
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        currentSubmissions.clear();

        List<Submission> all = submissionService.getAllSubmissions();
        List<Submission> filtered = all.stream()
                .filter(s -> activeFilter == null || s.getStatus() == activeFilter)
                .collect(Collectors.toList());

        currentSubmissions.addAll(filtered);

        for (Submission s : filtered) {
            tableModel.addRow(new Object[]{
                    "#" + s.getSubmissionId(),
                    s.getTitle(),
                    s.getType(),
                    s.getCategory().getDisplayName(),
                    s.getLocation(),
                    s.getPriority(),
                    "🔥 " + s.getHypeCount(),
                    s.getStatus()
            });
        }
    }

    private Submission getSelectedSubmission() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentSubmissions.size()) {
            JOptionPane.showMessageDialog(this, "Please select an issue row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return currentSubmissions.get(row);
    }

    private void handleStatusChange(SubmissionStatus newStatus) {
        Submission sub = getSelectedSubmission();
        if (sub == null) return;

        User admin = SessionContext.getCurrentUser();
        int adminId = admin != null ? admin.getUserId() : 5;
        String adminName = admin != null ? admin.getName() : "Campus Maintenance";

        trackingService.updateStatus(sub.getSubmissionId(), newStatus, adminId, adminName);
        refreshTable();
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }

        JOptionPane.showMessageDialog(this,
                "Ticket #" + sub.getSubmissionId() + " status transitioned to: " + newStatus.getDisplayName(),
                "Status Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOpenNotes() {
        Submission sub = getSelectedSubmission();
        if (sub == null) return;

        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ResolutionNoteDialog dialog = new ResolutionNoteDialog(topFrame, sub, trackingService, () -> {
            refreshTable();
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });
        dialog.setVisible(true);
    }
}
