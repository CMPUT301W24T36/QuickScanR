package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeListTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    private static boolean userSet = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static String testEventId;
    User user;
    MainActivity mActivity;

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
        // set proper user type
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
            // edit current user type to be attendee
            onView(withId(R.id.user_edit_profile)).perform(click());
            onView(withId(R.id.edit_profile_usertype)).perform(click());
            onView(withText(UserType.getString(UserType.ATTENDEE))).perform(click());
            onView(withId(R.id.save_profile_btn)).perform(click());
            onView(withId(R.id.nav_a_announcements_btn)).perform(click());
            userSet = true;
        }

        // get fragment instance + go to page
        onView(withId(R.id.nav_a_profile_btn)).perform(click());
        onView(withId(R.id.user_edit_profile)).perform(click());
        scenario.getScenario().onActivity(activity -> {
            fragManager = activity.getSupportFragmentManager();
            fragManager.executePendingTransactions();
            EditProfile editFragment = (EditProfile) fragManager.findFragmentByTag("EDIT_PROFILE");
            assertNotNull(editFragment);
            editInstance = editFragment;
            user = activity.user;
            mActivity = activity;
        });

        // wait to ensure activity callback
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @After
    public void tearDown() {
        // Ensure testEventId is not null before attempting to delete the document
        if (testEventId != null) {
            // Delete test event from database
            db.collection("events").document(testEventId).delete();
        }
    }

    public void addTestEvent(final OnEventAddedListener listener) {
        // test event data
        Map<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.evNameKey, "Test Event");
        data.put(DatabaseConstants.evDescKey, "Event Description");
        data.put(DatabaseConstants.evLocNameKey, "Location");
        data.put(DatabaseConstants.evStartKey, "26-03-2024");
        data.put(DatabaseConstants.evEndKey, "26-03-2024");
        data.put(DatabaseConstants.evRestricKey, "");
        data.put(DatabaseConstants.evPosterKey, "default");
        data.put(DatabaseConstants.evOwnerKey, MainActivity.user.getUserId());

        db.collection(DatabaseConstants.eventColName).add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = task.getResult();
                            if (documentReference != null) {
                                String eventId = documentReference.getId();
                                // Notify the listener that the event has been added
                                listener.onEventAdded(eventId);
                            }
                        } else {
                            // Handle error
                            Log.e("AttendeeListTest", "Error adding test event", task.getException());
                        }
                    }
                });
    }

    @Test
    public void testAttendeeListAfterCheckIn() {
        // Simulate checking in an attendee
        checkInAttendee();

        // Verify that the attendee appears on the checked-in attendees list
        onView(withId(R.id.chkd_usr_text)).check(matches(withText("John Doe")));
        // Dynamically verify the count of timestamps
        onView(withId(R.id.chkd_usr_count)).check(matches(withText("2"))); // Assuming "2" is the count of timestamps
    }

    public void checkInAttendee() {
        // Simulate checking in an attendee
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", "John Doe");
        List<Long> timestamps = Arrays.asList(1712517089266L, 1712443722028L);
        attendeeData.put("timestamps", timestamps);

        db.collection("events").document(testEventId).collection("attendees").add(attendeeData);
    }

    public interface OnEventAddedListener {
        void onEventAdded(String eventId);
    }
}
