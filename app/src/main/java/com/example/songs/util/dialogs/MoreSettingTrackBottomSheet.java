package com.example.songs.util.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.songs.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MoreSettingTrackBottomSheet extends BottomSheetDialogFragment {

    private MoreSettingTrackBottomSheetListener moreSettingTrackBottomSheetListener;

    private LinearLayout mAddToQueueLL, mShareOutsideLL;

    public MoreSettingTrackBottomSheet() {
    }

    public static MoreSettingTrackBottomSheet getInstance() {
        return new MoreSettingTrackBottomSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_setting_track_bottomsheet, container, false);

        mAddToQueueLL = view.findViewById(R.id.m_s_t_b_AddToNext);
        mShareOutsideLL = view.findViewById(R.id.m_s_t_b_share_outside);

        mAddToQueueLL.setOnClickListener(v -> {
            moreSettingTrackBottomSheetListener.onButtonClicked(v.getId());
            dismiss();
        });

        mShareOutsideLL.setOnClickListener(v -> {
            moreSettingTrackBottomSheetListener.onButtonClicked(v.getId());
            dismiss();
        });

        return view;
    }

    public void setMoreSettingTrackBottomSheetListener(MoreSettingTrackBottomSheetListener listener) {
        moreSettingTrackBottomSheetListener = listener;
    }

    public interface MoreSettingTrackBottomSheetListener {
        void onButtonClicked(int id);
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}