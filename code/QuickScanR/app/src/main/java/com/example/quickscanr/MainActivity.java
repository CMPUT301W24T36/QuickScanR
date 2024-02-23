package com.example.quickscanr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.firestore.auth.User;

public class MainActivity extends AppCompatActivity {

    Integer userType = UserType.ATTENDEE;
    Boolean appOpened = false;
    Integer currentPage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home);
        ImageButton announcementsBtn = findViewById(R.id.nav_a_announcements_btn);
        ImageButton profileBtn = findViewById(R.id.nav_a_profile_btn);
        if (userType == UserType.ATTENDEE || userType == UserType.ORGANIZER) {
            handleNavigation(announcementsBtn);
        }
        else if (userType == UserType.ADMIN) {
            handleNavigation(profileBtn);
        }
        appOpened = true;
    }

    public void handleAttendeeNav(int buttonClicked) {
        int homePage = R.layout.user_home;
        int eventsListPage = R.layout.attendee_event_list;
        int cameraPage = R.layout.qr_code_scanning;
//        int profilePage = R.layout.profile_page;

        ImageButton announcementsBtn = findViewById(R.id.nav_a_announcements_btn);
        ImageButton eventsBtn = findViewById(R.id.nav_a_events_btn);
        ImageButton cameraBtn = findViewById(R.id.nav_camera_btn);
        ImageButton profileBtn = findViewById(R.id.nav_a_profile_btn);

        if (buttonClicked == announcementsBtn.getId() && currentPage != homePage) {
            setContentView(homePage);
            currentPage = homePage;
            // TODO: highlight the button when you're on that page
        }
        else if (buttonClicked == eventsBtn.getId() && currentPage != eventsListPage) {
            setContentView(eventsListPage);
            currentPage = eventsListPage;
            // TODO: highlight the button when you're on that page
        }
        else if (buttonClicked == cameraBtn.getId() && currentPage != cameraPage) {
            setContentView(cameraPage);
            currentPage = cameraPage;
            // TODO: highlight the button when you're on that page
        }
//        else if (buttonClicked == profileBtn.getId() && currentPage != profilePage) {
//            setContentView(profilePage);
//            currentPage = profilePage;
//            // TODO: highlight the button when you're on that page
//        }
    }

    public void handleNavigation(View v) {
        int buttonClicked = v.getId();
        if (userType == UserType.ATTENDEE) {
            handleAttendeeNav(buttonClicked);
        }
        else if (userType == UserType.ORGANIZER) {

        }
        else if (userType == UserType.ADMIN) {

        }
    }
}