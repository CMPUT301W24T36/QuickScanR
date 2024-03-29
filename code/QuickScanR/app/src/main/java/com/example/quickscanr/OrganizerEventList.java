package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
 * Organizer Event List Page Functionality
 * @see Event
 * @see EventItemArrayAdapter
 */
public class OrganizerEventList extends OrganizerFragment {
    RecyclerView eventRecyclerView;
    ArrayList<Event> eventDataList;
    EventItemArrayAdapter eventArrayAdapter;
    private String userId;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    public static String EVENT_COLLECTION = "events";

    /**
     * Constructor
     */
    public OrganizerEventList() {}

    /**
     * Called when creating a new instance of the fragment
     * @param param1
     * @param param2
     * @return OrganizerEventList fragment
     */
    public static OrganizerEventList newInstance(String param1, String param2) {
        OrganizerEventList fragment = new OrganizerEventList();
        return fragment;
    }

    /**
     * Called at creation of the fragment OrganizerEventList
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called when creating the view for OrganizerEventList
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View of OrganizerEventList
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.organizer_event_list, container, false);
        addNavBarListeners(getActivity(), v);

        // DB LINKING
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection(EVENT_COLLECTION);

        eventRecyclerView = v.findViewById(R.id.org_ev_list);
        eventDataList = new ArrayList<>();

        MainActivity mainActivity = (MainActivity) getActivity();
        userId = mainActivity.user.getUserId();

        addListeners();
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addSnapshotListenerForEvent();

        return v;
    }

    /**
     * Snapshot Listener for real-time updates
     */
    private void addSnapshotListenerForEvent() {
        eventsRef.whereEqualTo(DatabaseConstants.evOwnerKey, userId).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG: OEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            eventDataList.clear();
            for (QueryDocumentSnapshot doc: value) {
                String eventName = doc.getString(DatabaseConstants.evNameKey);
                String eventDesc = doc.getString(DatabaseConstants.evDescKey);
                String eventLocName = doc.getString(DatabaseConstants.evLocNameKey);
                String eventLocId = doc.getString(DatabaseConstants.evLocIdKey);
                String eventRest = doc.getString(DatabaseConstants.evRestricKey);
                String eventStart = doc.getString(DatabaseConstants.evStartKey);
                String eventEnd = doc.getString(DatabaseConstants.evEndKey);
                User orgTemp = new User("Test","Test","test",0);  // TO BE REMOVED
                String eventId = doc.getId();
                Long eventTimestamp; // Declare the timestamp variable
                // Check if the document contains the timestamp field; TO BE REMOVED WHEN TIMESTAMPS ALWAYS EXIST
                if (doc.contains(DatabaseConstants.evTimestampKey)) {
                    eventTimestamp = doc.getLong(DatabaseConstants.evTimestampKey);
                } else {
                    eventTimestamp = 0L;    // Assign a default value if the timestamp doesn't exist
                }

                Log.d("DEBUG", String.format("Event (%s) fetched", eventName));
                eventDataList.add(new Event(eventName, eventDesc, eventLocName, eventLocId, eventStart, eventEnd, eventRest, orgTemp, eventId, eventTimestamp));
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
                .replace(R.id.content_main, EventDashboard.newInstance(event))
                .addToBackStack(null).commit();
    }
}
