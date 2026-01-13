package ui.controllers.login;

import authentication.AuthenticationService;
import db.DatabaseException;
import entities.User;
import ui.controllers.cardcontainer.CardContainerController;
import ui.controllers.root.RootController;
import ui.views.login.LoginLoadingDialog;
import ui.views.login.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LoginController knows LoginView and RootController.
 * Validates login credentials.
 * On success, tells RootController to show the app.
 */
public class LoginController {

    private final LoginView loginView;
    private final RootController rootController;

    // Guard against double-click / repeated login attempts while one is already running.
    private volatile boolean loggingIn = false;

    // UX: keep the "Logging in..." loader visible for at least this long (even if auth is fast).
    private static final int LOGIN_LOADER_MIN_MS = 3000;

    /**
     * Creates a controller for a {@link LoginView} and wires its UI events to the login flow.
     *
     * @param loginView       the view that provides user input and the login button
     * @param rootController  root navigation controller used to switch to the app after successful login
     */
    public LoginController(LoginView loginView, RootController rootController) {
        this.loginView = loginView;
        this.rootController = rootController;
        initializeLoginLogic();
    }

    private void initializeLoginLogic() {
        // Allow pressing Enter in any input field to trigger the same login action.
        loginView.wireEnterToLoginButton();

        loginView.getLoginButton().addActionListener(e -> {
            attemptLoginSafelyWithLoader();
        });
    }

    private void attemptLoginSafelyWithLoader() {
        if (loggingIn) {
            return;
        }
        loggingIn = true;

        final String email = loginView.getUsername().trim();
        final String password = loginView.getPassword();

        loginView.setInputsEnabled(false);

        Window owner = SwingUtilities.getWindowAncestor(loginView);
        LoginLoadingDialog loadingDialog = new LoginLoadingDialog(owner);
        final long dialogShownAtMs = System.currentTimeMillis();

        // Run authentication off the Swing UI thread so the UI stays responsive while the dialog is shown.
        new Thread(() -> {
            boolean ok = false;
            String errorTitle = null;
            String errorMessage = null;

            try {
                ok = AuthenticationService.login(email, password);
            } catch (DatabaseException | ExceptionInInitializerError | NoClassDefFoundError ex) {
                errorTitle = "Database";
                errorMessage = """
                        Can't connect to the database.
                        Please start MySQL and check your DB settings.""";
            } catch (RuntimeException ex) {
                errorTitle = "Error";
                errorMessage = "Something went wrong.\n" +
                        (ex.getMessage() != null ? ex.getMessage() : "");
            }

            final boolean finalOk = ok;
            final String finalErrorTitle = errorTitle;
            final String finalErrorMessage = errorMessage;

            SwingUtilities.invokeLater(() -> {
                long elapsed = System.currentTimeMillis() - dialogShownAtMs;
                int remaining = (int) Math.max(0, LOGIN_LOADER_MIN_MS - elapsed);

                Timer closeTimer = new Timer(remaining, (ActionEvent e) -> {
                    loadingDialog.dispose();

                    if (finalErrorTitle != null) {
                        showError(finalErrorTitle, finalErrorMessage != null ? finalErrorMessage : "");
                        loginView.resetForm();
                    } else if (finalOk) {
                        rootController.showApp();
                        CardContainerController cardContainerController = rootController.getCardContainerController();
                        if (cardContainerController != null) {
                            var currentUser = AuthenticationService.getCurrentUser();
                            if (currentUser != null && currentUser.getRol() == User.UserRol.ADMIN) {
                                cardContainerController.showAdminView();
                            } else {
                                cardContainerController.showUserView();
                            }
                        }
                    } else {
                        showError("Login", "Invalid username or password.");
                        loginView.resetForm();
                    }

                    // If login succeeded, the login view might already be replaced. This is still safe.
                    loginView.setInputsEnabled(true);
                    loggingIn = false;
                });
                closeTimer.setRepeats(false);
                closeTimer.start();
            });
        }, "login-worker").start();

        // Modal dialog blocks app interaction while login is being verified.
        loadingDialog.setVisible(true);
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(
                loginView,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}
