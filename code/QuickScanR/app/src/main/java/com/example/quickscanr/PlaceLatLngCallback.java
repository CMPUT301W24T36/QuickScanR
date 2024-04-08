/**
 * the callback interface for getting latitude and longitude from location
 */

package com.example.quickscanr;

import com.google.android.gms.maps.model.LatLng;

/**
 * A callback interface for receiving the latitude and longitude of a place.
 * This interface is used in asynchronous operations where the location data of a place,
 * identified by a place ID, is fetched from the Google Places API.
 */
public interface PlaceLatLngCallback {
    void onLatLngReceived(LatLng latLng);
}