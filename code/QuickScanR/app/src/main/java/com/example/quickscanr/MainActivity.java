/**
 * This file acts as the main hub when a user opens the app.
 */

package com.example.quickscanr;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;


/**
 * This is the main hub when a user opens the app. Depending on
 * the type of user you are, it will show a different home page.
 */
public class MainActivity extends AppCompatActivity {

    static User user;
    FirebaseFirestore db;
    CollectionReference usersRef;
    String fcmToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this); // This is required for firebase cloud messaging

        // Request for permissions for notification
        final ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // FCM SDK (and your app) can post notifications.
                    } else {
                        // TODO: Inform user that that your app will not show notifications.
                    }
                });

        // This asks for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }


        // Gets the FCM token of the user.
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("PushNotification", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String fcmToken = task.getResult();
                        Log.d("FCM", "FCM Token: " + fcmToken);

                        updateFCMTokenForCurrentUser(fcmToken);
                    }
                });

        // Subscribe to announcements
        // TODO: Subscribe each user to a specific announcement (more code) / stop notifications if they don't meet a condition (i.e. not signed up, opt this way if possible)
        FirebaseMessaging.getInstance().subscribeToTopic("announcements")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to announcements!";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("PushNotif", msg);
                    }
                });


        Intent intent = getIntent();
        boolean backFromCheckInMap = intent.getBooleanExtra("backFromCheckInMap", false);

        if (backFromCheckInMap) {
            Event event = (Event) intent.getSerializableExtra("event");
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, EventDashboard.newInstance(event))
                    .addToBackStack(null).commit();
            return;
        }

        // Check if camera permission has been granted
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 100);
//        } else {
        // If permission already been granted
        initializeApp();
//        }
    }

    /* Stores the FCM token for the current user in Firestore.
     *
     * @param token The FCM token to store.
     */
    private void updateFCMTokenForCurrentUser(String token) {
        String userId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, Object> updates = new HashMap<>();
        updates.put("fcmToken", token);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("FCM", "FCM token updated for user: " + userId))
                .addOnFailureListener(e -> Log.e("FCM", "Error updating FCM token for user: " + userId, e));
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
                        String imageID = document.getString(DatabaseConstants.userImageKey);
                        user.setUserId(userId);
                        user.setImageID(imageID,false);
                        showHome(user.getUserType());
                    } else {
                        // if new user, then add them to the database
                        user = new User("New User", "", "", UserType.ATTENDEE);
                        user.setHomepage("");
                        user.setGeoLoc(false);
                        user.setUserId(userId);
                        user.setFcmToken(fcmToken);
                        Map<String, Object> data = new HashMap<>();
                        data.put(DatabaseConstants.userFullNameKey, user.getName());
                        data.put(DatabaseConstants.userHomePageKey, user.getHomepage());
                        data.put(DatabaseConstants.userPhoneKey, user.getPhoneNumber());
                        data.put(DatabaseConstants.userEmailKey, user.getEmail());
                        data.put(DatabaseConstants.userTypeKey, user.getUserType());
                        data.put(DatabaseConstants.userGeoLocKey, user.getGeoLoc());
                        data.put(DatabaseConstants.userCheckedEventsKey, new ArrayList<String>());
                        data.put(DatabaseConstants.userSignedUpEventsKey, new ArrayList<String>());
                        data.put(DatabaseConstants.userImageKey, DatabaseConstants.userDefaultImageID);
                        data.put(DatabaseConstants.userFcmToken, user.getFcmToken()); // For push notifications
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