package com.nhom13.learningenglishapp.database.models;

import java.io.Serializable;

public class Vocabulary implements Serializable {
    private int id;
    private String word;
    private int chapterId;
    private String imagePath;
    private Chapter chapter;
    private int viewCount;

    public Vocabulary() {
        this.viewCount = 0;
    }

    public Vocabulary(String word, int chapterId, String imagePath) {
        this.word = word;
        this.chapterId = chapterId;
        this.imagePath = imagePath;
        this.viewCount = 0;
    }


    public Vocabulary(String word, int chapterId, String imagePath, int viewCount) {
        this.word = word;
        this.chapterId = chapterId;
        this.imagePath = imagePath;
        this.viewCount = viewCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }


    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

}