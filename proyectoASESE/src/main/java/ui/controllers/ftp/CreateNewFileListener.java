package ui.controllers.ftp;

import ftp.FtpService;
import ftp.FtpUtils;
import ui.models.ftp.FileType;
import ui.models.ftp.FtpFileModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This action listener is triggered when the user selects the 'New file' option in the ftp file option menu
 * If the file could not be created, a popup is shown.
 */
public class CreateNewFileListener implements ActionListener {
    private JTree tree;
    private DefaultTreeModel treeModel;

    public CreateNewFileListener(JTree tree, DefaultTreeModel treeModel) {
        this.tree = tree;
        this.treeModel = treeModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof FtpFileModel file
        ) {
            var rootWindow = SwingUtilities.getWindowAncestor(tree);
            var newFolderName = JOptionPane.showInputDialog(
                    rootWindow,
                    "Enter new file name:",
                    "New file",
                    JOptionPane.PLAIN_MESSAGE);
            if (newFolderName == null) {
                return;
            }

            var newFolderPath = switch (file.fileType) {
                case FOLDER -> file.filePath + "/" + newFolderName;
                default -> FtpUtils.getParentPath(file.filePath) + "/" + newFolderName;
            };

            DefaultMutableTreeNode parentNode;
            if (file.fileType == FileType.FOLDER) {
                parentNode = node;
            } else {
                parentNode = (DefaultMutableTreeNode) node.getParent();
            }

            if (FtpService.createFile(newFolderPath)) {
                treeModel.insertNodeInto(new DefaultMutableTreeNode(new FtpFileModel(
                        newFolderName,
                        newFolderPath,
                        FileType.FILE
                )), parentNode, 0);
                FtpUtils.logEvent(String.format("New empty file '%s' created", newFolderPath));
            } else {
                JOptionPane.showMessageDialog(
                        rootWindow,
                        "Could not create new file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                FtpUtils.logEvent(String.format("Could not create new empty file '%s'", newFolderPath));
            }
        }
    }
}
