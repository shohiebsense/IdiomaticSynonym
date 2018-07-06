package com.shohiebsense.idiomaticsynonym.model.event;

public class IdiomCardViewEvent {

    private boolean isSlideShow;

    public IdiomCardViewEvent(boolean isSlideShow) {
        this.isSlideShow = isSlideShow;
    }

    public boolean isSlideShow() {
        return isSlideShow;
    }

    public void setSlideShow(boolean isSlideShow) {
        this.isSlideShow = isSlideShow;
    }
}

