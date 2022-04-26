package com.example.base.recycler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface RecyclerViewType {
    int TYPE_INVALID = 0;

    int TYPE_SONG = 10;
    int TYPE_SONG_SEARCH = 11;

    int TYPE_ALBUM = 20;
    int TYPE_ALBUM_SEARCH = 21;

    int TYPE_PLAYLIST = 30;
}
