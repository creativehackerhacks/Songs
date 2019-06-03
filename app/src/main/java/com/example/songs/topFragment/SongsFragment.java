package com.example.songs.topFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.innerFragments.AlbumsFragment;
import com.example.songs.innerFragments.ArtistFragment;
import com.example.songs.innerFragments.TracksFragment;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    private Button mArtistButton;
    private Button mTrackButton;
    private Button mAlbumButton;


    public SongsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        SongsFragment songsFragment = new SongsFragment();
        return songsFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        mArtistButton = view.findViewById(R.id.f_songs_artist_btn);
        mTrackButton = view.findViewById(R.id.f_songs_track_btn);
        mAlbumButton = view.findViewById(R.id.f_songs_albums_btn);

        final ArtistFragment artistFragment = ArtistFragment.newInstance();
        final TracksFragment tracksFragment = TracksFragment.newInstance();
        final AlbumsFragment albumsFragment = AlbumsFragment.newInstance();

        mArtistButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).pushFragment(artistFragment);
            }
        });

        mTrackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).pushFragment(tracksFragment);
            }
        });

        mAlbumButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).pushFragment(albumsFragment);
            }
        });

        return view;
    }

}
