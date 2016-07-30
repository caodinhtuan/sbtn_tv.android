package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by onworldtv on 10/28/15.
 */
public class TimeLine implements Parcelable, PlayItemInterface {
    private String name;
    private String image;
    private String link;
    private String start;
    private String end;
    private int id;

    //hoa nguyen add
    private transient String linkTemp;
    private transient boolean isLiveTV = false;

    protected TimeLine(Parcel in) {
        name = in.readString();
        image = in.readString();
        link = in.readString();
        start = in.readString();
        end = in.readString();
        id = in.readInt();
    }

    public static final Creator<TimeLine> CREATOR = new Creator<TimeLine>() {
        @Override
        public TimeLine createFromParcel(Parcel in) {
            return new TimeLine(in);
        }

        @Override
        public TimeLine[] newArray(int size) {
            return new TimeLine[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getEnd() {
        return end;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getStart() {
        return start;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(link);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeInt(id);
    }

    @Override
    public String iGetLinkPlay() {
        return !TextUtils.isEmpty(link) ? link : linkTemp;
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
        return name;
    }

    @Override
    public int iGetId() {
        return id;
    }


    public void setLinkTemp(String linkTemp) {
        this.linkTemp = linkTemp;
    }

    public boolean isLiveTV() {
        return isLiveTV;
    }

    public void setLiveTV(boolean liveTV) {
        isLiveTV = liveTV;
    }
}
