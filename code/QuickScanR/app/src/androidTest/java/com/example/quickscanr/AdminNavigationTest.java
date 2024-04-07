package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for admin navigation.
 * Tests navigation between the 4 main pages.
 * referenced https://medium.com/azimolabs/guide-to-make-custom-viewaction-solving-problem-of-nestedscrollview-in-espresso-35b133850254
 *      for clicking on gear icon on the admin browse events page (the ViewAction)
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private static boolean userSet = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static String testEventId;
    private static String testUserId;
    private static String testImageId;

    /**
     * makes the current user an admin
     */
    @Before
    public void setUp() {
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
            // edit current user type to be admin
            onView(withId(R.id.user_edit_profile)).perform(click());
            onView(withId(R.id.edit_profile_usertype)).perform(click());
            onView(withText(UserType.getString(UserType.ADMIN))).perform(click());
            onView(withId(R.id.save_profile_btn)).perform(click());
            userSet = true;
        }
        try {
            Thread.sleep(1000L);    // give 1 second between tests to be safe
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * runs after each test
     */
    @After
    public void tearDown() {
        if (testEventId != null) {
            db.collection(DatabaseConstants.eventColName).document(testEventId).delete();
            testEventId = null;
        }
        if (testUserId != null) {
            db.collection(DatabaseConstants.usersColName).document(testUserId).delete();
            testUserId = null;
        }
        if (testImageId != null) {
            db.collection("images").document(testImageId).delete();
            testImageId = null;
        }
    }

    /**
     * adding a test event so event pages can be tested
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
     * adding a test profile so the profile pages can be tested
     */
    public void addTestProfile() {
        // test user data
        Map<String, Object> data = new HashMap<>();
        data.put(DatabaseConstants.userFullNameKey, "Test User");
        data.put(DatabaseConstants.userHomePageKey, "Test Homepage");
        data.put(DatabaseConstants.userPhoneKey, "111-111-1111");
        data.put(DatabaseConstants.userEmailKey, "test@gmail.com");
        data.put(DatabaseConstants.userTypeKey, UserType.ATTENDEE);
        data.put(DatabaseConstants.userGeoLocKey, false);
        data.put(DatabaseConstants.userCheckedEventsKey, new ArrayList<String>());
        data.put(DatabaseConstants.userSignedUpEventsKey, new ArrayList<String>());
        data.put(DatabaseConstants.userImageKey, DatabaseConstants.userDefaultImageID);
        db.collection(DatabaseConstants.usersColName).add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    if (documentReference != null) {
                        testUserId = documentReference.getId();
                    }
                }
            }
        });
    }

    /**
     * adding a test image so the image pages can be tested
     */
    public void addTestImage() {
        // test image data
        Map<String, Object> imageData = new HashMap<>();
        imageData.put(DatabaseConstants.imgDataKey, "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC\n");
        db.collection("images").add(imageData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    if (documentReference != null) {
                        testImageId = documentReference.getId();
                    }
                }
            }
        });
    }

    /**
     * go to admin browse events
     * check that we're on the browse events page
     */
    @Test
    public void testBrowseEventsBtn() {
        onView(withId(R.id.nav_ad_events_btn)).perform(click());
        onView(withId(R.id.admin_browse_events)).check(matches(isDisplayed()));
    }

    /**
     * go to admin events list
     * click the browse profiles button and check that it's being showed
     */
    @Test
    public void testBrowseProfilesBtn() {
        onView(withId(R.id.nav_ad_users_btn)).perform(click());
        onView(withId(R.id.admin_browse_profiles)).check(matches(isDisplayed()));
    }

    /**
     * go to organizer events list
     * click the browse images button and check that it's being showed
     */
    @Test
    public void testBrowseImagesBtn() {
        onView(withId(R.id.nav_ad_images_btn)).perform(click());
        onView(withId(R.id.admin_browse_images)).check(matches(isDisplayed()));
    }

    /**
     * new ViewAction for interacting with the admin browse events page.
     * allows for test to click on the setting icon (access to manage event page).
     * @return ViewAction representing the action of clicking on the settings icon in the event list
     */
    private static ViewAction clickEventSettings() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(ImageButton.class));
            }

            @Override
            public String getDescription() {
                return "Click on settings icon for an item in the admin event list.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ImageButton settingsButton = (ImageButton) view.findViewById(R.id.edit_event);
                settingsButton.performClick();
            }
        };
    }

    /**
     * go to browse events page
     * click on the settings icon for an event
     * check that the manage event page is being shown
     */
    @Test
    public void testManageEvent() {
        addTestEvent();
        onView(withId(R.id.nav_ad_events_btn)).perform(click());
        onView(ViewMatchers.withId(R.id.view_event_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickEventSettings()));
        onView(withId(R.id.admin_manage_event_page)).check(matches(isDisplayed()));
    }

    /**
     * go to browse profiles page
     * click on a profile in the list
     * check that the manage profile page is being shown
     */
    @Test
    public void testManageProfile() {
        addTestProfile();
        onView(withId(R.id.nav_ad_users_btn)).perform(click());
        onView(withId(R.id.adm_profile_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.admin_manage_profile_page)).check(matches(isDisplayed()));
    }

    /**
     * go to browse images page
     * click on a image in the list
     * check that the manage image page is being shown
     */
    @Test
    public void testManageImage() {
        addTestImage();
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.nav_ad_images_btn)).perform(click());
        onView(withId(R.id.view_img_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.admin_manage_img_page)).check(matches(isDisplayed()));
    }
}
