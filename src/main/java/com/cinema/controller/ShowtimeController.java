package com.cinema.controller;

import com.cinema.dao.ShowtimeDAO;
import com.cinema.model.Showtime;
import java.util.List;
import java.util.Date;

public class ShowtimeController {
    private ShowtimeDAO showtimeDAO;

    public ShowtimeController() {
        this.showtimeDAO = new ShowtimeDAO();
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeDAO.getAllShowtimes();
    }

    public List<Showtime> getShowtimesByMovie(int movieId) {
        return showtimeDAO.getShowtimesByMovie(movieId);
    }

    public List<Showtime> getShowtimesByTheater(int theaterId) {
        return showtimeDAO.getShowtimesByTheater(theaterId);
    }

    public boolean addShowtime(Showtime showtime) {
        return showtimeDAO.addShowtime(showtime);
    }

    public boolean updateShowtime(Showtime showtime) {
        return showtimeDAO.updateShowtime(showtime);
    }

    public boolean deleteShowtime(int id) {
        return showtimeDAO.deleteShowtime(id);
    }

    public List<Showtime> getShowtimesByDateRange(Date startDate, Date endDate) {
        return showtimeDAO.getShowtimesByDateRange(startDate, endDate);
    }

    public boolean hasConflict(Showtime showtime) {
        return showtimeDAO.hasConflict(showtime);
    }

    public Showtime getShowtimeById(int id) {
        return showtimeDAO.getShowtimeById(id);
    }
} 