package com.nut.bettersettlers.data;

import android.graphics.Point;

import com.nut.bettersettlers.data.MapSpecs.Resource;

/**
 * TODO: Move these to JSON data files
 * TODO: Some of these can probably be de-duped.
 */
public interface SettlersMap {
	/** How many rocks/clays there are available on to distribute. */
	public int getLowResourceNumber();
	
	/** How many wheats/woods/sheep there are available on to distribute. */
	public int getHighResourceNumber();
	
	/** The (x,y) coordinates of the each land hexagon. */
	public Point[] getLandGrid();
	
	/** The order in which the land grid is laid out. */
	public int[] getLandGridOrder();
	
	/** The (x,y) coordinates of each water hexagon. */
	public Point[] getWaterGrid();
	
	/**
	 * This list contains 2 or 3 numbers that are the possible corners that the lines can go to
	 * (half only touch two at 2 corners and half touch at 3 corners).  0 is the TL corner, 1
	 * is the top corner, 2 is the TR corner, and so forth clockwise around the hexagon to 5 (BL).
	 */
	public int[][] getHarborLines();
	
	/**
	 * This list of lists contains the land tiles that each land tile is neighbors with.
	 * The land tiles are numbered 0-18 starting at the TL corner and going L -> R, T -> B.
	 */
	public int[][] getLandNeighbors();
	
	/**
	 * This list of lists contains the land tiles that each ocean tile is neighbors with.
	 * The ocean tiles are (similar to everywhere else) numbered starting at the TL ocean
	 * tile and going around clockwise 0-17.  The land tiles are (similar to everywhere else)
	 * numbered 0-18 starting at the TL land tile and going L -> R, T -> B.
	 * Note that the numbers are not small to large but are numbered clockwise by whichever land
	 * tile is earliest on in the clockwise rotation (starting in the TL corner).
	 */
	public int[][] getWaterNeighbors();
	
	/**
	 * "Triplets" are defined as three terrain tiles that come together at an intersection
	 * (ports do not count). These are ordered starting in the TL corner going L -> R, T -> B
	 * (going straight across such that the top three terrain tiles are the "top two" for the
	 * first triplets. These are defined so that we can make sure no single settlement placement
	 * is too amazing.
	 */
	public int[][] getLandIntersections();
	
	/** This is a mapping between STANDARD_LAND_GRID and STANDARD_LAND_INTERSECTIONS. */
	public int[][] getLandIntersectionIndexes();
	
	/** This is a way to identify places in between the hexes. This is not a unique mapping, but
	 * the X represents which hex its referring to and the Y represents which direction off of
	 * the hex its pointing.
	 */
	public int[][] getPlacementIndexes();
	
	/** List of how many of each resource this type of board contains. */
	public Resource[] getAvailableResources();
	
	/** List of how many of each probability this type of board contains. */
	public int[] getAvailableProbabilities();
	
	/** List of how many of each probability this type of board contains. */
	public int[] getAvailableOrderedProbabilities();
	
	/** List of how many of harbors there are (desert is 3:1). */
	public Resource[] getAvailableHarbors();
	
	/** Hardcoded directions of harbors on "traditional" maps.  Hardcoded since they're weird. */
	public int[] getOrderedHarbors();
}