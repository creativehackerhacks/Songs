package com.example.songs.innerFragments.profileBioFragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.songs.R;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileBioSocialFragment extends Fragment {

    private TextView mAddSocialAccount;

    private ProfileBioSocialFragment() {
        // Required empty public constructor
    }

    public static ProfileBioSocialFragment getInstance() {
        ProfileBioSocialFragment profileBioSocialFragment = new ProfileBioSocialFragment();
        return profileBioSocialFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_bio_social, container, false);

        mAddSocialAccount = view.findViewById(R.id.f_p_b_s_addSocialAccount);
        mAddSocialAccount.setOnClickListener(v-> {
            openSocialAccount();
        });

        return view;
    }

    private void openSocialAccount() {
        openInstagramAccount();
    }

    private void openInstagramAccount() {
        Uri uri = Uri.parse("http://instagram.com/_u/wicked_hacks");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/xxx")));
        }
    }

}
