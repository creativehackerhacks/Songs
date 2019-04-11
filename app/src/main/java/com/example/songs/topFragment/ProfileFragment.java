package com.example.songs.topFragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.songs.R;
import com.example.songs.innerFragments.profileBioFragments.ProfileBioSocialFragment;
import com.example.songs.innerFragments.profileBioFragments.ProfileBioTextFragment;
import com.example.songs.innerFragments.profileFragments.ProfileCirclesFragment;
import com.example.songs.innerFragments.profileFragments.ProfileFollowersFragment;
import com.example.songs.innerFragments.profileFragments.ProfileFollowingFragment;
import com.example.songs.innerFragments.profileFragments.ProfilePostsFragment;
import com.example.songs.pagerAdapter.ProfileViewPagerAdapter;
import com.example.songs.pagerAdapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private ImageView mCoverImage;

    private ViewPagerAdapter mViewPagerAdapter;
    private ProfileViewPagerAdapter mProfileViewPagerAdapter;
    private ViewPager mViewPager;

    private ViewPager mOuterViewPager;
    private TabLayout mOuterTabLayout;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.f_profile_new, container, false);

        mCoverImage = view.findViewById(R.id.f_profile_inside_proImage);
        mViewPager = view.findViewById(R.id.f_profile_viewPager);

        mOuterViewPager = view.findViewById(R.id.f_profile_outer_viewPager);
        mOuterTabLayout = view.findViewById(R.id.outer_tabLayout);

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mProfileViewPagerAdapter = new ProfileViewPagerAdapter(getChildFragmentManager());

        mViewPagerAdapter.addFragment(new ProfileBioTextFragment());
        mViewPagerAdapter.addFragment(new ProfileBioSocialFragment());
        mViewPager.setAdapter(mViewPagerAdapter);

        mProfileViewPagerAdapter.addFragment(new ProfilePostsFragment(), "Songs");
        mProfileViewPagerAdapter.addFragment(new ProfileCirclesFragment(), "Circles");
        mProfileViewPagerAdapter.addFragment(new ProfileFollowersFragment(), "Followers");
        mProfileViewPagerAdapter.addFragment(new ProfileFollowingFragment(), "Following");
        mOuterViewPager.setAdapter(mProfileViewPagerAdapter);
//        mOuterViewPager.setPageMargin(24);

        mOuterTabLayout.setupWithViewPager(mOuterViewPager);


        mCoverImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Glide.with(getContext()).load(selectedImage)
                    .apply(new RequestOptions().circleCropTransform())
                    .into(mCoverImage);
        }
    }

}
