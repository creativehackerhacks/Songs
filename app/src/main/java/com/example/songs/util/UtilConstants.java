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

    // External Packages
    public static final String PACKAGE_FACEBOOK = "com.facebook.katana";
    public static final String PACKAGE_INSTAGRAM = "com.instagram.android";

    // For Firebase
    public static final String UPLOAD_SONG_TYPE = "audio/mpeg";

    /*
    song properties
     */
    public static final String SONG_ID = PACKAGENAME + "song_id";
    public static final String SONG_TITLE = PACKAGENAME + "song_title";
    public static final String SONG_ARTIST = PACKAGENAME + "song_artist";
    public static final String SONG_ALBUM = PACKAGENAME + "song_album";
    public static final String SONG_ALBUM_ID = PACKAGENAME + "song_album_id";
    public static final String SONG_TRACK_NUMBER = PACKAGENAME + "song_track_number";
    public static final String SONG_PATH = PACKAGENAME + "song_path";
    public static final String SONG_YEAR = PACKAGENAME + "song_year";

    // For Music Service
    public static final String PREF_AUTO_PAUSE = PACKAGENAME + "AUTO_PAUSE";
    public static final String ACTION_PLAY = PACKAGENAME + "ACTION_PLAY";
    public static final String ACTION_PAUSE = PACKAGENAME + "ACTION_PAUSE";
    public static final String ACTION_CHANGE_STATE = PACKAGENAME + "ACTION_CHANGE_STATE";
    public static final String ACTION_TOGGLE = PACKAGENAME + "ACTION_TOGGLE";
    public static final String ACTION_NEXT = PACKAGENAME + "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = PACKAGENAME + "ACTION_PREVIOUS";
    public static final String ACTION_STOP = PACKAGENAME + "ACTION_STOP";
    public static final String ACTION_CHOOSE_SONG = PACKAGENAME + "ACTION_CHOOSE_SONG";
    public static final String META_CHANGED = PACKAGENAME + "META_CHANGED";
    public static final String PLAYBACK_STATE = PACKAGENAME + "PLAYBACK_STATE";
    public static final String PLAYSTATE_CHANGED = PACKAGENAME + "PLAYSTATE_CHANGED";
    public static final String QUEUE_CHANGED = PACKAGENAME + "QUEUE_CHANGED";
    public static final String POSITION_CHANGED = PACKAGENAME + "POSITION_CHANGED";
    public static final String ITEM_ADDED = PACKAGENAME + "ITEM_ADDED";
    public static final String ORDER_CHANGED = PACKAGENAME + "ORDER_CHANGED";
    public static final String REPEAT_MODE_CHANGED = PACKAGENAME + "REPEAT_MODE_CHANGED";
    public static final String REPEATMODE = PACKAGENAME + "repeatMode";
    public static final String SHUFFLEMODE = PACKAGENAME + "shuffle";
    public static final String PLAYINGSTATE = PACKAGENAME + "playingState";
    public static final String CURRENTPOS = PACKAGENAME + "position";
    public static final String ACTION_PLAYINGVIEW = PACKAGENAME + "PLAYING_VIEW";
    public static final String ACTION_COMMAND = PACKAGENAME + "command";
    public static final String ACTION_COMMAND1 = PACKAGENAME + "command1";
    public static final String ACTION_COMMAND2 = PACKAGENAME + "command2";
    public static final String ACTION_FAV = PACKAGENAME + "widget_fav";
    public static final String PLAYER_POS = PACKAGENAME + "player_pos";
    public static final String PAUSE_SHORTCUTS = PACKAGENAME + "pause_shortcuts";
    public static final String PLAY_SHORTCUTS = PACKAGENAME + "pause_shortcuts";
    public static final String SHORTCUTS_TYPES = PACKAGENAME + "shortcuts_type";

}
