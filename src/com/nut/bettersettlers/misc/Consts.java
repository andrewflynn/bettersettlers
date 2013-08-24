package com.nut.bettersettlers.misc;

import android.os.Build;

public class Consts {
	public static final String SHARED_PREFS_SEAFARERS_KEY = "seafarers";
	
	public static final boolean DEBUG_MAP = false;
	public static final boolean TEST = true;
	//public static final String ANALYTICS_KEY = "UA-22984277-1"; // PROD
	public static final String ANALYTICS_KEY = "UA-24622139-1"; // TEST
	public static final int ANALYTICS_INTERVAL = 10;
	public static final String ANALYTICS_SHUFFLE = "/shuffle";
	public static final String ANALYTICS_SHUFFLE_MAP = ANALYTICS_SHUFFLE + "/map";
	public static final String ANALYTICS_SHUFFLE_PROBABILITIES = ANALYTICS_SHUFFLE + "/probabilities";
	public static final String ANALYTICS_SHUFFLE_HARBORS = ANALYTICS_SHUFFLE + "/harbors";
	public static final String ANALYTICS_USE_PLACEMENTS = "/placements";
	public static final String ANALYTICS_USE_ROLL_TRACKER = "/rollTracker";
	public static final String ANALYTICS_VIEW_SETTLERS = "/settlers";
	public static final String ANALYTICS_VIEW_SEAFARERS = "/seafarers";
	public static final String ANALYTICS_VIEW_MORE = "/more";
	public static final String ANALYTICS_CHANGE_MAP_SIZE_FORMAT = "/size/%s";
	public static final String ANALYTICS_CHANGE_MAP_TYPE_FORMAT = "/type/%s";
	public static final String ANALYTICS_SEAFARERS_PURCHASE_FORMAT = "/seafarersPurchaseView/%s";
	
	public static final String LAUNCH_MAP_ACTION = "com.nut.bettersettlers.action.LAUNCH_MAP";
	
	public static final boolean AT_LEAST_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
}
