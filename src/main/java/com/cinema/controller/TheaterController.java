package com.cinema.controller;

import com.cinema.dao.TheaterDAO;
import com.cinema.model.Theater;
import java.util.List;

public class TheaterController {
    private TheaterDAO theaterDAO;

    public TheaterController() {
        theaterDAO = new TheaterDAO();
    }

    public List<Theater> getAllTheaters() {
        return theaterDAO.getAllTheaters();
    }

    public Theater getTheaterById(int id) {
        return theaterDAO.getTheaterById(id);
    }

    public boolean addTheater(Theater theater) {
        return theaterDAO.addTheater(theater);
    }

    public boolean updateTheater(Theater theater) {
        return theaterDAO.updateTheater(theater);
    }

    public boolean deleteTheater(int id) {
        return theaterDAO.deleteTheater(id);
    }

    public List<Theater> searchTheaters(String keyword) {
        return theaterDAO.searchTheaters(keyword);
    }
} 