package com.sbtn.androidtv.datamodels;

/**
 * Created by hoa nguyen on 5/9/16.
 * Model just used for show info item
 */
public class InfoPlayControlItem implements PlayItemInterface {
    public String link;
    public String image;
    public String title;
    public String description;
    public int id;

    @Override
    public String iGetLinkPlay() {
        return link;
    }

    @Override
    public String iGetLinkImage() {
        return image;
    }

    @Override
    public String iGetTitle() {
        return title;
    }

    @Override
    public String iGetDescription() {
        return description;
    }

    @Override
    public int iGetId() {
        return id;
    }
}
