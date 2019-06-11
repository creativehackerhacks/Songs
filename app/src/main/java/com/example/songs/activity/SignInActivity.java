package com.example.songs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.songs.R;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1000;
    // For FireBase Login
    private List<IdpConfig> mProvidersList;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        // Init Auth Providers
        mProvidersList = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        showSignInOptions();

    }

    public void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(mProvidersList)
                        .setTheme(R.style.AppTheme)
                        .build(), MY_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(requestCode == RESULT_OK) {
                // Get User
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                // Show email on Toast
                Toast.makeText(SignInActivity.this, ""+ user.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignInActivity.this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user == null) {
            return;
        } else {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void signOut() {
        mFirebaseAuth.signOut();
        LoginManager.getInstance().logOut();
    }


}
