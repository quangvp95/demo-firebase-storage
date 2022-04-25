package com.example.demochatfirebase.model;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private int mIdAlbum;
    private String mNameAlbum;
    private List<Integer> mSongList = new ArrayList<>();

    public Album() {
    }

    public Album(Playlist playlist) {
        this(playlist.getIdCategory(), playlist.getNamePlaylist(), playlist.getSongList());
    }

    public Album(int mIdAlbum, String mNameAlbum) {
        this.mIdAlbum = mIdAlbum;
        this.mNameAlbum = mNameAlbum;
    }

    public Album(int mIdAlbum, String mNameAlbum, List<Song> mSongList) {
        this.mIdAlbum = mIdAlbum;
        this.mNameAlbum = mNameAlbum;
        this.mSongList = new ArrayList<>(mSongList.size());
        for (Song i : mSongList) {
            this.mSongList.add(i.getId());
        }
    }

    public List<Integer> getSongList() {
        return mSongList;
    }

    public int getIdAlbum() {
        return mIdAlbum;
    }

    public String getNameAlbum() {
        return mNameAlbum;
    }

    public void setIdAlbum(int mIdAlbum) {
        this.mIdAlbum = mIdAlbum;
    }

    public void setNameAlbum(String mNameCategory) {
        this.mNameAlbum = mNameCategory;
    }
}
