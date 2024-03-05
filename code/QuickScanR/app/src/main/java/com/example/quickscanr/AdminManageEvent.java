package com.example.quickscanr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

public class AdminManageEvent extends AdminFragment{
    private Event event;

    public AdminManageEvent(Event event) {
        this.event = event;
    }

    //On create we need to inflate the xml and create the fragment that can be connected to the event page
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_manage_event, container, false);
        //fill in the info
        populateInfo(v);
        return v;
    }

    public static AdminManageEvent newInstance(Event event) {
        AdminManageEvent fragment = new AdminManageEvent(event);
        Bundle args = new Bundle();

        args.putSerializable("event", event);
        fragment.setArguments(args);

        return fragment;
    }

    public void populateInfo(View v){
        TextInputEditText name = v.findViewById(R.id.manage_name);
        TextInputEditText descrption = v.findViewById(R.id.manage_desc);
        TextInputEditText location = v.findViewById(R.id.manage_loc);
        TextInputEditText starts = v.findViewById(R.id.manage_start);
        TextInputEditText ends = v.findViewById(R.id.manage_end);
        TextInputEditText guest_rst = v.findViewById(R.id.manage_restrictions);


        name.setText(event.getName());
        descrption.setText(event.getDescription());
        location.setText(event.getLocation());
        starts.setText(event.getStart());
        ends.setText(event.getEnd());
        guest_rst.setText(event.getRestrictions());

    }


}
