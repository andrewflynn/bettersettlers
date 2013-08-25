package com.nut.bettersettlers.iab;

import java.util.HashMap;
import java.util.Map;

import com.nut.bettersettlers.data.MapSizePair;

public enum MapContainer {
	HEADING_FOR_NEW_SHORES("Heading for New Shores", "seafarers.heading_for_new_shores",
			MapSizePair.HEADING_FOR_NEW_SHORES);
	
	private static final Map<String, MapContainer> ID_MAP = new HashMap<String, MapContainer>();
	static {
		for (MapContainer map : MapContainer.values()) {
			ID_MAP.put(map.id, map);
		}
	}
	public static MapContainer getMapById(String id) {
		return ID_MAP.get(id);
	}
	
	public final String id;
	public final String name;
	public final MapSizePair sizePair;
	
	private MapContainer(String id, String name, MapSizePair sizePair) {
		this.id = id;
		this.name = name;
		this.sizePair = sizePair;
	}
}
