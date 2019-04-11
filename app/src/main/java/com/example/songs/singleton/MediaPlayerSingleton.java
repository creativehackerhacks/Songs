package com.example.songs.singleton;

import android.media.MediaPlayer;

public class MediaPlayerSingleton {

    private static MediaPlayerSingleton sMediaPlayerService = null;
    private MediaPlayer mMediaPlayer;

    private MediaPlayerSingleton() {
        mMediaPlayer = new MediaPlayer();
    }

    public static MediaPlayerSingleton getInstance() {
        if (sMediaPlayerService == null) {
            sMediaPlayerService = new MediaPlayerSingleton();
        }
        return sMediaPlayerService;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

}
