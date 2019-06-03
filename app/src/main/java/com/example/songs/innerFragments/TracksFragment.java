package com.example.songs.innerFragments;


import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.adapters.TrackRecyclerViewAdapter;
import com.example.songs.archComp.TrackModel;
import com.example.songs.data.model.Tracks;
import com.example.songs.interfaces.RecyclerViewSimpleClickListener;
import com.example.songs.util.dialogs.LongBottomSheetFragment;
import com.example.songs.util.dialogs.LongBottomSheetFragment.LongBottomSheetListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends Fragment {

    public static final String TRACK_FRAGMENT = TracksFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TrackRecyclerViewAdapter mTrackRecyclerViewAdapter;
    private RecyclerViewSimpleClickListener mRecyclerViewSimpleClickListener;
    private List<Tracks> mTracks;
    private int mCurrentTrackPos;

    private int trackLoader = -1;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private TrackModel mTrackModel;

    public TracksFragment() {
        // Required empty public constructor
    }

    public static TracksFragment newInstance() {
        TracksFragment tracksFragment = new TracksFragment();
        return tracksFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackModel = ViewModelProviders.of(getActivity()).get(TrackModel.class);
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

        // Setting Adapter
        mTrackRecyclerViewAdapter.loadItems(mTrackModel.getTracks());
        Log.e(TRACK_FRAGMENT, "onCreateView: " +mTrackModel.getTracks().size());

        mRecyclerView.setAdapter(mTrackRecyclerViewAdapter);

        return view;
    }

    private void setUpRecyclerView(Context context) {
        mTrackRecyclerViewAdapter = new TrackRecyclerViewAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mTrackRecyclerViewAdapter.setOnItemClickListener(mClickListener);
        mTrackRecyclerViewAdapter.setOnItemLongClickListener(mLongClickListener);
//        mRecyclerView.setHasFixedSize(true);
    }

    // More Option Click Listener
    private View.OnClickListener mMoreOptionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "I AM CLICKING", Toast.LENGTH_SHORT).show();
        }
    };

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
            ((MainActivity) getActivity()).playAudio(mTrackRecyclerViewAdapter.getAllTrackList(), position);
//            Toast.makeText(getContext(), "You Clicked: " + mTracks.get(position).getTrackName(), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnLongClickListener mLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            RecyclerView.ViewHolder viewHolder = (ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            showDialog(position);
            return true;
        }
    };

    private void showDialog(int position) {
        LongBottomSheetFragment longBottomSheetFragment = LongBottomSheetFragment.getInstance();
        longBottomSheetFragment.show(getChildFragmentManager(), "long_bottomsheet_dialog");
        longBottomSheetFragment.setLongBottomListener(new LongBottomSheetListener() {
            @Override
            public void onButtonClicked(int id) {
                if(id == R.id.long_bottomsheet_delete) {
                    Toast.makeText(getContext(), "Deleting the selected song", Toast.LENGTH_SHORT).show();
                    deleteSingleSong(position);
                }
            }
        });
    }

    private void deleteSingleSong(int position) {
        Log.e(TRACK_FRAGMENT, "deleteSingleSong: " + position);
        Uri uri = Uri.parse(mTrackRecyclerViewAdapter.getAllTrackList().get(position).getTrackData());
        Log.e(TRACK_FRAGMENT, "deleteSingleSong: " + uri.getPath());
        Tracks currentTrack = mTrackRecyclerViewAdapter.getAllTrackList().get(position);
//        deleteFile(uri.getPath(), currentTrack);
    }

    private void deleteFile(String path, Tracks track) {
        if(path.startsWith("content://")) {
            ContentResolver contentResolver = getContext().getContentResolver();
            contentResolver.delete(Uri.parse(path), null, null);
            mTrackRecyclerViewAdapter.deleteSingleRow(track);
        } else {
            File file = new File(path);
            if(file.exists()) {
                if(file.delete()) {
                    Log.e(TRACK_FRAGMENT, "deleteFile: " + "File Deleted.");
                    mTrackRecyclerViewAdapter.deleteSingleRow(track);
                } else {
                    Log.e(TRACK_FRAGMENT, "deleteFile: " + "Failed to Delete.");
                }
            } else {
                Log.e(TRACK_FRAGMENT, "deleteFile: " + " File not exist.");
            }
        }
    }


}
