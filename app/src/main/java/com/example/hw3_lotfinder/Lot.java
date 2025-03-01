package com.example.hw3_lotfinder;

import java.io.Serializable;

// Model class representing a parking lot
public class Lot implements Serializable {

    private String Name;
    private double Rating;
    private String Prices;
    private String Vacancy;
    private int Distance;

    public Lot(String name, double rating, String prices, String vacancy, int distance) {
        this.Name = name;
        this.Rating = rating;
        this.Prices = prices;
        this.Vacancy = vacancy;
        this.Distance = distance;
    }

    public String getName() {
        return Name;
    }

    public double getRating() {
        return Rating;
    }

    public String getPrices() {
        return Prices;
    }

    public String getVacancy() {
        return Vacancy;
    }

    public int getDistance() {
        return Distance;
    }
}
