package com.example.quickscanr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AdminManageEvents
 * - allows admin to view more info about the user and also delete the user
 *
 * Resources used for alert dialog for seeing poster:
 * https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
 * https://stackoverflow.com/questions/6276501/how-to-put-an-image-in-an-alertdialog-android
 * https://stackoverflow.com/questions/27520967/how-to-change-the-colour-of-positive-and-negative-button-in-custom-alert-dialog */
public class AdminManageEvent extends InnerPageFragment{
    private Event event;
    private String event_id;

    Chip seePoster;

    private ArrayList<String> posterIds;


    Button deleteEvents;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference imgRef;
    private CollectionReference announceRef;



    public static String EVENTS_COLLECTION = "events";
    public static String USERS_COLLECTION = "users";

    public static String IMAGES_COLLECTION = "images";
    public static String ANNOUNCE_COLLECTION = "announcements";




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
     *       - deletes events from users signed up lists
     *       - deletes event posters in images
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_manage_event, container, false);
        //go back to events list when clicked
        addButtonListeners(getActivity(), v, new AdminEventsList());
        populateInfo(v);

        deleteEvents = v.findViewById(R.id.delete_btn);

        seePoster = v.findViewById(R.id.manage_upload);


        posterIds = new ArrayList<>();
        //set up the database
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection(EVENTS_COLLECTION);

        usersRef = db.collection(USERS_COLLECTION);

        imgRef = db.collection(IMAGES_COLLECTION);

        announceRef = db.collection(ANNOUNCE_COLLECTION);

        seePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "HIIIII THEREE ");

                //get the image id and the bitmap of the image so you can do directly to the image
                eventsRef.document(event_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String poster = documentSnapshot.getString(DatabaseConstants.evPosterKey);
                        Log.d("DEBUG", "still THEREE ");

                        imgRef.document(poster).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //collect the bitmap and imageid
                                Log.d("DEBUG", "HIIIII now ");

                                String img = documentSnapshot.getString(DatabaseConstants.imgDataKey);
                                Bitmap bitmap = ImgHandler.base64ToBitmap(img);

                                //need image to display
                                ImageView imageView = new ImageView(getContext());
                                imageView.setImageBitmap(bitmap);
                                Log.d("DEBUG", "problem ");

                                //create alert dialog and then show the image in the large box
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setView(imageView).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                //change the colour of the text so its black so we can see it
                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                    }
                                });

                                alertDialog.show();
                                alertDialog.getWindow().setLayout(900, 1000);



                            }
                        });
                    }
                });
            }
        });
        deleteEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventsRef.document(event_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String poster = documentSnapshot.getString(DatabaseConstants.evPosterKey);
                        Log.d("DEBUG", poster);

                        //delete event poster (except default)
                        if(!poster.equals("default")){
                            Log.d("DEBUG", "OK TO DELETE");
                            imgRef.document(poster).delete();
                        } else{
                            Log.d("DEBUG", "no deleting default images");
                        }



                        eventsRef.document(event_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //match
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

                                announceRef.whereEqualTo("eventId", event_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                            String id = doc.getId();
                                            Log.d("DEBUG", id + "DELETE");
                                            doc.getReference().delete();
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

                    }
                });




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
        TextInputEditText maxAttendees = v.findViewById(R.id.manage_max_attendees);


        name.setText(event.getName());
        descrption.setText(event.getDescription());
        location.setText(event.getLocationName());
        starts.setText(event.getStart());
        ends.setText(event.getEnd());
        if (event.getMaxAttendees() == -1) {
            maxAttendees.setText("No Limit");
        } else {
            maxAttendees.setText(String.valueOf(event.getMaxAttendees()));
        }

    }



}
