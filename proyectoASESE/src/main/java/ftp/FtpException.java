package ftp;

/**
 * Runtime exception thrown for errors related to FTP operations.
 */
public class FtpException extends RuntimeException {
    public FtpException() {
        super();
    }

    public FtpException(String message) {
        super(message);
    }

    public FtpException(Throwable cause) {
        super(cause);
    }

    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }
}
