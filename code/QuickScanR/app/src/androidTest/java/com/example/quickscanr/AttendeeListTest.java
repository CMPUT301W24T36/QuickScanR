package com.example.quickscanr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeListTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    private static boolean userSet = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static String testEventId;

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
            // edit current user type to be organizer
            onView(withId(R.id.user_edit_profile)).perform(click());
            onView(withId(R.id.edit_profile_usertype)).perform(click());
            onView(withText(UserType.getString(UserType.ORGANIZER))).perform(click());
            onView(withId(R.id.save_profile_btn)).perform(click());
            userSet = true;
        }
        try {
            Thread.sleep(1000L);    // give 1 second between tests to be safe
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Add test event and get its ID
        addTestEvent();
    }

    @After
    public void tearDown() {
        // Ensure testEventId is not null before attempting to delete the document
        if (testEventId != null) {
            // Delete test event from database
            db.collection("events").document(testEventId).delete();
        }
    }

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

    @Test
    public void testAttendeeListAfterCheckIn() {
        // Simulate checking in an attendee
        checkInAttendee();

        // Verify that the attendee appears on the checked-in attendees list
        onView(withId(R.id.chkd_usr_text)).check(matches(withText("John Doe")));
        onView(withId(R.id.chkd_usr_count)).check(matches(withText("2"))); // Assuming "2" is the count of timestamps
    }

    public void checkInAttendee() {
        // Simulate checking in an attendee
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", "John Doe");
        List<Long> timestamps = Arrays.asList(1712517089266L, 1712443722028L);
        attendeeData.put("timestamps", timestamps);

        db.collection("events").document(testEventId).collection("attendees").add(attendeeData);
    }
}
