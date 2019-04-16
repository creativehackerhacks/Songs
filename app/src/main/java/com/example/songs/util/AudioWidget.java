package com.example.songs.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class AudioWidget implements Controller {


    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }


    // static inner class
    public static class Builder {

        private final Context mContext;

        private Drawable mPlayDrawable;
        private Drawable mPauseDrawable;

        public Builder (@NonNull Context context) {
            mContext = context;
        }

    }


}

interface Controller {

    void play();

    void pause();

}
