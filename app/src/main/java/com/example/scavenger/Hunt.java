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

public class Hunt {

    private String name;
    private String desc;

    private FirebaseUser user;

    public Hunt(String name, String description, FirebaseUser user) {
        this.name = name;
        this.desc = description;
        this.user = user;
    }

    public Hunt() {
        this.name = "";
        this.desc = "";
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


}
