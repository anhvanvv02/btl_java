package com.cinema.dao;

import com.cinema.model.Showtime;
import com.cinema.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ShowtimeDAO {

    // Get all showtimes
    public List<Showtime> getAllShowtimes() {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movie_title, t.name as theater_name " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN theaters t ON s.theater_id = t.id";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setId(rs.getInt("id"));
                showtime.setMovieId(rs.getInt("movie_id"));
                showtime.setTheaterId(rs.getInt("theater_id"));
                showtime.setShowDateTime(rs.getTimestamp("show_date_time"));
                showtime.setPrice(rs.getDouble("price"));
                showtime.setStatus(rs.getBoolean("status"));
                showtime.setMovieTitle(rs.getString("movie_title"));
                showtime.setTheaterName(rs.getString("theater_name"));
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public List<Showtime> getShowtimesByMovie(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movie_title, t.name as theater_name " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN theaters t ON s.theater_id = t.id " +
                "WHERE s.movie_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Showtime showtime = new Showtime();
                    showtime.setId(rs.getInt("id"));
                    showtime.setMovieId(rs.getInt("movie_id"));
                    showtime.setTheaterId(rs.getInt("theater_id"));
                    showtime.setShowDateTime(rs.getTimestamp("show_date_time"));
                    showtime.setPrice(rs.getDouble("price"));
                    showtime.setStatus(rs.getBoolean("status"));
                    showtime.setMovieTitle(rs.getString("movie_title"));
                    showtime.setTheaterName(rs.getString("theater_name"));
                    showtimes.add(showtime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public List<Showtime> getShowtimesByTheater(int theaterId) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movie_title, t.name as theater_name " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN theaters t ON s.theater_id = t.id " +
                "WHERE s.theater_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, theaterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Showtime showtime = new Showtime();
                    showtime.setId(rs.getInt("id"));
                    showtime.setMovieId(rs.getInt("movie_id"));
                    showtime.setTheaterId(rs.getInt("theater_id"));
                    showtime.setShowDateTime(rs.getTimestamp("show_date_time"));
                    showtime.setPrice(rs.getDouble("price"));
                    showtime.setStatus(rs.getBoolean("status"));
                    showtime.setMovieTitle(rs.getString("movie_title"));
                    showtime.setTheaterName(rs.getString("theater_name"));
                    showtimes.add(showtime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public boolean addShowtime(Showtime showtime) {
        String sql = "INSERT INTO showtimes (movie_id, theater_id, show_date_time, price, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, showtime.getMovieId());
            ps.setInt(2, showtime.getTheaterId());
            ps.setTimestamp(3, new Timestamp(showtime.getShowDateTime().getTime()));
            ps.setDouble(4, showtime.getPrice());
            ps.setBoolean(5, showtime.isStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding showtime: " + showtime);
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateShowtime(Showtime showtime) {
        String sql = "UPDATE showtimes SET movie_id=?, theater_id=?, show_date_time=?, price=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, showtime.getMovieId());
            ps.setInt(2, showtime.getTheaterId());
            ps.setTimestamp(3, new Timestamp(showtime.getShowDateTime().getTime()));
            ps.setDouble(4, showtime.getPrice());
            ps.setBoolean(5, showtime.isStatus());
            ps.setInt(6, showtime.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteShowtime(int id) {
        String sql = "DELETE FROM showtimes WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasConflict(Showtime showtime) {
        String sql = "SELECT COUNT(*) FROM showtimes " +
                "WHERE theater_id = ? AND status = true " +
                "AND ((show_date_time BETWEEN ? AND DATE_ADD(?, INTERVAL 2 HOUR)) " +
                "OR (DATE_ADD(show_date_time, INTERVAL 2 HOUR) BETWEEN ? AND DATE_ADD(?, INTERVAL 2 HOUR))) " +
                "AND id != ? " +
                "AND DATE(show_date_time) = DATE(?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, showtime.getTheaterId());
            ps.setTimestamp(2, new Timestamp(showtime.getShowDateTime().getTime()));
            ps.setTimestamp(3, new Timestamp(showtime.getShowDateTime().getTime()));
            ps.setTimestamp(4, new Timestamp(showtime.getShowDateTime().getTime()));
            ps.setTimestamp(5, new Timestamp(showtime.getShowDateTime().getTime()));
            ps.setInt(6, showtime.getId() != 0 ? showtime.getId() : -1);
            ps.setTimestamp(7, new Timestamp(showtime.getShowDateTime().getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Showtime> getShowtimesByDateRange(Date startDate, Date endDate) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movie_title, t.name as theater_name " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN theaters t ON s.theater_id = t.id " +
                "WHERE DATE(show_date_time) BETWEEN DATE(?) AND DATE(?) " +
                "ORDER BY show_date_time";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Showtime showtime = new Showtime();
                    showtime.setId(rs.getInt("id"));
                    showtime.setMovieId(rs.getInt("movie_id"));
                    showtime.setTheaterId(rs.getInt("theater_id"));
                    showtime.setShowDateTime(rs.getTimestamp("show_date_time"));
                    showtime.setPrice(rs.getDouble("price"));
                    showtime.setStatus(rs.getBoolean("status"));
                    showtime.setMovieTitle(rs.getString("movie_title"));
                    showtime.setTheaterName(rs.getString("theater_name"));
                    showtimes.add(showtime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public Showtime getShowtimeById(int id) {
        String sql = "SELECT s.*, m.title as movie_title, t.name as theater_name " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN theaters t ON s.theater_id = t.id " +
                "WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Showtime showtime = new Showtime();
                    showtime.setId(rs.getInt("id"));
                    showtime.setMovieId(rs.getInt("movie_id"));
                    showtime.setTheaterId(rs.getInt("theater_id"));
                    showtime.setShowDateTime(rs.getTimestamp("show_date_time"));
                    showtime.setPrice(rs.getDouble("price"));
                    showtime.setStatus(rs.getBoolean("status"));
                    showtime.setMovieTitle(rs.getString("movie_title"));
                    showtime.setTheaterName(rs.getString("theater_name"));
                    return showtime;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Showtime> getValidShowtimesForMovieToday(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movie_title, t.name as theater_name " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN theaters t ON s.theater_id = t.id " +
                "WHERE s.movie_id = ? " +
                "AND DATE(s.show_date_time) = CURDATE() " +
                "AND s.status = true " +
                "AND s.show_date_time >= NOW() ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Showtime showtime = new Showtime();
                    showtime.setId(rs.getInt("id"));
                    showtime.setMovieId(rs.getInt("movie_id"));
                    showtime.setTheaterId(rs.getInt("theater_id"));
                    showtime.setShowDateTime(rs.getTimestamp("show_date_time"));
                    showtime.setPrice(rs.getDouble("price"));
                    showtime.setStatus(rs.getBoolean("status"));
                    showtime.setMovieTitle(rs.getString("movie_title"));
                    showtime.setTheaterName(rs.getString("theater_name"));
                    showtimes.add(showtime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
}