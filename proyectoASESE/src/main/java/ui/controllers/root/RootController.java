package ui.controllers.root;

import authentication.AuthenticationService;
import ftp.FtpService;
import smtp.ImapService;
import ui.controllers.cardcontainer.CardContainerController;
import ui.controllers.login.LoginController;
import ui.views.cardcontainer.CardContainerView;
import ui.views.content.ContentLayerView;
import ui.views.login.LoginView;

/**
 * RootController knows only ContentLayerView, LoginView, and CardContainerController.
 * Controls application navigation (Login ↔ App).
 * Does NOT directly manipulate CardLayout.
 */
public class RootController {

    private final ContentLayerView contentLayerView;
    private final LoginView loginView;
    private CardContainerView cardContainerView;
    private CardContainerController cardContainerController;

    public RootController(ContentLayerView contentLayerView) {
        this.contentLayerView = contentLayerView;
        this.loginView = new LoginView();
        new LoginController(loginView, this);
        showLogin();
    }

    /**
     * Shows the login view
     */
    public void showLogin() {
        contentLayerView.setContent(loginView);
    }

    /**
     * Logs out the current user, resets the login form, and navigates back to login.
     */
    public void logout() {
        AuthenticationService.logout();
        // Never let external-service disconnects block logout navigation.
        try {
            ImapService.disconnect();
        } catch (RuntimeException ignored) {
        }
        try {
            FtpService.disconnect();
        } catch (RuntimeException ignored) {
        }
        loginView.resetForm();

        // Tear down the app view so the next login rebuilds user-specific panels (and drops stale listeners/state).
        cardContainerView = null;
        cardContainerController = null;

        showLogin();
    }

    /**
     * Shows the application (CardContainerView) after successful login
     */
    public void showApp() {
        if (cardContainerView == null) {
            cardContainerView = new CardContainerView(this);
            cardContainerController = new CardContainerController(cardContainerView);
        }
        contentLayerView.setContent(cardContainerView);
    }

    /**
     * Gets the CardContainerController for switching between AdminView and UserView
     */
    public CardContainerController getCardContainerController() {
        return cardContainerController;
    }
}

