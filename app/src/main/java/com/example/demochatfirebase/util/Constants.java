package com.example.demochatfirebase.util;

public interface Constants {
    String FIREBASE_STORAGE_URL = "gs://fir-demo-59dee.appspot.com";
    String FIREBASE_SONG_PATH = "music";
    String FIREBASE_STORAGE_SONG_URL = FIREBASE_STORAGE_URL + "/" + FIREBASE_STORAGE_URL;

    String FIREBASE_REALTIME_DATABASE_URL = "https://fir-demo-59dee.firebaseio.com/";
    String FIREBASE_REALTIME_SONG_PATH = "song";
    String FIREBASE_REALTIME_SONG_URL = FIREBASE_REALTIME_DATABASE_URL + "/" + FIREBASE_REALTIME_SONG_PATH;
}
