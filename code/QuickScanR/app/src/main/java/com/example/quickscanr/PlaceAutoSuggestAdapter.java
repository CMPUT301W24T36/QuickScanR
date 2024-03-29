package com.example.quickscanr;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutoSuggestAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<Place> results;
    private int resource;
    private Context context;

    private PlaceAPI placeAPI = new PlaceAPI();

    public PlaceAutoSuggestAdapter(Context context, int resId) {
        super(context, resId);
        this.context = context;
        this.resource = resId;
        this.results = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public String getItem(int pos) {
        return results.get(pos).name;
    }

    public Place getItemInfo(int position) {
        if (position >= 0 && position < results.size()) {
            return results.get(position);
        }
        return null;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Fetch autocomplete place data as PlaceInfo objects
                    List<Place> placeDescriptions = placeAPI.autoComplete(constraint.toString());
                    results = new ArrayList<>(placeDescriptions);

                    filterResults.values = placeDescriptions;
                    filterResults.count = placeDescriptions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The filter operation succeeded
                    PlaceAutoSuggestAdapter.this.results = (ArrayList<Place>) results.values;
                    notifyDataSetChanged();
                } else {
                    // The filter operation failed or was not needed
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}