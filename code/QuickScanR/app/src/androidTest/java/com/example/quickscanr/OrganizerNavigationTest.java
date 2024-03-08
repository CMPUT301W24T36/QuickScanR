package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
 * Tests for organizer navigation.
 * Tests navigation between the 4 main pages.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerNavigationTest {

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
     * Tells fragment manager to show the homepage.
     */
    public void goHome() {
        scenario.getScenario().onActivity(activity -> {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.content_main, new OrganizerHome())
                    .addToBackStack(null).commit();
        });
    }

    /**
     * go to organizer home page
     * check that we're on the organizer home page
     */
    @Test
    public void testHomepageBtn() {
        goHome();
        onView(withId(R.id.organizer_home_page)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click the event list button and check that it's being showed
     */
    @Test
    public void testEventBtn() {
        goHome();
        onView(withId(R.id.nav_o_events_btn)).perform(click());
        onView(withId(R.id.organizer_event_list)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * click the add event button and check that it's being showed
     */
    @Test
    public void testOrganizerNav(){
        goHome();
        onView(withId(R.id.nav_o_add_event_btn)).perform(click());
        onView(withId(R.id.add_event_page)).check(matches(ViewMatchers.isDisplayed()));
    }

}
