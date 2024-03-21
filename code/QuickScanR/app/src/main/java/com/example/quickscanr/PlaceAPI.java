package com.example.quickscanr;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaceAPI {
    public ArrayList<Place> autoComplete(String input) {
        ArrayList<Place> placesList = new ArrayList<>();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input=").append(input);
            sb.append("&key=AIzaSyBMQOq8FHVTz3ZX4KfPBruoWEbL5YdrbnU");
            URL url = new URL(sb.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONArray predictions = jsonObject.getJSONArray("predictions");
            for (int i = 0; i < predictions.length(); i++) {
                String description = predictions.getJSONObject(i).getString("description");
                String placeId = predictions.getJSONObject(i).getString("place_id");
                Place place = new Place(description, placeId);
                placesList.add(place);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return placesList;
    }

    public Place getPlaceDetails(String placeId) {
        Place place = null;
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
            sb.append("place_id=").append(placeId);
            sb.append("&fields=name,place_id,geometry/location");
            sb.append("&key=AIzaSyBMQOq8FHVTz3ZX4KfPBruoWEbL5YdrbnU");
            URL url = new URL(sb.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
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
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONObject result = jsonObject.getJSONObject("result");
            JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            String name = result.getString("name");
            String id = result.getString("place_id");

            place = new Place(name, id, lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }
}
