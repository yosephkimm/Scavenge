package com.example.scavenger.leaderboardfiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.scavenger.Hunt;
import com.example.scavenger.R;

import java.util.ArrayList;

public class HuntArrayAdapter extends ArrayAdapter<Hunt> implements Filterable {
    Fragment currentFragment;

    private ArrayList<Hunt> huntArrayList;

    private ArrayList<Hunt> filteredList;

    private CustomFilter filter;

    // code modified from https://www.geeksforgeeks.org/gridview-in-android-with-example/#

    public HuntArrayAdapter(@NonNull Context context, ArrayList<Hunt> huntArrayList, Fragment fragment) {
        super(context, 0, huntArrayList);
        currentFragment = fragment;
        this.huntArrayList = new ArrayList<>(huntArrayList);
        this.filteredList = new ArrayList<>(huntArrayList);
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
        creatoremailTV.setText("Made by: " + hunt.getCreator());
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
        LeaderboardFragment.hunt = hunt;
        NavHostFragment.findNavController(currentFragment)
                .navigate(R.id.action_leaderboardMainFragment_to_leaderboardFragment); // LeaderboardMainFragment to LeaderboardFragment
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Hunt getItem(int position) {
        return filteredList.get(position);
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<Hunt> tempList = new ArrayList<>();
            if (constraint != null && constraint.length() > 0) {
                for (Hunt hunt : huntArrayList) {
                    if (hunt.getName().contains(constraint.toString().toLowerCase())) {
                        tempList.add(hunt);
                    }
                }
                results.count = tempList.size();
                results.values = tempList;
            } else {
                results.count = huntArrayList.size();
                results.values = huntArrayList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Hunt>) results.values;
            notifyDataSetChanged();
        }
    }
}
