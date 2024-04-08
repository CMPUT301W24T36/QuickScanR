package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static junit.framework.TestCase.assertEquals;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tests for add event functionality
 * referenced https://stackoverflow.com/questions/34439072/espresso-click-on-the-button-of-the-dialog
 *      for UIAutomator allow permissions idea
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEventTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private static boolean userSet = false;
    private static String testEventId;
    private static FirebaseFirestore db;
    private static CollectionReference eventsRef;

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
     * wait for db to setup and set the user type of organizer
     */
    @Before
    public void setUp() {
        allowPermissionsIfNeeded();
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
            db = FirebaseFirestore.getInstance();
            eventsRef = db.collection(DatabaseConstants.eventColName);
        }
        try {
            Thread.sleep(1000L);    // give 1 second between tests to be safe
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * if an event was added for testing, remove it
     */
    @After
    public void tearDown() {
        if (testEventId != null) {
            eventsRef.document(testEventId).delete();
            testEventId = null;
        }
    }

    /**
     * tests add event functionality
     * checks that event got added to the database
     */
    @Test
    public void testAddEvent() {
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentDateStr = format.format(currentDate);
        String eventName = String.format("Test Event(%s)", currentDateStr);

        onView(withId(R.id.nav_o_add_event_btn)).perform(click());
        onView(withId(R.id.evadd_txt_name)).perform(clearText(), typeText(eventName));
        onView(withId(R.id.evadd_txt_desc)).perform(clearText(), typeText("Test Event Description"));
        onView(withId(R.id.evadd_txt_loc)).perform(clearText(), typeText("Edmonton"));

        try {
            Thread.sleep(1000L);    // wait a second for the autocomplete view to show the options
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withText("Edmonton, AB, Canada")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(R.id.evadd_txt_start)).perform(replaceText(currentDateStr));
        onView(withId(R.id.evadd_txt_end)).perform(replaceText(currentDateStr));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.evadd_btn_add)).perform(click());
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        eventsRef.whereEqualTo(DatabaseConstants.evNameKey, eventName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        assertEquals(document.exists(),true);    // if doc exists that means it was added successfully
                        testEventId = document.getId();
                    }
                }
            }
        });

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
