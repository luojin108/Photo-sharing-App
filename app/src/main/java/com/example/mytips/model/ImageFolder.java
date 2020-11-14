package com.example.mytips.model;

import java.util.ArrayList;

public class ImageFolder {

    private  String folderPath;
    private  String folderName;
    private String albumCover;
    private ArrayList<Image> imageList=new ArrayList<>();

    public ImageFolder(){

    }

    public void setImageList(ArrayList<Image> imageList) {
        this.imageList = imageList;
    }

    public ArrayList<Image> getImageList() {
        return imageList;
    }

    public String getPath() {
        return folderPath;
    }

    public void setPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }


//compare folder path
    public boolean equals(Object o) {
        if (o != null && o instanceof ImageFolder) {
            if ((( ImageFolder ) o).getPath() == null && folderPath != null)
                return false;
            String oPath = (( ImageFolder ) o).getPath().toLowerCase();
            return oPath.equals(this.folderPath.toLowerCase());
        }
        return false;
    }
}


