package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * AdminManageEvents
 * - allows admin to view more info about the user and also delete the user
 */
public class AdminManageEvent extends InnerPageFragment{
    private Event event;
    private String event_id;


    Button deleteEvents;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;

    public static String EVENTS_COLLECTION = "events";
    public static String USERS_COLLECTION = "users";


    public AdminManageEvent(Event event) {
        this.event = event;
    }

    //On create we need to inflate the xml and create the fragment that can be connected to the event page
    /**
     * onCreate:
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            event_id = getArguments().getString("eventId");

        }
    }

    /**
     * onCreateView:
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *       - returns v, which is the view with the inflated layout
     *       - also returns the updated version of any change made with deleting
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_manage_event, container, false);
        //go back to events list when clicked
        addButtonListeners(getActivity(), v, new AdminEventsList());
        populateInfo(v);

        deleteEvents = v.findViewById(R.id.delete_btn);


        //set up the database
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection(EVENTS_COLLECTION);

        usersRef = db.collection(USERS_COLLECTION);

        deleteEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String event_name = event.getName();
//                String event_desc = event.getDescription();
//                String event_loc = event.getLocationName();
//                Log.d("DEBUG", event_name + event_desc + event_loc);
                    //THIS IS CORRECT< DO NOT TOUCH
                eventsRef.document(event_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        //also remove it from the checked events for any user
                        usersRef.addSnapshotListener((value, error) -> {
                            if (error != null) {
                                Log.e("DEBUG: AEL", error.getMessage());
                                return;
                            }

                            if (value == null) {
                                return;
                            }

                            for(QueryDocumentSnapshot doc: value){
                                List<String> checkedEvt = (List<String>) doc.get("checkedEvents");

                                if(checkedEvt != null && checkedEvt.contains(event_id)){
                                    checkedEvt.remove(event_id);
                                    usersRef.document(doc.getId()).update("checkedEvents", FieldValue.arrayRemove(event_id));
                                }

                            }
                        });

                        //remove from signed up events for any user
                        usersRef.addSnapshotListener((value, error) -> {
                            if (error != null) {
                                Log.e("DEBUG: AEL", error.getMessage());
                                return;
                            }

                            if (value == null) {
                                return;
                            }

                            for(QueryDocumentSnapshot doc: value){
                                List<String> signedUp = (List<String>) doc.get("signedUp");

                                if(signedUp != null && signedUp.contains(event_id)){
                                    signedUp.remove(event_id);
                                    usersRef.document(doc.getId()).update("signedUp", FieldValue.arrayRemove(event_id));
                                }

                            }
                        });

                        //go back to the previous page
                        AdminEventsList adminEventsList = new AdminEventsList();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, adminEventsList)
                                .addToBackStack(null).commit();
                    }
                });

//                eventsRef.document(event_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        usersRef.addSnapshotListener((value, error) -> {
//                            if (error != null) {
//                                Log.e("DEBUG: AEL", error.getMessage());
//                                return;
//                            }
//
//                            if (value == null) {
//                                return;
//                            }
//
//                            for(QueryDocumentSnapshot doc: value){
//                                List<String> checkedEvt = (List<String>) doc.get("checkedEvents");
//
//                                if(checkedEvt != null && checkedEvt.contains(event_id)){
//                                    checkedEvt.remove(event_id);
//                                    usersRef.document(doc.getId()).update("checkedEvents", FieldValue.arrayRemove(event_id));
//                                }
//
//                            }
//                        });
//                    }
//                });
//                eventsRef.whereEqualTo("name", event_name).whereEqualTo("description", event_desc)
//                        .whereEqualTo("location", event_loc)
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                                    String docId = doc.getId();
//                                    eventsRef.document(docId).delete();
//
//                                    Log.d("DEBUG", docId);
//                                    //go back to the previous page
//                                    AdminEventsList adminEventsList = new AdminEventsList();
//                                    getActivity().getSupportFragmentManager().beginTransaction()
//                                            .replace(R.id.content_main, adminEventsList)
//                                            .addToBackStack(null).commit();
//                                }
//                            }
//                        });
            }
        });


        return v;
    }

    /**
     *AdminManageProfile:
     *  - new instance of a fragment with specific user data
     * @param event : specific data regarding a user
     * @return
     *  - fragment: a new instance that is initialized with new user data
     */
    public static AdminManageEvent newInstance(Event event, String eventId) {
        AdminManageEvent fragment = new AdminManageEvent(event);
        Bundle args = new Bundle();

        args.putSerializable("event", event);
        args.putString("eventId", eventId);

        fragment.setArguments(args);

        return fragment;
    }

    /**
     *populateInfo:
     *  - populate fields with extra user information for admin to see
     * @param v: view that has the fields with the same data that we will populate the fields with
     */
    public void populateInfo(View v){
        TextInputEditText name = v.findViewById(R.id.manage_name);
        TextInputEditText descrption = v.findViewById(R.id.manage_desc);
        TextInputEditText location = v.findViewById(R.id.manage_loc);
        TextInputEditText starts = v.findViewById(R.id.manage_start);
        TextInputEditText ends = v.findViewById(R.id.manage_end);
        TextInputEditText guest_rst = v.findViewById(R.id.manage_restrictions);


        name.setText(event.getName());
        descrption.setText(event.getDescription());
        location.setText(event.getLocationName());
        starts.setText(event.getStart());
        ends.setText(event.getEnd());
        guest_rst.setText(event.getRestrictions());

    }


}
