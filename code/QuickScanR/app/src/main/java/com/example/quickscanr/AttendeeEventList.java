package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Attendee Event List Page functionality
 */
public class AttendeeEventList extends AttendeeFragment {
    RecyclerView eventRecyclerView;
    ArrayList<Event> eventDataList;
    EventItemArrayAdapter eventArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    public static String EVENT_COLLECTION = "events";

    public AttendeeEventList() {}

    public static AttendeeEventList newInstance(String param1, String param2) {
        AttendeeEventList fragment = new AttendeeEventList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attendee_event_list, container, false);
        addNavBarListeners(getActivity(), v);

        // DB LINKING
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection(EVENT_COLLECTION);

        eventRecyclerView = v.findViewById(R.id.atnd_ev_list);
        eventDataList = new ArrayList<>();

        addListeners();
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addSnapshotListenerForEvent();

        return v;
    }

    /**
     * Snapshot Listener for real-time updates
     */
    private void addSnapshotListenerForEvent() {
        eventsRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            eventDataList.clear();
            for (QueryDocumentSnapshot doc: value) {
                String eventName = doc.getString("name");
                String eventDesc = doc.getString("description");
                String eventLoc = doc.getString("location");
                String eventRest = doc.getString("restrictions");
                String eventStart = doc.getString("startDate");
                String eventEnd = doc.getString("endDate");
                User orgTemp = new User("Test","Test","test",0);  // TO BE REMOVED

                Log.d("DEBUG", String.format("Event (%s) fetched", eventName));
                eventDataList.add(new Event(eventName, eventDesc, eventLoc, eventStart, eventEnd, eventRest, orgTemp));
            }
            eventArrayAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Sets up listeners for any clickable items on the page
     */
    public void addListeners() {
        eventArrayAdapter = new EventItemArrayAdapter(getContext(), eventDataList, position -> eventClickAction(eventDataList.get(position)));
        eventRecyclerView.setAdapter(eventArrayAdapter);
    }

    /**
     * Functionality for clicked event object.
     * @param event Event object clicked by user
     */
    private void eventClickAction(Event event) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, EventDetails.newInstance(event))
                .addToBackStack(null).commit();
    }
}