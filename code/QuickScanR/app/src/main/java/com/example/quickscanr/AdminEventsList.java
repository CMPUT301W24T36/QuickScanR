package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminEventsList extends AdminFragment {

    RecyclerView eventView;
    ArrayList<Event> eventList;
    AdminEventArrayAdapter eventArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventReference;
    public static String EVENT_COLLECTION = "events";

    public AdminEventsList() {}

    public static AdminEventsList newInstance(String param1, String param2) {
        AdminEventsList fragment = new AdminEventsList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_browse_events, container, false);
        addNavBarListeners(getActivity(), v);

//         DB LINKING
        db = FirebaseFirestore.getInstance();
        eventReference = db.collection(EVENT_COLLECTION);

        eventView = v.findViewById(R.id.view_event_list);
        eventList = new ArrayList<>();


        addListeners();
        eventView.setLayoutManager(new LinearLayoutManager(getContext()));
        addSnapshotListenerForEvent();

        return v;
    }

    //snapshot is for real time updates
    private void addSnapshotListenerForEvent() {
        eventReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            eventList.clear();
            for (QueryDocumentSnapshot doc: value) {
                User admin = new User("testing","testing","testing",2);

                //call from database constants is the other class already set up
                //figure out what to do for admin profile
                String eventName = doc.getString(DatabaseConstants.evNameKey);
                String eventDesc = doc.getString(DatabaseConstants.evDescKey);
                String eventLoc = doc.getString(DatabaseConstants.evLocKey);
                String eventRest = doc.getString(DatabaseConstants.evRestricKey);
                String eventStart = doc.getString(DatabaseConstants.evStartKey);
                String eventEnd = doc.getString(DatabaseConstants.evEndKey);

                Log.d("DEBUG", String.format("Event (%s) fetched", eventName));
                eventList.add(new Event(eventName, eventDesc, eventLoc, eventStart, eventEnd, eventRest, admin));
            }
            eventArrayAdapter.notifyDataSetChanged();

        });
    }

    //listen for the clickable items
    public void addListeners() {
        eventArrayAdapter = new AdminEventArrayAdapter(getContext(), eventList, position -> buttonClickAction(eventList.get(position)));
        eventView.setAdapter(eventArrayAdapter);
    }


    //When you click on the buttonClickAction, it will link the position and take you
    //to the manage profile that fills in info
    private void buttonClickAction(Event event) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, AdminManageEvent.newInstance(event))
                .addToBackStack(null).commit();
    }
}
