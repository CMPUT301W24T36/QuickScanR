package com.example.quickscanr;

import android.graphics.Bitmap;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/***
 * Represents an event in the application.
 */

public class Event implements Serializable {
    private String id = "default";
    private long timestamp;
    private String name;
    private String description;
    private String locationName;
    private String locationId;
    private String start;
    private String end;
    private String restrictions;
    private Bitmap poster;
    private User organizer;
    private ArrayList<User> attendees;
    private HashMap<User, Integer> checkedInCounts;
    private ArrayList<String> signedUpUsers;
    private Integer maxAttendees = -1;

    /**
     * Constructor #1
     * This constructor includes eventID and timestamp
     * @param name
     * @param description
     * @param locationName
     * @param locationId
     * @param start
     * @param end
     * @param restrictions
     * @param organizer
     * @param id
     * @param timestamp
     */
    public Event(String name, String description, String locationName, String locationId, String start, String end, String restrictions, User organizer, String id, long timestamp, Integer maxAttendees) {
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.locationId = locationId;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.organizer = organizer;
        this.attendees = new ArrayList<>();
        this.checkedInCounts = new HashMap<>();
        this.id = id;
        this.timestamp = timestamp;
        this.maxAttendees = maxAttendees;
    }

    /**
     * Constructor #2
     * @param name
     * @param description
     * @param locationName
     * @param locationId
     * @param start
     * @param end
     * @param restrictions
     * @param organizer
     */

    public Event(String name, String description, String locationName, String locationId, String start, String end, String restrictions, User organizer) {
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.locationId = locationId;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.organizer = organizer;
        this.attendees = new ArrayList<>();
        this.checkedInCounts = new HashMap<>();
    }

    /**
     * Constructor #3
     * @param name
     * @param description
     * @param locationName
     * @param locationId
     * @param start
     * @param end
     * @param restrictions
     * @param poster
     * @param organizer
     * @param maxAttendees
     */

    public Event(String name, String description, String locationName, String locationId, String start, String end, String restrictions, Bitmap poster, User organizer, Integer maxAttendees) {
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.locationId = locationId;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.poster = poster;
        this.organizer = organizer;
        this.attendees = new ArrayList<>();
        this.checkedInCounts = new HashMap<>();
        this.maxAttendees = maxAttendees;
    }

    /** Constructor #4
     *
     * @param name
     * @param description
     * @param locationName
     * @param locationId
     * @param start
     * @param end
     * @param restrictions
     * @param poster
     * @param organizer
     * @param attendees
     * @param checkedInCounts
     */
    public Event(String name, String description, String locationName, String locationId, String start, String end, String restrictions, Bitmap poster, User organizer, ArrayList<User> attendees, HashMap<User, Integer> checkedInCounts) {
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.locationId = locationId;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.poster = poster;
        this.organizer = organizer;
        this.attendees = attendees;
        this.checkedInCounts = checkedInCounts;
    }

    /** Constructor #5
     *
     * @param name
     * @param description
     * @param locationName
     * @param locationId
     * @param start
     * @param end
     * @param restrictions
     * @param organizer
     * @param attendees
     * @param checkedInCounts
     */

    public Event(String name, String description, String locationName, String locationId, String start, String end, String restrictions, User organizer, ArrayList<User> attendees, HashMap<User, Integer> checkedInCounts) {
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.locationId = locationId;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.organizer = organizer;
        this.attendees = attendees;
        this.checkedInCounts = checkedInCounts;
    }

    /**
     * Constructor #6
     * @param name
     * @param description
     * @param locationName
     * @param locationId
     * @param start
     * @param end
     * @param organizer
     * @param maxAttendees
     */
    public Event(String name, String description, String locationName, String locationId, String start, String end, User organizer, Integer maxAttendees) {
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.locationId = locationId;
        this.start = start;
        this.end = end;
        this.poster = poster;
        this.organizer = organizer;
        this.maxAttendees = maxAttendees;
    }

    /**
     * Getter: ID
     * @return id of the vent
     */

    public String getId() { return id; }

    /**
     * setter: id
     * @param id to be set as
     */

    public void setId(String id) { this.id = id; }

    /**
     * getter: timestamp
     * @return the timestamp
     */
    public long getTimestamp() { return timestamp; }

    /**
     * setter: timestamp
     * @param timestamp to be set as
     */
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    /**
     * getter: name
     * @return name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * setter
     * @param name to be set as
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter: description
     * @return the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * setter: description
     * @param description  the event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * getter: locationName
     * @return event locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * setter: locationName
     * @param locationName to set the event as
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * getter: locationId
     * @return event locationId
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * setter: locationId
     * @param locationId to set the event as
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * getter: start date
     * @return the start date of event
     */
    public String getStart() {
        return start;
    }

    /**
     * setter: start date
     * @param start the start date of event
     */
    public void setStart(String start) {
        this.start = start;
    }
    /**
     * getter: end date
     * @return the end date of event
     */
    public String getEnd() {
        return end;
    }

    /**
     * setter: end date
     * @param end  the end date of event
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * getter: restrictions
     * @return the restrictions of event
     */
    public String getRestrictions() {
        return restrictions;
    }

    /**
     * setter: restrictions
     * @param restrictions of event
     */

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    /**
     * getter: poster
     * @return poster of event
     */
    public Bitmap getPoster() {
        return poster;
    }

    /**
     * setter: poster
     * @param poster of event
     */
    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    /**
     * getter: organizer
     * @return organizer of event
     */
    public User getOrganizer() {
        return organizer;
    }

    /**
     * setter: organizer
     * @param organizer of event
     */

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    /**
     * getter: attendees
     * @return attendees list
     */
    public ArrayList<User> getAttendees() {
        return attendees;
    }

    /**
     * adds an attendee in the list of attendees
     * @param attendee
     */
    public void addAttendee(User attendee) {
        attendees.add(attendee);
    }

    /**
     * Counts the number of check ins
     * @return the amount of checkedIn users
     */

    public HashMap<User, Integer> getCheckedInCounts() {
        return checkedInCounts;
    }

    /**
     * add a new checked in attendee
     * @param checkedIn
     */

    public void addCheckedInAttendee(User checkedIn) {
        if (checkedInCounts.getOrDefault(checkedIn, -1) == -1) {
            checkedInCounts.put(checkedIn, 1);
        }
        else {
            checkedInCounts.put(checkedIn, checkedInCounts.get(checkedIn) + 1);
        }
    }

    /**
     * gets the RSVP count of the event
     * @return
     */

    public int getRSVPCount() {
        return getAttendees().size();
    }

    /**
     * gets the total check in count in the event
     * @return
     */
    public int getTotalCheckInCount() {
        int checkInCount = 0;
        for (int count : checkedInCounts.values()) {
            checkInCount += count;
        }
        return checkInCount;
    }

    /**
     * Handles errors for input
     * @param nameInput
     * @param locationNameInput
     * @param startDateInput
     * @param endDateInput
     * @return
     */
    public boolean isErrors (TextInputEditText nameInput, AutoCompleteTextView locationNameInput, TextInputEditText startDateInput, TextInputEditText endDateInput, Switch isAttendeeLimit, EditText maxAttendeesInput) {
        boolean wasErrors = false;

        if (name.equals("")) {
            nameInput.setError("Event must have a name!");
            wasErrors = true;
        }
        if (locationName.equals("")) {
            locationNameInput.setError("Event must have a locationName!");
            wasErrors = true;
        }
        if (start.equals("")) {
            startDateInput.setError("Event must have a start date!");
            wasErrors = true;
        }
        if (end.equals("")) {
            endDateInput.setError("Event must have an end date!");
            wasErrors = true;
        }
        else if (!start.equals("") && !end.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date startDate = format.parse(start);
                Date endDate = format.parse(end);

                if (endDate.before(startDate)) {
                    endDateInput.setError("Event must end on or after the start date!");
                    wasErrors = true;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (isAttendeeLimit.isChecked() && maxAttendeesInput.getText().toString().equals("")) {
            maxAttendeesInput.setError("Please specify limit!");
            wasErrors = true;
        }

        return wasErrors;
    }

    /**
     * set the signed up users for the event
     * @param signedUpUsers list of users that's signed up
     */
    public void setSignedUp(ArrayList<String> signedUpUsers) {
        this.signedUpUsers = signedUpUsers;
    }

    /**
     * get number of users signed up
     * @return number of users signed up
     */
    public int getSignUpCount() {
        return signedUpUsers.size();
    }

    /**
     * get maximum number of attendees for an event
     * @return maximum number of attendees for an event
     */
    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    /**
     * set maximum number of attendees for an event
     * @param maxAttendees maximum number of attendees for an event
     */
    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    /**
     * checks if event is limited
     * @return true if event is limited, otherwise false
     */
    public boolean isLimitedAttendees() {
        return getMaxAttendees() != -1;
    }

    /**
     * checks if the event is full (# of signed up users >= max attendees if there is a max defined)
     * @return true if full, else false
     */
    public boolean isAtCapacity() {
        return getMaxAttendees() != -1? signedUpUsers.size() >= getMaxAttendees() : false;
    }
}