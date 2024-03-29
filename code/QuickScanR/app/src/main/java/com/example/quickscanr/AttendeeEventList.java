package com.example.quickscanr;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Attendee Event List Page functionality
 * Deals with getting the events out of the database and displaying it on the UI.
 * @see Event
 * @see EventItemArrayAdapter
 */
public class AttendeeEventList extends AttendeeFragment {
    private RecyclerView eventRecyclerView;
    private ArrayList<Event> eventDataList;
    private EventItemArrayAdapter eventArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    public static String EVENT_COLLECTION = "events";

    /**
     * Constructor
     */
    public AttendeeEventList() {}

    public static AttendeeEventList newInstance(String param1, String param2) {
        AttendeeEventList fragment = new AttendeeEventList();
        return fragment;
    }

    /**
     * Calls the superclass' onCreate method
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the view relevant to AttendeeEventList
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view created
     */
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
                String eventName = doc.getString(DatabaseConstants.evNameKey);
                String eventDesc = doc.getString(DatabaseConstants.evDescKey);
                String eventLoc = doc.getString(DatabaseConstants.evLocKey);
                String eventRest = doc.getString(DatabaseConstants.evRestricKey);
                String eventStart = doc.getString(DatabaseConstants.evStartKey);
                String eventEnd = doc.getString(DatabaseConstants.evEndKey);
                String eventPosterID = doc.getString(DatabaseConstants.evPosterKey);
                User orgTemp = new User("Test","Test","test",0);  // TO BE REMOVED

                Log.d("DEBUG", String.format("Event (%s) fetched", eventName));
                Event newEvent = new Event(eventName, eventDesc, eventLoc, eventStart, eventEnd, eventRest, orgTemp);
                if (eventPosterID != "") {
                    ImgHandler imgHandler = new ImgHandler(getContext());
                    imgHandler.getImage(eventPosterID, bitmap -> {
                        newEvent.setPoster(bitmap);
                        eventDataList.add(newEvent);
                        eventArrayAdapter.notifyDataSetChanged();
                    });
                } else {
                    eventDataList.add(newEvent);
                    eventArrayAdapter.notifyDataSetChanged();
                }
            }
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