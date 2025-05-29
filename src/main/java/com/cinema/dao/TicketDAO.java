package com.cinema.dao;

import com.cinema.model.Ticket;

import com.cinema.model.Movie;
import com.cinema.model.Theater;
import com.cinema.model.Employee;
import com.cinema.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private MovieDAO movieDAO;
    private TheaterDAO theaterDAO;
    private EmployeeDAO employeeDAO;

    public TicketDAO() {

        movieDAO = new MovieDAO();
        theaterDAO = new TheaterDAO();
        employeeDAO = new EmployeeDAO();
    }

    public boolean addTicket(Ticket ticket) {
        // Nếu ticket.getSeatNumber() chứa nhiều ghế (phân tách bởi dấu phẩy), insert từng ghế
        String[] seats = ticket.getSeatNumber().split(",");
        String sql = "INSERT INTO tickets (movie_id, theater_id, show_time, seat_number, price, employee_id, purchase_date, customer_name, customer_phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            for (String seat : seats) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, ticket.getMovie().getId());
                    stmt.setInt(2, ticket.getTheater().getId());
                    stmt.setTimestamp(3, new java.sql.Timestamp(ticket.getShowTime().getTime()));
                    stmt.setString(4, seat.trim());
                    stmt.setDouble(5, ticket.getPrice());
                    stmt.setInt(6, ticket.getEmployee().getId());
                    stmt.setTimestamp(7, new java.sql.Timestamp(ticket.getPurchaseDate().getTime()));
                    stmt.setString(8, ticket.getCustomerName());
                    stmt.setString(9, ticket.getCustomerPhone());
                    int affected = stmt.executeUpdate();
                    if (affected == 0) return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTicket(Ticket ticket) {
        // TODO: Implement database operation
        return true;
    }

    public boolean deleteTicket(int id) {
        // TODO: Implement database operation
        return true;
    }

    public Ticket getTicketById(int id) {
        // TODO: Implement database operation
        return null;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> getTicketsByMovie(int movieId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE movie_id=?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> getTicketsByTheater(int theaterId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE theater_id=?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, theaterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> getTicketsByEmployee(int employeeId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE employee_id=?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(price) as total FROM tickets";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getRevenueByMovie(int movieId) {
        String sql = "SELECT SUM(price) as total FROM tickets WHERE movie_id=?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<String> getBookedSeats(int movieId, int theaterId, Timestamp showTime) {
        List<String> seats = new ArrayList<>();
        String sql = "SELECT seat_number FROM tickets WHERE movie_id = ? AND theater_id = ? AND show_time = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, theaterId);
            stmt.setTimestamp(3, showTime);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seats.add(rs.getString("seat_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public List<String> getBookedSeatsByShowtime(int showtimeId) {
        List<String> seats = new ArrayList<>();
        String sql = "SELECT t.seat_number FROM tickets t JOIN showtimes s ON t.movie_id = s.movie_id AND t.theater_id = s.theater_id AND t.show_time = s.show_date_time WHERE s.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seats.add(rs.getString("seat_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public List<Ticket> getTicketsByDateRange(java.util.Date from, java.util.Date to) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE purchase_date >= ? AND purchase_date <= ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, new java.sql.Timestamp(from.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(to.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));

        Movie movie = movieDAO.getMovieById(rs.getInt("movie_id"));
        ticket.setMovie(movie);

        Theater theater = theaterDAO.getTheaterById(rs.getInt("theater_id"));
        ticket.setTheater(theater);

        ticket.setShowTime(rs.getTimestamp("show_time"));
        ticket.setSeatNumber(rs.getString("seat_number"));
        ticket.setPrice(rs.getDouble("price"));

        Employee employee = employeeDAO.getEmployee(rs.getInt("employee_id"));
        ticket.setEmployee(employee);

        ticket.setPurchaseDate(rs.getTimestamp("purchase_date"));
        ticket.setCustomerName(rs.getString("customer_name"));
        ticket.setCustomerPhone(rs.getString("customer_phone"));

        return ticket;
    }
}