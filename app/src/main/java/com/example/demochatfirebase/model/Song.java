package com.example.demochatfirebase.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

public class Song {

    private int id;
    private String nameSong;
    private String pathSong;
    private String singer;
    private String albumID;
    private String duration;
    private int idCategory;
    private String mImageUrl;
    private String mLinkUrl;

    public Song() {
        this(0, "", "", "", "", "");
    }

    //song offline
    public Song(int id, String nameSong, String imageUrl, String artist, String albumID, String duration) {
        this(id, nameSong, imageUrl, artist, albumID, duration, 0, "", "");
    }

    public Song(int id, String nameSong, String pathSong, String singer, String albumID, String duration, int idCategory, String mImageUrl, String mLinkUrl) {
        this.id = id;
        this.nameSong = nameSong;
        this.pathSong = pathSong;
        this.singer = singer;
        this.albumID = albumID;
        this.duration = duration;
        this.idCategory = idCategory;
        this.mImageUrl = mImageUrl;
        this.mLinkUrl = mLinkUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getId() {
        return id;
    }

    public String getNameSong() {
        return nameSong;
    }

    public String getPathSong() {
        return pathSong;
    }

    public String getSinger() {
        return singer;
    }

    public String getDuration() {
        return duration;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCategory() {
        return idCategory;
    }

    //lay anh theo bitmap neu co path
    public Bitmap loadImageFromPath(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
