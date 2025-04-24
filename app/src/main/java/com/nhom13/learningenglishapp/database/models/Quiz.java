package com.nhom13.learningenglishapp.database.models;

import java.io.Serializable;

public class Quiz implements Serializable {
    private int id;
    private String correctAnswer;
    private String wrongAnswer;
    private String imagePath;

    public Quiz() {
    }

    public Quiz(String correctAnswer, String wrongAnswer, String imagePath) {
        this.correctAnswer = correctAnswer;
        this.wrongAnswer = wrongAnswer;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}