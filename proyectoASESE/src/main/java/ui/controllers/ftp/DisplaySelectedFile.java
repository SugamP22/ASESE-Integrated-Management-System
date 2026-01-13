package ui.controllers.ftp;

import ui.models.ftp.FtpFileModel;
import ui.views.ftp.FtpFileView;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This action listener is triggered when the user click twice a file in the file tree. It is then displayed in the left
 * panel.
 */
public class DisplaySelectedFile extends MouseAdapter {
    private JTree tree;
    private FtpFileView fileView;

    public DisplaySelectedFile(JTree tree, FtpFileView fileView) {
        this.tree = tree;
        this.fileView = fileView;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            var treePath = tree.getPathForLocation(e.getX(), e.getY());
            if (treePath != null
                    && treePath.getLastPathComponent() instanceof DefaultMutableTreeNode node
                    && node.getUserObject() instanceof FtpFileModel file) {
                tree.addSelectionPath(treePath);
                fileView.setFile(file);
            }
        }
    }
}
