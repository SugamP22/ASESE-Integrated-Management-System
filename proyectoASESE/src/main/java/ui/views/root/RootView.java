package ui.views.root;

import ui.views.content.ContentLayerView;

import javax.swing.*;
import java.awt.*;

/**
 * RootView is responsible only for drawing a background image
 * and holding a single child panel (ContentLayerView).
 */
public class RootView extends JPanel {

    private static Image backgroundImage;
    private ContentLayerView contentLayerView;

    public RootView() {
        setLayout(new BorderLayout());
        loadBackgroundImage();
        createContentLayer();
    }

    private void loadBackgroundImage() {
        if (backgroundImage == null) {
            java.net.URL imageURL = getClass().getResource("/img/imagenFondo.jpg");
            if (imageURL != null) {
                backgroundImage = new ImageIcon(imageURL).getImage();
            } else {
                System.err.println("Image not found: make sure it's in src/main/resources/");
            }
        }
    }

    private void createContentLayer() {
        contentLayerView = new ContentLayerView();
        add(contentLayerView, BorderLayout.CENTER);
    }

    /**
     * Returns the single child content layer hosted by this root view.
     * Controllers use this to access and swap the main application content.
     */
    public ContentLayerView getContentLayerView() {
        return contentLayerView;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
