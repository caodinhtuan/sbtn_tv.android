package com.sbtn.androidtv.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/27/16.
 */
public class SBTNPackage implements Parcelable {
    private int promotion;
    private int pk_id;
    private int pkd_id;
    private int pk_type;
    private int gpk_id;
    private String price;
    private String duration;
    private String description;
    public String name;
    private String product_id;
    private boolean is_buy;
    private int max_player;
    private String image;
    @SerializedName("package")
    private ArrayList<SBTNPackage> packageExtras;

    public int getPromotion() {
        return promotion;
    }

    public int getId() {
        return pk_id;
    }

    public int getPkd_id() {
        return pkd_id;
    }

    public int getType() {
        return pk_type;
    }

    public int getGpk_id() {
        return gpk_id;
    }

    public String getPrice() {
        return price + "$";
    }

    public String getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public boolean isPromotion() {
        return promotion > 0;
    }

    public boolean isBuy() {
        return is_buy;
    }

    public int getMaxPlayer() {
        return max_player;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<SBTNPackage> getPackageExtras() {
        return packageExtras;
    }

    @Override
    public String toString() {
        return getName() + " - " + getProduct_id() + " - " + getPrice() + " - " + getDescription();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.promotion);
        dest.writeInt(this.pk_id);
        dest.writeInt(this.pkd_id);
        dest.writeInt(this.pk_type);
        dest.writeInt(this.gpk_id);
        dest.writeString(this.price);
        dest.writeString(this.duration);
        dest.writeString(this.description);
        dest.writeString(this.name);
        dest.writeString(this.product_id);
        dest.writeInt(this.is_buy ? 1 : 0);
        dest.writeInt(this.max_player);
        dest.writeString(this.image);
//        dest.writeList(packageExtras);
    }

    public SBTNPackage() {
    }

    protected SBTNPackage(Parcel in) {
        this.promotion = in.readInt();
        this.pk_id = in.readInt();
        this.pkd_id = in.readInt();
        this.pk_type = in.readInt();
        this.gpk_id = in.readInt();
        this.price = in.readString();
        this.duration = in.readString();
        this.description = in.readString();
        this.name = in.readString();
        this.product_id = in.readString();
        int bulBuy = in.readInt();
        this.is_buy = bulBuy > 0;
        this.max_player = in.readInt();
        this.image = in.readString();
//        in.readList(packageExtras, null);
//        if (this.packageExtras == null)
//            this.packageExtras = new ArrayList<>();
//        in.readTypedList(this.packageExtras, SBTNPackage.CREATOR);
    }

    public static final Parcelable.Creator<SBTNPackage> CREATOR = new Parcelable.Creator<SBTNPackage>() {
        @Override
        public SBTNPackage createFromParcel(Parcel source) {
            return new SBTNPackage(source);
        }

        @Override
        public SBTNPackage[] newArray(int size) {
            return new SBTNPackage[size];
        }
    };
}
