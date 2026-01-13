package ui.views.projectmanagment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * Project Management: shared Swing styling helpers (buttons, table, context label).
 */
public final class ProjectManagementUi {

    private ProjectManagementUi() {
    }

    public static final Color BTN_GREEN = new Color(16, 185, 129); // approx "APPROVE"
    public static final Color BTN_GRAY = new Color(107, 114, 128); // approx "DELETE/CANCEL/BACK"

    public static final Color MAIN_BG = Color.decode("#2f687d");

    // Table palette (grid style)
    public static final Color HEADER_BG = Color.decode("#fc0303");
    public static final Color HEADER_FG = Color.WHITE;
    public static final Color ROW_ALT_BG = Color.decode("#F3F8FD");
    public static final Color ROW_BG = Color.WHITE;
    public static final Color ROW_FG = Color.decode("#0F2A44");
    public static final Color ROW_SELECTED_BG = Color.decode("#D9E8F5"); // light blue selection
    public static final Color ROW_SELECTED_FG = Color.BLACK;
    public static final Color GRID_COLOR = Color.decode("#AFC3D6");

    public static final Color CONTEXT_BG = new Color(245, 248, 250);
    public static final Color CONTEXT_BORDER = new Color(200, 200, 200);
    public static final Color SCROLL_VIEWPORT_BG = ROW_BG;
    public static final Color TABLE_OUTLINE = Color.decode("#0F2A44");

    public static final Dimension VIEW_SELECTOR_SIZE = new Dimension(220, 32);

    public static JButton pillButton(String text, Color baseColor) {
        JButton b = new JButton(text == null ? "" : text.toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel btnModel = getModel();
                Color bg = baseColor;
                if (!isEnabled()) {
                    bg = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 140);
                } else if (btnModel.isPressed()) {
                    bg = baseColor.darker();
                } else if (btnModel.isRollover()) {
                    bg = brighten(baseColor, 0.08f);
                }

                int arc = getHeight();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(10, 26, 10, 26));
        b.setRolloverEnabled(true);
        return b;
    }

    public static void styleViewSelector(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(240, 240, 240));
        combo.setForeground(Color.BLACK);
        combo.setPreferredSize(VIEW_SELECTOR_SIZE);
    }

    public static JLabel createContextLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Color.BLACK);
        label.setOpaque(true);
        label.setBackground(CONTEXT_BG);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CONTEXT_BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        label.setVisible(false);
        return label;
    }

    public static void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(GRID_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setBackground(ROW_BG);
        table.setForeground(ROW_FG);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setSelectionBackground(ROW_SELECTED_BG);
        table.setSelectionForeground(ROW_SELECTED_FG);

        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_FG);
        header.setReorderingAllowed(false);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer renderer = createStripedRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        // Some tables/reporting models use concrete classes; register common ones too.
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(Integer.class, renderer);
        table.setDefaultRenderer(Long.class, renderer);
        table.setDefaultRenderer(Double.class, renderer);
        table.setDefaultRenderer(Float.class, renderer);
        table.setDefaultRenderer(Number.class, renderer);

        applyColumnRenderers(table, renderer);
    }

    /**
     * JTable columns can be recreated when the model changes columns; this re-applies our renderer per column.
     */
    public static void applyColumnRenderers(JTable table, DefaultTableCellRenderer renderer) {
        TableColumnModel columns = table.getColumnModel();
        for (int i = 0; i < columns.getColumnCount(); i++) {
            columns.getColumn(i).setCellRenderer(renderer);
        }
    }

    /**
     * Matches the existing selection/stripe logic used in ProjectManagementPanelView (keep behavior identical).
     */
    public static DefaultTableCellRenderer createStripedRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // Do NOT rely on the isSelected flag here: JTable considers a cell selected only if BOTH
                // the row and the column are selected. We want full-row highlight, so we check row selection directly.
                boolean rowSelected = table.getSelectionModel().isSelectedIndex(row);
                boolean highlight = rowSelected || hasFocus;
                if (highlight) {
                    c.setBackground(ROW_SELECTED_BG);
                    c.setForeground(ROW_SELECTED_FG);
                } else if (row % 2 == 0) {
                    c.setBackground(ROW_ALT_BG);
                    c.setForeground(ROW_FG);
                } else {
                    c.setBackground(ROW_BG);
                    c.setForeground(ROW_FG);
                }
                if (c instanceof JComponent jComponent) {
                    jComponent.setOpaque(true);
                    // Remove the default "focused cell" border so it reads as a row selection.
                    jComponent.setBorder(noFocusBorder);
                }
                return c;
            }
        };
    }

    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TABLE_OUTLINE, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getViewport().setBackground(SCROLL_VIEWPORT_BG);
        return scrollPane;
    }

    public static void restoreAllColumnsWidth(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            var column = columnModel.getColumn(i);
            column.setMinWidth(15);
            column.setMaxWidth(Integer.MAX_VALUE);
            column.setPreferredWidth(100);
            column.setResizable(true);
        }
        // Columns may have been recreated; ensure the row selection/striping renderer is applied.
        applyColumnRenderers(table, createStripedRenderer());
    }

    private static Color brighten(Color c, float amount) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsb[2] = Math.min(1f, hsb[2] + amount);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
}


