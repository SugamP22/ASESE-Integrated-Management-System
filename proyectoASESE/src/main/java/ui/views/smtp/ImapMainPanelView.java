package ui.views.smtp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Embeddable IMAP main view to display inside a tab (JPanel-based).
 * Mirrors the same controls as {@link ImapMainView}.
 */
public class ImapMainPanelView extends JPanel implements ImapMainViewContract {

    private JTable mailTable;
    private JLabel loadingLabel;
    private JLabel refreshDate;
    private DefaultTableModel tableModel;
    private JButton sendButton;
    private PopupMenu popupMenu;

    public ImapMainPanelView() {
        initComponents();
    }

    @Override
    public java.awt.Component asComponent() {
        return this;
    }

    private void initComponents() {
        setOpaque(false);

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
        mailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SmtpUi.styleTable(mailTable);

        JScrollPane scrollPane = SmtpUi.createTableScrollPane(mailTable);

        sendButton = SmtpUi.pillButton("Send new mail", SmtpUi.BTN_GREEN);

        popupMenu = new PopupMenu();
        mailTable.setComponentPopupMenu(popupMenu);

        // Status label (kept for controller contract), but styled like normal text (not a "tip" chip).
        loadingLabel = new JLabel("Loading mails...");
        loadingLabel.setForeground(SmtpUi.ROW_FG);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loadingLabel.setVisible(false);
        refreshDate = new JLabel();
        refreshDate.setForeground(SmtpUi.ROW_FG);
        refreshDate.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(15, 25, 10, 25));

        JPanel actionsCard = SmtpUi.cardPanel(SmtpUi.CARD_BG, SmtpUi.CARD_BORDER, 22);
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actionsCard.setBorder(new EmptyBorder(6, 10, 6, 10));
        actionsCard.add(sendButton);
        actionsCard.add(loadingLabel);
        actionsCard.add(refreshDate);

        buttonsPanel.add(actionsCard, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(buttonsPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public JButton getSendButton() {
        return sendButton;
    }

    @Override
    public JLabel getLoadingLabel() {
        return loadingLabel;
    }

    @Override
    public JLabel getRefreshDate() {
        return refreshDate;
    }

    @Override
    public JTable getMailTable() {
        return mailTable;
    }

    @Override
    public PopupMenu getPopupMenu() {
        return popupMenu;
    }
}


