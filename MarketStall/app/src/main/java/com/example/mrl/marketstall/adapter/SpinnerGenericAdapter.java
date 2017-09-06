package com.example.mrl.marketstall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public abstract class SpinnerGenericAdapter<T> extends ArrayAdapter<T>
{
    private final String TAG = getClass().getSimpleName();

    private List<T> items;
    private int layoutResourceId;

    public abstract View getCustomView(int position, View convertView, ViewGroup parent, List<T> items, int layoutResourceId);

    protected SpinnerGenericAdapter(Context context, int layoutResourceId, List<T> items)
    {
        super(context, layoutResourceId, items);
        this.items = items;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
    {

        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        return getCustomView(position, convertView, parent, items, layoutResourceId);
    }

}
