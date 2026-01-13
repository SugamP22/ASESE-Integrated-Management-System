package ui.controllers.smtp;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action listener for opening the email compose dialog.
 * Triggers when the user clicks the "Send new mail" button.
 */
public class ListenerOpenForm implements ActionListener {

    private EmailController controller;

    /**
     * Constructs a new open form listener.
     *
     * @param controller the email controller that will open the compose dialog
     */
    public ListenerOpenForm(EmailController controller) {
        this.controller = controller;
    }

    /**
     * Opens the email compose dialog.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.openComposeDialog();

    }
}
