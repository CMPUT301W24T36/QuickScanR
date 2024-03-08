package com.example.quickscanr;

/**
 * This class represents milestones
 * Titles would be along the lines of "Bronze check-in" etc.
 * Descriptions are short sentences letting the user know of their achievements.
 *
 * ISSUE: Not connected to the database yet. Will be implemented.
 *

 */
public class Milestone {


    private String title;
    private String description;

    /**
     * Constructor
     * @param title
     * @param description
     */
    public Milestone(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Gets title of the milestone
     * @return title of the milestone
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets title of the milestone
     * @param title of the milestone
     */

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description of the milestone
     * @return description of the milestone
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of the milestone
     * @param description of the milestone
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
