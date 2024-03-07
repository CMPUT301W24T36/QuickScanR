package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * AdminProfilesList
 * - allows admin to view all the profiles and click on each one to get more information
 */
public class AdminProfilesList extends AdminFragment {

    RecyclerView profileView;
    ArrayList<User> profileList;
    AdminProfileArrayAdapter profileArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference usersReference;

    public static String users_COLLECTION = "users";

    public AdminProfilesList() {}

    public static AdminProfilesList newInstance(String param1, String param2) {
        AdminProfilesList fragment = new AdminProfilesList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_browse_profiles, container, false);
        addNavBarListeners(getActivity(), v);

       //DB LINKING
        db = FirebaseFirestore.getInstance();
        usersReference = db.collection(users_COLLECTION);


        profileView = v.findViewById(R.id.adm_profile_list);
        profileList = new ArrayList<>();


        addListeners();
        profileView.setLayoutManager(new LinearLayoutManager(getContext()));
        addSnapshotListenerForEvent();

        profileArrayAdapter.notifyDataSetChanged();

        return v;
    }

    //snapshot is for real time updates
    private void addSnapshotListenerForEvent() {

        usersReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            profileList.clear();
            for (QueryDocumentSnapshot doc: value) {
                User admin = new User("testing","testing","testing",2);

                //call from database constants is the other class already set up
                //figure out what to do for admin profile
                String name = doc.getString(DatabaseConstants.userFullNameKey);
                String home = doc.getString(DatabaseConstants.userHomePageKey);
                String phone = doc.getString(DatabaseConstants.userPhoneKey);
                String email = doc.getString(DatabaseConstants.userEmailKey);
                long type = doc.getLong(DatabaseConstants.userTypeKey);
                int userType = (int) type;

                Log.d("DEBUG", String.format("User (%s) fetched", name + " " + home + " " + phone + " "));
                Log.d("DEBUG", String.format("User (%s) fetched", userType));

                profileList.add(new User(name, phone, email, userType));
            }
            profileArrayAdapter.notifyDataSetChanged();

        });
    }

    //listen for the clickable items
    public void addListeners() {
        profileArrayAdapter = new AdminProfileArrayAdapter(getContext(), profileList, position -> buttonClickAction(profileList.get(position)));
        profileView.setAdapter(profileArrayAdapter);
    }


    //When you click on the buttonClickAction, it will link the position and take you
    //to the manage profile that fills in info
    private void buttonClickAction(User user) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, AdminManageProfile.newInstance(user))
                .addToBackStack(null).commit();
    }
}
