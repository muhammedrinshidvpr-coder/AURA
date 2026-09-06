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
        // Sample placeholder submissions (in-memory) so the UI is usable now
        Submission s1 = new Submission(); s1.setSubmissionId(101); s1.setTitle("Broken Lamp in Lab"); s1.setStatus("PENDING"); s1.setHypeCount(5);
        Submission s2 = new Submission(); s2.setSubmissionId(102); s2.setTitle("WiFi Issues in Block B"); s2.setStatus("IN_PROGRESS"); s2.setHypeCount(12);
        submissions.add(s1); submissions.add(s2);
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
            // assign a synthetic ID and add to in-memory list
            int nextId = submissions.stream().mapToInt(Submission::getSubmissionId).max().orElse(100) + 1;
            sub.setSubmissionId(nextId);
            sub.setStatus("PENDING");
            submissions.add(sub);
            refreshTable();
            dlg.dispose();
        });
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
}
