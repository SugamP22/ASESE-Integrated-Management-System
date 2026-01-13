package ui.views.ftp;

import ui.controllers.ftp.DisplaySelectedFile;
import ui.controllers.ftp.FetchFilesListener;
import ui.controllers.ftp.ShowFileOptionsActionListener;
import ui.models.ftp.FileType;
import ui.models.ftp.FtpFileModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

/**
 * Panel that displays an FTP file tree and handles user interaction with it.
 * <p>
 * This view encapsulates a {@link JTree} configured to represent the
 * directory structure of an FTP server. It integrates custom rendering,
 * context menus, and listeners to support file navigation and actions.
 */

public class FtpFileTreeView extends JPanel {
    private FtpFileView fileView;

    // Components
    private JTree tree;

    public FtpFileTreeView(FtpFileView fileView) {
        this.fileView = fileView;
        createComponents();
        addComponents();
    }

    /**
     * Initializes and configures all UI components of this view.
     * <p>
     * This includes creating the FTP file tree, setting its model,
     * renderer, selection behavior, context menu, and registering
     * all required listeners.
     */
    private void createComponents() {
        // JTree
        var root = new DefaultMutableTreeNode(new FtpFileModel("/ftp", "/ftp", FileType.FOLDER), true);
        var treeModel = new DefaultTreeModel(root, true);
        tree = new JTree(root);
        tree.setModel(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); // Allow only selection of one item
        tree.setCellRenderer(new FtpTreeCellRenderer());
        ToolTipManager.sharedInstance().registerComponent(tree);

        // JPopupMenu
        var optionsMenu = new FtpFileOptionMenu(tree, treeModel);

        // Add listeners to JTree
        tree.addMouseListener(new ShowFileOptionsActionListener(tree, optionsMenu));
        tree.addMouseListener(new DisplaySelectedFile(tree, fileView));
        tree.addTreeWillExpandListener(new FetchFilesListener(treeModel));
        tree.collapseRow(0); // Needed to keep the root node collapsed and not have to click it twice
    }

    /**
     * Adds the configured components to this panel and sets the layout.
     */
    private void addComponents() {
        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }
}
