package com.example.scavenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scavenger.databinding.FragmentCreatorHomePageBinding;
import com.example.scavenger.databinding.FragmentEditHuntBinding;

public class EditHuntFragment extends Fragment {

    private FragmentEditHuntBinding binding;

    // the current hunt that the user is editing
    public static Hunt hunt;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditHuntBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(R.color.avocado));

        binding.edithuntname.setText(hunt.getName());

        // create a new checkpoint and add it to this hunt
        binding.newcheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checkpoint checkpoint = new Checkpoint();
                //hunt.addCheckpoint(checkpoint);
            }
        });

    }
}