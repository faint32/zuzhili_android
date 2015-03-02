package com.zuzhili.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by liutao on 14-3-4.
 */
public class SortModel implements Serializable, Parcelable {
    protected String sortKey;

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public static final Parcelable.Creator<SortModel> CREATOR = new Creator() {

        @Override
        public SortModel createFromParcel(Parcel source) {
            SortModel s = new SortModel();
            s.setSortKey(source.readString());
            return s;
        }

        @Override
        public SortModel[] newArray(int size) {
            return new SortModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sortKey);
    }
}
