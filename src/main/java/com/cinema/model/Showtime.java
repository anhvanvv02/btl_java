package com.cinema.model;

import java.util.Date;

public class Showtime {
    private int id;
    private int movieId;
    private int theaterId;
    private Date showDateTime;    // Thời gian xuất chiếu
    private double price;
    private boolean status;
    
    // For displaying purposes
    private String movieTitle;
    private String theaterName;

    public Showtime() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(int theaterId) {
        this.theaterId = theaterId;
    }

    public Date getShowDateTime() {
        return showDateTime;
    }

    public void setShowDateTime(Date showDateTime) {
        this.showDateTime = showDateTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }
} 