package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for attendee navigation.
 * Tests navigation between the 4 main pages.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private static boolean userSet = false;

    /**
     * make the current user an attendee
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
     * TODO: attendee event list not complete yet
     */
//    @Test
    public void testOpenEventDetails() {
        onView(withId(R.id.nav_a_events_btn)).perform(click());
        onView(withId(R.id.atnd_ev_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.event_details_page)).check(matches(ViewMatchers.isDisplayed()));
    }

}
