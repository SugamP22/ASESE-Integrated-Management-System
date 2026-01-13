package ui.controllers.user;

import ui.controllers.root.RootController;

import javax.swing.*;

/**
 * Controller for user dashboard
 */
public class UserController {

    private final RootController rootController;
    private final JTabbedPane tabbedPane;
    private static final int LOGOUT_TAB_INDEX = 4;

    public UserController(RootController rootController, JTabbedPane tabbedPane) {
        this.rootController = rootController;
        this.tabbedPane = tabbedPane;
        initializeTabListeners();
    }

    private void initializeTabListeners() {
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                java.awt.Component comp = tabbedPane.getTabComponentAt(i);
                if (comp != null) {
                    comp.repaint();
                }
            }
            
            if (selectedIndex == LOGOUT_TAB_INDEX) {
                rootController.logout();
            }
        });
    }
}

