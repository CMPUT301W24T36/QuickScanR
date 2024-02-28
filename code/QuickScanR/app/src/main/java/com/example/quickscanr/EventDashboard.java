package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class EventDashboard extends InnerPageFragment {

    private static final String EVENT = "event";

    private Event event;

    public EventDashboard() {}

    public static EventDashboard newInstance(Event event) {
        EventDashboard fragment = new EventDashboard();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(EVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.event_dashboard, container, false);
        addButtonListeners(getActivity(), v, new OrganizerEventList());
        populatePage(v);
        return v;
    }

    /**
     * Populates the page with the given event data
     * @param v View required to find IDs
     */
    private void populatePage(View v) {
        TextView name = v.findViewById(R.id.evdash_text_name);
        TextView stat1 = v.findViewById(R.id.evdash_txt_stat1);
        TextView stat2 = v.findViewById(R.id.evdash_txt_stat2);
        TextView stat3 = v.findViewById(R.id.evdash_txt_stat3);
        TextView stat4 = v.findViewById(R.id.evdash_txt_stat4);
        ImageView poster = v.findViewById(R.id.evdash_img_poster);

        name.setText(event.getName());
        poster.setImageResource(R.drawable.ic_launcher_background); // TO BE REPLACED
        stat1.setText("Event Statistic 1"); // TO BE REPLACED
        stat2.setText("Event Statistic 2"); // TO BE REPLACED
        stat3.setText("Event Statistic 3"); // TO BE REPLACED
        stat4.setText("Event Statistic 4"); // TO BE REPLACED
    }
}