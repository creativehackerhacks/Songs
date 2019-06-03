package com.example.songs.archComp;

import android.app.Application;
import android.provider.MediaStore.Audio.Artists;

import com.example.songs.data.loaders.AlbumData;
import com.example.songs.data.loaders.TrackData;
import com.example.songs.data.model.Albums;
import com.example.songs.data.model.Tracks;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class TrackModel extends AndroidViewModel {

    private TrackData mTrackData;
    private AlbumData mAlbumData;

    // This is the data that we fetch Asynchronously
    private List<Tracks> mTracksListData;
    private List<Tracks> mAlbumTracksListData;
    private List<Albums> mAlbumsListData;
    private List<Tracks> mArtistsTracksListData;
    private List<Artists> mAritstsListData;

    public TrackModel(@NonNull Application application) {
        super(application);
        mTrackData = new TrackData(application);
        mAlbumData = new AlbumData(application);
    }

    // To Load "Tracks"
    public List<Tracks> getTracks() {
        if(mTracksListData == null) {
            mTracksListData = loadTracks();
        }
        return mTracksListData;
    }

    private List<Tracks> loadTracks() {
        return mTrackData.getTrackList();
    }

    // To Load "Albums"
    public List<Albums> getAlbums() {
        if(mAlbumsListData == null) {
            mAlbumsListData = loadAlbums();
        }
        return mAlbumsListData;
    }

    private List<Albums> loadAlbums() {
        return mAlbumData.getAlbumArrayList();
    }



    // To Load "Album Tracks"
    public List<Tracks> getAlbumTracks(long albumId) {
//        if(mAlbumTracksListData == null) {
//            mAlbumTracksListData = loadAlbumTracks(albumId);
//        }
        return loadAlbumTracks(albumId);
    }

    private List<Tracks> loadAlbumTracks(long albumId) {
        return mTrackData.getAlbumTrackList(albumId);
    }

    public List<Tracks> getArtistsTracks(String artistName) {
        return loadArtistsTracks(artistName);
    }

    private List<Tracks> loadArtistsTracks(String artistsTracks) {
        return mTrackData.getArtistTrackList(artistsTracks);
    }

}
