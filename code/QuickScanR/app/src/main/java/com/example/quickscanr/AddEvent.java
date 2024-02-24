package com.example.quickscanr;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddEvent extends InnerPageFragment {

    public AddEvent() {}

    public static AddEvent newInstance(String param1, String param2) {
        AddEvent fragment = new AddEvent();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_event, container, false);
        addButtonListeners(getActivity(), v, new OrganizerEventList());
        return v;
    }
}