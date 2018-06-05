package com.shohiebsense.idiomaticsynonym.model.event;

public class EnglishFragmentIdiomEvent {

    private String idiom;

    public EnglishFragmentIdiomEvent(String idiom) {
        this.idiom = idiom;
    }

    public String getIdiom() {
        return idiom;
    }

    public void setIdiom(String idiom) {
        this.idiom = idiom;
    }
}
