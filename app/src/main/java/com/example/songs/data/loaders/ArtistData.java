package com.example.songs.data.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.Artists;

import com.example.songs.data.model.Artist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistData {

    private String mArtistSortOrder;
    private Context mArtistContext;

    public ArtistData(Context artistContext) {
        mArtistContext = artistContext;
    }

    public List<Artist> loadArtistListInBackground() {

        List<Artist> artistList = new ArrayList<>();

        Cursor cursor = mArtistContext.getContentResolver()
                .query(Artists.EXTERNAL_CONTENT_URI, null, null, null, mArtistSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndex(BaseColumns._ID);
            int nameCol = cursor.getColumnIndex(ArtistColumns.ARTIST);
            int trackNumCol = cursor.getColumnIndex(ArtistColumns.NUMBER_OF_TRACKS);
            int albumNumCol = cursor.getColumnIndex(ArtistColumns.NUMBER_OF_ALBUMS);

            do {
                long id = cursor.getLong(idCol);
                String artistName = cursor.getString(nameCol);
                int trackCount = cursor.getInt(trackNumCol);
                int albumCount = cursor.getInt(albumNumCol);

                Artist artist = new Artist();
                artist.setId(id);
                artist.setArtistName(artistName);
                artist.setArtistTrackCount(trackCount);
                artist.setArtistAlbumCount(albumCount);

                artistList.add(artist);

            } while (cursor.moveToNext());
            cursor.close();
        }

        if (cursor == null) {
            return Collections.emptyList();
        }
        return artistList;

    }

}
