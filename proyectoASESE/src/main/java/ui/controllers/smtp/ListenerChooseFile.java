package ui.controllers.smtp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Action listener for selecting file attachments to include in an email.
 * Opens a file chooser dialog allowing multiple file selection.
 */
public class ListenerChooseFile implements ActionListener {

    private EmailController controller;

    /**
     * Constructs a new file chooser listener.
     *
     * @param controller the email controller managing the form
     */
    public ListenerChooseFile(EmailController controller) {
        this.controller = controller;
    }

    /**
     * Opens a file chooser dialog and adds selected files to the email form.
     * Supports multiple file selection.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser fileManager = new JFileChooser();
        fileManager.setMultiSelectionEnabled(true);
        int resultado = fileManager.showOpenDialog(controller.getFormView());
        if(resultado == JFileChooser.APPROVE_OPTION){
            File[] files = fileManager.getSelectedFiles();
            for(int i=0;i<files.length;i++){
                controller.getFormView().addFiles(files[i].getAbsolutePath());
            }


        }

    }
}
