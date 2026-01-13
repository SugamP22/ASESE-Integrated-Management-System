package smtp;

/**
 * Custom exception for SMTP and IMAP related errors.
 * Extends RuntimeException to provide unchecked exception handling for mail operations.
 */
public class SmtpException extends RuntimeException {

    /**
     * Constructs a new SmtpException with no detail message.
     */
    public SmtpException() {
        super();
    }

    /**
     * Constructs a new SmtpException with the specified detail message.
     *
     * @param message the detail message
     */
    public SmtpException(String message) {
        super(message);
    }

    /**
     * Constructs a new SmtpException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public SmtpException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new SmtpException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public SmtpException(String message, Throwable cause) {
        super(message, cause);
    }
}