/**
 * This file acts as the fragment for the edit profile page.
 */

package com.example.quickscanr;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the fragment for the edit profile page.
 * @see User
 */
public class EditProfile extends Fragment {

    private static final String USERKEY = "user";

    private User user;

    TextInputEditText nameField;
    TextInputEditText homepageField;
    TextInputEditText numberField;
    TextInputEditText emailField;
    ImageView profPic;
    Spinner accountTypeField;
    Uri tempURI;
    boolean imgDeleted = false;

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

    /**
     * Called when creating the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection(DatabaseConstants.usersColName);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USERKEY);
        }
    }

    /**
     * Represents creating the EditProfile view and returning it
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view of the EditProfile
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_page, container, false);
        nameField = v.findViewById(R.id.edit_profile_name);
        homepageField = v.findViewById(R.id.edit_profile_homepage);
        numberField = v.findViewById(R.id.edit_profile_phone);
        emailField = v.findViewById(R.id.edit_profile_email);
        accountTypeField = v.findViewById(R.id.edit_profile_usertype);
        profPic = v.findViewById(R.id.profile_pic);
        populatePage(v);
        setListeners(v);

        // load pfp
        ProfileImage profileImage = new ProfileImage(getContext());
        profileImage.getProfileImage(getContext(), user.getUserId(), new ProfileImage.ProfileImageCallback() {
            @Override
            public void onImageReady(Bitmap image) {
                profPic.setImageBitmap(image);
            }
        });

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
                String homepage = homepageField.getText().toString();
                String phone = numberField.getText().toString();
                String email = emailField.getText().toString();
                Integer type = UserType.valueOf((String) accountTypeField.getSelectedItem());
                if (!user.isErrors(nameField)) {
                    user.setName(name);
                    user.setHomepage(homepage);
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
                    user.setUserType(type);
                    MainActivity.updateUser(user);
                    Map<String, Object> data = new HashMap<>();
                    data.put(DatabaseConstants.userFullNameKey, name);
                    data.put(DatabaseConstants.userHomePageKey, homepage);
                    data.put(DatabaseConstants.userPhoneKey, phone);
                    data.put(DatabaseConstants.userEmailKey, email);
                    data.put(DatabaseConstants.userTypeKey, type);
                    userDocRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            getParentFragmentManager().beginTransaction().replace(R.id.content_main, Profile.newInstance(MainActivity.user))
                                    .addToBackStack(null).commit();
                        }
                    });

                    // sync images with db
                    if (imgDeleted) {
                        user.setImageID(DatabaseConstants.userDefaultImageID, true);
                    }
                    if (tempURI != null) {
                        ImgHandler imgHandler = new ImgHandler(getContext());
                        imgHandler.uploadImage(tempURI, documentID -> user.setImageID(documentID, true));
                    }
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
        setupImageButtons(v);
    }

    /**
     * Sets up listeners for upload and delete buttons
     * @param view view of the fragment
     */
    private void setupImageButtons(View view) {
        //setup upload button
        ActivityResultLauncher<PickVisualMediaRequest> imgPicker =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        tempURI = uri;
                        Bitmap bmp = new ImgHandler(getContext()).uriToBitmap(uri);
                        imgDeleted = false;
                        profPic.setImageBitmap(bmp);
                    }
                });

        ImageButton pickButton = view.findViewById(R.id.upload_pfp_btn);
        pickButton.setOnClickListener(v -> imgPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(new ActivityResultContracts.PickVisualMedia.SingleMimeType("image/*"))
                .build()));

        // setup delete button
        ImageButton delButton = view.findViewById(R.id.remove_pfp_btn);
        delButton.setOnClickListener(v -> {
            imgDeleted = true;
            tempURI = null;
            ProfileImage profileImage = new ProfileImage(getContext());
            profPic.setImageBitmap(profileImage.createProfileImage(getContext(), "x",200,200));
        });
    }

    /**
     * populates the name, phone number, email, and user type fields
     * @param v the edit profile view
     */
    private void populatePage(View v) {
        nameField.setText(user.getName());
        homepageField.setText(user.getHomepage());
        numberField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());

        ArrayList<String> userTypes = new ArrayList<>();
        userTypes.add(UserType.getString(UserType.ATTENDEE));
        userTypes.add(UserType.getString(UserType.ORGANIZER));
        userTypes.add(UserType.getString(UserType.ADMIN));

        ArrayAdapter<String> userTypesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, userTypes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                return view;
            }
        };;

        userTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountTypeField.setAdapter(userTypesAdapter);
        accountTypeField.setSelection(MainActivity.user.getUserType());
    }
}