package com.example.scavenger.edithuntfiles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.example.scavenger.Checkpoint;
import com.example.scavenger.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditCheckpointWindow extends Activity {

    public static Checkpoint checkpoint;

    private EditText hint1box;

    private EditText hint2box;

    private EditText hint3box;

    private EditText descriptionbox;

    private int color;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_checkpoint_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .75), (int) (height * .85));

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
}