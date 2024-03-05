package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class AttendeeHome extends AttendeeFragment {

    private ArrayList<Announcement> announcements;


    public AttendeeHome() {}

    public static AttendeeHome newInstance(String param1, String param2) {
        AttendeeHome fragment = new AttendeeHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attendee_home,container,false);
        addNavBarListeners(getActivity(), v);

        announcements = new ArrayList<>();
        RecyclerView milestonesRecyclerView = view.findViewById(R.id.ann);

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

        return v;
    }
}