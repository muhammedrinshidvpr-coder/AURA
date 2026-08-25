package aura.ui.student;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import aura.exception.AuraException;
import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.service.AuthService;
import aura.service.HypeService;
import aura.service.SubmissionService;
import aura.service.TrackingService;
import aura.ui.LoginFrame;

/** Trending/Recent list + My Submissions tab. Wires only to the service layer. */
public class StudentDashboardFrame extends JFrame {

    private final SubmissionService submissionService = new SubmissionService();
    private final TrackingService trackingService = new TrackingService();
    private final HypeService hypeService = new HypeService();
    private final AuthService authService = new AuthService();

    private final DefaultTableModel trendingModel = createBrowseTableModel();
    private final DefaultTableModel recentModel = createBrowseTableModel();
    private final DefaultTableModel myModel = createMyTableModel();

    private final JTable trendingTable = new JTable(trendingModel);
    private final JTable recentTable = new JTable(recentModel);
    private final JTable myTable = new JTable(myModel);

    public StudentDashboardFrame() {
        super("AURA — Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Trending", buildBrowsePanel(trendingTable, trendingModel, this::refreshTrending));
        tabs.addTab("Recent", buildBrowsePanel(recentTable, recentModel, this::refreshRecent));
        tabs.addTab("My Submissions", buildMyPanel());

        add(buildTopBar(), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel buildTopBar() {
        JButton newSubmissionButton = new JButton("New Submission");
        newSubmissionButton.addActionListener(e -> SubmissionFormPanel.showDialog(this, this::refreshAll));
        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(e -> logout());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(newSubmissionButton);
        panel.add(logoutButton);
        return panel;
    }

    private JPanel buildBrowsePanel(JTable table, DefaultTableModel model, Runnable refresh) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton hypeButton = new JButton("Hype Selected");
        hypeButton.addActionListener(e -> hypeSelected(table, model));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh.run());

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(hypeButton);
        buttons.add(refreshButton);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(myTable), BorderLayout.CENTER);

        JButton viewNotesButton = new JButton("View Resolution Notes");
        viewNotesButton.addActionListener(e -> viewSelectedNotes());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshMy());

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(viewNotesButton);
        buttons.add(refreshButton);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void hypeSelected(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a submission first.");
            return;
        }
        int submissionId = (int) model.getValueAt(row, 0);
        try {
            hypeService.addHype(submissionId);
            refreshAll();
        } catch (AuraException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Hype failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelectedNotes() {
        int row = myTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a submission first.");
            return;
        }
        int submissionId = (int) myModel.getValueAt(row, 0);
        try {
            List<ResolutionNote> notes = trackingService.getResolutionNotes(submissionId);
            if (notes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No resolution note yet.");
                return;
            }
            StringBuilder text = new StringBuilder();
            for (ResolutionNote note : notes) {
                text.append(note.getNote()).append(System.lineSeparator());
            }
            JOptionPane.showMessageDialog(this, text.toString(), "Resolution Notes", JOptionPane.INFORMATION_MESSAGE);
        } catch (AuraException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAll() {
        refreshTrending();
        refreshRecent();
        refreshMy();
    }

    private void refreshTrending() {
        populateBrowseModel(trendingModel, submissionService.getTrending());
    }

    private void refreshRecent() {
        populateBrowseModel(recentModel, submissionService.getRecent());
    }

    private void refreshMy() {
        myModel.setRowCount(0);
        for (Submission submission : trackingService.findMySubmissions()) {
            myModel.addRow(new Object[]{
                submission.getSubmissionId(), submission.getTitle(), submission.getType(),
                submission.getPriority(), submission.getStatus(), submission.getCreatedAt()
            });
        }
    }

    private void populateBrowseModel(DefaultTableModel model, List<Submission> submissions) {
        model.setRowCount(0);
        for (Submission submission : submissions) {
            int hypeCount = hypeService.getHypeCount(submission.getSubmissionId());
            model.addRow(new Object[]{
                submission.getSubmissionId(), submission.getTitle(), submission.getType(),
                submission.getPriority(), submission.getStatus(), hypeCount
            });
        }
    }

    private void logout() {
        authService.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }

    private static DefaultTableModel createBrowseTableModel() {
        return new NonEditableTableModel(new Object[]{"ID", "Title", "Type", "Priority", "Status", "Hype"});
    }

    private static DefaultTableModel createMyTableModel() {
        return new NonEditableTableModel(new Object[]{"ID", "Title", "Type", "Priority", "Status", "Created"});
    }

    private static final class NonEditableTableModel extends DefaultTableModel {
        NonEditableTableModel(Object[] columnNames) {
            super(columnNames, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
