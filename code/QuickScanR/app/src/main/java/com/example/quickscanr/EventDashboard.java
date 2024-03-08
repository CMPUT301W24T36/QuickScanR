package com.example.quickscanr;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * This class manages the display of event details in the dashboard.
 */
public class EventDashboard extends InnerPageFragment {

    private static final String EVENT = "event";
    private Event event;

    private RealtimeData attendeeCounter;

    // Required empty constructor
    public EventDashboard() {}

    public static EventDashboard newInstance(Event event) {
        EventDashboard fragment = new EventDashboard();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event); // Pass the event object to the fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(EVENT);
            attendeeCounter = new RealtimeData();
            attendeeCounter.setEventListener(new RealtimeData.EventAttendeeCountListener() {
                @Override
                public void onCountUpdated(int newCount) {
                    // Update the UI with the new count
                    if (getView() != null) {
                        TextView attendeeCountView = getView().findViewById(R.id.evdash_txt_stat4);
                        attendeeCountView.setText(String.valueOf(newCount));
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_dashboard, container, false);
        populatePage(v);    // Populate the dashboard with event details
        setupAdditionalListeners(v);    // Set up listeners for interactive elements
        addButtonListeners(getActivity(), v);
        if (event != null) {
            attendeeCounter.startListening(event.getId());
        }
        return v;
    }


    /** This sets up listeners for UI buttons
     *
     * @param v The view to set up the buttons for
     */
    private void setupAdditionalListeners(View v) {
        // Note: Button for map feature is not implemented yet as map page does not exist yet
        v.findViewById(R.id.evdash_btn_qrcode).setOnClickListener(view -> switchToFragment(new PromotionQR()));
        v.findViewById(R.id.evdash_btn_checkin).setOnClickListener(view -> switchToFragment(CheckInQR.newInstance(event)));
        v.findViewById(R.id.evdash_img_stat4).setOnClickListener(view -> {
            String eventId = event.getId();
            switchToFragment(CheckedInAttendeeList.newInstance(eventId));
        });

        v.findViewById(R.id.evdash_txt_stat4).setOnClickListener(view -> {
            String eventId = event.getId();
            switchToFragment(CheckedInAttendeeList.newInstance(eventId));
        });
    }

    /** Switches to a new fragment
     *
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

    /** Fills in the event details into the respective views
     *
     * @param v The view which is being populated
     */
    private void populatePage(View v) {
        // Bind event data to UI elements
        ((TextView) v.findViewById(R.id.evdash_text_name)).setText(event.getName());
        ((ImageView) v.findViewById(R.id.evdash_img_poster)).setImageResource(R.drawable.ic_launcher_background); // Placeholder, needs replacement.
        ((TextView) v.findViewById(R.id.evdash_txt_stat1)).setText(event.getLocation());
        ((TextView) v.findViewById(R.id.evdash_txt_stat2)).setText(event.getStart());

    }
}