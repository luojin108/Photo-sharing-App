package com.example.mytips.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.example.mytips.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class BitmapGetter {
    private Context context;
    public BitmapGetter(Context context) {
        this.context=context;
    }
    public Bitmap getBitMap(String uri){
        File imageFile=new File(uri);
        FileInputStream fileInputStream=null;
        Bitmap bitmap=null;
        try{
            fileInputStream=new FileInputStream(imageFile);
            bitmap= BitmapFactory.decodeStream(fileInputStream);
        }catch (FileNotFoundException e){
            Toast.makeText(context,R.string.image_load_failed,Toast.LENGTH_SHORT).show();
        }
        finally {
            try{ fileInputStream.close();
            }catch (IOException e){
                Toast.makeText(context,R.string.errors_happened,Toast.LENGTH_SHORT).show();
            }
        }
        return bitmap;
    }
}
