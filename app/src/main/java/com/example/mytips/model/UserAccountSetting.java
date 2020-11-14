package com.example.mytips.model;

public class UserAccountSetting {
    private String birthday;
    private String description;
    private Long follower;
    private Long following;
    private String gender;
    private Long liked;
    private String profile_photo;
    private String background_photo;
    private String residence;
    private String name;

    public UserAccountSetting(String birthday, String description, Long follower, Long following, String gender, Long liked, String profile_photo, String background_photo,String residence, String name) {
        this.birthday = birthday;
        this.description = description;
        this.follower = follower;
        this.following = following;
        this.gender = gender;
        this.liked = liked;
        this.profile_photo = profile_photo;
        this.background_photo=background_photo;
        this.residence = residence;
        this.name=name;
    }

    public UserAccountSetting() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFollower() {
        return follower;
    }

    public void setFollower(Long follower) {
        this.follower = follower;
    }

    public Long getFollowing() {
        return following;
    }

    public void setFollowing(Long following) {
        this.following = following;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getLiked() {
        return liked;
    }

    public void setLiked(Long liked) {
        this.liked = liked;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getBackground_photo() {
        return background_photo;
    }

    public void setBackground_photo(String background_photo) {
        this.background_photo = background_photo;
    }

    @Override
    public String toString() {
        return "UserAccountSetting{" +
                "birthday='" + birthday + '\'' +
                ", description='" + description + '\'' +
                ", follower=" + follower +
                ", following=" + following +
                ", gender='" + gender + '\'' +
                ", liked=" + liked +
                ", profile_photo='" + profile_photo + '\'' +
                ", background_photo='" + background_photo + '\'' +
                ", residence='" + residence + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
