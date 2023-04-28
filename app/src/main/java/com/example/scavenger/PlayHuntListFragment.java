package com.example.scavenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.scavenger.databinding.FragmentCreatorHomePageBinding;
import com.example.scavenger.databinding.FragmentPlayHuntListBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlayHuntListFragment extends Fragment {

    private FragmentPlayHuntListBinding binding;

    PlayHuntListGVAdapter adapter;

    private GridView huntGV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlayHuntListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        huntGV = binding.huntGVplayhuntlist;
        displayHunts();
    }

    private void displayHunts() {
        FirebaseFirestore.getInstance().collection("Hunts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            ArrayList<Hunt> huntArrayList = new ArrayList<Hunt>();
                            for (DocumentSnapshot d : list) {
                                huntArrayList.add(d.toObject(Hunt.class));
                            }
                            adapter = new PlayHuntListGVAdapter(getActivity(), huntArrayList, PlayHuntListFragment.this);
                            huntGV.setAdapter(adapter);
                        } else {
                            System.out.println("Hunts database collection is empty!");
                            // maybe display something that says "user has no hunts created yet"?
                        }
                    }
                });
    }
}