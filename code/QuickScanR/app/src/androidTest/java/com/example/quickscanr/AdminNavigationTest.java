package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.CoreMatchers.allOf;

import androidx.fragment.app.FragmentActivity;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for admin navigation.
 * Tests navigation between the 4 main pages.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * setUp runs before all the tests run.
     * makes the thread sleep for 5s, to allow for the user to be initialized in Main Activity.
     */
    @Before
    public void setUp() {
        // wait for user to be initialized
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tells fragment manager to show the browse events page.
     */
    public void goToEvents() {
        scenario.getScenario().onActivity(activity -> {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.content_main, new AdminEventsList())
                    .addToBackStack(null).commit();
        });
    }

    /**
     * go to admin browse events
     * check that we're on the browse events page
     */
    @Test
    public void testBrowseEventsBtn() {
        goToEvents();
        onView(withId(R.id.admin_browse_events)).check(matches(isDisplayed()));
    }

    /**
     * go to admin events list
     * click the browse profiles button and check that it's being showed
     */
    @Test
    public void testBrowseProfilesBtn() {
        goToEvents();
        onView(withId(R.id.nav_ad_users_btn)).perform(click());     // TODO: this throws an error
        onView(withId(R.id.admin_browse_profiles)).check(matches(isDisplayed()));
    }

    /**
     * go to organizer events list
     * click the browse images button and check that it's being showed
     */
    @Test
    public void testBrowseImagesBtn() {
        goToEvents();
        onView(withId(R.id.nav_ad_images_btn)).perform(click());    // TODO: this throws an error
        onView(withId(R.id.admin_browse_events)).check(matches(isDisplayed()));
    }
}
