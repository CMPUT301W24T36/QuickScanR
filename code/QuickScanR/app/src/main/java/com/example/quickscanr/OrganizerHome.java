package com.example.quickscanr;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the home page for the organizer, also deals with the
 * functionality of displaying milestones relevant for the organizer/'s events
 *
 * ISSUE: MISSING IMPLEMENTATION OF THE ORGANIZER ANNOUNCING.
 * ISSUE: let milestone implementation be its own function.
 * @see Milestone
 * @see Announcement
 */

public class OrganizerHome extends OrganizerFragment {


    private ArrayList<Milestone> milestoneList = new ArrayList<>();
    private MilestoneAdapter milestoneAdapter;
    private EditText announcement;

    private RealtimeData realtimeData;
    private String userId;


    /**
     * Constructor
     */
    public OrganizerHome() {
    }

    /**
     * Called when creating a new instance of the OrganizerHome
     * @param param1
     * @param param2
     * @return OrganizerHome fragment
     */
    public static OrganizerHome newInstance(String param1, String param2) {
        return new OrganizerHome();
    }

    /**
     * Called when the fragment is created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            this.userId = mainActivity.user.getUserId();
        }
        setupRealtimeData();
    }


    private void setupRealtimeData() {
        realtimeData = new RealtimeData();

        realtimeData.setEventCountListener(new RealtimeData.EventCountListener() {
            @Override
            public void onEventCountUpdated(int eventCount) {
                Log.d("OrganizerHome", "Event count updated: " + eventCount);
                getActivity().runOnUiThread(() -> addEventMilestones(eventCount));
            }
        });

        realtimeData.setEventListener(new RealtimeData.EventAttendeeCountListener() {
            @Override
            public void onTotalCountUpdated(int totalAttendeeCount) {
                Log.d("OrganizerHome", "Total attendee count updated: " + totalAttendeeCount);
                getActivity().runOnUiThread(() -> addCheckInMilestones(totalAttendeeCount));
            }
        });

        realtimeData.startListeningForEventCount(userId);
    }

    /**
     * Creates the view for OrganizerHome fragment, deals with the functionality
     * for displaying the list of milestones.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_home, container, false);
        addNavBarListeners(getActivity(), view);

        milestoneList = new ArrayList<>();
        RecyclerView milestonesRecyclerView = view.findViewById(R.id.milestones_recycler_view);


        // Create the MilestoneAdapter and set it to the RecyclerView
        milestoneAdapter = new MilestoneAdapter(milestoneList);
        milestonesRecyclerView.setAdapter(milestoneAdapter);

        // Later added -> Earlier shown
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        milestonesRecyclerView.setLayoutManager(layoutManager);

        return view;

    }

    private void addCheckInMilestones(int totalAttendeeCount) {

        if (totalAttendeeCount == 1) {
            milestoneList.add(new Milestone("Iron Check-in", "Congratulations! You got your first check in!"));
            Log.d("Debug Milestone", "Added to milestone.");
        } else if (totalAttendeeCount < 11) {
            milestoneList.add(new Milestone("Bronze Check-in", "Congratulations! You've achieved 10 check-ins."));
        } else if (totalAttendeeCount < 51) {
            milestoneList.add(new Milestone("Silver Check-in", "Wow! You've achieved 50 check-ins."));
        } else if (totalAttendeeCount < 101) {
            milestoneList.add(new Milestone("Gold Check-in", "Nice! You've achieved 100 check-ins."));
        } else if (totalAttendeeCount < 501) {
            milestoneList.add(new Milestone("Platinum Check-in", "Incredible! You've achieved 500 check-ins."));
        } else if (totalAttendeeCount < 1001) {
            milestoneList.add(new Milestone("Diamond Check-in", "Impressive! You've achieved 1000 check-ins."));
        } else if (totalAttendeeCount < 10001) {
            milestoneList.add(new Milestone("Emerald Check-in", "You've hit the epic milestone of 10,000 event check-ins! Your passion for events is truly extraordinary."));
        }
        milestoneAdapter.notifyDataSetChanged();
    }


    private void addEventMilestones(int eventCount) {
        if (eventCount == 1) {
            milestoneList.add(new Milestone("Event Rookie", "Way to go! You made your first event."));
        } else if (eventCount < 6) {
            milestoneList.add(new Milestone("Event Enthusiast", "You've hosted 5 events. Keep the momentum going!"));
        } else if (eventCount < 21) {
            milestoneList.add(new Milestone("Event Icon", "Impressive! 20 events hosted. You're a seasoned host."));
        } else if (eventCount < 51) {
            milestoneList.add(new Milestone("Event Superstar", "Congratulations! You've hosted 50 events. Your impact is remarkable."));
        } else if (eventCount < 101) {
            milestoneList.add(new Milestone("Event Legend", "Incredible achievement! You've hosted 100 events. You're a true legend."));
        } else if (eventCount < 1001) {
            milestoneList.add(new Milestone("Event Gatsby", "Unbelievable! You have reached the Gatsby-worthy milestone of hosting 1000 events."));
        }
        milestoneAdapter.notifyDataSetChanged();

    }
}