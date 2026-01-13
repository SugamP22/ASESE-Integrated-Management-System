package ui.views.ftp;

import org.kordamp.ikonli.boxicons.BoxiconsRegular;
import org.kordamp.ikonli.swing.FontIcon;
import ui.models.ftp.FileType;
import ui.models.ftp.FtpFileModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Objects;

/**
 * Custom tree cell renderer for displaying FTP files and folders in a {@link JTree}.
 * <p>
 * This renderer customizes the appearance of tree nodes by setting
 * a specific font, icon, displayed text, and tooltip based on the
 * associated {@link FtpFileModel}. Folders and files are visually
 * distinguished using different icons.
 */
public class FtpTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final Font textFont = new Font("", Font.PLAIN, 18);
    private static final FontIcon folderIcon = FontIcon.of(BoxiconsRegular.FOLDER, 18);
    private static final FontIcon fileIcon = FontIcon.of(BoxiconsRegular.FILE, 18);

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        this.setFont(textFont);

        if (Objects.requireNonNull(value) instanceof DefaultMutableTreeNode node) {
            if (node.getUserObject() instanceof FtpFileModel file) {
                setText(file.fileName);
                setToolTipText(file.filePath);
                if (file.fileType == FileType.FOLDER) {
                    setIcon(folderIcon);
                } else {
                    setIcon(fileIcon);
                }
            }
        }

        return this;
    }
}
