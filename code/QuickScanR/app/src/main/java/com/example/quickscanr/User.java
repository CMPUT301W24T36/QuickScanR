package com.example.quickscanr;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;

/**
 * Represents the users of the app; Attendees, Organizers, and Admins
 * @see UserType
 */
public class User implements Serializable {

    private String name;
    private String homepage;
    private String phoneNumber;
    private String email;
    private int userType;
    private String userId;

    /**
     * Constructor
     */
    public User() {}

    /**
     * Constructor
     * @param name
     * @param phoneNumber
     * @param email
     * @param userType
     */
    public User(String name, String phoneNumber, String email, int userType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userType = userType;
    }

    /**
     * getter: name
     * @return name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * setter: name
     * @param name of user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter: homepage
     * @return homepage of user
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * setter : home page
     * @param homepage of user
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     * getter: number of user
     * @return phone number of user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * setter: number of user
     * @param phoneNumber of user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * getter: email
     * @return email of user
     */
    public String getEmail() {
        return email;
    }

    /**
     * setter: email
     * @param email of user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getter: usertype
     * @return type of user
     */
    public int getUserType() {
        return userType;
    }
    /**
     * setter: usertype
     * @param userType of user
     */

    public void setUserType(int userType) {
        this.userType = userType;
    }

    /**
     * getter: userID
     * @return id of user
     */
    public String getUserId() {
        return userId;
    }

    /**
     * setter: userID
     * @param userId of user
     */

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * for editing a profile. checks the fields for valid input
     * @param nameField the input box for a user's name
     * @return a boolean saying if there was errors or not
     */
    public boolean isErrors(TextInputEditText nameField) {
        boolean wasErrors = false;
        if (nameField.getText().toString() == "") {
            nameField.setError("User must have a name!");
            wasErrors = true;
        }
        return wasErrors;
    }
}
