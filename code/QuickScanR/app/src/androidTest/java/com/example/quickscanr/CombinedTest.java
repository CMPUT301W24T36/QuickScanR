package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.TestCase.assertEquals;

import androidx.fragment.app.FragmentActivity;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CombinedTest {

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
     * Tests the profile button regardless of user type.
     */
    @Test
    public void testProfileButton() {
        boolean onProfilePage = false;
        try {
            onView(withId(R.id.nav_a_profile_btn)).perform(click());
            onView(withId(R.id.attendee_profile_page))
                    .check(matches(hasDescendant(withId(R.id.profile_page))));
            onProfilePage = true;
        }
        catch (NoMatchingViewException e) {}
        try {
            onView(withId(R.id.nav_o_profile_btn)).perform(click());
            onView(withId(R.id.organizer_profile_page))
                    .check(matches(hasDescendant(withId(R.id.profile_page))));
            onProfilePage = true;
        }
        catch (NoMatchingViewException e) {}
        try {
            onView(withId(R.id.nav_ad_profile_btn)).perform(click());
            onView(withId(R.id.admin_profile_page))
                    .check(matches(hasDescendant(withId(R.id.profile_page))));
            onProfilePage = true;
        }
        catch (NoMatchingViewException e) {}
        assertEquals(onProfilePage, true);
    }
}
