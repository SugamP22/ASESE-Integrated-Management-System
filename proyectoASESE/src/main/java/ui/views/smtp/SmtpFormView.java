package ui.views.smtp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * A modal dialog for composing and sending new email messages.
 * This view provides input fields for the recipient, subject, and message body,
 * as well as functionality to attach files and select email domains.
 * It uses a styled layout consistent with the application's SMTP UI theme.
 */
public class SmtpFormView extends JDialog {

    private final ArrayList<JLabel> labels;
    private final ArrayList<JTextArea> boxs;
    private JComboBox<String> combo;
    private JButton sendButton;
    private JButton cancelButton;
    private JButton addFileButton;
    private final ArrayList<JLabel> fileLabels;
    private JPanel panelForm;
    private JPanel filePanel;
    private  JPanel panelCampo;


    /**
     * Constructs a new SmtpFormView dialog.
     *
     * @param owner the Parent Window (Frame or Dialog) that owns this dialog
     */
    public SmtpFormView(Window owner) {
        super(owner, "Send New Email", Dialog.ModalityType.APPLICATION_MODAL);
        labels = new ArrayList<>();
        fileLabels = new ArrayList<>();
        boxs = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setSize(720, 520);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        // Crear labels y cajas
        labels.add(new JLabel("To:"));
        combo = new JComboBox<>();
        combo.addItem("@gmail.com");
        combo.addItem("@asese.com");
        labels.add(new JLabel("Subject:"));
        labels.add(new JLabel("Message:"));

        boxs.add(new JTextArea());
        boxs.add(new JTextArea());
        boxs.add(new JTextArea());

        sendButton = SmtpUi.pillButton("Send Mail", SmtpUi.BTN_GREEN);
        cancelButton = SmtpUi.pillButton("Cancel", SmtpUi.BTN_GRAY);
        addFileButton = SmtpUi.pillButton("Add file", SmtpUi.BTN_GRAY);



        panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(true);
        panelForm.setBackground(SmtpUi.MAIN_BG);
        panelForm.setBorder(new EmptyBorder(16, 18, 10, 18));

        JLabel title = new JLabel("Send Email", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        panelForm.add(title);

        for(int i = 0; i < labels.size(); i++){
            panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelCampo.setOpaque(false);
            panelCampo.add(labels.get(i));
            panelCampo.add(boxs.get(i));

            labels.get(i).setFont(new Font("Segoe UI", Font.BOLD, 14));
            labels.get(i).setForeground(Color.WHITE);
            labels.get(i).setPreferredSize(new Dimension(85, 26));

            JTextArea field = boxs.get(i);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setForeground(Color.BLACK);
            field.setBackground(Color.WHITE);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SmtpUi.TABLE_OUTLINE, 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            field.setLineWrap(i == 2);
            field.setWrapStyleWord(true);

            field.setPreferredSize(new Dimension(420, 32));
            if(i==0){
                combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                combo.setBackground(Color.WHITE);
                combo.setForeground(Color.BLACK);
                combo.setPreferredSize(new Dimension(140, 32));
                panelCampo.add(combo);
            }
            panelForm.add(panelCampo);
        }
        boxs.get(boxs.size()-1).setPreferredSize(new Dimension(560, 180));



        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(true);
        panelBotones.setBackground(SmtpUi.MAIN_BG);
        panelBotones.setBorder(new EmptyBorder(10, 12, 14, 16));
        panelBotones.add(cancelButton);
        panelBotones.add(sendButton);
        panelBotones.add(addFileButton);


        cancelButton.addActionListener(e -> dispose());

        JScrollPane scroll = new JScrollPane(panelForm);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(SmtpUi.MAIN_BG);

        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    public void addFiles(String path){

        fileLabels.add(new JLabel(path));
        filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(fileLabels.get(fileLabels.size()-1));
        panelForm.add(filePanel);
        panelForm.revalidate();
        panelForm.repaint();
    }


    public ArrayList<JTextArea> getBoxs() {
        return boxs;
    }

    public JButton getSendButton() {
        return sendButton;
    }


    public JButton getAddFileButton() {
        return addFileButton;
    }

    public ArrayList<JLabel> getFileLabels() {
        return fileLabels;
    }

    public JPanel getPanelForm() {
        return panelForm;
    }


    public JPanel getFilePanel() {
        return filePanel;
    }

    public JComboBox<String> getCombo() {
        return combo;
    }
}