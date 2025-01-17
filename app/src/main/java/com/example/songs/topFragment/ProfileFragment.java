package com.example.songs.topFragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.data.model.UserProfile;
import com.example.songs.innerFragments.ProfileEdit;
import com.example.songs.innerFragments.profileBioFragments.ProfileBioSocialFragment;
import com.example.songs.innerFragments.profileBioFragments.ProfileBioTextFragment;
import com.example.songs.innerFragments.profileFragments.ProfileCirclesFragment;
import com.example.songs.innerFragments.profileFragments.ProfileFollowersFragment;
import com.example.songs.innerFragments.profileFragments.ProfileFollowingFragment;
import com.example.songs.innerFragments.profileFragments.ProfilePostsFragment;
import com.example.songs.pagerAdapter.ProfileViewPagerAdapter;
import com.example.songs.pagerAdapter.ViewPagerAdapter;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public static final String PROFILE_FRAGMENT = ProfileFragment.class.getSimpleName();

    // For FireBase
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    private ImageView mCoverImage, mProfileImage;
    private Uri mProImageUri, mCoverImageUri;

    private ViewPagerAdapter mViewPagerAdapter;
    private ProfileViewPagerAdapter mProfileViewPagerAdapter;
    private ViewPager mViewPager;
    private Button mEditProfileBtn;

    private String strName;
    private String strUserName;
    private TextView mName;
    private TextView mUserName;

    private ViewPager mOuterViewPager;
    private TabLayout mOuterTabLayout;
    private UserProfile mUserProfile;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mStorageReference = FirebaseStorage.getInstance().getReference().child("users").child(mFirebaseUser.getUid()).child("userProfileImages");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid());

        mUserProfile = new UserProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_old_profile, container, false);

        mCoverImage = view.findViewById(R.id.f_profile_cover_image);
        mProfileImage = view.findViewById(R.id.f_profile_inside_proImage);
        mViewPager = view.findViewById(R.id.f_profile_viewPager);
        mEditProfileBtn = view.findViewById(R.id.f_profile_edit_profile);

        mName = view.findViewById(R.id.f_profile_inside_proName);
        mUserName = view.findViewById(R.id.f_profile_inside_proUserName);

        mOuterViewPager = view.findViewById(R.id.f_profile_outer_viewPager);
        mOuterTabLayout = view.findViewById(R.id.outer_tabLayout);

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mProfileViewPagerAdapter = new ProfileViewPagerAdapter(getChildFragmentManager());

        mViewPagerAdapter.addFragment(new ProfileBioTextFragment());
        mViewPagerAdapter.addFragment(ProfileBioSocialFragment.getInstance());
        mViewPager.setAdapter(mViewPagerAdapter);

        mProfileViewPagerAdapter.addFragment(new ProfilePostsFragment(), "Posts");
        mProfileViewPagerAdapter.addFragment(new ProfileCirclesFragment(), "Circles");
        mProfileViewPagerAdapter.addFragment(new ProfileFollowersFragment(), "Followers");
        mProfileViewPagerAdapter.addFragment(new ProfileFollowingFragment(), "Following");
        mOuterViewPager.setAdapter(mProfileViewPagerAdapter);
//        mOuterViewPager.setPageMargin(24);

        mOuterTabLayout.setupWithViewPager(mOuterViewPager);

        retrieveUserData();

        mCoverImage.setOnClickListener(mImageClick);
        mProfileImage.setOnClickListener(mImageClick);

        mEditProfileBtn.setOnClickListener(mProfileEditBtnClick);

        return view;
    }

    private void retrieveUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRootDataRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid());
        DatabaseReference userProDataRef = userRootDataRef.child("userProData");
        userProDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    strName = dataSnapshot.child("name").getValue().toString();
                    strUserName = dataSnapshot.child("username").getValue().toString();
                    mUserProfile.setName(strName);
                    mUserProfile.setUserName(strUserName);
                    setUserData();
                    Log.e(PROFILE_FRAGMENT, "onDataChange: NAME -- " + strName + " USERNAME -- " + strUserName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Firebase Retreive Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserData() {
        mName.setText(mUserProfile.getName());
        mUserName.setText(mUserProfile.getUserName());
        Log.e(PROFILE_FRAGMENT, "setUserData: setting Name== " + mUserProfile.getName());
    }

    private View.OnClickListener mProfileEditBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            Toast.makeText(getContext(), "Editing Profile", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).pushFragment(ProfileEdit.getInstance());
        }
    };



    @Override
    public void onStart() {
        super.onStart();

    }

    private void retrieveImages() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users").child(mFirebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("userCoverImage").exists()) {
                    String coverImage = dataSnapshot.child("userCoverImage").getValue().toString();
                    Log.e(PROFILE_FRAGMENT, "onDataChange: COVER IMAGE --  " + coverImage);
                    Glide.with(getContext()).load(coverImage)
                            .into(mCoverImage);
                }
                if(dataSnapshot.child("userProImage").exists()) {
                    String proImage = dataSnapshot.child("userProImage").getValue().toString();
                    Log.e(PROFILE_FRAGMENT, "onDataChange: PRO IMAGE --  " + proImage);
                    Glide.with(getContext()).load(proImage)
                            .apply(new RequestOptions().circleCropTransform())
                            .into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        retrieveImages();
    }

    private View.OnClickListener mImageClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.f_profile_cover_image:
                    Intent pickCoverPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickCoverPhoto, 1);//one can be replaced with any action code
                    break;
                case R.id.f_profile_inside_proImage:
                    Intent pickProfilePhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickProfilePhoto, 2);//one can be replaced with any action code
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 1) {
                mCoverImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media
                            .getBitmap(getContext().getContentResolver(), mCoverImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadFile(mCoverImageUri, requestCode);
            }
            if (requestCode == 2) {
                mProImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media
                            .getBitmap(getContext().getContentResolver(), mProImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadFile(mProImageUri, requestCode);
            }
        }
    }

    private void uploadFile(Uri resultUri, int requestCode) {
//        String uniqueTime = System.currentTimeMillis() + ".jpg";
        // checking if file is available
        switch (requestCode) {
            case 1:
                // getting the storage reference
                StorageReference coverStorageReference = mStorageReference.child("coverImage");

                UploadTask coverUploadTask = coverStorageReference.putFile(resultUri);
                Task<Uri> coverUriTask = coverUploadTask.continueWithTask(new Continuation<TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return coverStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Toast.makeText(getContext(), "Cover Uploaded", Toast.LENGTH_SHORT).show();
                        Uri coverUri = task.getResult();

                        // creating the upload object to store uploaded image.
//                            String uploadId = mDatabaseReference.push().getKey();
                        mDatabaseReference.child("userCoverImage").setValue(coverUri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                break;
            case 2:
                // getting the storage reference
                StorageReference proStorageReference = mStorageReference.child("proImage");

                UploadTask proUploadTask = proStorageReference.putFile(resultUri);
                Task<Uri> proUriTask = proUploadTask.continueWithTask(new Continuation<TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return proStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Toast.makeText(getContext(), "Pro Uploaded", Toast.LENGTH_SHORT).show();
                        Uri proUri = task.getResult();

                        // creating the upload object to store uploaded image.
//                            String uploadId = mDatabaseReference.push().getKey();
                        mDatabaseReference.child("userProImage").setValue(proUri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                break;
        }
    }


}
