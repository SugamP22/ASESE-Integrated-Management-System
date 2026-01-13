package ui.views.login;

import ui.views.smtp.SmtpUi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple modal progress dialog shown while logging in.
 * Styled to match the existing SMTP "Sending..." dialog.
 */
public class LoginLoadingDialog extends JDialog {

    public LoginLoadingDialog(Window owner) {
        super(owner, "Logging in...", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(12, 12));

        getContentPane().setBackground(SmtpUi.MAIN_BG);
        ((javax.swing.JComponent) getContentPane()).setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Logging in...", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setBorderPainted(false);
        bar.setForeground(SmtpUi.BTN_GREEN);
        bar.setBackground(new Color(255, 255, 255, 40));

        add(title, BorderLayout.NORTH);
        add(bar, BorderLayout.CENTER);

        setSize(360, 140);
        setLocationRelativeTo(getOwner());
    }
}


