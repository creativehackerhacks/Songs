package com.example.songs.data.model;

import android.os.Parcel;

public class Artist implements android.os.Parcelable {

    private long id;
    private String artistName;
    private int artistTrackCount;
    private int artistAlbumCount;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getArtistAlbumCount() {
        return artistAlbumCount;
    }

    public void setArtistAlbumCount(int artistAlbumCount) {
        this.artistAlbumCount = artistAlbumCount;
    }

    public int getArtistTrackCount() {
        return artistTrackCount;
    }

    public void setArtistTrackCount(int artistTrackCount) {
        this.artistTrackCount = artistTrackCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.artistName);
        dest.writeInt(this.artistAlbumCount);
        dest.writeInt(this.artistTrackCount);
    }

    public Artist() {
    }

    protected Artist(Parcel in) {
        this.id = in.readLong();
        this.artistName = in.readString();
        this.artistAlbumCount = in.readInt();
        this.artistTrackCount = in.readInt();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
