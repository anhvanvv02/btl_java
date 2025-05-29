package com.cinema.ui;

import com.cinema.controller.EmployeeController;
import com.cinema.model.Employee;
import com.cinema.ui.interfaces.IReload;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EmployeePanel extends JPanel implements IReload{
    private EmployeeController employeeController;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

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

    public EmployeePanel() {
        employeeController = new EmployeeController();
        initComponents();
        loadEmployees();
    }

    @Override
    public void reloadData() {
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
        JLabel titleLabel = new JLabel("Quản Lý Nhân Viên");
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JButton searchButton = new JButton("Tìm kiếm");
        styleButton(searchButton, primaryColor);
        searchButton.addActionListener(e -> searchEmployees());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        // Create table model
        String[] columns = { "ID", "Họ Tên", "Vị Trí", "Email", "Số Điện Thoại", "Địa Chỉ" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and style table
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(normalFont);
        employeeTable.setRowHeight(35);
        employeeTable.setShowGrid(true);
        employeeTable.setGridColor(new Color(189, 195, 199));
        employeeTable.setSelectionBackground(new Color(52, 152, 219, 50));
        employeeTable.setSelectionForeground(textColor);
        employeeTable.setBackground(Color.WHITE);

        // Style header
        JTableHeader header = employeeTable.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(primaryColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(41, 128, 185)));

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
        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Set column widths
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Họ Tên
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Vị Trí
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Số Điện Thoại
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Địa Chỉ

        // Style table cells
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Create scroll pane with custom border
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        // Wrap in panel with padding
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(bgColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        addButton = new JButton("Thêm Nhân Viên");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");

        styleButton(addButton, successColor);
        styleButton(editButton, primaryColor);
        styleButton(deleteButton, dangerColor);

        addButton.addActionListener(e -> showEmployeeDialog(null));
        editButton.addActionListener(e -> editSelectedEmployee());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());

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

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeController.getAllEmployees();
        for (Employee emp : employees) {
            Object[] row = {
                    emp.getId(),
                    emp.getName(),
                    emp.getPosition(),
                    emp.getEmail(),
                    emp.getPhone(),
                    emp.getAddress()
            };
            tableModel.addRow(row);
        }
    }

    private void searchEmployees() {
        String keyword = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        List<Employee> employees = employeeController.getAllEmployees();
        for (Employee emp : employees) {
            if (emp.getName().toLowerCase().contains(keyword) ||
                    emp.getPosition().toLowerCase().contains(keyword) ||
                    emp.getEmail().toLowerCase().contains(keyword)) {

                Object[] row = {
                        emp.getId(),
                        emp.getName(),
                        emp.getPosition(),
                        emp.getEmail(),
                        emp.getPhone(),
                        emp.getAddress()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showEmployeeDialog(Employee employee) {
        JDialog dialog = new JDialog();
        dialog.setTitle(employee == null ? "Thêm Nhân Viên Mới" : "Cập Nhật Thông Tin Nhân Viên");
        dialog.setModal(true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel(employee == null ? "Thêm Nhân Viên Mới" : "Cập Nhật Thông Tin Nhân Viên");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // Add form fields
        JTextField nameField = createFormField("Họ tên:", employee != null ? employee.getName() : "");
        JComboBox<String> positionField = createPositionComboBox("Vị trí:",
                employee != null ? employee.getPosition() : "");
        JTextField emailField = createFormField("Email:", employee != null ? employee.getEmail() : "");
        JTextField phoneField = createFormField("Số điện thoại:", employee != null ? employee.getPhone() : "");
        JTextField addressField = createFormField("Địa chỉ:", employee != null ? employee.getAddress() : "");
        JTextField usernameField = createFormField("Tên đăng nhập:", employee != null ? employee.getUsername() : "");
        JPasswordField passwordField = createPasswordField("Mật khẩu:");

        // Add fields to form panel
        formPanel.add(createFieldPanel(nameField, "Họ tên:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(positionField, "Vị trí:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(emailField, "Email:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(phoneField, "Số điện thoại:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(addressField, "Địa chỉ:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(usernameField, "Tên đăng nhập:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(passwordField, "Mật khẩu:"));

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton, successColor);
        styleButton(cancelButton, dangerColor);

        saveButton.addActionListener(e -> {
            if (validateForm(nameField, emailField, phoneField, addressField, usernameField,
                    employee == null ? passwordField : null)) {
                Employee emp = employee != null ? employee : new Employee();
                emp.setName(nameField.getText().trim());
                emp.setPosition(positionField.getSelectedItem().toString());
                emp.setEmail(emailField.getText().trim());
                emp.setPhone(phoneField.getText().trim());
                emp.setAddress(addressField.getText().trim());
                emp.setUsername(usernameField.getText().trim());

                String password = new String(passwordField.getPassword()).trim();
                if (employee == null || !password.isEmpty()) {
                    emp.setPassword(password);
                }

                boolean success;
                if (employee == null) {
                    success = employeeController.addEmployee(emp);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Thêm nhân viên thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    success = employeeController.updateEmployee(emp);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Cập nhật thông tin thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                if (success) {
                    loadEmployees();
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
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        // Add scrolling if needed
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private JPanel createFieldPanel(JComponent field, String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(450, 60));

        JLabel label = new JLabel(labelText);
        label.setFont(normalFont);
        label.setForeground(textColor);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JComboBox<String> createPositionComboBox(String label, String selectedValue) {
        JComboBox<String> comboBox = new JComboBox<>(new String[] { "Admin", "Nhân viên" });
        comboBox.setFont(normalFont);
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 35));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        comboBox.setBackground(Color.WHITE);

        if (selectedValue != null && !selectedValue.isEmpty()) {
            comboBox.setSelectedItem(selectedValue);
        }

        return comboBox;
    }

    private boolean validateForm(JTextField nameField, JTextField emailField,
            JTextField phoneField, JTextField addressField,
            JTextField usernameField, JPasswordField passwordField) {

        if (nameField.getText().trim().isEmpty()) {
            showValidationError("Vui lòng nhập họ tên!");
            nameField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showValidationError("Vui lòng nhập email!");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(emailField.getText().trim())) {
            showValidationError("Email không hợp lệ!");
            emailField.requestFocus();
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Vui lòng nhập số điện thoại!");
            phoneField.requestFocus();
            return false;
        }

        if (!isValidPhone(phoneField.getText().trim())) {
            showValidationError("Số điện thoại không hợp lệ!");
            phoneField.requestFocus();
            return false;
        }

        if (addressField.getText().trim().isEmpty()) {
            showValidationError("Vui lòng nhập địa chỉ!");
            addressField.requestFocus();
            return false;
        }

        if (usernameField.getText().trim().isEmpty()) {
            showValidationError("Vui lòng nhập tên đăng nhập!");
            usernameField.requestFocus();
            return false;
        }

        if (passwordField != null && new String(passwordField.getPassword()).trim().isEmpty()) {
            showValidationError("Vui lòng nhập mật khẩu!");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^[0-9]{10}$";
        return phone.matches(phoneRegex);
    }

    private void editSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) employeeTable.getValueAt(selectedRow, 0);
            Employee employee = employeeController.getEmployeeById(id);
            if (employee != null) {
                showEmployeeDialog(employee);
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn nhân viên cần sửa",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) employeeTable.getValueAt(selectedRow, 0);
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xóa nhân viên này?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                if (employeeController.deleteEmployee(id)) {
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Không thể xóa nhân viên này",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn nhân viên cần xóa",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private JTextField createFormField(String label, String value) {
        JTextField field = new JTextField(value);
        field.setFont(normalFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private JPasswordField createPasswordField(String label) {
        JPasswordField field = new JPasswordField();
        field.setFont(normalFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }
}