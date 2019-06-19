package com.example.songs.innerFragments.profileBioFragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.util.UtilConstants;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileBioSocialFragment extends Fragment {

    private static final String PROFILE_BIO_SOCIAL_FRAGMENT = ProfileBioSocialFragment.class.getSimpleName();

    private TextView mAddSocialAccount;
    private ImageView mAddFacebookAccount;
    private ImageView mAddInstagramAccunt;

    private boolean isFaceBookInstalled;
    private boolean isInstaInstalled;

    public ProfileBioSocialFragment() {
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

        mAddSocialAccount = view.findViewById(R.id.f_p_b_s_addSocialAccount_TV);
        mAddFacebookAccount = view.findViewById(R.id.f_p_b_s_addFacebookAccount_IV);
        mAddInstagramAccunt = view.findViewById(R.id.f_p_b_s_addInstagramAccount_IV);

        mAddSocialAccount.setOnClickListener(v-> {
            Toast.makeText(container.getContext(), "Add other accounts", Toast.LENGTH_SHORT).show();
        });
        checkWhichAppInstalled(container.getContext());

        return view;
    }

    private void checkWhichAppInstalled(Context context) {
        isFaceBookInstalled = appInstalledOrNot(context, UtilConstants.PACKAGE_FACEBOOK);
        isInstaInstalled = appInstalledOrNot(context, UtilConstants.PACKAGE_INSTAGRAM);

        forFaceBook(context, isFaceBookInstalled);
        forInstagram(context, isInstaInstalled);
    }

    private void forFaceBook(Context context, boolean isFaceBookInstalled) {
        if (isFaceBookInstalled) {
            mAddFacebookAccount.setVisibility(View.VISIBLE);
            mAddFacebookAccount.setOnClickListener(v-> {
                Intent launchFacebookIntent = context.getPackageManager().getLaunchIntentForPackage(UtilConstants.PACKAGE_FACEBOOK);
                startActivity(launchFacebookIntent);
            });
        } else {
            mAddFacebookAccount.setVisibility(View.GONE);
            Log.e(PROFILE_BIO_SOCIAL_FRAGMENT, "forFaceBook: " + "App is not installed in your device.");
        }
    }

    private void forInstagram(Context context, boolean isInstaInstalled) {
        if (isFaceBookInstalled) {
            mAddInstagramAccunt.setVisibility(View.VISIBLE);
            mAddInstagramAccunt.setOnClickListener(v-> {
                Intent launchInstaIntent = context.getPackageManager().getLaunchIntentForPackage(UtilConstants.PACKAGE_INSTAGRAM);
                startActivity(launchInstaIntent);
            });
        } else {
            mAddInstagramAccunt.setVisibility(View.GONE);
            Log.e(PROFILE_BIO_SOCIAL_FRAGMENT, "forFaceBook: " + "App is not installed in your device.");
        }
    }

    private boolean appInstalledOrNot( Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        boolean appInstalled = false;
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appInstalled = false;
        }

        return appInstalled;
    }


//    private void openInstagramAccount() {
//        Uri uri = Uri.parse("http://instagram.com/_u/wicked_hacks");
//        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
//
//        likeIng.setPackage("com.instagram.android");
//
//        try {
//            startActivity(likeIng);
//        } catch (ActivityNotFoundException e) {
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://instagram.com/xxx")));
//        }
//    }

}
