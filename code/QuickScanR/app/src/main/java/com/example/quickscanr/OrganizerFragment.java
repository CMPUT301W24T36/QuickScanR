package com.example.quickscanr;

import android.media.Image;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class OrganizerFragment extends Fragment {
    public void addNavBarListeners(FragmentActivity activity, View v) {
        ImageButton homeBtn = v.findViewById(R.id.nav_o_announcements_btn);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new OrganizerHome())
                        .addToBackStack(null).commit();
            }
        });
        ImageButton eventListBtn = v.findViewById(R.id.nav_o_events_btn);
        eventListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new OrganizerEventList())
                        .addToBackStack(null).commit();
            }
        });
        ImageButton addEventsBtn = v.findViewById(R.id.nav_o_add_event_btn);
        addEventsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AddEvent())
                        .addToBackStack(null).commit();
            }
        });
    }
}
