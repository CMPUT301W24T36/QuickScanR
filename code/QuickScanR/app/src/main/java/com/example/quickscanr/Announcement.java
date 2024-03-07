package com.example.quickscanr;
import java.util.*; // for Date

/**
 * This represents the Announcements from the Organizers to the Attendees
 * Will be stored in the database.
 * title: user input
 * body: user input
 * date: date of post, date and time
 * userID: unique ID, will translate to the name in the UI
 *
 */
public class Announcement {
    private String title;
    private String body;
    private String date;
    private String userID; // no implementation yet!
    private String userName;

    public Announcement(String title, String body, String date, String userID, String userName) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.userID = userID;
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
