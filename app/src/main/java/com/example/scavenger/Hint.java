package com.example.scavenger;

/**
 * Represents a hint for a scavenger hunt game.
 * Hints can have a description and an optional photo.
 */
public class Hint {
    private String description;
    private String photoUrl;

    /**
     * Constructs a new Hint object with the given description
     * @param description The description of the hint.
     */
    public Hint(String description) {
        this.description = description;
        this.photoUrl = null; // Initialize photo URL as null by default
    }

    /**
     * Constructs a new Hint object with the given description and photo URL
     * @param description The description of the hint.
     * @param photoUrl    The URL of the photo associated with the hint.
     */
    public Hint(String description, String photoUrl) {
        this.description = description;
        this.photoUrl = photoUrl;
    }

    public Hint() {}

    /**
     * Returns the description of the hint.
     * @return The description of the hint.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the URL of the photo associated with the hint.
     * @return The URL of the photo associated with the hint, or null if no photo is available.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Sets the description of the hint.
     * @param description The description of the hint.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the URL of the photo associated with the hint.
     * @param photoUrl The URL of the photo associated with the hint.
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Updates the description and/or photo URL of the hint.
     * @param description The updated description of the hint.
     * @param photoUrl    The updated URL of the photo associated with the hint.
     */
    public void updateHint(String description, String photoUrl) {
        this.description = description;
        this.photoUrl = photoUrl;
    }

    /**
     * Deletes the hint by setting the description and photo URL to null.
     */
    public void deleteHint() {
        this.description = null;
        this.photoUrl = null;
    }

    /**
     * Returns a string representation of the Hint object.
     * @return A string representation of the Hint object.
     */
    @Override
    public String toString() {
        return "Hint{" +
                "description='" + description + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}





