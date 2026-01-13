package ui.views.content;

import javax.swing.*;
import java.awt.*;

/**
 * ContentLayerView acts as the application content container.
 * It does NOT use CardLayout. Instead, it exposes a method 
 * setContent(JPanel panel) which removes the current content 
 * and displays the given panel.
 */
public class ContentLayerView extends JPanel {

    public ContentLayerView() {
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    /**
     * Sets the content panel, removing any existing content.
     * 
     * @param panel The panel to display
     */
    public void setContent(JPanel panel) {
        removeAll();
        
        if (panel != null) {
            add(panel, BorderLayout.CENTER);
        }
        
        revalidate();
        repaint();
    }
}

