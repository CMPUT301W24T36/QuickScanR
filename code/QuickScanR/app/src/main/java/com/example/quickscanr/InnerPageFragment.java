package com.example.quickscanr;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.button.MaterialButton;

public class InnerPageFragment extends Fragment {
    public void addButtonListeners(FragmentActivity activity, View v) {
        MaterialButton backBtn = v.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                activity.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_main, new OrganizerEventList())
                        .addToBackStack(null).commit();
            }
        });
    }
}
