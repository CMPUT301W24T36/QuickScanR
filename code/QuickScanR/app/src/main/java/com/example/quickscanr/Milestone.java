package com.example.quickscanr;

/**
 * This class represents milestones
 * Titles would be along the lines of "Bronze check-in" etc.
 * Descriptions are short sentences letting the user know of their achievements.
 */
public class Milestone {


    private String title;
    private String description;

    public Milestone(String title, String description) {
        this.title = title;
        this.description = description;
    }


    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
