package ui.views.smtp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Main graphical interface for the IMAP client.
 * This class extends {@link JFrame} and implements {@link ImapMainViewContract} to
 * display a table of emails, a refresh timestamp, and a button to send new messages.
 * It follows a typical Swing architecture for desktop mail applications.
 */
public class ImapMainView extends JFrame implements ImapMainViewContract {

    private JTable mailTable;
    private JLabel loadingLabel;
    private JLabel refreshDate;
    private DefaultTableModel tableModel;
    JButton sendButton;
    private PopupMenu popupMenu;

    /**
     * Initializes a new instance of the ImapMainView and triggers
     * the setup of all graphical components.
     */
    public ImapMainView() {
        initComponents();
    }

    @Override
    public java.awt.Component asComponent() {
        return this;
    }

    private void initComponents() {
        setTitle("Mail");
        setSize(1200, 600);
        // Don't exit the whole app when this window closes.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        String[] columnas = {"Read", "sender", "subject", "date"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }
        };

        mailTable = new JTable(tableModel);
        mailTable.setRowHeight(30);
        mailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(mailTable);
        sendButton = new JButton("Send new mail");
        sendButton.setSize(200,200);
        popupMenu = new PopupMenu();
        mailTable.setComponentPopupMenu(popupMenu);

        loadingLabel = new JLabel("Loading mails...");
        refreshDate = new JLabel();

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(sendButton);
        buttonsPanel.add(loadingLabel);
        buttonsPanel.add(refreshDate);



        setLayout(new BorderLayout());
        add(buttonsPanel,BorderLayout.NORTH);
        add(scrollPane,BorderLayout.CENTER);

    }


    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JLabel getLoadingLabel() {
        return loadingLabel;
    }

    public PopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JTable getMailTable() {
        return mailTable;
    }

    public JLabel getRefreshDate() {
        return refreshDate;
    }

    public void setRefreshDate(JLabel refreshDate) {
        this.refreshDate = refreshDate;
    }
}