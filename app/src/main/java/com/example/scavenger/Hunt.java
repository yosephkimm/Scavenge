package com.example.scavenger;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Hunt {

    private String name;
    private String desc;

    public Hunt(String name, String description) {
        this.name = name;
        this.desc = description;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void setDescription(String newDescription) {
        this.desc = newDescription;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    /*
     * Get an arraylist of all the created hunts from the database
     */
    public static ArrayList<Hunt> getHunts() {
        FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
        ArrayList<Hunt> hunts = new ArrayList<Hunt>();
        firestoreDatabase.collection("Courses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Hunt hunt = d.toObject(Hunt.class);
                                hunts.add(hunt);
                            }
                        } else {
                            // if the snapshot is empty
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // if we do not get any data
                    }
                });

        return hunts;
    }



}
