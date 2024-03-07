package com.example.quickscanr;

import android.graphics.Bitmap;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Event implements Serializable {
    private String id = "default";
    private long timestamp;
    private String name;
    private String description;
    private String location;
    private String start;
    private String end;
    private String restrictions;
    private Bitmap poster;
    private User organizer;
    private ArrayList<User> attendees;
    private HashMap<User, Integer> checkedInCounts;

    // this constructor includes eventID and timestamp
    public Event(String name, String description, String location, String start, String end, String restrictions, User organizer, String id, long timestamp) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.organizer = organizer;
        this.attendees = new ArrayList<>();
        this.checkedInCounts = new HashMap<>();
        this.id = id;
        this.timestamp = timestamp;
    }

    public Event(String name, String description, String location, String start, String end, String restrictions, User organizer) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.organizer = organizer;
        this.attendees = new ArrayList<>();
        this.checkedInCounts = new HashMap<>();
    }

    public Event(String name, String description, String location, String start, String end, String restrictions, Bitmap poster, User organizer) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.poster = poster;
        this.organizer = organizer;
        this.attendees = new ArrayList<>();
        this.checkedInCounts = new HashMap<>();
    }

    public Event(String name, String description, String location, String start, String end, String restrictions, Bitmap poster, User organizer, ArrayList<User> attendees, HashMap<User, Integer> checkedInCounts) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.poster = poster;
        this.organizer = organizer;
        this.attendees = attendees;
        this.checkedInCounts = checkedInCounts;
    }

    public Event(String name, String description, String location, String start, String end, String restrictions, User organizer, ArrayList<User> attendees, HashMap<User, Integer> checkedInCounts) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.organizer = organizer;
        this.attendees = attendees;
        this.checkedInCounts = checkedInCounts;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public ArrayList<User> getAttendees() {
        return attendees;
    }

    public void addAttendee(User attendee) {
        attendees.add(attendee);
    }

    public HashMap<User, Integer> getCheckedInCounts() {
        return checkedInCounts;
    }

    public void addCheckedInAttendee(User checkedIn) {
        if (checkedInCounts.getOrDefault(checkedIn, -1) == -1) {
            checkedInCounts.put(checkedIn, 1);
        }
        else {
            checkedInCounts.put(checkedIn, checkedInCounts.get(checkedIn) + 1);
        }
    }

    public int getRSVPCount() {
        return getAttendees().size();
    }

    public int getTotalCheckInCount() {
        int checkInCount = 0;
        for (int count : checkedInCounts.values()) {
            checkInCount += count;
        }
        return checkInCount;
    }

    public boolean isErrors (TextInputEditText nameInput, TextInputEditText locationInput, TextInputEditText startDateInput, TextInputEditText endDateInput) {
        boolean wasErrors = false;

        if (name.equals("")) {
            nameInput.setError("Event must have a name!");
            wasErrors = true;
        }
        if (location.equals("")) {
            locationInput.setError("Event must have a location!");
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

        return wasErrors;
    }

}