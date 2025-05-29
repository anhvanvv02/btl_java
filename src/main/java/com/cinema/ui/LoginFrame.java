package com.cinema.ui;

import com.cinema.controller.EmployeeController;
import com.cinema.model.Employee;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private EmployeeController employeeController;
    private Color primaryColor = new Color(41, 128, 185);
    private Color lightColor = new Color(236, 240, 241);
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);

    public LoginFrame() {
        employeeController = new EmployeeController();
        initComponents();
        customizeComponents();
    }

    private void customizeComponents() {
        // Set custom fonts
        txtUsername.setFont(mainFont);
        txtPassword.setFont(mainFont);
        btnLogin.setFont(mainFont);
        btnExit.setFont(mainFont);

        // Customize text fields
        customizeTextField(txtUsername);
        customizeTextField(txtPassword);

        // Customize buttons
        customizeButton(btnLogin, primaryColor);
        customizeButton(btnExit, new Color(231, 76, 60));
    }

    private void customizeTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(250, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(primaryColor, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        textField.setBackground(lightColor);
    }

    private void customizeButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Rạp Chiếu Phim - Đăng Nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Reset gridwidth
        gbc.gridwidth = 1;
        gbc.insets = new Insets(7, 7, 7, 7);

        // Username
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(mainFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(mainFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(txtPassword, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        btnLogin = new JButton("Đăng nhập");
        btnExit = new JButton("Thoát");

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(buttonPanel, gbc);

        // Add action listeners
        btnLogin.addActionListener(e -> login());
        btnExit.addActionListener(e -> System.exit(0));

        // Add main panel to frame
        add(mainPanel);

        // Set frame properties
        pack();
        setLocationRelativeTo(null);
    }

    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            return;
        }

        Employee employee = employeeController.login(username, password);
        if (employee != null) {
            // Login successful
            System.out.println("Đăng nhập thành công với nhân viên: " + employee.getName());
            MainFrame mainFrame = new MainFrame(employee);
            mainFrame.setVisible(true);
            this.dispose();
        } else {
            showError("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Customize JOptionPane
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("OptionPane.messageForeground", new Color(44, 62, 80));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}