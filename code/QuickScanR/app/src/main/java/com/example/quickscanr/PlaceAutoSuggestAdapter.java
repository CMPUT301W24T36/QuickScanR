package com.example.quickscanr;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;

public class PlaceAutoSuggestAdapter extends ArrayAdapter<Place> implements Filterable {
    ArrayList<Place> results;

    int resource;
    Context context;

    PlaceAPI placeAPI = new PlaceAPI();

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
    public Place getItem(int pos) {
        return results.get(pos);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    ArrayList<Place> places = placeAPI.autoComplete(constraint.toString());
                    results = places;

                    filterResults.values = places;
                    filterResults.count = places.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // Cast to the correct type
                    PlaceAutoSuggestAdapter.this.results = (ArrayList<Place>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}