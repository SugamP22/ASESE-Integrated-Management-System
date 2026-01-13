package ui.views.smtp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * SMTP/IMAP tab: shared Swing styling helpers (aligned with Project Management tab look & feel).
 */
public final class SmtpUi {

    private SmtpUi() {
    }

    public static final Color BTN_GREEN = new Color(16, 185, 129);
    public static final Color BTN_GRAY = new Color(107, 114, 128);

    public static final Color MAIN_BG = Color.decode("#2f687d");

    // Table palette (matching ProjectManagementUi)
    public static final Color HEADER_BG = Color.decode("#fc0303");
    public static final Color HEADER_FG = Color.WHITE;
    public static final Color ROW_ALT_BG = Color.decode("#F3F8FD");
    public static final Color ROW_BG = Color.WHITE;
    public static final Color ROW_FG = Color.decode("#0F2A44");
    public static final Color ROW_SELECTED_BG = Color.decode("#D9E8F5");
    public static final Color ROW_SELECTED_FG = Color.BLACK;
    public static final Color GRID_COLOR = Color.decode("#AFC3D6");
    public static final Color TABLE_OUTLINE = Color.decode("#0F2A44");

    // Card containers (for header/meta areas)
    public static final Color CARD_BG = new Color(255, 255, 255, 235);
    public static final Color CARD_BORDER = new Color(15, 42, 68, 90);

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

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text == null ? "" : text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        return label;
    }

    public static JPanel cardPanel(Color bg, Color border, int arc) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

                if (border != null) {
                    g2.setColor(border);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        return p;
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

        DefaultTableCellRenderer striped = createStripedRenderer();
        TableCellRenderer booleanRenderer = createBooleanRenderer();

        table.setDefaultRenderer(Object.class, striped);
        table.setDefaultRenderer(String.class, striped);
        table.setDefaultRenderer(Integer.class, striped);
        table.setDefaultRenderer(Long.class, striped);
        table.setDefaultRenderer(Boolean.class, booleanRenderer);

        applyColumnRenderers(table, striped, booleanRenderer);
    }

    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TABLE_OUTLINE, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getViewport().setBackground(ROW_BG);
        return scrollPane;
    }

    private static void applyColumnRenderers(JTable table, DefaultTableCellRenderer striped, TableCellRenderer booleanRenderer) {
        TableColumnModel columns = table.getColumnModel();
        for (int i = 0; i < columns.getColumnCount(); i++) {
            Class<?> columnClass = table.getColumnClass(i);
            if (columnClass == Boolean.class || columnClass == boolean.class) {
                columns.getColumn(i).setCellRenderer(booleanRenderer);
            } else {
                columns.getColumn(i).setCellRenderer(striped);
            }
        }
    }

    private static TableCellRenderer createBooleanRenderer() {
        return new TableCellRenderer() {
            private final JCheckBox checkBox = new JCheckBox();

            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                boolean rowSelected = table.getSelectionModel().isSelectedIndex(row);
                boolean highlight = rowSelected || hasFocus;

                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                checkBox.setOpaque(true);
                checkBox.setBorderPainted(false);
                checkBox.setFocusPainted(false);

                boolean checked = false;
                if (value instanceof Boolean b) {
                    checked = b;
                }
                checkBox.setSelected(checked);

                if (highlight) {
                    checkBox.setBackground(ROW_SELECTED_BG);
                } else if (row % 2 == 0) {
                    checkBox.setBackground(ROW_ALT_BG);
                } else {
                    checkBox.setBackground(ROW_BG);
                }

                return checkBox;
            }
        };
    }

    private static DefaultTableCellRenderer createStripedRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
                    jComponent.setBorder(noFocusBorder);
                }
                return c;
            }
        };
    }

    private static Color brighten(Color c, float amount) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsb[2] = Math.min(1f, hsb[2] + amount);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
}


