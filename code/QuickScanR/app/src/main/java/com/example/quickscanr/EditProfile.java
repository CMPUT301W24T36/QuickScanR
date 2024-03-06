/**
 * This file acts as the fragment for the edit profile page.
 */

package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.quickscanr.Profile;
import com.example.quickscanr.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the fragment for the edit profile page.
 */
public class EditProfile extends Fragment {

    private static final String USERKEY = "user";

    private User user;

    TextInputEditText nameField;
    TextInputEditText numberField;
    TextInputEditText emailField;
    TextInputEditText accountType;

    FirebaseFirestore db;
    CollectionReference usersRef;

    public EditProfile() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user User that the edit profile page is for
     * @return A new instance of fragment EditProfile.
     */
    public static EditProfile newInstance(User user) {
        EditProfile fragment = new EditProfile();
        Bundle args = new Bundle();
        args.putSerializable(USERKEY, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection(DatabaseConstants.usersColName);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USERKEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_page, container, false);
        nameField = v.findViewById(R.id.edit_profile_name);
        numberField = v.findViewById(R.id.edit_profile_phone);
        emailField = v.findViewById(R.id.edit_profile_email);
        accountType = v.findViewById(R.id.edit_profile_account_type);
        populatePage(v);
        setListeners(v);
        return v;
    }

    /**
     * adds listeners for the save and cancel button
     * @param v the edit profile view
     */
    private void setListeners(View v) {
        Button saveBtn = v.findViewById(R.id.save_profile_btn);
        DocumentReference userDocRef = usersRef.document(MainActivity.user.getUserId());

        // save the user and update the database
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString();
                String phone = numberField.getText().toString();
                String email = emailField.getText().toString();
                Integer type = UserType.valueOf(accountType.getText().toString());
                if (!user.isErrors(nameField)) {
                    user.setName(name);
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
//                    user.setUserType(type);   TODO: allow users to change their user type
                    MainActivity.updateUser(user);
                    Map<String, Object> data = new HashMap<>();
                    data.put(DatabaseConstants.userFullNameKey, name);
                    data.put(DatabaseConstants.userPhoneKey, phone);
                    data.put(DatabaseConstants.userEmailKey, email);
                    data.put(DatabaseConstants.userTypeKey, MainActivity.user.getUserType());     // TODO: allow users to change their user type
                    userDocRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            getParentFragmentManager().beginTransaction().replace(R.id.content_main, Profile.newInstance(MainActivity.user))
                                    .addToBackStack(null).commit();
                        }
                    });

                }
            }
        });
        // doesn't save anything when you click cancel
        Button cancelBtn = v.findViewById(R.id.cancel_save_profile);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.content_main, Profile.newInstance(MainActivity.user))
                        .addToBackStack(null).commit();
            }
        });
    }

    /**
     * populates the name, phone number, email, and user type fields
     * @param v the edit profile view
     */
    private void populatePage(View v) {
        nameField.setText(user.getName());
        numberField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());
        accountType.setText(UserType.getString(user.getUserType()));
    }
}