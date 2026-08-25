package aura.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import aura.enums.SubmissionStatus;
import aura.exception.AuraException;
import aura.model.Submission;
import aura.service.AdminSubmissionService;
import aura.service.AuthService;
import aura.ui.LoginFrame;

/** Queue, assignment, resolution note entry, dashboard stats. Wires only to AdminSubmissionService. */
public class AdminDashboardFrame extends JFrame {

    private final AdminSubmissionService adminSubmissionService = new AdminSubmissionService();
    private final AuthService authService = new AuthService();

    private final DefaultTableModel queueModel = new QueueTableModel();
    private final JTable queueTable = new JTable(queueModel);
    private final JLabel statsLabel = new JLabel();

    public AdminDashboardFrame() {
        super("AURA — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null);

        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(queueTable), BorderLayout.CENTER);
        add(buildActionBar(), BorderLayout.SOUTH);

        refreshQueue();
    }

    private JPanel buildTopBar() {
        JButton reportsButton = new JButton("Reports");
        reportsButton.addActionListener(e -> new ReportPanel(this).setVisible(true));
        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(e -> logout());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(reportsButton);
        buttons.add(logoutButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(statsLabel, BorderLayout.WEST);
        panel.add(buttons, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildActionBar() {
        JButton assignButton = new JButton("Assign");
        assignButton.addActionListener(e -> withSelectedSubmission(adminSubmissionService::assign));

        JButton inProgressButton = new JButton("Mark In Progress");
        inProgressButton.addActionListener(e ->
            withSelectedSubmission(id -> adminSubmissionService.updateStatus(id, SubmissionStatus.IN_PROGRESS)));

        JButton resolveButton = new JButton("Resolve (with note)");
        resolveButton.addActionListener(e -> withSelectedSubmission(id -> closeWithNote(id, SubmissionStatus.RESOLVED)));

        JButton rejectButton = new JButton("Reject (with note)");
        rejectButton.addActionListener(e -> withSelectedSubmission(id -> closeWithNote(id, SubmissionStatus.REJECTED)));

        JButton sortPriorityButton = new JButton("Sort by Priority");
        sortPriorityButton.addActionListener(e -> populateQueueModel(adminSubmissionService.getQueueSortedByPriority()));

        JButton sortHypeButton = new JButton("Sort by Hype");
        sortHypeButton.addActionListener(e -> populateQueueModel(adminSubmissionService.getQueueSortedByHype()));

        JButton refreshButton = new JButton("Refresh (by Date)");
        refreshButton.addActionListener(e -> refreshQueue());

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(assignButton);
        panel.add(inProgressButton);
        panel.add(resolveButton);
        panel.add(rejectButton);
        panel.add(sortPriorityButton);
        panel.add(sortHypeButton);
        panel.add(refreshButton);
        return panel;
    }

    private void withSelectedSubmission(IntConsumer action) {
        int row = queueTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a submission first.");
            return;
        }
        int submissionId = (int) queueModel.getValueAt(row, 0);
        try {
            action.accept(submissionId);
            refreshQueue();
        } catch (AuraException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Action failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeWithNote(int submissionId, SubmissionStatus finalStatus) {
        String note = JOptionPane.showInputDialog(this, "Note for this submission:");
        if (note == null) {
            return;
        }
        adminSubmissionService.addResolutionNote(submissionId, note);
        adminSubmissionService.updateStatus(submissionId, finalStatus);
    }

    private void refreshQueue() {
        populateQueueModel(adminSubmissionService.getQueue());
        refreshStats();
    }

    private void populateQueueModel(List<Submission> submissions) {
        queueModel.setRowCount(0);
        for (Submission submission : submissions) {
            queueModel.addRow(new Object[]{
                submission.getSubmissionId(), submission.getTitle(), submission.getType(),
                submission.getPriority(), submission.getStatus(), submission.getCreatedAt()
            });
        }
    }

    private void refreshStats() {
        Map<SubmissionStatus, Integer> stats = adminSubmissionService.getDashboardStats();
        StringBuilder text = new StringBuilder("Stats:  ");
        for (SubmissionStatus status : SubmissionStatus.values()) {
            text.append(status).append('=').append(stats.get(status)).append("   ");
        }
        statsLabel.setText(text.toString());
    }

    private void logout() {
        authService.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }

    private static final class QueueTableModel extends DefaultTableModel {
        QueueTableModel() {
            super(new Object[]{"ID", "Title", "Type", "Priority", "Status", "Created"}, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
