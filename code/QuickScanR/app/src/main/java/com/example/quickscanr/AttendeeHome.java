package com.example.quickscanr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/***
 * This represents the fragment home page for the Attendee
 * Additionally, it also deals with displaying announcements on the Attendee's home page.
 * @see Announcement
 * @see AnnouncementAdapter
 */

public class AttendeeHome extends AttendeeFragment {

    private ArrayList<Announcement> announcementsDataList;
    private String currentUserId;

    // db

    private FirebaseFirestore db;

    public static String ANNOUNCEMENT_COLLECTION = "announcements";
    private CollectionReference announcementsRef;
    private AnnouncementAdapter announcementAdapter;

    /**
     * Constructor
     */
    public AttendeeHome() {}

    /**
     * Returns a new instance of the AttendeeHome fragment
     * @param param1
     * @param param2
     * @return the fragment, AttendeeHome
     */
    public static AttendeeHome newInstance(String param1, String param2) {
        AttendeeHome fragment = new AttendeeHome();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Initialize currentUserId with the ID of the logged-in user here
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.user != null) {
            currentUserId = mainActivity.user.getUserId();
        } else {
            // Handle the scenario when MainActivity or user is null
            throw new IllegalStateException("User must be logged in to view AttendeeHome");
        }
    }


    /**
     * Is called when the fragment is first created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Responsible for creating and returning the view for AttendeeHome
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the View that has been created and linked to the database
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_home,container,false);
        addNavBarListeners(getActivity(), view);

        announcementsDataList = new ArrayList<>();
        RecyclerView announcementsRecyclerView = view.findViewById(R.id.announcements_recycler_view);



        // Connect to the database
        db = FirebaseFirestore.getInstance();
        announcementsRef = db.collection(ANNOUNCEMENT_COLLECTION);

        // Create the announcementAdapter and set it to the RecyclerView
        announcementAdapter = new AnnouncementAdapter(announcementsDataList);
        announcementsRecyclerView.setAdapter(announcementAdapter);

        // Later added -> Earlier shown
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        announcementsRecyclerView.setLayoutManager(layoutManager);

        fetchSignedUpEventsAndListenForAnnouncements();

        TextView nameField = view.findViewById(R.id.user_name);
        nameField.setText(MainActivity.user.getName());
        AttendeeFragment.setNavActive(view, 0);

        return view;
    }

    private void fetchSignedUpEventsAndListenForAnnouncements() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("signedUp")) {
                List<String> signedUpEvents = (List<String>) documentSnapshot.get("signedUp");
                if (signedUpEvents != null && !signedUpEvents.isEmpty()) {
                    // Now listen for announcements related to these events
                    addSnapshotListenerForAnnouncements(signedUpEvents);
                }
            }
        }).addOnFailureListener(e -> Log.e("AttendeeHome", "Error fetching signed up events", e));
    }


    /**
     * Add the snapshot listener for the announcement collection
     */


    private void addSnapshotListenerForAnnouncements(List<String> eventIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        announcementsRef.whereIn("eventId", eventIds)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.e("DEBUG: AttendeeHome", error.getMessage());
                        return;
                    }
                    if (value == null) {
                        return;
                    }

            announcementsDataList.clear();
            for (QueryDocumentSnapshot doc : value) {
                String announcementBody = doc.getString(DatabaseConstants.anBody);
                String announcementDate = doc.getString(DatabaseConstants.anDate);
                String announcementUser= doc.getString(DatabaseConstants.anUserName);
                String announcementTitle = doc.getString(DatabaseConstants.anTitle);
                String announcementOwnerID = doc.getString(DatabaseConstants.anUserKey);
                String announcementEventID = doc.getString(DatabaseConstants.anEventID);
                Announcement an = new Announcement(announcementTitle, announcementBody,announcementDate, announcementUser, announcementOwnerID, announcementEventID);

                // build user object
                db.collection(DatabaseConstants.usersColName).document(announcementOwnerID).get().addOnSuccessListener(document -> {
                    // add profile pic
                    ProfileImage profileImage = new ProfileImage(getContext());
                    profileImage.getProfileImage(getContext(), announcementOwnerID, image -> {
                        Log.d("DEBUG: AttendeeHome", String.format("Announcement( User: %s, Title: %s) fetched", announcementUser, announcementTitle));
                        an.setBitmap(image);
                        announcementsDataList.add(an);
                        announcementAdapter.notifyDataSetChanged();
                    });
                });
            }
            announcementAdapter.notifyDataSetChanged();
        });
    }

}