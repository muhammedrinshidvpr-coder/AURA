package aura.ui.student;

import aura.model.Submission;
import aura.service.HypeService;
import aura.service.SubmissionService;
import aura.ui.common.ModernButton;
import aura.ui.common.StatusBadge;
import aura.ui.common.UITheme;
import aura.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scrollable card feed of campus submissions for students.
 * Supports Trending, Recent, and My Submissions (Anonymity Vault) views.
 */
public class StudentFeedPanel extends JPanel {
    public enum FeedMode {
        TRENDING,
        RECENT,
        MY_SUBMISSIONS
    }

    private final SubmissionService submissionService;
    private final HypeService hypeService;
    private final FeedMode mode;
    private final JPanel cardsContainer;

    public StudentFeedPanel(SubmissionService submissionService, HypeService hypeService, FeedMode mode) {
        this.submissionService = submissionService;
        this.hypeService = hypeService;
        this.mode = mode;

        setLayout(new BorderLayout());
        setOpaque(false);

        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(new EmptyBorder(12, 16, 20, 16));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        cardsContainer.removeAll();

        int currentStudentId = SessionContext.isLoggedIn() ? SessionContext.getCurrentUser().getUserId() : 1;
        List<Submission> list;

        switch (mode) {
            case TRENDING -> list = submissionService.getTrendingSubmissions();
            case MY_SUBMISSIONS -> list = submissionService.getStudentSubmissions(currentStudentId);
            case RECENT -> list = submissionService.getRecentSubmissions();
            default -> list = submissionService.getAllSubmissions();
        }

        if (list.isEmpty()) {
            cardsContainer.add(createEmptyState());
        } else {
            for (Submission sub : list) {
                cardsContainer.add(createSubmissionCard(sub, currentStudentId));
                cardsContainer.add(Box.createVerticalStrut(12));
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createSubmissionCard(Submission sub, int currentStudentId) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.createCardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Left Hype Button Panel
        JPanel hypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        hypePanel.setOpaque(false);
        hypePanel.setPreferredSize(new Dimension(65, 80));

        boolean hasHyped = hypeService.hasStudentHyped(sub.getSubmissionId(), currentStudentId);
        ModernButton hypeBtn = new ModernButton("🔥 " + sub.getHypeCount(),
                hasHyped ? ModernButton.Style.FLAME : ModernButton.Style.GHOST);
        hypeBtn.setFont(UITheme.FONT_MONO);
        hypeBtn.setToolTipText(hasHyped ? "Click to remove hype" : "Click to hype this issue!");
        hypeBtn.addActionListener(e -> {
            boolean isNowHyped = hypeService.toggleHype(sub.getSubmissionId(), currentStudentId);
            hypeBtn.setText("🔥 " + hypeService.getHypeCount(sub.getSubmissionId()));
            refresh();
        });
        hypePanel.add(hypeBtn);
        card.add(hypePanel, BorderLayout.WEST);

        // Center Content Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Meta row: Category pill + Location + Relative time
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        metaRow.setOpaque(false);

        JLabel catLabel = new JLabel(sub.getCategory().getIcon() + " " + sub.getCategory().getDisplayName());
        catLabel.setFont(UITheme.FONT_SMALL_BOLD);
        catLabel.setForeground(UITheme.PRIMARY_LIGHT);

        JLabel locLabel = new JLabel("📍 " + sub.getLocation());
        locLabel.setFont(UITheme.FONT_SMALL);
        locLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel timeLabel = new JLabel("• " + formatRelativeTime(sub.getCreatedAt()));
        timeLabel.setFont(UITheme.FONT_SMALL);
        timeLabel.setForeground(UITheme.TEXT_SUBTLE);

        metaRow.add(catLabel);
        metaRow.add(locLabel);
        metaRow.add(timeLabel);
        centerPanel.add(metaRow);
        centerPanel.add(Box.createVerticalStrut(4));

        // Title
        JLabel titleLabel = new JLabel(sub.getTitle());
        titleLabel.setFont(UITheme.FONT_SUBHEADER);
        titleLabel.setForeground(UITheme.TEXT_MAIN);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(4));

        // Description preview
        String desc = sub.getDescription();
        if (desc.length() > 100) {
            desc = desc.substring(0, 97) + "...";
        }
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(UITheme.FONT_BODY);
        descLabel.setForeground(UITheme.TEXT_MUTED);
        centerPanel.add(descLabel);

        card.add(centerPanel, BorderLayout.CENTER);

        // Right Status Pill Panel
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        rightPanel.setOpaque(false);
        rightPanel.add(new StatusBadge(sub.getStatus()));
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel icon = new JLabel("📭", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel(mode == FeedMode.MY_SUBMISSIONS ?
                "You haven't submitted any tickets yet." : "No submissions found.");
        text.setFont(UITheme.FONT_BODY_BOLD);
        text.setForeground(UITheme.TEXT_MUTED);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(10));
        panel.add(text);
        return panel;
    }

    private String formatRelativeTime(LocalDateTime dt) {
        if (dt == null) return "Just now";
        Duration dur = Duration.between(dt, LocalDateTime.now());
        long hours = dur.toHours();
        if (hours < 1) {
            long minutes = Math.max(1, dur.toMinutes());
            return minutes + "m ago";
        } else if (hours < 24) {
            return hours + "h ago";
        } else {
            long days = dur.toDays();
            return days + "d ago";
        }
    }
}
