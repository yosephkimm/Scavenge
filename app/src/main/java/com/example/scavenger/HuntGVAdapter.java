package com.example.scavenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

public class HuntGVAdapter extends ArrayAdapter<Hunt> {

    Fragment currentFragment;

    // code modified from https://www.geeksforgeeks.org/gridview-in-android-with-example/#

    public HuntGVAdapter(@NonNull Context context, ArrayList<Hunt> huntArrayList, Fragment fragment) {
        super(context, 0, huntArrayList);
        currentFragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.hunt_card_view, parent, false);
        }

        // get the hunt and set the hunt name and description text
        Hunt hunt = getItem(position);
        TextView huntnameTV = listitemView.findViewById(R.id.huntname);
        TextView huntdescTV = listitemView.findViewById(R.id.huntdesc);
        listitemView.findViewById(R.id.edithuntbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editHunt(hunt);
            }
        });

        huntnameTV.setText(hunt.getName());
        huntdescTV.setText(hunt.getDesc());
        return listitemView;
    }

    private void editHunt(Hunt hunt) {
        EditHuntFragment.hunt = hunt;
        NavHostFragment.findNavController(currentFragment)
                .navigate(R.id.action_creatorHomePageFragment_to_editHuntFragment);
    }

}
