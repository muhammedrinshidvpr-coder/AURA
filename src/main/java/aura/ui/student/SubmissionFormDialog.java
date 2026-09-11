package aura.ui.student;

import aura.enums.Category;
import aura.enums.Priority;
import aura.enums.SubmissionType;
import aura.model.Submission;
import aura.service.SubmissionService;
import aura.ui.common.ModernButton;
import aura.ui.common.UITheme;
import aura.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for anonymous issue and suggestion submission.
 * Enforces Decoupled Anonymity Vault protection.
 */
public class SubmissionFormDialog extends JDialog {
    private final SubmissionService submissionService;
    private final Runnable onSuccessCallback;

    private final JTextField titleField = new JTextField();
    private final JComboBox<SubmissionType> typeCombo = new JComboBox<>(SubmissionType.values());
    private final JComboBox<Category> categoryCombo = new JComboBox<>(Category.values());
    private final JTextField locationField = new JTextField();
    private final JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
    private final JTextArea descriptionArea = new JTextArea(4, 20);

    public SubmissionFormDialog(Frame parent, SubmissionService submissionService, Runnable onSuccessCallback) {
        super(parent, "Report Campus Issue or Suggestion", true);
        this.submissionService = submissionService;
        this.onSuccessCallback = onSuccessCallback;

        setSize(520, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_SURFACE);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Submit Campus Report");
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subLabel = new JLabel("🛡️ Protected by Decoupled Anonymity Vault: No student ID is stored on this ticket.");
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(UITheme.PRIMARY_LIGHT);

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        root.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        formPanel.add(createFieldLabel("Title / Summary:"));
        formPanel.add(styleInput(titleField));
        formPanel.add(Box.createVerticalStrut(10));

        // Row with Type and Category
        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setOpaque(false);
        JPanel typeBox = new JPanel(new BorderLayout(0, 4));
        typeBox.setOpaque(false);
        typeBox.add(createFieldLabel("Type:"), BorderLayout.NORTH);
        typeBox.add(typeCombo, BorderLayout.CENTER);

        JPanel catBox = new JPanel(new BorderLayout(0, 4));
        catBox.setOpaque(false);
        catBox.add(createFieldLabel("Category:"), BorderLayout.NORTH);
        catBox.add(categoryCombo, BorderLayout.CENTER);

        row1.add(typeBox);
        row1.add(catBox);
        formPanel.add(row1);
        formPanel.add(Box.createVerticalStrut(10));

        // Row with Location and Priority
        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setOpaque(false);
        JPanel locBox = new JPanel(new BorderLayout(0, 4));
        locBox.setOpaque(false);
        locBox.add(createFieldLabel("Campus Location:"), BorderLayout.NORTH);
        locBox.add(styleInput(locationField), BorderLayout.CENTER);

        JPanel prioBox = new JPanel(new BorderLayout(0, 4));
        prioBox.setOpaque(false);
        prioBox.add(createFieldLabel("Urgency / Priority:"), BorderLayout.NORTH);
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        prioBox.add(priorityCombo, BorderLayout.CENTER);

        row2.add(locBox);
        row2.add(prioBox);
        formPanel.add(row2);
        formPanel.add(Box.createVerticalStrut(10));

        // Description
        formPanel.add(createFieldLabel("Detailed Description:"));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UITheme.FONT_BODY);
        descriptionArea.setBackground(UITheme.BG_INPUT);
        descriptionArea.setForeground(UITheme.TEXT_MAIN);
        descriptionArea.setCaretColor(Color.WHITE);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(UITheme.createSubtleBorder());
        formPanel.add(descScroll);

        root.add(formPanel, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.Style.GHOST);
        cancelBtn.addActionListener(e -> dispose());

        ModernButton submitBtn = new ModernButton("Submit Anonymously", ModernButton.Style.PRIMARY);
        submitBtn.addActionListener(e -> handleSubmit());

        footer.add(cancelBtn);
        footer.add(submitBtn);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_SMALL_BOLD);
        label.setForeground(UITheme.TEXT_MUTED);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
    }

    private JTextField styleInput(JTextField field) {
        field.setFont(UITheme.FONT_BODY);
        field.setBackground(UITheme.BG_INPUT);
        field.setForeground(UITheme.TEXT_MAIN);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                UITheme.createSubtleBorder(),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }

    private void handleSubmit() {
        String title = titleField.getText().trim();
        String desc = descriptionArea.getText().trim();
        String location = locationField.getText().trim();
        SubmissionType type = (SubmissionType) typeCombo.getSelectedItem();
        Category cat = (Category) categoryCombo.getSelectedItem();
        Priority prio = (Priority) priorityCombo.getSelectedItem();

        try {
            int studentId = SessionContext.isLoggedIn() ? SessionContext.getCurrentUser().getUserId() : 1;
            Submission sub = new Submission(0, title, desc, type, cat, location, prio);
            submissionService.createSubmission(sub, studentId);

            JOptionPane.showMessageDialog(this,
                    "Your report has been submitted anonymously.\nReceipt logged in your private Anonymity Vault.",
                    "Submission Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
