package aura.ui.admin;

import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.model.SubmissionHistory;
import aura.service.TrackingService;
import aura.ui.common.ModernButton;
import aura.ui.common.UITheme;
import aura.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modal dialog for viewing audit history and appending official resolution notes.
 */
public class ResolutionNoteDialog extends JDialog {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    private final Submission submission;
    private final TrackingService trackingService;
    private final Runnable onUpdatedCallback;

    private final JPanel historyListPanel = new JPanel();
    private final JTextArea noteInputArea = new JTextArea(3, 20);

    public ResolutionNoteDialog(Frame parent, Submission submission, TrackingService trackingService, Runnable onUpdatedCallback) {
        super(parent, "Resolution Notes & Audit History — Ticket #" + submission.getSubmissionId(), true);
        this.submission = submission;
        this.trackingService = trackingService;
        this.onUpdatedCallback = onUpdatedCallback;

        setSize(600, 620);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BG_SURFACE);

        initUI();
        refreshHistory();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Info
        JPanel header = new JPanel(new GridLayout(3, 1, 0, 4));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("#" + submission.getSubmissionId() + " — " + submission.getTitle());
        titleLabel.setFont(UITheme.FONT_SUBHEADER);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subMeta = new JLabel("Category: " + submission.getCategory().getDisplayName() +
                "  |  Location: " + submission.getLocation() + "  |  Status: " + submission.getStatus());
        subMeta.setFont(UITheme.FONT_SMALL);
        subMeta.setForeground(UITheme.TEXT_MUTED);

        header.add(titleLabel);
        header.add(subMeta);
        header.add(new JSeparator());
        root.add(header, BorderLayout.NORTH);

        // Center: Scrollable Notes & History
        historyListPanel.setLayout(new BoxLayout(historyListPanel, BoxLayout.Y_AXIS));
        historyListPanel.setOpaque(false);
        historyListPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(historyListPanel);
        scroll.setBorder(UITheme.createSubtleBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);

        // Bottom: New Note Input + Actions
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);

        JLabel addNoteLabel = new JLabel("Add Official Resolution Note:");
        addNoteLabel.setFont(UITheme.FONT_SMALL_BOLD);
        addNoteLabel.setForeground(UITheme.TEXT_MUTED);
        bottomPanel.add(addNoteLabel, BorderLayout.NORTH);

        noteInputArea.setLineWrap(true);
        noteInputArea.setWrapStyleWord(true);
        noteInputArea.setFont(UITheme.FONT_BODY);
        noteInputArea.setBackground(UITheme.BG_INPUT);
        noteInputArea.setForeground(UITheme.TEXT_MAIN);
        noteInputArea.setCaretColor(Color.WHITE);

        JScrollPane noteScroll = new JScrollPane(noteInputArea);
        noteScroll.setBorder(UITheme.createSubtleBorder());
        bottomPanel.add(noteScroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        ModernButton closeBtn = new ModernButton("Close", ModernButton.Style.GHOST);
        closeBtn.addActionListener(e -> dispose());

        ModernButton saveBtn = new ModernButton("Post Resolution Note", ModernButton.Style.PRIMARY);
        saveBtn.addActionListener(e -> handleAddNote());

        btnRow.add(closeBtn);
        btnRow.add(saveBtn);
        bottomPanel.add(btnRow, BorderLayout.SOUTH);

        root.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void refreshHistory() {
        historyListPanel.removeAll();

        List<ResolutionNote> notes = trackingService.getResolutionNotes(submission.getSubmissionId());
        List<SubmissionHistory> histories = trackingService.getSubmissionHistory(submission.getSubmissionId());

        if (notes.isEmpty() && histories.isEmpty()) {
            JLabel empty = new JLabel("No notes or state changes recorded yet.");
            empty.setFont(UITheme.FONT_SMALL);
            empty.setForeground(UITheme.TEXT_SUBTLE);
            historyListPanel.add(empty);
        } else {
            // Notes section
            if (!notes.isEmpty()) {
                JLabel notesHeader = new JLabel("📝 Official Notes (" + notes.size() + ")");
                notesHeader.setFont(UITheme.FONT_SMALL_BOLD);
                notesHeader.setForeground(UITheme.PRIMARY_LIGHT);
                historyListPanel.add(notesHeader);
                historyListPanel.add(Box.createVerticalStrut(6));

                for (ResolutionNote n : notes) {
                    JPanel item = new JPanel(new BorderLayout(0, 4));
                    item.setBackground(UITheme.BG_CARD);
                    item.setBorder(UITheme.createCardBorder());

                    JLabel author = new JLabel("By " + n.getAdminName() + " • " + n.getCreatedAt().format(DATE_FMT));
                    author.setFont(UITheme.FONT_SMALL_BOLD);
                    author.setForeground(UITheme.TEXT_MUTED);

                    JLabel text = new JLabel("<html>" + n.getNote().replace("\n", "<br>") + "</html>");
                    text.setFont(UITheme.FONT_BODY);
                    text.setForeground(UITheme.TEXT_MAIN);

                    item.add(author, BorderLayout.NORTH);
                    item.add(text, BorderLayout.CENTER);

                    historyListPanel.add(item);
                    historyListPanel.add(Box.createVerticalStrut(8));
                }
            }

            // History section
            if (!histories.isEmpty()) {
                JLabel histHeader = new JLabel("🔄 State Transition Audit Trail (" + histories.size() + ")");
                histHeader.setFont(UITheme.FONT_SMALL_BOLD);
                histHeader.setForeground(UITheme.PRIMARY_LIGHT);
                historyListPanel.add(histHeader);
                historyListPanel.add(Box.createVerticalStrut(6));

                for (SubmissionHistory h : histories) {
                    JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
                    item.setOpaque(false);

                    JLabel text = new JLabel(h.getOldStatus() + " ➔ " + h.getNewStatus() +
                            " (by " + h.getChangedByName() + ") • " + h.getChangedAt().format(DATE_FMT));
                    text.setFont(UITheme.FONT_SMALL);
                    text.setForeground(UITheme.TEXT_MUTED);
                    item.add(text);

                    historyListPanel.add(item);
                }
            }
        }

        historyListPanel.revalidate();
        historyListPanel.repaint();
    }

    private void handleAddNote() {
        String note = noteInputArea.getText().trim();
        if (note.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a resolution note before posting.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int adminId = SessionContext.isLoggedIn() ? SessionContext.getCurrentUser().getUserId() : 5;
        String adminName = SessionContext.isLoggedIn() ? SessionContext.getCurrentUser().getName() : "Campus Maintenance";

        trackingService.addResolutionNote(submission.getSubmissionId(), adminId, adminName, note);
        noteInputArea.setText("");
        refreshHistory();

        if (onUpdatedCallback != null) {
            onUpdatedCallback.run();
        }
    }
}
