/**
 * This file represents the announcement object
 */
package com.example.quickscanr;
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.*; // for Date

/**
 * This represents the object, announcement, that Organizers will make to show to the Attendees.
 * Will be stored in the database.
 * @see OrganizerHome where announcements are from
 * @see AttendeeHome where announcments arrive to
 */
public class Announcement {
    private String title;
    private String body;
    private String date;
    private String userID;
    private String userName;
    private Bitmap bitmap;
    private String selectedEventId;

    /**
     * Constructor
     * @param title
     * @param body
     * @param date
     * @param userName
     */


    public Announcement(String title, String body, String date, String userID, String userName, String selectedEventId) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.userID = userID;
        this.userName = userName;
        this.selectedEventId = selectedEventId;
    }


    /**
     * Getter: Title
     * @return  the title text inputted by the user
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter: Title
     * @param title represents title text
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter: Body
     * @return  the body text inputted by the user
     */
    public String getBody() {
        return body;
    }

    /**
     * Setter: body text
     * @param body represents the body text
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Getter: Date
     * @return  the date when the user posted the announcement
     */
    public String getDate() {
        return date;
    }


    /**
     * Setter: date
     * @param date represents the date when the user posted the announcement
     */

    public void setDate(String date) {
        this.date = date;
    }


    /**
     * Getter: user's name
     * @return the name of the user who posted the announcement
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Setter: sets the user's name of who posted the announcement
     * @param userName the user
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Getter: gets the event id of the announcement
     * @return the event ID
     */
    public String getEventID() {
        return selectedEventId;
    }

    /**
     * Setter: sets the event ID for the announcement
     * @param eventID the relevant event
     */
    public void setEventID(String eventID) {
        this.selectedEventId = selectedEventId;
    }

    /**
     * Setter: sets the announcement bitmap
     * @param bmp the image to be set
     */
    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp;
    }

    /**
     * Getter: gets the announcement bitmap
     * @return bmp the image of the announcement
     */
    public Bitmap getBitmap() {
        return bitmap;
    }
}