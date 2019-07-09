package com.example.songs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songs.R;
import com.example.songs.data.model.SharedFollowingSong;

import java.util.ArrayList;
import java.util.List;

public class SharedSongAdapter extends RecyclerView.Adapter<SharedSongAdapter.SharedSongViewHolder> {

    private Context mContext;
    private List<SharedFollowingSong> mSharedFollowingList = new ArrayList<>();

    private View.OnClickListener mItemClickListener;

    public SharedSongAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void loadAllSongNameList(List<SharedFollowingSong> sharedFollowingList) {
        if(sharedFollowingList == null) {
            return;
        }
        mSharedFollowingList.clear();
        mSharedFollowingList = sharedFollowingList;
        notifyDataSetChanged();
    }

    public List<SharedFollowingSong> getSharedFollowingList() {
        return mSharedFollowingList;
    }

    @NonNull
    @Override
    public SharedSongAdapter.SharedSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_following_song_uri_request, parent, false);

        return new SharedSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedSongAdapter.SharedSongViewHolder holder, int position) {
        SharedFollowingSong sharedFollowingSong = mSharedFollowingList.get(position);
        holder.bind(sharedFollowingSong);
    }

    @Override
    public int getItemCount() {
        return mSharedFollowingList.size();
    }

    public void setItemClickListener(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }


    public class SharedSongViewHolder extends RecyclerView.ViewHolder {

        private TextView mSongName;

        public SharedSongViewHolder(@NonNull View itemView) {
            super(itemView);
            mSongName = itemView.findViewById(R.id.row_following_song_uri_songName_TV);
            itemView.setTag(this);
            itemView.setOnClickListener(mItemClickListener);
        }

        public void bind(SharedFollowingSong sharedFollowingSong) {
            mSongName.setText(sharedFollowingSong.getSongName());
        }
    }


}
