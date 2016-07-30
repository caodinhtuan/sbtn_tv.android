package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by onworldtv on 9/12/15.
 */
public class Advertisement implements Parcelable {
    private String link;
    private String type;
    @SerializedName("start")
    private int startTime;
    @SerializedName("duration")
    private int durationTime;
    @SerializedName("skip")
    private int skipTime;
    @SerializedName("skippable_time")
    private long skippableTime;

    public int getDurationTime() {
        return durationTime;
    }

    public long getSkippableTime() {
        return skippableTime;
    }

    public int getSkipTime() {
        return skipTime;
    }

    public String getLink() {
        return link;
    }

    public int getStartTime() {
        return startTime;
    }

    public String getType() {
        return type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.link);
        dest.writeString(this.type);
        dest.writeInt(this.startTime);
        dest.writeInt(this.durationTime);
        dest.writeInt(this.skipTime);
        dest.writeLong(this.skippableTime);
    }

    public Advertisement() {
    }

    protected Advertisement(Parcel in) {
        this.link = in.readString();
        this.type = in.readString();
        this.startTime = in.readInt();
        this.durationTime = in.readInt();
        this.skipTime = in.readInt();
        this.skippableTime = in.readLong();
    }

    public static final Parcelable.Creator<Advertisement> CREATOR = new Parcelable.Creator<Advertisement>() {
        @Override
        public Advertisement createFromParcel(Parcel source) {
            return new Advertisement(source);
        }

        @Override
        public Advertisement[] newArray(int size) {
            return new Advertisement[size];
        }
    };
}
