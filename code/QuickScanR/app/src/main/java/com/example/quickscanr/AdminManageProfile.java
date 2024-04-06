package com.example.quickscanr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AdminManageProfile
 * - allows admin to view more info about the user and also delete the user
 */
public class AdminManageProfile extends InnerPageFragment{
    private User user;

    private String user_id;

    Button deleteProfile;

    private FirebaseFirestore db;
    private CollectionReference profiles;

    private CollectionReference eventRef;
    private CollectionReference attendeesRef;
    private CollectionReference announceRef;
    private CollectionReference imgRef;



    public static String USER_COLLECTION = "users";
    public static String EVENT_COLLECTION = "events";
    public static String ATTEN_COLLECTION = "attendees";
    public static String ANN_COLLECTION = "announcements";
    public static String IMAGE_COLLECTION = "images";





    public AdminManageProfile(User user) {
        this.user = user;
    }

    /**
     * onCreate:
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            user_id = getArguments().getString("userId");

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
     *       - goes through the events collection to remove users that were signed up for events
     *       - delete any image that user uploaded
     *       - removes all annoucements made by user
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_manage_profile, container, false);

        //go back and forth to profile list when clicked
        addButtonListeners(getActivity(), v, new AdminProfilesList());
        populateInfo(v);

        //set up the database
        db = FirebaseFirestore.getInstance();
        profiles = db.collection(USER_COLLECTION);

        eventRef = db.collection(EVENT_COLLECTION);

        announceRef = db.collection(ANN_COLLECTION);

        imgRef = db.collection(IMAGE_COLLECTION);



        deleteProfile = v.findViewById(R.id.delete_btn);

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profiles.document(user_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        eventRef.addSnapshotListener((value, error) -> {
                            if (error != null) {
                                Log.e("DEBUG: AEL", error.getMessage());
                                return;
                            }

                            if (value == null) {
                                return;
                            }

                            for(QueryDocumentSnapshot doc: value){
                                List<String> signedUp = (List<String>) doc.get("signedUpUsers");

                                if(signedUp != null && signedUp.contains(user_id)){
                                    signedUp.remove(user_id);
                                    eventRef.document(doc.getId()).update("signedUpUsers", FieldValue.arrayRemove(user_id));
                                }

                                CollectionReference attendRef = doc.getReference().collection("attendees");

                                attendRef.document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            DocumentReference delAttend = documentSnapshot.getReference();

                                            delAttend.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("DEBUG", "deleted" + doc.getId());
                                                }
                                            });
                                        }

                                    }
                                });
                            }

                        });

                        // go through the attendee's collection
                        CollectionReference attenRef = eventRef.document(user_id).collection(ATTEN_COLLECTION);
                        Log.d("DEBUG", "HIIIII");

                        attenRef.document(user_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DEBUG", "user delete from attnedees");
                            }
                        });

                        eventRef.whereEqualTo("ownerID", user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                    DocumentReference eventDoc = documentSnapshot.getReference();

                                    eventDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("DEBUG", "deleted created event");
                                        }
                                    });
                                }
                            }
                        });

                        announceRef.whereEqualTo("userID", user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                    String id = doc.getId();
                                    doc.getReference().delete();
                                    Log.d("DEBUG", id + "idk whats happening");
                                }

                            }
                        });

                        //remove any images that user uploaded
                        imgRef.whereEqualTo("user", user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                    if(!doc.getId().equals("default") || !doc.getId().equals("default_user")){
                                        doc.getReference().delete();
                                    }
                                    //Log.d("DEBUG", id + "delete uploaded images");
                                }
                            }
                        });


                        //go back to the previous page
                        AdminProfilesList adminProfilesList = new AdminProfilesList();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, adminProfilesList)
                                .addToBackStack(null).commit();

                    }
                });

            }
        });



        return v;
    }


    /**
     *AdminManageProfile:
     *  - new instance of a fragment with specific user data
     * @param user : specific data regarding a user
     * @return
     *  - fragment: a new instance that is initialized with new user data
     */
    public static AdminManageProfile newInstance(User user, String userId) {
        AdminManageProfile fragment = new AdminManageProfile(user);
        Bundle args = new Bundle();

        args.putSerializable("user", user);
        args.putString("userId", userId);

        fragment.setArguments(args);

        return fragment;
    }

    /**
     *populateInfo:
     *  - populate fields with extra user information for admin to see
     * @param v: view that has the fields with the same data that we will populate the fields with
     */
    public void populateInfo(View v) {

        //pull the data and then display information about the user
        TextInputEditText name = v.findViewById(R.id.manage_name);
        TextInputEditText phone = v.findViewById(R.id.manage_phone);
        TextInputEditText email = v.findViewById(R.id.manage_email);
        ImageView profileImageView = v.findViewById(R.id.manage_profile_pic);

        name.setText(user.getName());
        phone.setText(user.getPhoneNumber());
        email.setText(user.getEmail());

        new ProfileImage(getContext()).getProfileImage(getContext(), user.getUserId(), new ProfileImage.ProfileImageCallback() {
            @Override
            public void onImageReady(Bitmap image) {
                profileImageView.setImageBitmap(image);
            }
        });
    }
}
