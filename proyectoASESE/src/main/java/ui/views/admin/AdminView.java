package ui.views.admin;

import ui.controllers.admin.AdminController;
import ui.controllers.root.RootController;
import ui.views.cardcontainer.CardContainerView;
import ui.views.ftp.FtpView;
import ui.views.home.DefaultPanelView;
import ui.views.projectmanagment.ProjectManagementPanelView;
import ui.views.smtp.SendGmailPanelView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminView extends JPanel {

    private final Color backgroundColor = new Color(30, 40, 55, 200);
    private final Color titleColor = new Color(255, 215, 0);

    private JTabbedPane tabbedPane;
    private ProjectManagementPanelView projectManagementPanel;

    public AdminView(CardContainerView cardContainerView, RootController rootController) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 30, 30, 30));

        createTitlePanel();
        createTabbedPane();

        new AdminController(rootController, tabbedPane);
        
        tabbedPane.setSelectedIndex(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2d.dispose();
    }

    private void createTitlePanel() {
        JPanel topTitle = new JPanel(new BorderLayout());
        topTitle.setOpaque(false);

        JLabel titleLabel = new JLabel("ADMIN DASHBOARD");
        titleLabel.setForeground(titleColor);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        topTitle.add(titleLabel, BorderLayout.CENTER);
        add(topTitle, BorderLayout.NORTH);
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane() {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
                    @Override
                    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(new Color(40, 50, 65, 255));
                        g2d.fillRect(0, 0, getWidth(), getTabAreaHeight());
                        g2d.dispose();
                    }

                    private int getTabAreaHeight() {
                        if (tabbedPane.getTabCount() == 0) return 0;
                        java.awt.Component comp = tabbedPane.getTabComponentAt(0);
                        if (comp != null) {
                            return comp.getPreferredSize().height + 10;
                        }
                        return 50;
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 50, 65, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setOpaque(true);
        tabbedPane.setBackground(new Color(40, 50, 65, 255));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 18));

        DefaultPanelView defaultPanel = new DefaultPanelView();
        tabbedPane.addTab("Home", defaultPanel);
        tabbedPane.setTabComponentAt(0, createTabLabel("Home", 0));

        projectManagementPanel = new ProjectManagementPanelView();
        tabbedPane.addTab("Project Management", projectManagementPanel);
        tabbedPane.setTabComponentAt(1, createTabLabel("Project Management", 1));

        FtpView ftpPanel = new FtpView();
        tabbedPane.addTab("FTP Server", ftpPanel);
        tabbedPane.setTabComponentAt(2, createTabLabel("FTP Server", 2));

        SendGmailPanelView gmailPanel = new SendGmailPanelView();
        tabbedPane.addTab("Send Gmail", gmailPanel);
        tabbedPane.setTabComponentAt(3, createTabLabel("Send Gmail", 3));

       

        JPanel logoutPlaceholder = new JPanel();
        logoutPlaceholder.setOpaque(false);
        tabbedPane.addTab("Logout", logoutPlaceholder);
        tabbedPane.setTabComponentAt(4, createTabLabel("Logout", 4));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTabLabel(String text, int tabIndex) {
        JPanel tabPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (tabbedPane.getSelectedIndex() == tabIndex) {
                    g2d.setColor(new Color(60, 75, 95, 255));
                } else {
                    g2d.setColor(new Color(40, 50, 65, 255));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2d.dispose();
            }
        };
        tabPanel.setOpaque(false);
        tabPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(12, 20, 12, 20));
        tabPanel.add(label, BorderLayout.CENTER);
        tabPanel.setPreferredSize(new Dimension(label.getPreferredSize().width + 40, 45));

        return tabPanel;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void resetToHomeTab() {
        tabbedPane.setSelectedIndex(0);
        // Reset Project Management panel to initial state
        if (projectManagementPanel != null) {
            projectManagementPanel.resetToInitialState();
        }
    }
}
