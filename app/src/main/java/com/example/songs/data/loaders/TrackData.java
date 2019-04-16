package com.example.songs.data.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;

import com.example.songs.base.BaseAsyncTaskLoader;
import com.example.songs.data.model.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

public class TrackData extends BaseAsyncTaskLoader<List<Track>> {
    private String Where = null;
    private String sortorder = null;
    private String[] selectionargs = null;
    private String mFilter = null;


    private String[] datacol = {MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.COMPOSER
    };

    public TrackData(@NonNull Context context) {
        super(context);
    }

    public List<Track> getTrackList() {
        List<Track> trackList = new ArrayList<>();

        Uri trackUri = Media.EXTERNAL_CONTENT_URI;

        String filter = MediaStore.Audio.Media.IS_MUSIC + " !=0";//+ " OR " + MediaStore.Audio.Media.TRACK + " !=0";
        Where = filter + " AND " + Media.DURATION + ">=" + 30;
//        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String trackSortOrder = AudioColumns.TRACK + " ASC";
//        String trackSortOrder = AudioColumns.ARTIST + " DESC, " + AudioColumns.TRACK + " DESC";

        Cursor trackCursor = getContext().getContentResolver().query(trackUri, datacol, Where, null, trackSortOrder);

        if (trackCursor != null && trackCursor.moveToFirst()) {
            int idCol = trackCursor.getColumnIndex(Media._ID);
            int titleCol = trackCursor.getColumnIndex(Media.TITLE);
            int artistCol = trackCursor.getColumnIndex(Media.ARTIST);
            int albumCol = trackCursor.getColumnIndex(Media.ALBUM);
            int albumIdCol = trackCursor.getColumnIndex(Media.ALBUM_ID);
            int trackCol = trackCursor.getColumnIndex(Media.TRACK);
            int datacol = trackCursor.getColumnIndex(Media.DATA);
            int yearCol = trackCursor.getColumnIndex(Media.YEAR);
            int durCol = trackCursor.getColumnIndex(Media.DURATION);
            int artistIdCol = trackCursor.getColumnIndex(Media.ARTIST_ID);

            do {
                long id = trackCursor.getLong(idCol);
                String title = trackCursor.getString(titleCol);
                String artist = trackCursor.getString(artistCol);
                String album = trackCursor.getString(albumCol);
                long albumId = trackCursor.getLong(albumIdCol);
                int track = trackCursor.getInt(trackCol);
                String mSongPath = trackCursor.getString(datacol);
                String year = trackCursor.getString(yearCol);
                long artistId = trackCursor.getLong(artistIdCol);
                String trackDuration = trackCursor.getString(durCol);

                Track trackSong = new Track();
                trackSong.setTrackId(id);
                trackSong.setTrackAlbumId(albumId);
                trackSong.setTrackData(mSongPath);
                trackSong.setTrackName(title);
                trackSong.setTrackArtistName(artist);

                trackList.add(trackSong);

            } while (trackCursor.moveToNext());
            trackCursor.close();
        }
        if (trackCursor == null) {
            return Collections.emptyList();
        }
        return trackList;
    }

    public List<Track> getAlbumTrackList(long albumTrackId) {
        List<Track> trackList = new ArrayList<>();

        Uri trackUri = Media.EXTERNAL_CONTENT_URI;
        Where = MediaStore.Audio.Media.ALBUM_ID + " = ?" + " AND " + Media.DURATION + ">=" + 30;

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        if (albumTrackId > 0) {
            selection = selection + " and album_id = " + albumTrackId;
        }

        String trackSortOrder = MediaStore.Audio.Media.TITLE + " ASC";
//        String trackSortOrder = AudioColumns.ARTIST + " DESC, " + AudioColumns.TRACK + " DESC";

        Cursor trackCursor = getContext().getContentResolver().query(trackUri, datacol, selection, null, trackSortOrder);

        if (trackCursor != null && trackCursor.moveToFirst()) {
            int idCol = trackCursor.getColumnIndex(Media._ID);
            int titleCol = trackCursor.getColumnIndex(Media.TITLE);
            int artistCol = trackCursor.getColumnIndex(Media.ARTIST);
            int albumCol = trackCursor.getColumnIndex(Media.ALBUM);
            int albumIdCol = trackCursor.getColumnIndex(Media.ALBUM_ID);
            int trackCol = trackCursor.getColumnIndex(Media.TRACK);
            int datacol = trackCursor.getColumnIndex(Media.DATA);
            int yearCol = trackCursor.getColumnIndex(Media.YEAR);
            int durCol = trackCursor.getColumnIndex(Media.DURATION);
            int artistIdCol = trackCursor.getColumnIndex(Media.ARTIST_ID);

            do {
                long id = trackCursor.getLong(idCol);
                String title = trackCursor.getString(titleCol);
                String artist = trackCursor.getString(artistCol);
                String album = trackCursor.getString(albumCol);
                long albumId = trackCursor.getLong(albumIdCol);
                int track = trackCursor.getInt(trackCol);
                String mSongPath = trackCursor.getString(datacol);
                String year = trackCursor.getString(yearCol);
                long artistId = trackCursor.getLong(artistIdCol);
                String trackDuration = trackCursor.getString(durCol);

                Track trackSong = new Track();
                trackSong.setTrackId(id);
                trackSong.setTrackAlbumId(albumId);
                trackSong.setTrackData(mSongPath);
                trackSong.setTrackName(title);
                trackSong.setTrackArtistName(artist);

                trackList.add(trackSong);

            } while (trackCursor.moveToNext());
            trackCursor.close();
        }
        if (trackCursor == null) {
            return Collections.emptyList();
        }
        return trackList;
    }


    public void setSortorder(String orderBy) {
        sortorder = orderBy;
    }

    public void filteralbumsong(String filter, String[] args) {
//        Where = filter + " AND " + MediaStore.Audio.Media.DURATION + ">=" + Helper.filterAudio(); // list song greater than 30sec duration
        selectionargs = args;
    }

    public String getFilter() {
        return mFilter;
    }

    public void setFilter(String filter) {
        mFilter = filter;
    }

}
