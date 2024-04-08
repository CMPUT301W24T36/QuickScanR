/**
 * manage and allow for live location updates
 */

package com.example.quickscanr;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * manage and allow for live location updates
 */
public class LocationHelper {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;
    private Context context;

    /**
     * the constructor
     * @param context context of page that needs location
     */
    public LocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }

    /**
     * allow for live location updates
     */
    public void startLocationUpdates() {
        try {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * stop live location updates
     */
    public void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    /**
     * get current location
     * @return location object of current location
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }
}