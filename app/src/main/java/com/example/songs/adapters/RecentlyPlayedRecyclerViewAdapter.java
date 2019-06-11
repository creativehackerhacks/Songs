package com.example.songs.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.songs.R;
import com.example.songs.data.model.Tracks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RecentlyPlayedRecyclerViewAdapter extends RecyclerView.Adapter<RecentlyPlayedRecyclerViewAdapter.RecentlyPlayedViewHolder> {

    private Uri mArtWorkUri = Uri.parse("content://media/external/audio/albumart");

    private Context mContext;
    private View.OnClickListener mClickListener;
    private List<Tracks> mTracksList = new ArrayList<>();

    public RecentlyPlayedRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void loadRecentSongs(List<Tracks> tracksList) {
        if(tracksList == null) {
            return;
        }
        mTracksList.clear();
        mTracksList.addAll(tracksList);
        Collections.reverse(mTracksList);
        notifyDataSetChanged();
    }

    public List<Tracks> getAllRecentTracksList() {
        return mTracksList;
    }


    public void recentlyPlayedSetOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }


    @NonNull
    @Override
    public RecentlyPlayedRecyclerViewAdapter.RecentlyPlayedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_recently_played, parent, false);
        return new RecentlyPlayedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyPlayedRecyclerViewAdapter.RecentlyPlayedViewHolder holder, int position) {
        Tracks tracks = mTracksList.get(position);
        holder.bindView(tracks);
    }


    @Override
    public int getItemCount() {
        return (null != mTracksList ? mTracksList.size() : 0);
    }

    public class RecentlyPlayedViewHolder extends RecyclerView.ViewHolder  {

        private CardView mRecentCardView;
        private ImageView mCoverArt;
        private TextView mSongName;
        private TextView mSongArtistName;

        public RecentlyPlayedViewHolder(@NonNull View itemView) {
            super(itemView);
            mRecentCardView = itemView.findViewById(R.id.row_card_recently_played_main_layout);
            mCoverArt = itemView.findViewById(R.id.row_card_recently_played_track_coverArt);
            mSongName = itemView.findViewById(R.id.row_card_recently_played_trackName);
            mSongArtistName = itemView.findViewById(R.id.row_card_recently_played_artistName);

            itemView.setTag(this);
            itemView.setOnClickListener(mClickListener);
        }

        private void bindView(Tracks tracks) {
            Uri uri = ContentUris.withAppendedId(mArtWorkUri, tracks.getTrackId());
            Glide.with(itemView).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_gradient_track_artwork)
                    .into(mCoverArt);

            mSongName.setText(tracks.getTrackName());
            mSongArtistName.setText(tracks.getTrackArtistName());
        }

    }
}
