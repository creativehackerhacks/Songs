package com.example.songs.innerFragments.profileFragments;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.adapters.SharedSongAdapter;
import com.example.songs.adapters.UserProfileAdapter;
import com.example.songs.adapters.UserProfileFollowingListAdapter;
import com.example.songs.data.model.SharedFollowingSong;
import com.example.songs.util.UtilConstants;
import com.example.songs.util.recyclerviewUtil.SwipeController;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilePostsFragment extends Fragment {

    public static final String PROFILE_POSTS_FRAGMENT = ProfilePostsFragment.class.getSimpleName();

    // Firebase
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    private RecyclerView mRecyclerView;
    private SharedSongAdapter mSharedSongAdapter;

    private List<SharedFollowingSong> mSharedFollowingSongList = new ArrayList<>();
    private List<String> mUserFollowingList = new ArrayList<>();


    public ProfilePostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("userSongUri").child(mFirebaseUser.getUid());

//        mStorageReference = FirebaseStorage.getInstance().getReference("users").child(mFirebaseUser.getUid()).child("userSongData");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_posts, container, false);

        mRecyclerView = view.findViewById(R.id.f_p_p_user_profile_following_songs_uri);

        setUpRecyclerViewData();

        return view;
    }

    private void setUpRecyclerViewData() {
        getFollowingList();
    }

    private void getFollowingList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("follow")
                .child(mFirebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserFollowingList.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()) {
                    mUserFollowingList.add(post.getKey());
                }
                getFollowingSongUriList(mUserFollowingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingSongUriList(List<String> userFollowingList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSongUri");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSharedFollowingSongList.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()) {
                    SharedFollowingSong sharedFollowingSong = new SharedFollowingSong();
                    String songName = post.child("songName").getValue().toString();
                    String publisher = post.child("publisher").getValue().toString();
                    String songPath = post.child("songPath").getValue().toString();
                    sharedFollowingSong.setSongName(songName);
                    sharedFollowingSong.setPublisher(publisher);
                    sharedFollowingSong.setSongPath(songPath);
                    for(String id : mUserFollowingList) {
                        if(sharedFollowingSong.getPublisher().equals(id)) {
                            mSharedFollowingSongList.add(sharedFollowingSong);
                        }
                    }
//                    Log.e(PROFILE_POSTS_FRAGMENT, "Following URI Data: " + mSharedFollowingSongList.get(0).getSongName());
                    setAdapterData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapterData() {
        mSharedSongAdapter = new SharedSongAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mSharedSongAdapter.loadAllSongNameList(mSharedFollowingSongList);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setAdapter(mSharedSongAdapter);
        mSharedSongAdapter.setItemClickListener(mItemClickListener);
    }

    private View.OnClickListener mItemClickListener = v -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();

        int position = viewHolder.getAdapterPosition();
        Toast.makeText(getContext(), mSharedSongAdapter.getSharedFollowingList().get(position).getSongName(), Toast.LENGTH_SHORT).show();
    };


}
