package com.example.demochatfirebase.model;

public interface Constants {
    String FIREBASE_STORAGE_URL = "gs://music-server-5ec92.appspot.com";
    String FIREBASE_SONG_PATH = "music";
    String FIREBASE_STORAGE_SONG_URL = FIREBASE_STORAGE_URL + "/" + FIREBASE_STORAGE_URL;

    String FIREBASE_REALTIME_DATABASE_URL = "https://music-server-5ec92-default-rtdb.firebaseio.com/";
    String FIREBASE_REALTIME_SONG_PATH = "song";
    String FIREBASE_REALTIME_HOME_PATH = "home";
    String FIREBASE_REALTIME_SEARCH_PATH = "search";
    String FIREBASE_REALTIME_SONG_URL = FIREBASE_REALTIME_DATABASE_URL + "/" + FIREBASE_REALTIME_SONG_PATH;

    String SONG_ID_NAME = "songID";
    String PLAYLIST_ID_NAME = "playlistID";
}
