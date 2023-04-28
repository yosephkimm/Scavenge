package com.example.scavenger.leaderboardfiles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.example.scavenger.Hunt;
import com.example.scavenger.R;
import com.example.scavenger.databinding.FragmentLeaderboardBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;

    // the current hunt that the user is editing
    public static Hunt hunt;

    private GridView timeGV;

    private PlayerTimeAdapter adapter;


    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(R.color.avocado));

        binding.leaderboardhuntname.setText(hunt.getName());
        timeGV = binding.timeGV;
        displayTimes();
    }
    private void displayTimes() {
        FirebaseFirestore.getInstance().collection("PlayerLeaderboardTimes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // get the currently signed in user account's email
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            ArrayList<PlayerTime> timeList = new ArrayList<>();
                            for (DocumentSnapshot d : list) {
                                if (d.toObject(PlayerTime.class).getHuntId().equals(hunt.getName())) {
                                    timeList.add(d.toObject(PlayerTime.class));
                                }
                            }
                            Comparator<PlayerTime> comp = new Comparator<PlayerTime>() {
                                @Override
                                public int compare(PlayerTime o1, PlayerTime o2) {
                                    if (o1.getTime() > o2.getTime()) {
                                        return 1;
                                    } else if (o1.getTime() == o2.getTime()) {
                                        return 0;
                                    } else {
                                        return -1;
                                    }
                                }
                            };
                            Collections.sort(timeList, comp);
                            adapter = new PlayerTimeAdapter(getActivity(), timeList, LeaderboardFragment.this);
                            timeGV.setAdapter(adapter);
                        } else {
                            System.out.println("Hunts database collection is empty!");
                            // maybe display something that says "user has no hunts created yet"?
                        }
                    }
                });
    }
}