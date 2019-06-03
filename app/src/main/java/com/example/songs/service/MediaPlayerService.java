package com.example.songs.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.example.songs.data.model.Tracks;

import java.io.IOException;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(api = VERSION_CODES.LOLLIPOP)
public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = MediaPlayerService.class.getSimpleName();
    private final LocalBinder mIBinder = new LocalBinder();

    private MediaPlayer mMediaPlayer;
    private String mMediaFile;
    private AudioManager mAudioManager;

    // List of available Audio files
    private List<Tracks> mTracksList;
    private Tracks mTracks;
    private int mTrackPos;
    private boolean mIsPlaying = false;


    // Initialize the audioList with the ArrayList
    public void setAudioList(List<Tracks> tracksList, int trackPos, boolean play) {
        mTracksList = tracksList;
        mTrackPos = trackPos;

        startCurrentTrack(trackPos, play);
    }

    private void startCurrentTrack(int trackPos, boolean play) {
        if (trackPos != -1 && trackPos <= mTracksList.size()) {
            mTrackPos = trackPos;
            mTracks = mTracksList.get(mTrackPos);
            if (play) {
                tellStatus(true, mTracks);
            } else {
                tellStatus(false, mTracks);
            }
        }
    }

    private void tellStatus(boolean bVal, Tracks tracks) {
        mIsPlaying = bVal;
//        startSong(tracks);
        initMediaPlayer(tracks);
    }

    private void initMediaPlayer(Tracks tracks) {
        mMediaPlayer = new MediaPlayer();

        // Set uup MediaPlayer event listeners
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);

        // Reset so that the MediaPlayer is not pointing to another data source.
        stopMedia();
        mMediaPlayer.reset();

        mMediaPlayer.setAudioAttributes(attr);
        try {
            // Set the data source to the mediaFile location
            mMediaPlayer.setDataSource(tracks.getTrackData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mMediaPlayer.prepareAsync();
    }

    private void startSong(Tracks tracks) {
        if (returnPos() != -1 && mTracksList.size() > 0) {
            mMediaPlayer = new MediaPlayer();
            Uri dataLoader = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, tracks.getTrackAlbumId());
            if (dataLoader == null) {
                return;
            }
            try {
                mMediaPlayer.reset();
//                mMediaPlayer.setDataSource(MediaPlayerService.this, dataLoader);
                mMediaPlayer.setDataSource(tracks.getTrackData());
                mMediaPlayer.setAudioAttributes(attr);
                mMediaPlayer.prepareAsync();
//                mMediaPlayer.setAuxEffectSendLevel(1.0f);
                Log.d(TAG, "Prepared");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "position error");
        }
    }

    private int returnPos() {
        return mTracksList.indexOf(mTracks) != -1 && mTracksList.indexOf(mTracks) < mTracksList.size() ? mTracksList.indexOf(mTracks) : -1;
    }


    // Used to pause/resume MediaPlayer
    private int resumePosition;

    private AudioAttributes attr = new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build();

    private void initMediaPlayer(int trackPos) {
        mMediaPlayer = new MediaPlayer();

        // Set uup MediaPlayer event listeners
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);

        // Reset so that the MediaPlayer is not pointing to another data source.
        mMediaPlayer.reset();

        mMediaPlayer.setAudioAttributes(attr);
        try {
            // Set the data source to the mediaFile location
            mMediaPlayer.setDataSource(mTracksList.get(trackPos).getTrackData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mMediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            resumePosition = mMediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(resumePosition);
            mMediaPlayer.start();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        int newPosition = mTrackPos++;
//        mMediaPlayer.setNextMediaPlayer(mp);
//        initMediaPlayer(newPosition);
        startCurrentTrack(newPosition, true);
        // to stop the service.
        stopSelf();
    }

    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation.
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        Log.d(TAG, "onPrepared: GOTTING CALLED");
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
//                if (mMediaPlayer == null) initMediaPlayer(mTracksList.get(mTrackPos));
//                if (mMediaPlayer == null) startCurrentTrack(mTrackPos, true);
//                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.abandonAudioFocus(this);
    }


    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }


    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

//        initMediaPlayer(mTrackPos);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        removeAudioFocus();
    }

    public void onNextSong(int pos) {
        stopMedia();
        mMediaPlayer.reset();
//        initMediaPlayer(pos);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
    }

    private void initMediaPlayer(String media) {
        mMediaPlayer = new MediaPlayer();

        // Set uup MediaPlayer event listeners
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);

        // Reset so that the MediaPlayer is not pointing to another data source.
        mMediaPlayer.reset();

        mMediaPlayer.setAudioAttributes(attr);
        try {
            // Set the data source to the mediaFile location
            mMediaPlayer.setDataSource(media);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mMediaPlayer.prepareAsync();
    }

}
