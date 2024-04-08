package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.Matchers.allOf;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class AnnouncementTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static boolean userSet = false;
    private String testEventId = "FakeEventWithCustomID";



    /**
     * To delete the tested announcement, signed up event for user, & test event
     */

    @After
    public void tearDown() {

        db.collection(DatabaseConstants.eventColName).document(testEventId).delete(); // delete the test event made

        // Get a reference to the user document in Firestore
        DocumentReference userRef = db.collection("users").document(MainActivity.user.getUserId());

        // Update the user document to delete the signed-up event
        userRef.update("signedUp", FieldValue.arrayRemove(testEventId));

        // Iterate through announcements and delete the test announcement
        db.collection("announcements")
                .whereEqualTo("eventId", testEventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the query results
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the reference to the document and delete it
                            db.collection("announcements")
                                    .document(documentSnapshot.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("PushNotif", "DocumentSnapshot successfully deleted!");
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PushNotif", "Error getting documents", e);
                    }
                });
    }


    /**
     * Uses UIAutomator to allow permissions on startup
     */
    private static void allowPermissionsIfNeeded() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject allowButton = device.findObject(new UiSelector()
                .className("android.widget.Button")
                .textContains("Allow"));

        // wait for 1000 ms to see if appears
        if (allowButton.waitForExists(1000)) {
            try {
                allowButton.click();
            } catch (Exception e) {
                Log.d("PERMS", "Failed to allow permissions for testing");
            }
        }
    }

    /**
     * Be at the right state
     */
    @Before
    public void setUp() {
        // only setup once
        if (!userSet) {
            allowPermissionsIfNeeded();
            try {
                Thread.sleep(5000L);    // let database setup
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // go to profile
            try {
                onView(withId(R.id.nav_ad_profile_btn)).perform(click());
            } catch (Exception e) {
                try {
                    onView(withId(R.id.nav_o_profile_btn)).perform(click());
                } catch (Exception e1) {
                    try {
                        onView(withId(R.id.nav_a_profile_btn)).perform(click());
                    } catch (Exception e2) {}
                }
            }
            // edit current user type to be organizer
            onView(withId(R.id.user_edit_profile)).perform(click());
            onView(withId(R.id.edit_profile_usertype)).perform(click());
            onView(withText(UserType.getString(UserType.ORGANIZER))).perform(click());
            onView(withId(R.id.save_profile_btn)).perform(click());
            userSet = true;
        }
        try {
            Thread.sleep(1000L);    // give 1 second between tests to be safe
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        addTestEvent();


    }

    /**
     * add a test event
     */
    public void addTestEvent() {

        // Test event data
        Map<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.evNameKey, "Test Event For Push Notification");
        data.put(DatabaseConstants.evDescKey, "Event Description");
        data.put(DatabaseConstants.evLocNameKey, "Location");
        data.put(DatabaseConstants.evStartKey, "26-03-2024");
        data.put(DatabaseConstants.evEndKey, "26-03-2024");
        data.put(DatabaseConstants.evRestricKey, "Restrictions");
        data.put(DatabaseConstants.evTimestampKey, System.currentTimeMillis());
        data.put(DatabaseConstants.evPosterKey, "default");
        data.put(DatabaseConstants.evOwnerKey, MainActivity.user.getUserId());

        // Get reference to the document with the custom ID
        DocumentReference eventRef = db.collection(DatabaseConstants.eventColName).document("FakeEventWithCustomID");

        // Set the document with the data
        eventRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("PushNotif", "Event added successfully with custom ID: " + "FakeEventWithCustomID");
                } else {
                    Log.e("PushNotif", "Failed to add event with custom ID", task.getException());
                }
            }
        });
    }


    /**
     * This function adds the test event for the user to help with testing
     * @param eventId the test event
     * @param userId the current user
     */
    private void addSignedUpEventToUser(String eventId, String userId) {
        // Get a reference to the user document in Firestore
        DocumentReference userRef = db.collection("users").document(userId);

        // Update the user document to include the signed-up event
        userRef.update("signedUp", FieldValue.arrayUnion(eventId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("PushNotificationTest", "Signed-up event added to user document successfully");
                        } else {
                            Log.e("PushNotificationTest", "Failed to add signed-up event to user document", task.getException());
                        }
                    }
                });
    }

    private void deleteAllEvents(String userId) {
        db.collection("events")
                .whereEqualTo("ownerID", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the query results
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the reference to the document and delete it
                            db.collection("events")
                                    .document(documentSnapshot.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("PushNotif", "DocumentSnapshot successfully deleted!");
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PushNotif", "Error getting documents", e);
                    }
                });
    }
    /**
     * This tests if the organizer can make an announcement and signed up attendees receive it
     * To mimic the organizer/attendee interaction: signed up the same event that same user made
     */

    @Test
    public void testAnnouncementInteraction() {
        addSignedUpEventToUser(testEventId, MainActivity.user.getUserId()); // Signing up to the event to see the announcement on their page

        // Go to homepage
        onView(withId(R.id.nav_o_announcements_btn)).perform(click());
        // Click on announcement trigger
        onView(withId(R.id.announcement_trigger)).perform(click());
        // Write stuff in the title and body
        onView(withId(R.id.an_title_edit_text)).perform(ViewActions.typeText("Title Test"));
        onView(withId(R.id.an_body_edit_text)).perform(ViewActions.typeText("Body Test"));

        onView(withId(R.id.annc_add_button)).perform(click());

        onView(withId(R.id.nav_o_profile_btn)).perform(click());

        // edit current user type to be attendee
        onView(withId(R.id.user_edit_profile)).perform(click());
        onView(withId(R.id.edit_profile_usertype)).perform(click());
        onView(withText(UserType.getString(UserType.ATTENDEE))).perform(click());
        onView(withId(R.id.save_profile_btn)).perform(click());

        onView(withId(R.id.nav_a_announcements_btn)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withText("Title Test")).check(matches(isDisplayed()));


    }


    /**
     * See if the user has no events, they can't announce
     */
    @Test
    public void testFailedAnnouncement() {

        deleteAllEvents(MainActivity.user.getUserId());

        onView(withId(R.id.nav_a_profile_btn)).perform(click());

        // edit current user type to be organizer
        onView(withId(R.id.user_edit_profile)).perform(click());
        onView(withId(R.id.edit_profile_usertype)).perform(click());
        onView(withText(UserType.getString(UserType.ORGANIZER))).perform(click());
        onView(withId(R.id.save_profile_btn)).perform(click());

        // Go to homepage
        onView(withId(R.id.nav_o_announcements_btn)).perform(click());
        // Click on announcement trigger
        onView(withId(R.id.announcement_trigger)).perform(click());
        // Write stuff in the title and body
        onView(withId(R.id.an_title_edit_text)).perform(ViewActions.typeText("Title Test"));
        onView(withId(R.id.an_body_edit_text)).perform(ViewActions.typeText("Body Test"));

        onView(withId(R.id.annc_add_button)).perform(click());

        onView(withText("You have no events to announce to!")).check(matches(isDisplayed()));


    }





}
