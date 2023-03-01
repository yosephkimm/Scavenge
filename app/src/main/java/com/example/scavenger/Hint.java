package com.example.scavenger;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class Hint extends Activity {
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
