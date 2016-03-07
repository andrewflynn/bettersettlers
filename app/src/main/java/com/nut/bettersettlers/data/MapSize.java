package com.nut.bettersettlers.data;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.bettersettlers.R;
import com.nut.bettersettlers.data.maps.CatanMapProvider;
import com.nut.bettersettlers.data.maps.ClothForCatan;
import com.nut.bettersettlers.data.maps.ClothForCatanExp;
import com.nut.bettersettlers.data.maps.HeadingForNewShores;
import com.nut.bettersettlers.data.maps.HeadingForNewShoresExp;
import com.nut.bettersettlers.data.maps.Large;
import com.nut.bettersettlers.data.maps.NewWorld;
import com.nut.bettersettlers.data.maps.NewWorldExp;
import com.nut.bettersettlers.data.maps.Standard;
import com.nut.bettersettlers.data.maps.TheFogIsland;
import com.nut.bettersettlers.data.maps.TheFogIslandExp;
import com.nut.bettersettlers.data.maps.TheForgottenTribe;
import com.nut.bettersettlers.data.maps.TheForgottenTribeExp;
import com.nut.bettersettlers.data.maps.TheFourIslands;
import com.nut.bettersettlers.data.maps.TheFourIslandsExp;
import com.nut.bettersettlers.data.maps.ThePirateIslands;
import com.nut.bettersettlers.data.maps.ThePirateIslandsExp;
import com.nut.bettersettlers.data.maps.TheWondersOfCatan;
import com.nut.bettersettlers.data.maps.TheWondersOfCatanExp;
import com.nut.bettersettlers.data.maps.ThroughTheDesert;
import com.nut.bettersettlers.data.maps.ThroughTheDesertExp;
import com.nut.bettersettlers.data.maps.XLarge;

public enum MapSize implements Parcelable {
	STANDARD(new Standard(), "standard", R.drawable.title_settlers, "standard"),
	LARGE(new Large(), "large", R.drawable.title_settlers, "large"),
	XLARGE(new XLarge(), "xlarge", R.drawable.title_settlers, "xlarge"),
	
	HEADING_FOR_NEW_SHORES(new HeadingForNewShores(), "heading_for_new_shores",
			R.drawable.title_heading_for_new_shores, "seafarers.heading_for_new_shores"),
	THE_FOUR_ISLANDS(new TheFourIslands(), "the_four_islands",
			R.drawable.title_the_four_islands, "seafarers.the_four_islands"),
	THE_FOG_ISLAND(new TheFogIsland(), "the_fog_island",
			R.drawable.title_the_fog_island, "seafarers.the_fog_island"),
	THROUGH_THE_DESERT(new ThroughTheDesert(), "through_the_desert",
			R.drawable.title_through_the_desert, "seafarers.through_the_desert"),
	THE_FORGOTTEN_TRIBE(new TheForgottenTribe(), "the_forgotten_tribe",
			R.drawable.title_the_forgotten_tribe, "seafarers.the_forgotten_tribe"),
	CLOTH_FOR_CATAN(new ClothForCatan(), "cloth_for_catan",
			R.drawable.title_cloth_for_catan, "seafarers.cloth_for_catan"),
	THE_PIRATE_ISLANDS(new ThePirateIslands(), "the_pirate_islands",
			R.drawable.title_the_pirate_islands, "seafarers.the_pirate_islands"),
	THE_WONDERS_OF_CATAN(new TheWondersOfCatan(), "the_wonders_of_catan",
			R.drawable.title_the_wonders_of_catan, "seafarers.the_wonders_of_catan"),
	NEW_WORLD(new NewWorld(), "new_world", R.drawable.title_new_world, "seafarers.new_world"),

	HEADING_FOR_NEW_SHORES_EXP(new HeadingForNewShoresExp(), "heading_for_new_shores_exp",
			R.drawable.title_heading_for_new_shores, null),
	THE_FOUR_ISLANDS_EXP(new TheFourIslandsExp(), "the_four_islands_exp",
			R.drawable.title_the_four_islands, null),
	THE_FOG_ISLAND_EXP(new TheFogIslandExp(), "the_fog_island_exp",
			R.drawable.title_the_fog_island, null),
	THROUGH_THE_DESERT_EXP(new ThroughTheDesertExp(), "through_the_desert_exp",
			R.drawable.title_through_the_desert, null),
	THE_FORGOTTEN_TRIBE_EXP(new TheForgottenTribeExp(), "the_forgotten_tribe_exp",
			R.drawable.title_the_forgotten_tribe, null),
	CLOTH_FOR_CATAN_EXP(new ClothForCatanExp(), "cloth_for_catan_exp",
			R.drawable.title_cloth_for_catan, null),
	THE_PIRATE_ISLANDS_EXP(new ThePirateIslandsExp(), "the_pirate_islands_exp",
			R.drawable.title_the_pirate_islands, null),
	THE_WONDERS_OF_CATAN_EXP(new TheWondersOfCatanExp(), "the_wonders_of_catan_exp",
			R.drawable.title_the_wonders_of_catan, null),
	NEW_WORLD_EXP(new NewWorldExp(), "new_world_exp", R.drawable.title_new_world, null);

	private static final Map<String, MapSize> PRODUCT_ID_MAP = new HashMap<>();
	static {
		for (MapSize size : MapSize.values()) {
			PRODUCT_ID_MAP.put(size.productId, size);
		}
	}
	public static MapSize getMapSizeByProductId(String id) {
		return PRODUCT_ID_MAP.get(id);
	}
	
	public final CatanMapProvider mapProvider;
	public final String title;
	public final int titleDrawableId;
	public final String productId;
	
	MapSize(CatanMapProvider mapProvider, String title, int titleDrawableId, String productId) {
		this.mapProvider = mapProvider;
		this.title = title;
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
		return title;
	}
}
