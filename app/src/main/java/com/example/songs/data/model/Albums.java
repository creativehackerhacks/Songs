package com.example.songs.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Albums implements Parcelable {

    private long mTrackId;
    private String mAlbumData;
    private String mAlbumName;
    private int mAlbum_NoOfSongs;
    private int albumYear;

    public Albums() {
    }

    public long getTrackId() {
        return mTrackId;
    }

    public void setTrackId(long trackId) {
        mTrackId = trackId;
    }

    public String getAlbumData() {
        return mAlbumData;
    }

    public void setAlbumData(String albumData) {
        mAlbumData = albumData;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public int getAlbum_NoOfSongs() {
        return mAlbum_NoOfSongs;
    }

    public void setAlbum_NoOfSongs(int album_NoOfSongs) {
        mAlbum_NoOfSongs = album_NoOfSongs;
    }

    public int getAlbumYear() {
        return albumYear;
    }

    public void setAlbumYear(int albumYear) {
        this.albumYear = albumYear;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mTrackId);
        dest.writeString(this.mAlbumData);
        dest.writeString(this.mAlbumName);
        dest.writeInt(this.mAlbum_NoOfSongs);
        dest.writeInt(this.albumYear);
    }

    protected Albums(Parcel in) {
        this.mTrackId = in.readLong();
        this.mAlbumData = in.readString();
        this.mAlbumName = in.readString();
        this.mAlbum_NoOfSongs = in.readInt();
        this.albumYear = in.readInt();
    }

    public static final Creator<Albums> CREATOR = new Creator<Albums>() {
        @Override
        public Albums createFromParcel(Parcel source) {
            return new Albums(source);
        }

        @Override
        public Albums[] newArray(int size) {
            return new Albums[size];
        }
    };
}
