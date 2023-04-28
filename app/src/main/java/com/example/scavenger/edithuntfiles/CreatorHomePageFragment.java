package com.example.scavenger.edithuntfiles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.scavenger.playhuntfiles.Hunt;
import com.example.scavenger.R;
import com.example.scavenger.databinding.FragmentCreatorHomePageBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreatorHomePageFragment extends Fragment {
    private FragmentCreatorHomePageBinding binding;

    private GridView huntGV;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCreatorHomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.newhuntimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CreatorHomePageFragment.this)
                        .navigate(R.id.action_creatorHomePageFragment_to_createHuntSettingsFragment);
            }
        });


        huntGV = binding.huntGV;
        displayHunts();
    }

    /**
     * Display all the hunts that the current user has created in the card view
     * @post list is updated with all hunts that the user has created
     * @pre the user is not a guest
     */
    private void displayHunts() {
        FirebaseFirestore.getInstance().collection("Hunts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // get the currently signed in user account's email
                        String userEmail = GoogleSignIn.getLastSignedInAccount(getActivity()).getEmail();

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            ArrayList<Hunt> huntArrayList = new ArrayList<Hunt>();
                            for (DocumentSnapshot d : list) {
                                if (d.toObject(Hunt.class).getCreator().equalsIgnoreCase(userEmail))
                                    huntArrayList.add(d.toObject(Hunt.class));
                            }
                            HuntGVAdapter adapter = new HuntGVAdapter(getActivity(), huntArrayList, CreatorHomePageFragment.this);
                            huntGV.setAdapter(adapter);
                        } else {
                            System.out.println("Hunts database collection is empty!");
                            // maybe display something that says "user has no hunts created yet"?
                        }
                    }
                });
    }
}