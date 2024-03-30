package com.example.quickscanr;

import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class AdminFragment extends Fragment {
    public void addNavBarListeners(FragmentActivity activity, View v) {
        ImageButton eventListBtn = v.findViewById(R.id.nav_ad_events_btn);
        eventListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AdminEventsList())
                        .addToBackStack(null).commit();
            }
        });
        ImageButton usersListBtn = v.findViewById(R.id.nav_ad_users_btn);
        usersListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AdminProfilesList())
                        .addToBackStack(null).commit();

            }
        });
        ImageButton profileBtn = v.findViewById(R.id.nav_ad_profile_btn);
        profileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, Profile.newInstance(MainActivity.user))
                        .addToBackStack(null).commit();
            }
        });

        ImageButton imgListBtn = v.findViewById(R.id.nav_ad_images_btn);
        imgListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new AdminImageList())
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
        ImageButton announcement = v.findViewById(R.id.nav_ad_events_btn);
        ImageButton events = v.findViewById(R.id.nav_ad_users_btn);
        ImageButton camera = v.findViewById(R.id.nav_ad_images_btn);
        ImageButton profile = v.findViewById(R.id.nav_ad_profile_btn);

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
