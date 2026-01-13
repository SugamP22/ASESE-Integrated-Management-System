package ui.views.ftp;

import org.kordamp.ikonli.boxicons.BoxiconsRegular;
import org.kordamp.ikonli.swing.FontIcon;
import ui.controllers.ftp.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

/**
 * Popup menu that provides file and folder operations for an FTP file tree.
 * <p>
 * This menu is intended to be used as a contextual (right-click) menu
 * associated with a {@link JTree} that represents files and directories
 * from an FTP server. It offers actions such as delete, rename, create,
 * upload, download, and view file information.
 */
public class FtpFileOptionMenu extends JPopupMenu {
    private static final FontIcon removeIcon = FontIcon.of(BoxiconsRegular.TRASH, 18);
    private static final FontIcon infoIcon = FontIcon.of(BoxiconsRegular.INFO_CIRCLE, 18);
    private static final FontIcon newFolderIcon = FontIcon.of(BoxiconsRegular.FOLDER_PLUS, 18);
    private static final FontIcon newFileIcon = FontIcon.of(BoxiconsRegular.PLUS_CIRCLE, 18);
    private static final FontIcon renameIcon = FontIcon.of(BoxiconsRegular.RENAME, 18);
    private static final FontIcon downloadIcon = FontIcon.of(BoxiconsRegular.DOWNLOAD, 18);
    private static final FontIcon uploadIcon = FontIcon.of(BoxiconsRegular.UPLOAD, 18);

    private JTree tree;
    private DefaultTreeModel treeModel;

    // Components
    private JMenuItem removeOption;
    private JMenuItem showInfoMenu;
    private JMenuItem newFolderOption;
    private JMenuItem newFileOption;
    private JMenuItem renameOption;
    private JMenuItem uploadNewFileOption;
    private JMenuItem downloadFileOption;

    public FtpFileOptionMenu(JTree tree, DefaultTreeModel treeModel) {
        this.tree = tree;
        this.treeModel = treeModel;

        createComponents();
        addComponents();
    }

    /**
     * Initializes and configures all menu items used by this popup menu.
     *
     * Each menu item is created, assigned an icon, and linked to its
     * corresponding action listener.
     */
    private void createComponents() {
        // Remove option
        removeOption = new JMenuItem("Delete");
        removeOption.addActionListener(new DeleteFileListener(tree, treeModel));
        removeOption.setIcon(removeIcon);

        // Show file's information option
        showInfoMenu = new JMenuItem("Show information");
        showInfoMenu.addActionListener(new ShowFileInformationListener(tree));
        showInfoMenu.setIcon(infoIcon);

        // Create new folder
        newFolderOption = new JMenuItem("New folder");
        newFolderOption.addActionListener(new CreateNewFolderListener(tree, treeModel));
        newFolderOption.setIcon(newFolderIcon);

        // Create new file
        newFileOption = new JMenuItem("New file");
        newFileOption.addActionListener(new CreateNewFileListener(tree, treeModel));
        newFileOption.setIcon(newFileIcon);

        // Rename file
        renameOption = new JMenuItem("Rename");
        renameOption.addActionListener(new RenameFileListener(tree, treeModel));
        renameOption.setIcon(renameIcon);

        // Upload new file
        uploadNewFileOption = new JMenuItem("Upload new file");
        uploadNewFileOption.addActionListener(new UploadNewFileListener(tree, treeModel));
        uploadNewFileOption.setIcon(uploadIcon);

        // Download new file
        downloadFileOption = new JMenuItem("Download file");
        downloadFileOption.addActionListener(new DownloadFileListener(tree));
        downloadFileOption.setIcon(downloadIcon);
    }

    /**
     * Adds all previously created menu items to this popup menu
     * in the appropriate order.
     */
    private void addComponents() {
        add(removeOption);
        add(showInfoMenu);
        add(newFolderOption);
        add(newFileOption);
        add(renameOption);
        add(uploadNewFileOption);
        add(downloadFileOption);
    }
}
