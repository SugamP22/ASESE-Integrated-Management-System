package ui.views.smtp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Minimal contract shared by the standalone (JFrame) and embedded (JPanel) IMAP main views.
 * This lets the controller/listeners work in both modes without changing behavior.
 */
public interface ImapMainViewContract {
    Component asComponent();
    DefaultTableModel getTableModel();
    JButton getSendButton();
    JLabel getLoadingLabel();
    JLabel getRefreshDate();
    JTable getMailTable();
    PopupMenu getPopupMenu();
}


