package com.example.hw3_lotfinder;

import java.io.Serializable;

// Model class representing a parking lot
public class Lot implements Serializable {

    // Firestore identification variable
    private String ID;
    private String Name;
    private String Rating;
    private String Prices;
    private String Vacancy;
    private String Distance;

    public Lot(String name, String rating, String prices, String vacancy, String distance) {
        this.Name = name;
        this.Rating = rating;
        this.Prices = prices;
        this.Vacancy = vacancy;
        this.Distance = distance;
        this.ID = String.valueOf(System.currentTimeMillis());
    }

    public Lot(String name, String rating, String prices, String vacancy, String distance, String id) {
        this.Name = name;
        this.Rating = rating;
        this.Prices = prices;
        this.Vacancy = vacancy;
        this.Distance = distance;
        this.ID = id;
    }

    // Firestore identifier getter
    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getRating() {
        return Rating;
    }

    public String getPrices() {
        return Prices;
    }

    public String getVacancy() {
        return Vacancy;
    }

    public String getDistance() {
        return Distance;
    }
}
