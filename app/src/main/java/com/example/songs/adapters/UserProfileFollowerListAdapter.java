package com.example.songs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songs.R;
import com.example.songs.data.model.UserProfileData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFollowerListAdapter extends RecyclerView.Adapter<UserProfileFollowerListAdapter.UserProfileFollowerListAdapterViewHolder> {

    private Context mContext;
    private List<UserProfileData> mUserProfileFollowerDataList = new ArrayList<>();

    private View.OnClickListener mItemClickListener;
    private View.OnClickListener mFollowIngBtnClickListener;

    public UserProfileFollowerListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void loadAllUsersFollowingData(List<UserProfileData> userProfileFollowerData) {
        if(userProfileFollowerData == null) {
            return;
        }
        mUserProfileFollowerDataList.clear();
        mUserProfileFollowerDataList.addAll(userProfileFollowerData);
        notifyDataSetChanged();
    }

    public List<UserProfileData> getAllUsersFollowingData() {
        return mUserProfileFollowerDataList;
    }

    @NonNull
    @Override
    public UserProfileFollowerListAdapter.UserProfileFollowerListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_profile_followling_list, parent, false);

        return new UserProfileFollowerListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileFollowerListAdapter.UserProfileFollowerListAdapterViewHolder holder, int position) {
        UserProfileData userProfileData = mUserProfileFollowerDataList.get(position);
        holder.bind(userProfileData);
    }

    @Override
    public int getItemCount() {
        return mUserProfileFollowerDataList.size();
    }

    public void setmItemClickListener(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setmFollowIngBtnClickListener(View.OnClickListener followIngBtnClickListener) {
        mFollowIngBtnClickListener = followIngBtnClickListener;
    }

    public class UserProfileFollowerListAdapterViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProImageView;
        private TextView mProName;
        private TextView mProUserName;
        private Button mProFollowIngBtn;

        public UserProfileFollowerListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mProImageView = itemView.findViewById(R.id.row_profile_following_proImage);
            mProName = itemView.findViewById(R.id.row_profile_following_name);
            mProUserName = itemView.findViewById(R.id.row_profile_following_userName);
            mProFollowIngBtn = itemView.findViewById(R.id.row_profile_following_followIng_btn);

            itemView.setTag(this);
            itemView.setOnClickListener(mItemClickListener);
            mProFollowIngBtn.setTag(this);
            mProFollowIngBtn.setOnClickListener(mFollowIngBtnClickListener);
        }

        public void bind(UserProfileData userProfileData) {
            mProName.setText(userProfileData.getName());
            mProUserName.setText(userProfileData.getUserName());

            Glide.with(mContext).load(userProfileData.getmImageUrl()).into(mProImageView);

            isFollowing(mProFollowIngBtn, userProfileData.getmUserId());
        }

        private void isFollowing(Button button, String sendUserId) {
            FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("follow").child(mCurrentUser.getUid())
                    .child("Following");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(sendUserId).exists()) {
                        button.setText("Following");
                    } else {
                        button.setText("Follow");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
