package com.example.mrl.marketstall.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.mikhaellopez.circularimageview.CircularImageView;

public class CardViewHolder extends RecyclerView.ViewHolder
{
    public TextView title1;
    public CircularImageView circularImageView;

    public CardViewHolder(final View itemView, final RecyclerGenericAdapter.OnRecyclerItemClicked onRecyclerItemClicked)
    {
        super(itemView);
        title1 = (TextView) itemView.findViewById(R.id.text_title1);
        circularImageView = (CircularImageView) itemView.findViewById(R.id.image_view);

        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onRecyclerItemClicked.onItemClicked(itemView, getAdapterPosition());
            }
        });
    }
}
