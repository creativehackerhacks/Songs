package com.example.songs.innerFragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.adapters.TrackRecyclerViewAdapter;
import com.example.songs.archComp.TrackModel;
import com.example.songs.data.model.Tracks;
import com.example.songs.interfaces.RecyclerViewSimpleClickListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumListFragment extends Fragment {

    private static final String ALBUMLISTFRAGMENT = "ALBUMLISTFRAGMENT";
    private RecyclerView mAlbumListRecyclerView;
    private TrackRecyclerViewAdapter mTrackRecyclerViewAdapter;
    private RecyclerViewSimpleClickListener mRecyclerViewSimpleClickListener;
    private List<Tracks> mAlbumListTracks;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private TrackModel mTrackModel;

    private Long mAlbumId;

    public AlbumListFragment() {
    }

    public static AlbumListFragment getInstance() {
        AlbumListFragment albumListFragment = new AlbumListFragment();
        Bundle args = new Bundle();
        albumListFragment.setArguments(args);

        return albumListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackModel = ViewModelProviders.of(getActivity()).get(TrackModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);

        if (getArguments() != null) {
            mAlbumId = getArguments().getLong("ALBUM_ID");
        }

        Log.e(ALBUMLISTFRAGMENT, "onCreateView: album id is:: " + mTrackModel.getAlbumTracks(mAlbumId));

        mCollapsingToolbarLayout = view.findViewById(R.id.f_tracks_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle("Songs");

        mToolbar = view.findViewById(R.id.f_tracks_toolbar);
        mAlbumListRecyclerView = view.findViewById(R.id.f_tracks_recyclerView);

        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        setUpRecyclerView(container.getContext());

        // Setting Adapter
        mTrackRecyclerViewAdapter.loadItems(mTrackModel.getAlbumTracks(mAlbumId));
        mAlbumListRecyclerView.setAdapter(mTrackRecyclerViewAdapter);

        return view;
    }

    private void setUpRecyclerView(Context context) {
//        mAlbumListTracks = new TrackData(context).getAlbumTrackList(mAlbumId);
        mTrackRecyclerViewAdapter = new TrackRecyclerViewAdapter(context);
//        mTrackRecyclerViewAdapter.addTrackList(mAlbumListTracks);
//        mAlbumListRecyclerView.setAdapter(mTrackRecyclerViewAdapter);
        mAlbumListRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mTrackRecyclerViewAdapter.setOnItemClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            ((MainActivity) getActivity()).playAudio(mTrackRecyclerViewAdapter.getAllTrackList(), position);
//            Toast.makeText(getContext(), "You Clicked: " + mTracks.get(position).getTrackName(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onStop() {
        super.onStop();

    }
}
