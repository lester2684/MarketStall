package com.example.mrl.marketstall.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration
{

    private int itemOffsetLeft;
    private int itemOffsetTop;
    private int itemOffsetRight;
    private int itemOffsetBottom;

    public ItemOffsetDecoration(int itemOffsetLeft, int itemOffsetTop, int itemOffsetRight, int itemOffsetBottom)
    {
        this.itemOffsetLeft = itemOffsetLeft;
        this.itemOffsetTop = itemOffsetTop;
        this.itemOffsetRight = itemOffsetRight;
        this.itemOffsetBottom = itemOffsetBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(itemOffsetLeft, itemOffsetTop, itemOffsetRight, itemOffsetBottom);
    }
}