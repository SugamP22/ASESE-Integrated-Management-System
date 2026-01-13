package ui.controllers.cardcontainer;

import ui.views.cardcontainer.CardContainerView;

/**
 * CardContainerController knows only CardContainerView.
 * Switches between AdminView and UserView.
 */
public class CardContainerController {

    private final CardContainerView cardContainerView;

    public CardContainerController(CardContainerView cardContainerView) {
        this.cardContainerView = cardContainerView;
    }

    /**
     * Shows the admin view
     */
    public void showAdminView() {
        cardContainerView.showAdminPanel();
    }

    /**
     * Shows the user view
     */
    public void showUserView() {
        cardContainerView.showUserPanel();
    }
}

