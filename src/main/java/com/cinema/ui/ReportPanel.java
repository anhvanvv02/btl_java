package com.cinema.ui;

import com.cinema.dao.MovieDAO;
import com.cinema.dao.TicketDAO;
import com.cinema.model.Movie;
import com.cinema.ui.interfaces.IReload;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportPanel extends JPanel implements IReload{
    private JTabbedPane tabbedPane;
    private JTable salesTable, movieTable;
    private DefaultTableModel salesTableModel, movieTableModel;
    private JDateChooser fromDate, toDate;
    private JButton btnGenerate;
    private MovieDAO movieDAO;
    private TicketDAO ticketDAO;

    public ReportPanel() {
        movieDAO = new MovieDAO();
        ticketDAO = new TicketDAO();
        initComponents();
    }

    @Override
    public void reloadData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Sales Report Panel
        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Date Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("From:"));
        fromDate = new JDateChooser();
        fromDate.setPreferredSize(new Dimension(150, 25));
        filterPanel.add(fromDate);

        filterPanel.add(new JLabel("To:"));
        toDate = new JDateChooser();
        toDate.setPreferredSize(new Dimension(150, 25));
        filterPanel.add(toDate);

        btnGenerate = new JButton("Generate Report");
        filterPanel.add(btnGenerate);

        salesPanel.add(filterPanel, BorderLayout.NORTH);

        // Sales Table
        String[] salesColumns = {"Date", "Movie", "Theater", "Tickets Sold", "Total Revenue"};
        salesTableModel = new DefaultTableModel(salesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = new JTable(salesTableModel);
        JScrollPane salesScrollPane = new JScrollPane(salesTable);
        salesPanel.add(salesScrollPane, BorderLayout.CENTER);

        // Movie Statistics Panel
        JPanel moviePanel = new JPanel(new BorderLayout());
        moviePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Movie Table
        String[] movieColumns = {"Movie", "Total Tickets", "Total Revenue", "Average Price"};
        movieTableModel = new DefaultTableModel(movieColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movieTable = new JTable(movieTableModel);
        JScrollPane movieScrollPane = new JScrollPane(movieTable);
        moviePanel.add(movieScrollPane, BorderLayout.CENTER);

        // Add tabs
        tabbedPane.addTab("Sales Report", salesPanel);
        tabbedPane.addTab("Movie Statistics", moviePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Add action listeners
        btnGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        // Load initial data
        loadMovieStatistics();
    }

    private void generateReport() {
        if (fromDate.getDate() == null || toDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select both from and to dates!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fromDate.getDate().after(toDate.getDate())) {
            JOptionPane.showMessageDialog(this, "From date cannot be after to date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear table
        salesTableModel.setRowCount(0);

        // Lấy danh sách vé đã bán theo khoảng ngày
        java.util.Date from = fromDate.getDate();
        java.util.Date to = toDate.getDate();
        List<com.cinema.model.Ticket> tickets = ticketDAO.getTicketsByDateRange(from, to);
        // Nhóm theo ngày, phim, rạp
        java.util.Map<String, Object[]> reportMap = new java.util.LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (com.cinema.model.Ticket ticket : tickets) {
            String dateStr = ticket.getPurchaseDate() != null ? sdf.format(ticket.getPurchaseDate()) : "";
            String movie = ticket.getMovie() != null ? ticket.getMovie().getTitle() : "";
            String theater = ticket.getTheater() != null ? ticket.getTheater().getName() : "";
            String key = dateStr + "|" + movie + "|" + theater;
            if (!reportMap.containsKey(key)) {
                reportMap.put(key, new Object[]{dateStr, movie, theater, 0, 0.0});
            }
            Object[] row = reportMap.get(key);
            row[3] = (int) row[3] + 1;
            row[4] = (double) row[4] + ticket.getPrice();
        }
        for (Object[] row : reportMap.values()) {
            row[4] = String.format("%,.0f VND", row[4]);
            salesTableModel.addRow(row);
        }
    }

    private void loadMovieStatistics() {
        movieTableModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMovies();
        
        for (Movie movie : movies) {
            double revenue = ticketDAO.getRevenueByMovie(movie.getId());
            int ticketCount = ticketDAO.getTicketsByMovie(movie.getId()).size();
            double avgPrice = ticketCount > 0 ? revenue / ticketCount : 0;
            
            Object[] row = {
                movie.getTitle(),
                ticketCount,
                String.format("$%.2f", revenue),
                String.format("$%.2f", avgPrice)
            };
            movieTableModel.addRow(row);
        }
    }
} 