package com.example.quickscanr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlaceAPI {
    public List<Place> autoComplete(String input) {
        List<Place> suggestions = new ArrayList<>();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            String apiKey = "AIzaSyBMQOq8FHVTz3ZX4KfPBruoWEbL5YdrbnU";
            String baseUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
            String requestUrl = String.format("%s?input=%s&key=%s", baseUrl, input, apiKey);

            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = reader.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
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