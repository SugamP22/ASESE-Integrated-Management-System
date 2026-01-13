package ftp;

import authentication.AuthenticationService;
import db.LogRepository;
import entities.Log;

/**
 * Utility class providing common FTP-related path and logging operations.
 */
public class FtpUtils {

    /**
     * Returns the parent path of the given FTP path.
     * <p>
     * For example, {@code /ftp/user/file.txt} returns {@code /ftp/user}.
     *
     * @param path the FTP path
     * @return the parent path, or the original path if it is empty or root
     */
    public static String getParentPath(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.substring(0, Math.max(path.lastIndexOf('/'), 1));
    }

    /**
     * Returns the last segment (child) of the given FTP path.
     * <p>
     * For example, {@code /ftp/user/file.txt} returns {@code file.txt}.
     *
     * @param path the FTP path
     * @return the child path, or the original path if it is empty or root
     */
    public static String getChildPath(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * Extracts the username from an FTP path assuming the path
     * is under {@code /ftp/username/...}.
     * <p>
     * Returns {@code null} if the path does not start with {@code /ftp/}.
     *
     * @param path the FTP path
     * @return the username, or {@code null} if not applicable
     */
    public static String getUsernameFromPath(String path) {
        if (!path.startsWith("/ftp/") || path.length() < 6) {
            return null;
        }
        var s = path.substring(5);
        var index = s.indexOf("/");
        if (index == -1) {
            return s;
        }
        return s.substring(0, index);
    }

    /**
     * Logs an event message associated with the currently authenticated user.
     * <p>
     * If no user is authenticated, the event is ignored.
     *
     * @param msg the message to log
     */
    public static void logEvent(String msg) {
        var currentUser = AuthenticationService.getCurrentUser();
        if (currentUser != null) {
            LogRepository.addLog(new Log(currentUser.getId(), msg));
        }
    }
}
