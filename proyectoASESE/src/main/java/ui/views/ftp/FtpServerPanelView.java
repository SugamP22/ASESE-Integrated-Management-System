package ui.views.ftp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel that represents the FTP server section of the user interface.
 * <p>
 * This panel renders a custom rounded background with antialiasing
 * and displays a centered title label. It is intended to act as a
 * visual container or header for FTP-related views.
 */
public class FtpServerPanelView extends JPanel {

    public FtpServerPanelView() {
        setOpaque(false);
        setLayout(new BorderLayout());
        initializePanel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 165, 0, 240));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2d.dispose();
    }

    /**
     * Panel that represents the FTP server section of the user interface.
     * <p>
     * This panel renders a custom rounded background with antialiasing
     * and displays a centered title label. It is intended to act as a
     * visual container or header for FTP-related views.
     */
    private void initializePanel() {
        JLabel titleLabel = new JLabel("FTP Server", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(titleLabel, BorderLayout.CENTER);
    }
}

