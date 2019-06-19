package com.example.songs.util.touchListeners;

import android.view.View;
import android.widget.Button;

public interface RecyclerViewTouchListener {

    void onItemClick(View view, int position);
    void onButtonClick(View view, int position);

}
