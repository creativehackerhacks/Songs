package com.example.songs.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.data.model.Tracks;
import com.example.songs.innerFragments.NowPlayingFragment;
import com.example.songs.topFragment.ProfileFragment;
import com.example.songs.topFragment.SettingsFragment;
import com.example.songs.topFragment.SongsFragment;
import com.example.songs.util.UtilConstants;
import com.example.songs.util.FirebaseDatabaseUtils;
import com.example.songs.util.touchListeners.CustomSwipeTouchListener;
import com.example.songs.service.SimpleMusicService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemReselectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavSwitchController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    public static final String MAINACTIVITYTAG = MainActivity.class.getSimpleName();
    private boolean mToggleMinPlayerFlag = false;

    private static final int INDEX_PHOTOS = 1;
    private static final int INDEX_TRENDINGS = 2;
    private static final int INDEX_COLLECTIONS = 3;
    private static final String Broadcast_PLAY_NEW_AUDIO = "com.example.songs.PlayNewAudio";

    private List<Fragment> mFragmentList;
    private FragNavController mFragNavController;

    private BottomNavigationView mBottomNavigationView;

    public RelativeLayout mMinPlayer;
    private TextView mSongName;
    private Button mPlayPauseBtn;
    private Tracks mSinglePlayScreenTracks;

    private Drawable mPlayDrawable, mPauseDrawable;

    private FragNavTransactionOptions mFragNavTransactionOptions;

    /**
     * @param savedInstanceState
     */
    public SimpleMusicService mMediaPlayerService;
    private boolean isServiceBound = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabaseUtils.getDatabase();

        hidestatusBar();

        mBottomNavigationView = findViewById(R.id.a_m_navigation);
        mMinPlayer = findViewById(R.id.a_m_min_player);
        mSongName = findViewById(R.id.a_m_min_player_songName);
        mPlayPauseBtn = findViewById(R.id.a_m_play_pause_toggle);

        mPlayDrawable = ContextCompat.getDrawable(this, R.drawable.ic_play_light);
        mPauseDrawable = ContextCompat.getDrawable(this, R.drawable.ic_pause_light);

        mFragmentList = new ArrayList<>(3);
        mFragNavController = new FragNavController(getSupportFragmentManager(), R.id.main_frame_layout);

        initializeFragmentList();
        mFragNavController.initialize(FragNavController.TAB1, savedInstanceState);

        mFragNavTransactionOptions = FragNavTransactionOptions.Companion.newBuilder()
                .customAnimations(
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left,
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right)
                .build();

        mBottomNavigationView.setOnNavigationItemSelectedListener(bottomNavItemSelected);
        mBottomNavigationView.setOnNavigationItemReselectedListener(bottomNavItemReselected);


//        mMinPlayer.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("TrackFromMainActivity", mSinglePlayScreenTracks);
//                Fragment mPlayingFragment = NowPlayingFragment.newInstance();
//                mPlayingFragment.setArguments(bundle);
//                pushFragment(mPlayingFragment);
//            }
//        });

        mMinPlayer.setOnTouchListener(mCustomMinPlayerListener);

        mPlayPauseBtn.setOnClickListener(mPlayPauseToggle);
    }


    private void hidestatusBar() {
        // Hide Status Bar
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
//            View decorView = getWindow().getDecorView();
//            // Hide Status Bar.
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private View.OnClickListener mPlayPauseToggle = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mMediaPlayerService == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.a_m_play_pause_toggle:
                    mMediaPlayerService.toggle();
                    break;
            }
        }
    };


    private void playPauseToggle() {
        if(mMediaPlayerService != null) {
            if(mMediaPlayerService.isPlaying()) {
                mPlayPauseBtn.setBackground(mPauseDrawable);
            } else {
                mPlayPauseBtn.setBackground(mPlayDrawable);
            }
        }
    }

    private View.OnTouchListener mCustomMinPlayerListener = new CustomSwipeTouchListener() {
        @Override
        public void onSwipeRight() {
            Toast.makeText(MainActivity.this, "Swiped Right", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSwipeLeft() {
            Toast.makeText(MainActivity.this, "Swiped Left", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick() {
            Bundle bundle = new Bundle();
            bundle.putParcelable("TrackFromMainActivity", mSinglePlayScreenTracks);
            Fragment mPlayingFragment = NowPlayingFragment.newInstance();
            mPlayingFragment.setArguments(bundle);
            pushFragment(mPlayingFragment);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ServiceState", isServiceBound);
        if (mFragNavController != null) {
            mFragNavController.onSaveInstanceState(outState);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavItemSelected =
            new OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.navigation_photos:
                            mFragNavController.switchTab(FragNavController.TAB1, FragNavTransactionOptions.Companion.newBuilder().transition(FragmentTransaction.TRANSIT_NONE).build());
                            return true;
                        case R.id.navigation_trendings:
                            mFragNavController.switchTab(FragNavController.TAB2, FragNavTransactionOptions.Companion.newBuilder().transition(FragmentTransaction.TRANSIT_NONE).build());
                            return true;
                        case R.id.navigation_collections:
                            mFragNavController.switchTab(FragNavController.TAB3, FragNavTransactionOptions.Companion.newBuilder().transition(FragmentTransaction.TRANSIT_NONE).build());
                            return true;
                        default:
                            return false;
                    }
                }
            };

    private FragNavSwitchController mFragNavSwitchController = new FragNavSwitchController() {
        @Override
        public void switchTab(int i, FragNavTransactionOptions fragNavTransactionOptions) {
            mBottomNavigationView.setSelectedItemId(i);
        }
    };

    public void pushFragment(Fragment fragment) {
        mFragNavController.pushFragment(fragment, mFragNavTransactionOptions);
    }


    public void popFragment() {
        mFragNavController.popFragment();
    }


    private BottomNavigationView.OnNavigationItemReselectedListener bottomNavItemReselected =
            new OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                    if (!mFragNavController.isRootFragment()) {
                        mFragNavController.clearStack();
                    } else {
//                        Fragment f = mFragNavController.getCurrentFrag();
//                        if (f != null) {
//                            View fragmentView = f.getView();
//                            RecyclerView mRecyclerView = fragmentView.findViewById(R.id.f_p_recyclerView);//mine one is RecyclerView
//                            if (mRecyclerView != null)
//                                mRecyclerView.smoothScrollToPosition(0);
//                        }
                    }
                }
            };


    private void initializeFragmentList() {
        mFragmentList.add(SongsFragment.newInstance());
        mFragmentList.add(ProfileFragment.newInstance());
        mFragmentList.add(SettingsFragment.newInstance());

        mFragNavController.setRootFragments(mFragmentList);
    }

    @Override
    public void onBackPressed() {
        if (mFragNavController.getCurrentStack().size() > 1) {
            mFragNavController.popFragment(mFragNavTransactionOptions);
        } else {
            super.onBackPressed();
        }
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SimpleMusicService.LocalSimpleMusicBinder binder = (SimpleMusicService.LocalSimpleMusicBinder) service;
            mMediaPlayerService = binder.getService();
            isServiceBound = true;

            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mMediaPlayerService == null) {
                return;
            }
            playPauseToggle();
        }
    };


    public void playAudio(List<Tracks> tracks, int pos) {
        if (mMediaPlayerService == null) {
            return;
        }
        mMediaPlayerService.setTrackList(tracks, pos, true);
        minPlayerSetup(tracks, pos);
    }

    private void minPlayerSetup(List<Tracks> tracks, int pos) {
        mMinPlayer.setVisibility(View.VISIBLE);
        mSinglePlayScreenTracks = tracks.get(pos);
        mSongName.setText(tracks.get(pos).getTrackName());
    }


    public void toggleMinPlayer() {
        if (mToggleMinPlayerFlag) {
            mMinPlayer.setVisibility(View.VISIBLE);
            mToggleMinPlayerFlag = false;
        } else {
            mMinPlayer.setVisibility(View.GONE);
            mToggleMinPlayerFlag = true;
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isServiceBound = savedInstanceState.getBoolean("ServiceState");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isServiceBound) {
            Intent intent = new Intent(this, SimpleMusicService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        } else {
            if (mMediaPlayerService != null) {
                Toast.makeText(mMediaPlayerService,
                        "I am getting called from MainActivity: onResume method",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (isServiceBound) {
////            unbindService(serviceConnection);
////            //service is active
////            mMediaPlayerService.stopSelf();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
            mMediaPlayerService = null;
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isServiceBound) {
            Intent intent = new Intent(this, SimpleMusicService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        registerReceiver(mBroadcastReceiver, new IntentFilter(UtilConstants.PLAYBACK_STATE));
    }

    public void hideStatusBar() {
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showStatusBar() {
        // Hide status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    // for hiding/showing bottom navigation view
    public void hideBottomNavigationView() {
        mBottomNavigationView.animate().translationY(mBottomNavigationView.getHeight()).setDuration(300);
        mBottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigationView() {
        mBottomNavigationView.animate().translationY(0).setDuration(300);
        mBottomNavigationView.setVisibility(View.VISIBLE);
    }

}
