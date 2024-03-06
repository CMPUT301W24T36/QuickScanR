package com.example.quickscanr;

import com.google.firebase.Timestamp;
import java.util.List;

public class Attendee {
    private String userId;
    private String name;
    private List<Timestamp> checkIns; // Stores check-in Timestamps

    public Attendee(String userId, String name, List<Timestamp> checkIns) {
        this.userId = userId;
        this.name = name;
        this.checkIns = checkIns;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<Timestamp> getCheckIns() {
        return checkIns;
    }

    // Utility method to get check-in count
    public int getCheckInCount() {
        return checkIns != null ? checkIns.size() : 0;
    }
}
