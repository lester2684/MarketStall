package com.example.mrl.marketstall.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrl.marketstall.R;

public class RecyclerViewHolderDetailTextView extends RecyclerView.ViewHolder
{
    public ImageView icon;
    public TextView text;

    public RecyclerViewHolderDetailTextView(final View itemView)
    {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        text = (TextView) itemView.findViewById(R.id.text);
    }
}
