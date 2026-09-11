package aura.ui.admin;

import aura.service.ReportService;
import aura.ui.common.ModernButton;
import aura.ui.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;

/**
 * Administrative panel for generating compliance reports and exporting CSV data.
 */
public class ReportExportPanel extends JPanel {
    private final ReportService reportService;

    public ReportExportPanel(ReportService reportService) {
        this.reportService = reportService;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
    }

    private void initUI() {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.createCardBorder());

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("📊 Campus Governance Reporting & Analytics");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel desc = new JLabel("Export full submission lifecycle logs and triage metrics compliant with university reporting standards.");
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(UITheme.TEXT_MUTED);

        header.add(title);
        header.add(desc);
        card.add(header, BorderLayout.NORTH);

        // Center Stats Breakdown
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        statsGrid.setOpaque(false);

        statsGrid.add(createMiniStat("Pending Triage:", String.valueOf(reportService.getPendingCount()), UITheme.BORDER_COLOR));
        statsGrid.add(createMiniStat("Active & In Progress:", String.valueOf(reportService.getActiveCount()), UITheme.PRIMARY));
        statsGrid.add(createMiniStat("Resolved Issues:", String.valueOf(reportService.getResolvedCount()), new Color(16, 185, 129)));
        statsGrid.add(createMiniStat("Rejected Submissions:", String.valueOf(reportService.getRejectedCount()), new Color(239, 68, 68)));

        card.add(statsGrid, BorderLayout.CENTER);

        // Bottom Action
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottom.setOpaque(false);

        ModernButton exportBtn = new ModernButton("💾 Export Full CSV Report", ModernButton.Style.PRIMARY);
        exportBtn.addActionListener(e -> handleExportCsv());
        bottom.add(exportBtn);

        card.add(bottom, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);
    }

    private JPanel createMiniStat(String label, String value, Color accent) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(UITheme.BG_SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                UITheme.createSubtleBorder(),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL_BOLD);
        lbl.setForeground(UITheme.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_KPI_VAL);
        val.setForeground(accent);

        panel.add(lbl, BorderLayout.WEST);
        panel.add(val, BorderLayout.EAST);
        return panel;
    }

    private void handleExportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("aura_campus_report.csv"));
        int choice = chooser.showSaveDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(target)) {
                writer.write(reportService.generateCsvReport());
                JOptionPane.showMessageDialog(this,
                        "Report successfully saved to:\n" + target.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export report: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
