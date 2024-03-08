package com.example.quickscanr;

import android.media.Image;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Fragment for Organizers, deals with the functionality of the navBar
 * to ensure Organizer can navigate through each relevant fragment FOR the organizer using the navBar
 */
public class OrganizerFragment extends Fragment {
    /**
     * The functionality for the navBar
     * @param activity
     * @param v
     */
    public void addNavBarListeners(FragmentActivity activity, View v) {

        // Home button
        ImageButton homeBtn = v.findViewById(R.id.nav_o_announcements_btn);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new OrganizerHome())
                        .addToBackStack(null).commit();
            }
        });

        // Event button
        ImageButton eventListBtn = v.findViewById(R.id.nav_o_events_btn);
        eventListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new OrganizerEventList())
                        .addToBackStack(null).commit();
            }
        });
        // Add events button
        ImageButton addEventsBtn = v.findViewById(R.id.nav_o_add_event_btn);
        addEventsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AddEvent())
                        .addToBackStack(null).commit();
            }
        });

        // Profile button
        ImageButton profileBtn = v.findViewById(R.id.nav_o_profile_btn);
        profileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, Profile.newInstance(MainActivity.user))
                        .addToBackStack(null).commit();
            }
        });
    }
}
