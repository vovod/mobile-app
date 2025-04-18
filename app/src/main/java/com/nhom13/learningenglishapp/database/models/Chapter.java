package com.nhom13.learningenglishapp.database.models;

import java.io.Serializable;

public class Chapter implements Serializable {
    private int id;
    private String name;
    private String imagePath;

    public Chapter() {
    }

    public Chapter(int id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}