package com.shohiebsense.idiomaticsynonym.model.event;

public class IdiomsSummarrydiomEvent {

    private String idiom;

    public IdiomsSummarrydiomEvent(String idiom) {
        this.idiom = idiom;
    }

    public String getIdiom() {
        return idiom;
    }

    public void setIdiom(String idiom) {
        this.idiom = idiom;
    }
}
