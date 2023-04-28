package com.example.scavenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.example.scavenger.databinding.AccountWindowBinding;
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

import java.util.ArrayList;
import java.util.List;

public class AccountWindow extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .71), (int) (height * .5));

        findViewById(R.id.logoutbutton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("logout button pressed");
            }
        });

        findViewById(R.id.workerprofilepic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePic(R.drawable.workerprofilepic);
            }
        });

        findViewById(R.id.bballplayerprofilepic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePic(R.drawable.bballplayerprofilepic);
            }
        });

        findViewById(R.id.businessprofilepic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePic(R.drawable.businessprofilepic);
            }
        });

        findViewById(R.id.studentprofilepic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePic(R.drawable.studentprofilepic);
            }
        });

        findViewById(R.id.logoutbutton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });
    }

    /*
     * sign the user out and return to the main activity (log in page)
     */
    private void SignOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(this,gso);

        AccountWindow aw = this;

        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(aw,MainActivity.class));
            }
        });
    }

    private void changeProfilePic(int newProfilePic) {
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            System.out.println("account is null!");
            return;
        }
        FirebaseFirestore.getInstance().collection("Users")
                .document(account.getEmail())
                .update("profilePic",newProfilePic);
        finish();
    }

}