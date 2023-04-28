package com.example.scavenger.playhuntfiles;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.scavenger.Checkpoint;
import com.example.scavenger.Hint;
import com.example.scavenger.R;
import com.example.scavenger.databinding.HintWindowBinding;

import java.util.ArrayList;

public class HintWindow extends Activity {

    public static Checkpoint checkpoint;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hint_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.71), (int)(height*.5));

        // set the hints on the HintWindow
        ArrayList<Hint> hints = checkpoint.getHints();
        ((TextView)findViewById(R.id.hint1textview)).setText(hints.get(0).getDescription());
        ((TextView)findViewById(R.id.hint2textview)).setText(hints.get(1).getDescription());
        ((TextView)findViewById(R.id.hint3textview)).setText(hints.get(2).getDescription());

    }



}
