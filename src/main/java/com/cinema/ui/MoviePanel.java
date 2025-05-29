package com.cinema.ui;

import com.cinema.controller.MovieController;
import com.cinema.model.Movie;
import com.cinema.ui.interfaces.IReload;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class MoviePanel extends JPanel implements IReload{
    private MovieController movieController;
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private String selectedImagePath;
    
    // Movie genres
    private final String[] GENRES = {
        "Hành động", "Phiêu lưu", "Hoạt hình", "Hài hước", 
        "Tội phạm", "Tài liệu", "Chính kịch", "Gia đình", 
        "Giả tưởng", "Lịch sử", "Kinh dị", "Nhạc", 
        "Bí ẩn", "Lãng mạn", "Khoa học viễn tưởng", "Thể thao",
        "Kinh dị", "Chiến tranh"
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

    public MoviePanel() {
        movieController = new MovieController();
        initComponents();
        loadMovies();
    }
    @Override
    public void reloadData() {
        loadMovies();
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
        JLabel titleLabel = new JLabel("Quản Lý Phim");
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
        searchButton.addActionListener(e -> searchMovies());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        String[] columns = {"ID", "Tên Phim", "Thể Loại", "Thời Lượng", "Đạo Diễn", "Ngày Khởi Chiếu"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        movieTable = new JTable(tableModel);
        movieTable.setFont(normalFont);
        movieTable.setRowHeight(35);
        movieTable.setShowGrid(true);
        movieTable.setGridColor(new Color(189, 195, 199));
        movieTable.setSelectionBackground(new Color(52, 152, 219, 50));
        movieTable.setSelectionForeground(textColor);
        movieTable.setBackground(Color.WHITE);

        // Style header
        JTableHeader header = movieTable.getTableHeader();
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
        for (int i = 0; i < movieTable.getColumnCount(); i++) {
            movieTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Set column widths
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên Phim
        movieTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Thể Loại
        movieTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Thời Lượng
        movieTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Đạo Diễn
        movieTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Ngày Khởi Chiếu

        // Style table cells
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        for (int i = 0; i < movieTable.getColumnCount(); i++) {
            movieTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(movieTable);
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

        addButton = new JButton("Thêm Phim");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");

        styleButton(addButton, successColor);
        styleButton(editButton, primaryColor);
        styleButton(deleteButton, dangerColor);

        addButton.addActionListener(e -> showMovieDialog(null));
        editButton.addActionListener(e -> editSelectedMovie());
        deleteButton.addActionListener(e -> deleteSelectedMovie());

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

    private void loadMovies() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieController.getAllMovies();
        for (Movie movie : movies) {
            Object[] row = {
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration() + " phút",
                movie.getDirector(),
                new java.text.SimpleDateFormat("dd/MM/yyyy").format(movie.getReleaseDate())
            };
            tableModel.addRow(row);
        }
    }

    private void searchMovies() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Movie> movies = movieController.searchMovies(keyword);
        for (Movie movie : movies) {
            Object[] row = {
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration() + " phút",
                movie.getDirector(),
                new java.text.SimpleDateFormat("dd/MM/yyyy").format(movie.getReleaseDate())
            };
            tableModel.addRow(row);
        }
    }

    private void showMovieDialog(Movie movie) {
        JDialog dialog = new JDialog();
        dialog.setTitle(movie == null ? "Thêm Phim Mới" : "Cập Nhật Thông Tin Phim");
        dialog.setModal(true);
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(this);

        // Main panel with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel(movie == null ? "Thêm Phim Mới" : "Cập Nhật Thông Tin Phim");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Form fields
        JTextField titleField = createFormField("Tên phim:", movie != null ? movie.getTitle() : "");
        
        // Genre ComboBox
        JComboBox<String> genreBox = new JComboBox<>(GENRES);
        genreBox.setFont(normalFont);
        if (movie != null) {
            genreBox.setSelectedItem(movie.getGenre());
        }
        
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(
            movie != null ? movie.getDuration() : 90, 1, 300, 1));
        durationSpinner.setFont(normalFont);
        
        JTextField directorField = createFormField("Đạo diễn:", movie != null ? movie.getDirector() : "");
        
        // Release Date Chooser
        JDateChooser releaseDateChooser = new JDateChooser();
        releaseDateChooser.setFont(normalFont);
        releaseDateChooser.setDateFormatString("dd/MM/yyyy");
        if (movie != null && movie.getReleaseDate() != null) {
            releaseDateChooser.setDate(movie.getReleaseDate());
        }
        
        JTextArea descriptionArea = new JTextArea(movie != null ? movie.getDescription() : "");
        descriptionArea.setFont(normalFont);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Add actors field
        JTextArea actorsArea = new JTextArea(movie != null && movie.getActors() != null ? 
            String.join(", ", movie.getActors()) : "");
        actorsArea.setFont(normalFont);
        actorsArea.setLineWrap(true);
        actorsArea.setWrapStyleWord(true);
        actorsArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Image panel
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setBackground(Color.WHITE);
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(200, 300));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        if (movie != null && movie.getImagePath() != null) {
            selectedImagePath = movie.getImagePath();
            // Load image using resource path
            Path imagePath = Paths.get("src", "main", "resources", selectedImagePath);
            ImageIcon imageIcon = new ImageIcon(imagePath.toString());
            Image image = imageIcon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        }

        JButton chooseImageButton = new JButton("Chọn Ảnh");
        styleButton(chooseImageButton, primaryColor);
        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Create images directory if it doesn't exist
                    Path imagesDir = Paths.get("src", "main", "resources", "images");
                    if (!Files.exists(imagesDir)) {
                        Files.createDirectories(imagesDir);
                    }

                    // Generate unique filename
                    String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    Path targetPath = imagesDir.resolve(fileName);
                    
                    // Copy image to images directory
                    Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    
                    // Store relative path for database
                    selectedImagePath = "images/" + fileName;

                    // Load and display image preview using absolute path
                    ImageIcon imageIcon = new ImageIcon(targetPath.toString());
                    Image image = imageIcon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(image));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                        "Không thể tải ảnh lên. Vui lòng thử lại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        imagePanel.add(imageLabel);
        imagePanel.add(Box.createHorizontalStrut(10));
        imagePanel.add(chooseImageButton);

        // Add components to form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createFieldPanel(titleField, "Tên phim:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(genreBox, "Thể loại:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(durationSpinner, "Thời lượng (phút):"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(directorField, "Đạo diễn:"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel(releaseDateChooser, "Ngày khởi chiếu:"));
        formPanel.add(Box.createVerticalStrut(10));
        
        // Add actors panel
        JPanel actorsPanel = createFieldPanel(new JScrollPane(actorsArea), "Diễn viên (phân cách bằng dấu phẩy):");
        actorsPanel.setPreferredSize(new Dimension(actorsPanel.getPreferredSize().width, 100));
        formPanel.add(actorsPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        JPanel descriptionPanel = createFieldPanel(new JScrollPane(descriptionArea), "Mô tả:");
        descriptionPanel.setPreferredSize(new Dimension(descriptionPanel.getPreferredSize().width, 150));
        formPanel.add(descriptionPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(imagePanel);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton, successColor);
        styleButton(cancelButton, dangerColor);

        saveButton.addActionListener(e -> {
            if (validateMovieForm(titleField, directorField, releaseDateChooser, descriptionArea, actorsArea)) {
                Movie newMovie = movie != null ? movie : new Movie();
                newMovie.setTitle(titleField.getText().trim());
                newMovie.setGenre(genreBox.getSelectedItem().toString());
                newMovie.setDuration((Integer) durationSpinner.getValue());
                newMovie.setDirector(directorField.getText().trim());
                newMovie.setReleaseDate(releaseDateChooser.getDate());
                newMovie.setDescription(descriptionArea.getText().trim());
                newMovie.setImagePath(selectedImagePath);
                
                // Set actors
                String actorsText = actorsArea.getText().trim();
                if (!actorsText.isEmpty()) {
                    List<String> actorsList = Arrays.asList(actorsText.split("\\s*,\\s*"));
                    newMovie.setActors(actorsList);
                } else {
                    newMovie.setActors(new ArrayList<>());
                }

                boolean success;
                if (movie == null) {
                    success = movieController.addMovie(newMovie);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                            "Thêm phim thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    success = movieController.updateMovie(newMovie);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                            "Cập nhật thông tin thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                if (success) {
                    loadMovies();
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private JPanel createFieldPanel(JComponent field, String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(700, field instanceof JScrollPane ? 150 : 60));

        JLabel label = new JLabel(labelText);
        label.setFont(normalFont);
        label.setForeground(textColor);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JTextField createFormField(String label, String value) {
        JTextField field = new JTextField(value);
        field.setFont(normalFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private boolean validateMovieForm(JTextField titleField, JTextField directorField,
            JDateChooser releaseDateChooser, JTextArea descriptionArea, JTextArea actorsArea) {
        
        if (titleField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên phim!");
            titleField.requestFocus();
            return false;
        }

        if (directorField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên đạo diễn!");
            directorField.requestFocus();
            return false;
        }

        if (releaseDateChooser.getDate() == null) {
            showError("Vui lòng chọn ngày khởi chiếu!");
            releaseDateChooser.requestFocus();
            return false;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            showError("Vui lòng nhập mô tả phim!");
            descriptionArea.requestFocus();
            return false;
        }

        if (actorsArea.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên các diễn viên!");
            actorsArea.requestFocus();
            return false;
        }

        if (selectedImagePath == null || selectedImagePath.isEmpty()) {
            showError("Vui lòng chọn ảnh cho phim!");
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

    private void editSelectedMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) movieTable.getValueAt(selectedRow, 0);
            Movie movie = movieController.getMovieById(id);
            if (movie != null) {
                showMovieDialog(movie);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn phim cần sửa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) movieTable.getValueAt(selectedRow, 0);
            int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phim này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                if (movieController.deleteMovie(id)) {
                    loadMovies();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa phim này",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn phim cần xóa",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }
} 