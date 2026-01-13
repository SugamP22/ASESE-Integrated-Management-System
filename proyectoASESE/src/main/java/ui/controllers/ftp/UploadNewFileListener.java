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
 * This action listener is triggered when the user selects the 'Upload new file' option in the ftp file option menu
 * If the file could not be uploaded a popup is shown.
 */
public class UploadNewFileListener implements ActionListener {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JFileChooser fileChooser;

    public UploadNewFileListener(JTree tree, DefaultTreeModel treeModel) {
        this.tree = tree;
        this.treeModel = treeModel;
        this.fileChooser = new JFileChooser();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof FtpFileModel file
        ) {
            var root = SwingUtilities.getWindowAncestor(tree);
            if (fileChooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
                var selectedFile = fileChooser.getSelectedFile();
                var rootWindow = SwingUtilities.getWindowAncestor(tree);

                var destinationPath = switch (file.fileType) {
                    case FOLDER -> file.filePath;
                    default -> FtpUtils.getParentPath(file.filePath);
                } + "/" + selectedFile.getName();
                var parentNode = (DefaultMutableTreeNode) switch (file.fileType) {
                    case FOLDER -> node;
                    default -> node.getParent();
                };
                var destinationFile = new FtpFileModel(
                        selectedFile.getName(),
                        destinationPath,
                        FileType.FILE
                );

                var ok = FtpService.uploadFile(destinationPath, selectedFile);
                if (ok) {
                    treeModel.insertNodeInto(new DefaultMutableTreeNode(destinationFile), parentNode, 0);
                    JOptionPane.showMessageDialog(
                            rootWindow,
                            "File uploaded successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    FtpUtils.logEvent(String.format("Uploaded file '%s'", file.filePath));
                } else {
                    JOptionPane.showMessageDialog(
                            rootWindow,
                            "Could not upload file",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    FtpUtils.logEvent(String.format("Could not upload file '%s'", file.filePath));
                }
            }
        }
    }
}
