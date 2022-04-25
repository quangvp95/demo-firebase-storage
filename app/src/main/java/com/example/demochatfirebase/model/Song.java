package com.example.demochatfirebase.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.google.firebase.database.Exclude;

public class Song {

    private int id;
    private String nameSong;
    private String pathSong;
    private String singer;
    private String albumID;
    private String duration;
    private int idCategory;
    private String imageUrl;
    private String linkUrl;
    @Exclude
    private String testField = "asdf";

    public Song() {
        this(0, "", "", "", "", "");
    }

    //song offline
    public Song(int id, String nameSong, String imageUrl, String artist, String albumID,
        String duration) {
        this(id, nameSong, imageUrl, artist, albumID, duration, 0, "", "");
    }

    public Song(int id, String nameSong, String pathSong, String singer, String albumID,
        String duration, int idCategory, String mImageUrl, String mLinkUrl) {
        this.id = id;
        this.nameSong = nameSong;
        this.pathSong = pathSong;
        this.singer = singer;
        this.albumID = albumID;
        this.duration = duration;
        this.idCategory = idCategory;
        this.imageUrl = mImageUrl;
        this.linkUrl = mLinkUrl;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public int getIdCategory() {
        return idCategory;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    @Exclude
    public String getTestField() {
        return testField;
    }

    @Exclude
    public void setTestField(String testField) {
        this.testField = testField;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategoryID(int id) {
        this.idCategory = id;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
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
