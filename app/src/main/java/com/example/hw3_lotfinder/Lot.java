package com.example.hw3_lotfinder;

import java.io.Serializable;

// Model class representing a parking lot
public class Lot implements Serializable {

    private String ID;
    private String Name;
    private String Rating;
    private String Prices;
    private String Vacancy;
    private String LAT;
    private String LNG;
    private String Distance = "-1";

    public Lot(String name, String rating, String prices, String vacancy, String lat, String lng) {
        this.Name = name;
        this.Rating = rating;
        this.Prices = prices;
        this.Vacancy = vacancy;
        this.LAT = lat;
        this.LNG = lng;
        this.ID = String.valueOf(System.currentTimeMillis());
    }

    public Lot(String name, String rating, String prices, String vacancy, String lat, String lng, String id) {
        this.Name = name;
        this.Rating = rating;
        this.Prices = prices;
        this.Vacancy = vacancy;
        this.LAT = lat;
        this.LNG = lng;
        this.ID = id;
    }

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

    public String getLNG() {
        return LNG;
    }

    public String getLAT() {
        return LAT;
    }

    public String getDistance() {
        return Distance;
    }

    public void calculateDistance(double userLat, double userLng) {
        try {
            double lotLat = Double.parseDouble(this.LAT);
            double lotLng = Double.parseDouble(this.LNG);

            float[] result = new float[1];
            android.location.Location.distanceBetween(userLat, userLng, lotLat, lotLng, result);

            float distanceInMeters = result[0];

            if (distanceInMeters <= 999) {
                this.Distance = String.format("%.0f", distanceInMeters) + "m";
            } else {
                double distanceInKm = distanceInMeters / 1000.0;

                if (distanceInKm >= 1000) {
                    this.Distance = "+999km";
                } else if (distanceInKm < 10) {
                    this.Distance = String.format("%.2f", distanceInKm) + "km";
                } else if (distanceInKm < 100) {
                    this.Distance = String.format("%.1f", distanceInKm) + "km";
                } else {
                    this.Distance = String.format("%.0f", distanceInKm) + "km";
                }
            }
        } catch (Exception e) {
        }
    }
}
