package com.example.songs.innerFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileEdit extends Fragment {

    // For FireBase
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;

    private ImageView mProImageView;
    private EditText mNameET;
    private EditText mUsernameET;
    private Button mSaveProfileBtn;

    public ProfileEdit() {
        // Required empty public constructor
    }

    public static ProfileEdit getInstance() {
        ProfileEdit profileEdit = new ProfileEdit();
//        Bundle bundle = new Bundle();
//        profileEdit.setArguments(bundle);
        return profileEdit;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        mProImageView = view.findViewById(R.id.f_p_e_profile_imageIV);
        mNameET = view.findViewById(R.id.f_p_e_nameET);
        mUsernameET = view.findViewById(R.id.f_p_e_usernameET);
        mSaveProfileBtn = view.findViewById(R.id.f_p_e_save_profileBtn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mCurrentUser.getUid()).child("userProData");

        mSaveProfileBtn.setOnClickListener(mSaveProfileBtnClick);

        return view;
    }

    private View.OnClickListener mSaveProfileBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveUserData();
        }
    };

    private void saveUserData() {
        String name = mNameET.getText().toString();
        String rawUserName = mUsernameET.getText().toString();
        String userName = "@" + rawUserName;

        if(name != null && userName != null) {
            Map<String, Object> mUserProfileDataObj = new HashMap<>();
            mUserProfileDataObj.put("name", name);
            mUserProfileDataObj.put("username", userName);

            mDatabaseReference.updateChildren(mUserProfileDataObj);
            ((MainActivity) getActivity()).popFragment();
        } else {
            Toast.makeText(getContext(), "Enter Name and UserName", Toast.LENGTH_SHORT).show();
        }

    }


}
