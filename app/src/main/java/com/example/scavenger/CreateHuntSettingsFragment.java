package com.example.scavenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.scavenger.databinding.FragmentCreateHuntSettingsBinding;
import com.example.scavenger.databinding.FragmentHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

                createIfValidName(name, desc);
            }
        });
    }

    private void createIfValidName(String name, String desc) {

        // iterate through the database Hunts and see if the name exists already
        // create the hunt and add it to the database if it is a unique name
        // do nothing if the name already exists
        firestoreDatabase.collection("Hunts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            boolean nameExists = false;
                            for (DocumentSnapshot d : list) {
                                if (d.toObject(Hunt.class).getName().equalsIgnoreCase(name)) nameExists = true;
                            }
                            if (nameExists) {
                                displayError();
                                return;
                            }
                        }
                        createHunt(name,desc);
                    }
                });

    }

    private void displayError() {

    }

    /*
     * Create a scavenger hunt object and add it to the database
     */
    private void createHunt(String name, String description) {
        // creating a collection reference for our Firebase Firestore database.
        CollectionReference dbHunts = firestoreDatabase.collection("Hunts");
        // create a hunt object
        Hunt hunt = new Hunt(name, description, FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // below method is used to add data to Firebase Firestore.
        dbHunts.document(name).set(hunt)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // navigate to the hunt editor page
                        EditHuntFragment.hunt = hunt;
                        NavHostFragment.findNavController(CreateHuntSettingsFragment.this)
                                .navigate(R.id.editHuntFragment);
                    }
        });


    }
}