package com.sbtn.androidtv.eventbus;

/**
 * Created by hoanguyen on 6/23/16.
 */
public class ReloadDataEvent extends BaseEvent {

    public ReloadDataEvent(String tagPoster) {
        super(tagPoster);
    }

    @Override
    String toMyString() {
        return ReloadDataEvent.class.getSimpleName();
    }
}
