package com.example.songs.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import com.example.songs.activity.MainActivity;
import com.example.songs.data.model.Tracks;
import com.example.songs.singleton.MediaPlayerSingleton;
import com.example.songs.util.AudioWidget;
import com.example.songs.util.Extras;
import com.example.songs.util.UtilConstants;
import com.example.songs.util.notify.NotificationGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import static com.example.songs.util.UtilConstants.ITEM_ADDED;
import static com.example.songs.util.UtilConstants.META_CHANGED;
import static com.example.songs.util.UtilConstants.ORDER_CHANGED;
import static com.example.songs.util.UtilConstants.PLAYSTATE_CHANGED;
import static com.example.songs.util.UtilConstants.POSITION_CHANGED;
import static com.example.songs.util.UtilConstants.QUEUE_CHANGED;
import static com.example.songs.util.UtilConstants.SONG_ALBUM;
import static com.example.songs.util.UtilConstants.SONG_ALBUM_ID;
import static com.example.songs.util.UtilConstants.SONG_ARTIST;
import static com.example.songs.util.UtilConstants.SONG_ID;
import static com.example.songs.util.UtilConstants.SONG_PATH;
import static com.example.songs.util.UtilConstants.SONG_TITLE;
import static com.example.songs.util.UtilConstants.SONG_TRACK_NUMBER;


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
    private boolean onPlayNotify = false;
    private boolean paused;
    private boolean fastPlay;
    private boolean mLostAudioFocus = false;

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
        mBroadcastIntent = new Intent(UtilConstants.PLAYBACK_STATE);
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
        updateService("LOL");
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
        updateService(PLAYSTATE_CHANGED);
        paused = false;
        isRunning = true;
        this.sendBroadcast(mBroadcastIntent);
    }

    public void pause() {
        if (mSingleTracks == null) {
            return;
        }
        finalPause();
        MediaPlayerSingleton.getInstance().getMediaPlayer().pause();
    }

    public void finalPause() {
        onPlayNotify = true;
        updateService(PLAYSTATE_CHANGED);
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
//        switch (focusChange) {
//
//        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mTracksList.size() == 0 || returnpos() == -1) {
            return;
        }
        playNext(true);
        int pos = mTracksList.size() - 1;
        if (mTracksList.get(pos) != null) {
            updateService(PLAYSTATE_CHANGED);
        }
//        Extras.getInstance().saveSeekServices(0);
    }

    private void updateService(String updateServices) {
        NotificationGenerator.buildNotification(SimpleMusicService.this, updateServices);
//        Intent intent = new Intent(updateServices);
//        if (updateServices.equals(PLAYSTATE_CHANGED) && intent.getAction().equals(PLAYSTATE_CHANGED)) {
//            sendBroadcast(intent);
//        } else if (updateServices.equals(META_CHANGED) && intent.getAction().equals(META_CHANGED)) {
//            Bundle bundle = new Bundle();
////            bundle.putString(SONG_TITLE, getsongTitle());
////            bundle.putString(SONG_ALBUM, getsongAlbumName());
////            bundle.putLong(SONG_ALBUM_ID, getsongAlbumID());
////            bundle.putString(SONG_ARTIST, getsongArtistName());
////            bundle.putLong(SONG_ID, getsongId());
////            bundle.putString(SONG_PATH, getsongData());
////            bundle.putInt(SONG_TRACK_NUMBER, getsongNumber());
//            bundle.putInt(POSITION_CHANGED, returnpos());
//            intent.putExtras(bundle);
//            Log.d(TAG, "broadcast song metadata");
//            sendBroadcast(intent);
//        } else if ((updateServices.equals(QUEUE_CHANGED) || updateServices.equals(ORDER_CHANGED) || updateServices.equals(ITEM_ADDED)) && (intent.getAction().equals(QUEUE_CHANGED) || intent.getAction().equals(ORDER_CHANGED) || intent.getAction().equals(ITEM_ADDED))) {
//            sendBroadcast(intent);
////            saveState(true);
//        }
//        if (onPlayNotify) {
////            if (!Extras.getInstance().hideNotify()) {
//                NotificationGenerator.buildNotification(SimpleMusicService.this, updateServices);
////            }
//        }
    }

    /*
    For Data retrieve
     */
    public String getSongTitle() {
        if(mSingleTracks == null) {
            return null;
        }
        return mSingleTracks.getTrackName();
    }

    public String getArtistName() {
        if(mSingleTracks == null) {
            return null;
        }
        return mSingleTracks.getTrackArtistName();
    }




    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        updateService(META_CHANGED);
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

    /**
     * BroadCast controls
     */
//    private class ControlReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
//                if (mLostAudioFocus) {
//                    mLostAudioFocus = false;
//                }
//                pause();
//                Log.d(TAG, "noisyAudio");
//            } else if (intent.getAction().equals(ACTION_PLAY)) {
//                play();
//            } else if (intent.getAction().equals(ACTION_PAUSE)) {
//                pause();
//            } else if (intent.getAction().equals(ACTION_NEXT)) {
//                playnext(true);
//            } else if (intent.getAction().equals(ACTION_PREVIOUS)) {
//                playprev(true);
//            } else if (intent.getAction().equals(ACTION_STOP)) {
//                stopSelf();
//            } else if (intent.getAction().equals(ACTION_TOGGLE)) {
//                toggle();
//            } else if (intent.getAction().equals(ACTION_CHANGE_STATE)) {
//                if (widgetPermission) {
//                    if (!Extras.getInstance().floatingWidget()) {
//                        audioWidget.show(Extras.getInstance().getwidgetPositionX(), Extras.getInstance().getwidgetPositionY());
//                    } else {
//                        audioWidget.hide();
//                    }
//                }
//            } else if (intent.getAction().equals(ACTION_FAV)) {
//                if (favHelper.isFavorite(Extras.getInstance().getSongId(getsongId()))) {
//                    favHelper.removeFromFavorites(Extras.getInstance().getSongId(getsongId()));
//                    updateService(META_CHANGED);
//                } else {
//                    favHelper.addFavorite(Extras.getInstance().getSongId(getsongId()));
//                    updateService(META_CHANGED);
//                }
//            } else if (intent.getAction().equals(ACTION_COMMAND)) {
//                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
//                musicxWidget.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
//            } else if (intent.getAction().equals(ACTION_COMMAND1)) {
//                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
//                musicXwidget4x4.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
//            } else if (intent.getAction().equals(ACTION_COMMAND2)) {
//                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
//                musicXWidget5x5.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
//            }
//        }
//
//    }




    public class LocalSimpleMusicBinder extends Binder {
        public SimpleMusicService getService() {
            return SimpleMusicService.this;
        }
    }


}






