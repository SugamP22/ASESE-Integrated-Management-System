package ui.views.ftp;

import authentication.AuthenticationService;
import ftp.FtpService;
import ftp.FtpUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Main FTP view panel that combines the file tree and file detail views.
 * <p>
 * This panel is responsible for handling the FTP login process for the
 * currently authenticated user and initializing the UI components used
 * to browse and preview files on the FTP server.
 */
public class FtpView extends JPanel {
    // Components
    private FtpFileTreeView fileTreeView;
    private FtpFileView fileView;

    public FtpView() {
        // Login with current user
        if (AuthenticationService.isLogged()) {
            try {
                var currentUser = AuthenticationService.getCurrentUser();
                assert currentUser != null; // This should not be necessary because we are using isLogged()
                if(!FtpService.login(currentUser.getFtpUsername(), currentUser.getPassword())) {
                    JOptionPane.showMessageDialog(
                            SwingUtilities.getWindowAncestor(this),
                            "Could not log into the FTP server",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    FtpUtils.logEvent(String.format("User could not log in as '%s'", currentUser.getFtpUsername()));
                } else {
                    FtpService.cd("/ftp");
                    FtpUtils.logEvent(String.format("User logged in as '%s'", currentUser.getFtpUsername()));
                }
            } catch (RuntimeException ex) {
                // Don't crash app navigation if FTP is down/unreachable.
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Could not connect to FTP server",
                        "FTP Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        createComponents();
        addComponents();
    }

    /**
     * Initializes the child components of this view, including
     * the file tree and the file detail view.
     */
    private void createComponents() {
        fileView = new FtpFileView();
        fileTreeView = new FtpFileTreeView(fileView);
    }

    /**
     * Adds and lays out all child components within this panel.
     * <p>
     * The file tree is placed on the left inside a scroll pane,
     * while the file detail view occupies the central area.
     */
    private void addComponents() {
        setLayout(new BorderLayout());

        // Add fileTreeView
        var scrollPanel = new JScrollPane(fileTreeView);
        scrollPanel.setPreferredSize(new Dimension(350, 0));
        add(scrollPanel, BorderLayout.WEST);

        // Add file view
        add(fileView, BorderLayout.CENTER);
    }
}