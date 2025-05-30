package com.cinema.controller;

import com.cinema.dao.TicketDAO;
import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import java.util.List;

public class TicketController {
    private TicketDAO ticketDAO;

    public TicketController() {
        ticketDAO = new TicketDAO();
    }

    public boolean addTicket(Ticket ticket) {
        return ticketDAO.addTicket(ticket);
    }

    public boolean updateTicket(Ticket ticket) {
        return ticketDAO.updateTicket(ticket);
    }

    public boolean deleteTicket(int id) {
        return ticketDAO.deleteTicket(id);
    }

    public Ticket getTicketById(int id) {
        return ticketDAO.getTicketById(id);
    }
    public Movie getMovieById(int id) {
        return ticketDAO.getMovieById(id);
    }
    public List<Ticket> getAllTickets() {
        return ticketDAO.getAllTickets();
    }

    public List<String> getBookedSeatsByShowtime(int showtimeId) {
        return ticketDAO.getBookedSeatsByShowtime(showtimeId);
    }

    public List<String> getBookedSeats(int movieId, int theaterId, java.sql.Timestamp showTime) {
        return ticketDAO.getBookedSeats(movieId, theaterId, showTime);
    }
} 