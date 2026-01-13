package ui.views.projectmanagment;

import javax.swing.*;
import java.awt.event.ActionListener;

import static ui.controllers.projectmanagment.ProjectManagementActions.*;

/**
 * Project Management: right-click popup for drill-down navigation between views.
 */
public class ProjectManagementPopupMenu extends JPopupMenu {

    public ProjectManagementPopupMenu(ManagementView currentView, int row, ProjectManagementPanelView panel, boolean allowUsersView) {
        switch (currentView) {
            case PROJECTS -> {
                addItem(MENU_VIEW_STAGES, ev -> panel.navigateToStagesFromProject(row));
                if (allowUsersView) {
                    addItem(MENU_VIEW_USERS, ev -> panel.navigateToUsersFromProject(row));
                }
                addItem(MENU_VIEW_CREW, ev -> panel.navigateToCrewFromProject(row));
            }
            case STAGES -> {
                addItem(MENU_VIEW_DELIVERIES, ev -> panel.navigateToDeliveriesFromStage(row));
                addItem(MENU_VIEW_PERMISSIONS, ev -> panel.navigateToPermissionsFromStage(row));
            }
            case USERS -> {
                addItem(MENU_VIEW_PROJECTS, ev -> panel.navigateToProjectsFromUser(row));
                addItem(MENU_VIEW_LOGS, ev -> panel.navigateToLogsFromUser(row));
            }
            case CONTRACTORS -> {
                addItem(MENU_VIEW_CREW, ev -> panel.navigateToCrewFromContractor(row));
            }
            default -> {
            }
        }
    }

    private void addItem(String label, ActionListener listener) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(listener);
        add(item);
    }
}

