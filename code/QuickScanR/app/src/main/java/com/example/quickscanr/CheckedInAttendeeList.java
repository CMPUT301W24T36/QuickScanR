package com.example.quickscanr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// NOT YET FUNCTIONAL
public class CheckedInAttendeeList extends InnerPageFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.checked_users_list, container, false);
        addButtonListeners(getActivity(), v);
        return v;
    }
}
