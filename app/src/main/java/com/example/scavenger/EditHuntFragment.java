package com.example.scavenger;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.scavenger.databinding.FragmentCreatorHomePageBinding;
import com.example.scavenger.databinding.FragmentEditHuntBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class EditHuntFragment extends Fragment {

    private FragmentEditHuntBinding binding;

    // the current hunt that the user is editing
    public static Hunt hunt;

    private CheckpointRVAdapter adapter;

    private RecyclerView recyclerView;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditHuntBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CheckpointRVAdapter((ArrayList<Checkpoint>)hunt.getCheckpoints().clone(), getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = binding.checkpointRV;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // code from https://www.youtube.com/watch?v=cT9w4T9FCSQ
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL)); // adds divider between them
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int positionDragged = viewHolder.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                Collections.swap(adapter.getItems(), positionDragged, positionTarget);
                adapter.notifyItemMoved(positionDragged, positionTarget);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);

        // create a new checkpoint and add it to this hunt
        binding.newcheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get current latitude and longitude
                double latitude = 0;
                double longitude = 0;
                // create a new checkpoint and add it to this hunt
                Checkpoint checkpoint = new Checkpoint(hunt, latitude, longitude, "", "desc", adapter.getItemCount());
                ArrayList<Hint> hints = new ArrayList<>();
                hints.add(new Hint("Sample Description"));
                hints.add(new Hint("Sample Description"));
                hints.add(new Hint("Sample Description"));
                checkpoint.setHints(hints);
                //int checkpointPos = hunt.addCheckpoint(checkpoint);
                //if (checkpointPos < 0) {
                    // maximum checkpoint capacity reached
                //    return;
                //}
                // refresh checkpoint display
                adapter.add(checkpoint);
                recyclerView.setAdapter(adapter);
            }
        });

        binding.savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    public void refresh(CheckpointRVAdapter adapter, int removedCpPos) {
        for (Checkpoint cp : adapter.getItems()) {
            if (cp.getPosition() > removedCpPos) {
                cp.setPosition(cp.getPosition()-1);
            }
        }
        recyclerView.setAdapter(adapter);
    }

    public void save() {
        FirebaseFirestore.getInstance().collection("Hunts")
                .document(hunt.getName())
                .update("checkpoints",adapter.getItems());
    }
}