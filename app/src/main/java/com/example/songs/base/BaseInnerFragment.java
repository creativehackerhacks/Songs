package com.example.songs.base;

import com.example.songs.activity.MainActivity;

import androidx.fragment.app.Fragment;

public class BaseInnerFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
//        ((MainActivity) getActivity()).hideStatusBar();
    }

    @Override
    public void onStop() {
        super.onStop();
//        ((MainActivity) getActivity()).showStatusBar();
    }
}
