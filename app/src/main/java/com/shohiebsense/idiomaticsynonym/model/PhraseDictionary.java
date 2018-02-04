package com.shohiebsense.idiomaticsynonym.model;

/**
 * Created by shohiebsense on 14/06/17.
 */

public class PhraseDictionary {

    String phrase, translation;

    public PhraseDictionary(String phrase, String translation) {
        this.phrase = phrase;
        this.translation = translation;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
