package com.shohiebsense.idiomaticsynonym.model.event;

public class IdiomCardViewEvent {

    public static final int LAYOUT_LINEAR = 0;
    public static final int LAYOUT_CARD = 1;
    public static final int LAYOUT_SLIDE = 2;
    private int layout;

    public IdiomCardViewEvent(int layout) {
        this.layout = layout;
    }

    public int getLayout() {
        return layout;
    }
}

