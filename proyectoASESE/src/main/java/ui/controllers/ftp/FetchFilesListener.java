package ui.controllers.ftp;

import authentication.AuthenticationService;
import entities.User;
import ftp.FtpService;
import ftp.FtpUtils;
import ui.models.ftp.FtpFileModel;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import java.util.Objects;

/**
 * This listener fetched all the files for the clicked item in the JTree from the FTP server.
 * It then updates the view.
 */
public class FetchFilesListener implements TreeWillExpandListener {
    private DefaultTreeModel treeModel;

    public FetchFilesListener(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        var treePath = e.getPath();
        if (treePath != null
                && treePath.getLastPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof FtpFileModel file) {

            var currentUser = AuthenticationService.getCurrentUser();
            if (currentUser == null) {
                return;
            }

            if (
                    file.filePath.equals("/ftp") || // Allow all users to read the /ftp folder
                    currentUser.getRol() == User.UserRol.ADMIN || // Allow admins to read all folders
                    Objects.equals(FtpUtils.getUsernameFromPath(file.filePath), currentUser.getFtpUsername()) // Only allow if it's the user's folder
            ) {
                node.removeAllChildren();
                for (var f : FtpService.getFiles(file.filePath)) {
                    node.add(new DefaultMutableTreeNode(f));
                }
                treeModel.reload(node);
            }
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {}
}
