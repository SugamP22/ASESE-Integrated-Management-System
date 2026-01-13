package ui.controllers.smtp;

import smtp.ImapService;
import smtp.SmtpException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action listener for deleting email messages.
 * Shows a confirmation dialog before permanently removing the email from the server.
 */
public class ListenerDeleteMail implements ActionListener {

    private EmailController controller;

    /**
     * Constructs a new delete mail listener.
     *
     * @param controller the email controller managing the email list
     */
    public ListenerDeleteMail(EmailController controller) {
        this.controller = controller;
    }

    /**
     * Handles the delete action with user confirmation.
     * Deletes the selected email from the IMAP server in a background thread
     * and refreshes the email list.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        int row =controller.getMainView().getMailTable().getSelectedRow();
        if(row<0) {
            System.out.println("select one row");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(controller.getMainView().asComponent(), "Delete this email??","Confirm",JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            //controller.getMainView().getTableModel().removeRow(row);
            new Thread(() -> {
                try {
                    ImapService.delMessage(ImapService.getFolder().getMessageCount()-row);
                    SwingUtilities.invokeLater(() -> {
                        controller.loadEmails();
                    });
                } catch (Exception ex) {
                    throw new SmtpException("Failed to delete message", ex);
                }
            }).start();
        }
    }
}