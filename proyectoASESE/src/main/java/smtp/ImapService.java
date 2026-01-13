package smtp;

import ui.models.smtp.Email;
import ui.models.smtp.SmtpCredentials;

import javax.mail.*;
import java.util.Properties;

/**
 * Service class for handling IMAP protocol operations.
 * Provides methods to connect, retrieve, read, and delete email messages from Gmail IMAP server.
 * Uses a persistent connection to maintain the mailbox state.
 */
public class ImapService {

    private static Store store;
    private static Folder folder;

    private static final String HOST = "imap.gmail.com";
    private static final String PORT = "993";

    /**
     * Establishes a connection to the IMAP server using SSL.
     * If already connected, this method returns immediately.
     * Opens the INBOX folder in READ_WRITE mode.
     *
     * @throws SmtpException if connection fails
     */
    private static void connect() {
        try {
            if (store != null && store.isConnected()) {
                return;
            }

            Properties prop = new Properties();
            prop.put("mail.store.protocol", "imaps");
            prop.put("mail.imap.host", HOST);
            prop.put("mail.imap.port", PORT);
            prop.put("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(prop);
            store = session.getStore("imaps");
            store.connect(HOST, SmtpCredentials.getUser(), SmtpCredentials.getToken());

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            System.out.println("Connection done");

        } catch (Exception e) {
            throw new SmtpException("Failed to connect :", e);
        }
    }

    /**
     * Retrieves the last 10 email messages from the INBOX.
     * Fetches envelope information and flags for each message.
     *
     * @return an array of Email objects containing sender, subject, date, and read status
     * @throws SmtpException if retrieval fails
     */
    public static Email[] getAllMessages() {
        try {
            connect();
            Message[] messages = folder.getMessages(Math.max(1, folder.getMessageCount() - 10),
                    folder.getMessageCount());

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fp);
            Email[] emails = new Email[messages.length];
            for (int i = 0; i < messages.length; i++) {
                try {
                    emails[i] = new Email(
                            messages[i].getFrom()[0].toString(),
                            messages[i].getSubject(),
                            messages[i].getReceivedDate(),
                            "",
                            messages[i].isSet(Flags.Flag.SEEN));
                } catch (Exception e) {
                    throw new SmtpException("Failed to get Messages ", e);
                }
            }
            return emails;
        } catch (Exception e) {
            throw new SmtpException("Failed to get messages", e);
        }
    }

    /**
     * Deletes a message from the mailbox by marking it as DELETED and expunging.
     *
     * @param message the message number to delete (1-based index)
     * @throws SmtpException if deletion fails
     */
    public static void delMessage(int message) {
        try {
            connect();
            Message msg = folder.getMessage(message);
            msg.setFlag(Flags.Flag.DELETED, true);
            folder.expunge();

        } catch (Exception e) {
            throw new SmtpException("Failed to delete message", e);
        }
    }

    /**
     * Marks a message as read or unread.
     *
     * @param message the message number (1-based index)
     * @param read true to mark as read, false to mark as unread
     * @throws SmtpException if operation fails
     */
    public static void readMessage(int message, boolean read) {
        try {
            connect();
            Message msg = folder.getMessage(message);
            msg.setFlag(Flags.Flag.SEEN, read);

        } catch (Exception e) {
            throw new SmtpException("Failed to read message", e);
        }

    }

    /**
     * Retrieves the body content of a specific message.
     * Handles both plain text and multipart content types.
     *
     * @param numeroMensaje the message number (1-based index)
     * @return the body content as a String, or error message if retrieval fails
     */
    public static String getBody(int numeroMensaje) {
        try {
            connect();

            if (folder.getMessage(numeroMensaje).getContent() instanceof String) {
                return (String) folder.getMessage(numeroMensaje).getContent();
            }
            if (folder.getMessage(numeroMensaje).getContent() instanceof Multipart) {
                return ((Multipart) folder.getMessage(numeroMensaje).getContent()).getBodyPart(0).getContent()
                        .toString();

            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "";
    }

    /**
     * Disconnects from the IMAP server and closes the folder.
     * Resets the store and folder references to null.
     *
     * @throws SmtpException if disconnection fails
     */
    public static void disconnect() {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
                folder = null;
            }
            if (store != null && store.isConnected()) {
                store.close();
                store = null;
            }
            System.out.println("IMAP disconnected");
        } catch (Exception e) {
            throw new SmtpException("failed to disconnect imap",e);
        }
    }

    /**
     * Gets the current INBOX folder reference.
     *
     * @return the Folder object representing the INBOX
     */
    public static Folder getFolder() {
        return folder;
    }
}