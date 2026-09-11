package aura.ui.common;

import aura.enums.SubmissionStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable rounded status pill badge for submissions.
 */
public class StatusBadge extends JLabel {
    private final Color bgColor;
    private final Color fgColor;

    public StatusBadge(SubmissionStatus status) {
        super(status.getDisplayName());
        this.bgColor = status.getBackgroundColor();
        this.fgColor = status.getForegroundColor();

        setFont(UITheme.FONT_SMALL_BOLD);
        setForeground(fgColor);
        setBorder(new EmptyBorder(3, 10, 3, 10));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.setColor(new Color(fgColor.getRed(), fgColor.getGreen(), fgColor.getBlue(), 90));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
