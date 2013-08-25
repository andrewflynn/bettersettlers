package com.nut.bettersettlers.data;

public final class MapType {
	private MapType() {}
	public static final int TRADITIONAL = 0;
	public static final int FAIR = 1;
	public static final int RANDOM = 2;
	
	public static String getString(int mapType) {
		switch (mapType) {
		case TRADITIONAL:
			return "TRADITIONAL";
		case FAIR:
			return "FAIR";
		case RANDOM:
			return "RANDOM";
		default:
			return "UNKNOWN";
		}
	}
}
