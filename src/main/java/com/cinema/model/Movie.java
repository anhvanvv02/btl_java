package com.cinema.model;

import java.util.Date;
import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String genre;
    private int duration; // in minutes
    private String imagePath;
    private String director;
    private List<String> actors; // List of actor names
    private Date releaseDate;
    private String rating; // e.g., "P", "C13", "C16", "C18"
    private boolean status; // true for active, false for inactive

    private String showtimes;
    public Movie() {
    }

    public Movie(int id, String title, String description, String genre, int duration,
            String imagePath, String director, List<String> actors, Date releaseDate, String rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.imagePath = imagePath;
        this.director = director;
        this.actors = actors;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public boolean isActive() {
        return status;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setShowtimes(String showtimes) {
        this.showtimes = showtimes;
    }

    public String getShowtimes() {
        return showtimes;
    }
    @Override
    public String toString() {
        return this.title;
    }
}