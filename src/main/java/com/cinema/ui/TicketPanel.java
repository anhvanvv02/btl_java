package com.cinema.ui;

import com.cinema.controller.MovieController;
import com.cinema.controller.TicketController;
import com.cinema.controller.ShowtimeController;
import com.cinema.model.Employee;
import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.model.Showtime;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import com.cinema.ui.interfaces.IReload;

public class TicketPanel extends JPanel implements IReload {
    private Employee currentEmployee;
    private MovieController movieController;
    private TicketController ticketController;
    private ShowtimeController showtimeController;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel seatsPanel;
    private Movie selectedMovie;
    private String selectedTime;
    private String selectedDate;
    private List<String> selectedSeats;
    private List<Showtime> showtimesForSelectedMovie;
    private Showtime selectedShowtime;
    private JTextField searchShowtimeField;
    
    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(230, 126, 34);
    private Color bgColor = new Color(236, 240, 241);
    private Color textColor = new Color(44, 62, 80);
    
    // Fonts
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    // Thêm biến searchField cho TicketPanel
    private JTextField searchField;

    public TicketPanel(Employee employee) {
        this.currentEmployee = employee;
        this.movieController = new MovieController();
        this.ticketController = new TicketController();
        this.showtimeController = new ShowtimeController();
        this.selectedSeats = new ArrayList<>();
        this.showtimesForSelectedMovie = new ArrayList<>();
        initComponents();
    }

    @Override
    public void reloadData() {
        reloadMovieList();
    }


    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // Content Panel with CardLayout
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(bgColor);

        // Add different booking steps
        contentPanel.add(createMovieSelectionPanel(), "MOVIE_SELECTION");
        contentPanel.add(createShowtimePanel(), "SHOWTIME");
        contentPanel.add(createSeatingPanel(), "SEATING");
        contentPanel.add(createSummaryPanel(), "SUMMARY");

        add(contentPanel, BorderLayout.CENTER);
        
        // Start with movie selection
        cardLayout.show(contentPanel, "MOVIE_SELECTION");
    }

    private JPanel createMovieSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Chọn Phim", SwingConstants.CENTER);
        headerLabel.setFont(titleFont);
        headerLabel.setForeground(primaryColor);
        panel.add(headerLabel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(bgColor);
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(220, 35));
        searchField.setFont(normalFont);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setToolTipText("Tìm theo tên phim hoặc thời lượng...");
        JButton searchButton = new JButton("Tìm kiếm");
        styleButton((AbstractButton)searchButton);
        searchButton.addActionListener(e -> reloadMovieList());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.SOUTH);

        // Movies List (vertical)
        JPanel moviesList = new JPanel();
        moviesList.setLayout(new BoxLayout(moviesList, BoxLayout.Y_AXIS));
        moviesList.setBackground(bgColor);
        JScrollPane scrollPane = new JScrollPane(moviesList);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        reloadMovieList(moviesList);
        return panel;
    }

    private void reloadMovieList() {
        JScrollPane scrollPane = (JScrollPane) ((JPanel) contentPanel.getComponent(0)).getComponent(2);
        JPanel moviesList = (JPanel) scrollPane.getViewport().getView();
        reloadMovieList(moviesList);
    }

    private void reloadMovieList(JPanel moviesList) {
        moviesList.removeAll();
        String keyword = searchField.getText().trim().toLowerCase();
        List<Movie> movies = movieController.GetAllMovieByDate();
        for (Movie movie : movies) {
            // Lấy danh sách suất chiếu hợp lệ cho phim này
            List<com.cinema.model.Showtime> validShowtimes = movieController.getValidShowtimesForMovieToday(movie.getId());
            JPanel movieCard = createMovieListCard(movie, validShowtimes);
            moviesList.add(movieCard);
            moviesList.add(Box.createVerticalStrut(12));
        }
        moviesList.revalidate();
        moviesList.repaint();
    }

    private JPanel createMovieListCard(Movie movie, List<com.cinema.model.Showtime> validShowtimes) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setMaximumSize(new Dimension(900, 120));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Poster
        JLabel posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(80, 96));
        posterLabel.setMinimumSize(new Dimension(80, 96));
        posterLabel.setMaximumSize(new Dimension(80, 96));
        try {
            String imagePath = movie.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                java.nio.file.Path fullPath = java.nio.file.Paths.get("src", "main", "resources", imagePath);
                ImageIcon originalIcon = new ImageIcon(fullPath.toString());
                Image originalImage = originalIcon.getImage();
                Image scaledImage = originalImage.getScaledInstance(80, 96, Image.SCALE_SMOOTH);
                posterLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                posterLabel.setText("No Image");
                posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
                posterLabel.setVerticalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            posterLabel.setText("Error Image");
        }
        card.add(posterLabel, BorderLayout.WEST);

        // Center: Title + Description
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        // Tên phim
        JLabel titleLabel = new JLabel("<html><b>" + movie.getTitle() + "</b></html>");
        titleLabel.setFont(headerFont);
        titleLabel.setForeground(textColor);
        infoPanel.add(titleLabel);
        // Mô tả (tối đa 2 dòng, cắt ...)
        String desc = movie.getDescription();
        if (desc == null) desc = "";
        String[] descLines = desc.split("\n");
        StringBuilder descShort = new StringBuilder();
        int lineCount = 0;
        for (String line : descLines) {
            if (lineCount >= 2) break;
            if (line.length() > 60) line = line.substring(0, 57) + "...";
            descShort.append(line).append("<br>");
            lineCount++;
        }
        if (descLines.length > 2 || desc.length() > 120) descShort.append("...");
        JLabel descLabel = new JLabel("<html><span style='color:#888;'>" + descShort + "</span></html>");
        descLabel.setFont(normalFont);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(descLabel);
        // Thời lượng + Diễn viên
        String actors = movie.getActors() != null ? String.join(", ", movie.getActors()) : "";
        if (actors.length() > 40) actors = actors.substring(0, 37) + "...";
        JLabel metaLabel = new JLabel("<html><span style='color:#555;'>" + movie.getDuration() + " phút | Diễn viên: " + actors + "</span></html>");
        metaLabel.setFont(normalFont);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(metaLabel);
        // Suất chiếu hôm nay
        List<String> todayTimes = new ArrayList<>();
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
        for (Showtime st : validShowtimes) {
            if (!st.isStatus()) continue;
            Calendar showCal = Calendar.getInstance();
            showCal.setTime(st.getShowDateTime());
            boolean sameDay = showCal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
                && showCal.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            if (sameDay) {
                todayTimes.add(sdfHour.format(st.getShowDateTime()));
            }
        }
        String showtimeText = todayTimes.isEmpty() ? "Không có suất chiếu hôm nay" :
            "Suất chiếu hôm nay: " + String.join(", ", todayTimes.subList(0, Math.min(5, todayTimes.size())))
            + (todayTimes.size() > 5 ? ", ..." : "");
        JLabel showtimeLabel = new JLabel("<html><span style='color:#2980b9;'>" + showtimeText + "</span></html>");
        showtimeLabel.setFont(normalFont);
        infoPanel.add(showtimeLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        // Nút chọn
        JButton selectButton = new JButton("Chọn");
        styleButton((AbstractButton)selectButton);
        selectButton.setPreferredSize(new Dimension(90, 36));
        selectButton.setFont(normalFont);
        selectButton.setBackground(accentColor);
        selectButton.setForeground(Color.WHITE);
        selectButton.addActionListener(e -> {
            selectedMovie = movie;
            // Mở dialog chọn suất chiếu và chọn ghế
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Đặt vé cho phim: " + movie.getTitle(), true);
            dialog.setSize(1400, 800);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)
            ));
            // Header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            headerPanel.setOpaque(false);
            JLabel header = new JLabel("ĐẶT VÉ PHIM");
            header.setFont(new Font("Segoe UI", Font.BOLD, 32));
            header.setForeground(primaryColor);
            headerPanel.add(header);
            mainPanel.add(headerPanel);
            mainPanel.add(Box.createVerticalStrut(18));
            // --- Layout 2 cột: suất chiếu trái, ghế phải ---
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
            contentPanel.setBackground(Color.WHITE);
            // --- Panel suất chiếu bên trái ---
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setBackground(Color.WHITE);
            leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
            JLabel labelSC = new JLabel("Chọn suất chiếu:");
            labelSC.setFont(headerFont);
            labelSC.setAlignmentX(Component.LEFT_ALIGNMENT);
            labelSC.setAlignmentY(TOP_ALIGNMENT);
            leftPanel.add(labelSC);
            leftPanel.add(Box.createVerticalStrut(8));
            // Thêm khai báo sdf ở đây
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            ButtonGroup group = new ButtonGroup();
            List<JRadioButton> radioButtons = new ArrayList<>();
            JPanel radioPanel = new JPanel();
            radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
            radioPanel.setBackground(new Color(245, 246, 250));
            radioPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
            ));
            radioPanel.setAlignmentY(TOP_ALIGNMENT);
            for (com.cinema.model.Showtime st : validShowtimes) {
                String label = "Rạp: " + st.getTheaterName() + " | Giờ: " + sdf.format(st.getShowDateTime()) + " | Giá: " + String.format("%,.0f", st.getPrice()) + " VND";
                JRadioButton radio = new JRadioButton(label);
                radio.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                radio.setBackground(new Color(245, 246, 250));
                radio.setFocusable(false);
                radio.setAlignmentX(LEFT_ALIGNMENT);
                radio.setHorizontalAlignment(SwingConstants.LEFT);
                group.add(radio);
                radioPanel.add(radio);
                radioPanel.add(Box.createVerticalStrut(8));
                radioButtons.add(radio);
            }
            leftPanel.add(radioPanel);
            leftPanel.add(Box.createVerticalStrut(8));
            // --- Panel ghế bên phải ---
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout());
            rightPanel.setBackground(Color.WHITE);
            JLabel labelGhe = new JLabel("Chọn ghế:");
            labelGhe.setFont(headerFont);
            labelGhe.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            rightPanel.add(labelGhe, BorderLayout.NORTH);
            JPanel seatsPanelDialog = new JPanel(new GridLayout(8, 10, 8, 8));
            seatsPanelDialog.setBackground(new Color(245, 246, 250));
            seatsPanelDialog.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
            ));
            JScrollPane seatsScroll = new JScrollPane(seatsPanelDialog);
            seatsScroll.setPreferredSize(new Dimension(700, 500));
            seatsScroll.setBorder(null);
            seatsScroll.getVerticalScrollBar().setUnitIncrement(16);
            rightPanel.add(seatsScroll, BorderLayout.CENTER);
            // --- Legend ---
            JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
            legend.setBackground(Color.WHITE);
            mainPanel.add(Box.createVerticalStrut(10));
            addLegendItem(legend, Color.WHITE, "Ghế trống");
            addLegendItem(legend, primaryColor, "Ghế đang chọn");
            addLegendItem(legend, Color.RED, "Ghế đã bán");
            rightPanel.add(legend, BorderLayout.SOUTH);
            // --- Thêm vào contentPanel ---
            contentPanel.add(leftPanel);
            contentPanel.add(rightPanel);
            mainPanel.add(contentPanel);
            mainPanel.add(Box.createVerticalStrut(10));
            // --- Tổng tiền ---
            JLabel totalLabel = new JLabel("Tổng tiền: 0 VND");
            totalLabel.setFont(headerFont);
            totalLabel.setForeground(accentColor);
            totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(totalLabel);
            // --- Nút tiếp tục ---
            JButton nextBtn = new JButton("Tiếp tục");
            styleButton(nextBtn);
            nextBtn.setPreferredSize(new Dimension(180, 48));
            nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            nextBtn.setBackground(new Color(46, 204, 113));
            nextBtn.setEnabled(false);
            nextBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            nextBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            mainPanel.add(Box.createVerticalStrut(18));
            mainPanel.add(nextBtn);
            // --- Sự kiện chọn suất chiếu ---
            for (int i = 0; i < radioButtons.size(); i++) {
                int idx = i;
                radioButtons.get(i).addActionListener(ev -> {
                    selectedShowtime = validShowtimes.get(idx);
                    selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedShowtime.getShowDateTime());
                    selectedTime = new SimpleDateFormat("HH:mm").format(selectedShowtime.getShowDateTime());
                    selectedSeats.clear();
                    java.sql.Timestamp showTime = new java.sql.Timestamp(selectedShowtime.getShowDateTime().getTime());
                    List<String> bookedSeats = ticketController.getBookedSeats(selectedShowtime.getMovieId(), selectedShowtime.getTheaterId(), showTime);
                    seatsPanelDialog.removeAll();
                    String[] rows = {"A", "B", "C", "D", "E", "F", "G", "H"};
                    for (String row : rows) {
                        for (int j = 1; j <= 10; j++) {
                            String seatCode = row + j;
                            JToggleButton seat = new JToggleButton(seatCode);
                            seat.setFont(normalFont);
                            seat.setFocusPainted(false);
                            seat.setBackground(Color.WHITE);
                            seat.setForeground(textColor);
                            seat.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true));
                            seat.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            seat.setPreferredSize(new Dimension(44, 44));
                            seat.setOpaque(true);
                            if (bookedSeats.contains(seatCode)) {
                                seat.setEnabled(false);
                                seat.setBackground(Color.RED);
                                seat.setForeground(Color.RED);
                            }
                            seat.addMouseListener(new java.awt.event.MouseAdapter() {
                                public void mouseEntered(java.awt.event.MouseEvent evt) {
                                    if (seat.isEnabled() && !seat.isSelected() && seat.isEnabled()) seat.setBackground(new Color(200, 230, 255));
                                    if (!seat.isEnabled()) seat.setBackground(Color.RED);
                                }
                                public void mouseExited(java.awt.event.MouseEvent evt) {
                                    if (seat.isEnabled() && !seat.isSelected()) seat.setBackground(Color.WHITE);
                                    if (!seat.isEnabled()) seat.setBackground(Color.RED);
                                }
                            });
                            seat.addActionListener(evv -> {
                                if (seat.isSelected()) {
                                    seat.setBackground(accentColor);
                                    seat.setForeground(Color.WHITE);
                                    selectedSeats.add(seatCode);
                                } else {
                                    seat.setBackground(Color.WHITE);
                                    seat.setForeground(textColor);
                                    selectedSeats.remove(seatCode);
                                }
                                double price = selectedShowtime != null ? selectedShowtime.getPrice() : 0;
                                totalLabel.setText("Tổng tiền: " + String.format("%,.0f", price * selectedSeats.size()) + " VND");
                                nextBtn.setEnabled(selectedSeats.size() > 0);
                            });
                            seatsPanelDialog.add(seat);
                        }
                    }
                    seatsPanelDialog.revalidate();
                    seatsPanelDialog.repaint();
                    totalLabel.setText("Tổng tiền: 0 VND");
                    nextBtn.setEnabled(false);
                });
            }
            // --- Tiếp tục sang popup xác nhận/thanh toán ---
            nextBtn.addActionListener(ev -> {
                // --- Dialog thanh toán ---
                JDialog payDialog = new JDialog(dialog, "Xác nhận thanh toán", true);
                payDialog.setSize(500, 520);
                payDialog.setLocationRelativeTo(dialog);
                payDialog.setLayout(new BorderLayout(10, 10));
                JPanel infoPanel2 = new JPanel();
                infoPanel2.setLayout(new BoxLayout(infoPanel2, BoxLayout.Y_AXIS));
                infoPanel2.setBackground(Color.WHITE);
                infoPanel2.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor, 1, true),
                    BorderFactory.createEmptyBorder(32, 48, 32, 48)
                ));
                JLabel confirmHeader = new JLabel("XÁC NHẬN THANH TOÁN", SwingConstants.CENTER);
                confirmHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
                confirmHeader.setForeground(primaryColor);
                confirmHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel infoGrid = new JPanel();
                infoGrid.setLayout(new BoxLayout(infoGrid, BoxLayout.Y_AXIS));
                infoGrid.setBackground(Color.WHITE);
                infoGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel phimLabel = new JLabel("Phim: " + selectedMovie.getTitle(), SwingConstants.CENTER);
                phimLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                phimLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel rapLabel = new JLabel("Rạp: " + selectedShowtime.getTheaterName(), SwingConstants.CENTER);
                rapLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                rapLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel suatchieuLabel = new JLabel("Suất chiếu: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(selectedShowtime.getShowDateTime()), SwingConstants.CENTER);
                suatchieuLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                suatchieuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel gheLabel = new JLabel("Ghế: " + String.join(", ", selectedSeats), SwingConstants.CENTER);
                gheLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                gheLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel totalPay = new JLabel("Tổng tiền: " + String.format("%,.0f", selectedShowtime.getPrice() * selectedSeats.size()) + " VND", SwingConstants.CENTER);
                totalPay.setFont(new Font("Segoe UI", Font.BOLD, 22));
                totalPay.setForeground(accentColor);
                totalPay.setAlignmentX(Component.CENTER_ALIGNMENT);
                // Thêm trường nhập tên khách hàng và SĐT
                JPanel customerPanel = new JPanel();
                customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
                customerPanel.setBackground(Color.WHITE);
                customerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel nameLabel = new JLabel("Tên khách hàng:");
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                JTextField nameField = new JTextField();
                nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                JLabel phoneLabel = new JLabel("Số điện thoại:");
                phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                JTextField phoneField = new JTextField();
                phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                customerPanel.add(Box.createVerticalStrut(10));
                customerPanel.add(nameLabel);
                customerPanel.add(nameField);
                customerPanel.add(Box.createVerticalStrut(8));
                customerPanel.add(phoneLabel);
                customerPanel.add(phoneField);
                customerPanel.add(Box.createVerticalStrut(10));
                infoGrid.add(phimLabel);
                infoGrid.add(Box.createVerticalStrut(8));
                infoGrid.add(rapLabel);
                infoGrid.add(Box.createVerticalStrut(8));
                infoGrid.add(suatchieuLabel);
                infoGrid.add(Box.createVerticalStrut(8));
                infoGrid.add(gheLabel);
                infoGrid.add(Box.createVerticalStrut(12));
                infoGrid.add(totalPay);
                infoGrid.add(customerPanel);
                infoPanel2.add(confirmHeader);
                infoPanel2.add(Box.createVerticalStrut(18));
                infoPanel2.add(infoGrid);
                JButton payBtn = new JButton("Xác nhận thanh toán");
                styleButton(payBtn);
                payBtn.setPreferredSize(new Dimension(220, 54));
                payBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
                payBtn.setBackground(new Color(46, 204, 113));
                payBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                payBtn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
                payBtn.addActionListener(evv -> {
                    String customerName = nameField.getText().trim();
                    String customerPhone = phoneField.getText().trim();
                    if (customerName.isEmpty() || customerPhone.isEmpty()) {
                        JOptionPane.showMessageDialog(payDialog, "Vui lòng nhập đầy đủ tên và số điện thoại khách hàng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    com.cinema.model.Ticket ticket = new com.cinema.model.Ticket();
                    ticket.setMovie(selectedMovie);
                    ticket.setTheater(new com.cinema.model.Theater(selectedShowtime.getTheaterId()));
                    ticket.setShowTime(selectedShowtime.getShowDateTime());
                    ticket.setSeatNumber(String.join(",", selectedSeats));
                    ticket.setPrice(selectedShowtime.getPrice());
                    ticket.setEmployee(currentEmployee);
                    ticket.setPurchaseDate(new java.util.Date());
                    ticket.setCustomerName(customerName);
                    ticket.setCustomerPhone(customerPhone);
                    boolean ok = ticketController.addTicket(ticket);
                    if (ok) {
                        JOptionPane.showMessageDialog(payDialog, "Đặt vé thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        payDialog.dispose();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(payDialog, "Đặt vé thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
                infoPanel2.add(Box.createVerticalStrut(32));
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                btnPanel.setBackground(Color.WHITE);
                btnPanel.add(payBtn);
                infoPanel2.add(btnPanel);
                payDialog.add(infoPanel2, BorderLayout.CENTER);
                payDialog.setVisible(true);
            });
            dialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
            dialog.setVisible(true);
        });
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(selectButton, BorderLayout.NORTH);
        card.add(buttonPanel, BorderLayout.EAST);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(230, 240, 255));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor, 2, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        });
        return card;
    }

    private JPanel createShowtimePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(bgColor);
        JButton backButton = new JButton("← Quay lại");
        styleButton((AbstractButton)backButton);
        backButton.addActionListener(e -> {
            reloadMovieList();
            cardLayout.show(contentPanel, "MOVIE_SELECTION");
        });
        JLabel headerLabel = new JLabel("Chọn Suất Chiếu", SwingConstants.CENTER);
        headerLabel.setFont(titleFont);
        headerLabel.setForeground(primaryColor);
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Date Selection
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        datePanel.setBackground(bgColor);
        List<String> availableDates = new ArrayList<>();
        SimpleDateFormat sdfBtn = new SimpleDateFormat("dd/MM");
        SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy-MM-dd");
        // Lấy các ngày có suất chiếu hợp lệ
        if (selectedMovie != null) {
            showtimesForSelectedMovie = new ArrayList<>();
            List<Showtime> allShowtimes = showtimeController.getShowtimesByMovie(selectedMovie.getId());
            Calendar today = Calendar.getInstance();
            Date now = new Date();
            for (Showtime st : allShowtimes) {
                Calendar showCal = Calendar.getInstance();
                showCal.setTime(st.getShowDateTime());
                boolean sameDay = showCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && showCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
                if (sameDay && st.isStatus() && st.getShowDateTime().after(now)) {
                    showtimesForSelectedMovie.add(st);
                    String dateStr = sdfKey.format(st.getShowDateTime());
                    if (!availableDates.contains(dateStr)) availableDates.add(dateStr);
                }
            }
        }
        // Nếu chưa chọn ngày thì chọn ngày đầu tiên
        if ((selectedDate == null || selectedDate.isEmpty()) && !availableDates.isEmpty()) {
            selectedDate = availableDates.get(0);
        }
        ButtonGroup dateBtnGroup = new ButtonGroup();
        for (String dateStr : availableDates) {
            JToggleButton dateButton = new JToggleButton(sdfBtn.format(java.sql.Date.valueOf(dateStr)));
            styleButton((AbstractButton)dateButton);
            dateButton.setPreferredSize(new Dimension(80, 40));
            if (dateStr.equals(selectedDate)) dateButton.setSelected(true);
            dateButton.addActionListener(e -> {
                selectedDate = dateStr;
                updateShowtimeList(panel);
            });
            dateBtnGroup.add(dateButton);
            datePanel.add(dateButton);
        }

        // Search showtime by hour
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(bgColor);
        searchShowtimeField = new JTextField(12);
        searchShowtimeField.setPreferredSize(new Dimension(120, 32));
        searchShowtimeField.setFont(normalFont);
        searchShowtimeField.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchShowtimeField.setToolTipText("Tìm theo giờ (vd: 17 hoặc 17:30)");
        JButton searchButton = new JButton("Tìm giờ");
        styleButton((AbstractButton)searchButton);
        searchButton.addActionListener(e -> updateShowtimeList(panel));
        searchPanel.add(searchShowtimeField);
        searchPanel.add(searchButton);

        // Showtime List Panel
        JPanel showtimeListPanel = new JPanel();
        showtimeListPanel.setLayout(new BoxLayout(showtimeListPanel, BoxLayout.Y_AXIS));
        showtimeListPanel.setBackground(bgColor);
        JScrollPane scrollPane = new JScrollPane(showtimeListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 20));
        centerPanel.setBackground(bgColor);
        centerPanel.add(datePanel, BorderLayout.NORTH);
        centerPanel.add(searchPanel, BorderLayout.CENTER);
        centerPanel.add(scrollPane, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Nếu không có suất chiếu hợp lệ
        if (showtimesForSelectedMovie == null || showtimesForSelectedMovie.isEmpty()) {
            showtimeListPanel.removeAll();
            JLabel noShowtime = new JLabel("Không có suất chiếu hợp lệ cho phim này hôm nay.", SwingConstants.CENTER);
            noShowtime.setFont(headerFont);
            noShowtime.setForeground(Color.RED);
            showtimeListPanel.add(Box.createVerticalStrut(40));
            showtimeListPanel.add(noShowtime);
            showtimeListPanel.revalidate();
            showtimeListPanel.repaint();
        } else {
            updateShowtimeList(panel);
        }
        return panel;
    }

    private void updateShowtimeList(JPanel showtimePanel) {
        JPanel centerPanel = (JPanel) ((BorderLayout) showtimePanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JScrollPane scrollPane = (JScrollPane) ((BorderLayout) centerPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        JPanel showtimeListPanel = (JPanel) scrollPane.getViewport().getView();
        showtimeListPanel.removeAll();
        SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
        String keyword = searchShowtimeField != null ? searchShowtimeField.getText().trim() : "";
        boolean hasShowtime = false;
        for (Showtime st : showtimesForSelectedMovie) {
            if (!st.isStatus()) continue;
            String dateStr = sdfKey.format(st.getShowDateTime());
            if (!dateStr.equals(selectedDate)) continue;
            String hourStr = sdfHour.format(st.getShowDateTime());
            if (!keyword.isEmpty() && !hourStr.contains(keyword)) continue;
            hasShowtime = true;
            JPanel card = new JPanel(new BorderLayout(10, 0));
            card.setMaximumSize(new Dimension(700, 60));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
            JLabel timeLabel = new JLabel(hourStr);
            timeLabel.setFont(headerFont);
            timeLabel.setForeground(primaryColor);
            card.add(timeLabel, BorderLayout.WEST);
            JLabel infoLabel = new JLabel("Rạp: " + st.getTheaterName() + " | Giá: " + String.format("%,.0f", st.getPrice()) + " VND");
            infoLabel.setFont(normalFont);
            infoLabel.setForeground(textColor);
            card.add(infoLabel, BorderLayout.CENTER);
            JButton selectButton = new JButton("Chọn");
            styleButton((AbstractButton)selectButton);
            selectButton.setBackground(accentColor);
            selectButton.addActionListener(e -> {
                selectedShowtime = st;
                selectedTime = hourStr;
                selectedDate = dateStr;
                selectedSeats.clear();
                // Lấy danh sách ghế đã đặt
                List<String> bookedSeats = ticketController.getBookedSeatsByShowtime(st.getId());
                // Tạo lại seatsPanel với trạng thái ghế đã đặt
                seatsPanel.removeAll();
                String[] rows = {"A", "B", "C", "D", "E", "F", "G", "H"};
                for (String row : rows) {
                    for (int i = 1; i <= 10; i++) {
                        String seatCode = row + i;
                        JToggleButton seat = new JToggleButton(seatCode);
                        styleSeatButton(seat);
                        if (bookedSeats.contains(seatCode)) {
                            seat.setEnabled(false);
                            seat.setBackground(Color.RED);
                            seat.setForeground(Color.WHITE);
                        }
                        seatsPanel.add(seat);
                    }
                }
                seatsPanel.revalidate();
                seatsPanel.repaint();
                cardLayout.show(contentPanel, "SEATING");
            });
            card.add(selectButton, BorderLayout.EAST);
            // Hover effect
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    card.setBackground(new Color(230, 240, 255));
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryColor, 2, true),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                    ));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    card.setBackground(Color.WHITE);
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                    ));
                }
            });
            showtimeListPanel.add(card);
            showtimeListPanel.add(Box.createVerticalStrut(8));
        }
        if (!hasShowtime) {
            JLabel noShowtime = new JLabel("Không có suất chiếu hợp lệ cho ngày này.", SwingConstants.CENTER);
            noShowtime.setFont(headerFont);
            noShowtime.setForeground(Color.RED);
            showtimeListPanel.add(Box.createVerticalStrut(40));
            showtimeListPanel.add(noShowtime);
        }
        showtimeListPanel.revalidate();
        showtimeListPanel.repaint();
    }

    private JPanel createSeatingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(bgColor);
        
        JButton backButton = new JButton("← Quay lại");
        styleButton((AbstractButton)backButton);
        backButton.addActionListener(e -> {
            // Khi quay lại từ chọn ghế về suất chiếu, cập nhật lại danh sách suất chiếu
            updateShowtimeList((JPanel) contentPanel.getComponent(1));
            cardLayout.show(contentPanel, "SHOWTIME");
        });
        
        JLabel headerLabel = new JLabel("Chọn Ghế", SwingConstants.CENTER);
        headerLabel.setFont(titleFont);
        headerLabel.setForeground(primaryColor);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Screen
        JPanel screenPanel = new JPanel();
        screenPanel.setBackground(new Color(52, 73, 94));
        screenPanel.setPreferredSize(new Dimension(600, 5));
        JLabel screenLabel = new JLabel("MÀN HÌNH", SwingConstants.CENTER);
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setFont(headerFont);
        
        JPanel screenWrapper = new JPanel(new BorderLayout(0, 10));
        screenWrapper.setBackground(bgColor);
        screenWrapper.add(screenLabel, BorderLayout.NORTH);
        screenWrapper.add(screenPanel, BorderLayout.CENTER);
        screenWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Seats
        seatsPanel = new JPanel(new GridLayout(8, 10, 5, 5));
        seatsPanel.setBackground(bgColor);
        
        String[] rows = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (String row : rows) {
            for (int i = 1; i <= 10; i++) {
                JToggleButton seat = new JToggleButton(row + i);
                styleSeatButton(seat);
                seatsPanel.add(seat);
            }
        }

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.setBackground(bgColor);
        
        addLegendItem(legendPanel, Color.WHITE, "Ghế trống");
        addLegendItem(legendPanel, primaryColor, "Ghế đã chọn");
        addLegendItem(legendPanel, Color.RED, "Ghế đã bán");

        // Continue Button
        JButton continueButton = new JButton("Tiếp tục");
        styleButton((AbstractButton)continueButton);
        continueButton.addActionListener(e -> {
            // Cập nhật lại panel tóm tắt trước khi show
            JPanel summaryPanel = (JPanel) contentPanel.getComponent(3);
            summaryPanel.removeAll();
            JPanel newSummary = createSummaryPanel();
            contentPanel.remove(3);
            contentPanel.add(newSummary, "SUMMARY");
            cardLayout.show(contentPanel, "SUMMARY");
        });

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(bgColor);
        centerPanel.add(screenWrapper, BorderLayout.NORTH);
        centerPanel.add(seatsPanel, BorderLayout.CENTER);
        centerPanel.add(legendPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(continueButton, BorderLayout.SOUTH);

        return panel;
    }

    private void addLegendItem(JPanel panel, Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(bgColor);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel label = new JLabel(text);
        label.setFont(normalFont);
        
        item.add(colorBox);
        item.add(label);
        panel.add(item);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(bgColor);
        
        JButton backButton = new JButton("← Quay lại");
        styleButton((AbstractButton)backButton);
        backButton.addActionListener(e -> {
            // Khi quay lại từ summary về chọn ghế, cập nhật lại seatsPanel
            JPanel seatingPanel = (JPanel) contentPanel.getComponent(2);
            seatsPanel.revalidate();
            seatsPanel.repaint();
            cardLayout.show(contentPanel, "SEATING");
        });
        
        JLabel headerLabel = new JLabel("Xác nhận đặt vé", SwingConstants.CENTER);
        headerLabel.setFont(titleFont);
        headerLabel.setForeground(primaryColor);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Summary Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Add summary details
        addSummaryRow(contentPanel, "Phim:", selectedMovie != null ? selectedMovie.getTitle() : "");
        addSummaryRow(contentPanel, "Ngày chiếu:", selectedDate);
        addSummaryRow(contentPanel, "Suất chiếu:", selectedTime);
        addSummaryRow(contentPanel, "Ghế đã chọn:", String.join(", ", selectedSeats));
        addSummaryRow(contentPanel, "Tổng tiền:", "75,000 VND x " + selectedSeats.size() + " = " + 
            (75000 * selectedSeats.size()) + " VND");

        // Confirm Button
        JButton confirmButton = new JButton("Xác nhận đặt vé");
        styleButton((AbstractButton)confirmButton);
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.addActionListener(e -> confirmBooking());

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(bgColor);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        centerPanel.add(confirmButton, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void addSummaryRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(normalFont);
        labelComponent.setForeground(textColor);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(normalFont);
        
        row.add(labelComponent, BorderLayout.WEST);
        row.add(valueComponent, BorderLayout.CENTER);
        
        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

    private void styleButton(AbstractButton button) {
        button.setFont(normalFont);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        if (button instanceof JButton) {
            ((JButton) button).setBorderPainted(false);
        }
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
        });
    }

    private void styleSeatButton(JToggleButton button) {
        button.setFont(normalFont);
        button.setBackground(Color.WHITE);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (button.isSelected()) {
                button.setBackground(primaryColor);
                button.setForeground(Color.WHITE);
                selectedSeats.add(button.getText());
            } else {
                button.setBackground(Color.WHITE);
                button.setForeground(textColor);
                selectedSeats.remove(button.getText());
            }
        });
    }

    private void updateShowtimes() {
        // TODO: Update available showtimes based on selected date
    }

    private void confirmBooking() {
        // TODO: Implement booking confirmation
        JOptionPane.showMessageDialog(this,
            "Đặt vé thành công!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
        // Sau khi đặt vé thành công, reload lại danh sách phim
        reloadMovieList();
        cardLayout.show(contentPanel, "MOVIE_SELECTION");
    }
} 