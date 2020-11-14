package com.example.mytips.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageRotator {
    public ImageRotator() {
    }
    public  Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
