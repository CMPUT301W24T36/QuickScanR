package com.example.quickscanr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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
    private static final String SHOWCONFDIALOG = "showConfDialog";
    public static final String ATTENDEE_COLLECTION = "attendees";
    public static final String EVENT_COLLECTION = "events";
    private FirebaseFirestore db;
    private Event event;
    private boolean showConfDialog;
    MainActivity mainActivity = (MainActivity) getActivity();
    User user = mainActivity.user;

    public EventDetails() {}

    public static EventDetails newInstance(Event event) {
        EventDetails fragment = new EventDetails();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().size() == 1) {
                event = (Event) getArguments().getSerializable(EVENT);
            } else if (getArguments().size() == 2) {
                event = (Event) getArguments().getSerializable(EVENT);
                showConfDialog = (boolean) getArguments().getSerializable(SHOWCONFDIALOG);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_details, container, false);
        addButtonListeners(getActivity(), v, new AttendeeEventList());
        db = FirebaseFirestore.getInstance();
        populatePage(v);
        if (showConfDialog) {
            showConfDialog();
            showConfDialog = false;
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
        TextView restrictions = v.findViewById(R.id.evdetail_txt_restrictions);
        ImageView hostPic = v.findViewById(R.id.evdetail_img_host);
        ImageView poster = v.findViewById(R.id.evdetail_img_poster);

        host.setText(event.getOrganizer().getName());
        ImgHandler imgHandler = new ImgHandler(getContext());
        imgHandler.getImage(event.getOrganizer().getImageID(), hostPic::setImageBitmap);
        poster.setImageBitmap(event.getPoster());
        location.setText(event.getLocation());
        start.setText(event.getStart());
        end.setText(event.getEnd());
        restrictions.setText(event.getRestrictions());
    }

    /**
     * Shows check in confirmation dialog to the user.
     */
    public void showConfDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Check In")
                .setMessage("Do you want to check in to this event?")
                .setPositiveButton(android.R.string.yes, (dialog, x) -> {
                    String eventID = event.getId();
                    String userID = user.getUserId();
                    Long timestamp = System.currentTimeMillis();
                    DocumentReference attRef = db.collection(EVENT_COLLECTION).document(eventID).collection(ATTENDEE_COLLECTION).document(userID);

                    // db request
                    attRef.get().addOnCompleteListener(doc -> {
                        if (doc.isSuccessful()) {
                            DocumentSnapshot attendees = doc.getResult();
                            if (attendees.exists()) {
                                // user already checked in prior
                                addEventToUser(event);
                                attRef.update("timestamps", FieldValue.arrayUnion(timestamp))
                                        .addOnSuccessListener(aVoid -> Log.d("DEBUG", "Event notified of check-in"))
                                        .addOnFailureListener(e -> Log.w("DEBUG", "Event notified of check-in", e));
                            } else {
                                // first time user check in
                                addEventToUser(event);
                                Map<String, Object> newAttendee = new HashMap<>();
                                newAttendee.put("timestamps", Arrays.asList(timestamp));
                                newAttendee.put("name", user.getName());

                                attRef.set(newAttendee)
                                        .addOnSuccessListener(e -> Log.d("DEBUG", "Event notified of 1st check-in"))
                                        .addOnFailureListener(e -> Log.d("DEBUG", "Error notifying 1st check-in"));
                            }
                        } else {
                            Log.d("DEBUG", "Failed to add event to DB");
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
        alertDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        alertDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
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
}