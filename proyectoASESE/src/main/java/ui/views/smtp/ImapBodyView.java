package ui.views.smtp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Mail body preview window for the SMTP/IMAP tab.
 * Styled to match the teal theme and kept at a reasonable size.
 */
public class ImapBodyView extends JDialog {

    private JEditorPane text;
    private JScrollPane scrollPane;
    private JLabel fromValue;
    private JLabel subjectValue;
    private JLabel dateValue;


    public ImapBodyView() {
        super((Window) null, "Mail", Dialog.ModalityType.MODELESS);
        initComponents();
    }

    private void initComponents() {
        setTitle("Mail");
        setSize(920, 620);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(SmtpUi.MAIN_BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JLabel title = new JLabel("Mail preview", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton close = SmtpUi.pillButton("Close", SmtpUi.BTN_GRAY);
        close.addActionListener(e -> dispose());

        header.add(title, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);

        JPanel meta = SmtpUi.cardPanel(SmtpUi.CARD_BG, SmtpUi.CARD_BORDER, 18);
        meta.setLayout(new BorderLayout());
        meta.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel metaGrid = new JPanel();
        metaGrid.setOpaque(false);
        metaGrid.setLayout(new javax.swing.BoxLayout(metaGrid, javax.swing.BoxLayout.Y_AXIS));

        fromValue = metaLine("From:", "-");
        subjectValue = metaLine("Subject:", "-");
        dateValue = metaLine("Date:", "-");

        metaGrid.add(fromValue);
        metaGrid.add(subjectValue);
        metaGrid.add(dateValue);
        meta.add(metaGrid, BorderLayout.CENTER);

        text = new JEditorPane();
        text.setEditable(false);
        text.setContentType("text/html");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setBackground(Color.WHITE);
        text.setBorder(new EmptyBorder(10, 12, 10, 12));

        scrollPane = new JScrollPane(text);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        root.add(header, BorderLayout.NORTH);
        root.add(meta, BorderLayout.CENTER);
        root.add(scrollPane, BorderLayout.SOUTH);

        // Keep scroll as main content; meta should not push it off-screen.
        root.remove(meta);
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(meta, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);

        JPanel bodyCard = SmtpUi.cardPanel(Color.WHITE, SmtpUi.CARD_BORDER, 18);
        bodyCard.setLayout(new BorderLayout());
        bodyCard.setBorder(new EmptyBorder(0, 0, 0, 0));
        bodyCard.add(scrollPane, BorderLayout.CENTER);
        root.add(bodyCard, BorderLayout.CENTER);

        setContentPane(root);

    }

    private JLabel metaLine(String label, String value) {
        JLabel l = new JLabel(label + " " + (value == null ? "" : value));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(SmtpUi.ROW_FG);
        l.setBorder(new EmptyBorder(2, 0, 2, 0));
        return l;
    }

    public void setMeta(String from, String subject, Object date) {
        fromValue.setText("From: " + (from == null ? "-" : from));
        subjectValue.setText("Subject: " + (subject == null ? "-" : subject));
        dateValue.setText("Date: " + (date == null ? "-" : String.valueOf(date)));
    }


    public void setText(String text) {
        this.text.setText(text);
        this.text.setCaretPosition(0);

    }
}
