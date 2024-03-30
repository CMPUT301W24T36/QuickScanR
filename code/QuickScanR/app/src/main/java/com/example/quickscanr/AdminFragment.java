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
}
