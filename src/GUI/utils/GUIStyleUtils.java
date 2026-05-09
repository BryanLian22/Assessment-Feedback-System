package GUI.utils;

import javax.swing.*;
import java.awt.*;

public class GUIStyleUtils {
    
    // Professional color palette
    public static final Color PRIMARY_BLUE = new Color(41, 128, 185);
    public static final Color SECONDARY_BLUE = new Color(52, 152, 219);
    public static final Color PRIMARY_PURPLE = new Color(155, 89, 182);
    public static final Color SECONDARY_PURPLE = new Color(142, 68, 173);
    public static final Color PRIMARY_ORANGE = new Color(230, 126, 34);
    public static final Color SECONDARY_ORANGE = new Color(211, 84, 0);
    public static final Color PRIMARY_RED = new Color(192, 57, 43);
    public static final Color SECONDARY_RED = new Color(231, 76, 60);
    public static final Color SUCCESS_GREEN = new Color(39, 174, 96);
    public static final Color ERROR_RED = new Color(231, 76, 60);
    public static final Color WARNING_ORANGE = new Color(243, 156, 18);
    public static final Color INFO_BLUE = new Color(52, 152, 219);
    public static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    public static final Color PANEL_COLOR = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(52, 73, 94);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    
    public static JButton createPrimaryButton(String text, int width, int height) {
        return createStyledButton(text, PRIMARY_BLUE, SECONDARY_BLUE, width, height);
    }
    
    public static JButton createSuccessButton(String text, int width, int height) {
        return createStyledButton(text, SUCCESS_GREEN, new Color(SUCCESS_GREEN.brighter().getRGB()), width, height);
    }
    
    public static JButton createDangerButton(String text, int width, int height) {
        return createStyledButton(text, ERROR_RED, new Color(ERROR_RED.brighter().getRGB()), width, height);
    }
    
    public static JButton createSecondaryButton(String text, int width, int height) {
        return createStyledButton(text, new Color(149, 165, 166), new Color(127, 140, 141), width, height);
    }
    
    public static JButton createStyledButton(String text, Color bgColor, Color hoverColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 100));
                } else if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        
        return button;
    }
    
    public static JTextField createStyledTextField(String placeholder, int width, int height) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(width, height));
        field.setBackground(PANEL_COLOR);
        field.setForeground(new Color(149, 165, 166));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(149, 165, 166));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    public static JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        area.setBackground(PANEL_COLOR);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
    
    public static void styleDialog(JDialog dialog) {
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(dialog);
        } catch (Exception e) {
            // Use default look and feel
        }
    }
    
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return card;
    }
    
    public static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    /**
     * Word-wrapping table cell renderer that auto-wraps text to new lines
     * and adjusts row height dynamically.
     */
    public static class WordWrapCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private javax.swing.JTextArea textArea;
        
        public WordWrapCellRenderer() {
            textArea = new javax.swing.JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setOpaque(true);
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            textArea.setText(value != null ? value.toString() : "");
            textArea.setFont(table.getFont());
            
            if (isSelected) {
                textArea.setBackground(table.getSelectionBackground());
                textArea.setForeground(table.getSelectionForeground());
            } else {
                textArea.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                textArea.setForeground(table.getForeground());
            }
            
            // Calculate required height for text wrapping
            int columnWidth = table.getColumnModel().getColumn(column).getWidth();
            textArea.setSize(columnWidth, Short.MAX_VALUE);
            int preferredHeight = textArea.getPreferredSize().height;
            
            // Update row height if needed (minimum 25, add padding)
            int newHeight = Math.max(25, preferredHeight + 10);
            if (table.getRowHeight(row) < newHeight) {
                table.setRowHeight(row, newHeight);
            }
            
            return textArea;
        }
    }
    
    /**
     * Apply word-wrap renderer to specified columns of a table.
     * @param table The JTable to apply the renderer to
     * @param columnIndices The column indices that should have word-wrap
     */
    public static void applyWordWrapToTable(javax.swing.JTable table, int... columnIndices) {
        WordWrapCellRenderer renderer = new WordWrapCellRenderer();
        for (int col : columnIndices) {
            if (col >= 0 && col < table.getColumnCount()) {
                table.getColumnModel().getColumn(col).setCellRenderer(renderer);
            }
        }
    }
    
    /**
     * Apply word-wrap renderer to all columns of a table.
     * @param table The JTable to apply the renderer to
     */
    public static void applyWordWrapToAllColumns(javax.swing.JTable table) {
        WordWrapCellRenderer renderer = new WordWrapCellRenderer();
        for (int col = 0; col < table.getColumnCount(); col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(renderer);
        }
    }
}


