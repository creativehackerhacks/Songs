package com.example.songs.util.touchListeners;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public abstract class RelativeLayoutMinPlayerSwipeListener implements OnTouchListener {

    static final String logTag = "ActivitySwipeDetector";
    private Context mContext;
    static final int MIN_DISTANCE = 100;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, downY, upX, upY;

    public RelativeLayoutMinPlayerSwipeListener(Context context) {
        mContext = context;
    }

    public abstract void onRightToLeftSwipe();
//    {
//        Log.i(logTag, "RightToLeftSwipe!");
//        Toast.makeText(mContext, "RightToLeftSwipe", Toast.LENGTH_SHORT).show();
//        // activity.doSomething();
//    }

    public abstract void onLeftToRightSwipe();
//    {
//        Log.i(logTag, "LeftToRightSwipe!");
//        Toast.makeText(mContext, "LeftToRightSwipe", Toast.LENGTH_SHORT).show();
//        // activity.doSomething();
//    }

    public abstract void onTopToBottomSwipe();
//    {
//        Log.i(logTag, "onTopToBottomSwipe!");
//        Toast.makeText(mContext, "onTopToBottomSwipe", Toast.LENGTH_SHORT).show();
//        // activity.doSomething();
//    }

    public abstract void onBottomToTopSwipe();
//    {
//        Log.i(logTag, "onBottomToTopSwipe!");
//        Toast.makeText(mContext, "onBottomToTopSwipe", Toast.LENGTH_SHORT).show();
//        // activity.doSomething();
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long horizontally, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                // swipe vertical?
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long vertically, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                return false; // no swipe horizontally and no swipe vertically
            }// case MotionEvent.ACTION_UP:
        }
        return false;
    }


}
