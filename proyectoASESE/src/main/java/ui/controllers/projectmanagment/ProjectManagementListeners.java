package ui.controllers.projectmanagment;

import ui.views.projectmanagment.ProjectManagementPanelView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;

/**
 * Project Management: factory methods for Swing listeners used by {@link ProjectManagementPanelView}.
 */
public final class ProjectManagementListeners {

    private ProjectManagementListeners() {
    }

    /**
     * Listener for the "Back" action that returns to the "all items" view.
     */
    public static ActionListener createBackButtonListener(ProjectManagementPanelView panel) {
        return e -> panel.onBackToAll();
    }

    /**
     * Listener for the view selector (combo/radio) that changes the current Project Management view.
     */
    public static ActionListener createViewSelectorListener(ProjectManagementPanelView panel) {
        return e -> panel.onViewSelectionChanged();
    }

    /**
     * Listener for table row selection changes; updates the context based on the selected row.
     */
    public static ListSelectionListener createTableSelectionListener(ProjectManagementPanelView panel) {
        return (ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                panel.updateContextFromSelectedRow();
            }
        };
    }
}

