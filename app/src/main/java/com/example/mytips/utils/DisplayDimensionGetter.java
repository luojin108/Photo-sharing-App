package com.example.mytips.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayDimensionGetter {
    public float getDisplayWidthInDp(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels/ displayMetrics.density;
    }
    public float getDisplayLengthInDp(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels/ displayMetrics.density;
    }

}
