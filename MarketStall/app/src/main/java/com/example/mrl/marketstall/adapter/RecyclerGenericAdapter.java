package com.example.mrl.marketstall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerGenericAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final String TAG = getClass().getSimpleName();

    private Context context;
    private List<T> items;
    private OnRecyclerItemClicked onRecyclerItemClicked;

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked);

    public abstract void onBindData(Context context, RecyclerView.ViewHolder holder, T val, int position);

    public abstract OnRecyclerItemClicked onGetRecyclerItemClickListener();

    public RecyclerGenericAdapter(Context context, List<T> items)
    {
        this.context = context;
        this.items = items;
        onRecyclerItemClicked = onGetRecyclerItemClickListener();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return setViewHolder(parent , viewType, onRecyclerItemClicked);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        onBindData(context, holder,items.get(position), position);
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }


    public void clear()
    {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<T> list)
    {
        this.items.addAll(list);
        notifyDataSetChanged();
    }

    public void setItems( ArrayList<T> savedCardItems)
    {
        items = savedCardItems;
        this.notifyDataSetChanged();
    }

    public T getItem(int position)
    {
        return items.get(position);
    }

    public interface OnRecyclerItemClicked
    {
        void onItemClicked(View view, int position);
    }

    @Override
    public int getItemCount()
    {

        return items.size();
    }
}
