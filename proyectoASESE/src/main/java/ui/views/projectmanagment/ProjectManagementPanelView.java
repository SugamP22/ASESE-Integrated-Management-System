package ui.views.projectmanagment;

import db.*;
import entities.*;
import ui.controllers.projectmanagment.ProjectManagementActions;
import ui.controllers.projectmanagment.ProjectManagementListeners;
import ui.data.projectmanagment.ProjectManagementDataProvider;
import ui.data.projectmanagment.ProjectManagementDataProviderImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Project Management: main panel (table + view selector + right-click navigation + context label).
 */
public class ProjectManagementPanelView extends JPanel {

    private JPanel contentContainer;
    private JPanel listPanel;

    private JButton backButton;
    private JComboBox<ManagementView> viewSelector;
    private JLabel contextLabel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton modifyButton;

    private DefaultTableModel tableModel;
    private JTable dataTable;

    private final ProjectManagementDataProvider dataProvider;
    private final Integer userModeUserId;
    private boolean listenersInstalled;
    private boolean drillDownMode; // true when the current view was entered via right-click navigation
    private boolean viewChangeFromNavigation; // guards spinner listener when we programmatically switch views

    private Integer viewingProjectId;
    private Integer viewingStageId;
    private Integer viewingUserId;
    private Integer viewingContractorId;

    public ProjectManagementPanelView() {
        this(new ProjectManagementDataProviderImpl(), null);
    }

    /**
     * User-mode constructor: filters the Projects view for the given user and hides the USERS view.
     */
    public ProjectManagementPanelView(int userId) {
        this(new ProjectManagementDataProviderImpl(), userId);
    }

    private ProjectManagementPanelView(ProjectManagementDataProvider dataProvider, Integer userModeUserId) {
        this.dataProvider = dataProvider;
        this.userModeUserId = userModeUserId;

        setOpaque(true);
        setBackground(Color.decode("#2f687d"));
        setLayout(new BorderLayout());

        contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);

        listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);

        contentContainer.add(listPanel, BorderLayout.CENTER);
        add(contentContainer, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        backButton = ProjectManagementUi.pillButton("Back to All", ProjectManagementUi.BTN_GRAY);
        backButton.setVisible(false);

        viewSelector = new JComboBox<>(getAvailableViews());
        ProjectManagementUi.styleViewSelector(viewSelector);

        contextLabel = ProjectManagementUi.createContextLabel();

        topPanel.add(backButton);
        topPanel.add(viewSelector);
        topPanel.add(contextLabel);

        listPanel.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dataTable = new JTable(tableModel);
        ProjectManagementUi.styleTable(dataTable);

        JScrollPane scrollPane = ProjectManagementUi.createTableScrollPane(dataTable);

        listPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        buttonPanel.setOpaque(false);

        addButton = createActionButton("Add");
        deleteButton = createActionButton("Delete");
        modifyButton = createActionButton("Modify");

        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 30, 20, 30));
        wrapper.add(buttonPanel, BorderLayout.CENTER);

        listPanel.add(wrapper, BorderLayout.SOUTH);

        loadProjects();
    }

    private boolean isUserMode() {
        return userModeUserId != null;
    }

    private ManagementView[] getAvailableViews() {
        if (!isUserMode()) {
            return ManagementView.values();
        }
        // USER mode: allow CRUD for entities within assigned projects.
        // (Exclude admin-only views like USERS and WHITELIST.)
        return new ManagementView[]{
                ManagementView.PROJECTS,
                ManagementView.STAGES,
                ManagementView.DELIVERIES,
                ManagementView.PERMISSIONS,
                ManagementView.CONTRACTORS,
                ManagementView.CREW,
                ManagementView.LOGS
        };
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (!listenersInstalled) {
            listenersInstalled = true;
            installListeners();
        }
    }

    private void installListeners() {
        backButton.addActionListener(ProjectManagementListeners.createBackButtonListener(this));
        viewSelector.addActionListener(ProjectManagementListeners.createViewSelectorListener(this));

        dataTable.getSelectionModel().addListSelectionListener(
                ProjectManagementListeners.createTableSelectionListener(this)
        );

        installTablePopup();

        deleteButton.addActionListener(e -> deleteSelectedRow());
        addButton.addActionListener(e -> addNewRow());
        modifyButton.addActionListener(e -> modifySelectedRow());
    }

    private void addNewRow() {
        ManagementView view = (ManagementView) viewSelector.getSelectedItem();
        if (view == null || view == ManagementView.LOGS) {
            return;
        }

        int newId = 0; // ID will be auto-generated by database
        Object[] row = ProjectManagementCrudDialogs.showAddDialog(
                this,
                view,
                dataProvider,
                newId,
                viewingProjectId,
                viewingStageId,
                viewingContractorId,
                drillDownMode,
                userModeUserId
        );
        if (row == null) {
            return;
        }

        // Convert Object[] to entity and persist to database
        try {
            persistEntityToDatabase(view, row, true);
            // Reload table from database
            reloadCurrentView();
            JOptionPane.showMessageDialog(
                    this,
                    "Item added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error saving to database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void modifySelectedRow() {
        ManagementView view = (ManagementView) viewSelector.getSelectedItem();
        if (view == null || view == ManagementView.LOGS) {
            return;
        }

        int viewRow = dataTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item.",
                    "No selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Object[] existing = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            existing[i] = tableModel.getValueAt(modelRow, i);
        }

        Object[] updated = ProjectManagementCrudDialogs.showModifyDialog(
                this,
                view,
                dataProvider,
                existing,
                viewingProjectId,
                viewingStageId,
                viewingContractorId,
                drillDownMode,
                userModeUserId
        );
        if (updated == null) {
            return;
        }

        // Convert Object[] to entity and persist to database
        try {
            if (view == ManagementView.WHITELIST) {
                // Keep old email so we can update the key (email) in DB.
                String oldEmail = existing[0] != null ? existing[0].toString() : null;
                String newEmail = updated.length > 0 && updated[0] != null ? updated[0].toString() : null;
                updated = new Object[]{newEmail, oldEmail};
            }
            persistEntityToDatabase(view, updated, false);
            // Reload table from database
            reloadCurrentView();
            // Try to restore selection
            if (viewRow >= 0 && viewRow < dataTable.getRowCount()) {
                dataTable.setRowSelectionInterval(viewRow, viewRow);
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Item modified successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error updating database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSelectedRow() {
        ManagementView view = (ManagementView) viewSelector.getSelectedItem();
        if (view == null || view == ManagementView.LOGS) {
            return;
        }

        int viewRow = dataTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item.",
                    "No selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= tableModel.getRowCount()) {
            return;
        }

        // Whitelist is keyed by email (String), not by numeric ID.
        if (view == ManagementView.WHITELIST) {
            String email = tableModel.getValueAt(modelRow, 0) != null ? tableModel.getValueAt(modelRow, 0).toString() : null;
            if (email == null || email.isBlank()) {
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this email from the whitelist?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                WhitelistRepository.deleteEmail(email);
                reloadCurrentView();
                dataTable.clearSelection();
                JOptionPane.showMessageDialog(
                        this,
                        "Item deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error deleting from database: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            return;
        }

        // Get ID from selected row
        Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (id == null) {
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete from database
        try {
            deleteEntityFromDatabase(view, id);
            // Reload table from database
            reloadCurrentView();
            dataTable.clearSelection();
            JOptionPane.showMessageDialog(
                    this,
                    "Item deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting from database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JButton createActionButton(String text) {
        if ("Delete".equalsIgnoreCase(text)) {
            return ProjectManagementUi.pillButton(text, ProjectManagementUi.BTN_GRAY);
        }
        return ProjectManagementUi.pillButton(text, ProjectManagementUi.BTN_GREEN);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.decode("#2f687d"));
        int margin = 16;
        int arc = 25;
        int width = getWidth() - 2 * margin;
        int height = getHeight() - 2 * margin;
        if (width > 0 && height > 0) {
            g2d.fillRoundRect(margin, margin, width, height, arc, arc);
        }
        g2d.dispose();
    }

    public void onViewSelectionChanged() {
        ManagementView view = (ManagementView) viewSelector.getSelectedItem();
        if (view == null) {
            return;
        }
        if (isUserMode() && view == ManagementView.USERS) {
            viewSelector.setSelectedItem(ManagementView.PROJECTS);
            return;
        }

        boolean fromNavigation = viewChangeFromNavigation;
        viewChangeFromNavigation = false;

        if (!fromNavigation) {
            // Spinner browsing: always show full data for that view (no drill-down context).
            exitDrillDownMode();
            switch (view) {
                case PROJECTS -> loadProjects();
                case STAGES -> {
                    if (isUserMode()) {
                        loadStagesForUser(userModeUserId);
                    } else {
                        loadAllStages();
                    }
                }
                case DELIVERIES -> {
                    if (isUserMode()) {
                        loadDeliveriesForUser(userModeUserId);
                    } else {
                        loadAllDeliveries();
                    }
                }
                case PERMISSIONS -> {
                    if (isUserMode()) {
                        loadPermissionsForUser(userModeUserId);
                    } else {
                        loadAllPermissions();
                    }
                }
                case USERS -> loadUsers();
                case WHITELIST -> loadWhitelist();
                case CONTRACTORS -> {
                    if (isUserMode()) {
                        loadContractors();
                    } else {
                        loadContractors();
                    }
                }
                case CREW -> {
                    if (isUserMode()) {
                        loadCrewForUser(userModeUserId);
                    } else {
                        loadAllCrew();
                    }
                }
                case LOGS -> {
                    if (isUserMode()) {
                        loadLogsForUser(userModeUserId);
                    } else {
                        loadAllLogs();
                    }
                }
            }
        } else {
            // Right-click drill-down navigation: keep the current context-based behavior.
            drillDownMode = true;
            updateBackButtonVisibility();
            switch (view) {
                case PROJECTS -> {
                    if (viewingUserId != null) {
                        loadProjectsForUser(viewingUserId);
                    } else {
                        loadProjects();
                    }
                }
                case STAGES -> {
                    if (viewingProjectId != null) {
                        loadStagesForProject(viewingProjectId);
                    } else {
                        loadAllStages();
                    }
                }
                case DELIVERIES -> {
                    if (viewingStageId != null) {
                        loadDeliveriesForStage(viewingStageId);
                    } else {
                        loadAllDeliveries();
                    }
                }
                case PERMISSIONS -> {
                    if (viewingStageId != null) {
                        loadPermissionsForStage(viewingStageId);
                    } else {
                        loadAllPermissions();
                    }
                }
                case USERS -> {
                    if (viewingProjectId != null) {
                        loadUsersForProject(viewingProjectId);
                    } else {
                        loadUsers();
                    }
                }
                case WHITELIST -> loadWhitelist();
                case CONTRACTORS -> {
                    if (isUserMode()) {
                        loadContractorsForUser(userModeUserId);
                    } else {
                        loadContractors();
                    }
                }
                case CREW -> {
                    if (viewingProjectId != null) {
                        loadCrewForProject(viewingProjectId);
                    } else if (viewingContractorId != null) {
                        loadCrewForContractor(viewingContractorId);
                    } else if (isUserMode()) {
                        loadCrewForUser(userModeUserId);
                    } else {
                        loadAllCrew();
                    }
                }
                case LOGS -> {
                    if (viewingUserId != null) {
                        loadLogsForUser(viewingUserId);
                    } else {
                        loadAllLogs();
                    }
                }
            }
        }
        updateCrudButtonsForView(view);
        // Clear selection so the context label updates consistently for the new view.
        dataTable.clearSelection();
    }

    @SuppressWarnings("unused")
    public void onBackToAll() {
        exitDrillDownMode();
        viewSelector.setSelectedItem(ManagementView.PROJECTS);
        loadProjects();
        dataTable.clearSelection();
    }

    private void loadProjects() {
        if (userModeUserId != null) {
            loadProjectsForUser(userModeUserId);
            return;
        }
        loadTable(new String[]{"Project ID", "Name"}, dataProvider.getProjects());
    }

    private void loadProjectsForUser(int userId) {
        loadTable(new String[]{"Project ID", "Name"}, dataProvider.getProjectsForUser(userId));
    }

    private void loadStagesForProject(int projectId) {
        loadTable(
                new String[]{
                        "Stage ID", "Project ID", "Name", "Description",
                        "Initial Date", "Final Date", "Permission Count",
                        "Initial Delivery Count", "Final Delivery Count"
                },
                dataProvider.getStagesForProject(projectId),
                1
        );
    }

    private void loadAllStages() {
        loadTable(
                new String[]{
                        "Stage ID", "Project ID", "Name", "Description",
                        "Initial Date", "Final Date", "Permission Count",
                        "Initial Delivery Count", "Final Delivery Count"
                },
                dataProvider.getAllStages(),
                1
        );
    }

    private void loadDeliveriesForStage(int stageId) {
        loadTable(
                new String[]{
                        "Delivery ID", "Stage ID", "Material", "Description", "Type"
                },
                dataProvider.getDeliveriesForStage(stageId),
                1
        );
    }

    private void loadAllDeliveries() {
        loadTable(
                new String[]{
                        "Delivery ID", "Stage ID", "Material", "Description", "Type"
                },
                dataProvider.getAllDeliveries(),
                1
        );
    }

    private void loadPermissionsForStage(int stageId) {
        loadTable(
                new String[]{
                        "Permission ID", "Stage ID", "Name", "Description"
                },
                dataProvider.getPermissionsForStage(stageId),
                1
        );
    }

    private void loadAllPermissions() {
        loadTable(
                new String[]{
                        "Permission ID", "Stage ID", "Name", "Description"
                },
                dataProvider.getAllPermissions(),
                1
        );
    }

    private void loadUsers() {
        loadTable(
                new String[]{
                        "User ID", "First Name", "Surname", "Email", "Role"
                },
                dataProvider.getUsers(),
                null
        );
    }

    private void loadWhitelist() {
        loadTable(
                new String[]{"Email"},
                dataProvider.getWhitelist(),
                null
        );
    }

    private void loadUsersForProject(int projectId) {
        loadTable(
                new String[]{
                        "User ID", "First Name", "Surname", "Email", "Role"
                },
                dataProvider.getUsersForProject(projectId),
                null
        );
    }

    private void loadContractors() {
        loadTable(
                new String[]{
                        "Contractor ID", "Name", "Address"
                },
                dataProvider.getContractors(),
                null
        );
    }

    private void loadAllCrew() {
        loadTable(
                new String[]{
                        "Crew ID", "Name", "Job Type", "Contractor ID", "Project ID"
                },
                dataProvider.getCrew(),
                3, 4
        );
    }

    private void loadCrewForProject(int projectId) {
        loadTable(
                new String[]{
                        "Crew ID", "Name", "Job Type", "Contractor ID", "Project ID"
                },
                dataProvider.getCrewForProject(projectId),
                3, 4
        );
    }

    private void loadCrewForContractor(int contractorId) {
        loadTable(
                new String[]{
                        "Crew ID", "Name", "Job Type", "Contractor ID", "Project ID"
                },
                dataProvider.getCrewForContractor(contractorId),
                3, 4
        );
    }

    private void loadAllLogs() {
        loadTable(
                new String[]{
                        "Log ID", "Date", "Description", "User ID", "User Email"
                },
                dataProvider.getLogs(),
                null
        );
    }

    private void loadLogsForUser(int userId) {
        loadTable(
                new String[]{
                        "Log ID", "Date", "Description", "User ID", "User Email"
                },
                dataProvider.getLogsForUser(userId),
                null
        );
    }

    private void loadStagesForUser(int userId) {
        loadTable(
                new String[]{
                        "Stage ID", "Project ID", "Name", "Description",
                        "Initial Date", "Final Date", "Permission Count",
                        "Initial Delivery Count", "Final Delivery Count"
                },
                dataProvider.getStagesForUser(userId),
                1
        );
    }

    private void loadDeliveriesForUser(int userId) {
        loadTable(
                new String[]{
                        "Delivery ID", "Stage ID", "Material", "Description", "Type"
                },
                dataProvider.getDeliveriesForUser(userId),
                1
        );
    }

    private void loadPermissionsForUser(int userId) {
        loadTable(
                new String[]{
                        "Permission ID", "Stage ID", "Name", "Description"
                },
                dataProvider.getPermissionsForUser(userId),
                1
        );
    }

    private void loadCrewForUser(int userId) {
        loadTable(
                new String[]{
                        "Crew ID", "Name", "Job Type", "Contractor ID", "Project ID"
                },
                dataProvider.getCrewForUser(userId),
                3, 4
        );
    }

    private void loadContractorsForUser(int userId) {
        loadTable(
                new String[]{
                        "Contractor ID", "Name", "Address"
                },
                dataProvider.getContractorsForUser(userId),
                null
        );
    }

    private void loadTable(String[] columns, Object[][] data, int... hiddenColumnIndexes) {
        setTableColumns(columns);
        fillTable(data);
        if (hiddenColumnIndexes != null) {
            for (int index : hiddenColumnIndexes) {
                hideColumn(index);
            }
        }
    }

    private void updateCrudButtonsForView(ManagementView view) {
        boolean enableCrud = view != ManagementView.LOGS;
        addButton.setEnabled(enableCrud);
        deleteButton.setEnabled(enableCrud);
        modifyButton.setEnabled(enableCrud);
    }

    private void setTableColumns(String[] columns) {
        tableModel.setDataVector(new Object[0][0], columns);
        restoreAllColumnsWidth();
    }

    private void fillTable(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }

    private void hideColumn(int index) {
        TableColumnModel columnModel = dataTable.getColumnModel();
        if (index >= 0 && index < columnModel.getColumnCount()) {
            TableColumn column = columnModel.getColumn(index);
            column.setMinWidth(0);
            column.setMaxWidth(0);
            column.setPreferredWidth(0);
            column.setResizable(false);
        }
    }

    private void restoreAllColumnsWidth() {
        ProjectManagementUi.restoreAllColumnsWidth(dataTable);
    }

    private void installTablePopup() {
        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    return;
                }
                int row = dataTable.rowAtPoint(e.getPoint());
                if (row < 0) {
                    return;
                }
                dataTable.setRowSelectionInterval(row, row);
                showPopupMenu(e, row);
            }
        });
    }

    private void showPopupMenu(MouseEvent e, int row) {
        ManagementView view = (ManagementView) viewSelector.getSelectedItem();
        if (view == null) {
            return;
        }
        ProjectManagementPopupMenu popup = new ProjectManagementPopupMenu(view, row, this, !isUserMode());
        if (popup.getComponentCount() > 0) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void navigateToStagesFromProject(int viewRow) {
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (projectId == null) {
            return;
        }
        viewingProjectId = projectId;
        viewingStageId = null;
        viewingContractorId = null;
        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.STAGES);
    }

    public void navigateToUsersFromProject(int viewRow) {
        if (isUserMode()) {
            return;
        }
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (projectId == null) {
            return;
        }
        viewingProjectId = projectId;
        viewingStageId = null;
        viewingContractorId = null;
        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.USERS);
    }

    public void navigateToCrewFromProject(int viewRow) {
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (projectId == null) {
            return;
        }
        viewingProjectId = projectId;
        viewingStageId = null;
        viewingContractorId = null;
        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.CREW);
    }

    public void navigateToDeliveriesFromStage(int viewRow) {
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (stageId == null) {
            return;
        }
        Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
        viewingStageId = stageId;
        if (projectId != null) {
            viewingProjectId = projectId;
        }
        viewingContractorId = null;
        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.DELIVERIES);
    }

    public void navigateToPermissionsFromStage(int viewRow) {
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (stageId == null) {
            return;
        }
        Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
        viewingStageId = stageId;
        if (projectId != null) {
            viewingProjectId = projectId;
        }
        viewingContractorId = null;
        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.PERMISSIONS);
    }

    public void navigateToProjectsFromUser(int viewRow) {
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer userId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (userId == null) {
            return;
        }
        viewingUserId = userId;
        viewingProjectId = null;
        viewingStageId = null;
        viewingContractorId = null;

        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.PROJECTS);
    }

    public void navigateToLogsFromUser(int viewRow) {
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer userId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (userId == null) {
            return;
        }
        viewingUserId = userId;
        viewingProjectId = null;
        viewingStageId = null;
        viewingContractorId = null;
        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.LOGS);
    }

    public void navigateToCrewFromContractor(int viewRow) {
        int modelRow = dataTable.convertRowIndexToModel(viewRow);
        Integer contractorId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
        if (contractorId == null) {
            return;
        }
        viewingContractorId = contractorId;
        viewingProjectId = null;
        viewingStageId = null;
        markNavigationViewChange();
        viewSelector.setSelectedItem(ManagementView.CREW);
    }

    /**
     * Updates the context label based on the currently selected row in the table.
     * This method extracts the full hierarchy (User -> Project -> Stage) from the selected row.
     */
    public void updateContextFromSelectedRow() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow < 0) {
            contextLabel.setText("");
            contextLabel.setVisible(false);
            return;
        }

        int modelRow = dataTable.convertRowIndexToModel(selectedRow);
        ManagementView currentView = (ManagementView) viewSelector.getSelectedItem();
        if (currentView == null) {
            return;
        }

        String label = drillDownMode
                ? buildBreadcrumbLabel(modelRow, currentView)
                : buildSelectedOnlyLabel(modelRow, currentView);

        contextLabel.setText(label);
        contextLabel.setVisible(!label.isBlank());
    }

    /**
     * Resets all context tracking variables.
     */
    private void resetContext() {
        viewingUserId = null;
        viewingProjectId = null;
        viewingStageId = null;
        viewingContractorId = null;
    }
    
    /**
     * Resets the panel to its initial state (called when tab becomes visible after login).
     */
    public void resetToInitialState() {
        // Reset drill-down mode
        drillDownMode = false;
        viewChangeFromNavigation = false;
        updateBackButtonVisibility();
        
        // Reset context variables
        resetContext();
        
        // Reset view selector to default (PROJECTS)
        viewSelector.setSelectedItem(ManagementView.PROJECTS);
        
        // Clear table selection
        dataTable.clearSelection();
        
        // Clear context label
        contextLabel.setText("");
        contextLabel.setVisible(false);
        
        // Reload the default view
        loadProjects();
    }

    private String buildSelectedOnlyLabel(int modelRow, ManagementView view) {
        // For employees, always show the full breadcrumb path starting with User
        if (isUserMode()) {
            return buildEmployeeBreadcrumbLabel(modelRow, view);
        }
        // For admins, show the relationship path (Project > Stage > Delivery/etc)
        return buildAdminBreadcrumbLabel(modelRow, view);
    }
    
    /**
     * Builds a breadcrumb label for admins showing relationships: Project > Stage > Delivery/Permission
     */
    private String buildAdminBreadcrumbLabel(int modelRow, ManagementView view) {
        StringBuilder sb = new StringBuilder();
        
        switch (view) {
            case PROJECTS -> {
                Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String projectName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                if (projectId != null) {
                    appendSegment(sb, "Project " + projectId + " - " + projectName);
                }
            }
            case STAGES -> {
                Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
                String stageName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (projectId != null) {
                    String projectName = dataProvider.findProjectNameById(projectId);
                    appendSegment(sb, "Project " + projectId + " - " + projectName);
                }
                if (stageId != null) {
                    appendSegment(sb, "Stage " + stageId + " - " + stageName);
                }
            }
            case DELIVERIES -> {
                Integer deliveryId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
                String deliveryMaterial = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (stageId != null) {
                    Integer projectId = dataProvider.findProjectIdByStageId(stageId);
                    String stageName = dataProvider.findStageNameById(stageId);
                    if (projectId != null) {
                        String projectName = dataProvider.findProjectNameById(projectId);
                        appendSegment(sb, "Project " + projectId + " - " + projectName);
                    }
                    appendSegment(sb, "Stage " + stageId + " - " + stageName);
                }
                if (deliveryId != null) {
                    appendSegment(sb, "Delivery " + deliveryId + " - " + deliveryMaterial);
                }
            }
            case PERMISSIONS -> {
                Integer permissionId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
                String permissionName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (stageId != null) {
                    Integer projectId = dataProvider.findProjectIdByStageId(stageId);
                    String stageName = dataProvider.findStageNameById(stageId);
                    if (projectId != null) {
                        String projectName = dataProvider.findProjectNameById(projectId);
                        appendSegment(sb, "Project " + projectId + " - " + projectName);
                    }
                    appendSegment(sb, "Stage " + stageId + " - " + stageName);
                }
                if (permissionId != null) {
                    appendSegment(sb, "Permission " + permissionId + " - " + permissionName);
                }
            }
            case USERS -> {
                Integer userId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String firstName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                String surname = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (userId != null) {
                    String fullName = firstName + (surname.isBlank() ? "" : " " + surname);
                    appendSegment(sb, "User " + userId + " - " + fullName.trim());
                }
            }
            case WHITELIST -> {
                String email = ProjectManagementActions.getNameAsString(tableModel, modelRow, 0);
                if (!email.isBlank()) {
                    appendSegment(sb, "Whitelist - " + email);
                }
            }
            case CONTRACTORS -> {
                Integer contractorId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String contractorName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                if (contractorId != null) {
                    appendSegment(sb, "Contractor " + contractorId + " - " + contractorName);
                }
            }
            case CREW -> {
                Integer crewId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String crewName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 4);
                if (projectId != null) {
                    String projectName = dataProvider.findProjectNameById(projectId);
                    appendSegment(sb, "Project " + projectId + " - " + projectName);
                }
                if (crewId != null) {
                    appendSegment(sb, "Crew " + crewId + " - " + crewName);
                }
            }
            case LOGS -> {
                Integer logId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String date = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                String description = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                Integer userId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 3);
                if (userId != null) {
                    String[] userInfo = dataProvider.findUserInfoById(userId);
                    String userName = (userInfo != null && userInfo.length > 0) ? userInfo[0] : "";
                    appendSegment(sb, "User " + userId + " - " + userName);
                }
                if (logId != null) {
                    appendSegment(sb, "Log " + logId + (date.isBlank() ? "" : " - " + date) + (description.isBlank() ? "" : " - " + description));
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Builds a breadcrumb label for employees showing: User > Project > Stage > Delivery/Permission
     */
    private String buildEmployeeBreadcrumbLabel(int modelRow, ManagementView view) {
        StringBuilder sb = new StringBuilder();
        
        // Always start with current user
        if (userModeUserId != null) {
            var currentUser = authentication.AuthenticationService.getCurrentUser();
            if (currentUser != null) {
                String userName = currentUser.getName() + " " + currentUser.getSurname();
                appendSegment(sb, "User " + userModeUserId + " - " + userName.trim());
            }
        }
        
        // Build path based on view type
        switch (view) {
            case PROJECTS -> {
                Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String projectName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                if (projectId != null) {
                    appendSegment(sb, "Project " + projectId + " - " + projectName);
                }
            }
            case STAGES -> {
                Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                Integer projectId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
                String stageName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (projectId != null) {
                    String projectName = dataProvider.findProjectNameById(projectId);
                    appendSegment(sb, "Project " + projectId + " - " + projectName);
                }
                if (stageId != null) {
                    appendSegment(sb, "Stage " + stageId + " - " + stageName);
                }
            }
            case DELIVERIES -> {
                Integer deliveryId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
                String deliveryMaterial = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (stageId != null) {
                    Integer projectId = dataProvider.findProjectIdByStageId(stageId);
                    String stageName = dataProvider.findStageNameById(stageId);
                    if (projectId != null) {
                        String projectName = dataProvider.findProjectNameById(projectId);
                        appendSegment(sb, "Project " + projectId + " - " + projectName);
                    }
                    appendSegment(sb, "Stage " + stageId + " - " + stageName);
                }
                if (deliveryId != null) {
                    appendSegment(sb, "Delivery " + deliveryId + " - " + deliveryMaterial);
                }
            }
            case PERMISSIONS -> {
                Integer permissionId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                Integer stageId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 1);
                String permissionName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (stageId != null) {
                    Integer projectId = dataProvider.findProjectIdByStageId(stageId);
                    String stageName = dataProvider.findStageNameById(stageId);
                    if (projectId != null) {
                        String projectName = dataProvider.findProjectNameById(projectId);
                        appendSegment(sb, "Project " + projectId + " - " + projectName);
                    }
                    appendSegment(sb, "Stage " + stageId + " - " + stageName);
                }
                if (permissionId != null) {
                    appendSegment(sb, "Permission " + permissionId + " - " + permissionName);
                }
            }
            case LOGS -> {
                // For logs, just show the log info (already user-specific)
                Integer logId = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String date = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                String description = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (logId != null) {
                    appendSegment(sb, "Log " + logId + (date.isBlank() ? "" : " - " + date) + (description.isBlank() ? "" : " - " + description));
                }
            }
            case USERS, WHITELIST, CONTRACTORS, CREW -> {
                // Not shown in employee mode, but handle for exhaustiveness.
            }
        }
        
        return sb.toString();
    }

    private String buildBreadcrumbLabel(int modelRow, ManagementView view) {
        StringBuilder sb = new StringBuilder();
        if (viewingUserId != null) {
            String[] userInfo = dataProvider.findUserInfoById(viewingUserId);
            String userName = (userInfo != null && userInfo.length > 0) ? userInfo[0] : "";
            appendSegment(sb, "User " + viewingUserId + " - " + userName);
        }
        if (viewingProjectId != null) {
            appendSegment(sb, "Project " + viewingProjectId + " - " + dataProvider.findProjectNameById(viewingProjectId));
        }
        if (viewingStageId != null) {
            appendSegment(sb, "Stage " + viewingStageId + " - " + dataProvider.findStageNameById(viewingStageId));
        }
        if (viewingContractorId != null) {
            appendSegment(sb, "Contractor " + viewingContractorId + " - " + dataProvider.findContractorNameById(viewingContractorId));
        }

        appendSegment(sb, buildRowLabel(modelRow, view));
        return sb.toString();
    }

    private String buildRowLabel(int modelRow, ManagementView view) {
        switch (view) {
            case USERS -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String firstName = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                String surname = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (id == null) return "";
                String fullName = firstName + (surname.isBlank() ? "" : " " + surname);
                return "User " + id + " - " + fullName;
            }
            case WHITELIST -> {
                String email = ProjectManagementActions.getNameAsString(tableModel, modelRow, 0);
                return email.isBlank() ? "" : "Whitelist - " + email;
            }
            case PROJECTS -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String name = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                if (id == null) return "";
                return "Project " + id + " - " + name;
            }
            case STAGES -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String name = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (id == null) return "";
                return "Stage " + id + " - " + name;
            }
            case DELIVERIES -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String material = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                String type = ProjectManagementActions.getNameAsString(tableModel, modelRow, 4);
                if (id == null) return "";
                return "Delivery " + id + " - " + material + (type.isBlank() ? "" : " (" + type + ")");
            }
            case PERMISSIONS -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String name = ProjectManagementActions.getNameAsString(tableModel, modelRow, 2);
                if (id == null) return "";
                return "Permission " + id + " - " + name;
            }
            case CONTRACTORS -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String name = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                if (id == null) return "";
                return "Contractor " + id + " - " + name;
            }
            case CREW -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String name = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                if (id == null) return "";
                return "Crew " + id + " - " + name;
            }
            case LOGS -> {
                Integer id = ProjectManagementActions.getIdAsInt(tableModel, modelRow, 0);
                String date = ProjectManagementActions.getNameAsString(tableModel, modelRow, 1);
                if (id == null) return "";
                return "Log " + id + (date.isBlank() ? "" : " - " + date);
            }
        }
        return "";
    }

    private void appendSegment(StringBuilder sb, String segment) {
        if (segment == null || segment.isBlank()) {
            return;
        }
        if (!sb.isEmpty()) {
            sb.append(" > ");
        }
        sb.append(segment);
    }

    private void exitDrillDownMode() {
        drillDownMode = false;
        viewChangeFromNavigation = false;
        resetContext();
        updateBackButtonVisibility();
    }

    private void markNavigationViewChange() {
        drillDownMode = true;
        viewChangeFromNavigation = true;
        updateBackButtonVisibility();
    }

    private void updateBackButtonVisibility() {
        backButton.setVisible(drillDownMode);
    }

    /**
     * Reloads the current view from the database based on the selected view and context.
     */
    private void reloadCurrentView() {
        ManagementView view = (ManagementView) viewSelector.getSelectedItem();
        if (view == null) {
            return;
        }

        switch (view) {
            case PROJECTS -> {
                if (userModeUserId != null) {
                    loadProjectsForUser(userModeUserId);
                } else if (viewingUserId != null) {
                    loadProjectsForUser(viewingUserId);
                } else {
                    loadProjects();
                }
            }
            case STAGES -> {
                if (viewingProjectId != null) {
                    loadStagesForProject(viewingProjectId);
                } else if (userModeUserId != null) {
                    loadStagesForUser(userModeUserId);
                } else {
                    loadAllStages();
                }
            }
            case DELIVERIES -> {
                if (viewingStageId != null) {
                    loadDeliveriesForStage(viewingStageId);
                } else if (userModeUserId != null) {
                    loadDeliveriesForUser(userModeUserId);
                } else {
                    loadAllDeliveries();
                }
            }
            case PERMISSIONS -> {
                if (viewingStageId != null) {
                    loadPermissionsForStage(viewingStageId);
                } else if (userModeUserId != null) {
                    loadPermissionsForUser(userModeUserId);
                } else {
                    loadAllPermissions();
                }
            }
            case USERS -> {
                if (viewingProjectId != null) {
                    loadUsersForProject(viewingProjectId);
                } else {
                    loadUsers();
                }
            }
            case WHITELIST -> loadWhitelist();
            case CONTRACTORS -> {
                if (userModeUserId != null) {
                    loadContractors();
                } else {
                    loadContractors();
                }
            }
            case CREW -> {
                if (viewingProjectId != null) {
                    loadCrewForProject(viewingProjectId);
                } else if (viewingContractorId != null) {
                    loadCrewForContractor(viewingContractorId);
                } else if (userModeUserId != null) {
                    loadCrewForUser(userModeUserId);
                } else {
                    loadAllCrew();
                }
            }
            case LOGS -> {
                if (userModeUserId != null) {
                    loadLogsForUser(userModeUserId);
                } else if (viewingUserId != null) {
                    loadLogsForUser(viewingUserId);
                } else {
                    loadAllLogs();
                }
            }
        }
        dataTable.clearSelection();
    }

    /**
     * Converts Object[] to entity and persists to database (add or update).
     */
    private void persistEntityToDatabase(ManagementView view, Object[] row, boolean isAdd) {
        switch (view) {
            case PROJECTS -> {
                Project project = convertToProject(row);
                if (isAdd) {
                    ProjectRepository.addProject(project);
                } else {
                    ProjectRepository.updateProject(project);
                }
            }
            case STAGES -> {
                Stage stage = convertToStage(row);
                if (isAdd) {
                    StageRepository.addStage(stage);
                } else {
                    StageRepository.updateStage(stage);
                }
            }
            case DELIVERIES -> {
                Delivery delivery = convertToDelivery(row);
                if (isAdd) {
                    DeliveryRepository.addDelivery(delivery);
                } else {
                    DeliveryRepository.updateDelivery(delivery);
                }
            }
            case PERMISSIONS -> {
                Permission permission = convertToPermission(row);
                if (isAdd) {
                    PermissionRepository.addPermission(permission);
                } else {
                    PermissionRepository.updatePermission(permission);
                }
            }
            case USERS -> {
                User user = convertToUser(row);
                if (isAdd) {
                    UserRepository.addUser(user);
                } else {
                    UserRepository.updateUser(user);
                    // Optional password change (dialog may include a "New Password" field)
                    String newPassword = extractUserPassword(row);
                    if (newPassword != null && !newPassword.isBlank()) {
                        UserRepository.updateUserPassword(user.getId(), newPassword);
                    }
                }
            }
            case WHITELIST -> {
                if (isAdd) {
                    String email = row != null && row.length > 0 && row[0] != null ? row[0].toString() : null;
                    if (email == null || email.isBlank()) {
                        throw new IllegalArgumentException("Email is required.");
                    }
                    WhitelistRepository.addEmail(email);
                } else {
                    // row: [newEmail, oldEmail]
                    String newEmail = row != null && row.length > 0 && row[0] != null ? row[0].toString() : null;
                    String oldEmail = row != null && row.length > 1 && row[1] != null ? row[1].toString() : null;
                    if (oldEmail == null || oldEmail.isBlank()) {
                        throw new IllegalArgumentException("Old email is required.");
                    }
                    if (newEmail == null || newEmail.isBlank()) {
                        throw new IllegalArgumentException("Email is required.");
                    }
                    if (!oldEmail.equalsIgnoreCase(newEmail)) {
                        WhitelistRepository.updateEmail(oldEmail, newEmail);
                    }
                }
            }
            case CONTRACTORS -> {
                Contractor contractor = convertToContractor(row);
                if (isAdd) {
                    ContractorRepository.addContractor(contractor);
                } else {
                    ContractorRepository.updateContractor(contractor);
                }
            }
            case CREW -> {
                Crew crew = convertToCrew(row);
                if (isAdd) {
                    CrewRepository.addCrew(crew);
                } else {
                    CrewRepository.updateCrew(crew);
                }
            }
            case LOGS -> {
                // Logs are read-only in UI, but handle for completeness
                Log log = convertToLog(row);
                if (isAdd) {
                    LogRepository.addLog(log);
                } else {
                    LogRepository.updateLog(log);
                }
            }
        }
    }

    /**
     * Deletes an entity from the database.
     */
    private void deleteEntityFromDatabase(ManagementView view, int id) {
        switch (view) {
            case PROJECTS -> ProjectRepository.deleteProject(id);
            case STAGES -> StageRepository.deleteStage(id);
            case DELIVERIES -> DeliveryRepository.deleteDelivery(id);
            case PERMISSIONS -> PermissionRepository.deletePermission(id);
            case USERS -> UserRepository.deleteUser(id);
            case WHITELIST -> {
                // Whitelist is keyed by email, delete is handled in deleteSelectedRow().
            }
            case CONTRACTORS -> ContractorRepository.deleteContractor(id);
            case CREW -> CrewRepository.deleteCrew(id);
            case LOGS -> LogRepository.deleteLog(id);
        }
    }

    // Conversion methods: Object[] -> Entity

    private Project convertToProject(Object[] row) {
        int id = ((Number) row[0]).intValue();
        String name = row[1].toString();
        return new Project(id, name);
    }

    private Stage convertToStage(Object[] row) {
        int id = ((Number) row[0]).intValue();
        int idProject = ((Number) row[1]).intValue();
        String name = row[2].toString();
        String description = row[3] != null ? row[3].toString() : null;
        LocalDate initialDate = parseDate(row[4]);
        LocalDate finalDate = parseDate(row[5]);
        if (initialDate == null) {
            initialDate = LocalDate.now();
        }
        int permissionCount = ((Number) row[6]).intValue();
        int initialDeliveryCount = ((Number) row[7]).intValue();
        int finalDeliveryCount = ((Number) row[8]).intValue();
        return new Stage(id, idProject, name, description, initialDate, finalDate,
                permissionCount, initialDeliveryCount, finalDeliveryCount);
    }

    private Delivery convertToDelivery(Object[] row) {
        int id = ((Number) row[0]).intValue();
        int idStage = ((Number) row[1]).intValue();
        String material = row[2] != null ? row[2].toString() : null;
        String description = row[3] != null ? row[3].toString() : null;
        Delivery.DeliveryTiming timing = Delivery.DeliveryTiming.valueOf(row[4].toString());
        return new Delivery(id, material, description, idStage, timing);
    }

    private Permission convertToPermission(Object[] row) {
        int id = ((Number) row[0]).intValue();
        int idStage = ((Number) row[1]).intValue();
        String name = row[2].toString();
        String description = row[3] != null ? row[3].toString() : null;
        return new Permission(id, name, description, idStage);
    }

    private User convertToUser(Object[] row) {
        int id = ((Number) row[0]).intValue();
        String firstName = row[1].toString();
        String surname = row[2].toString();
        String email = row[3].toString();
        String emailToken = extractUserEmailToken(row);
        String password = extractUserPassword(row);
        User.UserRol rol = User.UserRol.valueOf(row[4].toString());
        // For update, preserve emailToken/password unless user supplied new values in the dialog.
        if (id > 0) {
            var existing = UserRepository.getUserById(id);
            if (existing.isPresent()) {
                User existingUser = existing.get();
                if (emailToken == null || emailToken.isBlank()) {
                    emailToken = existingUser.getEmailToken();
                }
                if (password == null || password.isBlank()) {
                    password = existingUser.getPassword();
                }
            }
        }

        // For add, if token was left blank, generate one (repository does not do this today).
        if (id <= 0 && (emailToken == null || emailToken.isBlank())) {
            emailToken = UUID.randomUUID().toString();
        }
        if (id <= 0 && (password == null || password.isBlank())) {
            throw new IllegalArgumentException("Password is required for new users.");
        }
        return new User(id, firstName, surname, email, emailToken, password, rol);
    }

    private String extractUserPassword(Object[] row) {
        // Dialogs for USERS append password at index 5.
        if (row == null || row.length <= 5) return null;
        Object v = row[5];
        return v == null ? null : v.toString();
    }

    private String extractUserEmailToken(Object[] row) {
        // Dialogs for USERS append email token at index 6.
        if (row == null || row.length <= 6) return null;
        Object v = row[6];
        return v == null ? null : v.toString();
    }

    private Contractor convertToContractor(Object[] row) {
        int id = ((Number) row[0]).intValue();
        String name = row[1].toString();
        String address = row[2] != null ? row[2].toString() : null;
        return new Contractor(id, name, address);
    }

    private Crew convertToCrew(Object[] row) {
        int id = ((Number) row[0]).intValue();
        String name = row[1].toString();
        String jobType = row[2] != null ? row[2].toString() : null;
        int idContractor = ((Number) row[3]).intValue();
        int idProject = ((Number) row[4]).intValue();
        return new Crew(id, name, jobType, idContractor, idProject);
    }

    private Log convertToLog(Object[] row) {
        int id = ((Number) row[0]).intValue();
        LocalDate date = parseDate(row[1]);
        String description = row[2] != null ? row[2].toString() : null;
        int idUser = ((Number) row[3]).intValue();
        return new Log(id, idUser, date, description);
    }

    private LocalDate parseDate(Object dateObj) {
        if (dateObj == null) {
            return null;
        }
        String dateStr = dateObj.toString().trim();
        if (dateStr.isEmpty() || "null".equalsIgnoreCase(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

}