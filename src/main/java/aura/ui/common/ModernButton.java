package aura.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern FlatLaf-styled button with hover states.
 */
public class ModernButton extends JButton {
    public enum Style {
        PRIMARY,
        SUCCESS,
        DANGER,
        GHOST,
        FLAME
    }

    private final Style style;
    private boolean isHovered = false;

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;

        setFont(UITheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(8, 16, 8, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg;
        Color fg = Color.WHITE;

        switch (style) {
            case SUCCESS -> {
                bg = isHovered ? new Color(16, 185, 129) : new Color(16, 185, 129, 210);
            }
            case DANGER -> {
                bg = isHovered ? new Color(239, 68, 68) : new Color(239, 68, 68, 200);
            }
            case FLAME -> {
                bg = isHovered ? new Color(249, 115, 22) : new Color(249, 115, 22, 190);
            }
            case GHOST -> {
                bg = isHovered ? UITheme.BG_CARD_HOVER : UITheme.BG_CARD;
                fg = UITheme.TEXT_MAIN;
            }
            case PRIMARY -> {
                bg = isHovered ? UITheme.PRIMARY_HOVER : UITheme.PRIMARY;
            }
            default -> bg = UITheme.PRIMARY;
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

        if (style == Style.GHOST) {
            g2.setColor(UITheme.BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        }

        g2.dispose();

        setForeground(fg);
        super.paintComponent(g);
    }
}
