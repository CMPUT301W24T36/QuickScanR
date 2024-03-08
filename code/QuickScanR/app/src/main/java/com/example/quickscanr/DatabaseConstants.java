package com.example.quickscanr;

/**
 * This class holds all the constants used to connect to the database.
 * @see Event
 * @see User
 * @see Announcement
 * @see ImgHandler
 */
public class DatabaseConstants {

    // Users
    public static final String usersColName = "users";
    public static final String userFullNameKey = "name";
    public static final String userHomePageKey = "homepage";
    public static final String userPhoneKey = "phoneNumber";
    public static final String userEmailKey = "email";
    public static final String userTypeKey = "userType";

    // Events
    public static final String eventColName = "events";
    public static final String evNameKey = "name";
    public static final String evDescKey = "description";
    public static final String evLocKey = "location";
    public static final String evStartKey = "startDate";
    public static final String evEndKey = "endDate";
    public static final String evRestricKey = "restrictions";
    public static final String evTimestampKey = "timestamp";
    public static final String evPosterKey = "posterID";
    public static final String evOwnerKey = "ownerID";

    // Announcements

    public static final String anBody = "body";
    public static final String anDate = "date";

    public static final String anUserName = "userName";
    public static final String anTitle = "title";
    public static final String anUserKey = "userID";

    // Images
    public static final String imgDataKey = "image";

    // QR CODE (not 100% DB but fits good here)
    public static final String qrTypeCheckIn = "CI";
    public static final String qrTypePromo = "PR";

}
