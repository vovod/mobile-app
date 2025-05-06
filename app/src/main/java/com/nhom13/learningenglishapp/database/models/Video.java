package com.nhom13.learningenglishapp.database.models;

import java.io.Serializable;

public class Video implements Serializable {
    private int id;
    private String title;
    private String thumbnailUrl;
    private String videoUrl;

    public Video() {
    }

    public Video(String title, String thumbnailUrl, String videoUrl) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}