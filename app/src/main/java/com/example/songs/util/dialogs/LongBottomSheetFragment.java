package com.example.songs.util.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.songs.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LongBottomSheetFragment extends BottomSheetDialogFragment {

    private LongBottomSheetListener mLongBottomSheetListener;
    private LinearLayout mDeleteLinearLayout;
    private LinearLayout mLongBottomSheetSend;

    private LongBottomSheetFragment() {

    }

    public static LongBottomSheetFragment getInstance() {
        return new LongBottomSheetFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.long_bottomsheet_fragment, container, false);

        mDeleteLinearLayout = view.findViewById(R.id.long_bottomsheet_delete);
        mLongBottomSheetSend = view.findViewById(R.id.long_bottomsheet_send);

        mDeleteLinearLayout.setOnClickListener(v-> {
            mLongBottomSheetListener.onButtonClicked(v.getId());
            dismiss();
        });

        mLongBottomSheetSend.setOnClickListener(v -> {
            mLongBottomSheetListener.onButtonClicked(v.getId());
            dismiss();
        });

        return view;
    }

//    // Use this if you want the activity to implement this method.
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            mLongBottomSheetListener = (LongBottomSheetListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement LongBottomSheetListener");
//        }
//    }

    public void setLongBottomListener(LongBottomSheetListener longBottomSheetListener) {
        mLongBottomSheetListener = longBottomSheetListener;
    }

    public interface LongBottomSheetListener {
        void onButtonClicked(int id);
    }

    /**
     * Overriding the theme of the activity/fragment (in this case - fragment)
     * to apply our own custom theme from the "style"
     * @return
     */
    @Override
    public int getTheme() {
//        return super.getTheme();
        return R.style.BottomSheetDialogTheme;
    }

}
