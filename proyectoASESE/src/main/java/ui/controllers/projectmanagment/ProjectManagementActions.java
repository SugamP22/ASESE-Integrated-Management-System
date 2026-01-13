package ui.controllers.projectmanagment;

import javax.swing.table.DefaultTableModel;

/**
 * Project Management: shared popup labels + tiny table helpers used by the UI.
 */
public final class ProjectManagementActions {

    private ProjectManagementActions() {
    }

    public static final String MENU_VIEW_STAGES = "View Stages";
    public static final String MENU_VIEW_DELIVERIES = "View Deliveries";
    public static final String MENU_VIEW_PERMISSIONS = "View Permissions";
    public static final String MENU_VIEW_PROJECTS = "View Projects";
    public static final String MENU_VIEW_USERS = "View Users";
    public static final String MENU_VIEW_LOGS = "View Logs";
    public static final String MENU_VIEW_CREW = "View Crew";

    /**
     * Reads a table cell as an {@link Integer} id.
     *
     * @return the id value if the cell contains a {@link Number}, otherwise {@code null}
     */
    public static Integer getIdAsInt(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        if (value instanceof Number idNumber) {
            return idNumber.intValue();
        }
        return null;
    }

    /**
     * Reads a table cell as a string label.
     *
     * @return {@code ""} if the cell is {@code null}; otherwise {@code value.toString()}
     */
    public static String getNameAsString(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        return value != null ? value.toString() : "";
    }
}

