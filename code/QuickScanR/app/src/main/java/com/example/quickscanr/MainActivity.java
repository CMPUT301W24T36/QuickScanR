package com.example.quickscanr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

public class MainActivity extends AppCompatActivity {

    static User user;
    FirebaseFirestore db;
    CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if camera permission has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            // If permission already been granted
            initializeApp();
        }
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
                        // if new user, then add them to the database and return it
                        Map<String, Object> data = new HashMap<>();
                        data.put(DatabaseConstants.userFullNameKey, "New User");
                        data.put(DatabaseConstants.userHomePageKey, "");
                        data.put(DatabaseConstants.userPhoneKey, "");
                        data.put(DatabaseConstants.userEmailKey, "");
                        data.put(DatabaseConstants.userTypeKey, UserType.ATTENDEE);
                        db.collection(DatabaseConstants.usersColName).document(userId).set(data);
                        showHome(UserType.ATTENDEE);
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

    public static void updateUser(User newUserInfo) {
        user = newUserInfo;
    }
}