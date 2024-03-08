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

import java.util.ArrayList;
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


    private ArrayList<Milestone> milestoneList;
    private EditText announcement;


    // Database-fetched values
    int checkInsCount;
    int eventsCount;

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
        OrganizerHome fragment = new OrganizerHome();
        return fragment;
    }

    /**
     * Called when the fragment is created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Get the relevant user information to show their milestones! TO BE IMPLEMENTED
//        checkInsCount = 0;
//        eventsCount = 0;
//        fetchValuesFromDatabase(eventsCount, checkInsCount);


        // Create the MilestoneAdapter and set it to the RecyclerView
        MilestoneAdapter milestoneAdapter = new MilestoneAdapter(milestoneList);
        milestonesRecyclerView.setAdapter(milestoneAdapter);

        // Later added -> Earlier shown
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        milestonesRecyclerView.setLayoutManager(layoutManager);

        // SAMPLE: Since not connected to DB
        checkInsCount = 1;
        addCheckInMilestones(checkInsCount);
        checkInsCount = 10;
        addCheckInMilestones(checkInsCount);
        checkInsCount = 1000;
        addCheckInMilestones(checkInsCount);

        eventsCount = 1;
        addEventMilestones(eventsCount);
        eventsCount = 1000;
        addEventMilestones(eventsCount);
        return view;

    }

    /**
     * This is a temporary function, as actual implementation may result in
     * these two parameters being in separate functions. This is to show future
     * database implementation that will modify these values accordingly
     *
     * @param eventsCount
     * @param checkInsCount
     */

    private void fetchValuesFromDatabase(int eventsCount, int checkInsCount) {
        // To be implemented.
    }

    /**
     * Prompts Check In Milestones to check to add to the list!
     * @param checkInsCount
     */
    private void addCheckInMilestones(int checkInsCount) {

        if (checkInsCount == 1) {
            milestoneList.add(new Milestone("Iron Check-in", "Congratulations! You got your first check in!"));
            Log.d("Debug Milestone", "Added to milestone.");
        } else if (checkInsCount < 11) {
            milestoneList.add(new Milestone("Bronze Check-in", "Congratulations! You've achieved 10 check-ins."));
        } else if (checkInsCount < 51) {
            milestoneList.add(new Milestone("Silver Check-in", "Wow! You've achieved 50 check-ins."));
        } else if (checkInsCount < 101) {
            milestoneList.add(new Milestone("Gold Check-in", "Nice! You've achieved 100 check-ins."));
        } else if (checkInsCount < 501) {
            milestoneList.add(new Milestone("Platinum Check-in", "Incredible! You've achieved 500 check-ins."));
        } else if (checkInsCount < 1001) {
            milestoneList.add(new Milestone("Diamond Check-in", "Impressive! You've achieved 1000 check-ins."));
        } else if (checkInsCount < 10001) {
            milestoneList.add(new Milestone("Emerald Check-in", "You've hit the epic milestone of 10,000 event check-ins! Your passion for events is truly extraordinary."));
        }
    }

    /**
     * Prompts Event  Milestones to check to add to the list!
     * @param eventsCount
     */
    private void addEventMilestones(int eventsCount) {
        if (eventsCount == 1) {
            milestoneList.add(new Milestone("Event Rookie", "Way to go! You made your first event."));
        } else if (eventsCount < 6) {
            milestoneList.add(new Milestone("Event Enthusiast", "You've hosted 5 events. Keep the momentum going!"));
        } else if (eventsCount < 21) {
            milestoneList.add(new Milestone("Event Icon", "Impressive! 20 events hosted. You're a seasoned host."));
        } else if (eventsCount < 51) {
            milestoneList.add(new Milestone("Event Superstar", "Congratulations! You've hosted 50 events. Your impact is remarkable."));
        } else if (eventsCount < 101) {
            milestoneList.add(new Milestone("Event Legend", "Incredible achievement! You've hosted 100 events. You're a true legend."));
        } else if (eventsCount < 1001) {
            milestoneList.add(new Milestone("Event Gatsby", "Unbelievable! You have reached the Gatsby-worthy milestone of hosting 1000 events."));
        }

    }
}