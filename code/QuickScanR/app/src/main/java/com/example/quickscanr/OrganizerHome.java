package com.example.quickscanr;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the home page for the organizer, also deals with the
 * functionality of displaying milestones relevant for the organizer/'s events

 * @see Milestone
 * @see Announcement
 */

public class OrganizerHome extends OrganizerFragment implements AddAnnouncementFragment.AddAnnounceDialogListener{


    private ArrayList<Milestone> milestoneList = new ArrayList<>();
    private MilestoneAdapter milestoneAdapter;
    private EditText announcement;

    private RealtimeData realtimeData;
    private String userId;
    private String userName;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference anncRef;
    private List<String> eventIds = new ArrayList<>();
    private ImageView profPic;

    int lastEventCount; // to help with the ranges; may be temporary
    int lastAttendeeCount; // to help with the ranges; may be temporary

    private boolean hasFirstAttendeeCheck;
    private boolean hasSecondAttendeeCheck;
    private boolean hasThirdAttendeeCheck;
    private boolean hasFourthAttendeeCheck;
    private boolean hasFifthAttendeeCheck;

    private boolean hasFirstEventCheck = false;
    private boolean hasSecondEventCheck = false;
    private boolean hasThirdEventCheck = false;
    private boolean hasFourthEventCheck = false;
    private boolean hasFifthEventCheck = false;
    /**
     * Constructor
     */
    public OrganizerHome() {
    }

    // Interface methods

    /**
     * adds the announcement to the database
     *
     * @param announcement to announce by the organizer.
     */
    @Override
    public void addAnnouncement(Announcement announcement) {


        // Add to the database
        anncRef = db.collection("announcements"); // Get relevant database path

        // Set up the data

        HashMap<String, String> data = new HashMap<>();
        data.put("title", announcement.getTitle());
        data.put("body", announcement.getBody());
        data.put("date", announcement.getDate());
        data.put("userName", announcement.getUserName());
        data.put("eventId", announcement.getEventID());
        data.put(DatabaseConstants.anUserKey, userId);

        anncRef.document() // can be changed
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DEBUG", "Announcement added successfully");
                    }
                });
    }


    /**
     * This allows dialog fragment and organizer home to talk to each other.
     * TODO: get rid of this, change announcement_trigger to a button instead.
     */
    @Override
    public void inDismiss() {
        announcement = getView().findViewById(R.id.announcement_trigger);
        announcement.clearFocus();
    }

    /**
     * Called when creating a new instance of the OrganizerHome
     * @param param1
     * @param param2
     * @return OrganizerHome fragment
     */
    public static OrganizerHome newInstance(String param1, String param2) {
        return new OrganizerHome();
    }

    /**
     * Called when the fragment is created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            this.userId = mainActivity.user.getUserId();
            this.userName = mainActivity.user.getName();
        }
        getOrganizerEventIds();
        startListeningForEventCount();



    }

    /**
     * Counts the NUMBER OF EVENTS + has the functionality of letting the milestones know of the event count
     */
    private void startListeningForEventCount() { // Functional
        // Listen for real-time updates on the event count based on the organizer's userId
        db.collection("events")
                .whereEqualTo("ownerID", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("OrganizerHome", "Listen for event count failed.", e);
                            return;
                        }
                        if (snapshots != null) {
                            int eventCount = snapshots.size();
                            // Let the milestone know about the event number
                            addEventMilestones(eventCount);
                        }
                    }
                });
    }

    /**
     * Counts the NUMBER OF ATTENDEES + has the functionality of letting milestones know the amount of events.
     * @param eventIds the relevant event
     */
    private void startListeningForAttendeeCount(List<String> eventIds) {
        for (String eventId : eventIds) {

            // reset check for every event
            hasFirstAttendeeCheck = false;
            hasSecondAttendeeCheck = false;
            hasThirdAttendeeCheck = false;
            hasFourthAttendeeCheck = false;
            hasFifthAttendeeCheck = false;

            db.collection("events").document(eventId).collection("attendees")

                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("OrganizerHome", "Listen for attendees failed.", e);
                                return;
                            }
                            if (snapshots != null) {
                                int totalAttendeeCount = snapshots.size();
                                // Call Milestones to update the UI!

                                addCheckInMilestones(totalAttendeeCount);
                            }
                        }
                    });
        }
    }

    /**
     * gets the organizer event ids
     */
    private void getOrganizerEventIds() {
        db.collection("events")
                .whereEqualTo("ownerID", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            eventIds.clear(); // Clear the list to avoid duplicates
                            for (DocumentSnapshot document : task.getResult()) {
                                eventIds.add(document.getId()); // Add the event ID to the list
                            }
                            // Now that you have the event IDs, you can start listening for attendees
                            startListeningForAttendeeCount(eventIds);
                        } else {
                            Log.d("OrganizerHome", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    /**
     * sets up the real time data
     */
    private void setupRealtimeData() {
        realtimeData = new RealtimeData();

        realtimeData.setEventCountListener(new RealtimeData.EventCountListener() {
            @Override
            public void onEventCountUpdated(int eventCount) {
                Log.d("OrganizerHome", "Event count updated: " + eventCount);
                getActivity().runOnUiThread(() -> addEventMilestones(eventCount));
            }
        });


        realtimeData.setEventListener(new RealtimeData.EventAttendeeCountListener() {
            @Override
            public void onTotalCountUpdated(int totalAttendeeCount) {
                Log.d("OrganizerHome", "Total attendee count updated: " + totalAttendeeCount);
                getActivity().runOnUiThread(() -> addCheckInMilestones(totalAttendeeCount));
            }
        });

        realtimeData.startListeningForEventCount(userId);
    }



    /**
     * Creates the view for OrganizerHome fragment, deals with the functionality
     * for displaying the list of milestones AND AddAnnouncementFragment call
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view
     */
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_home, container, false);
        addNavBarListeners(getActivity(), view);
        OrganizerFragment.setNavActive(view, 0);

        profPic = view.findViewById(R.id.organizer_profile_pic); // get the profile pic

        // load pfp
        ProfileImage profileImage = new ProfileImage(getContext());
        profileImage.getProfileImage(getContext(), userId, new ProfileImage.ProfileImageCallback() {
            @Override
            public void onImageReady(Bitmap image) {
                profPic.setImageBitmap(image);
            }
        });


        milestoneList = new ArrayList<>();
        RecyclerView milestonesRecyclerView = view.findViewById(R.id.milestones_recycler_view);


        // Create the MilestoneAdapter and set it to the RecyclerView
        milestoneAdapter = new MilestoneAdapter(milestoneList);
        milestonesRecyclerView.setAdapter(milestoneAdapter);

        // Later added -> Earlier shown
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        milestonesRecyclerView.setLayoutManager(layoutManager);

        TextView nameField = view.findViewById(R.id.organizer_name);
        nameField.setText(MainActivity.user.getName());

        // Announcement

        final EditText announcement = view.findViewById(R.id.announcement_trigger);
        announcement.setOnFocusChangeListener(new View.OnFocusChangeListener() { // Focus = When the announcement body is clicked.
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Call the fragment
                    AddAnnouncementFragment fragment = new AddAnnouncementFragment(userName);
                    fragment.show(getChildFragmentManager(), "AddAnnouncementFragment");

                }
            }
        });

        return view;

    }



    /**
     * What the milestones will show based on the total attendee count
     * @param totalAttendeeCount the number of attendees
     */
    @SuppressLint("NotifyDataSetChanged")
    private void addCheckInMilestones(int totalAttendeeCount) {

        if(totalAttendeeCount==0) {
            //do nothing
        }
        else if (totalAttendeeCount >=1 && totalAttendeeCount <5 && !hasFirstAttendeeCheck){
            milestoneList.add(new Milestone("Iron Check-in", "Congratulations! You got your first check in!"));
            hasFirstAttendeeCheck = true;
        } else if ((totalAttendeeCount >=5 && totalAttendeeCount <50 && !hasSecondAttendeeCheck)) {
            milestoneList.add(new Milestone("Bronze Check-in", "Congratulations! You've achieved 5 check-ins."));
            hasSecondAttendeeCheck = true;
        } else if ((totalAttendeeCount >=10 && totalAttendeeCount <50 && !hasThirdAttendeeCheck)) {
            milestoneList.add(new Milestone("Silver Check-in", "Wow! You've achieved 10 check-ins."));
            hasThirdAttendeeCheck = true;
        } else if ((totalAttendeeCount >=50 && totalAttendeeCount <500 && !hasFourthAttendeeCheck)){
            milestoneList.add(new Milestone("Gold Check-in", "Nice! You've achieved 50 check-ins."));
            hasFourthAttendeeCheck = true;
        } else if (((totalAttendeeCount >=500 && totalAttendeeCount <1000 && !hasFifthAttendeeCheck))){
            milestoneList.add(new Milestone("Platinum Check-in", "Incredible! You've achieved 500 check-ins."));
            hasFifthAttendeeCheck = true;
        }
        milestoneAdapter.notifyDataSetChanged();
    }


    /***
     * What the milestones will be based on the event count
     *
     * @param eventCount the number of events the organizer has
     */
    @SuppressLint("NotifyDataSetChanged")
    private void addEventMilestones(int eventCount) {

        if (eventCount == 0) {
            // do nothing
        } else if (eventCount >=1 && eventCount <5 && !hasFirstEventCheck) {
            milestoneList.add(new Milestone("Event Rookie", "Way to go! You made your first event."));
            hasFirstEventCheck= true;
        } else if (eventCount >=5 && eventCount <20 && !hasSecondEventCheck) {
            milestoneList.add(new Milestone("Event Enthusiast", "You've hosted 5 events. Keep the momentum going!"));
            hasSecondEventCheck = true;
        } else if (eventCount >=20 && eventCount <50  && !hasThirdEventCheck) {
            milestoneList.add(new Milestone("Event Icon", "Impressive! 20 events hosted. You're a seasoned host."));
            hasThirdEventCheck = true;
        } else if (eventCount >=50 && eventCount <100 && !hasFourthEventCheck) {
            milestoneList.add(new Milestone("Event Superstar", "Congratulations! You've hosted 50 events. Your impact is remarkable."));
            hasFourthEventCheck = true;
        } else if (eventCount >=100 && eventCount <1000  && !hasFifthEventCheck ) {
            milestoneList.add(new Milestone("Event Legend", "Incredible achievement! You've hosted 100 events. You're a true legend."));
            hasFifthEventCheck = true;
        }
        milestoneAdapter.notifyDataSetChanged();
    }



}
