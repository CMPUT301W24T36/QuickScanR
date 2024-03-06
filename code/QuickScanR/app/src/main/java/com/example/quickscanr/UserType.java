package com.example.quickscanr;

public class UserType {
    public final static Integer ATTENDEE = 0;
    public final static Integer ORGANIZER = 1;
    public final static Integer ADMIN = 2;

    public static String getString(Integer type) {
        if (type == ATTENDEE) {
            return "Attendee";
        }
        else if (type == ORGANIZER) {
            return "Organizer";
        }
        return "Admin";
    }

    public static int valueOf(String type) {
        if (type == getString(ATTENDEE)) {
            return ATTENDEE;
        }
        if (type == getString(ORGANIZER)) {
            return ORGANIZER;
        }
        return ADMIN;
    }
}
