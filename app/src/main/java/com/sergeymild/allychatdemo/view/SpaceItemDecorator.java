package com.sergeymild.allychatdemo.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sergeymild.allychatdemo.R;

/**
 * Created by sergeyMild on 12/10/15.
 */
public class SpaceItemDecorator extends RecyclerView.ItemDecoration {
    private int margin;
    private int topMargin;

    public SpaceItemDecorator(@NonNull Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.margin_8);
        topMargin = margin;
    }

    public SpaceItemDecorator(@NonNull Context context, @DimenRes int topMargin) {
        this(context);
        this.topMargin = context.getResources().getDimensionPixelSize(topMargin);
    }
    public SpaceItemDecorator(int topMargin, int margin) {
        this.margin = margin;
        this.topMargin = topMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = margin;
        outRect.right = margin;
        outRect.bottom = margin;

        // Add top margin only for the first item to avoid double space between items
        if(parent.getChildAdapterPosition(view) == 0)
            outRect.top = topMargin;
    }
}
