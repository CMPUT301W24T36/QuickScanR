package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for scanning a variety of QR codes including invalid ones.
 *
 * 1. Reference for UIAutomator allow permissions idea:
 * https://stackoverflow.com/questions/34439072/espresso-click-on-the-button-of-the-dialog
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class QRScanTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
    private static boolean userSet = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragManager;
    ScanQR scanInstance;
    private static String testEventId;
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
        }

        addTestEvent();
        onView(withId(R.id.nav_camera_btn)).perform(click());
        scenario.getScenario().onActivity(activity -> {
            fragManager = activity.getSupportFragmentManager();
            fragManager.executePendingTransactions();
            ScanQR scanQRFragment = (ScanQR) fragManager.findFragmentByTag("SCANNER");
            assertNotNull(scanQRFragment);
            scanInstance = scanQRFragment;
        });

        try {
            Thread.sleep(500L);    // wait for activity callback
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Uses UIAutomator to allow permissions on startup
     * REFERENCE 1
     */
    private static void allowPermissionsIfNeeded() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // for notifications
        UiObject allowButton = device.findObject(new UiSelector()
                .className("android.widget.Button")
                .textContains("Allow"));

        // for camera perms
        UiObject whileButton = device.findObject(new UiSelector()
                .className("android.widget.Button")
                .textContains("While using the app"));

        // look for Allow for 1s
        if (allowButton.waitForExists(1000)) {
            try {
                allowButton.click();
            } catch (Exception e) {
                Log.d("PERMS", "Failed to allow notification permissions for testing");
            }
        }

        // look for While using the app for 1s
        if (whileButton.waitForExists(1000)) {
            try {
                whileButton.click();
            } catch (Exception e) {
                Log.d("PERMS", "Failed to allow camera permissions for testing");
            }
        }
    }

    /**
     * Remove all testing events from DB and reset to orignal state
     */
    @After
    public void tearDown() {
        db.collection(DatabaseConstants.eventColName).document(testEventId).delete();
    }

    /**
     * When called, adds a basic test event to the database for later use
     */
    public void addTestEvent() {
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
     * Tests a promotional event QR code scan.
     * Checks if it is on the page PR_EVENT at end.
     */
    @Test
    public void testPromoScan() {
        onView(withId(R.id.qr_scanning_page)).check(matches(ViewMatchers.isDisplayed())); // on scanner page?
        scanInstance.onScan("PR_" + testEventId + "_X");   // simulate scanning CI event
        try {
            Thread.sleep(1000L);    // wait for db fetching
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // check if visible fragment is tagged PR_EVENT
        Fragment visibleFragment = fragManager.findFragmentByTag("PR_EVENT");
        assertNotNull(visibleFragment);
        assertTrue(visibleFragment.isVisible());
    }

    /**
     * Tests a checkin event QR code scan.
     * Checks if it is on the page CI_EVENT at end.
     */
    @Test
    public void testCheckInScan() {
        onView(withId(R.id.qr_scanning_page)).check(matches(ViewMatchers.isDisplayed())); // on scanner page?
        scanInstance.onScan("CI_" + testEventId + "_X");   // simulate scanning CI event
        try {
            Thread.sleep(1000L);    // wait for db fetching
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // check if visible fragment is tagged CI_EVENT
        Fragment visibleFragment = fragManager.findFragmentByTag("CI_EVENT");
        assertNotNull(visibleFragment);
        assertTrue(visibleFragment.isVisible());
    }

    /**
     * Tests an invalid QR code scan.
     * A fake string is inputted and we check if
     * the device remains on the fragment SCANNER.
     */
    @Test
    public void testInvalidScan() {
        onView(withId(R.id.qr_scanning_page)).check(matches(ViewMatchers.isDisplayed()));  // check scanner page
        scanInstance.onScan("FAKE EVENT QR STRING");
        onView(withId(R.id.qr_scanning_page)).check(matches(ViewMatchers.isDisplayed()));  // no transition

        // check if visible fragment is tagged SCANNER still
        Fragment visibleFragment = fragManager.findFragmentByTag("SCANNER");
        assertNotNull(visibleFragment);
        assertTrue(visibleFragment.isVisible());
    }

    /**
     * Tests an invalid QR code scan with a valid format.
     * A correctly formatted checkin QR is given but
     * the Event ID is incorrect. Checks if the device remains
     * on the fragment SCANNER.
     */
    @Test
    public void testInvalidEventScan() {
        onView(withId(R.id.qr_scanning_page)).check(matches(ViewMatchers.isDisplayed()));  // check scanner page
        scanInstance.onScan("CI_goodFormatBadEventID_X");
        onView(withId(R.id.qr_scanning_page)).check(matches(ViewMatchers.isDisplayed()));  // no transition

        // check if visible fragment is tagged SCANNER still
        Fragment visibleFragment = fragManager.findFragmentByTag("SCANNER");
        assertNotNull(visibleFragment);
        assertTrue(visibleFragment.isVisible());
    }
}
