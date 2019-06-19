package com.example.songs.data.model;

import java.util.List;
import java.util.Map;

public class FollowingUser {

    private String mUserId;
    private String mImageUrl;
    private Map<String, String> mFollowingUserProfile;

    public FollowingUser() {
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public Map<String, String> getmFollowingUserProfile() {
        return mFollowingUserProfile;
    }

    public void setmFollowingUserProfile(Map<String, String> mFollowingUserProfile) {
        this.mFollowingUserProfile = mFollowingUserProfile;
    }
}
