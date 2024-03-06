package com.example.quickscanr;

public class CheckInAttendee {
    private String userId;
    private String name;
    private int checkInCount;

    public CheckInAttendee(String userId, String name, int checkInCount) {
        this.userId = userId;
        this.name = name;
        this.checkInCount = checkInCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String name) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCheckInCount() {
        return checkInCount;
    }

    public void setCheckInCount(int checkInCount) {
        this.checkInCount = checkInCount;
    }

}
