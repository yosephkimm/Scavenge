package com.example.scavenger.playhuntfiles;

import com.example.scavenger.Checkpoint;

import java.util.ArrayList;

/**
 * A Hunt is played or created by users. It holds information about
 * checkpoints in the hunt that determine the steps a player must
 * take to complete the hunt and hints that guide the player to the
 * next checkpoint.
 */
public class Hunt {

    /**
     * The unique name of the Hunt as a string
     */
    private String name;
    /**
     * The description of the Hunt as a string
     */
    private String desc;

    /**
     * The user that created this Hunt
     */
    private String creator;


    /**
     * An arraylist of all of the checkpoints of this hunt
     * @invariant checkpoints.size() <= maxCheckpoints
     */
    private ArrayList<Checkpoint> checkpoints;

    private String bgcolor;

    /**
     * The maximum number of checkpoints that Hunts are allowed to have
     * @invariant maxCheckpoints > 0
     */
    private final int maxCheckpoints = 10;

    private boolean published;

    public Hunt(String name, String description, String creator, String bgcolor) {
        this.name = name;
        this.desc = description;
        this.creator = creator;
        this.bgcolor = bgcolor;
        this.checkpoints = new ArrayList<>();
        this.published = false;
    }

    public Hunt() {
        this.name = "";
        this.desc = "";
    }

    /**
     * Change the name of this Hunt
     * @param newName the new name of the Hunt
     */
    public void setName(String newName){
        this.name = newName;
    }

    /**
     * Change the description of this Hunt
     * @param newDescription the new description of the Hunt
     */
    public void setDescription(String newDescription) {
        this.desc = newDescription;
    }

    /**
     * Get the name of this Hunt
     * @return the name of this Hunt
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the description of this Hunt
     * @return the description of this Hunt
     */
    public String getDesc() {
        return this.desc;
    }

    public String getCreator() { return this.creator; }

    public String getbgcolor() {return this.bgcolor;}

    public void setbgcolor(String bgcolor) {this.bgcolor = bgcolor;}

    /**
     * Add a new checkpoint to this Hunt
     * @param cp the checkpoint to add
     * @return true if successfully added, false if checkpoints is at max capacity
     */
    public int addCheckpoint(Checkpoint cp) {
        if (checkpoints.size() >= maxCheckpoints) return -1;
        checkpoints.add(cp);
        return checkpoints.size()-1;
    }

    /**
     * Get the arraylist of all checkpoints of this Hunt
     * @return the checkpoints as an arraylist
     */
    public ArrayList<Checkpoint> getCheckpoints() {
        return this.checkpoints;
    }

    public void setPublished() {this.published = true;}

    public boolean getPublished() {
        return this.published;
    }

}
