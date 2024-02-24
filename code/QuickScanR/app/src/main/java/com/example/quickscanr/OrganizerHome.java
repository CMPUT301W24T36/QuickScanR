package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OrganizerHome extends OrganizerFragment {

    public OrganizerHome() {}

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
        View v = inflater.inflate(R.layout.organizer_home, container, false);
        addNavBarListeners(getActivity(), v);
        return v;
    }
}