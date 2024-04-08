/**
 * the place model
 */

package com.example.quickscanr;

/***
 * Represents a real world place in the application.
 */
public class Place {
    public String name;
    public String placeId;

    /**
     * Constructor
     * This constructor includes name and placeId
     * @param name
     * @param placeId
     */
    public Place(String name, String placeId) {
        this.name = name;
        this.placeId = placeId;
    }
}
