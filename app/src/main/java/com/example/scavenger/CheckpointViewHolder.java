package com.example.scavenger;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CheckpointViewHolder extends RecyclerView.ViewHolder {

    TextView checknum;

    TextView editview;

    TextView deleteview;

    boolean cpclicked;

    View view;

    CheckpointViewHolder(View itemView)
    {
        super(itemView);
        this.checknum = (TextView) itemView.findViewById(R.id.checknum);
        this.editview = (TextView) itemView.findViewById(R.id.editcptextview);
        this.deleteview = (TextView) itemView.findViewById(R.id.deletecptextview);
        this.view  = itemView;
    }

}
