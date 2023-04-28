package com.example.scavenger.edithuntfiles;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scavenger.R;

public class CheckpointViewHolder extends RecyclerView.ViewHolder {

    TextView checknum;

    ImageView editview;

    ImageView deleteview;

    ImageView flagimage;

    boolean cpclicked;

    View view;

    CheckpointViewHolder(View itemView)
    {
        super(itemView);
        this.checknum = (TextView) itemView.findViewById(R.id.checknum);
        this.editview = (ImageView) itemView.findViewById(R.id.editcptextview);
        this.deleteview = (ImageView) itemView.findViewById(R.id.deletecptextview);
        this.flagimage = (ImageView) itemView.findViewById(R.id.flagimage);
        this.view  = itemView;
    }

}
