package com.example.scavenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.example.scavenger.databinding.FragmentPlayHuntBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PlayHunt extends Fragment {

    private FragmentPlayHuntBinding binding;
    private ImageView imageView;


    private Double time = 0.0;
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
            binding.buttonHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), Hint.class));
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
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),105);
            }
        });
        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(R.drawable.ic_menu_gallery);
            }
        });


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