package com.example.quickscanr;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
