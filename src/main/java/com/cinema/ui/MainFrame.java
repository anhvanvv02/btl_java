package com.cinema.ui;

import com.cinema.model.Employee;
import com.cinema.ui.interfaces.IReload;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private Employee currentEmployee;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel sidePanel;

    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(230, 126, 34);
    private Color bgColor = new Color(236, 240, 241);
    private Color textColor = new Color(44, 62, 80);
    private Color menuBgColor = new Color(52, 73, 94);
    private Color menuHoverColor = new Color(44, 62, 80);

    // Fonts
    private Font menuFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 16);
    private Map<String, IReload> reloadablePanels = new HashMap<>();
    
    public MainFrame(Employee employee) {
        this.currentEmployee = employee;
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Rạp Chiếu Phim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(bgColor);

        // Initialize main content panel with CardLayout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(bgColor);

        // Create content panels
        JPanel welcomePanel = createWelcomePanel();

        JPanel moviePanel = new MoviePanel();
        JPanel employeePanel = new EmployeePanel();
        JPanel theaterPanel = new TheaterPanel();
        JPanel ticketPanel = new TicketPanel(currentEmployee);
        // JPanel reportPanel = new ReportPanel();
        JPanel showtimePanel = new ShowtimePanel();
        JPanel ticketManagementPanel = new TicketManagementPanel();

        // Add panels to CardLayout
        mainPanel.add(welcomePanel, "HOME");
        mainPanel.add(moviePanel, "MOVIES");
        mainPanel.add(theaterPanel, "THEATERS");
        mainPanel.add(ticketPanel, "TICKETS");
        // mainPanel.add(reportPanel, "REPORTS");
        mainPanel.add(showtimePanel, "SHOWTIMES");
        mainPanel.add(ticketManagementPanel, "TICKET_MANAGEMENT");
        
        if ("Admin".equals(currentEmployee.getPosition())) {
            mainPanel.add(employeePanel, "EMPLOYEES");
        }

        reloadablePanels.put("MOVIES", (IReload) moviePanel);
        reloadablePanels.put("EMPLOYEES", (IReload) employeePanel);
        reloadablePanels.put("THEATERS", (IReload) theaterPanel);
        reloadablePanels.put("TICKETS", (IReload) ticketPanel);
        // reloadablePanels.put("REPORTS", (IReload) reportPanel);
        reloadablePanels.put("SHOWTIMES", (IReload) showtimePanel);
        reloadablePanels.put("TICKET_MANAGEMENT", (IReload) ticketManagementPanel);
        
        // Create side panel with menu
        createSidePanel();

        // Add panels to frame
        add(sidePanel, BorderLayout.WEST);

        // Add main panel with padding
        JPanel contentWrapper = new JPanel(new BorderLayout(10, 10));
        contentWrapper.setBackground(bgColor);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentWrapper.add(mainPanel, BorderLayout.CENTER);
        add(contentWrapper, BorderLayout.CENTER);

        // Show initial panel
        cardLayout.first(mainPanel);
    }

    private void customizeComponents() {
        // Set frame appearance
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(0, 0));

        // Create and customize side panel
        createSidePanel();
    }

    private void createSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(250, getHeight()));
        sidePanel.setBackground(menuBgColor);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Add user info panel
        addUserInfoPanel();

        // Add separator
        addSeparator();

        // Add menu items
        if ("Admin".equals(currentEmployee.getPosition())) {
            addMenuItem("Quản Lý Nhân Viên", "person.png", "EMPLOYEES");
        }
        addMenuItem("Quản Lý Phim", "frame.png", "MOVIES");
        addMenuItem("Quản Lý Rạp", "frame.png", "THEATERS");
        addMenuItem("Quản Lý Xuất Chiếu", "calendar.png", "SHOWTIMES");
        addMenuItem("Bán Vé", "ticket.png", "TICKETS");
        // addMenuItem("Danh Sách Vé", "ticket.png", "TICKETS");
        // addMenuItem("Báo Cáo Doanh Thu", "data-analysis.png", "REPORTS");
        // addMenuItem("Báo Cáo Phim", "data-analysis.png", "REPORTS");
        addMenuItem("Quản Lý Vé Đã Bán", "ticket.png", "TICKET_MANAGEMENT");

        // Add separator before system menu
        addSeparator();

        // Add system menu items
        addMenuItem("Đăng Xuất", "ata-analysis.png", e -> logout());

        addMenuItem("Thoát", "ata-analysis.pn", e -> System.exit(0));

        // Add side panel to frame
        add(sidePanel, BorderLayout.WEST);
    }

    private void addUserInfoPanel() {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(menuBgColor);
        userPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // User avatar
        JLabel avatarLabel = new JLabel("👤", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User name
        JLabel nameLabel = new JLabel(currentEmployee.getName());
        nameLabel.setFont(subtitleFont);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User role
        JLabel roleLabel = new JLabel(currentEmployee.getPosition());
        roleLabel.setFont(menuFont);
        roleLabel.setForeground(new Color(189, 195, 199));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userPanel.add(avatarLabel);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(nameLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(roleLabel);

        sidePanel.add(userPanel);
    }

    private void addSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(149, 165, 166));
        separator.setMaximumSize(new Dimension(250, 1));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(separator);
        sidePanel.add(Box.createVerticalStrut(10));
    }

    private void addMenuItem(String text, String icon, String cardName) {
        JPanel menuItem = createMenuItem(text, icon);
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    IReload reloadablePanel = reloadablePanels.get(cardName);
                    if (reloadablePanel != null) {
                        reloadablePanel.reloadData(); // Cập nhật lại dữ liệu panel con
                    }
                    cardLayout.show(mainPanel, cardName); // Hiển thị panel con

                    highlightSelectedMenu(menuItem);
                    System.out.println("Successfully switched to panel: " + cardName);
                } catch (Exception ex) {
                    System.err.println("Error switching to panel: " + cardName);
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!menuItem.getBackground().equals(primaryColor)) {
                    menuItem.setBackground(menuHoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!menuItem.getBackground().equals(primaryColor)) {
                    menuItem.setBackground(menuBgColor);
                }
            }
        });
        sidePanel.add(menuItem);
    }

    private void addMenuItem(String text, String icon, ActionListener action) {
        JPanel menuItem = createMenuItem(text, icon);
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(menuHoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(menuBgColor);
            }
        });
        sidePanel.add(menuItem);
    }

    private JPanel createMenuItem(String text, String iconPath) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setBackground(menuBgColor);
        menuItem.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Load icon
        // JLabel iconLabel;
        // String iconPathC = "/icons/" + iconPath;
        // ImageIcon icon = new ImageIcon(getClass().getResource(iconPathC));
        // // Resize icon nếu cần
        // Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        // iconLabel = new JLabel(new ImageIcon(img));

        JLabel textLabel = new JLabel("  " + text);
        textLabel.setFont(menuFont);
        textLabel.setForeground(Color.WHITE);

        // menuItem.add(iconLabel, BorderLayout.WEST);
        menuItem.add(textLabel, BorderLayout.CENTER);
        menuItem.setMaximumSize(new Dimension(250, 45));

        return menuItem;
    }

    private void highlightSelectedMenu(JPanel selectedMenu) {
        // Reset all menu items background
        for (Component comp : sidePanel.getComponents()) {
            if (comp instanceof JPanel && !(comp instanceof JSeparator)) {
                comp.setBackground(menuBgColor);
            }
        }
        // Highlight selected menu
        selectedMenu.setBackground(primaryColor);
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(bgColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Chào mừng đến với Hệ thống Quản lý Rạp chiếu phim!");
        welcomeLabel.setFont(titleFont);
        welcomeLabel.setForeground(primaryColor);
        welcomePanel.add(welcomeLabel, gbc);

        // Current date and time
        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("EEEE, dd/MM/yyyy").format(new java.util.Date()));
        dateLabel.setFont(subtitleFont);
        dateLabel.setForeground(textColor);
        welcomePanel.add(dateLabel, gbc);

        return welcomePanel;
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}

