package com.example.mytips.model;

public class UserInfo {
    private String email;
    private String user_name;

    public UserInfo(String email, String user_name) {
        this.email = email;
        this.user_name = user_name;
    }
    public UserInfo() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "email='" + email + '\'' +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}
