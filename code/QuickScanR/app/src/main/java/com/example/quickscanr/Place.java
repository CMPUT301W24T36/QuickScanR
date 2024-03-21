package com.example.quickscanr;

public class Place {
    private String name;
    private String placeId;
    private double latitude;
    private double longitude;

    // Constructor used for autocomplete suggestions
    public Place(String name, String placeId) {
        this.name = name;
        this.placeId = placeId;
    }

    // Full constructor
    public Place(String name, String placeId, double latitude, double longitude) {
        this.name = name;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    // Getter methods for latitude and longitude
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return name;
    }
}