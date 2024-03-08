package com.example.quickscanr;

/**
 * Represents the different types for each user: attendee, organizer, admin
 */
public class UserType {
    public final static Integer ATTENDEE = 0;
    public final static Integer ORGANIZER = 1;
    public final static Integer ADMIN = 2;

    /**
     * Information about the user
     * @param type
     * @return the String representation of the type of user
     */
    public static String getString(Integer type) {
        if (type == ATTENDEE) {
            return "Attendee";
        }
        else if (type == ORGANIZER) {
            return "Organizer";
        }
        return "Admin";
    }

    /**
     * Returns the value of the user type
     * @param type
     * @return
     */
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
