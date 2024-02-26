package com.example.quickscanr;

import android.media.Image;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    private String name;
    private String description;
    private String location;
    private String start;
    private String end;
    private String restrictions;
    private Image poster;
    private User organizer;

    public Event() {}

    public Event(String name, String description, String location, String start, String end, String restrictions, User organizer) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.organizer = organizer;
    }

    public Event(String name, String description, String location, String start, String end, String restrictions, Image poster, User organizer) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.restrictions = restrictions;
        this.poster = poster;
        this.organizer = organizer;
    }

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

    public Image getPoster() {
        return poster;
    }

    public void setPoster(Image poster) {
        this.poster = poster;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public boolean isErrors (TextInputEditText name, TextInputEditText location, TextInputEditText startDateInput, TextInputEditText endDateInput) {
        boolean wasErrors = false;

        String eventName = name.getText().toString();
        String eventLoc = location.getText().toString();
        String startDateString = startDateInput.getText().toString();
        String endDateString = endDateInput.getText().toString();

        if (eventName.equals("")) {
            name.setError("Event must have a name!");
            wasErrors = true;
        }
        if (eventLoc.equals("")) {
            location.setError("Event must have a location!");
            wasErrors = true;
        }
        if (startDateString.equals("")) {
            startDateInput.setError("Event must have a start date!");
            wasErrors = true;
        }
        if (endDateString.equals("")) {
            endDateInput.setError("Event must have an end date!");
            wasErrors = true;
        }

        return wasErrors;
    }

}
