package aura.ui.student;

import aura.model.Submission;
import aura.model.ResolutionNote;
import aura.model.SubmissionHistory;
import aura.model.User;
import aura.service.AuthService;
import aura.service.HypeService;
import aura.service.ServiceRegistry;
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
    private final HypeService hypeService = new HypeService();

    public StudentDashboardFrame(User user) {
        if (user == null || new AuthService().isCurrentUserAdmin() || !new AuthService().isCurrentUser(user)) {
            throw new SecurityException("An authenticated student session is required.");
        }
        this.user = user;
        initUI();
        loadSubmissions();
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
        JButton refresh = new JButton("Refresh");
        JButton hype = new JButton("Hype Selected");
        JButton viewDetails = new JButton("View Details");
        bottom.add(newSubmission);
        bottom.add(refresh);
        bottom.add(hype);
        bottom.add(viewDetails);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        logoutBtn.addActionListener((ActionEvent e) -> {
            new AuthService().logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        newSubmission.addActionListener((ActionEvent e) -> openSubmissionDialog());
        refresh.addActionListener((ActionEvent e) -> loadSubmissions());
        hype.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a submission first.", "Information", JOptionPane.INFORMATION_MESSAGE); return; }
            try { hypeService.addHype((int) tableModel.getValueAt(row, 0), user.getUserId()); loadSubmissions(); JOptionPane.showMessageDialog(this, "Hype added.", "Success", JOptionPane.INFORMATION_MESSAGE); }
            catch (RuntimeException exception) { showError(exception); }
        });
        viewDetails.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a submission first.", "Information", JOptionPane.INFORMATION_MESSAGE); return; }
            showDetails((int) tableModel.getValueAt(row, 0));
        });
    }

    private void loadSubmissions() {
        aura.service.SubmissionService svc = ServiceRegistry.getSubmissionService();
        submissions.clear();
        try { submissions.addAll(svc.listSubmissionsByStudent(user.getUserId())); refreshTable(); }
        catch (RuntimeException exception) { showError(exception); }
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
            try { svc.createSubmission(sub, user.getUserId()); loadSubmissions(); dlg.dispose(); JOptionPane.showMessageDialog(this, "Submission created.", "Success", JOptionPane.INFORMATION_MESSAGE); }
            catch (RuntimeException exception) { showError(exception); }
        });
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void showError(RuntimeException exception) { JOptionPane.showMessageDialog(this, exception.getMessage(), "AURA error", JOptionPane.ERROR_MESSAGE); }

    private void showDetails(int submissionId) {
        aura.service.SubmissionService service = ServiceRegistry.getSubmissionService();
        StringBuilder details = new StringBuilder();
        for (SubmissionHistory item : service.getHistory(submissionId)) details.append(item.getChangedAt()).append(" : ").append(item.getOldStatus()).append(" -> ").append(item.getNewStatus()).append("\n");
        for (ResolutionNote note : service.findNotesBySubmission(submissionId)) details.append(note.getCreatedAt()).append(" : ").append(note.getNote()).append("\n");
        JOptionPane.showMessageDialog(this, details.length() == 0 ? "No resolution updates yet." : details.toString(), "Submission updates", JOptionPane.INFORMATION_MESSAGE);
    }
}
