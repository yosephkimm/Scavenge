package com.example.scavenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.scavenger.databinding.FragmentCreateHuntSettingsBinding;
import com.example.scavenger.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class CreateHuntSettingsFragment extends Fragment {


    // https://www.geeksforgeeks.org/how-to-retrieve-data-from-the-firebase-realtime-database-in-android/#
    // creating a variable for our Firebase Database.
    FirebaseDatabase firebaseDatabase;
    // creating a variable for our Database Reference for Firebase.
    DatabaseReference databaseReference;
    // creating a variable for firebasefirestore.
    private FirebaseFirestore firestoreDatabase;

    private FragmentCreateHuntSettingsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCreateHuntSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(R.color.avocado));

        firebaseDatabase = FirebaseDatabase.getInstance();
        firestoreDatabase = FirebaseFirestore.getInstance();


        // when the done button is clicked, get the name and description and create a new hunt
        binding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(binding.editTextHuntName.getText());
                String desc = String.valueOf(binding.editTextHuntDesc.getText());

                if (verifyName(name)) createHunt(name, desc);
            }
        });
    }


    /*
     * Get an arraylist of all the created hunts from the database
     */
    /*
    private ArrayList<Hunt> getHunts() {
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

     */



    /*
     * Check if the proposed name of the hunt already exists
     * return true if it already exists, false otherwise
     */
    private boolean verifyName(String name) {
        // if the name of the proposed hunt is already a hunt
        for (Hunt hunt : Hunt.getHunts()) {
            if (hunt.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;

    }


    /*
     * Create a scavenger hunt object and add it to the database
     */
    private void createHunt(String name, String description) {
        // creating a collection reference for our Firebase Firestore database.
        CollectionReference dbHunts = firestoreDatabase.collection("Hunts");

        // create a hunt object
        Hunt hunt = new Hunt(name, description);

        // below method is used to add data to Firebase Firestore.
        dbHunts.add(hunt).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}