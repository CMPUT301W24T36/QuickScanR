package com.example.quickscanr;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * This deals with the functionality regarding how the Attendee can navigate (using the navBar)
 * around the application's fragments (specific for the Attendee) relevant to each navBar button.
 * @see AttendeeHome
 * @see AttendeeEventList
 * @see ScanQR
 * @see MainActivity
 */
public class AttendeeFragment extends Fragment {

    /**
     * The functionality of navigating through fragments
     * @param activity
     * @param v The view associated with the fragment
     */
    public void addNavBarListeners(FragmentActivity activity, View v) {
        // Home button functionality
        ImageButton homeBtn = v.findViewById(R.id.nav_a_announcements_btn);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AttendeeHome())
                        .addToBackStack(null).commit();
            }
        });

        // Events button functionality
        ImageButton eventsListBtn = v.findViewById(R.id.nav_a_events_btn);
        eventsListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AttendeeEventList())
                        .addToBackStack(null).commit();
            }
        });

        // Camera button functionality
        ImageButton cameraBtn = v.findViewById(R.id.nav_camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new ScanQR(), "SCANNER")
                        .addToBackStack(null).commit();
            }
        });

        // Profile button functionality
        ImageButton profileBtn = v.findViewById(R.id.nav_a_profile_btn);
        profileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, Profile.newInstance(MainActivity.user))
                        .addToBackStack(null).commit();
            }
        });
    }

    /**
     * Sets the active navigation button in the navbar
     * @param v view from fragment
     * @param pos position (0-3) of button to activate
     */
    public static void setNavActive(View v, int pos) {
        if (pos < 0 || pos > 3) return;  // safety

        // get refs
        ImageButton announcement = v.findViewById(R.id.nav_a_announcements_btn);
        ImageButton events = v.findViewById(R.id.nav_a_events_btn);
        ImageButton camera = v.findViewById(R.id.nav_camera_btn);
        ImageButton profile = v.findViewById(R.id.nav_a_profile_btn);

        // set all to white
        announcement.setBackgroundResource(R.drawable.round_button);
        events.setBackgroundResource(R.drawable.round_button);
        camera.setBackgroundResource(R.drawable.round_button);
        profile.setBackgroundResource(R.drawable.round_button);

        // apply new active
        if (pos == 0) announcement.setBackgroundResource(R.drawable.active_round_button);
        if (pos == 1) events.setBackgroundResource(R.drawable.active_round_button);
        if (pos == 2) camera.setBackgroundResource(R.drawable.active_round_button);
        if (pos == 3) profile.setBackgroundResource(R.drawable.active_round_button);
    }
}
