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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.AlbumViewHolder> {

    private Uri mUri = Uri.parse("content://media/external/audio/albumart");
    private Context mContext;
    private List<Albums> mAlbumsList = new ArrayList<>();
    private View.OnClickListener mClickListener;

    public AlbumRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void loadAllAlbums(List<Albums> albums) {
        if (albums == null) {
            return;
        }
        mAlbumsList.clear();
        mAlbumsList.addAll(albums);
        notifyDataSetChanged();
    }

    public List<Albums> getAlbumsList() {
        return mAlbumsList;
    }

    @NonNull
    @Override
    public AlbumRecyclerViewAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumRecyclerViewAdapter.AlbumViewHolder holder, int position) {
        Albums albums = mAlbumsList.get(position);
        holder.bindView(albums);
    }

    @Override
    public int getItemCount() {
        return mAlbumsList.size();
    }


    //TODO: Step 2 of 4: Assign itemClickListener to your local View.OnClickListener variable
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        private ImageView mAlbumCoverArt;
        private TextView mAlbumName;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            mAlbumCoverArt = itemView.findViewById(R.id.row_card_album_coverArt);
            mAlbumName = itemView.findViewById(R.id.row_card_album_name);

            itemView.setTag(this);
            itemView.setOnClickListener(mClickListener);
        }

        public void bindView(Albums albums) {
            Uri uri = ContentUris.withAppendedId(mUri, albums.getTrackId());
            Glide.with(itemView).load(uri)
                    .apply(new RequestOptions())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_gradient_album_artwork)
                    .into(mAlbumCoverArt);

            mAlbumName.setText(albums.getAlbumName());
        }
    }

}
