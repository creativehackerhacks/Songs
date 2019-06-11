package com.example.songs.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.songs.R;
import com.example.songs.data.model.Tracks;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackRecyclerViewAdapter extends RecyclerView.Adapter<TrackRecyclerViewAdapter.TrackViewHolder> {

    private Uri mArtWorkUri = Uri.parse("content://media/external/audio/albumart");
    private Context mContext;
    private List<Tracks> mNewTracks = new ArrayList<>();
    private View.OnClickListener mClickListener;
    private View.OnLongClickListener mLongClickListener;

    public TrackRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void loadItems(List<Tracks> tracks) {
        if(tracks == null) {
            return;
        }
        mNewTracks.clear();
        mNewTracks.addAll(tracks);
        notifyDataSetChanged();
    }

    public List<Tracks> getAllTrackList() {
        return mNewTracks;
    }

    public void deleteSingleRow(Tracks tracks) {
        if(mNewTracks==null) {
            return;
        }
        mNewTracks.remove(tracks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackRecyclerViewAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_track_item, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackRecyclerViewAdapter.TrackViewHolder holder, int position) {
        Tracks tracks = mNewTracks.get(position);
        holder.bindView(tracks);
    }

    //TODO: Step 2 of 4: Assign itemClickListener to your local View.OnClickListener variable
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(OnLongClickListener itemLongClickListener) {
        mLongClickListener = itemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return (null != mNewTracks ? mNewTracks.size() : 0);
    }

    public void addTrackList(List<Tracks> tracksList) {
        if (tracksList == null) {
            return;
        }
        mNewTracks = tracksList;
        notifyDataSetChanged();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView mTrackTitle, mTrackSubtitle;
        public ImageView mTrackArtwork;
        // Complete the below one.
        public ImageView mMoreOptionIV;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            mTrackTitle = itemView.findViewById(R.id.row_card_track_trackName);
            mTrackSubtitle = itemView.findViewById(R.id.row_card_track_artistName);
            mTrackArtwork = itemView.findViewById(R.id.row_card_track_coverArt);
            mMoreOptionIV = itemView.findViewById(R.id.row_card_track_more);


            itemView.setTag(this);
            itemView.setOnClickListener(mClickListener);
            itemView.setOnLongClickListener(mLongClickListener);
        }

        public void bindView(Tracks tracks) {
            Uri uri = ContentUris.withAppendedId(mArtWorkUri, tracks.getTrackId());
            Glide.with(itemView).load(uri)
                    .apply(new RequestOptions().circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_gradient_track_artwork)
                    .into(mTrackArtwork);

            mTrackTitle.setText(tracks.getTrackName());
            mTrackSubtitle.setText(tracks.getTrackArtistName());
        }

    }

}
