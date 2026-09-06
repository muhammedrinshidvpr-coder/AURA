package aura.ui.student;

import aura.model.Submission;
import aura.model.User;
import aura.ui.LoginFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * StudentDashboardFrame — minimal UI skeleton for student dashboard
 * Assigned to: Rahandeep RD (B25CS053)
 *
 * This is a placeholder Swing Frame that will be replaced with FlatLaf-styled UI.
 */
public class StudentDashboardFrame extends JFrame {

    private User user;
    private DefaultTableModel tableModel;
    private List<Submission> submissions = new ArrayList<>();

    public StudentDashboardFrame(User user) {
        this.user = user;
        initUI();
        loadSampleData();
    }

    private void initUI() {
        setTitle("AURA - Student Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top toolbar with welcome and logout
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JLabel welcome = new JLabel("Welcome, " + (user != null ? user.getName() : "Student"));
        topBar.add(welcome, BorderLayout.WEST);
        JButton logoutBtn = new JButton("Logout");
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Table of submissions
        String[] cols = new String[]{"ID", "Title", "Status", "Hype"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Bottom controls: New Submission button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newSubmission = new JButton("New Submission");
        bottom.add(newSubmission);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        logoutBtn.addActionListener((ActionEvent e) -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        newSubmission.addActionListener((ActionEvent e) -> openSubmissionDialog());
    }

    private void loadSampleData() {
        // Load from the SubmissionService (in-memory service for now)
        aura.service.SubmissionService svc = aura.service.ServiceRegistry.getSubmissionService();
        submissions.clear();
        submissions.addAll(svc.listAllSubmissions());
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Submission s : submissions) {
            tableModel.addRow(new Object[]{s.getSubmissionId(), s.getTitle(), s.getStatus(), s.getHypeCount()});
        }
    }

    private void openSubmissionDialog() {
        JDialog dlg = new JDialog(this, "New Submission", true);
        SubmissionFormPanel panel = new SubmissionFormPanel((Submission sub) -> {
            aura.service.SubmissionService svc = aura.service.ServiceRegistry.getSubmissionService();
            Submission created = svc.createSubmission(sub, user != null ? user.getUserId() : -1);
            // reload list from service and refresh table
            submissions.clear();
            submissions.addAll(svc.listAllSubmissions());
            refreshTable();
            dlg.dispose();
        });
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
}
