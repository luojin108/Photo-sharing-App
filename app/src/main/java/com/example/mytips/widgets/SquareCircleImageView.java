package com.example.mytips.widgets;

import android.content.Context;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class SquareCircleImageView extends CircleImageView {
    public SquareCircleImageView(Context context) {
        super(context);
    }

    public SquareCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
