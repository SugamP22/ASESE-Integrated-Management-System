
package ui.controllers.smtp;

import smtp.ImapService;
import ui.models.smtp.Email;
import ui.views.smtp.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main controller for email management functionality.
 * Manages IMAP email retrieval, SMTP sending, and coordinates all UI interactions.
 * Supports both standalone window mode and embedded panel mode for tab integration.
 */
public class EmailController {

    private ImapMainViewContract mainView;
    private SmtpFormView formView;
    private ImapBodyView bodyView;
    private MailRefreshThread hilo;
    private final AtomicBoolean loading = new AtomicBoolean(false);

    /**
     * Constructs a new EmailController instance.
     */
    public EmailController() {

    }

    /**
     * Starts the email controller in standalone window mode.
     * Creates a new JFrame window for the email interface.
     */
    public void start() {
        this.mainView = new ImapMainView();
        ((ImapMainView) this.mainView).setVisible(true);
        startCommon();
    }

    /**
     * Starts the SMTP/IMAP UI inside an existing panel (tab-friendly).
     * Keeps the same controller + listeners, but does not open a new JFrame for the main view.
     *
     * @param embeddedMainView the panel view to embed the email interface in
     */
    public void startEmbedded(ImapMainPanelView embeddedMainView) {
        this.mainView = embeddedMainView;
        startCommon();
    }

    /**
     * Common initialization logic for both standalone and embedded modes.
     * Sets up listeners, loads initial emails, and starts the refresh thread.
     */
    private void startCommon() {
        bodyView = new ImapBodyView();
        addOpenFormEvent();
        addDeleteMailButton();
        addReadMailEvent();
        addTableSelectRowEvent();
        addShowMailBodyEvent();

        // First load should not crash the app if credentials are missing/invalid.
        loadEmails();

        if (hilo == null || !hilo.isAlive()) {
            hilo = new MailRefreshThread(this);
            hilo.start();
        }
    }

    /**
     * Loads emails from the IMAP server and updates the table.
     * Fetches messages in a background thread to avoid blocking the UI.
     * Uses atomic boolean to prevent concurrent loading operations.
     */
    public void loadEmails() {
        if (!loading.compareAndSet(false, true)) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            mainView.getLoadingLabel().setVisible(true);
            mainView.getLoadingLabel().setText("Loading mails...");
        });

        new Thread(() -> {
            try {
                List<Email> emails = Arrays.asList(ImapService.getAllMessages());
                Collections.reverse(emails);

                SwingUtilities.invokeLater(() -> {
                    mainView.getTableModel().setRowCount(0);
                    for (Email email : emails) {
                        mainView.getTableModel().addRow(new Object[]{
                                email.isRead(), email.getSender(), email.getSubject(), email.getDate()
                        });
                    }
                    mainView.getLoadingLabel().setVisible(false);
                });
            } catch (RuntimeException ex) {
                SwingUtilities.invokeLater(() -> {
                    mainView.getTableModel().setRowCount(0);
                    mainView.getLoadingLabel().setVisible(true);
                    mainView.getLoadingLabel().setText("Connection failed. Configure your USER and PASS");
                });
            } finally {
                loading.set(false);
            }
        }, "imap-load-emails").start();
    }

    /**
     * Attaches the listener for opening the compose email form.
     */
    public void addOpenFormEvent(){
        mainView.getSendButton().addActionListener(new ListenerOpenForm(this));

    }

    /**
     * Opens the compose window as an application-modal dialog.
     * While it's open, the user cannot interact with the rest of the app (tabs/logout/etc).
     */
    public void openComposeDialog() {
        java.awt.Window owner = SwingUtilities.getWindowAncestor(mainView.asComponent());
        formView = new SmtpFormView(owner);
        formView.getSendButton().addActionListener(new ListenerSendMail(this));
        formView.getAddFileButton().addActionListener(new ListenerChooseFile(this));
        formView.setVisible(true);
    }

    /**
     * Attaches the listener for the delete email action in the popup menu.
     */
    public void addDeleteMailButton(){
        mainView.getPopupMenu().getDeleteItem().addActionListener(new ListenerDeleteMail(this));
    }

    /**
     * Attaches the mouse listener for marking emails as read/unread on double-click.
     */
    public void addReadMailEvent(){
        mainView.getMailTable().addMouseListener(new ListenerReadMail(this));
    }

    /**
     * Attaches the mouse listener for right-click row selection in the email table.
     */
    public void addTableSelectRowEvent(){
        mainView.getMailTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = mainView.getMailTable().rowAtPoint(e.getPoint());
                    if (row>=0) {
                        mainView.getMailTable().setRowSelectionInterval(row, row);
                    }
                }
            }
        });
    }

    /**
     * Attaches the listener for showing the email body in a separate window.
     */
    public void addShowMailBodyEvent(){
        mainView.getPopupMenu().getShowBody().addActionListener(new ListenerShowBody(this));
    }

    /**
     * Gets the compose email form view.
     *
     * @return the SMTP form view
     */
    public SmtpFormView getFormView() {
        return formView;
    }

    /**
     * Gets the main email list view.
     *
     * @return the main view contract
     */
    public ImapMainViewContract getMainView() {
        return mainView;
    }

    /**
     * Gets the email body display view.
     *
     * @return the body view
     */
    public ImapBodyView getBodyView() {
        return bodyView;
    }
}