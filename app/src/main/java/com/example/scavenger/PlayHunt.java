package com.example.scavenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.scavenger.databinding.FragmentPlayHuntBinding;

public class PlayHunt extends Fragment {

    private FragmentPlayHuntBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPlayHuntBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    MainActivity mainActivity = (MainActivity) getActivity();
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            binding.buttonHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), Hint.class));
                    ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}