package com.example.quickscanr;

import android.media.Image;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class AttendeeFragment extends Fragment {
    public void addNavBarListeners(FragmentActivity activity, View v) {
        ImageButton homeBtn = v.findViewById(R.id.nav_a_announcements_btn);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AttendeeHome())
                        .addToBackStack(null).commit();
            }
        });
        ImageButton eventsListBtn = v.findViewById(R.id.nav_a_events_btn);
        eventsListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AttendeeEventList())
                        .addToBackStack(null).commit();
            }
        });
        ImageButton cameraBtn = v.findViewById(R.id.nav_camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new ScanQR())
                        .addToBackStack(null).commit();
            }
        });
    }
}
