package com.example.songs.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songs.activity.MainActivity;
import com.example.songs.archComp.TrackModel;

import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseInnerFragment extends Fragment {

    private TrackModel mTrackModel;

    public abstract int setLayout();
    public abstract void setUI(View view);
    public abstract void setRecyclerView();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setLayout(), container, false);

        setUI(view);
        setRecyclerView();

        return view;
    }

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
