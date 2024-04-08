package com.example.quickscanr;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This class uses Google's Places API to fetch autocomplete suggestions for place inputs.
 * It parses the API's response to construct a list of Place objects.
 * Each Place object represents a suggested place, containing both a description and a unique
 * place ID.
 *
 * @see Place
 */
public class PlaceAPI {

    Context context;

    public PlaceAPI(Context context) {
        this.context = context;
    }

    /**
     * Fetches autocomplete suggestions for a place input.
     * This method sends a request to the Google Places API and parses the response to extract
     * place suggestions. It constructs a list of Place objects, each representing a suggested
     * place with its description and place ID.
     *
     * @param input The user input for which place suggestions are to be fetched.
     * @return A list of Place objects representing the autocomplete suggestions.
     */
    public List<Place> autoComplete(String input) {
        List<Place> suggestions = new ArrayList<>();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            Bundle appMetadata = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            String apiKey = appMetadata.getString("PLACES_API_KEY");
            // Construct the API request URL with the input and API key
            String baseUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
            String requestUrl = String.format("%s?input=%s&key=%s", baseUrl, input, apiKey);

            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            // Read the response into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = reader.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the HttpURLConnection
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
            // Parse the JSON response to extract place suggestions
            JSONObject jsonObj = new JSONObject(jsonResult.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject predJsonObj = predsJsonArray.getJSONObject(i);
                String description = predJsonObj.getString("description");
                String placeId = predJsonObj.getString("place_id");
                suggestions.add(new Place(description, placeId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    /**
     * Fetches the location (latitude and longitude) for a given place ID.
     * This method sends a request to the Google Places API and parses the response
     * to extract the location details. It does so asynchronously on a separate thread and
     * the location is passed to a callback interface once received.
     *
     * @param placeId The unique identifier for the place.
     * @param mainHandler The handler for the main thread, used to post the result for UI updates
     * @param callback An instance of the PlaceLatLngCallback interface
     */
    public void getPlaceLatLng(final String placeId, final Handler mainHandler, final PlaceLatLngCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                StringBuilder jsonResult = new StringBuilder();
                try {
                    Bundle appMetadata = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
                    String apiKey = appMetadata.getString("PLACES_API_KEY");
                    // Construct the API request URL with the place ID and API key
                    String baseUrl = "https://maps.googleapis.com/maps/api/place/details/json";
                    String requestUrl = String.format("%s?place_id=%s&fields=geometry/location&key=%s", baseUrl, placeId, apiKey);

                    URL url = new URL(requestUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                    // Read the response into a StringBuilder
                    int read;
                    char[] buff = new char[1024];
                    while ((read = reader.read(buff)) != -1) {
                        jsonResult.append(buff, 0, read);
                    }

                    // Parse the JSON response to extract the location
                    JSONObject jsonObj = new JSONObject(jsonResult.toString());
                    JSONObject location = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    final LatLng latLng = new LatLng(lat, lng);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Invoke the callback with the fetched LatLng
                            callback.onLatLngReceived(latLng);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}