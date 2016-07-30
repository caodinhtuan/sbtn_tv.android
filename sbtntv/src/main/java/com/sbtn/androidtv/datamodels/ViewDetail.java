package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Steve on 8/26/2015.
 */
public class ViewDetail implements Parcelable {
    private static final int PERMISSION_OK = 1;
    private ArrayList<Advertisement> adv;
    private int error = 0;
    private String message;
    private ContentModel content;
    private int permission = 0;
    private String perm_reason;
    @SerializedName("package")
    private ArrayList<SBTNPackageGroup> itemSBTNPackageGroup;
    private ArrayList<InfoVideoArrayModel> genres;
    private ArrayList<InfoVideoArrayModel> countries;
    private ArrayList<InfoVideoArrayModel> tags;
    private ArrayList<InfoVideoArrayModel> directors;
    private ArrayList<InfoVideoArrayModel> actors;
    private ArrayList<Episode> episodes;
    private ArrayList<Related> related;
    private ArrayList<TimeLines> timelines;

    @SerializedName("permission_type")
    private int permissionType;
    @SerializedName("view_count")
    private int numberUserView;

    public String getMessage() {
        return message;
    }

    public int getError() {
        return error;
    }

    public ArrayList<TimeLines> getTimelines() {
        return timelines;
    }

    public ArrayList<Advertisement> getAdvertisement() {
        return adv;
    }

    public ContentModel getContent() {
        return content;
    }


    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public ArrayList<Related> getRelated() {
        return related;
    }

    public ArrayList<InfoVideoArrayModel> getCountries() {
        return countries;
    }

    public ArrayList<InfoVideoArrayModel> getGenres() {
        return genres;
    }

    public ArrayList<InfoVideoArrayModel> getTags() {
        return tags;
    }

    public ArrayList<InfoVideoArrayModel> getDirectors() {
        return directors;
    }

    public ArrayList<InfoVideoArrayModel> getActors() {
        return actors;
    }

    public boolean isPermissionOK() {
        return permission == PERMISSION_OK;
    }

    public ArrayList<SBTNPackageGroup> getItemSBTNPackageGroup() {
        return itemSBTNPackageGroup;
    }

    @Override
    public String toString() {
        if (content != null) {
            return "ViewDetail: " + content.getName() + "_" + content.getId() + "_" + content.getImage();
        } else {
            return super.toString() + " content = null";
        }

    }

    public int getNumberUserView() {
        return numberUserView;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public boolean isPermisionFailMaxDevicePlay() {
        //permissionType == 1 là bị max device
        return !isPermissionOK() && permissionType == 1 && numberUserView > 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.adv);
        dest.writeInt(this.error);
        dest.writeString(this.message);
        dest.writeParcelable(this.content, flags);
        dest.writeInt(this.permission);
        dest.writeString(this.perm_reason);
        dest.writeTypedList(this.itemSBTNPackageGroup);
        dest.writeTypedList(this.genres);
        dest.writeTypedList(this.countries);
        dest.writeTypedList(this.tags);
        dest.writeTypedList(this.directors);
        dest.writeTypedList(this.actors);
        dest.writeTypedList(this.episodes);
        dest.writeTypedList(this.related);
        dest.writeTypedList(this.timelines);
        dest.writeInt(this.permissionType);
        dest.writeInt(this.numberUserView);
    }

    public ViewDetail() {
    }

    protected ViewDetail(Parcel in) {
        this.adv = in.createTypedArrayList(Advertisement.CREATOR);
        this.error = in.readInt();
        this.message = in.readString();
        this.content = in.readParcelable(ContentModel.class.getClassLoader());
        this.permission = in.readInt();
        this.perm_reason = in.readString();
        this.itemSBTNPackageGroup = in.createTypedArrayList(SBTNPackageGroup.CREATOR);
        this.genres = in.createTypedArrayList(InfoVideoArrayModel.CREATOR);
        this.countries = in.createTypedArrayList(InfoVideoArrayModel.CREATOR);
        this.tags = in.createTypedArrayList(InfoVideoArrayModel.CREATOR);
        this.directors = in.createTypedArrayList(InfoVideoArrayModel.CREATOR);
        this.actors = in.createTypedArrayList(InfoVideoArrayModel.CREATOR);
        this.episodes = in.createTypedArrayList(Episode.CREATOR);
        this.related = in.createTypedArrayList(Related.CREATOR);
        this.timelines = in.createTypedArrayList(TimeLines.CREATOR);
        this.permissionType = in.readInt();
        this.numberUserView = in.readInt();
    }

    public static final Parcelable.Creator<ViewDetail> CREATOR = new Parcelable.Creator<ViewDetail>() {
        @Override
        public ViewDetail createFromParcel(Parcel source) {
            return new ViewDetail(source);
        }

        @Override
        public ViewDetail[] newArray(int size) {
            return new ViewDetail[size];
        }
    };
}
