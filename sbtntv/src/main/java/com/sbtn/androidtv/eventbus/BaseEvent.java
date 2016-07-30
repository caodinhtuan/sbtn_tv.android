package com.sbtn.androidtv.eventbus;

/**
 * Created by hoanguyen on 6/23/16.
 */
public abstract class BaseEvent {
    public final String tagPoster;

    public BaseEvent(String tagPoster) {
        this.tagPoster = tagPoster;
    }

    @Override
    public String toString() {
        return toMyString() + " from - " + tagPoster;
    }

    abstract String toMyString();
}
