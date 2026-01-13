package ui.controllers.ftp;

import ftp.FtpService;
import ftp.FtpUtils;
import ui.models.ftp.FtpFileModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This action listener is triggered when the user selects the 'Rename file' option in the ftp file option menu.
 * If the file could not be renamed a popup is shown.
 */
public class RenameFileListener implements ActionListener {
    private JTree tree;
    private DefaultTreeModel treeModel;

    public RenameFileListener(JTree tree, DefaultTreeModel treeModel) {
        this.tree = tree;
        this.treeModel = treeModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof FtpFileModel file
        ) {
            var rootWindow = SwingUtilities.getWindowAncestor(tree);
            var newFileName = JOptionPane.showInputDialog(
                    rootWindow,
                    "New name:",
                    "Rename file",
                    JOptionPane.PLAIN_MESSAGE);

            var newFile = new FtpFileModel(
                    newFileName,
                    FtpUtils.getParentPath(file.filePath) + "/" + newFileName,
                    file.fileType
            );

            if (FtpService.renameFile(file.filePath, newFile.filePath)) {
                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(newFile),
                        (DefaultMutableTreeNode) node.getParent(),
                        0
                );
                treeModel.removeNodeFromParent(node);
                FtpUtils.logEvent(String.format("Renamed file '%s' to '%s'", file.filePath, newFile.filePath));
            } else {
                JOptionPane.showMessageDialog(
                        rootWindow,
                        "Could not rename file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                FtpUtils.logEvent(String.format("Could not rename file '%s' to '%s'", file.filePath, newFile.filePath));
            }
        }
    }
}
