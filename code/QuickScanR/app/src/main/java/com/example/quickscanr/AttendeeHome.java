package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class AttendeeHome extends AttendeeFragment {

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
        return v;
    }
}