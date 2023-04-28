package com.example.scavenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.scavenger.leaderboardfiles.PlayerTime;

import java.util.ArrayList;
import java.util.Comparator;

public class PlayerTimeAdapter extends ArrayAdapter<PlayerTime> {
    Fragment currentFragment;

    ArrayList<PlayerTime> timeArrayList;

    // code modified from https://www.geeksforgeeks.org/gridview-in-android-with-example/#

    public PlayerTimeAdapter(@NonNull Context context, ArrayList<PlayerTime> timeList, Fragment fragment) {
        super(context, 0, timeList);
        currentFragment = fragment;
        timeArrayList = timeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_time_card, parent, false);
        }

        // get the hunt and set the hunt name and description text
        PlayerTime playerTime = getItem(position);
        TextView playernameTV = listitemView.findViewById(R.id.playername);
        TextView playertimeTV = listitemView.findViewById(R.id.playertime);
        ImageView cardbg = listitemView.findViewById(R.id.huntcardbg);

        playernameTV.setText(playerTime.getPlayerId());
        playertimeTV.setText(playerTime.getTime() + "");
        if (timeArrayList.indexOf(playerTime) == 0) cardbg.setImageResource(R.drawable.goldbg);
        else if (timeArrayList.indexOf(playerTime) == 1) cardbg.setImageResource(R.drawable.silverbg);
        else if (timeArrayList.indexOf(playerTime) == 2) cardbg.setImageResource(R.drawable.bronzebg);
        else cardbg.setImageResource(R.drawable.woodenbg);

        return listitemView;
    }
}