package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by onworldtv on 1/28/16.
 */
public class InfoVideoArrayModel implements Parcelable {
    private int id;
    private String name;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    public InfoVideoArrayModel() {
    }

    protected InfoVideoArrayModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<InfoVideoArrayModel> CREATOR = new Parcelable.Creator<InfoVideoArrayModel>() {
        @Override
        public InfoVideoArrayModel createFromParcel(Parcel source) {
            return new InfoVideoArrayModel(source);
        }

        @Override
        public InfoVideoArrayModel[] newArray(int size) {
            return new InfoVideoArrayModel[size];
        }
    };
}
