package com.example.scavenger.playhuntfiles;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scavenger.R;

public class PlayCheckpointViewHolder extends RecyclerView.ViewHolder {

    ImageView flagimage;

    TextView checknum;

    CardView cardView;
    View view;

    PlayCheckpointViewHolder(View itemView) {
        super(itemView);
        this.flagimage = itemView.findViewById(R.id.flagimageplayhunt);
        this.checknum = itemView.findViewById(R.id.checknumplayhunt);
        this.cardView = itemView.findViewById(R.id.cpcardviewplayhunt);
        this.view  = itemView;
    }

}