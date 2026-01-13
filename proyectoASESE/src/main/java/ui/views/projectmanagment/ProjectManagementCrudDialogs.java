package ui.views.projectmanagment;

import db.UserRepository;
import entities.Delivery;
import entities.User;
import ui.data.projectmanagment.ProjectManagementDataProvider;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Project Management: builds Add/Modify dialogs and returns the row values to save.
 */
public final class ProjectManagementCrudDialogs {

    private ProjectManagementCrudDialogs() {
    }

    // Dialog palette aligned with the Project Management UI.
    private static final Color DIALOG_BG = Color.decode("#2f687d");
    private static final Color PANEL_BG = Color.decode("#2f687d");
    private static final Color FIELD_BG = Color.WHITE;
    private static final Color BORDER = new Color(200, 200, 200);
    private static final Color TEXT = Color.WHITE;
    private static final Color LABEL_TEXT = Color.WHITE;

    public static Object[] showAddDialog(
            Component parent,
            ManagementView view,
            ProjectManagementDataProvider dataProvider,
            int newId,
            Integer contextProjectId,
            Integer contextStageId,
            Integer contextContractorId,
            boolean drillDownMode,
            Integer userModeUserId
    ) {
        return showDialog(parent, "Add " + view, view, dataProvider, newId, null, contextProjectId, contextStageId, contextContractorId, drillDownMode, userModeUserId);
    }

    public static Object[] showModifyDialog(
            Component parent,
            ManagementView view,
            ProjectManagementDataProvider dataProvider,
            Object[] existingRow,
            Integer contextProjectId,
            Integer contextStageId,
            Integer contextContractorId,
            boolean drillDownMode,
            Integer userModeUserId
    ) {
        Integer id = existingRow != null && existingRow.length > 0 && existingRow[0] instanceof Number n ? n.intValue() : null;
        return showDialog(parent, "Modify " + view, view, dataProvider, id != null ? id : 0, existingRow, contextProjectId, contextStageId, contextContractorId, drillDownMode, userModeUserId);
    }

    private static Object[] showDialog(
            Component parent,
            String title,
            ManagementView view,
            ProjectManagementDataProvider dataProvider,
            int id,
            Object[] existingRow,
            Integer contextProjectId,
            Integer contextStageId,
            Integer contextContractorId,
            boolean drillDownMode,
            Integer userModeUserId
    ) {
        if (view == ManagementView.LOGS) {
            return null;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(DIALOG_BG);

        root.add(buildHeader(title), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        // Most entities have a numeric ID. Whitelist uses the email as its key, so we don't show an "ID" row there.
        if (view != ManagementView.WHITELIST) {
            JTextField idField = styledTextField(existingRow == null ? "Auto" : String.valueOf(id));
            idField.setEditable(false);
            idField.setFocusable(false);
            idField.setBackground(new Color(235, 235, 235)); // read-only look
            idField.setForeground(Color.DARK_GRAY);
            idField.setCaretColor(Color.DARK_GRAY);
            addRow(form, gbc, idLabelFor(view), idField);
        }

        RowBuilder builder = new RowBuilder(view, dataProvider, existingRow, contextProjectId, contextStageId, contextContractorId, drillDownMode, userModeUserId);
        builder.buildFields(form, gbc);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(PANEL_BG);
        scroll.setOpaque(true);
        scroll.setBackground(PANEL_BG);
        root.add(scroll, BorderLayout.CENTER);

        JButton cancel = ProjectManagementUi.pillButton("Cancel", ProjectManagementUi.BTN_GRAY);
        JButton save = ProjectManagementUi.pillButton(existingRow == null ? "Approve" : "Approve", ProjectManagementUi.BTN_GREEN);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(true);
        buttons.setBackground(DIALOG_BG);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttons.add(cancel);
        buttons.add(save);
        root.add(buttons, BorderLayout.SOUTH);

        final Object[][] resultHolder = new Object[1][];

        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            String validationError = builder.validateRequiredFields();
            if (validationError != null) {
                JOptionPane.showMessageDialog(
                    dialog,
                    validationError,
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return; // Don't close dialog
            }
            Object[] row = builder.buildRowValues(id);
            if (row == null) {
                return;
            }
            resultHolder[0] = row;
            dialog.dispose();
        });

        dialog.setContentPane(root);
        dialog.setSize(572, 572);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return resultHolder[0];
    }

    private static JComponent buildHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setForeground(TEXT);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(t, BorderLayout.CENTER);
        return header;
    }

    // buttons are created via ProjectManagementUi.pillButton(...)

    private static void addRow(JPanel form, GridBagConstraints gbc, String label, Component field) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(LABEL_TEXT);
        form.add(l, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);
        gbc.gridy++;
    }

    private static JTextField styledTextField(String initial) {
        JTextField tf = new JTextField(initial == null ? "" : initial);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(260, 30));
        tf.setBorder(compoundFieldBorder());
        tf.setBackground(FIELD_BG);
        tf.setForeground(Color.BLACK);
        tf.setCaretColor(Color.BLACK);
        return tf;
    }

    private static JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setPreferredSize(new Dimension(260, 30));
        pf.setBorder(compoundFieldBorder());
        pf.setBackground(FIELD_BG);
        pf.setForeground(Color.BLACK);
        pf.setCaretColor(Color.BLACK);
        return pf;
    }

    private static JTextArea styledTextArea(String initial) {
        JTextArea ta = new JTextArea(initial == null ? "" : initial, 4, 24);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(compoundFieldBorder());
        ta.setBackground(FIELD_BG);
        ta.setForeground(Color.BLACK);
        ta.setCaretColor(Color.BLACK);
        return ta;
    }

    private static Border compoundFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );
    }

    private static String idLabelFor(ManagementView view) {
        return switch (view) {
            case PROJECTS -> "Project ID";
            case USERS -> "User ID";
            case WHITELIST -> "Email";
            case STAGES -> "Stage ID";
            case DELIVERIES -> "Delivery ID";
            case PERMISSIONS -> "Permission ID";
            case CONTRACTORS -> "Contractor ID";
            case CREW -> "Crew ID";
            case LOGS -> "Log ID";
        };
    }

    private static final class IdNameItem {
        final int id;
        final String label;

        IdNameItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return id + " - " + label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IdNameItem that)) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static final class RowBuilder {
        private final ManagementView view;
        private final ProjectManagementDataProvider dataProvider;
        private final Object[] existingRow;
        private final Integer contextProjectId;
        private final Integer contextStageId;
        private final Integer contextContractorId;
        private final boolean drillDownMode;
        private final Integer userModeUserId;

        // common fields
        private JTextField name;
        private JTextField email;
        private JTextField emailToken;
        private JTextField surname;
        private JPasswordField password;
        private JTextArea description;
        private JTextField material;
        private JTextField address;
        private JTextField jobType;
        private JSpinner initialDateSpinner;
        private JSpinner finalDateSpinner;
        private JSpinner permissionCount;
        private JSpinner initialDeliveryCount;
        private JSpinner finalDeliveryCount;

        private JComboBox<User.UserRol> userRole;
        private JComboBox<Delivery.DeliveryTiming> deliveryType;
        private JComboBox<IdNameItem> projectDropdown;
        private JComboBox<IdNameItem> stageDropdown;
        private JComboBox<IdNameItem> contractorDropdown;

        RowBuilder(
                ManagementView view,
                ProjectManagementDataProvider dataProvider,
                Object[] existingRow,
                Integer contextProjectId,
                Integer contextStageId,
                Integer contextContractorId,
                boolean drillDownMode,
                Integer userModeUserId
        ) {
            this.view = view;
            this.dataProvider = dataProvider;
            this.existingRow = existingRow;
            this.contextProjectId = contextProjectId;
            this.contextStageId = contextStageId;
            this.contextContractorId = contextContractorId;
            this.drillDownMode = drillDownMode;
            this.userModeUserId = userModeUserId;
        }

        void buildFields(JPanel form, GridBagConstraints gbc) {
            switch (view) {
                case PROJECTS -> {
                    name = styledTextField(getString(1));
                    addRow(form, gbc, "Name *", name);
                }
                case USERS -> {
                    name = styledTextField(getString(1));
                    surname = styledTextField(getString(2));
                    email = styledTextField(getString(3));
                    password = styledPasswordField();
                    if (existingRow != null) {
                        password.setToolTipText("Leave blank to keep current password");
                    } else {
                        password.setToolTipText("Required");
                    }
                    userRole = new JComboBox<>(User.UserRol.values());
                    userRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    userRole.setBorder(compoundFieldBorder());
                    userRole.setBackground(FIELD_BG);
                    userRole.setForeground(Color.BLACK);
                    String existingRole = getString(4);
                    if (!existingRole.isBlank()) {
                        try {
                            userRole.setSelectedItem(User.UserRol.valueOf(existingRole));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    addRow(form, gbc, "First Name *", name);
                    addRow(form, gbc, "Surname *", surname);
                    addRow(form, gbc, "Email *", email);
                    addRow(form, gbc, existingRow == null ? "Password *" : "New Password", password);

                    // Token: not shown in the table, so in modify mode we fetch from DB to prefill.
                    String tokenInitial = "";
                    if (existingRow != null) {
                        int userId = getInt(0);
                        if (userId > 0) {
                            var existingUser = UserRepository.getUserById(userId);
                            if (existingUser.isPresent() && existingUser.get().getEmailToken() != null) {
                                tokenInitial = existingUser.get().getEmailToken();
                            }
                        }
                    }
                    emailToken = styledTextField(tokenInitial);
                    emailToken.setToolTipText("Optional (leave blank to keep current token on modify)");
                    addRow(form, gbc, "Email Token", emailToken);

                    addRow(form, gbc, "Role *", userRole);
                }
                case WHITELIST -> {
                    email = styledTextField(existingRow != null && existingRow.length > 0 ? getString(0) : "");
                    addRow(form, gbc, "Email *", email);
                }
                case STAGES -> {
                    projectDropdown = buildProjectDropdown();
                    name = styledTextField(getString(2));
                    description = styledTextArea(getString(3));
                    // Initial date must be set automatically (default to today).
                    LocalDate initial = getLocalDate(4);
                    initialDateSpinner = createDateSpinner(initial != null ? initial : LocalDate.now());
                    finalDateSpinner = createDateSpinner(getLocalDate(5));
                    permissionCount = new JSpinner(new SpinnerNumberModel(getInt(6), 0, 9999, 1));
                    initialDeliveryCount = new JSpinner(new SpinnerNumberModel(getInt(7), 0, 9999, 1));
                    finalDeliveryCount = new JSpinner(new SpinnerNumberModel(getInt(8), 0, 9999, 1));
                    styleSpinner(permissionCount);
                    styleSpinner(initialDeliveryCount);
                    styleSpinner(finalDeliveryCount);

                    lockFkIfNeeded(projectDropdown, contextProjectId);

                    addRow(form, gbc, "Project *", projectDropdown);
                    addRow(form, gbc, "Name *", name);
                    addRow(form, gbc, "Description", wrap(description));
                    addRow(form, gbc, "Initial Date", initialDateSpinner);
                    addRow(form, gbc, "Final Date", finalDateSpinner);
                    addRow(form, gbc, "Permission Count", permissionCount);
                    addRow(form, gbc, "Initial Delivery Count", initialDeliveryCount);
                    addRow(form, gbc, "Final Delivery Count", finalDeliveryCount);
                }
                case DELIVERIES -> {
                    stageDropdown = buildStageDropdown();
                    material = styledTextField(getString(2));
                    description = styledTextArea(getString(3));
                    deliveryType = new JComboBox<>(Delivery.DeliveryTiming.values());
                    deliveryType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    deliveryType.setBorder(compoundFieldBorder());
                    deliveryType.setBackground(FIELD_BG);
                    deliveryType.setForeground(Color.BLACK);
                    String existingType = getString(4);
                    if (!existingType.isBlank()) {
                        try {
                            deliveryType.setSelectedItem(Delivery.DeliveryTiming.valueOf(existingType));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }

                    lockFkIfNeeded(stageDropdown, contextStageId);

                    addRow(form, gbc, "Stage *", stageDropdown);
                    addRow(form, gbc, "Material *", material);
                    addRow(form, gbc, "Description", wrap(description));
                    addRow(form, gbc, "Type *", deliveryType);
                }
                case PERMISSIONS -> {
                    stageDropdown = buildStageDropdown();
                    name = styledTextField(getString(2));
                    description = styledTextArea(getString(3));

                    lockFkIfNeeded(stageDropdown, contextStageId);

                    addRow(form, gbc, "Stage *", stageDropdown);
                    addRow(form, gbc, "Name *", name);
                    addRow(form, gbc, "Description", wrap(description));
                }
                case CONTRACTORS -> {
                    name = styledTextField(getString(1));
                    address = styledTextField(getString(2));
                    addRow(form, gbc, "Name *", name);
                    addRow(form, gbc, "Address", address);
                }
                case CREW -> {
                    contractorDropdown = buildContractorDropdown();
                    projectDropdown = buildProjectDropdown();
                    name = styledTextField(getString(1));
                    jobType = styledTextField(getString(2));

                    lockFkIfNeeded(contractorDropdown, contextContractorId);
                    lockFkIfNeeded(projectDropdown, contextProjectId);

                    addRow(form, gbc, "Name *", name);
                    addRow(form, gbc, "Job Type", jobType);
                    addRow(form, gbc, "Contractor *", contractorDropdown);
                    addRow(form, gbc, "Project *", projectDropdown);
                }
                case LOGS -> {
                    // no-op
                }
            }
        }

        String validateRequiredFields() {
            List<String> missingFields = new ArrayList<>();
            
            switch (view) {
                case PROJECTS -> {
                    if (safe(name).isBlank()) missingFields.add("Name");
                }
                case USERS -> {
                    if (safe(name).isBlank()) missingFields.add("First Name");
                    if (safe(surname).isBlank()) missingFields.add("Surname");
                    if (safe(email).isBlank()) missingFields.add("Email");
                    if (existingRow == null && safe(password).isBlank()) missingFields.add("Password");
                    if (userRole.getSelectedItem() == null) missingFields.add("Role");
                }
                case WHITELIST -> {
                    if (safe(email).isBlank()) {
                        missingFields.add("Email");
                    } else if (!safe(email).contains("@")) {
                        return "Please enter a valid email.";
                    }
                }
                case STAGES -> {
                    if (projectDropdown.getSelectedItem() == null) missingFields.add("Project");
                    if (safe(name).isBlank()) missingFields.add("Name");
                    // Date validation
                    LocalDate initDate = getSpinnerDate(initialDateSpinner);
                    LocalDate finDate = getSpinnerDate(finalDateSpinner);
                    if (initDate != null && finDate != null && initDate.isAfter(finDate)) {
                        return "Initial date must be before or equal to final date.";
                    }
                }
                case DELIVERIES -> {
                    if (stageDropdown.getSelectedItem() == null) missingFields.add("Stage");
                    if (safe(material).isBlank()) missingFields.add("Material");
                    if (deliveryType.getSelectedItem() == null) missingFields.add("Type");
                }
                case PERMISSIONS -> {
                    if (stageDropdown.getSelectedItem() == null) missingFields.add("Stage");
                    if (safe(name).isBlank()) missingFields.add("Name");
                }
                case CONTRACTORS -> {
                    if (safe(name).isBlank()) missingFields.add("Name");
                }
                case CREW -> {
                    if (safe(name).isBlank()) missingFields.add("Name");
                    if (contractorDropdown.getSelectedItem() == null) missingFields.add("Contractor");
                    if (projectDropdown.getSelectedItem() == null) missingFields.add("Project");
                }
                case LOGS -> {
                    // Logs are read-only, no validation needed
                }
            }
            
            if (missingFields.isEmpty()) {
                return null; // All required fields are filled
            }
            
            return "Please fill all required fields:\n" + String.join("\n- ", missingFields);
        }

        Object[] buildRowValues(int id) {
            switch (view) {
                case PROJECTS -> {
                    String n = safeOrNull(name);
                    return new Object[]{id, n};
                }
                case USERS -> {
                    String first = safeOrNull(name);
                    String sur = safeOrNull(surname);
                    String em = safeOrNull(email);
                    String pass = safeOrNull(password);
                    String token = safeOrNull(emailToken);
                    User.UserRol role = (User.UserRol) userRole.getSelectedItem();
                    return new Object[]{id, first, sur, em, role.name(), pass, token};
                }
                case WHITELIST -> {
                    String em = safeOrNull(email);
                    return new Object[]{em};
                }
                case STAGES -> {
                    IdNameItem p = (IdNameItem) projectDropdown.getSelectedItem();
                    String n = safeOrNull(name);
                    LocalDate initDate = getSpinnerDate(initialDateSpinner);
                    LocalDate finDate = getSpinnerDate(finalDateSpinner);
                    return new Object[]{
                            id,
                            p.id,
                            n,
                            safeOrNull(description),
                            initDate != null ? initDate.toString() : null,
                            finDate != null ? finDate.toString() : null,
                            ((Number) permissionCount.getValue()).intValue(),
                            ((Number) initialDeliveryCount.getValue()).intValue(),
                            ((Number) finalDeliveryCount.getValue()).intValue()
                    };
                }
                case DELIVERIES -> {
                    IdNameItem s = (IdNameItem) stageDropdown.getSelectedItem();
                    Delivery.DeliveryTiming t = (Delivery.DeliveryTiming) deliveryType.getSelectedItem();
                    String mat = safeOrNull(material);
                    return new Object[]{
                            id,
                            s.id,
                            mat,
                            safeOrNull(description),
                            t.name()
                    };
                }
                case PERMISSIONS -> {
                    IdNameItem s = (IdNameItem) stageDropdown.getSelectedItem();
                    String n = safeOrNull(name);
                    return new Object[]{
                            id,
                            s.id,
                            n,
                            safeOrNull(description)
                    };
                }
                case CONTRACTORS -> {
                    String n = safeOrNull(name);
                    return new Object[]{id, n, safeOrNull(address)};
                }
                case CREW -> {
                    IdNameItem c = (IdNameItem) contractorDropdown.getSelectedItem();
                    IdNameItem p = (IdNameItem) projectDropdown.getSelectedItem();
                    String n = safeOrNull(name);
                    return new Object[]{id, n, safeOrNull(jobType), c.id, p.id};
                }
                case LOGS -> {
                    return null;
                }
            }
            return null;
        }

        private static void styleSpinner(JSpinner spinner) {
            spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            spinner.setBackground(FIELD_BG);
            spinner.setForeground(Color.BLACK);
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor de) {
                de.getTextField().setBorder(compoundFieldBorder());
                de.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
                de.getTextField().setBackground(FIELD_BG);
                de.getTextField().setForeground(Color.BLACK);
                de.getTextField().setCaretColor(Color.BLACK);
            }
        }

        private JComboBox<IdNameItem> buildProjectDropdown() {
            JComboBox<IdNameItem> combo = new JComboBox<>();
            Object[][] projects = userModeUserId != null ? dataProvider.getProjectsForUser(userModeUserId) : dataProvider.getProjects();
            for (Object[] r : projects) {
                if (r.length > 1 && r[0] instanceof Number id && r[1] != null) {
                    combo.addItem(new IdNameItem(id.intValue(), r[1].toString()));
                }
            }
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            combo.setBorder(compoundFieldBorder());
            combo.setBackground(FIELD_BG);
            combo.setForeground(Color.BLACK);

            Integer existingProjectId = existingRow != null && existingRow.length > 1 && existingRow[1] instanceof Number n ? n.intValue() : null;
            if (existingProjectId != null) {
                combo.setSelectedItem(new IdNameItem(existingProjectId, ""));
            }
            if (contextProjectId != null) {
                combo.setSelectedItem(new IdNameItem(contextProjectId, ""));
            }
            return combo;
        }

        private JComboBox<IdNameItem> buildStageDropdown() {
            JComboBox<IdNameItem> combo = new JComboBox<>();
            Object[][] stages = userModeUserId != null ? dataProvider.getStagesForUser(userModeUserId) : dataProvider.getAllStages();
            for (Object[] r : stages) {
                if (r.length > 2 && r[0] instanceof Number id && r[2] != null) {
                    combo.addItem(new IdNameItem(id.intValue(), r[2].toString()));
                }
            }
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            combo.setBorder(compoundFieldBorder());
            combo.setBackground(FIELD_BG);
            combo.setForeground(Color.BLACK);

            Integer existingStageId = existingRow != null && existingRow.length > 1 && existingRow[1] instanceof Number n ? n.intValue() : null;
            if (existingStageId != null) {
                combo.setSelectedItem(new IdNameItem(existingStageId, ""));
            }
            if (contextStageId != null) {
                combo.setSelectedItem(new IdNameItem(contextStageId, ""));
            }
            return combo;
        }

        private JComboBox<IdNameItem> buildContractorDropdown() {
            JComboBox<IdNameItem> combo = new JComboBox<>();
            // Contractors are visible to USERs as a global list (not restricted to assigned projects).
            Object[][] contractors = dataProvider.getContractors();
            for (Object[] r : contractors) {
                if (r.length > 1 && r[0] instanceof Number id && r[1] != null) {
                    combo.addItem(new IdNameItem(id.intValue(), r[1].toString()));
                }
            }
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            combo.setBorder(compoundFieldBorder());
            combo.setBackground(FIELD_BG);
            combo.setForeground(Color.BLACK);

            Integer existingContractorId = existingRow != null && existingRow.length > 3 && existingRow[3] instanceof Number n ? n.intValue() : null;
            if (existingContractorId != null) {
                combo.setSelectedItem(new IdNameItem(existingContractorId, ""));
            }
            if (contextContractorId != null) {
                combo.setSelectedItem(new IdNameItem(contextContractorId, ""));
            }
            return combo;
        }

        private void lockFkIfNeeded(JComboBox<IdNameItem> combo, Integer contextId) {
            if (drillDownMode && contextId != null) {
                combo.setEnabled(false);
            }
        }

        private static String safe(JTextField tf) {
            return tf == null ? "" : tf.getText().trim();
        }

        private static String safe(JPasswordField pf) {
            return pf == null ? "" : new String(pf.getPassword()).trim();
        }

        // Returns null if empty/blank, otherwise returns trimmed string
        private static String safeOrNull(JTextField tf) {
            if (tf == null) return null;
            String trimmed = tf.getText().trim();
            return trimmed.isEmpty() ? null : trimmed;
        }

        private static String safeOrNull(JPasswordField pf) {
            if (pf == null) return null;
            String trimmed = new String(pf.getPassword()).trim();
            return trimmed.isEmpty() ? null : trimmed;
        }

        private static String safeOrNull(JTextArea ta) {
            if (ta == null) return null;
            String trimmed = ta.getText().trim();
            return trimmed.isEmpty() ? null : trimmed;
        }

        private static JSpinner createDateSpinner(LocalDate initialDate) {
            // JSpinner doesn't support null values, so we keep an internal Date value,
            // but we allow the user to leave the editor text blank to represent "no date".
            Date dateValue = initialDate != null
                    ? Date.from(initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    : new Date();

            SpinnerDateModel model = new SpinnerDateModel(dateValue, null, null, java.util.Calendar.DAY_OF_MONTH);
            JSpinner spinner = new JSpinner(model);
            JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
            spinner.setEditor(editor);
            styleDateSpinner(spinner);

            // If no date exists (e.g., Stage final_date is null), show empty instead of auto-filling today.
            if (initialDate == null) {
                editor.getTextField().setText("");
            }

            return spinner;
        }

        private static void styleDateSpinner(JSpinner spinner) {
            spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            spinner.setBackground(FIELD_BG);
            spinner.setForeground(Color.BLACK);
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DateEditor de) {
                JTextField tf = de.getTextField();
                if (tf instanceof JFormattedTextField ftf) {
                    // Allow blank text to remain blank when focus is lost (don't revert to last valid date).
                    ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);
                }
                tf.setBorder(compoundFieldBorder());
                tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tf.setBackground(FIELD_BG);
                tf.setForeground(Color.BLACK);
                tf.setCaretColor(Color.BLACK);
            }
        }

        private static LocalDate getSpinnerDate(JSpinner spinner) {
            if (spinner == null) return null;

            // Treat blank editor text as "no date" (optional field).
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DateEditor de) {
                String txt = de.getTextField().getText();
                if (txt == null || txt.trim().isEmpty()) {
                    return null;
                }
                // Prefer parsing the text the user typed (since with PERSIST it may not commit to the model).
                try {
                    return LocalDate.parse(txt.trim());
                } catch (Exception ignored) {
                    // fall back to spinner value below
                }
            }

            Date date = (Date) spinner.getValue();
            if (date == null) return null;
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        private LocalDate getLocalDate(int idx) {
            if (existingRow == null || idx < 0 || idx >= existingRow.length) return null;
            Object v = existingRow[idx];
            if (v == null) return null;
            if (v instanceof LocalDate ld) return ld;
            if (v instanceof String s && !s.isBlank()) {
                try {
                    return LocalDate.parse(s);
                } catch (Exception ignored) {
                }
            }
            return null;
        }

        private String getString(int idx) {
            if (existingRow == null || idx < 0 || idx >= existingRow.length) return "";
            return existingRow[idx] != null ? existingRow[idx].toString() : "";
        }

        private int getInt(int idx) {
            if (existingRow == null || idx < 0 || idx >= existingRow.length) return 0;
            Object v = existingRow[idx];
            return v instanceof Number n ? n.intValue() : 0;
        }

        private static JScrollPane wrap(JTextArea ta) {
            JScrollPane sp = new JScrollPane(ta);
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            return sp;
        }
    }
}


