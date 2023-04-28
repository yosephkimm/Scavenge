package com.example.scavenger.playhuntfiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scavenger.Checkpoint;
import com.example.scavenger.R;
import com.example.scavenger.edithuntfiles.CheckpointRVAdapter;
import com.example.scavenger.edithuntfiles.CheckpointViewHolder;

import java.util.Collections;
import java.util.List;

public class PlayCheckpointRVAdapter extends RecyclerView.Adapter<PlayCheckpointViewHolder> {

    List<Checkpoint> list = Collections.emptyList();

    private Fragment currentFragment;

    Context context;

    public PlayCheckpointRVAdapter(List<Checkpoint> list, Context context, Fragment currentFragment)
    {
        this.list = list;
        this.context = context;
        this.currentFragment = currentFragment;
    }

    @NonNull
    @Override
    public PlayCheckpointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View photoView = inflater.inflate(R.layout.play_checkpoint_card_view, parent, false);

        return new PlayCheckpointViewHolder(photoView);
    }

    public void onBindViewHolder(final PlayCheckpointViewHolder viewHolder, final int position) {
        Checkpoint checkpoint = list.get(position);
        viewHolder.view.findViewById(R.id.cpcardviewplayhunt).setBackgroundColor(Color.TRANSPARENT);
        viewHolder.flagimage.setImageResource(checkpoint.getColor());
        viewHolder.checknum.setText((position + 1) + "");

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlayHunt)currentFragment).displayHints();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void add(Checkpoint checkpoint) {
        this.list.add(checkpoint);
    }

}
