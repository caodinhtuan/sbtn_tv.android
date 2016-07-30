package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steve on 8/26/2015.
 */
public class ContentModel implements PlayItemInterface, Parcelable {
    public static final int MODE_VIDEO = 1;
    public static final int MODE_AUDIO = 2;
    private String link;
    private String name;
    private boolean isLive;
    private int id;
    private int mode;//mode = 1: video; mode = 2: audio
    private long duration;
    private int karaoke;
    private String image;
    private int type;//type = 0: single (like live tv)
    //type = 1: serries ( like phim)
    //type == 2: episode ( episode phim)
    private String description;
    private String year;


    public boolean getIsLive() {
        return isLive;
    }

    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public long getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }

    public int getMode() {
        return mode;
    }

    public int getId() {
        return id;
    }

    public int getKaraoke() {
        return karaoke;
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
        return description;
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
        dest.writeString(this.link);
        dest.writeString(this.name);
        dest.writeByte(this.isLive ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeInt(this.mode);
        dest.writeLong(this.duration);
        dest.writeInt(this.karaoke);
        dest.writeString(this.image);
        dest.writeInt(this.type);
        dest.writeString(this.description);
        dest.writeString(this.year);
    }

    public ContentModel() {
    }

    protected ContentModel(Parcel in) {
        this.link = in.readString();
        this.name = in.readString();
        this.isLive = in.readByte() != 0;
        this.id = in.readInt();
        this.mode = in.readInt();
        this.duration = in.readLong();
        this.karaoke = in.readInt();
        this.image = in.readString();
        this.type = in.readInt();
        this.description = in.readString();
        this.year = in.readString();
    }

    public static final Parcelable.Creator<ContentModel> CREATOR = new Parcelable.Creator<ContentModel>() {
        @Override
        public ContentModel createFromParcel(Parcel source) {
            return new ContentModel(source);
        }

        @Override
        public ContentModel[] newArray(int size) {
            return new ContentModel[size];
        }
    };
}

