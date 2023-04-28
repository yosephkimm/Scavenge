package com.example.scavenger.playhuntfiles;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.scavenger.R;

public class HintWindow extends Activity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hint_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.71), (int)(height*.5));

    }

}
