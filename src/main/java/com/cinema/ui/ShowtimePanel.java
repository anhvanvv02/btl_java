package com.cinema.ui;

import com.cinema.controller.MovieController;
import com.cinema.controller.ShowtimeController;
import com.cinema.controller.TheaterController;
import com.cinema.model.Movie;
import com.cinema.model.Showtime;
import com.cinema.model.Theater;
import com.cinema.ui.interfaces.IReload;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShowtimePanel extends JPanel implements IReload{
    private ShowtimeController showtimeController;
    private MovieController movieController;
    private TheaterController theaterController;
    private JTable showtimeTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTextField searchField;

    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color bgColor = new Color(245, 246, 250);
    private Color textColor = new Color(44, 62, 80);
    private Color accentColor = new Color(46, 204, 113);
    private Color errorColor = new Color(231, 76, 60);
    private Color tableHeaderBg = new Color(52, 73, 94);
    private Color tableStripeBg = new Color(236, 240, 241);

    // Fonts
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    public ShowtimePanel() {
        showtimeController = new ShowtimeController();
        movieController = new MovieController();
        theaterController = new TheaterController();
        initComponents();
        loadData();
    }

    @Override
    public void reloadData() {
        
    }
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header Panel với Search
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(bgColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Quản Lý Xuất Chiếu");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(bgColor);
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(normalFont);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JButton searchButton = new JButton("Tìm kiếm");
        styleButton(searchButton, primaryColor);
        searchButton.addActionListener(e -> searchShowtimes());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        addButton = createStyledButton("Thêm", accentColor);
        updateButton = createStyledButton("Cập nhật", secondaryColor);
        deleteButton = createStyledButton("Xóa", errorColor);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        // Add Action Listeners
        addButton.addActionListener(e -> showShowtimeDialog(null));
        updateButton.addActionListener(e -> {
            int selectedRow = showtimeTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xuất chiếu cần cập nhật!");
                return;
            }
            int showtimeId = (int) showtimeTable.getValueAt(selectedRow, 0);
            Showtime showtime = showtimeController.getShowtimeById(showtimeId);
            showShowtimeDialog(showtime);
        });
        deleteButton.addActionListener(e -> deleteShowtime());
    }

    private JPanel createTablePanel() {
        String[] columns = { "ID", "Phim", "Rạp", "Thời gian", "Giá vé", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        showtimeTable = new JTable(tableModel);
        showtimeTable.setFont(normalFont);
        showtimeTable.setRowHeight(35);
        showtimeTable.setShowGrid(true);
        showtimeTable.setGridColor(new Color(189, 195, 199));
        showtimeTable.setSelectionBackground(new Color(52, 152, 219, 50));
        showtimeTable.setSelectionForeground(textColor);
        showtimeTable.setBackground(Color.WHITE);

        // Style header
        JTableHeader header = showtimeTable.getTableHeader();
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
        for (int i = 0; i < showtimeTable.getColumnCount(); i++) {
            showtimeTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Set column widths
        showtimeTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        showtimeTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Phim
        showtimeTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Rạp
        showtimeTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Thời gian
        showtimeTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Giá vé
        showtimeTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Trạng thái

        // Style table cells
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        for (int i = 0; i < showtimeTable.getColumnCount(); i++) {
            showtimeTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(showtimeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(bgColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void loadData() {
        // Load Movies
        List<Movie> movies = movieController.getAllMovies();
        for (Movie movie : movies) {
            tableModel.addRow(new Object[]{
                    movie.getId(),
                    movie.getTitle(),
                    "",
                    "",
                    "",
                    ""
            });
        }

        // Load Theaters
        List<Theater> theaters = theaterController.getAllTheaters();
        for (Theater theater : theaters) {
            tableModel.addRow(new Object[]{
                    "",
                    "",
                    theater.getName(),
                    "",
                    "",
                    ""
            });
        }

        // Load Showtimes
        refreshShowtimeTable();
    }

    private void refreshShowtimeTable() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        List<Showtime> showtimes = showtimeController.getAllShowtimes();
        for (Showtime showtime : showtimes) {
            Object[] row = {
                    showtime.getId(),
                    showtime.getMovieTitle(),
                    showtime.getTheaterName(),
                    sdf.format(showtime.getShowDateTime()),
                    String.format("%,.0f VND", showtime.getPrice()),
                    showtime.isStatus() ? "Hoạt động" : "Đã hủy"
            };
            tableModel.addRow(row);
        }
    }

    private void showShowtimeDialog(Showtime showtime) {
        JDialog dialog = new JDialog();
        dialog.setTitle(showtime == null ? "Thêm Xuất Chiếu" : "Cập Nhật Xuất Chiếu");
        dialog.setModal(true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel(showtime == null ? "Thêm Xuất Chiếu" : "Cập Nhật Xuất Chiếu");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(12));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // Movie
        JComboBox<Movie> movieBox = new JComboBox<>();
        for (Movie m : movieController.getAllMovies()) movieBox.addItem(m);
        if (showtime != null) {
            for (int i = 0; i < movieBox.getItemCount(); i++) {
                if (movieBox.getItemAt(i).getId() == showtime.getMovieId()) {
                    movieBox.setSelectedIndex(i); break;
                }
            }
        }
        styleComboBox(movieBox);
        formPanel.add(createFieldPanel(movieBox, "Phim:"));
        formPanel.add(Box.createVerticalStrut(6));

        // Theater
        JComboBox<Theater> theaterBox = new JComboBox<>();
        for (Theater t : theaterController.getAllTheaters()) theaterBox.addItem(t);
        if (showtime != null) {
            for (int i = 0; i < theaterBox.getItemCount(); i++) {
                if (theaterBox.getItemAt(i).getId() == showtime.getTheaterId()) {
                    theaterBox.setSelectedIndex(i); break;
                }
            }
        }
        styleComboBox(theaterBox);
        formPanel.add(createFieldPanel(theaterBox, "Rạp:"));
        formPanel.add(Box.createVerticalStrut(6));

        // DateTime
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateTimeSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateTimeSpinner, "dd/MM/yyyy HH:mm");
        dateTimeSpinner.setEditor(dateEditor);
        dateTimeSpinner.setFont(normalFont);
        if (showtime != null) dateTimeSpinner.setValue(showtime.getShowDateTime());
        else dateTimeSpinner.setValue(new Date());
        formPanel.add(createFieldPanel(dateTimeSpinner, "Thời gian:"));
        formPanel.add(Box.createVerticalStrut(6));

        // Price
        JTextField priceField = new JTextField(showtime != null ? String.valueOf((int)showtime.getPrice()) : "");
        styleTextField(priceField);
        formPanel.add(createFieldPanel(priceField, "Giá vé:"));
        formPanel.add(Box.createVerticalStrut(6));

        // Status
        JCheckBox statusCheckBox = new JCheckBox("Hoạt động");
        statusCheckBox.setFont(normalFont);
        statusCheckBox.setBackground(Color.WHITE);
        statusCheckBox.setSelected(showtime == null || showtime.isStatus());
        formPanel.add(createFieldPanel(statusCheckBox, "Trạng thái:"));
        formPanel.add(Box.createVerticalStrut(12));

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(8));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton, accentColor);
        styleButton(cancelButton, errorColor);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        saveButton.addActionListener(e -> {
            try {
                Movie selectedMovie = (Movie) movieBox.getSelectedItem();
                Theater selectedTheater = (Theater) theaterBox.getSelectedItem();
                Date showDateTime = (Date) dateTimeSpinner.getValue();
                double price = Double.parseDouble(priceField.getText().trim());
                boolean status = statusCheckBox.isSelected();
                boolean success;
                if (showtime == null) {
                    Showtime newShowtime = new Showtime();
                    newShowtime.setMovieId(selectedMovie.getId());
                    newShowtime.setTheaterId(selectedTheater.getId());
                    newShowtime.setShowDateTime(showDateTime);
                    newShowtime.setPrice(price);
                    newShowtime.setStatus(status);
                    success = showtimeController.addShowtime(newShowtime);
                } else {
                    showtime.setMovieId(selectedMovie.getId());
                    showtime.setTheaterId(selectedTheater.getId());
                    showtime.setShowDateTime(showDateTime);
                    showtime.setPrice(price);
                    showtime.setStatus(status);
                    success = showtimeController.updateShowtime(showtime);
                }
                if (success) {
                    JOptionPane.showMessageDialog(dialog, (showtime == null ? "Thêm" : "Cập nhật") + " xuất chiếu thành công!");
                    refreshShowtimeTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, (showtime == null ? "Thêm" : "Cập nhật") + " xuất chiếu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá vé không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

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

    private void styleTextField(JTextField textField) {
        textField.setFont(normalFont);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(normalFont);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(normalFont);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
    }

    private void deleteShowtime() {
        int selectedRow = showtimeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn xuất chiếu cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa xuất chiếu này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int showtimeId = (int) showtimeTable.getValueAt(selectedRow, 0);
            if (showtimeController.deleteShowtime(showtimeId)) {
                JOptionPane.showMessageDialog(this, "Xóa xuất chiếu thành công!");
                refreshShowtimeTable();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa xuất chiếu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchShowtimes() {
        String keyword = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Showtime> showtimes = showtimeController.getAllShowtimes();
        for (Showtime showtime : showtimes) {
            if (showtime.getMovieTitle().toLowerCase().contains(keyword)
                || showtime.getTheaterName().toLowerCase().contains(keyword)
                || sdf.format(showtime.getShowDateTime()).contains(keyword)
                || String.format("%,.0f VND", showtime.getPrice()).contains(keyword)
                || (showtime.isStatus() ? "hoạt động" : "đã hủy").contains(keyword)) {
                Object[] row = {
                    showtime.getId(),
                    showtime.getMovieTitle(),
                    showtime.getTheaterName(),
                    sdf.format(showtime.getShowDateTime()),
                    String.format("%,.0f VND", showtime.getPrice()),
                    showtime.isStatus() ? "Hoạt động" : "Đã hủy"
                };
                tableModel.addRow(row);
            }
        }
    }
}