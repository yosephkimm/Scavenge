package com.example.scavenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.scavenger.databinding.FragmentLeaderboardMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardMainFragment extends Fragment {

    private FragmentLeaderboardMainBinding binding;

    private GridView huntGV;

    HuntArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        binding = FragmentLeaderboardMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        menuItem.setVisible(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.search);
        item.setVisible(false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(R.color.avocado));

        displayLeaderboards();
    }

    private void displayLeaderboards() {
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
                            adapter = new HuntArrayAdapter(getActivity(), huntArrayList, LeaderboardMainFragment.this);
                            huntGV.setAdapter(adapter);
                        } else {
                            System.out.println("Hunts database collection is empty!");
                            // maybe display something that says "no hunts created yet"?
                        }
                    }
                });
    }
}