package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for attendee navigation.
 * Tests navigation between the 4 main pages.
 * referenced https://stackoverflow.com/questions/34439072/espresso-click-on-the-button-of-the-dialog
 *      for UIAutomator allow permissions idea
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private static boolean userSet = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static String testEventId;

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
     * make the current user an attendee
     */
    @Before
    public void setUp() {
        allowPermissionsIfNeeded();
        // only setup once
        if (!userSet) {
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
            userSet = true;
        }
        try {
            Thread.sleep(1000L);    // give 1 second between tests to be safe
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void tearDown() {
        if (testEventId != null) {
            db.collection(DatabaseConstants.eventColName).document(testEventId).delete();
            db.collection(DatabaseConstants.usersColName).document(MainActivity.user.getUserId()).update(DatabaseConstants.userSignedUpEventsKey, FieldValue.arrayRemove(testEventId));
            testEventId = null;
        }
    }

    /**
     * adding a current event that the user is signed up for
     */
    public void addTestEvent() {
        // test event data
        Map<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.evNameKey, "Test Event");
        data.put(DatabaseConstants.evDescKey, "Event Description");
        data.put(DatabaseConstants.evLocIdKey, "ChIJI__egEUioFMRXRX2SgygH0E");  // place id of Edmonton
        data.put(DatabaseConstants.evLocNameKey, "Edmonton");
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedCurrentDate = sdf.format(currentDate);
        data.put(DatabaseConstants.evStartKey, formattedCurrentDate);
        data.put(DatabaseConstants.evEndKey, formattedCurrentDate);
        data.put("maxAttendees", -1);
        data.put(DatabaseConstants.evTimestampKey, System.currentTimeMillis());
        ArrayList<String> signedUpUsers = new ArrayList<>();
        signedUpUsers.add(MainActivity.user.getUserId());
        data.put(DatabaseConstants.evSignedUpUsersKey, signedUpUsers);
        data.put(DatabaseConstants.evPosterKey, "default");     // default event poster
        data.put(DatabaseConstants.evOwnerKey, MainActivity.user.getUserId());

        db.collection(DatabaseConstants.eventColName).add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    if (documentReference != null) {
                        testEventId = documentReference.getId();
                        db.collection(DatabaseConstants.usersColName).document(MainActivity.user.getUserId()).update(DatabaseConstants.userSignedUpEventsKey, FieldValue.arrayUnion(testEventId));
                    }
                }
            }
        });
    }

    /**
     * go to the attendee home page
     * check that we're on the attendee home page
     */
    @Test
    public void testHomepageBtn() {
        onView(withId(R.id.nav_a_announcements_btn)).perform(click());
        onView(withId(R.id.attendee_home_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click the event list button and check that it's being showed
     */
    @Test
    public void testEventsBtn() {
        onView(withId(R.id.nav_a_events_btn)).perform(click());
        onView(withId(R.id.attendee_event_list)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click the camera button and check that the camera page is being showed
     */
    @Test
    public void testScanBtn(){
        onView(withId(R.id.nav_camera_btn)).perform(click());
        onView(withId(R.id.qr_scanning_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click on attendee event list
     * click on first item in attendee event list
     * check that event details page is being shown
     */
//    @Test
    public void testOpenEventDetails() {
        addTestEvent();
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.nav_a_events_btn)).perform(click());
        onView(withId(R.id.atnd_ev_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.event_details_page)).check(matches(ViewMatchers.isDisplayed()));
    }

}
