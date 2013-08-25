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
			R.drawable.sea_heading_for_new_shores),
	THE_FOUR_ISLANDS(MapSize.THE_FOUR_ISLANDS,
			MapSize.THE_FOUR_ISLANDS_EXP,
			R.drawable.sea_the_four_islands_button, R.drawable.sea_the_four_islands_bw_button),
	THE_FOG_ISLAND(MapSize.THE_FOG_ISLAND,
			MapSize.THE_FOG_ISLAND_EXP,
			R.drawable.sea_the_fog_island_button, R.drawable.sea_the_fog_island_bw_button),
	THROUGH_THE_DESERT(MapSize.THROUGH_THE_DESERT,
			MapSize.THROUGH_THE_DESERT_EXP,
			R.drawable.sea_through_the_desert_button, R.drawable.sea_through_the_desert_bw_button),
	THE_FORGOTTEN_TRIBE(MapSize.THE_FORGOTTEN_TRIBE,
			MapSize.THE_FORGOTTEN_TRIBE_EXP,
			R.drawable.sea_the_forgotten_tribe_button, R.drawable.sea_the_forgotten_tribe_bw_button),
	CLOTH_FOR_CATAN(MapSize.CLOTH_FOR_CATAN,
			MapSize.CLOTH_FOR_CATAN_EXP,
			R.drawable.sea_cloth_for_catan_button, R.drawable.sea_cloth_for_catan_bw_button),
	THE_PIRATE_ISLANDS(MapSize.THE_PIRATE_ISLANDS,
			MapSize.THE_PIRATE_ISLANDS_EXP,
			R.drawable.sea_the_pirate_islands_button, R.drawable.sea_the_pirate_islands_bw_button),
	THE_WONDERS_OF_CATAN(MapSize.THE_WONDERS_OF_CATAN,
			MapSize.THE_WONDERS_OF_CATAN_EXP,
			R.drawable.sea_the_wonders_of_catan_button, R.drawable.sea_the_wonders_of_catan_bw_button),
	NEW_WORLD(MapSize.NEW_WORLD,
			MapSize.NEW_WORLD_EXP,
			R.drawable.sea_new_world_button, R.drawable.sea_new_world_bw_button);
	
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
