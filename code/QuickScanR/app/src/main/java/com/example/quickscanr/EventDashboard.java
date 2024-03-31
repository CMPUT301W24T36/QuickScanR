package com.example.quickscanr;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.Collections;

/**
 * This class manages the display of event details in the dashboard.
 * @see Event
 */
public class EventDashboard extends InnerPageFragment {

    private static final String EVENT = "event";
    private Event event;


    private RealtimeData attendeeCounter;

    // Required empty constructor

    public EventDashboard() {}

    /**
     * Creates a new instance of the fragment EventDashboard
     * @param event
     * @return the fragment, EventDashboard
     */
    public static EventDashboard newInstance(Event event) {
        EventDashboard fragment = new EventDashboard();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event); // Pass the event object to the fragment
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when creating the fragment EventDashboard
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(EVENT);
            attendeeCounter = new RealtimeData();
            attendeeCounter.setEventListener(new RealtimeData.EventAttendeeCountListener() {
                @Override
                public void onTotalCountUpdated(int newCount) {
                    getActivity().runOnUiThread(() -> {
                        TextView attendeeCountView = getView().findViewById(R.id.evdash_txt_stat4);
                        attendeeCountView.setText(String.valueOf(newCount));
                    });
                }
            });
        }
    }

    /**
     * To create the view relevant to EventDashboard
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view relevant to EventDashboard
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_dashboard, container, false);
        populatePage(v); // Populate the dashboard with event details
        setupAdditionalListeners(v); // Set up listeners for interactive elements
        addButtonListeners(getActivity(), v, new OrganizerEventList());
        if (event != null) {
            // Assume this method is adapted to handle a single event ID
            attendeeCounter.startListening(Collections.singletonList(event.getId()));
        }
        if (!MainActivity.user.getGeoLoc()) {
            MaterialButton mapBtn = v.findViewById(R.id.evdash_btn_map);
            mapBtn.setVisibility(View.INVISIBLE);
        }
        return v;
    }


    /**
     * This sets up listeners for UI buttons
     * @param v The view to set up the buttons for
     */
    private void setupAdditionalListeners(View v) {
        v.findViewById(R.id.evdash_btn_qrcode).setOnClickListener(view -> switchToFragment(PromotionQR.newInstance(event)));
        v.findViewById(R.id.evdash_btn_checkin).setOnClickListener(view -> switchToFragment(CheckInQR.newInstance(event)));
        v.findViewById(R.id.evdash_img_stat4).setOnClickListener(view -> {
            String eventId = event.getId();
            switchToFragment(CheckedInAttendeeList.newInstance(eventId));
        });

        v.findViewById(R.id.evdash_txt_stat4).setOnClickListener(view -> {
            String eventId = event.getId();
            switchToFragment(CheckedInAttendeeList.newInstance(eventId));
        });

        v.findViewById(R.id.evdash_btn_map).setOnClickListener(view -> {
            Intent myIntent = new Intent(EventDashboard.this.getContext(), CheckInMap.class);
            myIntent.putExtra("event", event);
            EventDashboard.this.startActivity(myIntent);
        });

        v.findViewById(R.id.evdash_img_stat3).setOnClickListener(view -> {
            switchToFragment(SignedUpList.newInstance(event));
        });
    }

    /**
     * Switches to a new fragment
     * @param fragment The fragment to switch to
     */
    private void switchToFragment(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Fills in the event details into the respective views
     * @param v The view which is being populated
     */
    private void populatePage(View v) {
        // Bind event data to UI elements
        ((TextView) v.findViewById(R.id.evdash_text_name)).setText(event.getName());
        ((ImageView) v.findViewById(R.id.evdash_img_poster)).setImageResource(R.drawable.ic_launcher_background); // Placeholder, needs replacement.
        ((TextView) v.findViewById(R.id.evdash_txt_stat1)).setText(event.getLocationName());
        ((TextView) v.findViewById(R.id.evdash_txt_stat2)).setText(event.getStart());
        ((TextView) v.findViewById(R.id.evdash_txt_stat3)).setText(String.valueOf(event.getSignUpCount()));

    }
}