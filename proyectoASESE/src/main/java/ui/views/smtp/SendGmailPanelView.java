package ui.views.smtp;

import ui.controllers.smtp.EmailController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A specialized panel designed to host the Gmail/IMAP interface within a larger UI container.
 * This class handles the aesthetic rendering of the panel, including anti-aliased
 * rounded corners and background overlays, while delegating email logic to the
 * {@link EmailController} in embedded mode.
 */
public class SendGmailPanelView extends JPanel {

    /**
     * Constructs a new SendGmailPanelView.
     * Configures the layout, transparency, and padding before triggering
     * the internal component initialization.
     */
    public SendGmailPanelView() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializePanel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Match Project Management tab base color (teal) with a subtle overlay.
        Color base = SmtpUi.MAIN_BG;
        g2d.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 245));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2d.dispose();
    }

    private void initializePanel() {
        JPanel embedded = new JPanel(new BorderLayout());
        embedded.setOpaque(false);

        // Main IMAP UI inside the tab (no standalone window required).
        ImapMainPanelView imapPanel = new ImapMainPanelView();
        embedded.add(imapPanel, BorderLayout.CENTER);
        add(embedded, BorderLayout.CENTER);

        // Reuse the same controller logic, but start it in embedded mode.
        EmailController controller = new EmailController();
        controller.startEmbedded(imapPanel);
    }
}