package ui.models.smtp;

import java.util.Date;

/**
 * Represents an email message within the application.
 * This model stores essential email metadata such as sender, subject,
 * timestamp, and content, along with the read status.
 */
public class Email {
    /** The email address of the person who sent the message. */
    private String sender;

    /** The subject line of the email. */
    private String subject;

    /** The date and time when the email was received. */
    private Date date;

    /** The main text content of the email. */
    private String body;

    /** Indicates whether the email has been opened/read by the user. */
    private boolean read;

    /**
     * Constructs a new Email instance with all required fields.
     *
     * @param sender  the sender's email address
     * @param subject the topic of the email
     * @param date    the arrival timestamp
     * @param body    the content of the message
     * @param read    the initial read status
     */
    public Email(String sender, String subject, Date date, String body, boolean read) {
        this.sender = sender;
        this.subject = subject;
        this.date = date;
        this.body = body;
        this.read = read;
    }

    /**
     * Gets the sender's email address.
     * @return a String representing the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Gets the subject line of the email.
     * @return the subject string
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the date and time the email was received.
     * @return the arrival Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the body content of the email.
     * @return the message text
     */
    public String getBody() {
        return body;
    }

    /**
     * Checks if the email has been read.
     * @return true if the email is marked as read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Updates the read status of the email.
     * @param read true to mark as read, false for unread
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Sets the body content of the email.
     * @param body the message text to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Sets the received date of the email.
     * @param date the new timestamp to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets the subject line of the email.
     * @param subject the new subject string
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets the sender's email address.
     * @param sender the new sender address
     */
    public void setSender(String sender) {
        this.sender = sender;
    }
}