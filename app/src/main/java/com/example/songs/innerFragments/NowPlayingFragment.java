package com.example.songs.innerFragments;


import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.animation.MyBounceInterpolator;
import com.example.songs.base.BaseInnerFragment;
import com.example.songs.data.model.Track;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends BaseInnerFragment {

    public static final String NOWPLAYINGFRAGMENT = NowPlayingFragment.class.getSimpleName();

    private Uri mArtWorkUri = Uri.parse("content://media/external/audio/albumart");
    private Track mTrack;

    private CardView mCardView;
    private ConstraintLayout mNowPlayingContainerLayout;
    private NumberPicker mSongEqualizerPicker;
    private TextView mSongName;
    private TextView mSongArtistName;
    private ImageView mPlayPauseImgView;

    private Long mArtworkTrackId;

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
        mSongEqualizerPicker = view.findViewById(R.id.f_now_playing_song_equalizer);
        mSongName = view.findViewById(R.id.f_now_playing_song_name);
        mSongArtistName = view.findViewById(R.id.f_now_playing_song_artist_name);
        mPlayPauseImgView = view.findViewById(R.id.f_now_playing_coverArt);

        String[] eqValues = {
                "Jazz", "Pop", "Rock", "Metal", "Bass"
        };
        mSongEqualizerPicker.setMinValue(0);
        mSongEqualizerPicker.setMaxValue(eqValues.length - 1);
        mSongEqualizerPicker.setDisplayedValues(eqValues);

        if (getArguments() != null) {
            mTrack = getArguments().getParcelable("TrackFromMainActivity");
            mSongName.setText(mTrack.getTrackName());
            mSongArtistName.setText(mTrack.getTrackArtistName());

            Log.e(NOWPLAYINGFRAGMENT, "onCreateView: " + mTrack.getTrackId());
            Log.e(NOWPLAYINGFRAGMENT, "onCreateView: " + mTrack.getTrackAlbumId());

            Uri uri = ContentUris.withAppendedId(mArtWorkUri, mTrack.getTrackId());
            Glide.with(container).load(uri)
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .into(mPlayPauseImgView);
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
        ((MainActivity) getActivity()).mMinPlayer.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
//        ((MainActivity) getActivity()).toggleMinPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).mMinPlayer.setVisibility(View.VISIBLE);
    }
}
