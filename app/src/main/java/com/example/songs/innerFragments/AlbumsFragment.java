package com.example.songs.innerFragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.adapters.AlbumRecyclerViewAdapter;
import com.example.songs.archComp.TrackModel;
import com.example.songs.base.BaseInnerFragment;
import com.example.songs.data.loaders.AlbumData;
import com.example.songs.data.model.Albums;
import com.example.songs.interfaces.RecyclerViewSimpleClickListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AlbumRecyclerViewAdapter mAlbumRecyclerViewAdapter;
    private RecyclerViewSimpleClickListener mRecyclerViewSimpleClickListener;
    private List<Albums> mAlbumsList;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private TrackModel mTrackModel;


    public AlbumsFragment() {
        // Required empty public constructor
    }

    public static AlbumsFragment newInstance() {
        AlbumsFragment albumsFragment = new AlbumsFragment();
        return albumsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackModel = ViewModelProviders.of(this).get(TrackModel.class);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_albums_list, container, false);

        mCollapsingToolbarLayout = view.findViewById(R.id.f_album_list_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle("Albums");

        mToolbar = view.findViewById(R.id.f_album_list_toolbar);
        mRecyclerView = view.findViewById(R.id.f_album_list_recyclerView);

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

        mAlbumRecyclerViewAdapter.loadAllAlbums(mTrackModel.getAlbums());
        mRecyclerView.setAdapter(mAlbumRecyclerViewAdapter);

        return view;
    }

    private void setUpRecyclerView(Context context) {
        mAlbumRecyclerViewAdapter = new AlbumRecyclerViewAdapter(context);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        mAlbumRecyclerViewAdapter.setOnItemClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            //TODO: Step 4 of 4: Finally call getTag() on the view.
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
//            ((MainActivity) getActivity()).playAudio(mAlbumsList, position);
            Toast.makeText(getContext(), "You Clicked: " + mAlbumRecyclerViewAdapter.getAlbumsList().get(position).getTrackId(), Toast.LENGTH_SHORT).show();

            Fragment albumListFragment = AlbumListFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("ALBUM_ID", mAlbumRecyclerViewAdapter.getAlbumsList().get(position).getTrackId());
            albumListFragment.setArguments(bundle);
            ((MainActivity) getActivity()).pushFragment(albumListFragment);
        }
    };

}
