package aura;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import aura.ui.LoginFrame;

/** Entry point: sets Nimbus Look & Feel, opens LoginFrame. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception e) {
                    // Nimbus unavailable on this platform — fall back to the default L&F.
                }
                break;
            }
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
