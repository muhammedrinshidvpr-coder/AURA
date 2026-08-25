package aura.ui.student;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import aura.enums.Priority;
import aura.enums.SubmissionType;
import aura.exception.AuraException;
import aura.service.SubmissionService;

/** Create-submission form. Wires only to SubmissionService. */
public class SubmissionFormPanel extends JDialog {

    private final SubmissionService submissionService = new SubmissionService();

    private final JTextField titleField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(6, 30);
    private final JComboBox<SubmissionType> typeBox = new JComboBox<>(SubmissionType.values());
    private final JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());

    private boolean submitted = false;

    public SubmissionFormPanel(Frame owner) {
        super(owner, "New Submission", true);
        setSize(450, 420);
        setLocationRelativeTo(owner);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeBox);
        formPanel.add(new JLabel("Priority:"));
        formPanel.add(priorityBox);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> handleSubmit());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSubmit() {
        try {
            submissionService.createSubmission(
                titleField.getText().trim(),
                descriptionArea.getText().trim(),
                (SubmissionType) typeBox.getSelectedItem(),
                (Priority) priorityBox.getSelectedItem()
            );
            submitted = true;
            dispose();
        } catch (AuraException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Submission failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Opens the form modally; runs onSuccess only if a submission was actually created. */
    public static void showDialog(Frame owner, Runnable onSuccess) {
        SubmissionFormPanel dialog = new SubmissionFormPanel(owner);
        dialog.setVisible(true);
        if (dialog.submitted) {
            onSuccess.run();
        }
    }
}
