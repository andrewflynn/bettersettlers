package com.nut.bettersettlers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.nut.bettersettlers.R;

public class MapProvider {
	private final Context mContext;
	private final Map<MapSize, CatanMap> maps;
	
	public enum MapSize {
		STANDARD(R.raw.standard, "standard", R.drawable.title_settlers, "standard"),
		LARGE(R.raw.large, "large", R.drawable.title_settlers, "large"),
		XLARGE(R.raw.xlarge, "xlarge", R.drawable.title_settlers, "xlarge"),
		HEADING_FOR_NEW_SHORES(R.raw.heading_for_new_shores, "heading_for_new_shores",
				R.drawable.title_heading_for_new_shores, "seafarers.heading_for_new_shores"),
		HEADING_FOR_NEW_SHORES_EXP(R.raw.heading_for_new_shores_exp, "heading_for_new_shores_exp",
				R.drawable.title_heading_for_new_shores, null);
		
		public final int rawResId;
		public final String name;
		public final int titleDrawableId;
		public final String productId;
		
		MapSize(int rawResId, String name, int titleDrawableId, String productId) {
			this.rawResId = rawResId;
			this.name = name;
			this.titleDrawableId = titleDrawableId;
			this.productId = productId;
		}
	}
	
	public MapProvider(Context context) {
		this(context, null, null);
	}
	
	public MapProvider(Context context, ArrayList<Integer> theftOrder, ArrayList<Integer> expTheftOrder) {
		mContext = context;
		
		maps = new HashMap<MapSize, CatanMap>();
		for (MapSize size : MapSize.values()) {
			maps.put(size, new JsonCatanMap(mContext.getResources().openRawResource(size.rawResId)));
		}
	}
	
	public CatanMap getMap(MapSize size) {
		return maps.get(size);
	}
	
	public CatanMap getMap(String name) {
		for (CatanMap map : maps.values()) {
			if (map.getName().equals(name)) {
				return map;
			}
		}
		return null;
	}
	
	public MapSize getMapSizeByProductId(String name) {
		for (MapSize mapSize : MapSize.values()) {
			if (mapSize.productId.equals(name)) {
				return mapSize;
			}
		}
		return null;
	}
	
	public void refreshMap(MapSize size) {
		maps.put(size, new JsonCatanMap(mContext.getResources().openRawResource(size.rawResId)));
	}
}
