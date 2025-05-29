package com.cinema.dao;

import com.cinema.model.Movie;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieDAO {


    public List<Movie> GetAllMovieByDate() {
        List<Movie> movies = new ArrayList<>();
        

        String query = "SELECT m.*, GROUP_CONCAT(DATE_FORMAT(s.show_date_time, '%H:%i') ORDER BY s.show_date_time) as showtimes " +
                       "FROM movies m " +
                       "JOIN showtimes s ON m.id = s.movie_id " +
                       "WHERE DATE(s.show_date_time) = CURDATE() " +
                       "AND s.status = true " +
                       "AND s.show_date_time >= NOW() " + 
                       "GROUP BY m.id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(extractMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }


    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT *, '' as showtimes FROM movies";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                movies.add(extractMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public Movie getMovieById(int id) {
        String query = "SELECT * FROM movies WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMovieFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addMovie(Movie movie) {
        String movieQuery = "INSERT INTO movies (title, description, genre, duration, poster_url, " +
                "director, release_date, actors) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(movieQuery)) {

            // Handle null actors list
            List<String> actors = movie.getActors();
            String actorsString = (actors != null && !actors.isEmpty()) ? String.join(", ", actors) : "";
            System.out.println("Actors: " + actorsString);

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setString(3, movie.getGenre());
            stmt.setInt(4, movie.getDuration());
            stmt.setString(5, movie.getImagePath());
            stmt.setString(6, movie.getDirector());
            stmt.setDate(7, new java.sql.Date(movie.getReleaseDate().getTime()));
            stmt.setString(8, actorsString);

            System.out.println("Executing SQL: " + movieQuery);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error adding movie:");
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("General error adding movie:");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMovie(Movie movie) {
        String movieQuery = "UPDATE movies SET title = ?, description = ?, genre = ?, duration = ?, " +
                "poster_url = ?, director = ?, release_date = ?, actors = ? " +
                "WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(movieQuery)) {
            // Handle null actors list
            List<String> actors = movie.getActors();
            String actorsString = (actors != null && !actors.isEmpty()) ? String.join(", ", actors) : "";

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setString(3, movie.getGenre());
            stmt.setInt(4, movie.getDuration());
            stmt.setString(5, movie.getImagePath());
            stmt.setString(6, movie.getDirector());
            stmt.setDate(7, new java.sql.Date(movie.getReleaseDate().getTime()));
            stmt.setString(8, actorsString);
            stmt.setInt(9, movie.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMovie(int id) {
        String deleteMovieQuery = "DELETE FROM movies WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(deleteMovieQuery)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Movie> searchMovies(String keyword) {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies " +
                "WHERE title LIKE ? OR genre LIKE ? OR director LIKE ? " +
                "OR actors LIKE ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(extractMovieFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    

    private Movie extractMovieFromResultSet(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setDescription(rs.getString("description"));
        movie.setGenre(rs.getString("genre"));
        movie.setDuration(rs.getInt("duration"));
        movie.setImagePath(rs.getString("poster_url"));
        movie.setDirector(rs.getString("director"));
        movie.setReleaseDate(rs.getDate("release_date"));
        movie.setShowtimes(rs.getString("showtimes") == null ? "" : rs.getString("showtimes"));
        // Handle actors as comma-separated string
        String actorsString = rs.getString("actors");
        if (actorsString != null && !actorsString.isEmpty()) {
            movie.setActors(Arrays.asList(actorsString.split("\\s*,\\s*")));
        } else {
            movie.setActors(new ArrayList<>());
        }

        return movie;
    }
}