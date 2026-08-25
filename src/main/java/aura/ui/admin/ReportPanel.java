package aura.ui.admin;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import aura.exception.AuraException;
import aura.service.ReportService;

/** Triggers the CSV export via ReportService (FR-11). */
public class ReportPanel extends JDialog {

    private final ReportService reportService = new ReportService();

    public ReportPanel(Frame owner) {
        super(owner, "Export Report", true);
        setSize(360, 160);
        setLocationRelativeTo(owner);

        JLabel infoLabel = new JLabel("<html><center>Export all submissions and resolutions as CSV.<br>"
            + "No submitting student's identity is ever included.</center></html>", SwingConstants.CENTER);
        JButton exportButton = new JButton("Choose File & Export");
        exportButton.addActionListener(e -> exportReport());

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(infoLabel, BorderLayout.CENTER);
        panel.add(exportButton, BorderLayout.SOUTH);
        add(panel);
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("aura_report.csv"));
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            reportService.exportCsv(fileChooser.getSelectedFile().toPath());
            JOptionPane.showMessageDialog(this, "Report exported successfully.");
            dispose();
        } catch (AuraException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Export failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
