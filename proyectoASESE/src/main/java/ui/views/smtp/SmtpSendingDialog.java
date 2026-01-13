package ui.views.smtp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple modal progress dialog shown while sending an email.
 * Blocks interaction with the rest of the app until the send finishes.
 */
public class SmtpSendingDialog extends JDialog {

    public SmtpSendingDialog(Window owner) {
        super(owner, "Sending...", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(12, 12));

        getContentPane().setBackground(SmtpUi.MAIN_BG);
        ((javax.swing.JComponent) getContentPane()).setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Sending email...", SwingConstants.CENTER);
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


