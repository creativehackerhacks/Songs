package com.example.songs.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.songs.activity.MainActivity;
import com.example.songs.data.model.Tracks;
import com.example.songs.reciever.MediaButtonIntentReceiver;
import com.example.songs.singleton.MediaPlayerSingleton;
import com.example.songs.util.AudioWidget;
import com.example.songs.util.Extras;
import com.example.songs.util.UtilConstants;
import com.example.songs.util.notify.NotificationGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import static com.example.songs.util.UtilConstants.ACTION_NEXT;
import static com.example.songs.util.UtilConstants.ACTION_PREVIOUS;
import static com.example.songs.util.UtilConstants.ACTION_TOGGLE;
import static com.example.songs.util.UtilConstants.ITEM_ADDED;
import static com.example.songs.util.UtilConstants.META_CHANGED;
import static com.example.songs.util.UtilConstants.ORDER_CHANGED;
import static com.example.songs.util.UtilConstants.PLAYSTATE_CHANGED;
import static com.example.songs.util.UtilConstants.POSITION_CHANGED;
import static com.example.songs.util.UtilConstants.QUEUE_CHANGED;
import static com.example.songs.util.UtilConstants.SONG_ALBUM;
import static com.example.songs.util.UtilConstants.SONG_ARTIST;
import static com.example.songs.util.UtilConstants.SONG_TITLE;


@TargetApi(VERSION_CODES.LOLLIPOP)
public class SimpleMusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = SimpleMusicService.class.getSimpleName();
    private final LocalSimpleMusicBinder mLocalSimpleMusicBinder = new LocalSimpleMusicBinder();

    private List<Tracks> mTracksList = new ArrayList<>();
    private List<Tracks> mOgList = new ArrayList<>();
    private List<Tracks> mCheckShuffle = new ArrayList<>();
    private List<Tracks> mQueueList = new ArrayList<>();
    private Tracks mSingleTracks;

    public boolean isRunning = false;
    private boolean isShuffled = false;
    private boolean onPlayNotify = false;
    private boolean paused;
    private boolean fastPlay;
    private boolean mLostAudioFocus = false;
    private boolean mIsDucked;

    private int playingIndex;
    private int trackDuration;

    private MainActivity mMainActivity;
    private Intent mBroadcastIntent;

    private MediaSessionCompat mMediaSession;

    private ControlReceiver mControlReceiver = null;

    private AudioWidget mAudioWidget;
//    private NotificationGenerator mNotificationGenerator;


    private AudioAttributes attr = new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build();

    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaData();
        initAudioWidget();
        otherStuff();
        initMediaSession();
//        initNotification();
    }

    private void initNotification() {
//        mNotificationGenerator = new NotificationGenerator(this);
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiverComponentName = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiverComponentName);

        PendingIntent mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, mediaButtonIntent, 0);
        mMediaSession = new MediaSessionCompat(this, "SONGSTAG", mediaButtonReceiverComponentName, mediaButtonReceiverPendingIntent);
        mMediaSession.setCallback(new MyMediaSessionCompatCallback());
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent);
        mMediaSession.setActive(true);
    }

    private void updateMediaSessionPlaybackState() {
        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                                returnpos(), 1)
                .build());
    }

    private void updateMediaSessionMetaData() {
        final Tracks track = mSingleTracks;
        if(track.getTrackId() == -1) {
            mMediaSession.setMetadata(null);
            return;
        }
    }

    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    private void otherStuff() {
        mSingleTracks = new Tracks();
        if(mControlReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            intentFilter.addAction(ACTION_PREVIOUS);
            intentFilter.addAction(ACTION_TOGGLE);
            intentFilter.addAction(ACTION_NEXT);
            registerReceiver(mControlReceiver, intentFilter);
            Log.e(TAG, "Control Receiver Started");
        }
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
        Log.e(TAG, "startCurrentTrack: returnPos == " + returnpos());
        Log.e(TAG, "startCurrentTrack: mTrackListSize == " + mTracksList.size());
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
        onPlayNotify = true;
        updateService(PLAYSTATE_CHANGED);
        paused = false;
        isRunning = true;
        updateMediaSessionPlaybackState();
        updateMediaSessionMetaData();
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

    public void playPrevSong(boolean torf) {
        int position = getPrevSongPos(torf);
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

    public void playPrev(boolean torf) {
        int position = getPrevPos(torf);
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

    public int getPrevPos(boolean yorn) {
        int pos = returnpos();
        return pos;
    }

    public int getPrevSongPos(boolean yorn) {
        int pos = returnpos()-1;
        return pos;
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

    public boolean isShuffleEnabled() {
        return isShuffled;
    }

    public void setShuffleEnabled(boolean enabled) {
        if(isShuffled != enabled) {
            isShuffled = enabled;
            if(enabled) {
                shuffle();
                isShuffled = true;
            } else {
                clearQueue();
                mTracksList.addAll(mOgList);
            }
            updateService(ORDER_CHANGED);
        }
    }

    public void shuffle() {
        if(mTracksList.size() > 0) {
            Random random = new Random();
            long speed = System.nanoTime();
            random.setSeed(speed);
            Collections.shuffle(mTracksList, random);
            Log.e(TAG, "shuffle playlist");
        }
    }

    public void clearQueue() {
        if(mTracksList.size() < 0) {
            return;
        }
        mTracksList.clear();
    }

    public void shufflePlay(List<Tracks> shuffleList) {
        mCheckShuffle = shuffleList;
        int pos = mineShuffle(mCheckShuffle);
        mSingleTracks = mCheckShuffle.get(pos);
        mCheckShuffle.add(mSingleTracks);
        Log.e(TAG, "shufflePlay: mSingleTracks " + mSingleTracks);
        Log.e(TAG, "shufflePlay: mCheckShuffle " + mCheckShuffle);
        setDataPos(pos, true);
    }

    private int mineShuffle(List<Tracks> sList) {
        int size = sList.size();
        int pos = (int) (Math.random() * size);
        return pos;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        fastPlay = false;
        isRunning = false;
        paused = false;
        stopMediaPlayer();
        receiverCleanUp();
        mMediaSession.setActive(false);
        mMediaSession.release();
        Log.e(TAG, "Control Receiver Destroyed");
    }

    private void receiverCleanUp() {
        if(mControlReceiver != null) {
            try {
                unregisterReceiver(mControlReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mControlReceiver = null;
        }
    }

    // Override methods
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "AudioFocus Loss");
                if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                    pause();
                    //service.stopSelf();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                    MediaPlayerSingleton.getInstance().getMediaPlayer().setVolume(0.3f, 0.3f);
                    mIsDucked = true;
                }
                Log.d(TAG, "AudioFocus Loss Can Duck Transient");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "AudioFocus Loss Transient");
                if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                    pause();
                    mLostAudioFocus = true;
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "AudioFocus Gain");
                if (mIsDucked) {
                    MediaPlayerSingleton.getInstance().getMediaPlayer().setVolume(1.0f, 1.0f);
                    mIsDucked = false;
                } else if (mLostAudioFocus) {
                    // If we temporarily lost the audio focus we can resume playback here
                    if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                        play();
                    }
                    mLostAudioFocus = false;
                }
                break;
            default:
                Log.d(TAG, "Unknown focus");
        }
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

    private void updateService(String updateservices) {
        Intent intent = new Intent(updateservices);
        if (updateservices.equals(PLAYSTATE_CHANGED) && intent.getAction().equals(PLAYSTATE_CHANGED)) {
            sendBroadcast(intent);
        } else if (updateservices.equals(META_CHANGED) && intent.getAction().equals(META_CHANGED)) {
            Bundle bundle = new Bundle();
            bundle.putString(SONG_TITLE, getSongTitle());
            bundle.putInt(POSITION_CHANGED, returnpos());
            intent.putExtras(bundle);
            Log.d(TAG, "broadcast song metadata");
            sendBroadcast(intent);
        } else if ((updateservices.equals(QUEUE_CHANGED) || updateservices.equals(ORDER_CHANGED) || updateservices.equals(ITEM_ADDED)) && (intent.getAction().equals(QUEUE_CHANGED) || intent.getAction().equals(ORDER_CHANGED) || intent.getAction().equals(ITEM_ADDED))) {
            sendBroadcast(intent);
        }
//        if (onPlayNotify) {
//            mNotificationGenerator.buildNotification();
//            onPlayNotify = false;
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

    public Tracks getCurrentTrack() {
        if (mSingleTracks == null) {
            return null;
        }
        return mSingleTracks;
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

    public void addToQueue(Tracks track) {
        if(mTracksList.size() > 0 || mTracksList.size() == 0) {
            mOgList.add(track);
            mTracksList.add(track);
        }
    }

    public void setAsNextTrack(Tracks track) {
        if(mTracksList.size() > 0 || mTracksList.size() == 0) {
            mOgList.add(track);
            mTracksList.add(returnpos() + 1, track);
        }
    }



    private final class MyMediaSessionCompatCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            play();
        }

        @Override
        public void onSeekTo(long position) {
            seekTo((int) position);
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onStop() {
            stopSelf();
        }

        @Override
        public void onSkipToNext() {
            playNext(true);
        }

        @Override
        public void onSkipToPrevious() {
            playPrev(true);
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            return MediaButtonIntentReceiver.handleIntent(SimpleMusicService.this, mediaButtonEvent);
        };
    }

    /**
     * BroadCast controls
     */
    public class ControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                if (mLostAudioFocus) {
                    mLostAudioFocus = false;
                }
                pause();
                Log.d(TAG, "noisyAudio");
            } else if (intent.getAction().equals(ACTION_NEXT)) {
                playNext(true);
            } else if (intent.getAction().equals(ACTION_PREVIOUS)) {
                playPrev(true);
            } else if (intent.getAction().equals(ACTION_TOGGLE)) {
                toggle();
            }
        }

    }




    public class LocalSimpleMusicBinder extends Binder {
        public SimpleMusicService getService() {
            return SimpleMusicService.this;
        }
    }


}






