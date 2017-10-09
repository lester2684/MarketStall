package com.example.mrl.marketstall.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.mrl.marketstall.R;

public class RecyclerViewHolderFormSpinner extends RecyclerView.ViewHolder
{
    public ImageView icon;
    public Spinner spinner;

    public RecyclerViewHolderFormSpinner(final View itemView)
    {
        super(itemView);
        icon = itemView.findViewById(R.id.icon);
        spinner = itemView.findViewById(R.id.spinner);
    }
}
