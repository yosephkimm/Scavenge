package com.example.scavenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scavenger.databinding.FragmentHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private GoogleSignInOptions gso; // for sign in process
    private GoogleSignInClient gsc; // for sign in process

    private GoogleSignInAccount account;

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
                        .navigate(R.id.action_homeFragment2_to_playHuntListFragment);
            }
        });

        binding.leaderboardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment2_to_leaderboardMainFragment);
            }
        });

        binding.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AccountWindow.class));
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
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
                            .navigate(R.id.action_homeFragment2_to_creatorHomePageFragment);
                }
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(getActivity(),gso);

        account=GoogleSignIn.getLastSignedInAccount(getActivity());
        // set display name text in home page and set if user is guest
        if (account != null) {
            binding.name.setText("Welcome, " + account.getDisplayName());
            isGuest = false;
        } else {
            binding.name.setText("Welcome, Guest");
            isGuest = true;
        }

        // get the user's profile pic and set the image resource to it
        setProfilePic();


    }

    public void onResume() {
        super.onResume();
        setProfilePic();

    }

    private void setProfilePic() {
        /*
        if (account == null) return;
        FirebaseFirestore.getInstance().collection("Users")
                .document(account.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int profilepic = documentSnapshot.toObject(User.class).getProfilePic();
                        binding.profilepic.setImageResource(profilepic);
                        binding.profilepic.setVisibility(View.VISIBLE);
                    }
                });
                
         */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}