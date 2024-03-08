package com.example.quickscanr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class for the Event Details page. Handles population of
 * page components when passed an event. Also can display a
 * confirmation dialog to check an attendee into an event if
 * provided with the argument.
 */
public class EventDetails extends InnerPageFragment {

    private static final String EVENT = "event";
    private static final String SHOWCONFDIALOG = "showConfDialog";

    private Event event;
    private boolean showConfDialog;

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
            if (getArguments().size() == 1) {
                event = (Event) getArguments().getSerializable(EVENT);
            } else if (getArguments().size() == 2) {
                event = (Event) getArguments().getSerializable(EVENT);
                showConfDialog = (boolean) getArguments().getSerializable(SHOWCONFDIALOG);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_details, container, false);
        addButtonListeners(getActivity(), v);
        populatePage(v);
        if (showConfDialog) {
            showConfDialog();
            showConfDialog = false;
        }
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

    /**
     * Shows check in confirmation dialog to the user.
     */
    public void showConfDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Check In")
                .setMessage("Do you want to check in to this event?")
                .setPositiveButton(android.R.string.yes, (dialog, x) -> {
                    Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
        alertDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        alertDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }
}