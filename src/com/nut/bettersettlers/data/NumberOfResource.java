package com.nut.bettersettlers.data;

public final class NumberOfResource {
	private NumberOfResource() {}
	public static final int DESERT = 0;
	public static final int LOW = 1;
	public static final int HIGH = 2;
	public static final int WATER = 3;
	public static final int GOLD = 4;
	
	public static String getString(int numberOfResource) {
		switch (numberOfResource) {
		case DESERT:
			return "DESERT";
		case LOW:
			return "LOW";
		case HIGH:
			return "HIGH";
		case WATER:
			return "WATER";
		case GOLD:
			return "GOLD";
		default:
			return "UNKNOWN";
		}
	}
}
