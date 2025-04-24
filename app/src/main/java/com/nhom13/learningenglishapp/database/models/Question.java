package com.nhom13.learningenglishapp.database.models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private List<Vocabulary> choices;
    private Vocabulary answer;

    public Question() {
    }

    public Question(List<Vocabulary> choices, Vocabulary answer) {
        this.choices = choices;
        this.answer = answer;
    }

    public List<Vocabulary> getChoices() {
        return choices;
    }

    public void setChoices(List<Vocabulary> choices) {
        this.choices = choices;
    }

    public Vocabulary getAnswer() {
        return answer;
    }

    public void setAnswer(Vocabulary answer) {
        this.answer = answer;
    }
}