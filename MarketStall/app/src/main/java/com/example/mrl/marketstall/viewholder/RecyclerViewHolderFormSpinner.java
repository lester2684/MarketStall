package com.example.mrl.marketstall.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mrl.marketstall.R;

public class RecyclerViewHolderFormSpinner extends RecyclerView.ViewHolder
{
    public ImageView icon;
    public Spinner spinner;
    public TextView textView;
    public ImageView button;

    public RecyclerViewHolderFormSpinner(final View itemView)
    {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        spinner = (Spinner) itemView.findViewById(R.id.spinner);
        textView = (TextView) itemView.findViewById(R.id.text_view);
        button = (ImageView) itemView.findViewById(R.id.button);
    }
}
