package com.shohiebsense.idiomaticsynonym.model.event;

import com.google.rpc.Help;
import com.klinker.android.link_builder.Link;

import java.util.ArrayList;

public class EnglishFragmentLinksEvent {

    private ArrayList<Link> links;

    public EnglishFragmentLinksEvent(ArrayList<Link> links) {
        this.links = links;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }
}
