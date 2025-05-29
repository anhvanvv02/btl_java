package com.cinema.dao;

import com.cinema.model.Theater;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheaterDAO {

    public List<Theater> getAllTheaters() {
        List<Theater> theaters = new ArrayList<>();
        String query = "SELECT * FROM theaters";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                theaters.add(extractTheaterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theaters;
    }

    public Theater getTheaterById(int id) {
        String query = "SELECT * FROM theaters WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTheaterFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addTheater(Theater theater) {
        String query = "INSERT INTO theaters (name, capacity, screen_type, status) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, theater.getName());
            stmt.setInt(2, theater.getCapacity());
            stmt.setString(3, theater.getScreenType());
            stmt.setBoolean(4, theater.isStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTheater(Theater theater) {
        String query = "UPDATE theaters SET name = ?, capacity = ?, screen_type = ?, status = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, theater.getName());
            stmt.setInt(2, theater.getCapacity());
            stmt.setString(3, theater.getScreenType());
            stmt.setBoolean(4, theater.isStatus());
            stmt.setInt(5, theater.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTheater(int id) {
        String query = "DELETE FROM theaters WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Theater> searchTheaters(String keyword) {
        List<Theater> theaters = new ArrayList<>();
        String query = "SELECT * FROM theaters WHERE name LIKE ? OR screen_type LIKE ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    theaters.add(extractTheaterFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theaters;
    }

    private Theater extractTheaterFromResultSet(ResultSet rs) throws SQLException {
        Theater theater = new Theater();
        theater.setId(rs.getInt("id"));
        theater.setName(rs.getString("name"));
        theater.setCapacity(rs.getInt("capacity"));
        theater.setScreenType(rs.getString("screen_type"));
        theater.setStatus(rs.getBoolean("status"));
        return theater;
    }
}