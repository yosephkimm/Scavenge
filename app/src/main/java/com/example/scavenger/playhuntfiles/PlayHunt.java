package com.example.scavenger.playhuntfiles;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.scavenger.Checkpoint;
import com.example.scavenger.Hint;
import com.example.scavenger.Hunt;
import com.example.scavenger.R;
import com.example.scavenger.databinding.FragmentPlayHuntBinding;
import com.example.scavenger.edithuntfiles.CheckpointRVAdapter;
import com.example.scavenger.edithuntfiles.CreateHuntSettingsFragment;
import com.example.scavenger.edithuntfiles.EditHuntFragment;
import com.example.scavenger.leaderboardfiles.PlayerTime;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PlayHunt extends Fragment {

    private GoogleSignInOptions gso; // for sign in process
    private GoogleSignInClient gsc; // for sign in process

    private FragmentPlayHuntBinding binding;
    private ImageView imageView;

    public static Hunt hunt;

    private Checkpoint currentCheckpoint;

    private Boolean mLocationPermissionsGranted = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private PlayCheckpointRVAdapter adapter;

    private RecyclerView recyclerView;
    private TextView timeTV;

    private long endTime;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentPlayHuntBinding.inflate(inflater, container, false);
        FirebaseApp.initializeApp(requireContext());

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // checkpoint list stuff
        currentCheckpoint = hunt.getCheckpoints().get(0); // get the first checkpoint
        ArrayList<Checkpoint> initial = new ArrayList<>();
        initial.add(currentCheckpoint);
        adapter = new PlayCheckpointRVAdapter((ArrayList<Checkpoint>) initial, getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = binding.checkpointRVplayhunt;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        imageView = binding.capturedImage;

        binding.buttonHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HintWindow.class));
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        });
        binding.buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(PlayHunt.this)
                        .navigate(R.id.action_PlayHunt_to_homeFragment2);
            }
        });
        binding.buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MapDemo.class));
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        });
        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 105);
            }
        });
        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(R.drawable.ic_menu_gallery);
            }
        });
        binding.buttonCheckloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocation();
            }
        });

        // start the timer at 0 counting up
        timeTV = binding.timer;
        timeTV.setText("0");
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                timeTV.setText(String.valueOf(i));
                handler.postDelayed(this, 1000);
            }
        });


    }

    private void checkLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (mLocationPermissionsGranted) {
                Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
                locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            System.out.println("got location!");
                            Location currentLocation = (Location) task.getResult();
                            // if the player is within 6 feet of the checkpoint, they can move on to the next one!
                            if (Math.abs(currentCheckpoint.getLatitude()-currentLocation.getLatitude()) <= 0.001
                                    && Math.abs(currentCheckpoint.getLongitude()-currentLocation.getLongitude()) <= 0.001) {
                                if (currentCheckpoint.getPosition() == hunt.getCheckpoints().size()-1) {
                                    // the user has completed the hunt!
                                    // stop the time
                                    endTime = Long.parseLong((String) timeTV.getText());
                                    // create a new PlayerTime object and add it to the database
                                    updatePlayerTime(endTime);
                                    // display something that says "Nice job! You did it!"
                                    System.out.println("finished with time: " + endTime);
                                } else {
                                    currentCheckpoint = hunt.getCheckpoints().get(currentCheckpoint.getPosition()+1);
                                    updateProgress(currentCheckpoint);
                                }

                            }
                        } else {
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                getLocationPermission();
            }
        } catch (SecurityException e) {
            System.out.println("security exception");
        }
    }

    private void updatePlayerTime(long endTime) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(getActivity(),gso);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getActivity());

        PlayerTime playerTime = new PlayerTime(hunt.getName(), account.getEmail(),endTime);

        FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
        DocumentReference dbPlayerTimes = firestoreDatabase.collection("PlayerLeaderboardTimes").document();
        dbPlayerTimes.set(playerTime)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

    }

    private void getLocationPermission(){
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void updateProgress(Checkpoint checkpoint) {
        adapter.add(checkpoint);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 105 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.capturedImage.setImageBitmap(imageBitmap);
            Bitmap testBitmap= imageBitmap;
                    //BitmapFactory.decodeResource(getResources(), R.drawable.test_run);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            // Create a reference to the image file in Firebase Storage
            StorageReference imageRef = storageRef.child("images/image.jpg");

            // Convert the Bitmap to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] dataImage = baos.toByteArray();
            // Upload the byte array to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(dataImage);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully, you can get the download URL here
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            // Do something with the image URL, e.g., display it in an ImageView
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occurred while uploading the image
                }
            });

            StorageReference imageTest = storageRef.child("images/test_run.jpg");
            imageRef.getDownloadUrl().toString();
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String one = uri.toString();
                    // Handle the success, one contains the download URL of image1.jpg

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle the failure
                }
            });
            String one = "https://firebasestorage.googleapis.com/v0/b/scavenger-6aa7e.appspot.com/o/images%2Fimage.jpg?alt=media&token=8c077b56-2db4-4e37-bbdc-389888a17573";
            String two = "https://firebasestorage.googleapis.com/v0/b/scavenger-6aa7e.appspot.com/o/images%2Ftest_run.jpg?alt=media&token=bdc63467-e0a6-4688-b328-26457120540c";
            imageTest.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String two = uri.toString();
                    // Handle the success, two contains the download URL of image2.jpg

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle the failure, e.g. show an error message
                }
            });

            CompareImagesTask task = new CompareImagesTask();
            task.execute(one, two);



        }
    }


    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress the bitmap with different compression options
        // You can adjust the compression quality to reduce the image size
        // Possible values for the format are JPEG, PNG, and WEBP
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private class CompareImagesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String one = strings[0];
            String two = strings[1];

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            String value = "{\r\n    \"image_a\": {\r\n        \"type\": \"url\",\r\n        \"content\": \"https://is4-ssl.mzstatic.com/image/thumb/Purple62/v4/0e/cc/cf/0ecccfae-0d5a-dcfc-9822-815f0705aadf/source/256x256bb.jpg\"\r\n    },\r\n    \"image_b\": {\r\n        \"type\": \"url\",\r\n        \"content\": \"https://is4-ssl.mzstatic.com/image/thumb/Purple62/v4/0e/cc/cf/0ecccfae-0d5a-dcfc-9822-815f0705aadf/source/256x256bb.jpg\"\r\n    }\r\n}";
            RequestBody body = RequestBody.create(mediaType, value);
            Request request = new Request.Builder()
                    .url("https://similarity2.p.rapidapi.com/similarity")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("X-RapidAPI-Key", "cc0ba133dfmshd8ceeaf057ee73ep157d8fjsn5da06ad0ec11")
                    .addHeader("X-RapidAPI-Host", "similarity2.p.rapidapi.com")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    // Handle failure
                }
            } catch (IOException e) {
                Log.e("PlayHunt", "Failed to compare images", e);
                // Handle failure
            }
            return null;
        }

        @Override
        protected void onPostExecute(String responseData) {
            if (responseData != null) {
                // Process the response data
                System.out.println(responseData);
            } else {
                System.out.println("ASD");
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}