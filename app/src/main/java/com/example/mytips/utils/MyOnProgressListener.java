package com.example.mytips.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MyOnProgressListener implements OnProgressListener<UploadTask.TaskSnapshot> {
    private int i;
    private ArrayList<String> imageUriList;
    private Context context;

    public MyOnProgressListener(Context context,int i, ArrayList<String> imageUriList) {
        this.context=context;
        this.i = i;
        this.imageUriList = imageUriList;
    }

    @Override
    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
        double progress=(double)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
        double imageUploadProgress=progress*(double) ((i+1)/imageUriList.size());
        ((OnUploadProgressListener)context).onUploadImagePosts(imageUploadProgress);
    }
    public interface OnUploadProgressListener{
        void onUploadImagePosts(double progress);
    }
}
