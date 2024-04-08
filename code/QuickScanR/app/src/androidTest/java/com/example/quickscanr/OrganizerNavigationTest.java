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
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for organizer navigation.
 * Tests navigation between the 4 main pages.
 * referenced https://stackoverflow.com/questions/34439072/espresso-click-on-the-button-of-the-dialog
 *      for UIAutomator allow permissions idea
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerNavigationTest {

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
     * makes current user an organizer
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
     * removes a test event that was added
     */
    @After
    public void tearDown() {
        db.collection(DatabaseConstants.eventColName).document(testEventId).delete();
    }

    /**
     * add a test event for testing
     */
    public void addTestEvent() {
        // test event data
        Map<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.evNameKey, "Test Event");
        data.put(DatabaseConstants.evDescKey, "Event Description");
        data.put(DatabaseConstants.evLocIdKey, "ChIJI__egEUioFMRXRX2SgygH0E");  // place id of Edmonton
        data.put(DatabaseConstants.evLocNameKey, "Edmonton");
        data.put(DatabaseConstants.evStartKey, "26-03-2024");
        data.put(DatabaseConstants.evEndKey, "26-03-2024");
        data.put("maxAttendees", -1);
        data.put(DatabaseConstants.evTimestampKey, System.currentTimeMillis());
        data.put(DatabaseConstants.evSignedUpUsersKey, new ArrayList<String>());
        data.put(DatabaseConstants.evPosterKey, "default");     // default event poster
        data.put(DatabaseConstants.evOwnerKey, MainActivity.user.getUserId());

        db.collection(DatabaseConstants.eventColName).add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    if (documentReference != null) {
                        testEventId = documentReference.getId();
                    }
                }
            }
        });
    }

    /**
     * go to organizer home page
     * check that we're on the organizer home page
     */
    @Test
    public void testHomepageBtn() {
        onView(withId(R.id.nav_o_announcements_btn)).perform(click());
        onView(withId(R.id.organizer_home_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click the event list button and check that it's being showed
     */
    @Test
    public void testEventBtn() {
        onView(withId(R.id.nav_o_events_btn)).perform(click());
        onView(withId(R.id.organizer_event_list)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click the add event button and check that it's being showed
     */
    @Test
    public void testAddEventBtn(){
        onView(withId(R.id.nav_o_add_event_btn)).perform(click());
        onView(withId(R.id.add_event_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click on organizer event list
     * click on first item in the list
     * check that the event dashboard page is being shown
     */
    @Test
    public void testEventDashBoard() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.nav_o_events_btn)).perform(click());
        onView(withId(R.id.org_ev_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.event_dashboard_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * go to organizer event list
     * click on first item in the list
     * click on the # checked-in text
     * check that the checked-in attendees list is shown
     */
    @Test
    public void testCheckedInAttendeeListAccess() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.nav_o_events_btn)).perform(click());
        onView(withId(R.id.org_ev_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.evdash_txt_stat4)).perform(click());
        onView(withId(R.id.checked_in_attendees_list)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * go to organizer event list
     * click on first item in the list
     * click on the check-in button
     * check that the check in qr code is being shown
     */
//    @Test
    public void testCheckInQRAccess() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.nav_o_events_btn)).perform(click());
        onView(withId(R.id.org_ev_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.evdash_btn_checkin)).perform(click());
        onView(withId(R.id.check_in_qr_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * go to organizer event list
     * click on first item in the list
     * click on the qr code button (for the promotion qr code)
     * check that the promotion qr code is being shown
     */
//    @Test
    public void testPromotionInQRAccess() {
        onView(withId(R.id.nav_o_events_btn)).perform(click());
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.org_ev_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.evdash_btn_qrcode)).perform(click());
        onView(withId(R.id.promotional_qr_code_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * turn on geolocation if it isn't already on
     * go to organizer event list
     * click on first item in the list
     * click on map button
     * check that map page is being shown
     */
//    @Test
    public void testCheckInMapAccess() {
        boolean needsReset = false;

        // if geolocation not on then turn it on
        if (!MainActivity.user.getGeoLoc()) {
            needsReset = true;
            Map<String, Object> data = new HashMap<>();
            data.put(DatabaseConstants.userGeoLocKey, true);
            db.collection(DatabaseConstants.usersColName).document(MainActivity.user.getUserId()).update(data);
            MainActivity.user.setGeoLoc(true);
        }
        onView(withId(R.id.nav_o_events_btn)).perform(click());
        onView(withId(R.id.org_ev_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.evdash_btn_map)).perform(click());
        onView(withId(R.id.check_in_map_page)).check(matches(ViewMatchers.isDisplayed()));

        // if geolocation was originally off then reset it
        if (needsReset) {
            Map<String, Object> data = new HashMap<>();
            data.put(DatabaseConstants.userGeoLocKey, false);
            db.collection(DatabaseConstants.usersColName).document(MainActivity.user.getUserId()).update(data);
            MainActivity.user.setGeoLoc(false);
        }
    }

}
