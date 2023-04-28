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
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HuntArrayAdapter extends ArrayAdapter<Hunt> {
    Fragment currentFragment;

    // code modified from https://www.geeksforgeeks.org/gridview-in-android-with-example/#

    public HuntArrayAdapter(@NonNull Context context, ArrayList<Hunt> huntArrayList, Fragment fragment) {
        super(context, 0, huntArrayList);
        currentFragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_card_view, parent, false);
        }

        // get the hunt and set the hunt name and description text
        Hunt hunt = getItem(position);
        TextView huntnameTV = listItemView.findViewById(R.id.huntname);
        TextView huntdescTV = listItemView.findViewById(R.id.huntdesc);
        TextView creatoremailTV = listItemView.findViewById(R.id.creatoremail);
        ImageView cardbg = listItemView.findViewById(R.id.huntcardbg);
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaderboardHunt(hunt);
            }
        });

        huntnameTV.setText(hunt.getName());
        huntdescTV.setText(hunt.getDesc());
        creatoremailTV.setText("Made by " + hunt.getCreator());
        if (hunt.getbgcolor().equalsIgnoreCase("Blue")) cardbg.setImageResource(R.drawable.bluebg);
        else if (hunt.getbgcolor().equalsIgnoreCase("Orange")) cardbg.setImageResource(R.drawable.orangebg);
        else cardbg.setImageResource(R.drawable.redbg);
        return listItemView;
    }

    /**
     * Navigate to the LeaderboardFragment
     * @post Leaderboard.hunt = hunt
     * @param hunt the Hunt that is being edited
     */
    private void leaderboardHunt(Hunt hunt) {
        EditHuntFragment.hunt = hunt;
        NavHostFragment.findNavController(currentFragment)
                .navigate(R.id.action_leaderboardMainFragment_to_leaderboardFragment); // LeaderboardMainFragment to LeaderboardFragment
    }

}
