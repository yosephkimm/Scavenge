package com.example.scavenger.edithuntfiles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.example.scavenger.Checkpoint;
import com.example.scavenger.playhuntfiles.Hunt;
import com.example.scavenger.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class EditCheckpointWindow extends Activity {

    public static Checkpoint checkpoint;

    private EditText hint1box;

    private EditText hint2box;

    private EditText hint3box;

    private EditText descriptionbox;

    private int color;

    private int checkpointCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_checkpoint_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .75), (int) (height * .9));

        descriptionbox = findViewById(R.id.editdescriptionbox);
        hint1box = findViewById(R.id.edithint1box);
        hint2box = findViewById(R.id.edithint2box);
        hint3box = findViewById(R.id.edithint3box);

        descriptionbox.setText(checkpoint.getDescription());
        hint1box.setText(checkpoint.getHints().get(0).getDescription());
        hint2box.setText(checkpoint.getHints().get(1).getDescription());
        hint3box.setText(checkpoint.getHints().get(2).getDescription());

        findViewById(R.id.doneeditcp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        findViewById(R.id.canceleditcp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.redflagimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Checkpoint.RED;
            }
        });

        findViewById(R.id.blueflagimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Checkpoint.BLUE;
            }
        });

        findViewById(R.id.yellowflagimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Checkpoint.YELLOW;
            }
        });

        findViewById(R.id.blackflagimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = Checkpoint.BLACK;
            }
        });
        findViewById(R.id.blackflagimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),105);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 105 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/image" + "size" + ".jpg");
        }
    }

    private void save(){
        checkpoint.setColor(color);
        checkpoint.setDescription(String.valueOf(descriptionbox.getText()));
        checkpoint.getHints().get(0).setDescription(String.valueOf(hint1box.getText()));
        checkpoint.getHints().get(1).setDescription(String.valueOf(hint2box.getText()));
        checkpoint.getHints().get(2).setDescription(String.valueOf(hint3box.getText()));
        FirebaseFirestore.getInstance().collection("Hunts")
                .document(checkpoint.getHunt().getName())
                .update("checkpoints",checkpoint.getHunt().getCheckpoints());
        finish();
    }

    private void getCheckpointCount() {
        FirebaseFirestore.getInstance().collection("Hunts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            ArrayList<Hunt> huntArrayList = new ArrayList<Hunt>();
                            for (DocumentSnapshot d : list) {
                                huntArrayList.add(d.toObject(Hunt.class));
                            }
                            int counter = 0;
                            for (Hunt hunt : huntArrayList) {
                                counter += hunt.getCheckpoints().size();
                            }
                            checkpointCount = counter;
                        } else {
                            System.out.println("Hunts database collection is empty!");
                            // maybe display something that says "user has no hunts created yet"?
                        }
                    }
                });
    }
}