package ui.controllers.smtp;

import smtp.ImapService;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action listener for displaying the full email body in a separate window.
 * Fetches the email content from the server and marks it as read.
 */
public class ListenerShowBody implements ActionListener {

    private final EmailController controller;

    /**
     * Constructs a new show body listener.
     *
     * @param controller the email controller managing the body view
     */
    public ListenerShowBody(EmailController controller) {
        this.controller = controller;
    }

    /**
     * Opens the email body viewer window and loads the content.
     * Updates the email header information (from, subject, date) and
     * fetches the body content in a background thread.
     * Marks the email as read on the server.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int row = controller.getMainView().getMailTable().getSelectedRow();
        if (row <0){
            return;
        }

        // Update header info from current table row (sender/subject/date).
        Object from = controller.getMainView().getTableModel().getValueAt(row, 1);
        Object subject = controller.getMainView().getTableModel().getValueAt(row, 2);
        Object date = controller.getMainView().getTableModel().getValueAt(row, 3);
        controller.getBodyView().setMeta(
                from == null ? null : String.valueOf(from),
                subject == null ? null : String.valueOf(subject),
                date
        );

        int numeroMensaje;
        try {
            numeroMensaje = ImapService.getFolder().getMessageCount() - row;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
        controller.getBodyView().setText("mail loading...");
        controller.getBodyView().setVisible(true);

        new Thread(() -> {
            try {

                String body = ImapService.getBody(numeroMensaje);
                ImapService.readMessage(numeroMensaje,true);

                SwingUtilities.invokeLater(() -> {
                    controller.getMainView().getTableModel().setValueAt(true, row, 0);
                    controller.getBodyView().setText(body);
                });


            } catch (Exception ex) {
                System.err.println("Failed to load mail body: " + ex.getMessage());
            }
        }).start();

    }

}