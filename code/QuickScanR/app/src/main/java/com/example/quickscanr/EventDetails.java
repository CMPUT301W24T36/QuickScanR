package com.example.quickscanr;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetails extends InnerPageFragment {

    private static final String EVENT = "event";

    private Event event;

    public EventDetails() {}

    public static EventDetails newInstance(Event event) {
        EventDetails fragment = new EventDetails();
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
        View v = inflater.inflate(R.layout.event_details, container, false);
        addButtonListeners(getActivity(), v);
        populatePage(v);
        return v;
    }

    /**
     * Populates the page with the given event data
     * @param v View required to find IDs
     */
    private void populatePage(View v) {
        TextView host = v.findViewById(R.id.evdetail_txt_host);
        TextView location = v.findViewById(R.id.evdetail_txt_loc);
        TextView start = v.findViewById(R.id.evdetail_txt_start);
        TextView end = v.findViewById(R.id.evdetail_txt_end);
        TextView restrictions = v.findViewById(R.id.evdetail_txt_restrictions);
        ImageView hostPic = v.findViewById(R.id.evdetail_img_host);
        ImageView poster = v.findViewById(R.id.evdetail_img_poster);

        host.setText(event.getOrganizer().getName());
        hostPic.setImageResource(R.drawable.ic_launcher_background); // TO BE REPLACED
        poster.setImageBitmap(event.getPoster());
        location.setText(event.getLocation());
        start.setText(event.getStart());
        end.setText(event.getEnd());
        restrictions.setText(event.getRestrictions());
    }
}