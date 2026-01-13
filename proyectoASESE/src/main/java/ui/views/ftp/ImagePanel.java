package ui.views.ftp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Panel specialized in displaying an {@link Image} scaled to fit its size.
 * <p>
 * The image is automatically redrawn and resized whenever the panel
 * is resized.
 */
class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
    }

    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Paints the panel and draws the current image scaled to the panel size.
     *
     * @param g the {@link Graphics} context used for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Resize the image to fit the panel size
        if (image != null) {
            int width = getWidth();
            int height = getHeight();
            g.drawImage(image, 0, 0, width, height, this);
        }
    }
}