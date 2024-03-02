package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class OrganizerHome extends OrganizerFragment {


    private RecyclerView milestonesRecyclerView;
    private List<Milestone> milestoneList;


    // Database-fetched values
    int checkInsCount;
    int eventsCount;

    public OrganizerHome() {
    }

    public static OrganizerHome newInstance(String param1, String param2) {
        OrganizerHome fragment = new OrganizerHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_home, container, false);
        addNavBarListeners(getActivity(), view);

        // Initialize view-related variables: the list of milestones!

        milestoneList = new ArrayList<>();

        // Find the views

        milestonesRecyclerView = view.findViewById(R.id.milestones_recycler_view);

        // Get the relevant user information to show their milestones!
        checkInsCount = 0;
        eventsCount = 0;
        fetchValuesFromDatabase(eventsCount, checkInsCount);

        // Update the milestones.
        addCheckInMilestones();
        addEventMilestones();
        // -- More milestone types can be added here.

        // Create the MilestoneAdapter and set it to the RecyclerView
        MilestoneAdapter milestoneAdapter = new MilestoneAdapter(milestoneList);
        milestonesRecyclerView.setAdapter(milestoneAdapter);
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
     */
    private void addCheckInMilestones() {

        if (checkInsCount == 1) {
            milestoneList.add(new Milestone("Iron Check-in", "Congratulations! You got your first check in!"));
        } else if (checkInsCount >= 10) {
            milestoneList.add(new Milestone("Bronze Check-in", "Congratulations! You've achieved 10 check-ins."));
        } else if (checkInsCount >= 50) {
            milestoneList.add(new Milestone("Silver Check-in", "Wow! You've achieved 50 check-ins."));
        } else if (checkInsCount >= 100) {
            milestoneList.add(new Milestone("Gold Check-in", "Nice! You've achieved 100 check-ins."));
        } else if (checkInsCount >= 500) {
            milestoneList.add(new Milestone("Platinum Check-in", "Incredible! You've achieved 500 check-ins."));
        } else if (checkInsCount >= 1000) {
            milestoneList.add(new Milestone("Diamond Check-in", "Impressive! You've achieved 1000 check-ins."));
        } else if (checkInsCount >= 10000) {
            milestoneList.add(new Milestone("Emerald Check-in", "You've hit the epic milestone of 10,000 event check-ins! Your passion for our events is truly extraordinary."));
        }
    }

    /**
     * Prompts Event  Milestones to check to add to the list!
     */
    private void addEventMilestones() {
        if (eventsCount == 1) {
            milestoneList.add(new Milestone("Event Rookie", "Way to go! You made your first event."));
        } else if (eventsCount >= 5) {
            milestoneList.add(new Milestone("Event Enthusiast", "You've hosted 5 events. Keep the momentum going!"));
        } else if (eventsCount >= 20) {
            milestoneList.add(new Milestone("Event Icon", "Impressive! 20 events hosted. You're a seasoned host."));
        } else if (eventsCount >= 50) {
            milestoneList.add(new Milestone("Event Superstar", "Congratulations! You've hosted 50 events. Your impact is remarkable."));
        } else if (eventsCount >= 100) {
            milestoneList.add(new Milestone("Event Legend", "Incredible achievement! You've hosted 100 events. You're a true legend."));
        } else if (eventsCount >= 1000) {
            milestoneList.add(new Milestone("Event Gatsby", "Unbelievable! You have reached the Gatsby-worthy milestone of hosting 1000 events."));
        }

    }
}