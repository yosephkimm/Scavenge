package com.example.scavenger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scavenger.databinding.FragmentFirstBinding;
import com.example.scavenger.databinding.FragmentHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private GoogleSignInOptions gso; // for sign in process
    private GoogleSignInClient gsc; // for sign in process

    private boolean isGuest;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(R.color.avocado));


        // set play button to go to playhunt fragment
        binding.playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment2_to_PlayHunt);
            }
        });

        binding.leaderboardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LeaderboardActivity.class));
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        });

        // set log out button to sign out and return to login screen
        binding.logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });

        binding.createhuntbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (isGuest) {
                    DialogFragment newFragment = new LoginDialogueFragment();
                    newFragment.show(getFragmentManager(), "game");
                } else {
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_homeFragment2_to_createHuntSettingsFragment);
                }
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(getActivity(),gso);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getActivity());
        // set display name text in home page and set if user is guest
        if (account != null) {
            binding.name.setText("Welcome, " + account.getDisplayName());
            isGuest = false;
        } else {
            binding.name.setText("Welcome, Guest");
            isGuest = true;
        }

    }

    /*
     * sign the user out and return to the main activity (log in page)
     */
    private void SignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getActivity(),MainActivity.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}