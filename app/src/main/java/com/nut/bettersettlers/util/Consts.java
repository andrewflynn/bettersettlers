package com.nut.bettersettlers.util;

public final class Consts {
	private Consts() {}

	///////////////////////////////////////////
	// All of these should be false for release
	public static final boolean TEST_STATIC_IAB = false;
	public static final boolean TEST_CONSUME_ALL_PURCHASES = false;
	
	public static final boolean DEBUG_MAP = false;
	public static final boolean DEBUG_MAP_WITH_RED = false;
    //
	///////////////////////////////////////////
	
	public static final String LAUNCH_MAP_ACTION = "com.nut.bettersettlers.action.LAUNCH_MAP";
	public static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.nut.bettersettlers";

	public static final String SHARED_PREFS_NAME = "Preferences";
	public static final String SHARED_PREFS_KEY_WHATS_NEW_HELP = "ShownWhatsNewVersion21";
	public static final String SHARED_PREFS_KEY_FOG_ISLAND_HELP = "TheFogIsland";
	
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
