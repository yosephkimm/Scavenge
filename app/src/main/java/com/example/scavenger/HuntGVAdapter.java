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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        ImageView cardbg = listitemView.findViewById(R.id.huntcardbg);
        listitemView.findViewById(R.id.edithuntbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editHunt(hunt);
            }
        });

        listitemView.findViewById(R.id.deletehuntbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteHunt(hunt);
            }
        });

        huntnameTV.setText(hunt.getName());
        huntdescTV.setText(hunt.getDesc());
        if (hunt.getbgcolor().equalsIgnoreCase("Blue")) cardbg.setImageResource(R.drawable.bluebg);
        else if (hunt.getbgcolor().equalsIgnoreCase("Orange")) cardbg.setImageResource(R.drawable.orangebg);
        else cardbg.setImageResource(R.drawable.redbg);

        return listitemView;
    }

    /**
     * Navigate to the EditHuntFragment
     * @post EditHuntFragment.hunt = hunt
     * @param hunt the Hunt that is being edited
     */
    private void editHunt(Hunt hunt) {
        EditHuntFragment.hunt = hunt;
        NavHostFragment.findNavController(currentFragment)
                .navigate(R.id.action_creatorHomePageFragment_to_editHuntFragment);
    }

    /**
     * Delete a hunt from the Firestore database and update the list of hunts
     * @param hunt The Hunt to delete
     */
    private void deleteHunt(Hunt hunt) {
        FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
        CollectionReference dbHunts = firestoreDatabase.collection("Hunts");
        dbHunts.document(hunt.getName()).delete();
        NavHostFragment.findNavController(currentFragment)
                .navigate(R.id.creatorHomePageFragment);
    }

}
