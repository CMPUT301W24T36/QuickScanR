package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Profile extends Fragment {

    private static final String USERKEY = "user";

    private User user;

    public Profile() {}

    public static Profile newInstance(User user) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putSerializable(USERKEY, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USERKEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (user.getUserType() == UserType.ATTENDEE) {
            view = inflater.inflate(R.layout.attendee_profile, container, false);
            try {
                AttendeeFragment.class.newInstance().addNavBarListeners(getActivity(), view);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (java.lang.InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        else if (user.getUserType() == UserType.ORGANIZER) {
            view = inflater.inflate(R.layout.organizer_profile, container, false);
            try {
                OrganizerFragment.class.newInstance().addNavBarListeners(getActivity(), view);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (java.lang.InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            view = inflater.inflate(R.layout.admin_profile, container, false);
            try {
                AdminFragment.class.newInstance().addNavBarListeners(getActivity(), view);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (java.lang.InstantiationException e) {
                throw new RuntimeException(e);
            }
            LinearLayout geolocSection = view.findViewById(R.id.geoloc_section);
            geolocSection.setVisibility(View.INVISIBLE);
        }
        populatePage(view);
        setListeners(view);
        return view;
    }

    private void setListeners(View v) {
        ImageButton editProfileBtn = v.findViewById(R.id.user_edit_profile);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.content_main, EditProfile.newInstance(MainActivity.user))
                        .addToBackStack(null).commit();
            }
        });
    }

    private void populatePage(View v) {
        TextView nameField = v.findViewById(R.id.name);
        TextView homepageField = v.findViewById(R.id.profile_homepage);
        TextView numberField = v.findViewById(R.id.number);
        TextView emailField = v.findViewById(R.id.email);
        nameField.setText(user.getName());
        homepageField.setText(user.getHomepage());
        numberField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());
    }
}