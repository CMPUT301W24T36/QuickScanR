package com.example.quickscanr;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/***
 * This handles the attendee home fragment that shows announcements
 */

public class AttendeeHome extends AttendeeFragment {

    private ArrayList<Announcement> announcementsDataList;

    // db

    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    public static String ANNOUNCEMENT_COLLECTION = "announcements";
    private CollectionReference announcementsRef;
    private AnnouncementAdapter announcementAdapter;

    public AttendeeHome() {}

    public static AttendeeHome newInstance(String param1, String param2) {
        AttendeeHome fragment = new AttendeeHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_home,container,false);
        addNavBarListeners(getActivity(), view);

        announcementsDataList = new ArrayList<>();
        RecyclerView announcementsRecyclerView = view.findViewById(R.id.announcements_recycler_view);



        // Connect to the database
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection(ANNOUNCEMENT_COLLECTION);

        // Create the announcementAdapter and set it to the RecyclerView
        announcementAdapter = new AnnouncementAdapter(announcementsDataList);
        announcementsRecyclerView.setAdapter(announcementAdapter);
        announcementsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addSnapshotListenerForAnnouncement();

        return view;
    }

    /**
     * Add the snapshot listener for the announcement collection
     */
    @SuppressLint("NotifyDataSetChanged")
    private void addSnapshotListenerForAnnouncement() {
        citiesRef.addSnapshotListener((value, error) -> {
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
                String announcementTitle = doc.getString(DatabaseConstants.anBody);
                String announcementUserID = doc.getString(DatabaseConstants.anUserKey);

                Log.d("DEBUG: AttendeeHome", String.format("Announcement( User: %s, Title: %s) fetched", announcementUser, announcementTitle));
                announcementsDataList.add(new Announcement(announcementTitle, announcementBody,announcementDate, announcementUserID, announcementUser));

            }

            announcementAdapter.notifyDataSetChanged();
        });
    }

}
