package com.example.quickscanr;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String homepage;
    private String phoneNumber;
    private String email;
    private int userType;
    private String userId;

    public User() {}

    public User(String name, String phoneNumber, String email, int userType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

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
