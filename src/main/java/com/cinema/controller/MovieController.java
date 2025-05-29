package com.cinema.controller;

import com.cinema.dao.MovieDAO;
import com.cinema.model.Movie;
import java.util.List;

public class MovieController {
    private MovieDAO movieDAO;

    public MovieController() {
        movieDAO = new MovieDAO();
    }

    public List<Movie> getAllMovies() {
        return movieDAO.getAllMovies();
    }

    public List<Movie> GetAllMovieByDate() {
        return movieDAO.GetAllMovieByDate();
    }

    public Movie getMovieById(int id) {
        return movieDAO.getMovieById(id);
    }

    public boolean addMovie(Movie movie) {
        return movieDAO.addMovie(movie);
    }

    public boolean updateMovie(Movie movie) {
        return movieDAO.updateMovie(movie);
    }

    public boolean deleteMovie(int id) {
        return movieDAO.deleteMovie(id);
    }

    public List<Movie> searchMovies(String keyword) {
        return movieDAO.searchMovies(keyword);
    }

    public List<com.cinema.model.Showtime> getValidShowtimesForMovieToday(int movieId) {
        com.cinema.dao.ShowtimeDAO showtimeDAO = new com.cinema.dao.ShowtimeDAO();
        return showtimeDAO.getValidShowtimesForMovieToday(movieId);
    }
} 