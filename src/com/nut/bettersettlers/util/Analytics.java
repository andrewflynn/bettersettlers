package com.nut.bettersettlers.util;

public final class Analytics {
	private Analytics() {}
	
	public static final String ID = "UA-40397310-1";

	public static final String CATEGORY_MAIN = "main";
	public static final String CATEGORY_MAPS_MENU = "maps_menu";
	public static final String CATEGORY_MENU_MENU = "menu_menu";
	public static final String CATEGORY_SETTLERS_MENU = "seafarers_menu";
	public static final String CATEGORY_SEAFARERS_MENU = "seafarers_menu";
	public static final String CATEGORY_EXPANSION_MENU = "expansion_menu";
	public static final String CATEGORY_ABOUT_MENU = "about_menu";
	public static final String CATEGORY_ROLL_TRACKER = "roll_tracker";

	public static final String ACTION_BUTTON = "button";
	public static final String ACTION_LONG_PRESS_BUTTON = "long_press_button";
	public static final String ACTION_PURCHASE_OFFER = "purchase_offer";
	
	public static final String INFO = "info";
	public static final String RATE_US = "rate_us";

	public static final String SETTINGS = "settings";
	public static final String USE_PLACEMENTS = "placements";
	public static final String NEXT_PLACEMENTS = "next";
	public static final String PREV_PLACEMENTS = "prev";
	public static final String SHUFFLE_MAP = "shuffle_map";
	
	public static final String SEE_SETTLERS = "settlers";
	public static final String SEE_SEAFARERS = "seafarers";
	public static final String SEE_MORE = "more";

	public static final String USE_ROLL_TRACKER = "roll_tracker";
	public static final String SHUFFLE_PROBABILITIES = "shuffle_probabilities";
	public static final String SHUFFLE_HARBORS = "shuffle_harbors";

	public static final String EXPANSION_SMALL = "expansion_small";
	public static final String EXPANSION_LARGE = "expansion_large";

	public static final String DELETE = "delete";

	// Arg 1: MapSize
	// Arg 2: MapType
	public static final String VIEW_MAP_FORMAT = "/map/%s/%s";
	public static final String VIEW_ROLL_TRACKER = "/roll_tracker";
	public static final String VIEW_INFO = "/info";
	public static final String VIEW_MAPS_MENU = "/main_menu";
	public static final String VIEW_EXPANSIONS_MENU = "/expansions_menu";
	public static final String VIEW_MORE_MENU = "/more_menu";
	public static final String VIEW_SETTLERS_MENU = "/settlers_menu";
	public static final String VIEW_SEAFARERS_MENU = "/seafarers_menu";
}
