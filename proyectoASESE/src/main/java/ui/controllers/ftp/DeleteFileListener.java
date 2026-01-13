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
 * This action listener is triggered when the user selects the 'Delete file' option in the ftp file option menu
 * If the folder or file could not be created, a popup is shown.
 */
public class DeleteFileListener implements ActionListener {
    private JTree tree;
    private DefaultTreeModel treeModel;

    public DeleteFileListener(JTree tree, DefaultTreeModel treeModel) {
        this.tree = tree;
        this.treeModel = treeModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof FtpFileModel file
        ) {
            switch (file.fileType) {
                case FileType.FILE -> {
                    FtpService.deleteFile(file.filePath);
                    treeModel.removeNodeFromParent(node);
                    FtpUtils.logEvent(String.format("Deleted file '%s'", file.filePath));
                }
                case FileType.FOLDER -> {
                    FtpService.deleteFolderRecursive(file.filePath);
                    treeModel.removeNodeFromParent(node);
                    FtpUtils.logEvent(String.format("Deleted folder '%s'", file.filePath));
                }
                default -> throw new RuntimeException("TODO: Could not delete unsupported file type");
            }
        }
    }
}
