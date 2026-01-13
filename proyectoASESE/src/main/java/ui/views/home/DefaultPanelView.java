package ui.views.home;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

/**
 * Home "welcome" panel shown inside the user dashboard.
 * Renders a short tagline plus the application logo.
 */
public class DefaultPanelView extends JPanel {

    public DefaultPanelView() {
        setOpaque(false);
        setLayout(new BorderLayout());
        initializePanel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        java.awt.GradientPaint gradient = new java.awt.GradientPaint(
                0, 0, new Color(45, 55, 72, 240),
                0, getHeight(), new Color(35, 45, 60, 250)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2d.setColor(new Color(255, 215, 0, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        g2d.dispose();
    }

    private void initializePanel() {
        JLabel welcomeLabel = getJLabel();

        add(welcomeLabel, BorderLayout.NORTH);

        JPanel imagePanel = new JPanel() {
            private Image image;

            {
                try {
                    ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/logoPepeLink.png")));
                    image = icon.getImage();
                } catch (Exception e) {
                    image = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (image == null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    String text = "Logo not found";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(text, x, y);
                    g2d.dispose();
                    return;
                }

                int padding = 10;
                int availableWidth = getWidth() - (2 * padding);
                int availableHeight = getHeight() - (2 * padding);
                int imgWidth = image.getWidth(this);
                int imgHeight = image.getHeight(this);

                if (imgWidth <= 0 || imgHeight <= 0) {
                    return;
                }

                double scaleX = (double) availableWidth / imgWidth;
                double scaleY = (double) availableHeight / imgHeight;
                double scale = Math.min(scaleX, scaleY);
                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);
                int x = padding + (availableWidth - scaledWidth) / 2;
                int y = padding + (availableHeight - scaledHeight) / 2;
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(image, x, y, scaledWidth, scaledHeight, this);
                g2d.dispose();
            }
        };
        imagePanel.setOpaque(false);

        add(imagePanel, BorderLayout.CENTER);
    }

    private static JLabel getJLabel() {
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'><b style='font-size: 1.2em; color: #FFD700;'>Where Innovation Begins</b><br/><span style='font-size: 0.75em; font-weight: normal; color: #E0E0E0;'>Empowering Tomorrow's Solutions Today</span></div></html>", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Calibri", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(255, 255, 255));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return welcomeLabel;
    }
}

