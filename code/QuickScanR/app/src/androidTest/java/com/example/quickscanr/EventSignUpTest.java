package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static junit.framework.TestCase.assertEquals;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * tests for sign up functionality
 * referenced https://stackoverflow.com/questions/34439072/espresso-click-on-the-button-of-the-dialog
 *      for UIAutomator allow permissions idea
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventSignUpTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private static boolean userSet = false;
    private static String testEventId;
    private static FirebaseFirestore db;
    private static CollectionReference eventsRef;
    private static CollectionReference usersRef;
    private static String eventName;

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
     * wait for db to setup and set the user type of attendee
     */
    @Before
    public void setUp() {
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
            userSet = true;
            db = FirebaseFirestore.getInstance();
            eventsRef = db.collection(DatabaseConstants.eventColName);
            usersRef = db.collection(DatabaseConstants.usersColName);

        }
        try {
            Thread.sleep(1000L);    // give 1 second between tests to be safe
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * if an event was added for testing, remove it
     * also remove it from the signedUp field for the user
     */
    @After
    public void tearDown() {
        if (testEventId != null) {
            usersRef.document(MainActivity.user.getUserId()).update(DatabaseConstants.userSignedUpEventsKey, FieldValue.arrayRemove(testEventId));
            eventsRef.document(testEventId).delete();
            testEventId = null;
        }
    }

    /**
     * add a test event for testing
     */
    public void addTestEvent(Integer maxAttendees) {
        eventName = String.format("Test Event (%s)", System.currentTimeMillis());
        // test event data
        Map<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.evNameKey, eventName);
        data.put(DatabaseConstants.evDescKey, "Event Description");
        data.put(DatabaseConstants.evLocIdKey, "ChIJI__egEUioFMRXRX2SgygH0E");  // place id of Edmonton
        data.put(DatabaseConstants.evLocNameKey, "Edmonton");
        data.put(DatabaseConstants.evStartKey, "26-03-2024");
        data.put(DatabaseConstants.evEndKey, "26-03-2024");
        data.put("maxAttendees", maxAttendees);
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
     * go to attendee event list
     * set filter to all
     * select the event in the list that was added for testing purposes
     * sign up for the event
     * check that event was actually signed up for
     */
    @Test
    public void testSignUp() {
        addTestEvent(-1);

        // go to event details page for newly added event
        onView(withId(R.id.nav_a_events_btn)).perform(click());
        onView(withId(R.id.atd_ev_list_filter)).perform(click());
        onView(withText(AttendeeEventList.Filters.ALL.getLabel())).perform(click());

        // wait for page to update
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true).className("android.widget.ScrollView"));
        scroll.setAsVerticalList();

        UiSelector selector = new UiSelector().text(eventName);
        try {
            scroll.scrollIntoView(selector);
            UiObject eventToClick = device.findObject(selector);
            if (eventToClick.exists()) {
                eventToClick.click();
            }
        } catch (Exception e) {}

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // sign up for event
        UiObject signUpButton = device.findObject(new UiSelector()
                .className("android.widget.Button")
                .textContains("Sign Up"));
        try {
            signUpButton.click();
        } catch (Exception ignored) {}
        UiObject okButton = device.findObject(new UiSelector()
                .className("android.widget.Button")
                .textContains("Ok"));
        try {
            okButton.click();
        } catch (Exception ignored) {}

        final boolean[] eventInSignedUp = {false};
        final boolean[] userInSignedUpUsers = {false};

        // check if user's signed up list got updated
        usersRef.document(MainActivity.user.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        ArrayList<String> signedUp = (ArrayList<String>) doc.get(DatabaseConstants.userSignedUpEventsKey);
                        if (signedUp.contains(testEventId)) {
                            eventInSignedUp[0] = true;
                        }
                    }
                }
            }
        });

        // check if event's signed up users list got update
        eventsRef.document(testEventId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        ArrayList<String> signedUpUsers = (ArrayList<String>) doc.get(DatabaseConstants.evSignedUpUsersKey);
                        if (signedUpUsers.contains(MainActivity.user.getUserId())) {
                            userInSignedUpUsers[0] = true;
                        }
                    }
                }
            }
        });

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(eventInSignedUp[0], userInSignedUpUsers[0]);   // check that the event id is in the user's signedUp field
        assertEquals(eventInSignedUp[0], true);     // check that the user id is in the event's signedUpUsers field

    }

    /**
     * go to attendee event list
     * set filter to all
     * select the event in the list that was added for testing purposes
     * sign up for the event
     * check that sign up was unsuccessful
     */
    @Test
    public void testSignUpLimit() {
        addTestEvent(0);

        // go to event details page for newly added event
        onView(withId(R.id.nav_a_events_btn)).perform(click());
        onView(withId(R.id.atd_ev_list_filter)).perform(click());
        onView(withText(AttendeeEventList.Filters.ALL.getLabel())).perform(click());

        // wait for page to update
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true).className("android.widget.ScrollView"));
        scroll.setAsVerticalList();

        UiSelector selector = new UiSelector().text(eventName);
        try {
            scroll.scrollIntoView(selector);
            UiObject eventToClick = device.findObject(selector);
            if (eventToClick.exists()) {
                eventToClick.click();
            }
        } catch (Exception e) {}

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // sign up for event
        UiObject signUpButton = device.findObject(new UiSelector()
                .className("android.widget.Button")
                .textContains("Sign Up"));
        try {
            signUpButton.click();
        } catch (Exception ignored) {}

        final boolean[] eventInSignedUp = {false};
        final boolean[] userInSignedUpUsers = {false};

        // check if user's signed up list got updated
        usersRef.document(MainActivity.user.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        ArrayList<String> signedUp = (ArrayList<String>) doc.get(DatabaseConstants.userSignedUpEventsKey);
                        if (signedUp.contains(testEventId)) {
                            eventInSignedUp[0] = true;
                        }
                    }
                }
            }
        });

        // check if event's signed up users list got update
        eventsRef.document(testEventId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        ArrayList<String> signedUpUsers = (ArrayList<String>) doc.get(DatabaseConstants.evSignedUpUsersKey);
                        if (signedUpUsers.contains(MainActivity.user.getUserId())) {
                            userInSignedUpUsers[0] = true;
                        }
                    }
                }
            }
        });

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(eventInSignedUp[0], userInSignedUpUsers[0]);   // check that the event id is not in the user's signedUp field
        assertEquals(eventInSignedUp[0], false);     // check that the user id is not in the event's signedUpUsers field

    }
}
