package aura.ui.student;

import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.service.HypeService;
import aura.service.SubmissionService;
import aura.service.TrackingService;
import aura.ui.common.ModernButton;
import aura.ui.common.StatusBadge;
import aura.ui.common.UITheme;
import aura.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * AURA STUDENT FEED PANEL — ACCORDION PROGRESSIVE DISCLOSURE FEED
 * ============================================================================
 * Scrollable card feed of campus submissions for students.
 * Supports Trending, Recent, and My Submissions (Anonymity Vault) views.
 * 
 * Key UI Innovations:
 * 1. Progressive Disclosure: Cards display minimal vital data by default
 *    (Title, Category, Status, Hype, Time).
 * 2. Inline Accordion Expansion: Clicking anywhere on a card smoothly toggles
 *    extended details (Full Description, Exact Location, Resolution Notes).
 * 3. Modern Minimalist White Styling: Pure white cards with subtle slate borders
 *    and soft hover feedback.
 * ============================================================================
 */
public class StudentFeedPanel extends JPanel {
    public enum FeedMode {
        TRENDING,
        RECENT,
        MY_SUBMISSIONS
    }

    private final SubmissionService submissionService;
    private final HypeService hypeService;
    private final TrackingService trackingService;
    private final FeedMode mode;
    private final JPanel cardsContainer;

    public StudentFeedPanel(SubmissionService submissionService, HypeService hypeService, FeedMode mode) {
        this(submissionService, hypeService, new TrackingService(), mode);
    }

    public StudentFeedPanel(SubmissionService submissionService, HypeService hypeService,
                            TrackingService trackingService, FeedMode mode) {
        this.submissionService = submissionService;
        this.hypeService = hypeService;
        this.trackingService = trackingService;
        this.mode = mode;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_BASE);
        setOpaque(true);

        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(UITheme.BG_BASE);
        cardsContainer.setOpaque(true);
        cardsContainer.setBorder(new EmptyBorder(16, 20, 24, 20));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UITheme.BG_BASE);
        scrollPane.getViewport().setBackground(UITheme.BG_BASE);
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
                cardsContainer.add(Box.createVerticalStrut(10));
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    /**
     * Builds an inline-expandable problem card.
     * Collapsed: Title, Category pill, Status, Hype button, Time, Chevron.
     * Expanded: Full description, Location, Priority tag, Resolution note.
     */
    private JPanel createSubmissionCard(Submission sub, int currentStudentId) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.createCardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86)); // Compact collapsed default
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- 1. Summary Header (Always Visible) ---
        JPanel summaryRow = new JPanel(new BorderLayout(14, 0));
        summaryRow.setOpaque(false);

        // Left Hype Button Panel
        JPanel hypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        hypePanel.setOpaque(false);
        hypePanel.setPreferredSize(new Dimension(72, 48));

        boolean hasHyped = hypeService.hasStudentHyped(sub.getSubmissionId(), currentStudentId);
        ModernButton hypeBtn = new ModernButton("🔥 " + sub.getHypeCount(),
                hasHyped ? ModernButton.Style.FLAME : ModernButton.Style.GHOST);
        hypeBtn.setFont(UITheme.FONT_MONO);
        hypeBtn.setToolTipText(hasHyped ? "Click to remove upvote" : "Click to upvote this campus issue!");
        hypeBtn.addActionListener(e -> {
            boolean isNowHyped = hypeService.toggleHype(sub.getSubmissionId(), currentStudentId);
            hypeBtn.setText("🔥 " + hypeService.getHypeCount(sub.getSubmissionId()));
            refresh();
        });
        hypePanel.add(hypeBtn);
        summaryRow.add(hypePanel, BorderLayout.WEST);

        // Center Title & Category
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Meta row: Category pill + Time + Chevron
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        metaRow.setOpaque(false);

        JLabel catLabel = new JLabel(sub.getCategory().getIcon() + " " + sub.getCategory().getDisplayName());
        catLabel.setFont(UITheme.FONT_SMALL_BOLD);
        catLabel.setForeground(UITheme.PRIMARY);

        JLabel timeLabel = new JLabel("• " + formatRelativeTime(sub.getCreatedAt()));
        timeLabel.setFont(UITheme.FONT_SMALL);
        timeLabel.setForeground(UITheme.TEXT_SUBTLE);

        JLabel idLabel = new JLabel("#" + sub.getSubmissionId());
        idLabel.setFont(UITheme.FONT_SMALL);
        idLabel.setForeground(UITheme.TEXT_SUBTLE);

        metaRow.add(catLabel);
        metaRow.add(timeLabel);
        metaRow.add(idLabel);
        centerPanel.add(metaRow);
        centerPanel.add(Box.createVerticalStrut(2));

        // Title
        JLabel titleLabel = new JLabel(sub.getTitle());
        titleLabel.setFont(UITheme.FONT_SUBHEADER);
        titleLabel.setForeground(UITheme.TEXT_MAIN);
        centerPanel.add(titleLabel);

        summaryRow.add(centerPanel, BorderLayout.CENTER);

        // Right Status Pill Panel & Chevron
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        rightPanel.setOpaque(false);

        JLabel chevronLabel = new JLabel("▼");
        chevronLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chevronLabel.setForeground(UITheme.TEXT_SUBTLE);

        rightPanel.add(new StatusBadge(sub.getStatus()));
        rightPanel.add(chevronLabel);
        summaryRow.add(rightPanel, BorderLayout.EAST);

        card.add(summaryRow);

        // --- 2. Expandable Details Panel (Hidden by default) ---
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);
        detailPanel.setVisible(false); // Initially collapsed
        detailPanel.setBorder(new EmptyBorder(12, 8, 4, 8));

        // Separator
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(UITheme.BORDER_SUBTLE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        detailPanel.add(sep);
        detailPanel.add(Box.createVerticalStrut(10));

        // Problem Description
        JLabel descHeader = new JLabel("Problem Description:");
        descHeader.setFont(UITheme.FONT_SMALL_BOLD);
        descHeader.setForeground(UITheme.TEXT_MUTED);
        detailPanel.add(descHeader);
        detailPanel.add(Box.createVerticalStrut(3));

        JTextArea descArea = new JTextArea(sub.getDescription());
        descArea.setFont(UITheme.FONT_BODY);
        descArea.setForeground(UITheme.TEXT_MAIN);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(null);
        detailPanel.add(descArea);
        detailPanel.add(Box.createVerticalStrut(10));

        // Meta Grid: Location & Priority
        JPanel detailMetaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        detailMetaRow.setOpaque(false);

        JLabel locLabel = new JLabel("📍 Location: " + sub.getLocation());
        locLabel.setFont(UITheme.FONT_BODY);
        locLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel prioLabel = new JLabel("⚡ Priority: " + sub.getPriority().name());
        prioLabel.setFont(UITheme.FONT_BODY_BOLD);
        prioLabel.setForeground(UITheme.TEXT_MUTED);

        detailMetaRow.add(locLabel);
        detailMetaRow.add(prioLabel);
        detailPanel.add(detailMetaRow);

        // Official Resolution Note (fetched via TrackingService if present)
        try {
            List<ResolutionNote> notes = trackingService.getResolutionNotes(sub.getSubmissionId());
            if (notes != null && !notes.isEmpty()) {
                ResolutionNote latest = notes.get(notes.size() - 1);
                detailPanel.add(Box.createVerticalStrut(10));
                JPanel resPanel = new JPanel(new BorderLayout(8, 4));
                resPanel.setBackground(new Color(0xDC, 0xFC, 0xE7)); // Green 100
                resPanel.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(0x16, 0xA3, 0x4A), 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));

                String adminInfo = latest.getAdminName() != null ? " (" + latest.getAdminName() + ")" : "";
                JLabel resTitle = new JLabel("✓ Official University Resolution" + adminInfo + ":");
                resTitle.setFont(UITheme.FONT_SMALL_BOLD);
                resTitle.setForeground(new Color(0x15, 0x80, 0x3D));

                JLabel resContent = new JLabel("<html>" + latest.getNote() + "</html>");
                resContent.setFont(UITheme.FONT_BODY);
                resContent.setForeground(new Color(0x14, 0x53, 0x2D));

                resPanel.add(resTitle, BorderLayout.NORTH);
                resPanel.add(resContent, BorderLayout.CENTER);
                detailPanel.add(resPanel);
            }
        } catch (Exception ignored) {}

        // Anonymity Vault Receipt Note
        detailPanel.add(Box.createVerticalStrut(8));
        JLabel vaultNotice = new JLabel("🛡️ Anonymity Vault Protected — Zero PII attached to ticket #" + sub.getSubmissionId());
        vaultNotice.setFont(UITheme.FONT_SMALL);
        vaultNotice.setForeground(UITheme.PRIMARY);
        detailPanel.add(vaultNotice);

        card.add(detailPanel);

        // --- 3. Click Listener for Inline Accordion Toggle ---
        MouseAdapter cardClickToggle = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean willExpand = !detailPanel.isVisible();
                detailPanel.setVisible(willExpand);
                chevronLabel.setText(willExpand ? "▲" : "▼");
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, willExpand ? 340 : 86));
                card.revalidate();
                card.repaint();
                cardsContainer.revalidate();
                cardsContainer.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(UITheme.BG_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(UITheme.BG_CARD);
            }
        };

        card.addMouseListener(cardClickToggle);
        summaryRow.addMouseListener(cardClickToggle);
        centerPanel.addMouseListener(cardClickToggle);
        titleLabel.addMouseListener(cardClickToggle);

        return card;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(60, 20, 60, 20));

        JLabel icon = new JLabel("📭", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel(mode == FeedMode.MY_SUBMISSIONS ?
                "You haven't submitted any tickets yet." : "No campus reports found in this category.");
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
