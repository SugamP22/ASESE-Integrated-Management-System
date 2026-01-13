package ui.controllers.smtp;

import authentication.AuthenticationService;
import db.LogRepository;
import db.WhitelistRepository;
import entities.Log;
import smtp.SmtpService;
import ui.views.smtp.SmtpFormView;
import ui.views.smtp.SmtpSendingDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action listener for sending email messages.
 * Validates form inputs, checks recipient against whitelist,
 * sends the email via SMTP, and logs the operation.
 */
public class ListenerSendMail implements ActionListener {

    private EmailController controller;

    /**
     * Constructs a new send mail listener.
     *
     * @param controller the email controller managing the compose form
     */
    public ListenerSendMail(EmailController controller) {
        this.controller = controller;
    }

    /**
     * Validates the email form and sends the email if all checks pass.
     * Performs the following validations:
     * - Ensures all required fields are filled
     * - Checks email format (no spaces, valid structure)
     * - Verifies recipient is in the whitelist
     *
     * Shows a modal loading dialog while sending and logs the result.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        SmtpFormView form = controller.getFormView();
        String recipient = form.getBoxs().get(0).getText();
        String subject = form.getBoxs().get(1).getText();
        String message = form.getBoxs().get(2).getText();
        if(recipient.isEmpty() || subject.isEmpty() || message.isEmpty() || recipient.contains("@")){
            JOptionPane.showMessageDialog(form,"required or invalid fields","Email not sent",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String mailExtension = form.getCombo().getSelectedItem().toString();
        String finalEmail = recipient+mailExtension;
        if (finalEmail.contains(" ")) {
            JOptionPane.showMessageDialog(form,"The email address cannot contain spaces..","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!WhitelistRepository.isEmailInWhitelistOrUsersList(finalEmail)){
            JOptionPane.showMessageDialog(null,
                    "Email no authorized",
                    "Email not sent",
                    JOptionPane.ERROR_MESSAGE);
            LogRepository.addLog(new Log(AuthenticationService.getCurrentUser().getId(),"[STMP] attempt to send an unauthorised email to "+finalEmail));

            return;
        }
        String [] paths = new String[form.getFileLabels().size()];
        for(int i=0;i<form.getFileLabels().size();i++){
            paths[i] = form.getFileLabels().get(i).getText();
        }

        Window owner = form.getOwner();
        form.dispose();

        SmtpSendingDialog sendingDialog = new SmtpSendingDialog(owner);
        new Thread(() -> {
            String errorMessage = null;
            try {
                SmtpService.sendMail(finalEmail, subject, message, paths);
            } catch (Exception ex) {
                errorMessage = ex.getMessage();
            }

            final String finalError = errorMessage;
            SwingUtilities.invokeLater(() -> {
                sendingDialog.dispose();
                if (finalError != null && !finalError.isBlank()) {
                    JOptionPane.showMessageDialog(owner, "failed to send emaill:\n" + finalError,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    LogRepository.addLog(new Log(AuthenticationService.getCurrentUser().getId(),"[STMP] failed to send email : "+ finalError));
                } else {
                    JOptionPane.showMessageDialog(owner, "email sent",
                            "OK", JOptionPane.INFORMATION_MESSAGE);
                    LogRepository.addLog(new Log(AuthenticationService.getCurrentUser().getId(),"[STMP] has sent an email to "+ finalEmail));
                }
            });
        }, "smtp-send-mail").start();

        // Modal dialog blocks app interaction while the mail is being sent.
        sendingDialog.setVisible(true);

    }
}