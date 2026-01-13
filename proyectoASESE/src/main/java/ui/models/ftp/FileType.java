package ui.models.ftp;

/**
 * Represents the type of an FTP file system entry.
 * <p>
 * This enum is used to distinguish between directories, regular files,
 * and symbolic links when working with FTP file models.
 */
public enum FileType {
    FOLDER,
    FILE,
    SYB_LINK,
}
