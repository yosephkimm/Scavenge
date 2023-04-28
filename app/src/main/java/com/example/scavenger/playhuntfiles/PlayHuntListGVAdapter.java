package com.example.scavenger.playhuntfiles;

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
import androidx.navigation.fragment.NavHostFragment;

import com.example.scavenger.Hunt;
import com.example.scavenger.R;
import com.example.scavenger.playhuntfiles.PlayHunt;

import java.util.ArrayList;

public class PlayHuntListGVAdapter extends ArrayAdapter<Hunt> {

    // code modified from https://www.geeksforgeeks.org/gridview-in-android-with-example/#

    Fragment currentFragment;

    public PlayHuntListGVAdapter(@NonNull Context context, ArrayList<Hunt> huntArrayList, Fragment fragment) {
        super(context, 0, huntArrayList);
        currentFragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.play_hunt_list_card_view, parent, false);
        }

        Hunt hunt = getItem(position);

        TextView huntcreatorTV = listitemView.findViewById(R.id.huntcreatorplayhuntlist);
        TextView huntnameTV = listitemView.findViewById(R.id.huntnameplayhuntlist);
        TextView huntdescTV = listitemView.findViewById(R.id.huntdescplayhuntlist);
        ImageView cardbg = listitemView.findViewById(R.id.huntcardbgplayhuntlist);

        huntcreatorTV.setText(hunt.getCreator());
        huntnameTV.setText(hunt.getName());
        huntdescTV.setText(hunt.getDesc());
        if (hunt.getbgcolor().equalsIgnoreCase("Blue")) cardbg.setImageResource(R.drawable.bluebg);
        else if (hunt.getbgcolor().equalsIgnoreCase("Orange"))
            cardbg.setImageResource(R.drawable.orangebg);
        else cardbg.setImageResource(R.drawable.redbg);

        listitemView.findViewById(R.id.playbuttonplayhuntlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playHunt(hunt);
            }
        });

        return listitemView;

    }

    private void playHunt(Hunt hunt) {
        PlayHunt.hunt = hunt;
        NavHostFragment.findNavController(currentFragment)
                .navigate(R.id.action_playHuntListFragment_to_PlayHunt);
    }
}
