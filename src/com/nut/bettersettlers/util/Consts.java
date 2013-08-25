package com.nut.bettersettlers.util;

public class Consts {
	public static final String SHARED_PREFS_SEAFARERS_KEY = "seafarers";

	public static final boolean TEST = true;
	public static final boolean DEBUG_MAP = false;
	public static final boolean DEBUG_MAP_WITH_RED = false;
	
	public static final String ANALYTICS_KEY = TEST ? "UA-24622139-1" : "UA-22984277-1";
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
	
	public static final int IAB_API_VERSION = 3;
	
	/**
	 * Mapping between the numbers that are shown (rolled on the dice) and the probability of each
	 * being rolled.
	 */
	public static final int[] PROBABILITY_MAPPING = {
		0,  // 0
        0,  // 1
        1,  // 2
        2,  // 3
        3,  // 4
        4,  // 5
        5,  // 6
        0,  // 7
        5,  // 8
        4,  // 9
        3,  // 10
        2,  // 11
        1   // 12
	};
	
	public static final int BOARD_RANGE_X = 22;//19;
	public static final int BOARD_RANGE_HALF_X = 11;//9;
	public static final int BOARD_RANGE_Y = 13;//11;
	public static final int BOARD_RANGE_HALF_Y = 7;//6;
}
