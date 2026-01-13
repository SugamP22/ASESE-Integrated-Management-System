package ui.models.ftp;

/**
 * Model representing a file system entry on an FTP server.
 * <p>
 * This class stores basic metadata about an FTP file, including
 * its name, full path, and type.
 */
public class FtpFileModel {
    public String fileName;
    public String filePath;
    public FileType fileType;

    public FtpFileModel(String fileName, String filePath, FileType fileType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }
}
