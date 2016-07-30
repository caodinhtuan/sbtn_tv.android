package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 */
public class DataDetailItem implements Serializable, Parcelable, PlayItemInterface {
    public static final int PACKAGE_TYPE_FREE = 0;
    private int id = 0;
    private String name = null;
    private String description = null;
    private String image = null;
    private String category = null;
    private int karaoke = 0;
    private int package_type;

    //hoa nguyen add on Apr 28 2016

    private transient String link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKaraoke() {
        return karaoke;
    }

    public void setKaraoke(int karaoke) {
        this.karaoke = karaoke;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String img) {
        this.image = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String pLink) {
        link = pLink;
    }

    public void updateImageUrl(String newImageSize) {
        if (TextUtils.isEmpty(this.image) == false) {
            this.image = this.image.replace("wxh", newImageSize);
        }
    }

    public int getPackageType() {
        return package_type;
    }

    public boolean isContentFree() {
        return PACKAGE_TYPE_FREE == package_type;
    }

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
        return name;
    }

    @Override
    public String iGetDescription() {
        return category;
    }

    @Override
    public int iGetId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeString(this.category);
        dest.writeInt(this.karaoke);
        dest.writeInt(this.package_type);
    }

    public DataDetailItem() {
    }

    protected DataDetailItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.category = in.readString();
        this.karaoke = in.readInt();
        this.package_type = in.readInt();
    }

    public static final Creator<DataDetailItem> CREATOR = new Creator<DataDetailItem>() {
        @Override
        public DataDetailItem createFromParcel(Parcel source) {
            return new DataDetailItem(source);
        }

        @Override
        public DataDetailItem[] newArray(int size) {
            return new DataDetailItem[size];
        }
    };
}
