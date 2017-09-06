package com.example.mrl.marketstall.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrl.marketstall.R;

public class SpinnerViewHolder
{
    public TextView title1;
    public TextView title2;
    public ImageView imageView;

    public SpinnerViewHolder(final View itemView)
    {
        title1 = (TextView) itemView.findViewById(R.id.text_title1);
        title2 = (TextView) itemView.findViewById(R.id.text_title2);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
    }
}
