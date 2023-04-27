package com.example.scavenger;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.scavenger.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // was in strings.xml: <string name="default_web_client_id">1010259840854-l51gpa8l14bgb8o3kutmbfrlc79f947f.apps.googleusercontent.com</string>

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    // Google Sign-In Instructions: https://www.youtube.com/watch?v=jEKjI0OzNNI
    private ImageView signin; // sign in image that acts as a button when pressed
    private TextView invalidlogin;
    private GoogleSignInOptions gso; // for sign in process
    private GoogleSignInClient gsc; // for sign in process
    private FirebaseAuth mAuth; // Firebase Authentication
    private FirebaseUser user;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        getSupportActionBar().hide(); // hides the action bar at the top of the screen

        // Initialize Firebase Auth
        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        invalidlogin = findViewById(R.id.invalidlogin);
        invalidlogin.setText("");
        signin= findViewById(R.id.signinimage);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken("1010259840854-j4dsqm6hl4hg401hvhgm80t4rrbuk476.apps.googleusercontent.com  ")
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
        gsc.signOut();

        // when the sign in button is pressed, invoke the Google sign in process
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
        // when the continue as guest button is pressed, sign in the user as a guest
        findViewById(R.id.asguestimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity();
            }
        });


}

    /*
     * Get the Google sign in intent and invoke the onActivityResult method
     */
    private void SignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }
    /*
     * Gets the request and if successful, call HomeActivity method
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        // if the email is not a valid wheaton.edu address, do not allow entry to the app
        if (!verifyEmail(account.getEmail())) {
            invalidlogin.setText("Error: invalid email");
            gsc.signOut();
            return;
        }
        if (requestCode==100) {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
                HomeActivity();
            } catch (ApiException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Checks to see if email address ends with "@my.wheaton.edu" to verify
     */
    private boolean verifyEmail(String email) {
        int atSign = email.indexOf('@');
        if (atSign != -1) {
            if (email.substring(atSign).equalsIgnoreCase("@my.wheaton.edu")) return true;
        }
        return false;
    }

    /*
     * Got code from: https://firebase.google.com/docs/auth/android/google-signin#java_1
     * authenticate account with firebase if there is a successful login
     */
    private void firebaseAuth(String idToken) {
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser fbuser = mAuth.getCurrentUser();
                                user = fbuser;
                            } else {
                                // If sign in fails, display a message to the user.
                                user = null;
                            }
                        }
                    });
        }
    }

    /*
     * Ends this activity and begins the LoginActivity (home page of the app)
     */
    private void HomeActivity() {
        //navController.navigate(R.id.action_FirstFragment_to_HomeFragment);
        /*
        finish();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);

         */
        navController.navigate(R.id.homeFragment2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}