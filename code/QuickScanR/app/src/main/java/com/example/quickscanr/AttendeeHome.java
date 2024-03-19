package com.example.quickscanr;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/***
 * This represents the fragment home page for the Attendee
 * Additionally, it also deals with displaying announcements on the Attendee's home page.
 * @see Announcement
 * @see AnnouncementAdapter
 */

public class AttendeeHome extends AttendeeFragment {

    private ArrayList<Announcement> announcementsDataList;

    // db

    private FirebaseFirestore db;
    private CollectionReference citiesRef;

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
        citiesRef = db.collection(ANNOUNCEMENT_COLLECTION);

        // Create the announcementAdapter and set it to the RecyclerView
        announcementAdapter = new AnnouncementAdapter(announcementsDataList);
        announcementsRecyclerView.setAdapter(announcementAdapter);

        // Later added -> Earlier shown
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        announcementsRecyclerView.setLayoutManager(layoutManager);

        addSnapshotListenerForAnnouncement();

        TextView nameField = view.findViewById(R.id.user_name);
        nameField.setText(MainActivity.user.getName());

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
                String announcementTitle = doc.getString(DatabaseConstants.anTitle);

                Log.d("DEBUG: AttendeeHome", String.format("Announcement( User: %s, Title: %s) fetched", announcementUser, announcementTitle));
                announcementsDataList.add(new Announcement(announcementTitle, announcementBody,announcementDate, announcementUser));

            }

            announcementAdapter.notifyDataSetChanged();
        });
    }

}
