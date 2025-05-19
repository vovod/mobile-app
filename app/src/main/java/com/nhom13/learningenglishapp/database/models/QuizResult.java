package com.nhom13.learningenglishapp.database.models;

import java.io.Serializable;

public class QuizResult implements Serializable {
    private int id;
    private int userId;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private long date;

    public QuizResult() {
    }

    public QuizResult(int userId, int score, int totalQuestions, int correctAnswers, long date) {
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


    public float getPercentageCorrect() {
        if (totalQuestions == 0) return 0;
        return ((float) correctAnswers / totalQuestions) * 100;
    }
}