package ui.views.projectmanagment;

/**
 * Project Management: available table views shown in the selector.
 */
public enum ManagementView {
    PROJECTS("Projects"),
    STAGES("Stages"),
    DELIVERIES("Deliveries"),
    PERMISSIONS("Permissions"),
    USERS("Users"),
    WHITELIST("Whitelist"),
    CONTRACTORS("Contractors"),
    CREW("Crew"),
    LOGS("Logs");

    private final String label;

    private ManagementView(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

