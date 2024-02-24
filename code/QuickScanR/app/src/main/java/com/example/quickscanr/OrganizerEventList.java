package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OrganizerEventList extends OrganizerFragment {

    public OrganizerEventList() {}

    public static OrganizerEventList newInstance(String param1, String param2) {
        OrganizerEventList fragment = new OrganizerEventList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.organizer_event_list, container, false);
        addNavBarListeners(getActivity(), v);
        return v;
    }
}