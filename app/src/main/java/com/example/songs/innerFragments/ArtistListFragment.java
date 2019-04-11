package com.example.songs.innerFragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.adapters.ArtistRecyclerViewAdapter;
import com.example.songs.base.BaseInnerFragment;
import com.example.songs.data.loaders.ArtistData;
import com.example.songs.data.model.Artist;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistListFragment extends BaseInnerFragment {

    public static final String ARTIST_LIST_FRAGMENT = ArtistListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ArtistRecyclerViewAdapter mArtistRecyclerViewAdapter;
    private List<Artist> mArtistList;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;


    public ArtistListFragment() {
        // Required empty public constructor
    }

    public static ArtistListFragment newInstance() {
        ArtistListFragment artistListFragment = new ArtistListFragment();
        return artistListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);


        mCollapsingToolbarLayout = view.findViewById(R.id.f_artist_list_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle("Albums");

        mToolbar = view.findViewById(R.id.f_artist_list_toolbar);
        mRecyclerView = view.findViewById(R.id.f_artist_list_recyclerView);

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
        mArtistList = new ArtistData(context).loadArtistListInBackground();
        mArtistRecyclerViewAdapter = new ArtistRecyclerViewAdapter(context, mArtistList);
        mRecyclerView.setAdapter(mArtistRecyclerViewAdapter);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        mArtistRecyclerViewAdapter.setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            Toast.makeText(getContext(), "v: " + mArtistList.get(position).getArtistName(), Toast.LENGTH_SHORT).show();
        }
    };

}
