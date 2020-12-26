package com.example.mytips.model;

import java.util.ArrayList;

public class Comment {
    private String post_id,user_id,comment_id,comment_text;
    private Long time_stamp;
    private ArrayList<Reply> replies,hiddenReplies;


    public Comment(){}

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public Long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public ArrayList<Reply> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Reply> replies) {
        this.replies = replies;
    }

    public ArrayList<Reply> getHiddenReplies() {
        return hiddenReplies;
    }

    public void setHiddenReplies(ArrayList<Reply> hiddenReplies) {
        this.hiddenReplies = hiddenReplies;
    }
}
