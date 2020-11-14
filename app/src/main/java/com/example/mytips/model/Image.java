package com.example.mytips.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {
    private String imagePath;
    private Boolean selectionState=false;
    public Image() {
    }


    protected Image(Parcel in) {
        selectionState = in.readByte() != 0;
        imagePath = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public Boolean getSelectionState() {
        return selectionState;
    }

    public void setSelectionState(Boolean selectionState) {
        this.selectionState = selectionState;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    //compare image path
    public boolean equals(Object o) {
        if (o != null && o instanceof Image) {
            if ((( Image ) o).getImagePath() == null && imagePath != null)
                return false;
            String oPath = (( Image ) o).getImagePath().toLowerCase();
            return oPath.equals(this.imagePath.toLowerCase());
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.selectionState ? (byte) 1 : (byte) 0);
        dest.writeString(imagePath);
    }
}
