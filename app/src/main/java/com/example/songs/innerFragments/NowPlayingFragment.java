package com.example.songs.innerFragments;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.animation.MyBounceInterpolator;
import com.example.songs.data.model.Tracks;
import com.example.songs.service.SimpleMusicService;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment {

    public static final String NOWPLAYINGFRAGMENT = NowPlayingFragment.class.getSimpleName();

    private SimpleMusicService mSimpleMusicService;
    private boolean mServiceBound = false;

    private Uri mArtWorkUri = Uri.parse("content://media/external/audio/albumart");
    private Tracks mTracks;

    private int position;
    private Handler mSeekbarHandler = new Handler();

    private CardView mCardView;
    private ConstraintLayout mNowPlayingContainerLayout;
    private TextView mSongName;
    private TextView mSongArtistName;
    private ImageView mPlayingImgView;

    private TextView mInitialTv, mFinalTv;
    private SeekBar mNPSeekbar;
    private ImageView mRewindIV, mForwardIV, mPlayPauseIV;

    private Long mArtworkTrackId;

    // Service Collection
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SimpleMusicService.LocalSimpleMusicBinder binder = (SimpleMusicService.LocalSimpleMusicBinder) service;
            mSimpleMusicService = binder.getService();
            mServiceBound = true;
            if(mSimpleMusicService != null) {
                reload();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    private OnClickListener mPlayPauseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mSimpleMusicService.toggle();
        }
    };

    private void reload() {
        playingView();
        seekbarProgress();
    }

    private void playingView() {
        if(mSimpleMusicService != null) {
            mNPSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && getSimpleMusicService() != null && (getSimpleMusicService().isPlaying() || getSimpleMusicService().isPaused())) {
                        getSimpleMusicService().seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            int duration = mSimpleMusicService.getTrackDuration();
            if (duration != -1) {
                mNPSeekbar.setMax(duration);
                mFinalTv.setText(durationCalculator(duration));
            }
        }
    }

    private void seekbarProgress() {
        mSeekbarHandler.postDelayed(mSeekbarHandlerPostDelayed, 100);
    }

    private Runnable mSeekbarHandlerPostDelayed = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            mSeekbarHandler.postDelayed(this, 1000);
        }
    };

    public NowPlayingFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        NowPlayingFragment nowPlayingFragment = new NowPlayingFragment();
        Bundle bundle = new Bundle();
        nowPlayingFragment.setArguments(bundle);
        return nowPlayingFragment;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        //TODO: init MediaPlayer and play the audio

        mCardView = view.findViewById(R.id.f_now_playing_coverArt_card);
        mNowPlayingContainerLayout = view.findViewById(R.id.f_n_p_main_layout);

        mSongName = view.findViewById(R.id.f_now_playing_song_name);
        mSongArtistName = view.findViewById(R.id.f_now_playing_song_artist_name);
        mPlayingImgView = view.findViewById(R.id.f_now_playing_coverArt);

        mNPSeekbar = view.findViewById(R.id.f_n_p_seekbar);
        mInitialTv = view.findViewById(R.id.f_n_p_initial_tv);
        mFinalTv = view.findViewById(R.id.f_n_p_final_tv);
        mRewindIV = view.findViewById(R.id.f_n_p_fast_rewind);
        mForwardIV = view.findViewById(R.id.f_n_p_fast_forward);
        mPlayPauseIV = view.findViewById(R.id.f_n_p_play_pause);

        mPlayPauseIV.setOnClickListener(mPlayPauseClickListener);


        if (getArguments() != null) {
            mTracks = getArguments().getParcelable("TrackFromMainActivity");
            mSongName.setText(mTracks.getTrackName());
            mSongArtistName.setText(mTracks.getTrackArtistName());

            Log.e(NOWPLAYINGFRAGMENT, "onCreateView: " + mTracks.getTrackId());
            Log.e(NOWPLAYINGFRAGMENT, "onCreateView: " + mTracks.getTrackAlbumId());

            Uri uri = ContentUris.withAppendedId(mArtWorkUri, mTracks.getTrackId());
            Glide.with(container).load(uri)
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .into(mPlayingImgView);
        }

        mCardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation animation = AnimationUtils.loadAnimation(container.getContext(), R.anim.bounce);
                MyBounceInterpolator myBounceInterpolator = new MyBounceInterpolator(0.2, 20);
                animation.setInterpolator(myBounceInterpolator);
                mCardView.startAnimation(animation);
//                Toast.makeText(container.getContext(), "I am working", Toast.LENGTH_LONG).show();
            }
        });

        mNowPlayingContainerLayout.setOnTouchListener(new OnTouchListener() {
            int downX, upX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = (int) event.getX();
                    Log.e(NOWPLAYINGFRAGMENT, "onTouch: From Now Playing Fragment. + " + downX);
                    if (downX > 60) {
                        ((MainActivity) getActivity()).popFragment();
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // do nothing
                    return false;
                }
                return false;
            }
        });



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() == null) {
            return;
        }
        if(mSimpleMusicService != null) {
            reload();
        }
        ((MainActivity) getActivity()).mMinPlayer.setVisibility(View.GONE);
        ((MainActivity) getActivity()).hideBottomNavigationView();
    }

    @Override
    public void onPause() {
        super.onPause();
//        ((MainActivity) getActivity()).toggleMinPlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getActivity() == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), SimpleMusicService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mServiceBound) {
            mSimpleMusicService = null;
            getActivity().unbindService(mServiceConnection);
            mServiceBound = false;
        }
        ((MainActivity) getActivity()).mMinPlayer.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).showBottomNavigationView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeCallback();
    }

    private void removeCallback() {
        mSeekbarHandler.removeCallbacks(mSeekbarHandlerPostDelayed);
        mSeekbarHandler.removeCallbacksAndMessages(null);
    }

    private void updateProgress() {
        if(mSimpleMusicService != null && mSimpleMusicService.isPlaying()) {
            position = mSimpleMusicService.getPlayerPos();
            Log.e(NOWPLAYINGFRAGMENT, "updateProgress: " + position);
            mNPSeekbar.setProgress(position);
            mInitialTv.setText(durationCalculator(position));
        }
    }

    private SimpleMusicService getSimpleMusicService() {
        return mSimpleMusicService;
    }

    private static String durationCalculator(long id) {
        String finalTimerString = "";
        String secondsString = "";
        String mp3Minutes = "";
        // Convert total duration into time

        int minutes = (int) (id % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((id % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (minutes < 10) {
            mp3Minutes = "0" + minutes;
        } else {
            mp3Minutes = "" + minutes;
        }
        finalTimerString = finalTimerString + mp3Minutes + ":" + secondsString;
        // return timer string
        return finalTimerString;
    }

}
