package com.example.scavenger;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditCheckpointWindow extends Activity {

    public static Checkpoint checkpoint;

    private EditText hint1box;

    private EditText hint2box;

    private EditText hint3box;

    private EditText descriptionbox;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_checkpoint_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .75), (int) (height * .6));

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

    }

    private void save(){
        checkpoint.setDescription(String.valueOf(descriptionbox.getText()));
        checkpoint.getHints().get(0).setDescription(String.valueOf(hint1box.getText()));
        checkpoint.getHints().get(1).setDescription(String.valueOf(hint2box.getText()));
        checkpoint.getHints().get(2).setDescription(String.valueOf(hint3box.getText()));
        FirebaseFirestore.getInstance().collection("Hunts")
                .document(checkpoint.getHunt().getName())
                .update("checkpoints",checkpoint.getHunt().getCheckpoints());
    }
}