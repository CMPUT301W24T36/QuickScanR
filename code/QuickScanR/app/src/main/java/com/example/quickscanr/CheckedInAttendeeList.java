package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fragment class for displaying a list of attendees who have checked into an event.
 * uses Firebase Firestore to fetch and display the attendees' data in a RecyclerView.
 * @see CheckedInAttendeeAdapter
 */

public class CheckedInAttendeeList extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Map<String, Object>> attendeesData = new ArrayList<>();
    private CheckedInAttendeeAdapter adapter;
    private RecyclerView recyclerView;

    /**
     * creates a new instance of CheckedInAttendeeList fragment with an event ID
     * @param eventId : ID of the event to display checked-in attendees for
     * @return : new instance of CheckedInAttendeeList with event ID passed as an argument
     */

    public static CheckedInAttendeeList newInstance(String eventId) {
        CheckedInAttendeeList fragment = new CheckedInAttendeeList();
        Bundle args = new Bundle();
        args.putString("EVENT_ID", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * inflates the fragment's view and initializes RecyclerView + Firestore data fetching
     * @param inflater : LayoutInflater object, can be used to inflate any views in the fragment
     * @param container : (if non-null) the parent view that the fragment's UI should be attached to
     * @param savedInstanceState : (f non-null) fragment re-constructed from a previous saved state
     * @return : View for the fragment's UI, or null
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checked_users_list, container, false);

        recyclerView = view.findViewById(R.id.chkd_usrs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new CheckedInAttendeeAdapter(getContext(), attendeesData);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            setupFirestoreRealtimeUpdate(eventId);
        }

        View backButton = view.findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });


        return view;
    }

    /**
     * sets up Firestore real-time update listener for event's attendees collection
     * updates the attendees data list and the RecyclerView adapter when changes are detected
     * @param eventId : ID of the event for which to listen for attendee check-ins
     */

    private void setupFirestoreRealtimeUpdate(String eventId) {
        final CollectionReference attendeesRef = db.collection("events").document(eventId).collection("attendees");

        attendeesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("CheckedInAttendeeList", "Listen failed.", e);
                    return;
                }

                List<Map<String, Object>> updatedAttendeesData = new ArrayList<>();
                if (snapshots != null) {
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        // Corrected debug log statement
                        Log.d("CheckedInAttendeeList", "Timestamps size for user " + doc.getId() + ": " +
                                (doc.get("timestamps") instanceof List ? ((List<?>) doc.get("timestamps")).size() : "null or not a list"));

                        Map<String, Object> attendeeData = new HashMap<>();
                        attendeeData.put("userId", doc.getId());
                        attendeeData.put("name", doc.getString("name"));
                        attendeeData.put("timestamps", doc.get("timestamps"));
                        updatedAttendeesData.add(attendeeData);
                    }
                }

                attendeesData.clear();
                attendeesData.addAll(updatedAttendeesData);
                adapter.notifyDataSetChanged();
            }
        });
    }

}