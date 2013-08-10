package com.nut.bettersettlers.data;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.nut.bettersettlers.R;

public class MapProvider {
	private final Map<MapSize, CatanMap> maps;
	
	public enum MapSize {
		STANDARD(R.raw.standard, "standard"),
		LARGE(R.raw.large, "large"),
		XLARGE(R.raw.xlarge, "xlarge"),
		HEADING_FOR_NEW_SHORES(R.raw.heading_for_new_shores, "heading_for_new_shores");
		
		public final int rawResId;
		public final String name;
		
		MapSize(int rawResId, String name) {
			this.rawResId = rawResId;
			this.name = name;
		}
	}
	
	public MapProvider(Context context) {
		maps = new HashMap<MapSize, CatanMap>();
		for (MapSize size : MapSize.values()) {
			maps.put(size, new JsonCatanMap(context.getResources().openRawResource(size.rawResId)));
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
}
