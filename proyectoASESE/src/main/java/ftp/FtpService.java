package ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import ui.models.ftp.FileType;
import ui.models.ftp.FtpFileModel;

import java.io.*;

/**
 * Provides utility methods for interacting with an FTP server.
 * <p>
 * This class manages a single shared {@link FTPClient} instance and
 * provides methods for connecting, disconnecting, logging in, navigating
 * directories, and performing file operations such as upload, download,
 * create, rename, and delete.
 */
public class FtpService {
    private static FTPClient client = null;
    private static FTPClientConfig config = null;

    public static final String FTP_ADDRESS = "localserver785.mooo.com";
    public static final int FTP_PORT = 21;

    /**
     * Establishes a connection to the configured FTP server if not already connected. Only used internally.
     *
     * @throws IOException if the connection fails
     */
    private static void connect() throws IOException {
        if (client != null && client.isConnected()) {
            return;
        }
        // If we have a stale/disconnected client, drop it and reconnect.
        disconnect();

        client = new FTPClient();
        config = new FTPClientConfig();
        client.setControlEncoding("UTF-8");
        client.configure(config);
        client.connect(FTP_ADDRESS, FTP_PORT);
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.enterLocalPassiveMode();
    }

    /**
     * Disconnects from the FTP server and cleans up the client instance.
     * <p>
     * Should be called in logout
     */
    public static void disconnect() {
        if (client == null) {
            return;
        }
        try {
            if (client.isConnected()) {
                try {
                    client.logout();
                } catch (IOException ignored) {
                    // ignore logout failures; we still want to disconnect
                }
                client.disconnect();
            }
        } catch (IOException e) {
            throw new FtpException("Failed to disconnect FTP client", e);
        } finally {
            client = null;
            config = null;
        }
    }

    /**
     * Logs into the FTP server using the provided username and password.
     *
     * @param user the FTP username
     * @param password the FTP password
     * @return {@code true} if login succeeds, {@code false} otherwise
     * @throws FtpException if a connection or login error occurs
     */
    public static boolean login(String user, String password) {
        try {
            connect();
            return client.login(user, password);
        } catch (IOException e) {
            throw new FtpException("Failed to login", e);
        }
    }

    /**
     * Renames a file or directory on the FTP server.
     *
     * @param oldName the current path of the file/directory
     * @param newName the new path/name for the file/directory
     * @return {@code true} if the rename operation succeeds
     * @throws FtpException if a connection or rename error occurs
     */
    public static boolean renameFile(String oldName, String newName) {
        try {
            connect();
            return client.rename(oldName, newName);
        } catch (IOException e) {
            throw new FtpException("Failed to login", e);
        }
    }

    /**
     * Creates a new folder on the FTP server.
     *
     * @param path the path of the new folder
     * @return {@code true} if the folder was created successfully
     * @throws FtpException if a connection or creation error occurs
     */
    public static boolean createFolder(String path) {
        try {
            connect();
            return client.makeDirectory(path);
        } catch (IOException e) {
            throw new FtpException("Could not create new directory", e);
        }
    }

    /**
     * Creates an empty file on the FTP server.
     *
     * @param path the path of the new file
     * @return {@code true} if the file was created successfully, {@code false} if it already exists
     * @throws FtpException if a connection or creation error occurs
     */
    public static boolean createFile(String path) {
        try {
            connect();
            for (var f : getFiles(FtpUtils.getParentPath(path))) {
                if (f.fileName.equals(FtpUtils.getChildPath(path))) {
                    return false;
                }
            }
            return client.storeFile(path, new ByteArrayInputStream(new byte[0]));
        } catch (IOException e) {
            throw new FtpException("Could not create new directory", e);
        }
    }

    /**
     * Lists the files and directories at the specified path on the FTP server.
     *
     * @param path the directory path to list files from
     * @return an array of {@link FtpFileModel} representing the entries
     * @throws FtpException if a connection or listing error occurs
     */
    public static FtpFileModel[] getFiles(String path) {
        try {
            connect();
            var ftpFiles = path == null ? client.listFiles() : client.listFiles(path); // TODO: Can pass null directly?
            var ftpModels = new FtpFileModel[ftpFiles.length];
            for (int i = 0; i < ftpModels.length; i++) {
                var f = ftpFiles[i];

                FileType fileType;
                if (f.isFile()) {
                    fileType =  FileType.FILE;
                } else if (f.isDirectory()) {
                    fileType =  FileType.FOLDER;
                } else if (f.isSymbolicLink()) {
                    fileType =  FileType.SYB_LINK;
                } else {
                    throw new RuntimeException("Unknown file type");
                }

                ftpModels[i] = new FtpFileModel(
                        f.getName(),
                        path + "/" + f.getName(),
                        fileType
                );
            }

            return ftpModels;
        } catch (IOException e) {
            throw new FtpException("Could not list files", e);
        }
    }

    /**
     * Deletes a file on the FTP server.
     *
     * @param path the path of the file to delete
     * @return {@code true} if the file was deleted successfully
     * @throws FtpException if a connection or deletion error occurs
     */
    public static boolean deleteFile(String path)  {
        try {
            connect();
            return client.deleteFile(path);
        } catch (IOException e) {
            throw new FtpException("Could not delete file", e);
        }
    }

    /**
     * Deletes an empty folder on the FTP server.
     *
     * @param path the path of the folder to delete
     * @return {@code true} if the folder was deleted successfully
     * @throws FtpException if a connection or deletion error occurs
     */
    public static boolean deleteFolder(String path)  {
        try {
            connect();
            return client.removeDirectory(path);
        } catch (IOException e) {
            throw new FtpException("Could not delete file", e);
        }
    }

    /**
     * Retrieves a file from the FTP server and writes its content to the provided {@link OutputStream}.
     *
     * @param path the path of the file on the FTP server
     * @param output the output stream to write the file contents to
     * @return {@code true} if the file was successfully retrieved
     * @throws FtpException if a connection or retrieval error occurs
     */
    public static boolean retrieveFile(String path, OutputStream output)  {
        try {
            connect();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            return client.retrieveFile(path, output);
        } catch (IOException e) {
            throw new FtpException("Could not delete file", e);
        }
    }

    /**
     * Recursively deletes a folder and all of its contents on the FTP server.
     *
     * @param path the path of the folder to delete
     * @return {@code true} if the folder and its contents were deleted successfully
     * @throws FtpException if a connection or deletion error occurs
     */
    public static boolean deleteFolderRecursive(String path)  {
        try {
            connect();
            for (var f : getFiles(path)) {
                if (f.fileType == FileType.FILE) {
                    deleteFile(f.filePath);
                } else if (f.fileType == FileType.FOLDER) {
                    deleteFolderRecursive(f.filePath);
                } else {
                    throw new RuntimeException("Failed to delete file");
                }
            }
            return deleteFolder(path);
        } catch (IOException e) {
            throw new FtpException("Could not delete file", e);
        }
    }

    /**
     * Uploads a file to the FTP server.
     *
     * @param filePath the destination path on the FTP server
     * @param file the local file to upload
     * @return {@code true} if the upload succeeds
     * @throws FtpException if a connection or upload error occurs
     */
    public static boolean uploadFile(String filePath, File file)  {
        try (var input = new FileInputStream(file)) {
            connect();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            return client.storeFile(filePath, input);
        } catch (IOException e) {
            throw new FtpException("Could not upload file", e);
        }
    }

    /**
     * Changes the current working directory on the FTP server.
     *
     * @param path the target directory path
     * @return {@code true} if the directory change was successful
     * @throws FtpException if a connection or directory change error occurs
     */
    public static boolean cd(String path) {
        try {
            connect();
            return client.changeWorkingDirectory(path);
        } catch (IOException e) {
            throw new FtpException("Could not get current working directory", e);
        }
    }
}