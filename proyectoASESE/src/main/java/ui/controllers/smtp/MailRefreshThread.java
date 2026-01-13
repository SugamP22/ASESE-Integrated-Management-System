package ui.controllers.smtp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Background thread that automatically refreshes the email list at regular intervals.
 * Runs every 10 seconds to check for new messages from the IMAP server.
 */
public class MailRefreshThread extends Thread{

    private EmailController controller;
    private boolean running;
    private static final int REFRESH_TIMER = 10000;
    private  String dateTime;

    /**
     * Constructs a new mail refresh thread.
     *
     * @param controller the email controller to refresh emails for
     */
    public MailRefreshThread(EmailController controller) {
        this.controller = controller;
        this.running = true;
        dateTime = "";

    }

    /**
     * Continuously refreshes the email list every 10 seconds.
     * Updates the last refresh timestamp in the UI.
     * Stops gracefully when interrupted.
     */
    @Override
    public void run() {
        while (running) {
            try {

                Thread.sleep(REFRESH_TIMER);
                controller.loadEmails();
                dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                controller.getMainView().getRefreshDate().setText("last refresh: "+dateTime);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.println("failed to refresh mails: " + e.getMessage());
            }
        }
    }
}