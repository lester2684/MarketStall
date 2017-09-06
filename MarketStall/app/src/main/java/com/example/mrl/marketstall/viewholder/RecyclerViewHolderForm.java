package com.example.mrl.marketstall.viewholder;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mrl.marketstall.R;

public class RecyclerViewHolderForm extends RecyclerView.ViewHolder
{
    public ImageView icon;
    public TextInputLayout textInputLayout;
    public EditText editText;

    public RecyclerViewHolderForm(final View itemView)
    {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        textInputLayout = (TextInputLayout) itemView.findViewById(R.id.text_input_layout);
        editText = (EditText) itemView.findViewById(R.id.edit_text);
    }
}
