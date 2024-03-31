package com.example.quickscanr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class uses Google's Places API to fetch autocomplete suggestions for place inputs.
 * It parses the API's response to construct a list of Place objects.
 * Each Place object represents a suggested place, containing both a description and a unique
 * place ID.
 *
 * @see Place
 */
public class PlaceAPI {
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
            // Construct the API request URL with the input and API key
            String apiKey = "AIzaSyBMQOq8FHVTz3ZX4KfPBruoWEbL5YdrbnU";
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
}