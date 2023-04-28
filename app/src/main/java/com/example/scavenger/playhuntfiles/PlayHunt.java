package com.example.scavenger;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.graphics.BitmapFactory;
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

import com.example.scavenger.Hunt;
import com.example.scavenger.R;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scavenger.Checkpoint;
import com.example.scavenger.Hint;
import com.example.scavenger.R;
import com.example.scavenger.databinding.FragmentPlayHuntBinding;
import com.example.scavenger.edithuntfiles.CheckpointRVAdapter;
import com.example.scavenger.edithuntfiles.CreateHuntSettingsFragment;
import com.example.scavenger.edithuntfiles.EditHuntFragment;
import com.example.scavenger.leaderboardfiles.PlayerTime;
import com.example.scavenger.playhuntfiles.HintWindow;
import com.example.scavenger.playhuntfiles.Mapping;
import com.example.scavenger.playhuntfiles.PlayCheckpointRVAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PlayHunt extends Fragment {

    private FragmentPlayHuntBinding binding;
    private ImageView imageView;
    private GoogleSignInOptions gso; // for sign in process
    private GoogleSignInClient gsc; // for sign in process


    private String link;
    private String check;
    private String temp[];
    private JSONObject one;
    private JSONObject two;
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
        link = "";
        temp = new String[2];
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
                startActivity(new Intent(getActivity(), Mapping.class));
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        });
        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),105);
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

    public void displayHints(Checkpoint checkpoint) {
        HintWindow.checkpoint = checkpoint;
        startActivity(new Intent(getActivity(), HintWindow.class));
        ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
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
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();


            // Create a reference to the image file in Firebase Storage
            StorageReference imageRef = storageRef.child("images/image.jpg");
            StorageReference imageTest = storageRef.child("images/test_run.jpg");

            //UPDATE
            // Retrieve from Firebase the image of current checkpoint and upload to imgur
            File localFile = null;
            try {
                localFile = File.createTempFile("image", "jpg");
                File finalLocalFile = localFile;
                imageTest.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    // The image has been downloaded to the local file, so we can decode it into a Bitmap
                    Bitmap testBitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    testBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dataImage = baos.toByteArray();
                    String send = Base64.encodeToString(dataImage, Base64.DEFAULT);
                    ImgurUploadTask task = new ImgurUploadTask(requireContext());
                    task.execute(send);
                }).addOnFailureListener(exception -> {
                    // Handle any errors that occurred during the download...
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Convert the Bitmap to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] dataImage = baos.toByteArray();
            String send = Base64.encodeToString(dataImage, Base64.DEFAULT);

            ImgurUploadTask task2 = new ImgurUploadTask(requireContext());

            task2.execute(send);

            // Upload the byte array to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(dataImage);

            //link and check both hold the jpg url






        }
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {
        private String jsonResponse;

        @Override
        protected String doInBackground(String... strings) {

            try {
                String imageUrl1 = strings[0];
                // Encode credentials
                String credentialsToEncode = "acc_7349655130743c2" + ":" + "887e02a21648c7305fbe41f338253124";
                String basicAuth = Base64.encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);

                // Set endpoint URL, index ID, features type, and image URLs
                String endpoint_url = "https://api.imagga.com/v2/tags";


                // Create URL with query parameters
                String url = endpoint_url + "?image_url=" + imageUrl1;
                URL urlObject = new URL(url);

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + basicAuth);
                // Send GET
                int responseCode = connection.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                // Read response
                BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                jsonResponse = connectionInput.readLine();
                connectionInput.close();
                // Parse JSON response
                System.out.println(jsonResponse);
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (one == null){
                    one = jsonObject;
                }
                else{
                    two = jsonObject;
                }
                if (two != null && one != null) {


                    try {
                        System.out.println(compareJsonObjects(one, two)+"ITOWRKED");
                    } catch (JSONException e) {
                        System.out.println("Error occured:" + e.getMessage());
                        throw new RuntimeException(e);
                    }


                }

            } catch (Exception e) {

            }


            return null;
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ImgurUploadTask extends AsyncTask<String, Void, String> {

        private Context context;
        private ProgressDialog progressDialog;

        public ImgurUploadTask(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Uploading photo...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String encoded = strings[0];
            String clientID = "309c32d8953018c";

            OkHttpClient client = new OkHttpClient();
            URL url = null;
            try {
                url = new URL("https://api.imgur.com/3/image");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Encode the photo file as Base64
            String data = null;
            try {
                data = URLEncoder.encode("image", "UTF-8") + "="
                        + URLEncoder.encode(encoded, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            conn.setDoOutput(true);
            conn.setDoInput(true);
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
            conn.setRequestProperty("Authorization", "Client-ID " + clientID);
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            try {
                conn.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            StringBuilder stb = new StringBuilder();
            OutputStreamWriter wr = null;
            try {
                wr = new OutputStreamWriter(conn.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                wr.write(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                wr.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Get the response
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String line;
            while (true) {
                try {
                    if (!((line = rd.readLine()) != null)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stb.append(line).append("\n");
            }
            try {
                wr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                rd.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return stb.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            Toast.makeText(context, "Upload successful!", Toast.LENGTH_SHORT).show();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject data = jsonObject.getJSONObject("data");

                NetworkTask task = new NetworkTask();


                if (link == ""){
                    link = data.getString("link");
                    Log.d("Upload", "Image Link: " + link);
                    task.execute(link);
                }
                else{
                    check = data.getString("link");
                    Log.d("Upload", "Image Link: " + check);
                    task.execute(check);
                }



                // You can use the 'link' variable here to do whatever you need to do with the uploaded image URL
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public int compareJsonObjects(JSONObject json1, JSONObject json2) throws JSONException {
        int similarityPercentage = 0;
        Map<String, Double> map1 = new HashMap<String, Double>();
        Map<String, Double> map2 = new HashMap<String, Double>();


        // Parse the first five tags and confidence levels from json1
        JSONObject jsonObject1 = json1;
        JSONArray predictions1 = jsonObject1.getJSONObject("result").getJSONArray("tags");
        for (int i = 0; i < Math.min(5, predictions1.length()); i++) {
            JSONObject tagObject = predictions1.getJSONObject(i);
            String tag = tagObject.getJSONObject("tag").getString("en");
            double confidence = tagObject.getDouble("confidence");
            map1.put(tag, confidence);
        }

        // Parse the tags and confidence levels from json2
        JSONObject jsonObject2 = json2  ;
        JSONArray predictions2 = jsonObject2.getJSONObject("result").getJSONArray("tags");
        for (int i = 0; i < Math.min(5, predictions2.length()); i++) {
            JSONObject tagObject = predictions2.getJSONObject(i);
            String tag = tagObject.getJSONObject("tag").getString("en");
            double confidence = tagObject.getDouble("confidence");
            map2.put(tag, confidence);
        }

        // Compare the hash maps
        int numMatchingTags = 0;
        int totalConfidence = 0;

        for (String tag : map1.keySet()) {
            if (map2.containsKey(tag)) {
                Double confidence1 = map1.get(tag);
                Double confidence2 = map2.get(tag);
                numMatchingTags++;
                totalConfidence += (100-(Math.abs(confidence1-confidence2)));
            }
        }

        // Calculate similarity percentage based on the average confidence of matching tags
        if (numMatchingTags > 0) {
            similarityPercentage = (int) ((double) totalConfidence / numMatchingTags);
        }

        return similarityPercentage;
    }


}