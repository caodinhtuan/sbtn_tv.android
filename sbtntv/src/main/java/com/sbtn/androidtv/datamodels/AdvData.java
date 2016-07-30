package com.sbtn.androidtv.datamodels;

/**
 * Created by hoanguyen on 7/5/16.
 * Class lưu info Adv sau khi load từ link xml về
 */
public class AdvData {
    private boolean shouldShowAdv = true;
    private Adv videoAdv;
    private Adv imageAdv;

    private int skipTimeVideo;
    private int startTimeImage;
    private String linkThumbnailItem;

    public int retryLoadTime = 0;

    public AdvData() {
    }

    public AdvData withVideoAdv(Adv videoAdv) {
        this.videoAdv = videoAdv;
        return this;
    }

    public AdvData withImageAdv(Adv imageAdv) {
        this.imageAdv = imageAdv;
        return this;
    }

    public Adv getVideoAdv() {
        return videoAdv;
    }

    public Adv getImageAdv() {
        return imageAdv;
    }

    public boolean isShouldShowAdv() {
        return shouldShowAdv;
    }

    public void enableShowAdv() {
        this.shouldShowAdv = true;
    }

    public void disableShowAdv() {
        this.shouldShowAdv = true;
    }

    public int getSkipTimeVideo() {
        return skipTimeVideo;
    }

    public void setSkipTimeVideo(int skipTimeVideo) {
        this.skipTimeVideo = skipTimeVideo;
    }

    public String getLinkThumbnailItem() {
        return linkThumbnailItem;
    }

    public void setLinkThumbnailItem(String linkThumbnailItem) {
        this.linkThumbnailItem = linkThumbnailItem;
    }

    public int getStartTimeImage() {
        return startTimeImage;
    }

    public void setStartTimeImage(int startTimeImage) {
        this.startTimeImage = startTimeImage;
    }
}
