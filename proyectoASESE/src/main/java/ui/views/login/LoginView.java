package ui.views.login;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;



/**
 * Login form view (email + password) used by {@link ui.controllers.login.LoginController}.
 * <p>
 * Exposes only the minimal API needed by the controller (read inputs, trigger login, and reset/disable UI).
 */
public class LoginView extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private String usernamePlaceholder;
    private String passwordPlaceholder;

    public LoginView() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 350));
        initializeContents();
    }

    private void initializeContents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel container = new JPanel();
        container.setBackground(new Color(33, 47, 61, 220));
        container.setLayout(new GridBagLayout());
        container.setBorder(new RoundedBorder(20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 15, 10, 15);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;

        JLabel titleLabel = new JLabel("LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(244, 211, 94));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridwidth = 2;
        container.add(titleLabel, c);

        c.gridy++;
        c.gridwidth = 1;
        JLabel usernameLabel = new JLabel("Email");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        container.add(usernameLabel, c);

        c.gridx = 1;
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(new RoundedBorder(10));
        usernameField.setBackground(new Color(240, 240, 240));
        usernameField.setForeground(Color.BLACK);
        usernamePlaceholder = "Enter your email";
        addPlaceholder(usernameField, usernamePlaceholder);
        container.add(usernameField, c);

        c.gridy++;
        c.gridx = 0;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        container.add(passwordLabel, c);

        c.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(new RoundedBorder(10));
        passwordField.setBackground(new Color(240, 240, 240));
        passwordField.setForeground(Color.BLACK);
        passwordPlaceholder = "********";
        addPlaceholder(passwordField, passwordPlaceholder);
        container.add(passwordField, c);

        c.gridy++;
        c.gridx = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(20, 50, 10, 50);
        loginButton = new JButton("Login");
        styleButton(loginButton);
        container.add(loginButton, c);

        add(container, gbc);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(244, 211, 94));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 230, 120));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(244, 211, 94));
            }
        });
    }

    private static class RoundedBorder extends LineBorder {
        private final int radius;
        public RoundedBorder(int radius) {
            super(Color.BLACK, 1, true);
            this.radius = radius;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    /**
     * Returns the typed username/email, or an empty string if the placeholder is still shown.
     */
    public String getUsername() {
        String value = usernameField.getText();
        if (usernamePlaceholder != null && usernamePlaceholder.equals(value)) {
            return "";
        }
        return value;
    }

    /**
     * Returns the typed password, or an empty string if the placeholder is still shown.
     */
    public String getPassword() {
        String value = new String(passwordField.getPassword());
        if (passwordPlaceholder != null && passwordPlaceholder.equals(value)) {
            return "";
        }
        return value;
    }

    /**
     * Resets the login form back to its placeholder state.
     * Useful after logout or after a failed login attempt.
     */
    public void resetForm() {
        // Restore placeholders and clear any user-entered values.
        if (usernameField != null) {
            usernameField.setText(usernamePlaceholder != null ? usernamePlaceholder : "");
            usernameField.setForeground(Color.GRAY);
        }
        if (passwordField != null) {
            passwordField.setText(passwordPlaceholder != null ? passwordPlaceholder : "");
            passwordField.setForeground(Color.GRAY);
        }
    }

    /**
     * Pressing Enter on username/password will trigger the login button.
     * This keeps the login logic centralized in the controller via button click.
     */
    public void wireEnterToLoginButton() {
        if (loginButton == null) {
            return;
        }
        if (usernameField != null) {
            usernameField.addActionListener(e -> loginButton.doClick());
        }
        if (passwordField != null) {
            passwordField.addActionListener(e -> loginButton.doClick());
        }
    }

    /**
     * Enables/disables all login inputs and actions (useful while login is running).
     */
    public void setInputsEnabled(boolean enabled) {
        if (usernameField != null) usernameField.setEnabled(enabled);
        if (passwordField != null) passwordField.setEnabled(enabled);
        if (loginButton != null) loginButton.setEnabled(enabled);
    }
}

