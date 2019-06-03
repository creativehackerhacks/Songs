package com.example.songs.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import com.example.songs.activity.MainActivity;
import com.example.songs.data.model.Tracks;
import com.example.songs.singleton.MediaPlayerSingleton;
import com.example.songs.util.AudioWidget;
import com.example.songs.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;


@TargetApi(VERSION_CODES.LOLLIPOP)
public class SimpleMusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = SimpleMusicService.class.getSimpleName();
    private final LocalSimpleMusicBinder mLocalSimpleMusicBinder = new LocalSimpleMusicBinder();

    private List<Tracks> mTracksList = new ArrayList<>();
    private List<Tracks> mOgList = new ArrayList<>();
    private Tracks mSingleTracks;
    private boolean isRunning = false;
    private boolean paused;
    private boolean fastPlay;
    private int playingIndex;
    private int trackDuration;

    private MainActivity mMainActivity;
    private Intent mBroadcastIntent;

    private AudioWidget mAudioWidget;


    private AudioAttributes attr = new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build();

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaData();
        initAudioWidget();
    }

    private void initAudioWidget() {
        mBroadcastIntent = new Intent(Constants.PLAYBACK_STATE);
    }

    private void initMediaData() {
        try {
            MediaPlayerSingleton.getInstance().getMediaPlayer();
            MediaPlayerSingleton.getInstance().getMediaPlayer().setOnPreparedListener(this);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setOnCompletionListener(this);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setOnErrorListener(this);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setAudioAttributes(attr);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            Log.d(TAG, "initMediaData: Done");
        } catch (Exception e) {
            Log.d(TAG, "initMediaData: error::", e);
        }
    }



    public void setTrackList(List<Tracks> tracksList, int pos, boolean play) {
        smartPlayList(tracksList);
        setDataPos(pos, play);
    }

    public void smartPlayList(List<Tracks> smartPlaylist) {
        if (smartPlaylist != null && smartPlaylist.size() > 0) {
            mOgList = smartPlaylist;
            mTracksList.clear();
            mTracksList.addAll(mOgList);
        } else {
            Log.d(TAG, "smartPlayList: error");
        }
    }

    private void setDataPos(int pos, boolean play) {
        if (pos != -1 && pos < mTracksList.size()) {
            playingIndex = pos;
            mSingleTracks = mTracksList.get(pos);
            if (play) {
                fastPlay(true, mSingleTracks);
            } else {
                fastPlay(false, mSingleTracks);
            }
        } else {
            Log.d(TAG, "setDataPos: null value");
        }
    }

    public void fastPlay(boolean torf, Tracks tracks) {
        fastPlay = torf;
        startCurrentTrack(tracks);
    }

    public int returnpos() {
        return mTracksList.indexOf(mSingleTracks) != -1 && mTracksList.indexOf(mSingleTracks) < mTracksList.size() ? mTracksList.indexOf(mSingleTracks) : -1;
    }

    public void startCurrentTrack(Tracks tracks) {
        if (returnpos() != -1 && mTracksList.size() > 0) {
            if (MediaPlayerSingleton.getInstance().getMediaPlayer() == null) {
                return;
            }
            Uri dataLoader = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, tracks.getTrackId());
            if (dataLoader == null) {
                return;
            }
            try {
                MediaPlayerSingleton.getInstance().getMediaPlayer().reset();
                MediaPlayerSingleton.getInstance().getMediaPlayer()
                        .setDataSource(SimpleMusicService.this, dataLoader);
                MediaPlayerSingleton.getInstance().getMediaPlayer().prepareAsync();
                MediaPlayerSingleton.getInstance().getMediaPlayer().setAuxEffectSendLevel(1.0f);
                Log.d(TAG, "startCurrentTrack: Prepared");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "startCurrentTrack: position error.");
        }
    }


    // for music play/pause
    public void play() {
        if (mSingleTracks == null) {
            return;
        }
        finalPlay();
        MediaPlayerSingleton.getInstance().getMediaPlayer().start();
    }

    public void finalPlay() {
        paused = false;
        isRunning = true;
        this.sendBroadcast(mBroadcastIntent);
//        Log.d(TAG, "finalPlay: Play");
    }

    public void pause() {
        if (mSingleTracks == null) {
            return;
        }
        finalPause();
        MediaPlayerSingleton.getInstance().getMediaPlayer().pause();
    }

    public void finalPause() {
        paused = true;
        isRunning = false;
        this.sendBroadcast(mBroadcastIntent);
    }

    private void pauseDrawable() {

    }

    public void playNext(boolean torf) {
        int position = getNextPos(torf);
        if (position != -1 && position < mTracksList.size()) {
            paused = false;
            fastPlay = true;
            mSingleTracks = mTracksList.get(position);
            fastPlay(true, mSingleTracks);
            Log.d(TAG, "playNext: Done");
        } else {
            fastPlay = false;
            paused = true;
            isRunning = false;
        }
    }

    public int getNextPos(boolean yorn) {
        int incPos = returnpos() + 1;
        return incPos;
    }

    public void seekTo(int seek) {
        if (MediaPlayerSingleton.getInstance().getMediaPlayer() != null) {
            MediaPlayerSingleton.getInstance().getMediaPlayer().seekTo(seek);
        } else {
            MediaPlayerSingleton.getInstance().getMediaPlayer().seekTo(0);
        }
    }

    public int getPlayerPos() {
        if(returnpos() == -1) {
            return 0;
        }
        if(MediaPlayerSingleton.getInstance().getMediaPlayer() != null) {
            return MediaPlayerSingleton.getInstance().getMediaPlayer().getCurrentPosition();
        } else {
            return -1;
        }
    }

    public int getTrackDuration() {
        if(returnpos() == -1) {
            return 0;
        }
        if(MediaPlayerSingleton.getInstance().getMediaPlayer() != null) {
            return trackDuration;
        } else {
            return -1;
        }
    }


    //
    public boolean isPlaying() {
        return isRunning;
    }

    public boolean isPaused() {
        return paused;
    }

    public void stopMediaPlayer() {
        if (MediaPlayerSingleton.getInstance().getMediaPlayer() == null) {
            return;
        }
        MediaPlayerSingleton.getInstance().getMediaPlayer().reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fastPlay = false;
        isRunning = false;
        paused = false;
        stopMediaPlayer();
    }

    // Override methods
    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mTracksList.size() == 0 || returnpos() == -1) {
            return;
        }
        playNext(true);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (fastPlay) {
            play();
            fastPlay = false;
        }
        trackDuration = MediaPlayerSingleton.getInstance().getMediaPlayer().getDuration();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // will modify later
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Done");
        return mLocalSimpleMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (isPlaying() && mTracksList.size() > 0) {
            return true;
        }
        Log.d(TAG, "onUnbind: Done");
        return false;
    }

    public void toggle() {
        if(MediaPlayerSingleton.getInstance().getMediaPlayer() == null) {
            return;
        }
        if(MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public class LocalSimpleMusicBinder extends Binder {
        public SimpleMusicService getService() {
            return SimpleMusicService.this;
        }
    }


}






