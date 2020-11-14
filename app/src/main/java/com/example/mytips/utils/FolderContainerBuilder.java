package com.example.mytips.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.mytips.model.Image;
import com.example.mytips.model.ImageFolder;

import java.io.File;
import java.util.ArrayList;

public class FolderContainerBuilder {
    private Context mContext;
    public FolderContainerBuilder(Context context) {
        this.mContext=context;
    }
    //Method to add image folders and corresponding information from media store to a array list.
    public ArrayList<ImageFolder> buildImageFolderContainer(){
        ArrayList<ImageFolder> imageFolderContainer= new ArrayList<>();
        ArrayList<Image> allImages = new ArrayList<>();
        Uri allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.i("alliamgeuri", "buildImageFolderContainer: "+allImagesUri.toString());
        String[] projection = { MediaStore.Images.ImageColumns.DATA , MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.DATE_MODIFIED };
        Cursor cursor = mContext.getContentResolver().query(allImagesUri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED+" DESC");
        ImageFolder allImagesFolder=new ImageFolder();
        allImagesFolder.setFolderName("All images");
        allImagesFolder.setPath("");
        imageFolderContainer.add(allImagesFolder);
        if(cursor!=null) {
            cursor.moveToFirst();
            do {
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                if(new File(imagePath).exists()){
                    String folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    Image image=new Image();
                    image.setImagePath(imagePath);
                    allImagesFolder.setAlbumCover(image.getImagePath());
                    allImages.add(image);
                    //......//
                    File imageFile=new File(imagePath);
                    String folderPath=imageFile.getParentFile().getAbsolutePath();
                    //......//
                    ImageFolder imageFolder = new ImageFolder();
                    imageFolder.setFolderName(folderName);
                    imageFolder.setPath(folderPath);
                    //this is to compare the folder paths
                    if(!imageFolderContainer.contains(imageFolder)){
                        imageFolderContainer.add(imageFolder);
                        imageFolder.getImageList().add(image);
                        imageFolder.setAlbumCover(image.getImagePath());
                    }else {
                        imageFolderContainer.get(imageFolderContainer.indexOf(imageFolder)).getImageList().add(image);
                    }
                }
            } while (cursor.moveToNext());
        }
        allImagesFolder.getImageList().addAll(allImages);
        return imageFolderContainer;
    }
}
