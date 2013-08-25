package com.nut.bettersettlers.data;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.maps.CatanMapProvider;
import com.nut.bettersettlers.data.maps.HeadingForNewShores;
import com.nut.bettersettlers.data.maps.HeadingForNewShoresExp;
import com.nut.bettersettlers.data.maps.Large;
import com.nut.bettersettlers.data.maps.Standard;
import com.nut.bettersettlers.data.maps.XLarge;

public enum MapSize implements Parcelable {
	STANDARD(new Standard(), "standard", R.drawable.title_settlers, "standard"),
	LARGE(new Large(), "large", R.drawable.title_settlers, "large"),
	XLARGE(new XLarge(), "xlarge", R.drawable.title_settlers, "xlarge"),
	
	HEADING_FOR_NEW_SHORES(new HeadingForNewShores(), "heading_for_new_shores",
			R.drawable.title_heading_for_new_shores, "seafarers.heading_for_new_shores"),
	HEADING_FOR_NEW_SHORES_EXP(new HeadingForNewShoresExp(), "heading_for_new_shores_exp",
			R.drawable.title_heading_for_new_shores, null);

	private static final Map<String, MapSize> PRODUCT_ID_MAP = new HashMap<String, MapSize>();
	static {
		for (MapSize size : MapSize.values()) {
			PRODUCT_ID_MAP.put(size.productId, size);
		}
	}
	public static MapSize getMapSizeByProductId(String name) {
		return PRODUCT_ID_MAP.get(name);
	}
	
	public final CatanMapProvider mapProvider;
	public final String name;
	public final int titleDrawableId;
	public final String productId;
	
	private MapSize(CatanMapProvider mapProvider, String name, int titleDrawableId, String productId) {
		this.mapProvider = mapProvider;
		this.name = name;
		this.titleDrawableId = titleDrawableId;
		this.productId = productId;
	}

    public static final Parcelable.Creator<MapSize> CREATOR = new Parcelable.Creator<MapSize>() {
        public MapSize createFromParcel(Parcel in) {
            return MapSize.values()[in.readInt()];
        }

        public MapSize[] newArray(int size) {
            return new MapSize[size];
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
		return name;
	}
}
