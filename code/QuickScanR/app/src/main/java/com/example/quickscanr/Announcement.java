package com.example.quickscanr;
import java.util.*; // for Date

/**
 * This represents the Announcements from the Organizers to the Attendees
 * Will be stored in the database.
 * title: user input
 * body: user input
 * date: date of post, date and time
 * userID: unique ID
 *
 */
public class Announcement {
    private String title;
    private String body;
    private Date date;
    private int userID; // no implementation yet!

    public Announcement(String title, String body, Date date, int userID) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.userID = userID;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
