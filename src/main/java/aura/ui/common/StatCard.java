package aura.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern KPI statistic card for Admin and Student overview panels.
 */
public class StatCard extends JPanel {
    private final JLabel valueLabel;
    private final JLabel titleLabel;

    public StatCard(String icon, String initialValue, String label, Color accentColor) {
        setLayout(new BorderLayout(14, 0));
        setBackground(UITheme.BG_CARD);
        setBorder(UITheme.createCardBorder());

        // Left Icon Box
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setPreferredSize(new Dimension(42, 42));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setOpaque(false);

        // Right Text Info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(UITheme.FONT_KPI_VAL);
        valueLabel.setForeground(UITheme.TEXT_MAIN);

        titleLabel = new JLabel(label);
        titleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(UITheme.TEXT_MUTED);

        infoPanel.add(valueLabel);
        infoPanel.add(titleLabel);

        add(iconLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
