package com.example.songs.animation;

import android.view.animation.Interpolator;

public class MyBounceInterpolator implements Interpolator {

    private double mAmplitude;
    private double mFrequency;

    public MyBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    @Override
    public float getInterpolation(float time) {
        float result = (float) (-1 * Math.pow(Math.E, -time / mAmplitude) * Math.cos(mFrequency * time) + 1);
        return result;
    }
}
