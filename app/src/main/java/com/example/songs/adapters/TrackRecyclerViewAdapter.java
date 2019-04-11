package com.example.songs.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.songs.R;
import com.example.songs.data.model.Track;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackRecyclerViewAdapter extends RecyclerView.Adapter<TrackRecyclerViewAdapter.TrackViewHolder> {

    private Uri mArtWorkUri = Uri.parse("content://media/external/audio/albumart");
    private Context mContext;
    private List<Track> mTrackList;
    private View.OnClickListener mClickListener;
    private View.OnLongClickListener mLongClickListener;

    public TrackRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public TrackRecyclerViewAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_track_item, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackRecyclerViewAdapter.TrackViewHolder holder, int position) {
        Track track = mTrackList.get(position);
        holder.bindView(track);
    }

    //TODO: Step 2 of 4: Assign itemClickListener to your local View.OnClickListener variable
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(View.OnLongClickListener itemLongClickListener) {
        mLongClickListener = itemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return (null != mTrackList ? mTrackList.size() : 0);
    }

    public void addTrackList(List<Track> trackList) {
        if (trackList == null) {
            return;
        }
        mTrackList = trackList;
        notifyDataSetChanged();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView mTrackTitle, mTrackSubtitle;
        public ImageView mTrackArtwork;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            mTrackTitle = itemView.findViewById(R.id.row_card_track_trackName);
            mTrackSubtitle = itemView.findViewById(R.id.row_card_track_artistName);
            mTrackArtwork = itemView.findViewById(R.id.row_card_track_coverArt);

            itemView.setTag(this);
            itemView.setOnClickListener(mClickListener);
            itemView.setOnLongClickListener(mLongClickListener);
        }

        public void bindView(Track track) {
            Uri uri = ContentUris.withAppendedId(mArtWorkUri, track.getTrackId());
            Glide.with(itemView).load(uri)
                    .apply(new RequestOptions().circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_gradient_track_artwork)
                    .into(mTrackArtwork);

            mTrackTitle.setText(track.getTrackName());
            mTrackSubtitle.setText(track.getTrackArtistName());
        }

    }

}