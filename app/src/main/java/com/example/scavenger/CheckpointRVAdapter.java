package com.example.scavenger;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scavenger.Checkpoint;
import com.example.scavenger.CheckpointViewHolder;
import com.example.scavenger.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckpointRVAdapter extends RecyclerView.Adapter<CheckpointViewHolder> {

    List<Checkpoint> list = Collections.emptyList();

    private Fragment currentFragment;

    Context context;

    public CheckpointRVAdapter(List<Checkpoint> list, Context context, Fragment currentFragment)
    {
        this.list = list;
        this.context = context;
        this.currentFragment = currentFragment;
    }

    @NonNull
    @Override
    public CheckpointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View photoView = inflater.inflate(R.layout.checkpoint_card_view, parent, false);

        return new CheckpointViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(final CheckpointViewHolder viewHolder, final int position) {
        Checkpoint checkpoint = list.get(position);
        String text = (checkpoint.getPosition()+1)+"";
        viewHolder.checknum.setText(text);
        CheckpointRVAdapter adapter = this;
        viewHolder.view.findViewById(R.id.cpcardview).setBackgroundColor(Color.TRANSPARENT);
        viewHolder.flagimage.setImageResource(checkpoint.getColor());

        viewHolder.view.findViewById(R.id.editcptextview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditCheckpointWindow.checkpoint = checkpoint;
                currentFragment.startActivity(new Intent(currentFragment.getActivity(), EditCheckpointWindow.class));
                ((Activity) currentFragment.getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        });

        viewHolder.view.findViewById(R.id.deletecptextview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = checkpoint.getPosition();
                checkpoint.getMarker().remove();
                list.remove(checkpoint);
                ((EditHuntFragment)currentFragment).refresh(adapter, pos);
            }
        });

        // when one of the checkpoints is clicked on
        viewHolder.view.findViewById(R.id.cpcardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!viewHolder.cpclicked) {
                    viewHolder.view.findViewById(R.id.cpcardview).animate().translationYBy(-100f).setDuration(500);
                    viewHolder.deleteview.setVisibility(View.VISIBLE);
                    viewHolder.editview.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.view.findViewById(R.id.cpcardview).animate().translationYBy(100f).setDuration(500);
                    viewHolder.deleteview.setVisibility(View.INVISIBLE);
                    viewHolder.editview.setVisibility(View.INVISIBLE);
                }
                viewHolder.cpclicked = !viewHolder.cpclicked;
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

    public void add(Checkpoint cp) {this.list.add(cp);}

    public ArrayList<Checkpoint> getItems() {return (ArrayList<Checkpoint>)this.list;}

}

