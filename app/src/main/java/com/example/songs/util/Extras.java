package com.example.songs.util;

public class Extras {

    private static Extras instance;

    public Extras() {
    }

    public static Extras init() {
        if (instance == null) {
            instance = new Extras();
        }
        return instance;
    }

    public static Extras getInstance() {
        return instance;
    }

//    public String getAudioFilter(){
//        return MusicXApplication.getmPreferences().getString(AUDIO_FILTER, "0");
//    }

}
