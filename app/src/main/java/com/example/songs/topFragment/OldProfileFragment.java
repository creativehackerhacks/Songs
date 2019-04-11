package com.example.songs.topFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.songs.R;
import com.example.songs.innerFragments.profileFragments.ProfileCirclesFragment;
import com.example.songs.innerFragments.profileFragments.ProfileFollowersFragment;
import com.example.songs.innerFragments.profileFragments.ProfileFollowingFragment;
import com.example.songs.innerFragments.profileFragments.ProfilePostsFragment;
import com.example.songs.pagerAdapter.ProfileViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static android.app.Activity.RESULT_OK;

public class OldProfileFragment extends Fragment {

    private ImageView mCoverImage;
    private boolean isVisible = false;

    private ProfileViewPagerAdapter mProfileViewPagerAdapter;
    private ViewPager mOuterViewPager;
    private TabLayout mOuterTabLayout;

    private LinearLayout mLinearLayout;
    private RelativeLayout mFirstRelLayout, mSecondRelLayout;

    private Animation mUpAnimation, mDownAnimation;


    public OldProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        OldProfileFragment oldProfileFragment = new OldProfileFragment();
        return oldProfileFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_old_profile, container, false);

        mCoverImage = view.findViewById(R.id.f_profile_inside_proImage);

        mOuterViewPager = view.findViewById(R.id.f_profile_outer_viewPager);
        mOuterTabLayout = view.findViewById(R.id.outer_tabLayout);

        mProfileViewPagerAdapter = new ProfileViewPagerAdapter(getChildFragmentManager());

        mProfileViewPagerAdapter.addFragment(new ProfilePostsFragment(), "Songs");
        mProfileViewPagerAdapter.addFragment(new ProfileCirclesFragment(), "Circles");
        mProfileViewPagerAdapter.addFragment(new ProfileFollowersFragment(), "Followers");
        mProfileViewPagerAdapter.addFragment(new ProfileFollowingFragment(), "Following");
        mOuterViewPager.setAdapter(mProfileViewPagerAdapter);
//        mOuterViewPager.setPageMargin(24);

        mOuterTabLayout.setupWithViewPager(mOuterViewPager);

        mLinearLayout = view.findViewById(R.id.f_profile_inside_lin_layout);
        mFirstRelLayout = view.findViewById(R.id.first_rel);
        mSecondRelLayout = view.findViewById(R.id.second_rel);

        mUpAnimation = AnimationUtils.loadAnimation(container.getContext(), R.anim.slide_up);
        mDownAnimation = AnimationUtils.loadAnimation(container.getContext(), R.anim.slide_down);

        mLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible) {
                    mSecondRelLayout.setVisibility(View.GONE);
                    mSecondRelLayout.startAnimation(mUpAnimation);
                    isVisible = false;
                } else {
                    mSecondRelLayout.startAnimation(mDownAnimation);
                    mSecondRelLayout.setVisibility(View.VISIBLE);
                    isVisible = true;
                }
            }
        });


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