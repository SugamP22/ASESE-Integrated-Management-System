package ui.views.smtp;

import org.kordamp.ikonli.boxicons.BoxiconsRegular;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

/**
 * Custom contextual menu used within the email table.
 * This popup provides quick access to email actions such as deleting a message
 * or viewing its full content. It utilizes Ikonli Boxicons for a modern visual style.
 */
public class PopupMenu extends JPopupMenu {
    private JMenuItem deleteItem;
    private JMenuItem showBody;
    private static final FontIcon deleteIcon = FontIcon.of(BoxiconsRegular.TRASH, 18);
    private static final FontIcon showIcon = FontIcon.of(BoxiconsRegular.SUBDIRECTORY_LEFT, 18);

    /**
     * Constructs a new PopupMenu and initializes the menu items with icons and custom fonts.
     * Sets the visual theme to match the application's SMTP UI design.
     */
    public PopupMenu() {
        setBackground(Color.WHITE);
        setBorder(javax.swing.BorderFactory.createLineBorder(SmtpUi.TABLE_OUTLINE, 1));

        this.deleteItem = new JMenuItem("Delete");
        this.showBody = new JMenuItem("Show mail");
        deleteItem.setIcon(deleteIcon);
        showBody.setIcon(showIcon);
        deleteItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(deleteItem);
        add(showBody);
    }

    public JMenuItem getDeleteItem() {
        return deleteItem;
    }

    public JMenuItem getShowBody() {
        return showBody;
    }
}