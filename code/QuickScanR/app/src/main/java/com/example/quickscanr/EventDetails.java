/**
 * event details page for attendees
 * view event details
 * also landing page for scanning qr codes
 * also allows for sign up if you haven't already
 */

package com.example.quickscanr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for the Event Details page. Handles population of
 * page components when passed an event. Also can display a
 * confirmation dialog to check an attendee into an event if
 * provided with the argument.
 */
public class EventDetails extends InnerPageFragment {

    private static final String EVENT = "event";
    private static final String SHOWCHECKINDIALOG = "showCheckInDialog";
    private static final String SHOWSIGNUPDIALOG = "showSignUpDialog";
    public static final String ATTENDEE_COLLECTION = "attendees";
    public static final String EVENT_COLLECTION = "events";
    private FirebaseFirestore db;
    private Event event;
    private boolean showCheckInDialog;
    private boolean showSignUpDialog;
    MainActivity mainActivity = (MainActivity) getActivity();
    User user = mainActivity.user;
    private LocationHelper locationHelper;

    /**
     * empty constructor (required)
     */
    public EventDetails() {}

    /**
     * get event details fragment
     * @param event event that the fragment is for
     * @return event details fragment
     */
    public static EventDetails newInstance(Event event) {
        EventDetails fragment = new EventDetails();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * valled when fragment is created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().size() == 1) {
                event = (Event) getArguments().getSerializable(EVENT);
            } else if (getArguments().size() == 2) {
                event = (Event) getArguments().getSerializable(EVENT);
                try {
                    showCheckInDialog = (boolean) getArguments().getSerializable(SHOWCHECKINDIALOG);
                    showSignUpDialog = false;
                }
                catch (Exception e) {
                    showSignUpDialog = (boolean) getArguments().getBoolean(SHOWSIGNUPDIALOG);
                    showCheckInDialog = false;
                }
            }
        }
        locationHelper = new LocationHelper(getActivity());
        locationHelper.startLocationUpdates();
    }

    /**
     * called when fragment is destroyed. stop the location updates.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop receiving unnecessary location updates
        locationHelper.stopLocationUpdates();
    }

    /**
     * called when the view is created
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the event details view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_details, container, false);
        addButtonListeners(getActivity(), v, new AttendeeEventList());
        db = FirebaseFirestore.getInstance();
        populatePage(v);
        Button signUpBtn = v.findViewById(R.id.ev_det_signup_btn);
        signUpBtn.setVisibility(View.INVISIBLE);
        if (showCheckInDialog) {
            showCheckInDialog();
            showCheckInDialog = false;
        }
        if (showSignUpDialog) {
            showSignUp(v);
        }
        return v;
    }

    /**
     * Populates the page with the given event data
     * @param v View required to find IDs
     */
    private void populatePage(View v) {
        TextView host = v.findViewById(R.id.evdetail_txt_host);
        TextView location = v.findViewById(R.id.evdetail_txt_loc);
        TextView start = v.findViewById(R.id.evdetail_txt_start);
        TextView end = v.findViewById(R.id.evdetail_txt_end);
        ImageView hostPic = v.findViewById(R.id.evdetail_img_host);
        ImageView poster = v.findViewById(R.id.evdetail_img_poster);

        host.setText(event.getOrganizer().getName());
        poster.setImageBitmap(event.getPoster());
        location.setText(event.getLocationName());
        start.setText(event.getStart());
        end.setText(event.getEnd());

        // add profile pic
        ProfileImage profileImage = new ProfileImage(getContext());
        profileImage.getProfileImage(getContext(), event.getOrganizer().getUserId(), new ProfileImage.ProfileImageCallback() {
            @Override
            public void onImageReady(Bitmap image) {
                hostPic.setImageBitmap(image);
            }
        });
    }

    /**
     * Shows check in confirmation dialog to the user.
     */
    public void showCheckInDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Check In")
                .setMessage("Do you want to check in to this event?")
                .setPositiveButton(android.R.string.yes, (dialog, x) -> {
                    String eventID = event.getId();
                    String userID = user.getUserId();
                    Long timestamp = System.currentTimeMillis();
                    DocumentReference attRef = db.collection(EVENT_COLLECTION).document(eventID).collection(ATTENDEE_COLLECTION).document(userID);

                    boolean isGeoLocOn = user.getGeoLoc();
                    if (isGeoLocOn) {
                        Location currentLocation = locationHelper.getCurrentLocation();
                        if (currentLocation != null) {
                            Log.d("DEBUG", "Updating with location");
                            updateAttendeeWithLocation(attRef, timestamp, currentLocation);
                        }
                    } else {
                        Log.d("DEBUG", "Updating without location");
                        updateAttendeeWithoutLocation(attRef, timestamp);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
        alertDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
    }

    /**
     * allows user to sign up for event (makes sign up button visible)
     */
    private void showSignUp(View v) {
        Button signUpBtn = v.findViewById(R.id.ev_det_signup_btn);
        signUpBtn.setVisibility(View.VISIBLE);
        if (event.isAtCapacity()) {
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Event is full!")
                            .setNegativeButton(android.R.string.ok, null)
                            .show();
                    alertDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                }
            });
        } else {
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle("Sign Up")
                            .setMessage("Do you want to sign up for this event?")
                            .setPositiveButton(android.R.string.yes, (dialog, x) -> {
                                String eventID = event.getId();
                                String userID = user.getUserId();
                                DocumentReference eventRef = db.collection(EVENT_COLLECTION).document(eventID);
                                eventRef.update(DatabaseConstants.evSignedUpUsersKey, FieldValue.arrayUnion(userID));
                                addSignedUpEvToUser(event);
                                getParentFragmentManager().beginTransaction().replace(R.id.content_main, new AttendeeEventList())
                                        .addToBackStack(null).commit();
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                    alertDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                }
            });
        }
    }

    /**
     * Adds an event to a user's checkedEvents database field
     * @param e event object to be added
     */
    private void addEventToUser(Event e) {
        DocumentReference ref = db.collection("users").document(user.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put(DatabaseConstants.userCheckedEventsKey, FieldValue.arrayUnion(e.getId()));
        ref.update(map);
        Log.d("DEBUG","Added event to user checkedEvents");
    }

    /**
     * Adds an event to a user's signedUp database field
     * @param e event object to be added
     */
    private void addSignedUpEvToUser(Event e) {
        DocumentReference ref = db.collection(DatabaseConstants.usersColName).document(user.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put(DatabaseConstants.userSignedUpEventsKey, FieldValue.arrayUnion(e.getId()));
        ref.update(map);
        Log.d("DEBUG","Added event to user signedUp");
    }

    /**
     * add or update the attendee in the attendees collection
     * @param attRef attendee reference in the attendee collection
     * @param timestamp timestamp that they checked in at
     * @param currentLocation location that they're checking in from
     */
    private void updateAttendeeWithLocation(DocumentReference attRef, Long timestamp, Location currentLocation) {
        attRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("DEBUG", "Updating existing attendee (with location)");
                    // Update existing entry
                    attRef.update("timestamps", FieldValue.arrayUnion(timestamp),
                            "name", MainActivity.user.getName(),
                            "latitude", currentLocation.getLatitude(),
                            "longitude", currentLocation.getLongitude(),
                            "geoLoc", true);
                } else {
                    Log.d("DEBUG", "Creating new attendee doc (with location)");
                    // Create a new entry
                    Map<String, Object> attendeeData = new HashMap<>();
                    attendeeData.put("timestamps", Arrays.asList(timestamp));
                    attendeeData.put("name", MainActivity.user.getName());
                    attendeeData.put("latitude", currentLocation.getLatitude());
                    attendeeData.put("longitude", currentLocation.getLongitude());
                    attendeeData.put("geoLoc", true);
                    attRef.set(attendeeData);
                    addEventToUser(event);
                }
            }
        });
    }

    /**
     * add or update the attendee in the attendees collection
     * @param attRef attendee reference in the attendee collection
     * @param timestamp timestamp that they checked in at
     */
    private void updateAttendeeWithoutLocation(DocumentReference attRef, Long timestamp) {
        attRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("DEBUG", "Updating existing attendee (without location)");
                    // Update existing entry
                    attRef.update("timestamps", FieldValue.arrayUnion(timestamp),
                            "name", MainActivity.user.getName(),
                            "geoLoc", false);
                } else {
                    Log.d("DEBUG", "Creating new attendee doc (without location)");
                    // Create a entry
                    Map<String, Object> attendeeData = new HashMap<>();
                    attendeeData.put("timestamps", Arrays.asList(timestamp));
                    attendeeData.put("geoLoc", false);
                    attendeeData.put("name", MainActivity.user.getName());
                    attRef.set(attendeeData);
                    addEventToUser(event);
                }
            }
        });
    }
}