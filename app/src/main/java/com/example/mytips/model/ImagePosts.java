package com.example.mytips.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ImagePosts{
    private String title;
    private String tags;
    private String description;
    private String user_id;
    private String date_posted;
    private String post_id;
    private String profile_photo;
    private String user_name;
    private int number_of_like;
    private long time_stamp;
    private ArrayList<String> http_uri=new ArrayList<>();
    private ArrayList<Integer> imageHeights=new ArrayList<>();
    private ArrayList<Integer> imageWidths=new ArrayList<>();


    public ImagePosts() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(String date_posted) {
        this.date_posted = date_posted;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getNumber_of_like() {
        return number_of_like;
    }

    public void setNumber_of_like(int number_of_like) {
        this.number_of_like = number_of_like;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public ArrayList<String> getHttp_uri() {
        return http_uri;
    }

    public void setHttp_uri(ArrayList<String> http_uri) {
        this.http_uri = http_uri;
    }

    public ArrayList<Integer> getImageHeights() {
        return imageHeights;
    }

    public void setImageHeights(ArrayList<Integer> imageHeights) {
        this.imageHeights = imageHeights;
    }

    public ArrayList<Integer> getImageWidths() {
        return imageWidths;
    }

    public void setImageWidths(ArrayList<Integer> imageWidths) {
        this.imageWidths = imageWidths;
    }

    public void addHttpUri(String string){
        http_uri.add(string);
    }

}
