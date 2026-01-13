package ui.views.ftp;

import ftp.FtpService;
import ui.models.ftp.FileType;
import ui.models.ftp.FtpFileModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Panel responsible for displaying information and a preview of a selected FTP file.
 * <p>
 * This view shows the selected file's name, path, and type, and attempts
 * to display an image preview when the selected file is an image
 * (JPG, JPEG, PNG, or BMP).
 */
public class FtpFileView extends JPanel {
    private FtpFileModel file = new FtpFileModel("(none selected)", "(none selected)", FileType.FILE); // TODO: Improve

    private JLabel fileName;
    private JLabel filePath;
    private JLabel fileType;
    private ImagePanel imagePanel;

    public FtpFileView() {
        createComponents();
        addComponents();
        update();
    }

    /**
     * Initializes all UI components used to display file information
     * and previews.
     */
    private void createComponents() {
        fileName = new JLabel();
        filePath = new JLabel();
        fileType = new JLabel();
        imagePanel = new ImagePanel();
    }

    /**
     * Adds and arranges all UI components within this panel.
     */
    private void addComponents() {
        setLayout(new BorderLayout());

        // Top panel
        var topPanel = new JPanel();
        topPanel.add(fileName);
        topPanel.add(new JLabel(" | "));
        topPanel.add(filePath);
        topPanel.add(new JLabel(" | "));
        topPanel.add(fileType);
        add(topPanel, BorderLayout.NORTH);

        // Center image
        add(imagePanel, BorderLayout.CENTER);
    }

    /**
     * Attempts to retrieve and load the selected file as an image.
     * <p>
     * Only regular files with supported image extensions are handled.
     * If the file cannot be retrieved or decoded, an error dialog
     * is shown and {@code null} is returned.
     *
     * @return an {@link ImageIcon} if the file is a supported image,
     *         or {@code null} otherwise
     */
    private ImageIcon getFileImage() {
        if (file.fileType != FileType.FILE) {
            return null;
        }

        if (
                file.fileName.toLowerCase().endsWith(".jpg") ||
                file.fileName.toLowerCase().endsWith(".jpeg") ||
                file.fileName.toLowerCase().endsWith(".bmp") ||
                file.fileName.toLowerCase().endsWith(".png")
        ) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (output) {
                if (!FtpService.retrieveFile(file.filePath, output)) {
                    throw new IOException("Could not retrieve file");
                }
                output.flush();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null, // TODO: Add root window
                        "Image could not be loaded",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return null;
            }

            try (var input = new ByteArrayInputStream(output.toByteArray())) {
                var bufferedImage = ImageIO.read(input);
                if (bufferedImage == null) {
                    throw new IOException("Could not handle image");
                }
                return new ImageIcon(bufferedImage);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null, // TODO: Add root window
                        "Image format is not valid\n Can only handle JPG, JPEG, PNG and BMP images.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return null;
            }
        }

        return null;
    }

    /**
     * Sets the currently selected file and updates the view.
     *
     * @param file the {@link FtpFileModel} representing the selected file
     */
    public void setFile(FtpFileModel file) {
        this.file = file;
        update();
    }

    /**
     * Updates the displayed file information and image preview
     * based on the currently selected file.
     */
    public void update() {
        fileName.setText("Selected file: " + file.filePath);
        filePath.setText("File name: " + file.fileName);
        fileType.setText("File type: " + file.fileType.toString());
        var imageIcon = getFileImage();
        if (imageIcon != null) {
            imagePanel.setImage(imageIcon.getImage());
        }
        revalidate();
        repaint();
    }
}
