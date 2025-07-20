package com.example.hw3_lotfinder;

import java.io.Serializable;

/**
 * Lot Class
 * ---------
 * Represents a parking lot object with key attributes such as name, rating, pricing,
 * geographic coordinates, vacancy status, and calculated distance from a reference location.
 *
 * Implements Serializable to allow Lot objects to be passed between activities via Intents.
 */
public class Lot implements Serializable {

    // Unique identifier for the lot (can be generated or provided)
    private String ID;

    // Core attributes of a parking lot
    private String Name;
    private String Rating;
    private String Prices;
    private String Vacancy;

    // Geographic coordinates as strings (lat/lng stored from Firestore)
    private String LAT;
    private String LNG;

    // Human-readable distance string (e.g., "400m", "1.2km")
    private String Distance = "-1";

    // Actual distance in meters, used for sorting or logic
    private double distanceInMeters = -1;

    /**
     * Constructor to initialize a lot without a predefined ID (uses system timestamp).
     */
    public Lot(String name, String rating, String prices, String vacancy, String lat, String lng) {
        this.Name = name;
        this.Rating = rating;
        this.Prices = prices;
        this.Vacancy = vacancy;
        this.LAT = lat;
        this.LNG = lng;
        this.ID = String.valueOf(System.currentTimeMillis()); // Generate a pseudo-unique ID
    }

    /**
     * Constructor to initialize a lot with a known ID (e.g., retrieved from Firestore).
     */
    public Lot(String name, String rating, String prices, String vacancy, String lat, String lng, String id) {
        this.Name = name;
        this.Rating = rating;
        this.Prices = prices;
        this.Vacancy = vacancy;
        this.LAT = lat;
        this.LNG = lng;
        this.ID = id;
    }

    // -----------------------------
    // Getter Methods
    // -----------------------------

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

    public String getLAT() {
        return LAT;
    }

    public String getLNG() {
        return LNG;
    }

    /**
     * Returns a human-readable string representation of the distance (e.g., "700m", "1.3km").
     */
    public String getDistance() {
        return Distance;
    }

    /**
     * Returns the raw distance value in meters.
     */
    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    // -----------------------------
    // Distance Calculation
    // -----------------------------

    /**
     * Calculates the distance from a given user location (latitude & longitude) to this lot.
     * Uses Android's Location API to compute the straight-line (great-circle) distance.
     *
     * @param userLat Latitude of the user's location
     * @param userLng Longitude of the user's location
     */
    public void calculateDistance(double userLat, double userLng) {
        try {
            double lotLat = Double.parseDouble(this.LAT);
            double lotLng = Double.parseDouble(this.LNG);

            float[] result = new float[1];
            android.location.Location.distanceBetween(userLat, userLng, lotLat, lotLng, result);
            distanceInMeters = result[0]; // result[0] = distance in meters

            // Convert the distance into a user-friendly string
            if (distanceInMeters <= 999) {
                this.Distance = String.format("%.0f", distanceInMeters) + "m";
            } else {
                double distanceInKm = distanceInMeters / 1000.0;

                if (distanceInKm >= 1000) {
                    this.Distance = "+999km"; // Cap display if distance is absurdly high
                } else if (distanceInKm < 10) {
                    this.Distance = String.format("%.2f", distanceInKm) + "km";
                } else if (distanceInKm < 100) {
                    this.Distance = String.format("%.1f", distanceInKm) + "km";
                } else {
                    this.Distance = String.format("%.0f", distanceInKm) + "km";
                }
            }

        } catch (Exception e) {
            // In case of parsing or calculation failure, set distance to max
            distanceInMeters = Double.MAX_VALUE;
        }
    }
}
