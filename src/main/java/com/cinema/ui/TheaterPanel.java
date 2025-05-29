package com.cinema.ui;

import com.cinema.controller.TheaterController;
import com.cinema.model.Theater;
import com.cinema.ui.interfaces.IReload;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TheaterPanel extends JPanel implements IReload{
    private TheaterController theaterController;
    private JTable theaterTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    // Screen types
    private final String[] SCREEN_TYPES = {
        "2D", "3D", "4DX", "IMAX"
    };
    
    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color bgColor = new Color(236, 240, 241);
    private Color textColor = new Color(44, 62, 80);
    private Color successColor = new Color(46, 204, 113);
    private Color dangerColor = new Color(231, 76, 60);
    
    // Fonts
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    public TheaterPanel() {
        theaterController = new TheaterController();
        initComponents();
        loadTheaters();
    }

    @Override
    public void reloadData() {
        initComponents();
        loadTheaters();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(bgColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = new JLabel("Quản Lý Rạp Chiếu");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(bgColor);

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(normalFont);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("Tìm kiếm");
        styleButton(searchButton, primaryColor);
        searchButton.addActionListener(e -> searchTheaters());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        String[] columns = {"ID", "Tên Rạp", "Sức Chứa", "Loại Màn Hình", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class;
                if (columnIndex == 4) return Boolean.class;
                return String.class;
            }
        };

        theaterTable = new JTable(tableModel);
        theaterTable.setFont(normalFont);
        theaterTable.setRowHeight(35);
        theaterTable.setShowGrid(true);
        theaterTable.setGridColor(new Color(189, 195, 199));
        theaterTable.setSelectionBackground(new Color(52, 152, 219, 50));
        theaterTable.setSelectionForeground(textColor);
        theaterTable.setBackground(Color.WHITE);

        // Style header
        JTableHeader header = theaterTable.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(primaryColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Custom header renderer
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                c.setBackground(primaryColor);
                c.setForeground(Color.WHITE);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        };

        // Apply header renderer to all columns
        for (int i = 0; i < theaterTable.getColumnCount(); i++) {
            theaterTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Set column widths
        theaterTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        theaterTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên Rạp
        theaterTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Sức Chứa
        theaterTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Loại Màn Hình
        theaterTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Trạng Thái

        // Style table cells
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        for (int i = 0; i < theaterTable.getColumnCount(); i++) {
            if (i != 4) { // Skip boolean column
                theaterTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }

        JScrollPane scrollPane = new JScrollPane(theaterTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(bgColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        addButton = new JButton("Thêm Rạp");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");

        styleButton(addButton, successColor);
        styleButton(editButton, primaryColor);
        styleButton(deleteButton, dangerColor);

        addButton.addActionListener(e -> showTheaterDialog(null));
        editButton.addActionListener(e -> editSelectedTheater());
        deleteButton.addActionListener(e -> deleteSelectedTheater());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(normalFont);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void showTheaterDialog(Theater theater) {
        JDialog dialog = new JDialog();
        dialog.setTitle(theater == null ? "Thêm Rạp Mới" : "Cập Nhật Thông Tin Rạp");
        dialog.setModal(true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel(theater == null ? "Thêm Rạp Mới" : "Cập Nhật Thông Tin Rạp");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Form fields
        JTextField nameField = createFormField("Tên rạp:", theater != null ? theater.getName() : "");
        
        SpinnerNumberModel capacityModel = new SpinnerNumberModel(
            theater != null ? theater.getCapacity() : 100, 50, 500, 10);
        JSpinner capacitySpinner = new JSpinner(capacityModel);
        capacitySpinner.setFont(normalFont);
        
        JComboBox<String> screenTypeBox = new JComboBox<>(SCREEN_TYPES);
        screenTypeBox.setFont(normalFont);
        if (theater != null) {
            screenTypeBox.setSelectedItem(theater.getScreenType());
        }
        
        JCheckBox statusCheckBox = new JCheckBox("Đang hoạt động");
        statusCheckBox.setFont(normalFont);
        statusCheckBox.setBackground(Color.WHITE);
        statusCheckBox.setSelected(theater == null || theater.isStatus());

        // Add components to form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createFieldPanel(nameField, "Tên rạp:"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createFieldPanel(capacitySpinner, "Sức chứa:"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createFieldPanel(screenTypeBox, "Loại màn hình:"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createFieldPanel(statusCheckBox, "Trạng thái:"));

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton, successColor);
        styleButton(cancelButton, dangerColor);

        saveButton.addActionListener(e -> {
            if (validateTheaterForm(nameField)) {
                Theater newTheater = theater != null ? theater : new Theater();
                newTheater.setName(nameField.getText().trim());
                newTheater.setCapacity((Integer) capacitySpinner.getValue());
                newTheater.setScreenType(screenTypeBox.getSelectedItem().toString());
                newTheater.setStatus(statusCheckBox.isSelected());

                boolean success;
                if (theater == null) {
                    success = theaterController.addTheater(newTheater);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                            "Thêm rạp thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    success = theaterController.updateTheater(newTheater);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                            "Cập nhật thông tin thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                if (success) {
                    loadTheaters();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Có lỗi xảy ra. Vui lòng thử lại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private JPanel createFieldPanel(JComponent field, String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 2));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(440, 55));

        JLabel label = new JLabel(labelText);
        label.setFont(normalFont);
        label.setForeground(textColor);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        return panel;
    }

    private JTextField createFormField(String label, String value) {
        JTextField field = new JTextField(value);
        field.setFont(normalFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return field;
    }

    private boolean validateTheaterForm(JTextField nameField) {
        if (nameField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên rạp!");
            nameField.requestFocus();
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE);
    }

    private void loadTheaters() {
        tableModel.setRowCount(0);
        List<Theater> theaters = theaterController.getAllTheaters();
        for (Theater theater : theaters) {
            Object[] row = {
                theater.getId(),
                theater.getName(),
                theater.getCapacity(),
                theater.getScreenType(),
                theater.isStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchTheaters() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Theater> theaters = theaterController.searchTheaters(keyword);
        for (Theater theater : theaters) {
            Object[] row = {
                theater.getId(),
                theater.getName(),
                theater.getCapacity(),
                theater.getScreenType(),
                theater.isStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void editSelectedTheater() {
        int selectedRow = theaterTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) theaterTable.getValueAt(selectedRow, 0);
            Theater theater = theaterController.getTheaterById(id);
            if (theater != null) {
                showTheaterDialog(theater);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn rạp cần sửa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedTheater() {
        int selectedRow = theaterTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) theaterTable.getValueAt(selectedRow, 0);
            int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa rạp này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                if (theaterController.deleteTheater(id)) {
                    loadTheaters();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa rạp này",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn rạp cần xóa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }
} 