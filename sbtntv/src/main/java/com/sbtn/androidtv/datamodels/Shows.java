package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Steve on 9/6/2015.
 */
public class Shows implements Serializable, Parcelable {


    public static final Creator<Shows> CREATOR = new Creator<Shows>() {
        @Override
        public Shows createFromParcel(Parcel in) {
            return new Shows(in);
        }

        @Override
        public Shows[] newArray(int size) {
            return new Shows[size];
        }
    };
    ArrayList<DataDetailItem> items;
    @SerializedName("id")
    private int showsId;
    @SerializedName("name")
    private String showsName;
    private int mode;
    private int karaoke;

    protected Shows(Parcel in) {
        items = in.createTypedArrayList(DataDetailItem.CREATOR);
        showsId = in.readInt();
        showsName = in.readString();
        mode = in.readInt();
        karaoke = in.readInt();
    }

    public Shows() {

    }

    public int getKaraoke() {
        return karaoke;
    }

    public int getMode() {
        return mode;
    }

    public int getShowsId() {
        return showsId;
    }

    public ArrayList<DataDetailItem> getShowsDetails() {
        return items;
    }

    public String getShowsName() {
        return showsName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(items);
        parcel.writeInt(showsId);
        parcel.writeString(showsName);
        parcel.writeInt(mode);
        parcel.writeInt(karaoke);
    }
}
