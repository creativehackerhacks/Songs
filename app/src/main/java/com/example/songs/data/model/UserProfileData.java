package com.example.songs.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserProfileData implements Parcelable {

    private String mUserId;
    private String mImageUrl;
    private String mName;
    private String mUserName;

    public UserProfileData() {

    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUserId);
        dest.writeString(this.mImageUrl);
        dest.writeString(this.mName);
        dest.writeString(this.mUserName);
    }

    protected UserProfileData(Parcel in) {
        this.mUserId = in.readString();
        this.mImageUrl = in.readString();
        this.mName = in.readString();
        this.mUserName = in.readString();
    }

    public static final Parcelable.Creator<UserProfileData> CREATOR = new Parcelable.Creator<UserProfileData>() {
        @Override
        public UserProfileData createFromParcel(Parcel source) {
            return new UserProfileData(source);
        }

        @Override
        public UserProfileData[] newArray(int size) {
            return new UserProfileData[size];
        }
    };
}
