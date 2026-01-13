package ui.controllers.smtp;

import smtp.ImapService;
import smtp.SmtpException;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Mouse listener for toggling the read/unread status of emails.
 * Responds to double-click events on the email table.
 */
public class ListenerReadMail implements MouseListener {

    private EmailController controller;

    /**
     * Constructs a new read mail listener.
     *
     * @param controller the email controller managing the email table
     */
    public ListenerReadMail(EmailController controller) {
        this.controller = controller;
    }

    /**
     * Handles double-click events to toggle email read status.
     * Updates both the local table display and the server flag.
     *
     * @param e the mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() == 2) {

            if (controller.getMainView().getMailTable().getSelectedRow()>=0) {
                boolean read = (boolean) (controller.getMainView().getTableModel().getValueAt(controller.getMainView().getMailTable().getSelectedRow(), 0));
                int numeroMensaje;
                try {
                    numeroMensaje = ImapService.getFolder().getMessageCount() - controller.getMainView().getMailTable().getSelectedRow();
                } catch (Exception ex) {
                    throw new SmtpException("Failed to set message read",ex);
                }
                controller.getMainView().getTableModel().setValueAt(!read, controller.getMainView().getMailTable().getSelectedRow(), 0);

                new Thread(() -> {
                    try{
                        ImapService.readMessage(numeroMensaje, !read);
                        controller.loadEmails();

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }

                }).start();
            }
        }



    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
