package com.example.mrl.marketstall.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.mrl.marketstall.R;

public class RecyclerViewHolderFormRatingBar extends RecyclerView.ViewHolder
{
    public ImageView icon;
    public RatingBar ratingBar;

    public RecyclerViewHolderFormRatingBar(final View itemView)
    {
        super(itemView);
        icon = itemView.findViewById(R.id.icon);
        ratingBar = itemView.findViewById(R.id.rating_bar);
    }
}
