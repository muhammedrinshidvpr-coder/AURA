package aura.ui.student;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;

/**
 * SubmissionFormPanel — placeholder for the student submission form panel
 * Assigned to: Rahandeep RD (B25CS053)
 */
public class SubmissionFormPanel extends JPanel {

    private JTextField titleField;
    private JTextArea descArea;
    private JComboBox<String> categoryCombo;
    private JButton submitBtn;
    public SubmissionFormPanel(java.util.function.Consumer<aura.model.Submission> onSubmit) {
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.insets = new java.awt.Insets(4,4,4,4);
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Title:"), c);
        c.gridx = 1; c.gridy = 0; titleField = new JTextField(30); form.add(titleField, c);
        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Category:"), c);
        c.gridx = 1; c.gridy = 1; categoryCombo = new JComboBox<>(new String[]{"GENERAL","IT_INFRASTRUCTURE","ELECTRICAL","CIVIL_MAINTENANCE","ACADEMIC_LABS","HOSTEL_MESS"}); form.add(categoryCombo, c);
        c.gridx = 0; c.gridy = 2; c.anchor = java.awt.GridBagConstraints.NORTH; form.add(new JLabel("Description:"), c);        c.gridx = 1; c.gridy = 2; descArea = new JTextArea(6, 30); JScrollPane sp = new JScrollPane(descArea); form.add(sp, c);
        add(form, BorderLayout.CENTER);
        submitBtn = new JButton("Submit");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(submitBtn);
        add(bottom, BorderLayout.SOUTH);
        submitBtn.addActionListener((java.awt.event.ActionEvent e) -> {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            if (title.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide title and description.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            aura.model.Submission s = new aura.model.Submission();
            s.setTitle(title);
            s.setDescription(desc);
            s.setHypeCount(0);
            if (onSubmit != null) onSubmit.accept(s);
        });
    }
}
