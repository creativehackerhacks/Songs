package com.example.songs.archComp;

import android.app.Application;

import com.example.songs.data.loaders.TrackData;
import com.example.songs.data.model.Track;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class TrackModel extends AndroidViewModel {

    private TrackData mTrackData;

    // This is the data that we fetch Asynchronously
    private List<Track> mTrackListData;
    private List<Track> mAlbumTrackListData;

    public TrackModel(@NonNull Application application) {
        super(application);
        mTrackData = new TrackData(application);
    }

    // To Load "Tracks"
    public List<Track> getTracks() {
        if(mTrackListData == null) {
            mTrackListData = loadTracks();
        }
        return mTrackListData;
    }

    private List<Track> loadTracks() {
        return mTrackData.getTrackList();
    }



    // To Load "Album Tracks"
    public List<Track> getAlbumTracks(long albumId) {
        if(mAlbumTrackListData == null) {
            mAlbumTrackListData = loadAlbumTracks(albumId);
        }
        return mAlbumTrackListData;
    }

    public List<Track> loadAlbumTracks(long albumId) {
        return mTrackData.getAlbumTrackList(albumId);
    }

}
