package com.example.songs.innerFragments.profileFragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.adapters.UserProfileAdapter;
import com.example.songs.adapters.UserProfileFollowingListAdapter;
import com.example.songs.data.model.FollowingUser;
import com.example.songs.data.model.UserProfile;
import com.example.songs.data.model.UserProfileData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFollowingFragment extends Fragment {

    public static final String PROFILE_FOLLOWING_FRAGMENT = ProfileCirclesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private UserProfileFollowingListAdapter mUserProfileFollowingAdapter;
    private List<UserProfileData> mUserProfileListData = new ArrayList<>();
    private List<String> mUserFollowingList = new ArrayList<>();

    private DatabaseReference mFollowingDatabaseReference;
    private FirebaseUser mCurrentUser;
    private String mSendUserId;


    public ProfileFollowingFragment() {
        // Required empty public constructor
    }

    public static ProfileFollowingFragment getInstance() {
        ProfileFollowingFragment profileFollowingFragment = new ProfileFollowingFragment();
        Bundle bundle = new Bundle();
        profileFollowingFragment.setArguments(bundle);
        return profileFollowingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFollowingDatabaseReference = FirebaseDatabase.getInstance().getReference("follow");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_following, container, false);

        mRecyclerView  = view.findViewById(R.id.f_p_c_user_profile_following_list);

        setUpRecyclerView(container.getContext());
//        getFollowingList();

        return view;
    }

    private void setUpRecyclerView(Context context) {
        setRecyclerViewAdapter();
    }

    private void setAdapterData() {
        mUserProfileFollowingAdapter = new UserProfileFollowingListAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mUserProfileFollowingAdapter.loadAllUsersFollowingData(mUserProfileListData);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setAdapter(mUserProfileFollowingAdapter);
        mUserProfileFollowingAdapter.setmItemClickListener(mItemClickListener);
        mUserProfileFollowingAdapter.setmFollowIngBtnClickListener(mFollowIngBtnClickListener);
    }

    private void setRecyclerViewAdapter() {
       getFollowingList();
    }

    private void getFollowingList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("follow")
                .child(mCurrentUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserFollowingList.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()) {
                    mUserFollowingList.add(post.getKey());
                    Log.e(PROFILE_FOLLOWING_FRAGMENT, "onDataChange: From Following -- " + mUserFollowingList);
                }

                getFollowingUserList(mUserFollowingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingUserList(List<String> userFollowingList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfileListData.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()) {
                    UserProfileData userProfile = new UserProfileData();
//                    Log.e(PROFILE_FOLLOWING_FRAGMENT, "onDataChange: NEWS -- " + followingUser);
                    String user_id = post.getKey();
                    String proImageUrl = post.child("userProImage").getValue().toString();
                    String name = post.child("userProData").child("name").getValue().toString();
                    String userName = post.child("userProData").child("username").getValue().toString();
                    userProfile.setmUserId(user_id);
                    userProfile.setmImageUrl(proImageUrl);
                    userProfile.setName(name);
                    userProfile.setUserName(userName);
                    for(String id : mUserFollowingList) {
                        if(userProfile.getmUserId().equals(id)) {
                            mUserProfileListData.add(userProfile);
                        }
                    }
                }

                setAdapterData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            Toast.makeText(getContext(), "Clicked on the User Profile and Position -- " + position, Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener mFollowIngBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Toast.makeText(getContext(), "Clicked on the button and Position -- " + position, Toast.LENGTH_SHORT).show();

            Button button = (Button) v;
            String currentBtnText = button.getText().toString();
            sendFollowRequest(button, position);
        }
    };

    private void sendFollowRequest(Button button, int position) {
//        String sendUserId = mUserProfileList.get(position).getmUserId();

        Log.e(PROFILE_FOLLOWING_FRAGMENT, "sendFollowRequest: -- ");
        mSendUserId = mUserProfileListData.get(position).getmUserId();
        button.setEnabled(false);

            mFollowingDatabaseReference.child(mCurrentUser.getUid()).child("Following").child(mSendUserId)
                    .removeValue();
            mFollowingDatabaseReference.child(mSendUserId).child("Followers").child(mCurrentUser.getUid())
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    button.setEnabled(true);
                }
            });

    }

}
