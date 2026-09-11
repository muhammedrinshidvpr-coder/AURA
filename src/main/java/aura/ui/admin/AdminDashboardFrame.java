package aura.ui.admin;

import aura.model.Submission;
import aura.model.ResolutionNote;
import aura.model.SubmissionHistory;
import aura.service.ServiceRegistry;
import aura.service.SubmissionService;
import aura.service.AuthService;
import aura.model.User;
import aura.util.SessionContext;
import aura.ui.LoginFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * AdminDashboardFrame — lists all submissions with controls to change status and add resolution notes.
 */
public class AdminDashboardFrame extends JFrame {

    private DefaultTableModel tableModel;
    private SubmissionService svc;
    private final User admin;

    public AdminDashboardFrame() {
        if (!new AuthService().isCurrentUserAdmin()) {
            throw new SecurityException("An authenticated administrator session is required.");
        }
        admin = SessionContext.getCurrentUser();
        svc = ServiceRegistry.getSubmissionService();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("AURA - Admin Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Admin Triage Queue");
        header.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(header, BorderLayout.NORTH);

        String[] cols = new String[]{"ID", "Title", "Status", "Hype"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton changeStatus = new JButton("Change Status");
        JButton addNote = new JButton("Add Resolution Note");
        JButton viewHistory = new JButton("View History");
        JButton logout = new JButton("Logout");
        controls.add(refresh); controls.add(changeStatus); controls.add(addNote); controls.add(viewHistory); controls.add(logout);
        add(controls, BorderLayout.SOUTH);

        refresh.addActionListener((ActionEvent e) -> loadData());
        logout.addActionListener((ActionEvent e) -> {
            new AuthService().logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        changeStatus.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a submission first.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            String[] options = new String[]{"PENDING","ASSIGNED","IN_PROGRESS","RESOLVED","REJECTED"};
            String newStatus = (String) JOptionPane.showInputDialog(this, "Select new status:", "Change Status", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (newStatus != null) {
                try { svc.updateStatus(id, newStatus, admin.getUserId()); loadData(); JOptionPane.showMessageDialog(this, "Status updated.", "Success", JOptionPane.INFORMATION_MESSAGE); }
                catch (RuntimeException exception) { showError(exception); }
            }
        });

        addNote.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a submission first.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            String note = JOptionPane.showInputDialog(this, "Enter resolution note:");
            if (note != null && !note.trim().isEmpty()) {
                try { svc.addResolutionNote(id, admin.getUserId(), note.trim()); JOptionPane.showMessageDialog(this, "Note added.", "Success", JOptionPane.INFORMATION_MESSAGE); }
                catch (RuntimeException exception) { showError(exception); }
            }
        });

        viewHistory.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a submission first.", "Information", JOptionPane.INFORMATION_MESSAGE); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            List<SubmissionHistory> hist = svc.getHistory(id);
            StringBuilder sb = new StringBuilder();
            for (SubmissionHistory h : hist) {
                sb.append(h.getChangedAt()).append(" : ").append(h.getOldStatus()).append(" -> ").append(h.getNewStatus()).append(" by ").append(h.getChangedBy()).append("\n");
            }
            for (ResolutionNote note : svc.findNotesBySubmission(id)) {
                sb.append(note.getCreatedAt()).append(" : note: ").append(note.getNote()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.length()==0?"No history":sb.toString(), "History", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try { for (Submission s : svc.listAllSubmissions()) tableModel.addRow(new Object[]{s.getSubmissionId(), s.getTitle(), s.getStatus(), s.getHypeCount()}); }
        catch (RuntimeException exception) { showError(exception); }
    }

    private void showError(RuntimeException exception) { JOptionPane.showMessageDialog(this, exception.getMessage(), "AURA error", JOptionPane.ERROR_MESSAGE); }
}
