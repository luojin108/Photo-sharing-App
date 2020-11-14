package com.example.mytips.share;

public interface ShareItemTouchListener {
    void onItemSwiped(int position);
    void onItemMoved(int positionFrom, int positionTo);
}
