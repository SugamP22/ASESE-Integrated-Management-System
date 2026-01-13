package ui.controllers.ftp;

import ui.models.ftp.FileType;
import ui.models.ftp.FtpFileModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This action listener is triggered when the user selects the 'Show information' option in the ftp file option menu.
 * It then opens a new window where details of the file or folder can be seen.
 */
public class ShowFileInformationListener implements ActionListener {
    private JTree tree;

    public ShowFileInformationListener(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof FtpFileModel file
        ) {
            // TODO: Replace with custom component
            var infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            var constrains = new GridBagConstraints();
            constrains.fill = GridBagConstraints.HORIZONTAL;
            constrains.insets = new Insets(0, 5, 5, 5);

            constrains.gridx = 0; constrains.gridy = 0;
            infoPanel.add(new JLabel("Name: "), constrains);
            constrains.gridx = 1; constrains.gridy = 0;
            infoPanel.add(new JLabel(file.fileName), constrains);

            constrains.gridx = 0; constrains.gridy = 1;
            infoPanel.add(new JLabel("Path: "), constrains);
            constrains.gridx = 1; constrains.gridy = 1;
            infoPanel.add(new JLabel(file.filePath), constrains);

            constrains.gridx = 0; constrains.gridy = 2;
            infoPanel.add(new JLabel("Filetype: "), constrains);
            constrains.gridx = 1; constrains.gridy = 2;
            infoPanel.add(new JLabel(file.fileType.toString()), constrains);

            var icon = switch (file.fileType) {
                case FileType.FOLDER -> UIManager.getIcon("FileView.directoryIcon");
                case FileType.FILE -> UIManager.getIcon("FileView.fileIcon");
                case FileType.SYB_LINK -> UIManager.getIcon("FileView.fileIcon");
            };
            var scaledImage = ((ImageIcon) icon).getImage().getScaledInstance(icon.getIconWidth() * 3, icon.getIconHeight() * 3, Image.SCALE_SMOOTH); // TODO: This is garbage
            var optionPanel = new JOptionPane(infoPanel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(scaledImage));

            var dialog = optionPanel.createDialog("File information"); // FIXME: When the 'OK' button is clicked, the dialog is not disposed automatically
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dialog.setContentPane(optionPanel);

            dialog.pack();
            dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(tree));
            dialog.setVisible(true);
        }
    }
}
