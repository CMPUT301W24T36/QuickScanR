package com.example.quickscanr;
import java.util.*; // for Date

/**
 * This represents the object, announcement, that Organizers will make to show to the Attendees.
 * Will be stored in the database.
 * title: user input
 * body: user input
 * date: date of post, date and time
 * userID: unique ID, may delete
 * userName: the user's name for display
 *
 */
public class Announcement {
    private String title;
    private String body;
    private String date;
    private String userID; // may delete
    private String userName;

    /**
     * Constructor
     * @param title
     * @param body
     * @param date
     * @param userID
     * @param userName
     */
    public Announcement(String title, String body, String date, String userID, String userName) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.userID = userID;
        this.userName = userName;
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
     * Getter: userID
     * @return the unique ID of the user who posted the announcement
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Setter: sets the unique user ID of who posted the announcement
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
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
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
