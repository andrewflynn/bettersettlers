package com.nut.bettersettlers.misc;

import android.os.Build;

public class Consts {
	public static final String ANALYTICS_KEY = "UA-22984277-1";
	public static final String ANALYTICS_SHUFFLE = "/shuffle";
	public static final String ANALYTICS_SHUFFLE_MAP = ANALYTICS_SHUFFLE + "/map";
	public static final String ANALYTICS_SHUFFLE_PROBABILITIES = ANALYTICS_SHUFFLE + "/probabilities";
	public static final String ANALYTICS_SHUFFLE_HARBORS = ANALYTICS_SHUFFLE + "/harbors";
	public static final String ANALYTICS_USE_PLACEMENTS = "/placements";
	public static final String ANALYTICS_USE_ROLL_TRACKER = "/rollTracker";
	public static final String ANALYTICS_CHANGE_MAP_SIZE_FORMAT = "/size/%s";
	public static final String ANALYTICS_CHANGE_MAP_TYPE_FORMAT = "/type/%s";
	
	public static final boolean AT_LEAST_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
}
