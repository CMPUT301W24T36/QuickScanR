package com.example.quickscanr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Attendee Event List Page functionality
 * Deals with getting the events out of the database and displaying it on the UI.
 * referenced https://stackoverflow.com/questions/5283047/intersection-and-union-of-arraylists-in-java
 *      for only showing an event once if the user has signed up and checked in to an event
 * @see Event
 * @see EventItemArrayAdapter
 */
public class AttendeeEventList extends AttendeeFragment {
    private RecyclerView eventRecyclerView;
    private ArrayList<Event> eventDataList;
    private EventItemArrayAdapter eventArrayAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration allEventListenReg;
    private ListenerRegistration filteredEventsListenReg;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    MainActivity mainActivity = (MainActivity) getActivity();
    User user = mainActivity.user;

    /**
     * enum for the filters on the attendee event list
     */
    public enum Filters {
        CURRENT("Current"),
        SIGNED_UP("Signed Up"),
        ALL("All");

        private final String label;

        /**
         * constructor for the enum
         * @param label the string representing the label for the filter
         */
        Filters(String label) {
            this.label = label;
        }

        /**
         * gets the label for the filter
         * @return string for the label of the filter
         */
        public String getLabel() {
            return label;
        }
    }

    /**
     * Constructor
     */
    public AttendeeEventList() {}

    /**
     * gets a new instance of the AttendeeEventList fragment
     * @return new AttendeeEventList fragment
     */
    public static AttendeeEventList newInstance() {
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
            loadEventIDs(arr, Filters.ALL.ordinal());
        });
    }

    /**
     * Sets up listeners for any clickable items on the page
     * sets up the event filter Spinner object
     */
    public void addListeners(View v) {
        eventArrayAdapter = new EventItemArrayAdapter(getContext(), eventDataList, position -> eventClickAction(eventDataList.get(position)));
        eventRecyclerView.setAdapter(eventArrayAdapter);
        TextView evListTitle = v.findViewById(R.id.textView);

        Spinner filterToggle = v.findViewById(R.id.atd_ev_list_filter);

        ArrayList<String> userTypes = new ArrayList<>();
        userTypes.add(Filters.CURRENT.getLabel());
        userTypes.add(Filters.SIGNED_UP.getLabel());
        userTypes.add(Filters.ALL.getLabel());

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, userTypes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.qr_active));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                return view;
            }

        };

        // setting the adapter and initialize selection to current events
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterToggle.setAdapter(filterAdapter);
        filterToggle.setSelection(Filters.CURRENT.ordinal());

        filterToggle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = (String) filterToggle.getItemAtPosition(position);
                showEvents(filter, evListTitle);   // filter events when filter is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showEvents(Filters.CURRENT.getLabel(), evListTitle);
            }
        });
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
     * show events based on a filter
     * @param filter string representing the current filter (Current, Signed Up, All)
     * @param title  Title text object
     */
    private void showEvents(String filter, TextView title) {
        title.setText(filter + " Events");
        if (allEventListenReg != null) allEventListenReg.remove();
        if (filteredEventsListenReg != null) filteredEventsListenReg.remove();
        if (filter.equals(Filters.CURRENT.getLabel())) {
            addFilteredEventsListener(Filters.CURRENT.ordinal());
        }
        else if (filter.equals(Filters.SIGNED_UP.getLabel())) {
            addFilteredEventsListener(Filters.SIGNED_UP.ordinal());
        }
        else if (filter.equals(Filters.ALL.getLabel())) {
            addSnapshotListenerForEvent();
        }
        eventArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Snapshot Listener for real-time updates to current events (happening right now and checked in events)
     * @param current 0 if only show current events, 1 if show future events
     */
    private void addFilteredEventsListener(Integer current) {
        eventDataList.clear();   // wipe old data

        // add listener and check for errors
        filteredEventsListenReg = db.collection(DatabaseConstants.usersColName).document(user.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("AEL", "Failed to get checked events", error);
                    return;
                }
                if (value != null && value.exists()) {
                    // get all the user's event IDs for loading
                    Set<String> eventSet = new HashSet<>();

                    ArrayList<String> docCheckedEvents = (ArrayList<String>) value.get(DatabaseConstants.userCheckedEventsKey);
                    ArrayList<String> docSignedUpEvents = (ArrayList<String>) value.get(DatabaseConstants.userSignedUpEventsKey);

                    if (docCheckedEvents != null) {
                        eventSet.addAll(docCheckedEvents);
                    }
                    if (docCheckedEvents == null && docSignedUpEvents != null) {
                        eventSet.addAll(docSignedUpEvents);
                    }
                    else if (docCheckedEvents != null && docSignedUpEvents != null) {
                        eventSet.addAll(docSignedUpEvents);
                    }

                    ArrayList<String> allFilteredEvents = new ArrayList<>(eventSet);
                    if (allFilteredEvents != null) {    // allFilteredEvents holds all own events (checked in and signed up)
                        loadEventIDs(allFilteredEvents, current);
                    }
                } else {
                    Log.d("AEL", "No current events to list");
                }
            }
        });
    }

    /**
     * New method to update the display with a list of eventIDs
     * allowing for more re-usability of code
     * @param eventIDs event IDs to show on the page
     * @param filter 0 = Current, 1 = Signed Up (future), 2 = All
     */
    private void loadEventIDs(ArrayList<String> eventIDs, Integer filter) {
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
                ArrayList<String> eventSignedUpUsers = (ArrayList<String>) doc.get(DatabaseConstants.evSignedUpUsersKey);

                Integer eventMaxAttendees;
                if (doc.contains(DatabaseConstants.evAttendeeLimitKey)) {
                    eventMaxAttendees = doc.getLong(DatabaseConstants.evAttendeeLimitKey).intValue();
                } else {
                    eventMaxAttendees = -1;
                }

                boolean add = true;

                if (filter == Filters.CURRENT.ordinal()) {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date startDate = format.parse(eventStart);
                        Date endDate = format.parse(eventEnd);
                        Date currentDate = new Date();
                        String formattedCurrDate = format.format(currentDate);
                        if (!((formattedCurrDate.equals(eventStart) || currentDate.after(startDate)) && (formattedCurrDate.equals(eventEnd) || currentDate.before(endDate)))) {
                            add = false;
                        }
                    } catch (ParseException e) {}
                }
                else if (filter == Filters.SIGNED_UP.ordinal()) {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date startDate = format.parse(eventStart);
                        Date currentDate = new Date();
                        if (!startDate.after(currentDate)) {
                            add = false;
                        }
                    } catch (ParseException e) {}
                }

                // only add event to the list if it satisfies the filter (if there is one)
                if (add) {
                    // build user object
                    db.collection(DatabaseConstants.usersColName).document(eventOwnerID).get().addOnSuccessListener(document -> {
                        String name = document.getString(DatabaseConstants.userFullNameKey);
                        Integer type = Integer.parseInt(document.get(DatabaseConstants.userTypeKey).toString());
                        String phone = document.getString(DatabaseConstants.userPhoneKey);
                        String email = document.getString(DatabaseConstants.userPhoneKey);
                        String picID = document.getString(DatabaseConstants.userImageKey);
                        User organizer = new User(name,phone,email,type, eventOwnerID);
                        organizer.setImageID(picID,false);

                        // continue fetching
                        Log.d("AEL", String.format("Fetched (%s) with id %s", eventName, eventID));
                        Event newEvent = new Event(eventName, eventDesc, eventLocName, eventLocId, eventStart, eventEnd, eventRest, organizer);
                        newEvent.setId(eventID);
                        newEvent.setSignedUp(eventSignedUpUsers);
                        newEvent.setMaxAttendees(eventMaxAttendees);
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
                }
            });

        }
    }
}