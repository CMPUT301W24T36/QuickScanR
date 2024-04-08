/**
 * This file is responsible for displaying the list of attendees who have signed-up to the event after scanning
 * displays attendee name by fetching realtime data from db
 */
package com.example.quickscanr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * fragment class for the list of signed up attendees for an event
 * @see SignedUpListAdapter
 */
public class SignedUpList extends InnerPageFragment {

    private static final String EVENTARG = "event";
    private Event event;
    FirebaseFirestore db;
    CollectionReference usersRef;
    private RecyclerView signedUpListView;
    private SignedUpListAdapter signedUpListAdapter;
    private ArrayList<ArrayList<String>> signedUpData = new ArrayList<>();

    /**
     * empty constructor for the fragment
     */
    public SignedUpList() {}

    /**
     * factory method to create a new instance of
     * this fragment based on a given event.
     *
     * @param event event that the signed up attendees list is for
     * @return A new instance of fragment SignedUpList.
     */
    public static SignedUpList newInstance(Event event) {
        SignedUpList fragment = new SignedUpList();
        Bundle args = new Bundle();
        args.putSerializable(EVENTARG, event);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when creating the fragment SignedUpList
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(EVENTARG);
        }

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection(DatabaseConstants.usersColName);
    }

    /**
     * To create the view relevant to SignedUpList
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view relevant to SignedUpList
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signed_up_list, container, false);

        signedUpListView = v.findViewById(R.id.signed_up_users_list);
        signedUpListView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        signedUpListAdapter = new SignedUpListAdapter(signedUpData, getContext());
        signedUpListView.setAdapter(signedUpListAdapter);

        addButtonListeners(v);
        showSignedUpAttendees();
        return v;
    }

    /**
     * update the recycler view to show the list of signed up attendees
     */
    public void showSignedUpAttendees() {
        usersRef.whereArrayContains(DatabaseConstants.userSignedUpEventsKey, event.getId()).addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.e("DEBUG: OEL", error.getMessage());
                return;
            }
            if (value == null) {
                return;
            }

            signedUpData.clear();
            for (QueryDocumentSnapshot doc: value) {
                String name = (String) doc.get(DatabaseConstants.userFullNameKey);
                String profilePicID = (String) doc.get(DatabaseConstants.userImageKey);

                ArrayList<String> userData = new ArrayList<>();     // each user has name and profile picture
                userData.add(name);
                userData.add(profilePicID);

                signedUpData.add(userData);
            }
            signedUpListAdapter.notifyDataSetChanged();

        });
    }

    public void addButtonListeners(View v) {
        MaterialButton backBtn = v.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference eventsRef = db.collection(DatabaseConstants.eventColName);
                eventsRef.document(event.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            ArrayList<String> signedUpUsers = (ArrayList<String>) doc.get(DatabaseConstants.evSignedUpUsersKey);
                            event.setSignedUp(signedUpUsers);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, EventDashboard.newInstance(event))
                                    .addToBackStack(null).commit();
                        }
                    }
                });
            }
        });
    }
}