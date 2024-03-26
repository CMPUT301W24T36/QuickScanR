package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.view.View;
import android.widget.ImageButton;

/**
 * Tests for admin navigation.
 * Tests navigation between the 4 main pages.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private static boolean userSet = false;

    /**
     * makes the current user an admin
     */
    @Before
    public void setUp() {
        // only setup once
        if (userSet) {
            return;
        }
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        onView(withId(R.id.user_edit_profile)).perform(click());
        onView(withId(R.id.edit_profile_usertype)).perform(click());
        onView(withText(UserType.getString(UserType.ADMIN))).perform(click());
        onView(withId(R.id.save_profile_btn)).perform(click());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        userSet = true;
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
//    @Test
    public void testBrowseImagesBtn() {
        onView(withId(R.id.nav_ad_images_btn)).perform(click());
//        onView(withId(R.id.admin_browse_images)).check(matches(isDisplayed()));   // TODO: page not implemented yet
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
        onView(withId(R.id.nav_ad_users_btn)).perform(click());
        onView(withId(R.id.adm_profile_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.admin_manage_profile_page)).check(matches(isDisplayed()));
    }

    // TODO: write test for accessing the manage image page
}
