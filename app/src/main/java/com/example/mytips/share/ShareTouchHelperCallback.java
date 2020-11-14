package com.example.mytips.share;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ShareTouchHelperCallback extends ItemTouchHelper.Callback {
    private Context mContext;
    public ShareTouchHelperCallback(Context context) {
        this.mContext=context;
    }

    //    set up movement types
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags=ItemTouchHelper.UP;
        int dragFlags=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags,swipeFlags);
    }
// callback when dragging the item
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        ((ShareItemTouchListener)mContext).onItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }
// callback when swiping the item
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        ((ShareItemTouchListener)mContext).onItemSwiped(viewHolder.getAdapterPosition());
    }
// activate long press drag
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
// activate item swiping
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
