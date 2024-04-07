package com.example.quickscanr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * AdminEventsList
 * - allows admin to view all the events and click on each one to get more information
 */
public class AdminEventsList extends AdminFragment {

    RecyclerView eventView;
    ArrayList<Event> eventList;
    ArrayList<String> eventIdList;
    AdminEventArrayAdapter eventArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventReference;
    private CollectionReference imgReference;


    public static String EVENT_COLLECTION = "events";
    public static String IMAGE_COLLECTION = "images";


    public AdminEventsList() {}

    /**
     * AdminEventsList
     *  - creates a new instance of the AdminEventsList fragment
     * @param param1
     *      -
     * @param param2
     * @return
     *  - returns fragment: which is of the new instance AdminEventsList
     */
    public static AdminEventsList newInstance(String param1, String param2) {
        AdminEventsList fragment = new AdminEventsList();
        return fragment;
    }

    /**
     * OnCreate
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateView
     *  - creates the view and inflates layout so that the
     *      browse event list can be displayed
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *      - returns v, which is the view with the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_browse_events, container, false);
        addNavBarListeners(getActivity(), v);

//         DB LINKING
        db = FirebaseFirestore.getInstance();
        eventReference = db.collection(EVENT_COLLECTION);
        imgReference = db.collection(IMAGE_COLLECTION);

        eventView = v.findViewById(R.id.view_event_list);
        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();


        //so that the events show up
        addListeners();
        eventView.setLayoutManager(new LinearLayoutManager(getContext()));
        addSnapshotListenerForEvent();
        AdminFragment.setNavActive(v, 0);

        return v;
    }

    //snapshot is for real time updates
    /**
     * addSnapshotListenerForEvent()
     *  - snapshot listener for the firestore database to listen to the event collection
     *  - anytime there are any changes within the database it updates
     */
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
                //figure out what to do for admin
                String eventName = doc.getString(DatabaseConstants.evNameKey);
                String eventDesc = doc.getString(DatabaseConstants.evDescKey);
                String eventLocName = doc.getString(DatabaseConstants.evLocNameKey);
                String eventLocId = doc.getString(DatabaseConstants.evLocIdKey);
                String eventRest = doc.getString(DatabaseConstants.evRestricKey);
                String eventStart = doc.getString(DatabaseConstants.evStartKey);
                String eventEnd = doc.getString(DatabaseConstants.evEndKey);
                Integer eventMaxAttendees;
                if (doc.contains(DatabaseConstants.evAttendeeLimitKey)) {
                    eventMaxAttendees = doc.getLong(DatabaseConstants.evAttendeeLimitKey).intValue();
                } else {
                    eventMaxAttendees = -1;
                }
                String posterId = doc.getString(DatabaseConstants.evPosterKey);
                //Bitmap bitmap = ImgHandler.base64ToBitmap(posterId);

                eventIdList.add(doc.getId());

                Log.d("DEBUG", eventName + "outside");
                Event event = new Event(eventName, eventDesc, eventLocName, eventLocId, eventStart, eventEnd, eventRest, null, admin, eventMaxAttendees);
                eventList.add(event);

                getImage(posterId, event);
                eventArrayAdapter.notifyDataSetChanged();


            }


        });


    }


    //listen for the clickable items
    /**
     * addListeners:
     *  - create eventArrayAdapter and set it as the adapter for the recycler view
     *  - keeps track of position when event is clicked
     */
    public void addListeners() {
        eventArrayAdapter = new AdminEventArrayAdapter(getContext(), eventList, eventIdList, (position, eventId) -> buttonClickAction(eventList.get(position), eventIdList.get(position)));
        eventView.setAdapter(eventArrayAdapter);
    }

    /**
     * buttonClickAction:
     *  - when event button is clicked, it will send specific data to AdminManageEvent page
     *  - includes ability to move forward and backwards to different pages
     * @param event : pass the specific user data
     */

    private void buttonClickAction(Event event, String eventId) {
        //When you click on the buttonClickAction, it will link the position and take you
        //to the manage profile that fills in info
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, AdminManageEvent.newInstance(event, eventId))
                .addToBackStack(null).commit();
    }

    /**
     * getImage():
     * @param posterId : the document id of the image that needs to be attached to this event
     * @param event : the particular event
     *   - takes in the posterId and event so that it can get the image field value from the
     *     image collection, get the bitmap, and then set the image as the poster
     *   - double checks there is no null image, if there is it will set a blank poster for the event
     */
    private void getImage(String posterId, Event event){
        imgReference.document(posterId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String img = documentSnapshot.getString("image");

                if(img != null){
                    Bitmap bitmap = ImgHandler.base64ToBitmap(img);
                    event.setPoster(bitmap);
                }

                eventArrayAdapter.notifyDataSetChanged();

            }
        });
    }
}
