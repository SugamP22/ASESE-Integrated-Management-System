package smtp;

import ui.models.smtp.SmtpCredentials;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * Service class for sending emails using SMTP protocol.
 * Provides functionality to send emails with optional file attachments through Gmail SMTP server.
 */
public class SmtpService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    /**
     * Sends an email with optional file attachments.
     * Uses TLS encryption and authenticates with the current user's credentials.
     *
     * @param recipient the email address of the recipient
     * @param subject the subject line of the email
     * @param msg the body content of the email
     * @param filePath array of file paths to attach, or null/empty for no attachments
     * @throws SmtpException if sending fails
     */
    public static void sendMail(String recipient, String subject, String msg, String[] filePath) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SmtpCredentials.getUser(), SmtpCredentials.getToken());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SmtpCredentials.getUser()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            if (filePath == null || filePath.length == 0) {
                message.setText(msg);
            } else {
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(msg);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);
                for (String path : filePath) {
                    MimeBodyPart file = new MimeBodyPart();
                    file.attachFile(path);
                    multipart.addBodyPart(file);
                }
                message.setContent(multipart);
            }
            Transport.send(message);

        } catch (Exception e) {
            throw new SmtpException("failed to send mail", e);
        }
    }
}