package com.cinema.model;

import java.util.Date;

public class Ticket {
    private int id;
    private Movie movie;
    private Theater theater;
    private Date showTime;
    private String seatNumber;
    private double price;
    private Employee employee; // employee who sold the ticket
    private Date purchaseDate;
    private String customerName;
    private String customerPhone;

    public Ticket() {
    }

    public Ticket(int id, Movie movie, Theater theater, Date showTime, String seatNumber, double price, 
                 Employee employee, Date purchaseDate, String customerName, String customerPhone) {
        this.id = id;
        this.movie = movie;
        this.theater = theater;
        this.showTime = showTime;
        this.seatNumber = seatNumber;
        this.price = price;
        this.employee = employee;
        this.purchaseDate = purchaseDate;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
} 