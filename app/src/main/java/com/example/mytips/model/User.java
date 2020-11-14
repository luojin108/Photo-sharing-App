package com.example.mytips.model;

import java.util.ArrayList;

public class User {
    private UserAccountSetting userAccountSetting;
    private UserInfo userInfo;
    private ArrayList<ImagePosts> userImagePostList;

    public User(UserAccountSetting userAccountSetting, UserInfo userInfo) {
        this.userAccountSetting = userAccountSetting;
        this.userInfo = userInfo;
    }
    public User() {
    }
    public ArrayList<ImagePosts> getUserImagePostList() {
        return userImagePostList;
    }
    public void setUserImagePostList(ArrayList<ImagePosts> userImagePostList) {
        this.userImagePostList = userImagePostList;
    }

    public UserAccountSetting getUserAccountSetting() {
        return userAccountSetting;
    }

    public void setUserAccountSetting(UserAccountSetting userAccountSetting) {
        this.userAccountSetting = userAccountSetting;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    @Override
    public String toString() {
        return "User{" +
                "userAccountSetting=" + userAccountSetting +
                ", userInfo=" + userInfo +
                ", userImagePostList=" + userImagePostList +
                '}';
    }

}
