package com.nut.bettersettlers.iab;

import java.util.HashMap;
import java.util.Map;

import com.nut.bettersettlers.data.MapSizePair;

public enum MapContainer {
	HEADING_FOR_NEW_SHORES("Heading for New Shores", "seafarers.heading_for_new_shores",
			MapSizePair.HEADING_FOR_NEW_SHORES),
	THE_FOUR_ISLANDS("The Four Islands", "seafarers.the_four_islands",
			MapSizePair.THE_FOUR_ISLANDS),
	THE_FOG_ISLAND("The Fog Island", "seafarers.the_fog_island",
			MapSizePair.THE_FOG_ISLAND),
	THROUGH_THE_DESERT("Through the Desert", "seafarers.through_the_desert",
			MapSizePair.THROUGH_THE_DESERT),
	THE_FORGOTTEN_TRIBE("The Forgotten Tribe", "seafarers.the_forgotten_tribe",
			MapSizePair.THE_FORGOTTEN_TRIBE),
	CLOTH_FOR_CATAN("Cloth for Catan", "seafarers.cloth_for_catan",
			MapSizePair.CLOTH_FOR_CATAN),
	THE_PIRATE_ISLANDS("The Pirate Islands", "seafarers.the_pirate_islands",
			MapSizePair.THE_PIRATE_ISLANDS),
	THE_WONDERS_OF_CATAN("The Wonders of Catan", "seafarers.the_wonders_of_catan",
			MapSizePair.THE_WONDERS_OF_CATAN),
	NEW_WORLD("New World", "seafarers.new_world",
			MapSizePair.NEW_WORLD);
	
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
