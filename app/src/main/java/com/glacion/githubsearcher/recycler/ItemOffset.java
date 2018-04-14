package com.glacion.githubsearcher.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemOffset extends RecyclerView.ItemDecoration {
    private final int offset;

    private ItemOffset(int offset) {
        this.offset = offset;
    }

    public ItemOffset(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int top = offset;
        if (parent.getChildAdapterPosition(view) == 0) top = 0;
        outRect.set(offset , top, offset, offset);
    }
}
