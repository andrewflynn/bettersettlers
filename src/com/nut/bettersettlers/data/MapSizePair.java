package com.nut.bettersettlers.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.bettersettlers.R;

public enum MapSizePair implements Parcelable {
	STANDARD(MapSize.STANDARD, null, R.drawable.map_standard_button),
	LARGE(MapSize.LARGE, null, R.drawable.map_large_button),
	XLARGE(MapSize.XLARGE, null, R.drawable.map_xlarge_button),
	
	HEADING_FOR_NEW_SHORES(MapSize.HEADING_FOR_NEW_SHORES,
			MapSize.HEADING_FOR_NEW_SHORES_EXP,
			R.drawable.sea_heading_for_new_shores);
	
	public final MapSize reg;
	public final MapSize exp;
	public final int buttonResId;
	public final int bwButtonResId;

	private MapSizePair(MapSize reg, MapSize exp) {
		this(reg, exp, 0, 0);
	}
	private MapSizePair(MapSize reg, MapSize exp, int buttonResId) {
		this(reg, exp, buttonResId, 0);
	}
	private MapSizePair(MapSize reg, MapSize exp, int buttonResId, int bwButtonResId) {
		this.reg = reg;
		this.exp = exp;
		this.buttonResId = buttonResId;
		this.bwButtonResId = bwButtonResId;
	}

    public static final Parcelable.Creator<MapSizePair> CREATOR = new Parcelable.Creator<MapSizePair>() {
        public MapSizePair createFromParcel(Parcel in) {
            return MapSizePair.values()[in.readInt()];
        }

        public MapSizePair[] newArray(int size) {
            return new MapSizePair[size];
        }
    };
	
	@Override
	public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
		out.writeInt(ordinal());
    }
	
	@Override
	public String toString() {
		return name();
	}
}
