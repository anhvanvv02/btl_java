package com.cinema.model;

public class Theater {
    private int id;
    private String name;
    private int capacity;
    private String screenType; // 2D, 3D, IMAX, etc.
    private boolean status; // available or not

    public Theater() {
    }

    public Theater(int id, String name, int capacity, String screenType, boolean status) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.screenType = screenType;
        this.status = status;
    }

    public Theater(int id) {
        this.id = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.name;
    }
} 