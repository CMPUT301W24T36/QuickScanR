package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class AttendeeEventList extends AttendeeFragment {

    public AttendeeEventList() {}

    public static AttendeeEventList newInstance(String param1, String param2) {
        AttendeeEventList fragment = new AttendeeEventList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attendee_event_list, container, false);
        addNavBarListeners(getActivity(), v);
        return v;
    }
}