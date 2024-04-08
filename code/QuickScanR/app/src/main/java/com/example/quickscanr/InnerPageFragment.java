/**
 * specialized fragment for inner pages (pages with back buttons)
 * sets up listener for back button
 */

package com.example.quickscanr;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.button.MaterialButton;

/**
 * Deals with the functionality for the back button
 */
public class InnerPageFragment extends Fragment {
    /**
     * The functionality of the back button
     * @param activity
     * @param v
     */
    public void addButtonListeners(FragmentActivity activity, View v, Fragment backPage) {

        MaterialButton backBtn = v.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pop the current fragment off the stack
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, backPage)
                        .addToBackStack(null).commit();
            }
        });
    }
}