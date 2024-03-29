/**
 * This file acts as the main hub when a user opens the app.
 */

package com.example.quickscanr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        boolean backFromCheckInMap = intent.getBooleanExtra("backFromCheckInMap", false);

        if (backFromCheckInMap) {
            Event event = (Event) intent.getSerializableExtra("event");
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, EventDashboard.newInstance(event))
                    .addToBackStack(null).commit();
            return;
        }

        // Check if camera permission has been granted
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
//        } else {
            // If permission already been granted
            initializeApp();
//        }
    }

    /**
     * This initializes everything necessary and loads data for the app
     */
    private void initializeApp() {
        // Move your existing onCreate logic here, after permission check
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
                    } else {
                        // if new user, then add them to the database
                        user = new User("New User", "", "", UserType.ATTENDEE);
                        user.setHomepage("");
                        user.setGeoLoc(false);
                        Map<String, Object> data = new HashMap<>();
                        data.put(DatabaseConstants.userFullNameKey, user.getName());
                        data.put(DatabaseConstants.userHomePageKey, user.getHomepage());
                        data.put(DatabaseConstants.userPhoneKey, user.getPhoneNumber());
                        data.put(DatabaseConstants.userEmailKey, user.getEmail());
                        data.put(DatabaseConstants.userTypeKey, user.getUserType());
                        data.put(DatabaseConstants.userGeoLocKey, user.getGeoLoc());
                        db.collection(DatabaseConstants.usersColName).document(userId).set(data);
                        showHome(user.getUserType());
                    }
                } else {
                    Log.d("DEBUG", "Failed with: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission granted, continue with initialization
                initializeApp();
            } else {
                // If permission denied, show a toast
                Toast.makeText(this, "Camera permission is required to use the QR scanner feature.", Toast.LENGTH_LONG).show();
                initializeApp();
            }
        }
    }

    /**
     * shows a different home page depending on the type of user you are
     * Reference: https://stackoverflow.com/questions/7793576/switching-between-fragment-view
     * @param userType type of the user
     */
    public void showHome(Integer userType) {
        // Show the appropriate UI based on the user type
        if (userType == UserType.ATTENDEE) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new AttendeeHome())
                    .addToBackStack(null).commit();
        } else if (userType == UserType.ORGANIZER) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new OrganizerHome())
                    .addToBackStack(null).commit();
        } else if (userType == UserType.ADMIN) {
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