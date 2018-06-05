package com.shohiebsense.idiomaticsynonym.model.event;

public class ViewEvent {

    private boolean isWrapped;

    public ViewEvent(boolean isWrapped) {
        this.isWrapped = isWrapped;
    }

    public boolean isWrapped() {
        return isWrapped;
    }

    public void setWrapped(boolean wrapped) {
        isWrapped = wrapped;
    }
}

