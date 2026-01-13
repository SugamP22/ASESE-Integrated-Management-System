package ui.controllers.ftp;

import ftp.FtpService;
import ftp.FtpUtils;
import ui.models.ftp.FileType;
import ui.models.ftp.FtpFileModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This action listener is triggered when the user selects the 'Download file' option in the ftp file option menu
 * If the file could not be downloaded an error popup is shown.
 */
public class DownloadFileListener implements ActionListener {
    private JTree tree;
    private JFileChooser fileChooser;

    public DownloadFileListener(JTree tree) {
        this.tree = tree;
        this.fileChooser = new JFileChooser();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof FtpFileModel file
        ) {
            var root = SwingUtilities.getWindowAncestor(tree);

            if (file.fileType != FileType.FILE) {
                JOptionPane.showMessageDialog(
                        root,
                        "Can only download files, directories are not supported",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            fileChooser.setSelectedFile(new File(file.fileName));
            if (fileChooser.showSaveDialog(root) == JFileChooser.APPROVE_OPTION) {
                var outputFile = fileChooser.getSelectedFile();
                var rootWindow = SwingUtilities.getWindowAncestor(tree);

                try (var outputStream = new FileOutputStream(outputFile)) {
                    if (!FtpService.retrieveFile(file.filePath, outputStream)) {
                        throw new IOException("Could not retrieve file from FTP");
                    }
                } catch (IOException ex) {
                    FtpUtils.logEvent(String.format("Could not download file '%s' from FTP", file.filePath));
                    JOptionPane.showMessageDialog(
                            rootWindow,
                            "Could not download file",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                FtpUtils.logEvent(String.format("File '%s' was successfully downloaded from FTP", file.filePath));
                JOptionPane.showMessageDialog(
                        rootWindow,
                        "File downloaded successfully and saved at '" + outputFile.getPath() + "'",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
}
