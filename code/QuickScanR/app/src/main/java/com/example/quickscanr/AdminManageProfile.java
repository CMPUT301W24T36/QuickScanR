package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

/**
 * AdminManageProfile
 * - allows admin to view more info about the user and also delete the user
 */
public class AdminManageProfile extends InnerPageFragment{
    private User user;
    Button deleteProfile;

    private FirebaseFirestore db;
    private CollectionReference profiles;
    public static String USER_COLLECTION = "users";

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
        View v = inflater.inflate(R.layout.admin_manage_profile, container, false);

        //go back and forth to profile list when clicked
        addButtonListeners(getActivity(), v);
        populateInfo(v);

        //set up the database
        db = FirebaseFirestore.getInstance();
        profiles = db.collection(USER_COLLECTION);

        deleteProfile = v.findViewById(R.id.delete_btn);

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // using both name and phone to get document id and then delete from database
                String username = user.getName();
                String userphone = user.getPhoneNumber();

                //match the phone and the name to the document id and then delete it
                profiles.whereEqualTo("name", username).whereEqualTo("phoneNumber", userphone)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                    String docId = doc.getId();
                                    profiles.document(docId).delete();

                                    Log.d("DEBUG", docId);
                                    //go back to the previous page
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
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
    public static AdminManageProfile newInstance(User user) {
        AdminManageProfile fragment = new AdminManageProfile(user);
        Bundle args = new Bundle();

        args.putSerializable("user", user);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     *populateInfo:
     *  - populate fields with extra user information for admin to see
     * @param v: view that has the fields with the same data that we will populate the fields with
     */
    public void populateInfo(View v){

        //pull the data and then display information about the user
        TextInputEditText name = v.findViewById(R.id.manage_name);
        TextInputEditText phone = v.findViewById(R.id.manage_phone);
        TextInputEditText email = v.findViewById(R.id.manage_email);


        name.setText(user.getName());
        phone.setText(user.getPhoneNumber());
        email.setText(user.getEmail());
    }

}
