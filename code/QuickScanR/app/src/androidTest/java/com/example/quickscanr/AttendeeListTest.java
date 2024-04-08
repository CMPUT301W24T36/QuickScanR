package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.util.Log;
import org.junit.Assert;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.annotation.NonNull;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains test cases to validate the functionality related to checked in
 *  attendee lists (tests name and appropriate count are updated after check-in.
 * @see MainActivity
 * @see UserType
 */

/**
 * 1. Reference for UIAutomator allow permissions idea:
 * https://stackoverflow.com/questions/34439072/espresso-click-on-the-button-of-the-dialog
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeListTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    private static boolean userSet = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String testEventId = "testEvent";

    /**
     * Uses UIAutomator to allow permissions on startup
     * REFERENCE 1
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
                    } catch (Exception e2) {
                    }
                }
            }
            // edit current user type to be organizer
            onView(withId(R.id.user_edit_profile)).perform(click());
            onView(withId(R.id.edit_profile_usertype)).perform(click());
            onView(withText(UserType.getString(UserType.ORGANIZER))).perform(click());
            onView(withId(R.id.save_profile_btn)).perform(click());
            userSet = true;
        }
        // wait to ensure activity callback
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        addTestEvent();

    }

    public void addTestEvent() {
        // Test event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", "Test Event For Attendee List");
        eventData.put("description", "Event Description");
        eventData.put("location", "Location");
        eventData.put("start_date", "26-03-2024");
        eventData.put("end_date", "26-03-2024");
        eventData.put("restrictions", "Restrictions");
        eventData.put("timestamp", System.currentTimeMillis());
        eventData.put("posterID", "default");
        eventData.put("ownerID", MainActivity.user.getUserId());
        eventData.put("LocationName", "Los Pollos Hermanos, Isleta Boulevard Southwest, Albuquerque, NM, USA");
        eventData.put("maxAttendees", -1);
        List<String> signedUpUsers = Arrays.asList("user123", "user222", "user333");
        eventData.put("signedUpUsers", signedUpUsers);

        DocumentReference eventRef = db.collection("events").document(testEventId);

        eventRef.set(eventData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("AttendeeListTest", "Event added successfully with custom ID: " + testEventId);
                        } else {
                            Log.e("AttendeeListTest", "Failed to add event with custom ID", task.getException());
                        }
                    }
                });
    }

    /**
     * Simulates checking in an attendee to a test event.
     * @param testEventId the ID of the test event
     */
    public void checkInAttendee(String testEventId) {
        // Simulate checking in an attendee
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", "John Doe");
        List<Long> timestamps = Arrays.asList(1712517089266L, 1712443722028L);
        attendeeData.put("timestamps", timestamps);

        db.collection("events")
                .document(testEventId)
                .collection("attendees")
                .add(attendeeData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("AttendeeListTest", "Attendee added successfully to event: " + testEventId);
                        } else {
                            Log.e("AttendeeListTest", "Failed to add attendee to event", task.getException());
                        }
                    }
                });
    }

    /**
     * Test method to verify attendee list after check-in.
     * Checks if attendees are properly listed after check-in process.
     * @throws UiObjectNotFoundException if UI object is not found during testing
     */
    @Test
    public void testAttendeeListAfterCheckIn() throws UiObjectNotFoundException {

        checkInAttendee(testEventId);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.nav_o_events_btn)).perform(click());

        onView(withId(R.id.organizer_event_list)).check(matches(isDisplayed()));

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiScrollable recyclerView = new UiScrollable(new UiSelector().className(RecyclerView.class.getName()));
        recyclerView.scrollTextIntoView("Test Event For Attendee List");

        UiObject eventToClick = device.findObject(new UiSelector().text("Test Event For Attendee List"));
        if (eventToClick.exists()) {
            try {
                eventToClick.click();
            } catch (UiObjectNotFoundException e) {
                // Handle exception if event is not clickable
                e.printStackTrace();
            }
        }

        // Check if the event dashboard page is displayed
        onView(withId(R.id.event_dashboard_page)).check(matches(isDisplayed()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Perform action to navigate to checked users list
        onView(withId(R.id.evdash_img_stat4)).perform(click());

        // Check if the checked users list is displayed
        onView(withId(R.id.chkd_usrs_list)).check(matches(isDisplayed()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Verify that the attendee appears on the checked-in attendees list
        onView(withId(R.id.chkd_usr_text)).check(matches(withText("John Doe")));
        onView(withId(R.id.chkd_usr_count)).check(matches(withText("2")));
    }


    /**
     * Tear down method to clean up after tests.
     * Deletes test event and related attendees from the database.
     */
    @After
    public void tearDown() {
        db.collection("events").document(testEventId).collection("attendees").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the query results
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the reference to the document and delete it
                            db.collection("events").document(testEventId).collection("attendees")
                                    .document(documentSnapshot.getId()).delete();
                            db.collection("events").document(testEventId)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("AttendeeListTest", "Attendee document deleted successfully");
                                            } else {
                                                Log.e("AttendeeListTest", "Failed to delete attendee document", task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AttendeeListTest", "Error getting attendee documents", e);
                    }
                });
    }

}

