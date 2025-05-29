package com.cinema.ui;

import com.cinema.controller.TicketController;
import com.cinema.controller.MovieController;
import com.cinema.model.Ticket;
import com.cinema.ui.interfaces.IReload;
import com.cinema.model.Movie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class TicketManagementPanel extends JPanel  implements IReload{
    private TicketController ticketController;
    private MovieController movieController;
    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Ticket> allTickets;

    @Override
    public void reloadData(){
        reloadTable();
    }
    public TicketManagementPanel() {
        ticketController = new TicketController();
        movieController = new MovieController();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Quản lý vé đã bán", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(new Color(41, 128, 185));
        add(header, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(getBackground());
        searchField = new JTextField(24);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.setToolTipText("Tìm theo tên phim...");
        JButton searchBtn = new JButton("Tìm kiếm");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchBtn.setBackground(new Color(41, 128, 185));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> reloadTable());
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"ID", "Tên phim", "Rạp", "Suất chiếu", "Ghế", "Giá vé", "Nhân viên bán", "Ngày mua", "Tên KH", "SĐT KH"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketTable = new JTable(tableModel);
        ticketTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        ticketTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        ticketTable.setRowHeight(28);
        ticketTable.setSelectionBackground(new Color(52, 152, 219, 60));
        ticketTable.setGridColor(new Color(189, 195, 199));
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true));
        add(scrollPane, BorderLayout.CENTER);

        reloadTable();
    }

    private void reloadTable() {
        String keyword = searchField.getText().trim().toLowerCase();
        allTickets = ticketController.getAllTickets();
        tableModel.setRowCount(0);
        for (Ticket ticket : allTickets) {
            Movie movie = ticket.getMovie();
            String movieTitle = movie != null ? movie.getTitle() : "";
            if (!keyword.isEmpty() && !movieTitle.toLowerCase().contains(keyword)) continue;
            tableModel.addRow(new Object[] {
                ticket.getId(),
                movieTitle,
                ticket.getTheater() != null ? ticket.getTheater().getName() : "",
                ticket.getShowTime() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ticket.getShowTime()) : "",
                ticket.getSeatNumber(),
                String.format("%,.0f VND", ticket.getPrice()),
                ticket.getEmployee() != null ? ticket.getEmployee().getName() : "",
                ticket.getPurchaseDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ticket.getPurchaseDate()) : "",
                ticket.getCustomerName(),
                ticket.getCustomerPhone()
            });
        }
    }
} 