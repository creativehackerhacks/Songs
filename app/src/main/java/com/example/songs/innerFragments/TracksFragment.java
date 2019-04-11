package com.example.songs.innerFragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.adapters.TrackRecyclerViewAdapter;
import com.example.songs.base.BaseInnerFragment;
import com.example.songs.data.loaders.TrackData;
import com.example.songs.data.model.Track;
import com.example.songs.interfaces.RecyclerViewSimpleClickListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends BaseInnerFragment implements LoaderCallbacks<List<Track>> {

    public static final String TRACK_FRAGMENT = TracksFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TrackRecyclerViewAdapter mTrackRecyclerViewAdapter;
    private RecyclerViewSimpleClickListener mRecyclerViewSimpleClickListener;
    private List<Track> mTracks;
    private List<Track> mSecondTrack;

    private int trackLoader = -1;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    public TracksFragment() {
        // Required empty public constructor
    }

    public static TracksFragment newInstance() {
        TracksFragment tracksFragment = new TracksFragment();
        return tracksFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);

        mCollapsingToolbarLayout = view.findViewById(R.id.f_tracks_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle("Songs");

        mToolbar = view.findViewById(R.id.f_tracks_toolbar);
        mRecyclerView = view.findViewById(R.id.f_tracks_recyclerView);

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

        return view;
    }

    private void setUpRecyclerView(Context context) {
        mTrackRecyclerViewAdapter = new TrackRecyclerViewAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mTracks = new ArrayList<>();
        mTrackRecyclerViewAdapter.setOnItemClickListener(mClickListener);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mTrackRecyclerViewAdapter);
        initload();
    }

    private void initload() {
        getLoaderManager().initLoader(trackLoader, null, this);
    }

    @NonNull
    @Override
    public Loader<List<Track>> onCreateLoader(int id, @Nullable Bundle args) {
        TrackData trackData = new TrackData(getContext());
        if (id == trackLoader) {
//            trackData.setSortorder(Extras.getInstance().get);
            return trackData;
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Track>> loader, List<Track> data) {
        if (data == null) {
            return;
        }
        mTracks = data;
        mTrackRecyclerViewAdapter.addTrackList(mTracks);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Track>> loader) {
        mTrackRecyclerViewAdapter.notifyDataSetChanged();
    }

    public List<Track> getTracks() {
        return mTracks;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            //TODO: Step 4 of 4: Finally call getTag() on the view.
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            // viewHolder.getItemId();
            // viewHolder.getItemViewType();
            // viewHolder.itemView;
            ((MainActivity) getActivity()).playAudio(mTracks, position);
//            Toast.makeText(getContext(), "You Clicked: " + mTracks.get(position).getTrackName(), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnLongClickListener mLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showDialog();
            return true;
        }
    };

    private void showDialog() {

    }

}
