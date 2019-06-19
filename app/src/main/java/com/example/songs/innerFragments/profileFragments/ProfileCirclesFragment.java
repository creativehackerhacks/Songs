package com.example.songs.innerFragments.profileFragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.adapters.UserProfileAdapter;
import com.example.songs.data.model.UserProfileData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileCirclesFragment extends Fragment {

    public static final String PROFILE_CIRCLES_FRAGMENT = ProfileCirclesFragment.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private UserProfileAdapter mUserProfileAdapter;
    private List<UserProfileData> mUserProfileList = new ArrayList<>();;

    private DatabaseReference mFollowUserDataBase;
    private FirebaseUser mCurrentUser;
    private String mSendUserId;


    public ProfileCirclesFragment() {
        // Required empty public constructor
    }

    public static ProfileCirclesFragment getInstance() {
        ProfileCirclesFragment profileCirclesFragment = new ProfileCirclesFragment();
        Bundle bundle = new Bundle();
        profileCirclesFragment.setArguments(bundle);
        return profileCirclesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFollowUserDataBase = FirebaseDatabase.getInstance().getReference("follow");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_circles, container, false);

        mRecyclerView = view.findViewById(R.id.f_p_c_user_profile_list);

        setUpRecyclerView(container.getContext());

        return view;
    }

    private void setAdapterData(List<UserProfileData> mUserProfileList) {
        mUserProfileAdapter = new UserProfileAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mUserProfileAdapter.loadAllUsers(mUserProfileList);
        mRecyclerView.setAdapter(mUserProfileAdapter);
        mUserProfileAdapter.setItemClickListener(mItemClickListener);
        mUserProfileAdapter.setButtonClickListener(mFollowIngBtnClickListener);
    }


    private void setUpRecyclerView(Context context) {
        setRecyclerViewAdapterData();
    }

    private void setRecyclerViewAdapterData() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootDataBaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        rootDataBaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfileList.clear();
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        if(!post.getKey().equals(mCurrentUser.getUid())) {
                        UserProfileData userProfile = new UserProfileData();
                        String user_id = post.getKey();
                        String proImageUrl = post.child("userProImage").getValue().toString();
                        String name = post.child("userProData").child("name").getValue().toString();
                        String userName = post.child("userProData").child("username").getValue().toString();

                        userProfile.setmUserId(user_id);
                        userProfile.setmImageUrl(proImageUrl);
                        userProfile.setName(name);
                        userProfile.setUserName(userName);
                        mUserProfileList.add(userProfile);

                    }
            }

                    setAdapterData(mUserProfileList);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(PROFILE_CIRCLES_FRAGMENT, "onCancelled: " + databaseError.getMessage());
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

        Log.e(PROFILE_CIRCLES_FRAGMENT, "sendFollowRequest: -- ");
        mSendUserId = mUserProfileList.get(position).getmUserId();
        button.setEnabled(false);

        if(button.getText().equals("Follow")) {
            mFollowUserDataBase.child(mCurrentUser.getUid()).child("Following").child(mSendUserId)
                    .setValue(true);
            mFollowUserDataBase.child(mSendUserId).child("Followers").child(mCurrentUser.getUid())
                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    button.setEnabled(true);
                }
            });
        } else {
            mFollowUserDataBase.child(mCurrentUser.getUid()).child("Following").child(mSendUserId)
                    .removeValue();
            mFollowUserDataBase.child(mSendUserId).child("Followers").child(mCurrentUser.getUid())
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    button.setEnabled(true);
                }
            });
        }
    }




}
