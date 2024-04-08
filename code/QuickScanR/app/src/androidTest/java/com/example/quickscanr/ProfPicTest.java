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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for uploading profile picture + removing profile picture
 *
 * 1. Reference for UIAutomator allow permissions idea:
 * https://stackoverflow.com/questions/34439072/espresso-click-on-the-button-of-the-dialog
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfPicTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
    private static boolean userSet = false;
    EditProfile editInstance;
    FragmentManager fragManager;
    String imgID;
    static User user;
    MainActivity mActivity;

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

        // look for While using the app for 1s
        if (whileButton.waitForExists(1000)) {
            try {
                whileButton.click();
            } catch (Exception e) {
                Log.d("PERMS", "Failed to allow camera permissions for testing");
            }
        }

        // look for While using the app for 1s
        if (whileButton.waitForExists(1000)) {
            try {
                whileButton.click();
            } catch (Exception e) {
                Log.d("PERMS", "Failed to allow location permissions for testing");
            }
        }

        // look for Allow for 1s
        if (allowButton.waitForExists(1000)) {
            try {
                allowButton.click();
            } catch (Exception e) {
                Log.d("PERMS", "Failed to allow notification permissions for testing");
            }
        }
    }

    /**
     * Sets up all relevant database fields before beginning test.
     * Also grabs a copy of the MainActivity and brings us to the right page.
     */
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
            user = MainActivity.user;
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
            mActivity = activity;
        });

        // wait to ensure activity callback
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test to remove a profile picture from a user.
     * After the button is clicked and save is pressed, user's
     * imageID is "default_user"
     */
    @Test
    public void testRemove() {
        onView(withId(R.id.remove_pfp_btn)).perform(click());
        onView(withId(R.id.save_profile_btn)).perform(click());
        assertEquals(user.getImageID(), "default_user");
    }

    /**
     * Image upload test. Upload an image, press save
     * and check if the user's imageID reflects as the
     * documentID of the upload.
     */
    @Test
    public void testUploadNew() {
        // get access to test uri
        ImgHandler img = new ImgHandler(ApplicationProvider.getApplicationContext());
        Uri uri = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.drawable.test_upload);
        img.uploadImage(uri, new ImgHandler.uploadCallback() {
            @Override
            public void onUploadComplete(String documentId) {
                imgID = documentId;
                user.setImageID(documentId, true);
            }
        }, user.getUserId(), user.getName());

        // wait for img upload completion
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.save_profile_btn)).perform(click());
        assertEquals(user.getImageID(), imgID);
    }

    /**
     * Reset user's image to default_user
     */
    @After
    public void resetUser() {
        user.setImageID("default_user",true);
    }
}
