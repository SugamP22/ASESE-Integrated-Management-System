package ui.views.cardcontainer;

import ui.controllers.root.RootController;
import ui.views.admin.AdminView;
import ui.views.user.UserView;

import javax.swing.*;
import java.awt.*;

/**
 * CardContainerView is the ONLY class that uses CardLayout.
 * It contains two cards: AdminView and UserView.
 */
public class CardContainerView extends JPanel {

    private static final String ADMIN_PANEL = "ADMIN_PANEL";
    private static final String USER_PANEL = "USER_PANEL";

    private final CardLayout cardLayout;
    private final AdminView adminView;
    private final UserView userView;

    public CardContainerView(RootController rootController) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setOpaque(false);

        adminView = new AdminView(this, rootController);
        userView = new UserView(this, rootController);

        add(adminView, ADMIN_PANEL);
        add(userView, USER_PANEL);
    }

    public void showAdminPanel() {
        cardLayout.show(this, ADMIN_PANEL);
        adminView.resetToHomeTab();
    }

    public void showUserPanel() {
        cardLayout.show(this, USER_PANEL);
        userView.resetToHomeTab();
    }

}

