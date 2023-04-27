package com.example.scavenger;

import java.util.ArrayList;

public class Checkpoint {
    private double latitude;
    private double longitude;
    private String imageUrl;
    private ArrayList<Hint> hints;
    private String description;

    private Hunt hunt;

    // the position of this checkpoint in the hunt
    private int position;

    public Checkpoint(Hunt hunt, double latitude, double longitude, String imageUrl, String description, int position) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.description = description;
        this.position = position;
        this.hints = new ArrayList<Hint>();
        this.hunt = hunt;
    }

    public Checkpoint() {}

    public Hunt getHunt(){return this.hunt;}

    // get latitude of the checkpoint
    public double getLatitude() {
        return latitude;
    }

    // get longitude of the checkpoint
    public double getLongitude() {
        return longitude;
    }

    // get image URL of the checkpoint
    public String getImageUrl() {
        return imageUrl;
    }

    // get hints of the checkpoint
    public ArrayList<Hint> getHints() {
        return hints;
    }

    // get description of the checkpoint
    public String getDescription() {
        return description;
    }

    // set latitude of the checkpoint
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // set longitude of the checkpoint
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // set image URL of the checkpoint
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // set hints of the checkpoint
    public void setHints(ArrayList<Hint> hints) {
        this.hints = hints;
    }

    // set description of the checkpoint
    public void setDescription(String description) {
        this.description = description;
    }

    public void setPosition(int position) {this.position = position; }
    public int getPosition() {return this.position;}

    // add a hint to the checkpoint
    public void addHint(Hint hint) {
        hints.add(hint);
    }

    // remove a hint from the checkpoint
    public void removeHint(Hint hint) {
        hints.remove(hint);
    }

    // update a hint at a specific index in the checkpoint
    public void updateHint(int index, Hint hint) {
        hints.set(index, hint);
    }
}

