package aura.ui.common;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Centralized FlatLaf UI design tokens and theme constants for AURA.
 * Matches the approved AURA Tech Dark design aesthetics.
 */
public final class UITheme {
    // --- Color Palette ---
    public static final Color BG_BASE = new Color(0x0B, 0x0F, 0x17);
    public static final Color BG_SURFACE = new Color(0x11, 0x18, 0x27);
    public static final Color BG_CARD = new Color(0x1F, 0x29, 0x37);
    public static final Color BG_CARD_HOVER = new Color(0x26, 0x33, 0x45);
    public static final Color BG_INPUT = new Color(0x16, 0x1F, 0x30);

    public static final Color BORDER_COLOR = new Color(0x37, 0x41, 0x51);
    public static final Color BORDER_SUBTLE = new Color(0x1E, 0x29, 0x3B);

    public static final Color PRIMARY = new Color(0x63, 0x66, 0xF1);
    public static final Color PRIMARY_HOVER = new Color(0x4F, 0x46, 0xE5);
    public static final Color PRIMARY_LIGHT = new Color(0x81, 0x8C, 0xF8);

    public static final Color ACCENT_FLAME = new Color(0xF9, 0x73, 0x16);
    public static final Color ACCENT_FLAME_BG = new Color(249, 115, 22, 35);

    public static final Color TEXT_MAIN = new Color(0xF3, 0xF4, 0xF6);
    public static final Color TEXT_MUTED = new Color(0x9C, 0xA3, 0xAF);
    public static final Color TEXT_SUBTLE = new Color(0x6B, 0x72, 0x80);

    // --- Typography ---
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.BOLD, 13);
    public static final Font FONT_KPI_VAL = new Font("Consolas", Font.BOLD, 22);

    private UITheme() {}

    /**
     * Initializes FlatDarkLaf look and feel and customizes Swing UIManager defaults.
     */
    public static void initLookAndFeel() {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("TabbedPane.showTabSeparators", false);
            UIManager.put("TabbedPane.selectedBackground", PRIMARY);
            UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
        } catch (Exception e) {
            System.err.println("Warning: Could not initialize FlatLaf look and feel: " + e.getMessage());
        }
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_SUBTLE, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        );
    }

    public static Border createSubtleBorder() {
        return new LineBorder(BORDER_SUBTLE, 1, true);
    }
}
