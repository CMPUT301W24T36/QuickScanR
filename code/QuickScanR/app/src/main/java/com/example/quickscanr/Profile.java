/**
 * the profile page for all user types
 */

package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the page for the profile of the user
 * @see User
 */
public class Profile extends Fragment {

    private static final String USERKEY = "user";

    private User user;

    /**
     * Constructor
     */
    public Profile() {}

    /**
     * Called when creating a new instance of Profile
     * @param user
     * @return Profile fragment
     */
    public static Profile newInstance(User user) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putSerializable(USERKEY, user);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when creating the Profile fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USERKEY);
        }
    }

    /**
     * Responsible for creating the view for Profile fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View for Profile fragment
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (user.getUserType() == UserType.ATTENDEE) {
            view = inflater.inflate(R.layout.attendee_profile, container, false);
            try {
                AttendeeFragment.class.newInstance().addNavBarListeners(getActivity(), view);
                AttendeeFragment.setNavActive(view, 3);
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
                OrganizerFragment.setNavActive(view, 3);
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
                AdminFragment.setNavActive(view, 3);
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

    /**
     * setting the listeners for the fragment
     * @param v
     */
    private void setListeners(View v) {
        ImageButton editProfileBtn = v.findViewById(R.id.user_edit_profile);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.content_main, EditProfile.newInstance(MainActivity.user), "EDIT_PROFILE")
                        .addToBackStack(null).commit();
            }
        });
        Switch geoLocSwitch = v.findViewById(R.id.geo_location);
        geoLocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.user.setGeoLoc(isChecked);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersRef = db.collection(DatabaseConstants.usersColName);
                DocumentReference userDocRef = usersRef.document(MainActivity.user.getUserId());
                Map<String, Object> data = new HashMap<>();
                data.put(DatabaseConstants.userGeoLocKey, isChecked);
                userDocRef.update(data);
            }
        });
    }

    /**
     * sets the relevant information for the Profile fragment
     * @param v
     */
    private void populatePage(View v) {
        TextView nameField = v.findViewById(R.id.name);
        TextView homepageField = v.findViewById(R.id.profile_homepage);
        TextView numberField = v.findViewById(R.id.number);
        TextView emailField = v.findViewById(R.id.email);
        Switch geoLocSwitch = v.findViewById(R.id.geo_location);

        nameField.setText(user.getName());
        homepageField.setText(user.getHomepage());
        numberField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());
        geoLocSwitch.setChecked(MainActivity.user.getGeoLoc());

        ImageView profileImageView = v.findViewById(R.id.profile_pic);
        Context context = v.getContext();

        ProfileImage profileImage = new ProfileImage(context);
        profileImage.getProfileImage(context, user.getUserId(), new ProfileImage.ProfileImageCallback() {
            @Override
            public void onImageReady(Bitmap image) {
                profileImageView.setImageBitmap(image);
            }
        });

        if (MainActivity.user.getGeoLoc()) {
            geoLocSwitch.setChecked(MainActivity.user.getGeoLoc());
        }
    }

}

