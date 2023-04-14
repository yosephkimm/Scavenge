package com.example.scavenger;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
     * The user as a FirebaseUser object that created this Hunt
     */
    private FirebaseUser user;

    /**
     * An arraylist of all of the checkpoints of this hunt
     * @invariant checkpoints.size() <= maxCheckpoints
     */
    private ArrayList<Checkpoint> checkpoints;

    /**
     * The maximum number of checkpoints that Hunts are allowed to have
     * @invariant maxCheckpoints > 0
     */
    private final int maxCheckpoints = 10;

    public Hunt(String name, String description, FirebaseUser user) {
        this.name = name;
        this.desc = description;
        this.user = user;
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

    /**
     * Add a new checkpoint to this Hunt
     * @param cp the checkpoint to add
     * @return true if successfully added, false if checkpoints is at max capacity
     */
    public boolean addCheckpoint(Checkpoint cp) {
        if (checkpoints.size() >= maxCheckpoints) return false;
        checkpoints.add(cp);
        return true;
    }

    /**
     * Get the arraylist of all checkpoints of this Hunt
     * @return the checkpoints as an arraylist
     */
    public ArrayList<Checkpoint> getCheckpoints() {
        return this.checkpoints;
    }


}
