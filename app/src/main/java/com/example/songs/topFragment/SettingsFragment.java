package com.example.songs.topFragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.activity.GoogleSignInActivity;
import com.example.songs.activity.MainActivity;
import com.example.songs.activity.SignInActivity;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Button mSignOut;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mSignOut = view.findViewById(R.id.f_settings_sing_out);

        mSignOut.setOnClickListener(mOnClickListener);

        return view;
    }

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // LogOut
            AuthUI.getInstance()
                    .signOut(getContext())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getActivity(), GoogleSignInActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

}
