package com.example.quickscanr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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
    private ListenerRegistration allEventListenReg;
    private ListenerRegistration checkedEventListenReg;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private static boolean toggleAll = false;
    MainActivity mainActivity = (MainActivity) getActivity();
    User user = mainActivity.user;

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
        eventsRef = db.collection(DatabaseConstants.eventColName);
        usersRef = db.collection(DatabaseConstants.usersColName);

        eventRecyclerView = v.findViewById(R.id.atnd_ev_list);
        eventDataList = new ArrayList<>();

        addListeners(v);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AttendeeFragment.setNavActive(v, 1);

        return v;
    }

    /**
     * Snapshot Listener for real-time updates to all events
     */
    private void addSnapshotListenerForEvent() {
        eventDataList.clear();  // wipe old data

        // add listener and check for errors
        allEventListenReg = eventsRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }
            if (value == null) {
                return;
            }

            // get all event IDs
            ArrayList<String> arr = new ArrayList<>();
            for (QueryDocumentSnapshot doc: value) {
                arr.add(doc.getId());
            }
            loadEventIDs(arr);
        });
    }

    /**
     * Sets up listeners for any clickable items on the page
     */
    public void addListeners(View v) {
        eventArrayAdapter = new EventItemArrayAdapter(getContext(), eventDataList, position -> eventClickAction(eventDataList.get(position)));
        eventRecyclerView.setAdapter(eventArrayAdapter);
        Button toggleBtn = v.findViewById(R.id.attEvListToggle);
        TextView evListTitle = v.findViewById(R.id.textView);
        toggleBtn.setOnClickListener(view -> {
            toggleEvent(toggleAll, toggleBtn, evListTitle);
        });
        toggleEvent(!toggleAll, toggleBtn, evListTitle);
    }

    /**
     * Functionality for clicked event object.
     * @param event Event object clicked by user
     */
    private void eventClickAction(Event event) {
        DocumentReference user = usersRef.document(MainActivity.user.getUserId());
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> checkedEvents = (ArrayList<String>) document.get(DatabaseConstants.userCheckedEventsKey);
                    ArrayList<String> signedUpEvents = (ArrayList<String>) document.get(DatabaseConstants.userSignedUpEventsKey);
                    boolean showSignUp = false;
                    if (!checkedEvents.contains(event.getId()) && !signedUpEvents.contains(event.getId())) {
                        showSignUp = true;
                    }
                    Bundle args = new Bundle();
                    EventDetails evDetFragment = EventDetails.newInstance(event);
                    args.putSerializable("event", event);
                    args.putBoolean("showSignUpDialog", showSignUp);
                    evDetFragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, evDetFragment)
                            .addToBackStack(null).commit();
                }
            }
        });
    }

    /**
     * Toggle functionality between checked in events and all events
     * @param viewAll  true to display all, false to display own
     * @param Btn  Toggle Button object
     * @param title  Title text object
     */
    private void toggleEvent(boolean viewAll, Button Btn, TextView title) {
        if (!viewAll) {
            Btn.setText("View Own");
            title.setText("All Events");
            toggleAll = true;
            if (checkedEventListenReg != null) checkedEventListenReg.remove();
            addSnapshotListenerForEvent();
            eventArrayAdapter.notifyDataSetChanged();
        } else {
            Btn.setText("View All");
            title.setText("My Events");
            toggleAll = false;
            if (allEventListenReg != null) allEventListenReg.remove();
            addCheckedEventsListener();
            eventArrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Snapshot Listener for real-time updates to checked in events
     */
    private void addCheckedEventsListener() {
        eventDataList.clear();   // wipe old data

        // add listener and check for errors
        checkedEventListenReg = db.collection(DatabaseConstants.usersColName).document(user.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("AEL", "Failed to get checked events", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    // get all checked event IDs for loading
                    ArrayList<String> checkedEvents = (ArrayList<String>) snapshot.get(DatabaseConstants.userCheckedEventsKey);
                    ArrayList<String> signedUpEvents = (ArrayList<String>) snapshot.get(DatabaseConstants.userSignedUpEventsKey);
                    checkedEvents.addAll(signedUpEvents);   // checkedEvents holds all own events (checked in and signed up)
                    loadEventIDs(checkedEvents);
                } else {
                    Log.d("AEL", "No checked events to list");
                }
            }
        });
    }

    /**
     * New method to update the display with a list of eventIDs
     * allowing for more re-usability of code
     * @param eventIDs
     */
    private void loadEventIDs(ArrayList<String> eventIDs) {
        for (String ID : eventIDs) {
            eventsRef.document(ID).get().addOnSuccessListener(doc -> {
                String eventName = doc.getString(DatabaseConstants.evNameKey);
                String eventDesc = doc.getString(DatabaseConstants.evDescKey);
                String eventLocName = doc.getString(DatabaseConstants.evLocNameKey);
                String eventLocId = doc.getString(DatabaseConstants.evLocIdKey);
                String eventRest = doc.getString(DatabaseConstants.evRestricKey);
                String eventStart = doc.getString(DatabaseConstants.evStartKey);
                String eventEnd = doc.getString(DatabaseConstants.evEndKey);
                String eventPosterID = doc.getString(DatabaseConstants.evPosterKey);
                String eventOwnerID = doc.getString(DatabaseConstants.evOwnerKey);
                String eventID = doc.getId();

                // build user object
                db.collection(DatabaseConstants.usersColName).document(eventOwnerID).get().addOnSuccessListener(document -> {
                    String name = document.getString(DatabaseConstants.userFullNameKey);
                    Integer type = Integer.parseInt(document.get(DatabaseConstants.userTypeKey).toString());
                    String phone = document.getString(DatabaseConstants.userPhoneKey);
                    String email = document.getString(DatabaseConstants.userPhoneKey);
                    String picID = document.getString(DatabaseConstants.userImageKey);
                    User organizer = new User(name,phone,email,type);
                    organizer.setImageID(picID);

                    // continue fetching
                    Log.d("AEL", String.format("Fetched (%s)", eventName));
                    Event newEvent = new Event(eventName, eventDesc, eventLocName, eventLocId, eventStart, eventEnd, eventRest, organizer);
                    newEvent.setId(eventID);
                    if (!Objects.equals(eventPosterID, "")) {
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
                });
            });

        }
    }
}