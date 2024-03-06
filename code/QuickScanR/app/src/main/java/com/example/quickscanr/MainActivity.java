/**
 * This file acts as the main hub when a user opens the app.
 */

package com.example.quickscanr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the main hub when a user opens the app. Depending on
 * the type of user you are, it will show a different home page.
 */
public class MainActivity extends AppCompatActivity {

    static User user;
    FirebaseFirestore db;
    CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection(DatabaseConstants.usersColName);
        DocumentReference userDocRef = usersRef.document(userId);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(User.class);   // user exists already
                        user.setUserId(userId);
                        showHome(user.getUserType());
                    }
                    else {
                        String name = "New User";   // if new user, then add them to the database and return it
                        String blank = "";
                        Map<String, Object> data = new HashMap<>();
                        data.put(DatabaseConstants.userFullNameKey, name);
                        data.put(DatabaseConstants.userHomePageKey, blank);
                        data.put(DatabaseConstants.userPhoneKey, blank);
                        data.put(DatabaseConstants.userEmailKey, blank);
                        data.put(DatabaseConstants.userTypeKey, UserType.ATTENDEE);
                        db.collection(DatabaseConstants.usersColName).document(userId).set(data);
                        showHome(UserType.ATTENDEE);
                    }
                }
                else {
                    Log.d("DEBUG", "Failed with: ", task.getException());
                }
            }
        });
        setContentView(R.layout.activity_main);
    }

    /**
     * shows a different home page depending on the type of user you are
     * Reference: https://stackoverflow.com/questions/7793576/switching-between-fragment-view
     * @param userType type of the user
     */
    public void showHome(Integer userType) {
        if (userType == UserType.ATTENDEE) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new AttendeeHome())
                    .addToBackStack(null).commit();
        }
        else if (userType == UserType.ORGANIZER) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new OrganizerHome())
                    .addToBackStack(null).commit();
        }
        else if (userType == UserType.ADMIN) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, Profile.newInstance(user))
                    .addToBackStack(null).commit();
        }
    }

    /**
     * used when editing your profile. updates your user information
     * in MainActivity.
     * @param newUserInfo the User object containing the new user information
     */
    public static void updateUser(User newUserInfo) {
        user = newUserInfo;
    }
}