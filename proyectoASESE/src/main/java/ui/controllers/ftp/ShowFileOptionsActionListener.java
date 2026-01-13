package ui.controllers.ftp;

import ui.models.ftp.FtpFileModel;
import ui.views.ftp.FtpFileOptionMenu;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This listener is triggered when the user right-clicks an item of the JTree.
 * It then shows a JPopupMenu with all the options that can executed on a file.
 */
public class ShowFileOptionsActionListener extends MouseAdapter {
    private JTree tree;
    private FtpFileOptionMenu optionsMenu;

    public ShowFileOptionsActionListener(JTree tree, FtpFileOptionMenu optionsMenu) {
        this.tree = tree;
        this.optionsMenu = optionsMenu;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            var treePath = tree.getPathForLocation(e.getX(), e.getY());
            if (treePath != null
                    && treePath.getLastPathComponent() instanceof DefaultMutableTreeNode node
                    && node.getUserObject() instanceof FtpFileModel) {
                tree.addSelectionPath(treePath);
                optionsMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
