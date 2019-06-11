package com.example.songs.topFragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.adapters.RecentlyPlayedRecyclerViewAdapter;
import com.example.songs.innerFragments.AlbumsFragment;
import com.example.songs.innerFragments.ArtistFragment;
import com.example.songs.innerFragments.TracksFragment;
import com.example.songs.util.Constants;
import com.example.songs.util.recyclerviewUtil.GridSpaceItemDecoration;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    public static final String SONGS_FRAGMENT = SongsFragment.class.getSimpleName();

    private LinearLayout mArtistButton;
    private LinearLayout mTrackButton;
    private LinearLayout mAlbumButton;

    private RecyclerView mRecyclerView;
    private RecentlyPlayedRecyclerViewAdapter mRecentlyPlayedRecyclerViewAdapter;

    private TextView mEmptyTextView;


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

        mRecyclerView = view.findViewById(R.id.f_songs_recyclerView);
        mEmptyTextView = view.findViewById(R.id.f_songs_empty_tv);

        mArtistButton = view.findViewById(R.id.f_songs_artists);
        mTrackButton = view.findViewById(R.id.f_songs_tracks);
        mAlbumButton = view.findViewById(R.id.f_songs_albums);

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

        setUpRecyclerView(container.getContext());

        return view;
    }

    private void setUpRecyclerView(Context context) {
        mRecentlyPlayedRecyclerViewAdapter = new RecentlyPlayedRecyclerViewAdapter(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        GridSpaceItemDecoration gridSpaceItemDecoration = new GridSpaceItemDecoration(context, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(gridSpaceItemDecoration);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        if(Constants.mRecentlyPlayedSongs.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecentlyPlayedRecyclerViewAdapter.loadRecentSongs(Constants.mRecentlyPlayedSongs);
            mEmptyTextView.setVisibility(View.GONE);
        }
        loadRecentlyPlayedSongs();
        mRecentlyPlayedRecyclerViewAdapter.recentlyPlayedSetOnClickListener(mOnClickListener);
        mRecyclerView.setAdapter(mRecentlyPlayedRecyclerViewAdapter);
    }

    private View.OnClickListener mOnClickListener = v -> {
        Toast.makeText(getContext(), "Added to recently played", Toast.LENGTH_SHORT).show();
        RecyclerView.ViewHolder viewHolder = (ViewHolder) v.getTag();
        int position = viewHolder.getAdapterPosition();

//        ((MainActivity) getActivity()).playAudio(Constants.mRecentlyPlayedSongs, position);
    };

    private void loadRecentlyPlayedSongs() {
        if(Constants.mRecentlyPlayedSongs != null) {
            mRecentlyPlayedRecyclerViewAdapter.loadRecentSongs(Constants.mRecentlyPlayedSongs);
            Log.e(SONGS_FRAGMENT, "loadRecentlyPlayedSongs: " + Constants.mRecentlyPlayedSongs.size());
        }
    }

}
