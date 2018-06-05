package com.shohiebsense.idiomaticsynonym.model.event;

import com.klinker.android.link_builder.Link;

import java.util.ArrayList;

public class IdiomsSummarryLinksEvent {

    private ArrayList<Link> links;

    public IdiomsSummarryLinksEvent(ArrayList<Link> links) {
        this.links = links;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }
}
