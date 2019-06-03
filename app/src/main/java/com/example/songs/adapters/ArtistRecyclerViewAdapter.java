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
import com.example.songs.data.model.Albums;
import com.example.songs.data.model.Artist;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ArtistViewHolder> {

    private Uri mUri = Uri.parse("content://media/external/audio/albumart");

    private Context mContext;
    private List<Artist> mArtistList;
    private View.OnClickListener mClickListener;

    public ArtistRecyclerViewAdapter(Context context, List<Artist> artistList) {
        mContext = context;
        mArtistList = artistList;
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ArtistRecyclerViewAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistRecyclerViewAdapter.ArtistViewHolder holder, int position) {
        Artist artist = mArtistList.get(position);
        holder.bindView(artist);
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public List<Artist> getArtistsList() {
        return mArtistList;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        private ImageView mArtistCoverArt;
        private TextView mArtistName;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            mArtistCoverArt = itemView.findViewById(R.id.row_card_artist_coverArt);
            mArtistName = itemView.findViewById(R.id.row_card_artist_name);

            itemView.setTag(this);
            itemView.setOnClickListener(mClickListener);
        }

        public void bindView(Artist artist) {
            Uri uri = ContentUris.withAppendedId(mUri, artist.getId());
            Glide.with(itemView).load(uri)
                    .apply(new RequestOptions().circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_gradient_track_artwork)
                    .into(mArtistCoverArt);

            mArtistName.setText(artist.getArtistName());
        }
    }

}
