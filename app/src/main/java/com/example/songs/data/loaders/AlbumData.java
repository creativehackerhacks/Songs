package com.example.songs.data.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.Media;

import com.example.songs.data.model.Albums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumData {

    private Context mAlbumContext;

    private String Where = null;
    private String sortorder = null;
    private String[] selectionargs = null;
    private String mFilter = null;

    public AlbumData(Context trackContext) {
        mAlbumContext = trackContext;
    }

    private String[] datacol = {BaseColumns._ID,
            AlbumColumns.ALBUM, AlbumColumns.ARTIST,
            AlbumColumns.NUMBER_OF_SONGS, AlbumColumns.FIRST_YEAR
    };

    public List<Albums> getAlbumArrayList() {
        List<Albums> albumsList = new ArrayList<>();

        Uri albumUri = Audio.Albums.EXTERNAL_CONTENT_URI;

        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        final String[] columns = { _id, album_name, artist, albumart, tracks };

        String albumSortOrder = Audio.Albums.DEFAULT_SORT_ORDER + " ASC";

        Cursor albumCursor = mAlbumContext.getContentResolver().query(albumUri, columns, null, null, albumSortOrder);

        if (albumCursor != null && albumCursor.moveToFirst()) {
//            int titleColumn = albumCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM);
            int titleColumn = albumCursor.getColumnIndex(album_name);
//            int idColumn = albumCursor.getColumnIndex(BaseColumns._ID);
            int idColumn = albumCursor.getColumnIndex(_id);
//            int artistColumn = albumCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST);
            int artistColumn = albumCursor.getColumnIndex(artist);
//            int numOfSongsColumn = albumCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS);
            int numOfSongsColumn = albumCursor.getColumnIndex(tracks);
//            int albumfirstColumn = albumCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR);

            do {
                long id = albumCursor.getLong(idColumn);
                String albumName = albumCursor.getString(titleColumn);
                String artistName = albumCursor.getString(artistColumn);
                int numOfSongs = albumCursor.getInt(numOfSongsColumn);
//                int year = albumCursor.getInt(albumfirstColumn);

                Albums albums = new Albums();
                albums.setTrackId(id);
                albums.setAlbumName(albumName);
                albums.setAlbum_NoOfSongs(numOfSongs);
//                albums.setAlbumYear(year);

                albumsList.add(albums);

            } while (albumCursor.moveToNext());
            albumCursor.close();
        }
        if (albumsList == null) {
            return Collections.emptyList();
        }
        return albumsList;

    }


}













