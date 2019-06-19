package com.example.songs.util;

import com.example.songs.data.model.Tracks;

import java.util.ArrayList;

public class UtilConstants {

    private static final String PACKAGENAME = "com.example.songs.";

    // ArrayList for recently played songs
    public static ArrayList<Tracks> mRecentlyPlayedSongs = new ArrayList<>();
    /*
    Choices
     */
    public static final String Zero = "0";
    public static final String One = "1";
    public static final String Two = "2";
    public static final String Three = "3";
    public static final String Four = "4";
    public static final String Five = "5";

    public static final String PLAYBACK_STATE = PACKAGENAME + "PLAYBACK_STATE";

    // External Packages
    public static final String PACKAGE_FACEBOOK = "com.facebook.katana";
    public static final String PACKAGE_INSTAGRAM = "com.instagram.android";

}
