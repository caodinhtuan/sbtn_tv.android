package com.sbtn.androidtv.request.datacallback;

import com.sbtn.androidtv.datamodels.SBTNPackageGroup;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 5/24/16.
 */
public class ListPackageDataCallBackRequest extends BaseDataCallbackRequest {
    private ArrayList<SBTNPackageGroup> data;

    public ArrayList<SBTNPackageGroup> getData() {
        return data;
    }

//    public static class SBTNPackageGroup implements Parcelable {
//        private String group_name;
//        private int max_viewer;
//        private ArrayList<SBTNPackage> items;
//        private String description;
//
//        public String getGroupName() {
//            return group_name;
//        }
//
//        public int getMaxViewer() {
//            return max_viewer;
//        }
//
//        public ArrayList<SBTNPackage> getItems() {
//            return items;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            dest.writeString(this.group_name);
//            dest.writeInt(this.max_viewer);
//            dest.writeTypedList(this.items);
//            dest.writeString(this.description);
//        }
//
//        public SBTNPackageGroup() {
//        }
//
//        protected SBTNPackageGroup(Parcel in) {
//            this.group_name = in.readString();
//            this.max_viewer = in.readInt();
//            this.items = in.createTypedArrayList(SBTNPackage.CREATOR);
//            this.description = in.readString();
//        }
//
//        public static final Parcelable.Creator<SBTNPackageGroup> CREATOR = new Parcelable.Creator<SBTNPackageGroup>() {
//            @Override
//            public SBTNPackageGroup createFromParcel(Parcel source) {
//                return new SBTNPackageGroup(source);
//            }
//
//            @Override
//            public SBTNPackageGroup[] newArray(int size) {
//                return new SBTNPackageGroup[size];
//            }
//        };
//    }


}
