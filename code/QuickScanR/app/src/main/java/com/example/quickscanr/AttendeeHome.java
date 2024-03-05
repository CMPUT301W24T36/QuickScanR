package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Date;

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
        View view = inflater.inflate(R.layout.attendee_home,container,false);
        addNavBarListeners(getActivity(), view);

        announcements = new ArrayList<>();
        RecyclerView announcementsRecyclerView = view.findViewById(R.id.announcements_recycler_view);

        // Create the MilestoneAdapter and set it to the RecyclerView
        AnnouncementAdapter announcementAdapter = new AnnouncementAdapter(announcements);
        announcementsRecyclerView.setAdapter(announcementAdapter);
        announcementsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // SAMPLE: Since not connected to DB yet
        Date date = new Date();
        Announcement a1 = new Announcement("Title1", "I am the body of this announcement.", date,123 );
        Announcement a2 = new Announcement("Title2", "I am the body of this announcement.", date,456);
        Announcement a3 = new Announcement("Title3", "I am the body of this announcement", date,789 );

        announcements.add(a1);
        announcements.add(a2);
        announcements.add(a3);


        return view;
    }
}