package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Steve on 8/26/2015.
 */
public class Related implements Parcelable, PlayItemInterface {
    @SerializedName("id")
    private int relatedId;
    @SerializedName("name")
    private String relatedName;
    @SerializedName("link")
    private String relatedLink;
    @SerializedName("image")
    private String relatedImage;

    protected Related(Parcel in) {
        relatedId = in.readInt();
        relatedName = in.readString();
        relatedLink = in.readString();
        relatedImage = in.readString();
    }

    public static final Creator<Related> CREATOR = new Creator<Related>() {
        @Override
        public Related createFromParcel(Parcel in) {
            return new Related(in);
        }

        @Override
        public Related[] newArray(int size) {
            return new Related[size];
        }
    };

    public int getRelatedId() {
        return relatedId;
    }

    public String getRelatedImage() {
        return relatedImage;
    }

    public String getRelatedLink() {
        return relatedLink;
    }

    public String getRelatedName() {
        return relatedName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(relatedId);
        dest.writeString(relatedName);
        dest.writeString(relatedLink);
        dest.writeString(relatedImage);
    }

    @Override
    public String iGetLinkPlay() {
        return relatedLink;
    }

    @Override
    public String iGetLinkImage() {
        return relatedImage;
    }

    @Override
    public String iGetTitle() {
        return relatedName;
    }

    @Override
    public String iGetDescription() {
        return relatedName;
    }

    @Override
    public int iGetId() {
        return relatedId;
    }
}
