package com.nut.bettersettlers.iab;

import java.util.HashMap;
import java.util.Map;

import com.nut.bettersettlers.data.MapSizePair;

public enum MapContainer {
	HEADING_FOR_NEW_SHORES("Heading for New Shores", "seafarers.heading_for_new_shores",
			MapSizePair.HEADING_FOR_NEW_SHORES),;
	
	private static final Map<String, MapContainer> ID_MAP = new HashMap<String, MapContainer>();
	static {
		for (MapContainer map : MapContainer.values()) {
			ID_MAP.put(map.id, map);
		}
	}
	public static MapContainer getMapById(String id) {
		return ID_MAP.get(id);
	}

	public final String title;
	public final String id;
	public final MapSizePair sizePair;
	
	private MapContainer(String title, String id, MapSizePair sizePair) {
		this.title = title;
		this.id = id;
		this.sizePair = sizePair;
	}
}
