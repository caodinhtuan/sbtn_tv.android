package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by onworldtv on 10/28/15.
 */
public class TimeLines implements Parcelable {
    private String title;
    private ArrayList<TimeLine> timeline;

    public ArrayList<TimeLine> getTimeLine() {
        return timeline;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(this.timeline);
    }

    public TimeLines() {
    }

    protected TimeLines(Parcel in) {
        this.title = in.readString();
        this.timeline = in.createTypedArrayList(TimeLine.CREATOR);
    }

    public static final Parcelable.Creator<TimeLines> CREATOR = new Parcelable.Creator<TimeLines>() {
        @Override
        public TimeLines createFromParcel(Parcel source) {
            return new TimeLines(source);
        }

        @Override
        public TimeLines[] newArray(int size) {
            return new TimeLines[size];
        }
    };
}
