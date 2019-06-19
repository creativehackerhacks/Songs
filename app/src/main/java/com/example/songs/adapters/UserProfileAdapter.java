package com.example.songs.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.songs.R;
import com.example.songs.data.model.UserProfileData;
import com.example.songs.util.touchListeners.RecyclerViewTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.UserProfileAdapterViewHolder> {

    private Context mContext;
    private List<UserProfileData> mUserProfileList = new ArrayList<>();

    private View.OnClickListener mItemClick;
    private View.OnClickListener mButtonClick;

    public UserProfileAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void loadAllUsers(List<UserProfileData> userProfileData) {
        if (userProfileData == null) {
            return;
        }
        mUserProfileList.clear();
        mUserProfileList.addAll(userProfileData);
        notifyDataSetChanged();
    }

    public List<UserProfileData> getAllUsers() {
        return mUserProfileList;
    }

    @NonNull
    @Override
    public UserProfileAdapter.UserProfileAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_profile_list_item, parent, false);
        return new UserProfileAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileAdapter.UserProfileAdapterViewHolder holder, int position) {
        UserProfileData userProfile = mUserProfileList.get(position);
        holder.bind(userProfile);
    }

    @Override
    public int getItemCount() {
        return mUserProfileList.size();
    }

    public void setItemClickListener(View.OnClickListener itemClick) {
        mItemClick = itemClick;
    }

    public void setButtonClickListener(View.OnClickListener buttonClick) {
        mButtonClick = buttonClick;
    }

    public class UserProfileAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView mProImageView;
        private TextView mName;
        private TextView mUserName;
        private Button mFollowIngBtn;

        public UserProfileAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mProImageView = itemView.findViewById(R.id.row_profile_list_proImage);
            mName = itemView.findViewById(R.id.row_profile_list_name);
            mUserName = itemView.findViewById(R.id.row_profile_list_userName);
            mFollowIngBtn = itemView.findViewById(R.id.row_profile_list_followIng_btn);

            itemView.setTag(this);
            itemView.setOnClickListener(mItemClick);
            mFollowIngBtn.setTag(this);
            mFollowIngBtn.setOnClickListener(mButtonClick);

        }

        public void bind(UserProfileData userProfile) {
            Glide.with(mContext).load(userProfile.getmImageUrl())
                    .apply(new RequestOptions().circleCropTransform())
                    .placeholder(R.drawable.placeholder_gradient_track_artwork)
                    .into(mProImageView);
            mName.setText(userProfile.getName());
            mUserName.setText(userProfile.getUserName());

            // I will make this better in future.
            isFollowing(mFollowIngBtn, userProfile.getmUserId());
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
