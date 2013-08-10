package com.nut.bettersettlers;

import java.io.Serializable;

import android.graphics.Point;

/**
 * 
 * Class that contains many useful numbers for putting together the map of catan.
 * @author Andrew Flynn
 * 
 * TODO(flynn): We maybe don't even need the non-ordered probs
 *
 */
public class MapSpecs {
	/**
	 * The simple class that represents a harbor.  Position is a number
	 * from 0-n for which harbor it is (0 is TL corner and goes Clockwise). 
	 * Note that "desert"==3:1 trading, water=no harbor and resource=2:1
	 * of that resource.  Facing is a variable referring to which tile the
	 * harbor's arms are facing, according to the numbering of the land tiles.
	 */
	public static class Harbor implements Serializable {
		private int position;
		private Resource resource;
		private int facing;
		
		public Harbor(int position, Resource resource, int facing) {
			this.position = position;
			this.resource = resource;
			this.facing = facing;
		}

		public int getPosition() {
			return position;
		}
		public void setPosition(int position) {
			this.position = position;
		}
		public Resource getResource() {
			return resource;
		}
		public void setResource(Resource resource) {
			this.resource = resource;
		}
		public int getFacing() {
			return facing;
		}
		public void setFacing(int facing) {
			this.facing = facing;
		}
	}
	
	protected static enum NumberOfResource {
		DESERT, LOW, HIGH, WATER;
	}

	protected static enum Resource {
		DESERT(0xFFE6C426, NumberOfResource.DESERT),
		WHEAT(0xFFFFFC00, NumberOfResource.HIGH),
		CLAY(0xFF8E1414, NumberOfResource.LOW),
		ROCK(0xFF9A9A9A, NumberOfResource.LOW),
		SHEEP(0xFF00FF1F, NumberOfResource.HIGH),
		WOOD(0xFF085D12, NumberOfResource.HIGH),
		WATER(0xFF0000FF, NumberOfResource.WATER);
		
		private final int color;
		private final NumberOfResource num;
		Resource(int color, NumberOfResource num) {
			this.color = color;
			this.num = num;
		}
		public int getColor() {
			return color;
		}
		public NumberOfResource getNumOfResource() {
			return num;
		}
	}
	
	protected static enum MapType { TRADITIONAL, FAIR, RANDOM; }
	
	protected static enum MapSize {
		STANDARD (MapSpecs.STANDARD_LOW_RESOURCE_NUMBER,
				MapSpecs.STANDARD_HIGH_RESOURCE_NUMBER,
				MapSpecs.STANDARD_LAND_GRID,
				MapSpecs.STANDARD_LAND_GRID_ORDER,
				MapSpecs.STANDARD_WATER_GRID,
				MapSpecs.STANDARD_HARBOR_LINES,
				MapSpecs.STANDARD_LAND_NEIGHBORS,
				MapSpecs.STANDARD_WATER_NEIGHBORS,
				MapSpecs.STANDARD_LAND_INTERSECTIONS,
				MapSpecs.STANDARD_LAND_INTERSECTION_INDEXES,
				MapSpecs.STANDARD_PLACEMENT_INDEXES,
				MapSpecs.STANDARD_AVAILABLE_RESOURCES,
				MapSpecs.STANDARD_AVAILABLE_PROBABILITIES,
				MapSpecs.STANDARD_AVAILABLE_ORDERED_PROBABILITIES,
				MapSpecs.STANDARD_AVAILABLE_HARBORS,
				MapSpecs.STANDARD_ORDERED_HARBORS),
		LARGE (MapSpecs.LARGE_LOW_RESOURCE_NUMBER,
				MapSpecs.LARGE_HIGH_RESOURCE_NUMBER,
				MapSpecs.LARGE_LAND_GRID,
				MapSpecs.LARGE_LAND_GRID_ORDER,
				MapSpecs.LARGE_WATER_GRID,
				MapSpecs.LARGE_HARBOR_LINES,
				MapSpecs.LARGE_LAND_NEIGHBORS,
				MapSpecs.LARGE_WATER_NEIGHBORS,
				MapSpecs.LARGE_LAND_INTERSECTIONS,
				MapSpecs.LARGE_LAND_INTERSECTION_INDEXES,
				MapSpecs.LARGE_PLACEMENT_INDEXES,
				MapSpecs.LARGE_AVAILABLE_RESOURCES,
				MapSpecs.LARGE_AVAILABLE_PROBABILITIES,
				MapSpecs.LARGE_AVAILABLE_ORDERED_PROBABILITIES,
				MapSpecs.LARGE_AVAILABLE_HARBORS,
				MapSpecs.LARGE_ORDERED_HARBORS),
		XLARGE (MapSpecs.XLARGE_LOW_RESOURCE_NUMBER,
				MapSpecs.XLARGE_HIGH_RESOURCE_NUMBER,
				MapSpecs.XLARGE_LAND_GRID,
				MapSpecs.XLARGE_LAND_GRID_ORDER,
				MapSpecs.XLARGE_WATER_GRID,
				MapSpecs.XLARGE_HARBOR_LINES,
				MapSpecs.XLARGE_LAND_NEIGHBORS,
				MapSpecs.XLARGE_WATER_NEIGHBORS,
				MapSpecs.XLARGE_LAND_INTERSECTIONS,
				MapSpecs.XLARGE_LAND_INTERSECTION_INDEXES,
				MapSpecs.XLARGE_PLACEMENT_INDEXES,
				MapSpecs.XLARGE_AVAILABLE_RESOURCES,
				MapSpecs.XLARGE_AVAILABLE_PROBABILITIES,
				MapSpecs.XLARGE_AVAILABLE_ORDERED_PROBABILITIES,
				MapSpecs.XLARGE_AVAILABLE_HARBORS,
				MapSpecs.XLARGE_ORDERED_HARBORS);
		
		private final int lowResourceNumber;
		private final int highResourceNumber;
		private final Point[] landGrid;
		private final int[] landGridOrder;
		private final Point[] waterGrid;
		private final int[][] harborLines;
		private final int[][] landNeighbors;
		private final int[][] waterNeighbors;
		private final int[][] landIntersections;
		private final int[][] landIntersectionIndexes;
		private final int[][] placementIndexes;
		private final Resource[] availableResources;
		private final int[] availableProbabilities;
		private final int[] availableOrderedProbabilities;
		private final Resource[] availableHarbors;
		private final int[] orderedHarbors;
		MapSize(int lrn, int hrn, Point[] lg, int[] lgo, Point[] wg,
				int[][] hl, int[][] ln, int[][] wn,
				int[][] li, int[][] lii, int[][] pi, Resource[] ar,
				int[] ap, int[] aop, Resource[] ah, int[] oh) {
			lowResourceNumber = lrn;
			highResourceNumber = hrn;
			landGrid = lg;
			landGridOrder = lgo;
			waterGrid = wg;
			harborLines = hl;
			landNeighbors = ln;
			waterNeighbors = wn;
			landIntersections = li;
			landIntersectionIndexes = lii;
			placementIndexes = pi;
			availableResources = ar;
			availableOrderedProbabilities = aop;
			availableProbabilities = ap;
			availableHarbors = ah;
			orderedHarbors = oh;
		}
		public int getLowResourceNumber() {
			return lowResourceNumber;
		}
		public int getHighResourceNumber() {
			return highResourceNumber;
		}
		public Point[] getLandGrid() {
			return landGrid;
		}
		public int[] getLandGridOrder() {
			return landGridOrder;
		}
		public Point[] getWaterGrid() {
			return waterGrid;
		}
		public int[][] getHarborLines() {
			return harborLines;
		}
		public int[][] getLandNeighbors() {
			return landNeighbors;
		}
		public int[][] getWaterNeighbors() {
			return waterNeighbors;
		}
		public int[][] getLandIntersections() {
			return landIntersections;
		}
		public int[][] getLandIntersectionIndexes() {
			return landIntersectionIndexes;
		}
		public int[][] getPlacementIndexes() {
			return placementIndexes;
		}
		public Resource[] getAvailableResources() {
			return availableResources;
		}
		public int[] getAvailableProbabilities() {
			return availableProbabilities;
		}
		public int[] getAvailableOrderedProbabilities() {
			return availableOrderedProbabilities;
		}
		public Resource[] getAvailableHarbors() {
			return availableHarbors;
		}
		public int[] getOrderedHarbors() {
			return orderedHarbors;
		}
	}
	
	/**
	 * Mapping between the numbers that are shown (rolled on the dice) and the probability of each
	 * being rolled.
	 */
	protected static final int[] PROBABILITY_MAPPING =
	  { 0,  // 0
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
        1}; // 12
	
	protected static final int BOARD_RANGE_X = 15;
	protected static final int BOARD_RANGE_Y = 9;
	
	/**
	 * STANDARD BOARD (3-4 ppl) How many rocks/clays there are available on to distribute.
	 */
	protected static final int STANDARD_LOW_RESOURCE_NUMBER = 3;

	/**
	 * STANDARD BOARD (3-4 ppl) How many wheats/woods/sheep there are available on to distribute.
	 */
	protected static final int STANDARD_HIGH_RESOURCE_NUMBER = 4;

	/**
	 * STANDARD BOARD (3-4 ppl) List of how many of each resource this type of board contains
	 */
	protected static final Resource[] STANDARD_AVAILABLE_RESOURCES = {
			Resource.SHEEP, Resource.SHEEP,
			Resource.SHEEP, Resource.SHEEP,
			Resource.WHEAT, Resource.WHEAT,
			Resource.WHEAT, Resource.WHEAT,
			Resource.WOOD, Resource.WOOD,
			Resource.WOOD, Resource.WOOD,
			Resource.ROCK, Resource.ROCK,
			Resource.ROCK, Resource.CLAY,
			Resource.CLAY, Resource.CLAY,
			Resource.DESERT };

	/**
	 * STANDARD BOARD (3-4 ppl) List of how many of each probability this type of board contains
	 */
	protected static final int[] STANDARD_AVAILABLE_PROBABILITIES = { 0, 2, 3, 3,
			4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12 };

	/**
	 * STANDARD BOARD (3-4 ppl) List of how many of each probability this type of board contains
	 */
	protected static final int[] STANDARD_AVAILABLE_ORDERED_PROBABILITIES = { 5, 2, 6,
		3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11 };

	/**
	 * STANDARD BOARD (3-4 ppl) List of how many of harbors there are (desert is 3:1)
	 */
	protected static final Resource[] STANDARD_AVAILABLE_HARBORS = {
			Resource.SHEEP, Resource.WHEAT,
			Resource.WOOD, Resource.ROCK,
			Resource.CLAY, Resource.DESERT,
			Resource.DESERT, Resource.DESERT,
			Resource.DESERT };

	/**
	 * STANDARD BOARD (3-4 ppl) The (x,y) pixels of the TL corner of each land hexagon for drawing purposes.
	 */
	protected static final Point[] STANDARD_LAND_GRID = { new Point(4, 2),
			new Point(6, 2), new Point(8, 2), new Point(3, 3), new Point(5, 3),
			new Point(7, 3), new Point(9, 3), new Point(2, 4), new Point(4, 4),
			new Point(6, 4), new Point(8, 4), new Point(10, 4),
			new Point(3, 5), new Point(5, 5), new Point(7, 5), new Point(9, 5),
			new Point(4, 6), new Point(6, 6), new Point(8, 6) };

	/**
	 * STANDARD BOARD (3-4 ppl) The (x,y) pixels of the TL corner of each land hexagon for drawing purposes.
	 */
	protected static final int[] STANDARD_LAND_GRID_ORDER =
	  { 16,17,18,15,11,6,2,1,0,3,7,12,13,14,10,5,4,8,9 };

	/**
	 * STANDARD BOARD (3-4 ppl) The (x,y) pixels of the TL corner of each ocean hexagon for drawing purposes.
	 */
	protected static final Point[] STANDARD_WATER_GRID = { new Point(3, 1),
			new Point(5, 1), new Point(7, 1), new Point(9, 1),
			new Point(10, 2), new Point(11, 3), new Point(12, 4),
			new Point(11, 5), new Point(10, 6), new Point(9, 7),
			new Point(7, 7), new Point(5, 7), new Point(3, 7), new Point(2, 6),
			new Point(1, 5), new Point(0, 4), new Point(1, 3), new Point(2, 2) };

	/**
	 * STANDARD BOARD (3-4 ppl) This list contains 2 or 3 numbers that are the possible corners that the lines can go to (half only touch two at 2 corners and half touch at 3 corners).  0 is the TL corner, 1 is the top corner, 2 is the TR corner, and so forth clockwise around the hexagon to 5 (BL).
	 */
	protected static final int[][] STANDARD_HARBOR_LINES = { { 3, 4 },
			{ 3, 4, 5 }, { 3, 4, 5 }, { 4, 5 }, { 4, 5, 0 }, { 4, 5, 0 },
			{ 5, 0 }, { 5, 0, 1 }, { 5, 0, 1 }, { 0, 1 }, { 0, 1, 2 },
			{ 0, 1, 2 }, { 1, 2 }, { 1, 2, 3 }, { 1, 2, 3 }, { 2, 3 },
			{ 2, 3, 4 }, { 2, 3, 4 } };
	
	protected static final int[] STANDARD_ORDERED_HARBORS = {
		0, // 0
		-1, // 1
		1, // 2
		-1, // 3
		0, // 4
		-1, // 5
		0, // 6
		-1, // 7
		1, // 8
		-1, // 9
		0, // 10
		-1, // 11
		0, // 12
		-1, // 13
		1, // 14
		-1, // 15
		0, // 16
		-1 // 17
	};

	/**
	 * STANDARD BOARD (3-4 ppl) This list of lists contains the land tiles that each land tile is neighbors with.  The land tiles are numbered 0-18 starting at the TL corner and going L -> R, T -> B.
	 */
	protected static final int[][] STANDARD_LAND_NEIGHBORS = { { 1, 3, 4 },
			{ 0, 2, 4, 5 }, { 1, 5, 6 }, { 0, 4, 7, 8 }, { 0, 1, 3, 5, 8, 9 },
			{ 1, 2, 4, 6, 9, 10 }, { 2, 5, 10, 11 }, { 3, 8, 12 },
			{ 3, 4, 7, 9, 12, 13 }, { 4, 5, 8, 10, 13, 14 },
			{ 5, 6, 9, 11, 14, 15 }, { 6, 10, 15 }, { 7, 8, 13, 16 },
			{ 8, 9, 12, 14, 16, 17 }, { 9, 10, 13, 15, 17, 18 },
			{ 10, 11, 14, 18 }, { 12, 13, 17 }, { 13, 14, 16, 18 },
			{ 14, 15, 17 } };

	/**
	 * STANDARD BOARD (3-4 ppl) This list of lists contains the land tiles that each ocean tile is neighbors with.  The ocean tiles are (similar to everywhere else) numbered starting at the TL ocean tile and going around clockwise 0-17.  The land tiles are (similar to everywhere else) numbered 0-18 starting at the TL land tile and going L -> R, T -> B.  Note that the numbers are not small to large but are numbered clockwise by whichever land tile is earliest on in the clockwise rotation (starting in the TL corner).
	 */
	protected static final int[][] STANDARD_WATER_NEIGHBORS = { { 0 },
			{ 1, 0 }, { 2, 1 }, { 2 }, { 6, 2 }, { 11, 6 }, { 11 }, { 15, 11 },
			{ 18, 15 }, { 18 }, { 17, 18 }, { 16, 17 }, { 16 }, { 12, 16 },
			{ 7, 12 }, { 7 }, { 3, 7 }, { 0, 3 } };

	/**
	 * STANDARD BOARD (3-4 ppl) "Triplets" are defined as three terrain tiles that come together at an intersection (ports do not count). These are ordered starting in the TL corner going L -> R, T -> B (going straight across such that the top three terrain tiles are the "top two" for the first triplets. These are defined so that we can make sure no single settlement placement is too amazing.
	 */
	protected static final int[][] STANDARD_LAND_INTERSECTIONS = {
		{ 0, 1, 4 }, // 0
    { 1, 2, 5 }, // 1
    { 0, 3, 4 }, // 2
    { 1, 4, 5 }, // 3
    { 2, 5, 6 }, // 4
    { 3, 4, 8 }, // 5
		{ 4, 5, 9 }, // 6
		{ 5, 6, 10 }, // 7
		{ 3, 7, 8 }, // 8
		{ 4, 8, 9 }, // 9
		{ 5, 9, 10 }, // 10
		{ 6, 10, 11 }, // 11
		{ 7, 8, 12 }, // 12
		{ 8, 9, 13 }, // 13
		{ 9, 10, 14 }, // 14
		{ 10, 11, 15 }, // 15
		{ 8, 12, 13 }, // 16
		{ 9, 13, 14 }, // 17
		{ 10, 14, 15 }, // 18
		{ 12, 13, 16 }, // 19
		{ 13, 14, 17 },  // 20
		{ 14, 15, 18 }, // 21
		{ 13, 16, 17 }, // 22
		{ 14, 17, 18 }, // 23
		{ 0 }, // 24
		{ 0, 1}, // 25
		{ 1, 2 }, // 26
		{ 2 }, // 27
		{ 2, 6 }, // 28
		{ 6, 11 }, // 29
		{ 11 }, // 30
		{ 11, 15 }, // 31
		{ 15, 18 }, // 32
		{ 18 }, // 33
		{ 17, 18 }, // 34
		{ 16, 17 }, // 35
		{ 16 }, // 36
		{ 12, 16 }, // 37
		{ 7, 12 }, // 38
		{ 7 }, // 39
		{ 3, 7 }, // 40
		{ 0, 3 } }; // 41
	
	protected static final int[][] STANDARD_PLACEMENT_INDEXES = {
		{ 0, 3 }, // 0
		{ 1, 3 }, // 1
		
		{ 0, 4 }, // 2
		{ 1, 4 }, // 3
		{ 2, 4 }, // 4
		
		{ 3, 3 }, // 5
		{ 4, 3 }, // 6
		{ 5, 3 }, // 7
		
		{ 3, 4 }, // 8
		{ 4, 4 }, // 9
		{ 5, 4 }, // 10
		{ 6, 4 }, // 11
		
		{ 7, 3 }, // 12
		{ 8, 3 }, // 13
		{ 9, 3 }, // 14
		{ 10, 3 }, // 15
		
		{ 12, 2 }, // 16
		{ 13, 2 }, // 17
		{ 14, 2 }, // 18
		
		{ 12, 3 }, // 19
		{ 13, 3 }, // 20
		{ 14, 3 }, // 21
		
		{ 16, 2 }, // 22
		{ 17, 2 }, // 23
		{}, // 24
		{ 0, 2 }, // 25
		{ 1, 2 }, // 26
		{}, // 27
		{ 2, 3 }, // 28
		{ 6, 3 }, // 29
		{}, // 30
		{ 11, 4 }, // 31
		{ 15, 4 }, // 32
		{}, // 33
		{ 17, 3 }, // 34
		{ 16, 3 }, // 35
		{}, // 36
		{ 12, 4 }, // 37
		{ 7, 4 }, // 38
		{}, // 39
		{ 3, 5 }, // 40
		{ 0, 5 }, // 41
	};
	
	/**
	 * STANDARD BOARD (3-4 ppl) This is a mapping between STANDARD_LAND_GRID and STANDARD_LAND_INTERSECTIONS
	 */
	protected static final int[][] STANDARD_LAND_INTERSECTION_INDEXES = {
		{ 0, 2, 24, 25, 41 }, // 0
		{ 0, 1, 3, 25, 26 }, // 1
		{ 1, 4, 26, 27, 28 }, // 2
		{ 2, 5, 8, 40, 41 }, // 3
		{	0, 2, 3, 5, 6, 9 }, // 4
		{	1, 3, 4, 6, 7, 10 }, // 5
		{ 4, 7, 11, 28, 29 }, // 6
		{ 8, 12, 38, 39, 40 }, // 7
		{ 5, 8, 9, 12, 13, 16 }, // 8
		{ 6, 9, 10, 13, 14, 17 }, // 9
		{ 7, 10, 11, 14, 15, 18 },	 // 10
		{ 11, 15, 29, 30, 31 },	 // 11
		{ 12, 16, 19, 37, 38 },	// 12
		{ 13, 16, 17, 19, 20, 22 }, // 13
		{ 14, 17, 18, 20, 21, 23 }, // 14
		{ 15, 18, 21, 31, 32}, // 15
		{ 19, 22, 35, 36, 37 }, // 16
		{ 20, 22, 23, 34, 35 }, // 17
		{ 21, 23, 32, 33, 34 }, // 18
	};

	/**
	 * LARGE BOARD (5 ppl)
	 * How many rocks/clays there are available on to distribute.
	 */
	protected static final int LARGE_LOW_RESOURCE_NUMBER = 4;
	
	/**
	 * LARGE BOARD (5 ppl)
	 * How many wheats/woods/sheep there are available on to distribute.
	 */
	protected static final int LARGE_HIGH_RESOURCE_NUMBER = 5;
	
	/**
	 * LARGE BOARD (5 ppl)
	 * List of how many of each probability this type of board contains
	 */
	protected static final int[] LARGE_AVAILABLE_PROBABILITIES =
	{ 0,2,3, 3,4,4, 5,5,6, 6,8,8, 8,9,9, 9,10,10, 10,11,11, 11,12,12 };
	
	/**
	 * LARGE BOARD (5 ppl)
	 * List of how many of each probability this type of board contains
	 */
	protected static final int[] LARGE_AVAILABLE_ORDERED_PROBABILITIES =
	{ 2,5,4, 6,3,9, 8,11,11, 10,6,3, 8,4,8, 10,11,12, 10,5,4, 9,5 };
	
	/**
	 * LARGE BOARD (5 ppl)
	 * List of how many of each resource this type of board contains
	 */
	protected static final Resource[] LARGE_AVAILABLE_RESOURCES =
	{
		Resource.SHEEP,  // 5 Sheep
		Resource.SHEEP,
		Resource.SHEEP,
		Resource.SHEEP,
		Resource.SHEEP,
		Resource.WHEAT,  // 5 Wheat
		Resource.WHEAT,
		Resource.WHEAT,
		Resource.WHEAT,
		Resource.WHEAT,
		Resource.WOOD,   // 5 Wood
		Resource.WOOD,
		Resource.WOOD,
		Resource.WOOD,
		Resource.WOOD,
		Resource.ROCK,   // 4 Rock
		Resource.ROCK,
		Resource.ROCK,
		Resource.ROCK,
		Resource.CLAY,   // 4 Clay
		Resource.CLAY,
		Resource.CLAY,
		Resource.CLAY,
		Resource.DESERT, // 1 Desert
	};
	
	/**
	 * LARGE BOARD (5 ppl)
	 * List of how many of harbors there are (desert is 3:1)
	 */
	protected static final Resource[] LARGE_AVAILABLE_HARBORS =
	{
		Resource.SHEEP,  // 1 Sheep 2:1
		Resource.WHEAT,  // 1 Wheat 2:1
		Resource.WOOD,   // 1 Wood 2:1
		Resource.ROCK,   // 1 Rock 2:1
		Resource.CLAY,   // 1 Clay 2:1
		Resource.DESERT, // 5 3:1's
		Resource.DESERT,
		Resource.DESERT,
		Resource.DESERT,
		Resource.DESERT,
	};
	
	/**
	 * LARGE BOARD (5 ppl)
	 * The (x,y) pixels of the TL corner of each land hexagon for drawing purposes.
	 */
	protected static final Point[] LARGE_LAND_GRID = 
	{
		new Point(4,2),  // 0
		new Point(6,2),  // 1
		new Point(8,2),  // 2
		new Point(10,2), // 3
		new Point(3,3),  // 4
		new Point(5,3),  // 5
		new Point(7,3),  // 6
		new Point(9,3),  // 7
		new Point(11,3), // 8
		new Point(2,4),  // 9
		new Point(4,4),  // 10
		new Point(6,4),  // 11
		new Point(8,4),  // 12
		new Point(10,4), // 13
		new Point(12,4), // 14
		new Point(3,5),  // 15
		new Point(5,5),  // 16
		new Point(7,5),  // 17
		new Point(9,5),  // 18
		new Point(11,5), // 19
		new Point(4,6),  // 20
		new Point(6,6),  // 21
		new Point(8,6),  // 22
		new Point(10,6)  // 23
    };

	/**
	 * STANDARD BOARD (3-4 ppl) The (x,y) pixels of the TL corner of each land hexagon for drawing purposes.
	 */
	protected static final int[] LARGE_LAND_GRID_ORDER =
	  { 20,21,22, 23,19,14, 8,3,2, 1,0,4, 9,15,16, 17,18,13, 7,6,5, 10,11,12 };
	
	/**
	 * LARGE BOARD (5 ppl)
	 * The (x,y) pixels of the TL corner of each ocean hexagon for drawing purposes.
	 */
	protected static final Point[] LARGE_WATER_GRID =
	{
		new Point(3,1),  // 0
		new Point(5,1),  // 1
		new Point(7,1),  // 2
		new Point(9,1),  // 3
		new Point(11,1), // 4
		new Point(12,2), // 5
		new Point(13,3), // 6
		new Point(14,4), // 7
		new Point(13,5), // 8
		new Point(12,6), // 9
		new Point(11,7), // 10
		new Point(9,7),  // 11
		new Point(7,7),  // 12
		new Point(5,7),  // 13
		new Point(3,7),  // 14
		new Point(2,6),  // 15
		new Point(1,5),  // 16
		new Point(0,4),  // 17
		new Point(1,3),  // 18
		new Point(2,2),  // 19
	};

	/**
	 * LARGE BOARD (5 ppl)
	 * This list contains 2 or 3 numbers that are the possible corners that
	 * the lines can go to (half only touch two at 2 corners and half touch at 3 corners).  0 is the TL corner,
	 * 1 is the top corner, 2 is the TR corner, and so forth clockwise around the hexagon to 5 (BL).
	 */
	protected static final int[][] LARGE_HARBOR_LINES =
	{
		   {3,4},   // 0
           {3,4,5}, // 1
           {3,4,5}, // 2
           {3,4,5}, // 3
           {4,5},   // 4
           {4,5,0}, // 5
           {4,5,0}, // 6
           {5,0},   // 7
           {5,0,1}, // 8
           {5,0,1}, // 9
           {0,1},   // 10
           {0,1,2}, // 11
           {0,1,2}, // 12
           {0,1,2}, // 13
           {1,2},   // 14
           {1,2,3}, // 15
           {1,2,3}, // 16
           {2,3},   // 17
           {2,3,4}, // 18
           {2,3,4}, // 19
	};
	
	protected static final int[] LARGE_ORDERED_HARBORS = {
		0, // 0
		-1, // 1
		1, // 2
		-1, // 3
		0, // 4
		-1, // 5
		1, // 6
		-1, // 7
		0, // 8
		-1, // 9
		0, // 10
		-1, // 11
		1, // 12
		-1, // 13
		0, // 14
		-1, // 15
		1, // 16
		-1, // 17
		0, // 18
		-1 // 19
	};
	
	/**
	 * LARGE BOARD (5 ppl)
	 * This list of lists contains the land tiles that each land tile is neighbors with.  The land tiles are
	 * numbered 0-23 starting at the TL corner and going L -> R, T -> B.
	 */
	protected static final int[][] LARGE_LAND_NEIGHBORS =
	{
		{1,4,5},
		{0,2,5,6},
		{1,3,6,7},
		{2,7,8},
		{0,5,9,10},
		{0,1,4,6,10,11},
		{1,2,5,7,11,12},
		{2,3,6,8,12,13},
		{3,7,13,14},
		{4,10,15},
		{4,5,9,11,15,16},
		{5,6,10,12,16,17},
		{6,7,11,13,17,18},
		{7,8,12,14,18,19},
		{8,13,19},
		{9,10,16,20},
		{10,11,15,17,20,21},
		{11,12,16,18,21,22},
		{12,13,17,19,22,23},
		{13,14,18,23},
		{15,16,21},
		{16,17,20,22},
		{17,18,21,23},
		{18,19,22},
	};
	
	/**
	 * LARGE BOARD (5 ppl)
	 * This list of lists contains the land tiles that each ocean tile is neighbors with.  The ocean tiles are
	 * (similar to everywhere else) numbered starting at the TL ocean tile and going around clockwise 0-17.  The
	 * land tiles are (similar to everywhere else) numbered 0-18 starting at the TL land tile and going
	 * L -> R, T -> B.  Note that the numbers are not small to large but are numbered clockwise by whichever land
	 * tile is earliest on in the clockwise rotation (starting in the TL corner).
	 */
	protected static final int[][] LARGE_WATER_NEIGHBORS =
	{
		    {0},     // 0
	        {1,0},   // 1
	        {2,1},   // 2
	        {3,2},   // 3
	        {3},     // 4
	        {8,3},   // 5
	        {14,8},  // 6
	        {14},    // 7
	        {19,14}, // 8
	        {23,19}, // 9
	        {23},    // 10
	        {22,23}, // 11
	        {21,22}, // 12
	        {20,21}, // 13
	        {20},    // 14
	        {15,20}, // 15
	        {9,15},  // 16
	        {9},     // 17
	        {4,9},   // 18
	        {0,4},   // 19
	 };
	
	/**
	 * LARGE BOARD (5 ppl)
	 * "Triplets" are defined as three terrain tiles that come together at an intersection (ports do not count).
	 * These are ordered starting in the TL corner going L -> R, T -> B (going straight across such that the top
	 * three terrain tiles are the "top two" for the first triplets.
	 * These are defined so that we can make sure no single settlement placement is too amazing.
	 */
	protected static final int[][] LARGE_LAND_INTERSECTIONS =
		{
		  {0,1,5}, // 0
		  {1,2,6}, // 1
		  {2,3,7}, // 2
		  
		  {0,4,5}, // 3
		  {1,5,6}, // 4
		  {2,6,7}, // 5
		  {3,7,8}, // 6
		  
		  {4,5,10}, // 7
		  {5,6,11}, // 8
		  {6,7,12}, // 9
		  {7,8,13}, // 10
		  
		  {4,9,10}, // 11
		  {5,10,11}, // 12
		  {6,11,12}, // 13
		  {7,12,13}, // 14
		  {8,13,14}, // 15
		 
		  {9,10,15}, // 16
		  {10,11,16}, // 17
		  {11,12,17}, // 18
		  {12,13,18}, // 19
		  {13,14,19}, // 20
		  
		  {10,15,16}, // 21
		  {11,16,17}, // 22
		  {12,17,18}, // 23
		  {13,18,19}, // 24
		  
		  {15,16,20}, // 25
		  {16,17,21}, // 26
		  {17,18,22}, // 27
		  {18,19,23}, // 28
		  
		  {16,20,21}, // 29
		  {17,21,22}, // 30
		  {18,22,23}, // 31
		  
		  {0},        // 32
		  {0,1},      // 33
		  {1,2},      // 34
		  {2,3},      // 35
		  
		  {3},        // 36
		  {3,8},      // 37
		  {8,14},     // 38

		  {14},       // 39
		  {14,19},    // 40
		  {19,23},    // 41
		  
		  {23},       // 42
		  {22,23},    // 43
		  {21,22},    // 44
		  {20,21},    // 45
		  
		  {20},       // 46
		  {15,20},    // 47
		  {9,15},     // 48
		  
		  {9},        // 49
		  {4,9},      // 50
		  {0,4}       // 51
		};
	
	protected static final int[][] LARGE_PLACEMENT_INDEXES = {
		{ 0, 3 }, // 0
		{ 1, 3 }, // 1
		{ 2, 3 }, // 2
		
		{ 0, 4 }, // 3
		{ 1, 4 }, // 4
		{ 2, 4 }, // 5
		{ 3, 4 }, // 6
		
		{ 4, 3 }, // 7
		{ 5, 3 }, // 8
		{ 6, 3 }, // 9
		{ 7, 3 }, // 10
		
		{ 4, 4 }, // 11
		{ 5, 4 }, // 12
		{ 6, 4 }, // 13
		{ 7, 4 }, // 14
		{ 8, 4 }, // 15
		
		{ 9, 3 }, // 16
		{ 10, 3 }, // 17
		{ 11, 3 }, // 18
		{ 12, 3 }, // 19
		{ 13, 3 }, // 20
		
		{ 10, 4 }, // 21
		{ 11, 4 }, // 22
		{ 12, 4 }, // 23
		{ 13, 4 }, // 24
		
		{ 15, 3 }, // 25
		{ 16, 3 }, // 26
		{ 17, 3 }, // 27
		{ 18, 3 }, // 28
		
		{ 16, 4 }, // 29
		{ 17, 4 }, // 30
		{ 18, 4 }, // 31
		
		{}, // 32
		{ 0, 2 }, // 33
		{ 1, 2 }, // 34
		{ 2, 2 }, // 35
		{}, // 36
		{ 8, 1 }, // 37
		{ 8, 3 }, // 38
		{}, // 39
		{ 19, 2 }, // 40
		{ 19, 4 }, // 41
		{}, // 42
		{ 23, 5 }, // 43
		{ 22, 5 }, // 44
		{ 21, 5 }, // 45
		{}, // 46
		{ 15, 4 }, // 47
		{ 15, 0 }, // 48
		{}, // 49
		{ 4, 5 }, // 50
		{ 4, 1 }, // 51
	};
	
	/**
	 * LARGE BOARD (5 ppl) This is a mapping between LARGE_LAND_GRID and LARGE_LAND_INTERSECTIONS
	 */
	protected static final int[][] LARGE_LAND_INTERSECTION_INDEXES = {
		{ 0, 3, 32, 33, 51 }, // 0
		{ 0, 1, 4, 33, 34 }, // 1
		{ 1, 2, 5, 34, 35 }, // 2
		{ 2, 6, 35, 36, 37 }, // 3
		{ 3, 7, 11, 50, 51 }, // 4
		{ 0, 3, 4, 7, 8, 12 }, // 5
		{ 1, 4, 5, 8, 9, 13 }, // 6
		{ 2, 5, 6, 9, 10, 14 }, // 7
		{ 6, 10, 15, 37, 38 }, // 8
		{ 11, 16, 48, 49, 50 }, // 9
		{ 7, 11, 12, 16, 17, 21 }, // 10
		{ 8, 12, 13, 17, 18, 22 }, // 11
		{ 9, 13, 14, 18, 19, 23 }, // 12
		{ 10, 14, 15, 19, 20, 24 }, // 13
		{ 15, 20, 38, 39, 40 }, // 14
		{ 16, 21, 25, 47, 48 }, // 15
		{ 17, 21, 22, 25, 26, 29 }, // 16
		{ 18, 22, 23, 26, 27, 30 }, // 17
		{ 19, 23, 24, 27, 28, 31 }, // 18
		{ 20, 24, 28, 40, 41 }, // 19
		{ 25, 29, 45, 46, 47 }, // 20
		{ 26, 29, 30, 44, 45 }, // 21
		{ 27, 30, 31, 43, 44 }, // 22
		{ 28, 31, 41, 42, 43 } // 23
	};

	/**
	 * XLARGE BOARD (6 ppl) How many rocks/clays there are available on to distribute.
	 */
	protected static final int XLARGE_LOW_RESOURCE_NUMBER = 5;

	/**
	 * XLARGE BOARD (6 ppl) How many wheats/woods/sheep there are available on to distribute.
	 */
	protected static final int XLARGE_HIGH_RESOURCE_NUMBER = 6;

	/**
	 * XLARGE BOARD (6 ppl) List of how many of each probability this type of board contains
	 */
	protected static final int[] XLARGE_AVAILABLE_PROBABILITIES =
	{ 0,0,2,2,3,3,3,4,4,4,5,5,5,6,6,6,8,8,8,9,9,9,10,10,10,11,11,11,12,12 };

	/**
	 * LARGE BOARD (5 ppl)
	 * List of how many of each probability this type of board contains
	 */
	protected static final int[] XLARGE_AVAILABLE_ORDERED_PROBABILITIES =
	{ 2,5,4,6,3,9,8,11,11,10,6,3,8,4,8,10,11,12,10,5,4,9,5,9,12,3,2,6 };

	/**
	 * XLARGE BOARD (6 ppl) List of how many of each resource this type of board contains
	 */
	protected static final Resource[] XLARGE_AVAILABLE_RESOURCES =
	{
			Resource.SHEEP, // 6 Sheep
			Resource.SHEEP,
			Resource.SHEEP,
			Resource.SHEEP,
			Resource.SHEEP,
			Resource.SHEEP,
			Resource.WHEAT, // 6 Wheat
			Resource.WHEAT,
			Resource.WHEAT,
			Resource.WHEAT,
			Resource.WHEAT,
			Resource.WHEAT,
			Resource.WOOD,  // 6 Wood
			Resource.WOOD,
			Resource.WOOD,
			Resource.WOOD,
			Resource.WOOD,
			Resource.WOOD,
			Resource.ROCK,  // 5 Rock
			Resource.ROCK,
			Resource.ROCK,
			Resource.ROCK,
			Resource.ROCK,
			Resource.CLAY,  // 5 Clay
			Resource.CLAY,
			Resource.CLAY,
			Resource.CLAY,
			Resource.CLAY,
			Resource.DESERT, // 2 Desert
			Resource.DESERT
	};

	/**
	 * XLARGE BOARD (6 ppl) List of how many of harbors there are (desert is 3:1)
	 */
	protected static final Resource[] XLARGE_AVAILABLE_HARBORS = {
			Resource.SHEEP,  // 2 Sheep 2:1's
			Resource.SHEEP,
			Resource.WHEAT,  // 1 Wheat 2:1
			Resource.WOOD,   // 1 Wood 2:1
			Resource.ROCK,   // 1 Rock 2:1
			Resource.CLAY,   // 1 Clay 2:1
			Resource.DESERT, // 5 3:1's
			Resource.DESERT,
			Resource.DESERT,
			Resource.DESERT,
			Resource.DESERT };

	/**
	 * XLARGE BOARD (6 ppl) The (x,y) pixels of the TL corner of each land hexagon for drawing purposes.
	 */
	protected static final Point[] XLARGE_LAND_GRID =
	{
		new Point(5,1),  // 0
		new Point(7,1),  // 1
		new Point(9,1),  // 2
		new Point(4,2),  // 3
		new Point(6,2),  // 4
		new Point(8,2),  // 5
		new Point(10,2), // 6
		new Point(3,3),  // 7
		new Point(5,3),  // 8
		new Point(7,3),  // 9
		new Point(9,3),  // 10
		new Point(11,3), // 11
		new Point(2,4),  // 12
		new Point(4,4),  // 13
		new Point(6,4),  // 14
		new Point(8,4),  // 15
		new Point(10,4), // 16
		new Point(12,4), // 17
		new Point(3,5),  // 18
		new Point(5,5),  // 19
		new Point(7,5),  // 20
		new Point(9,5),  // 21
		new Point(11,5), // 22
		new Point(4,6),  // 23
		new Point(6,6),  // 24
		new Point(8,6),  // 25
		new Point(10,6), // 26
		new Point(5,7),  // 27
		new Point(7,7),  // 28
		new Point(9,7)   // 29
	};

	/**
	 * STANDARD BOARD (3-4 ppl) The (x,y) pixels of the TL corner of each land hexagon for drawing purposes.
	 */
	protected static final int[] XLARGE_LAND_GRID_ORDER =
	{ 27,28,29, 26,22,17, 11,6,2, 1,0,3, 7,12,18, 23,24,25, 21,16,10, 5,4,8, 13,19,20, 15,9,14 };

	/**
	 * XLARGE BOARD (6 ppl) The (x,y) pixels of the TL corner of each ocean hexagon for drawing purposes.
	 */
	protected static final Point[] XLARGE_WATER_GRID =
	{
		new Point(4,0),  // 0
		new Point(6,0),  // 1
		new Point(8,0),  // 2
		new Point(10,0), // 3
		new Point(11,1), // 4
		new Point(12,2), // 5
		new Point(13,3), // 6
		new Point(14,4), // 7
		new Point(13,5), // 8
		new Point(12,6), // 9
		new Point(11,7), // 10
		new Point(10,8), // 11
		new Point(8,8),  // 12
		new Point(6,8),  // 13
		new Point(4,8),  // 14
		new Point(3,7),  // 15
		new Point(2,6),  // 16
		new Point(1,5),  // 17
		new Point(0,4),  // 18
		new Point(1,3),  // 19
		new Point(2,2),  // 20	
		new Point(3,1),  // 21	
	};

	/**
	 * XLARGE BOARD (6 ppl) This list contains 2 or 3 numbers that are the possible corners that the lines can go to (half only touch two at 2 corners and half touch at 3 corners).  0 is the TL corner, 1 is the top corner, 2 is the TR corner, and so forth clockwise around the hexagon to 5 (BL).
	 */
	protected static final int[][] XLARGE_HARBOR_LINES =
	{
		   {3,4},   // 0
           {3,4,5}, // 1
           {3,4,5}, // 2
           {4,5},   // 3
           {4,5,0}, // 4
           {4,5,0}, // 5
           {4,5,0}, // 6
           {5,0},   // 7
           {5,0,1}, // 8
           {5,0,1}, // 9
           {5,0,1}, // 10
           {0,1},   // 11
           {0,1,2}, // 12
           {0,1,2}, // 13
           {1,2},   // 14
           {1,2,3}, // 15
           {1,2,3}, // 16
           {1,2,3}, // 17
           {2,3},   // 18
           {2,3,4}, // 19
           {2,3,4}, // 20
           {2,3,4}, // 21
	};
	
	protected static final int[] XLARGE_ORDERED_HARBORS = {
		0, // 0
		-1, // 1
		1, // 2
		-1, // 3
		0, // 4
		-1, // 5
		-1, // 6
		0, // 7
		-1, // 8
		1, // 9
		0, // 10
		-1, // 11
		0, // 12
		-1, // 13
		0, // 14
		-1, // 15
		1, // 16
		0, // 17
		-1, // 18
		0, // 19
		-1, // 20
		-1 // 21
	};

	/**
	 * XLARGE BOARD (6 ppl) This list of lists contains the land tiles that each land tile is neighbors with.  The land tiles are numbered 0-23 starting at the TL corner and going L -> R, T -> B.
	 */
	protected static final int[][] XLARGE_LAND_NEIGHBORS =
	{
		{1,3,4},
		{0,2,4,5},
		{1,5,6},
		
		{0,4,7,8},
		{0,1,3,5,8,9},
		{1,2,4,6,9,10},
		{2,5,10,11},
		
		{3,8,12,13},
		{3,4,7,9,13,14},
		{4,5,8,10,14,15},
		{5,6,9,11,15,16},
		{6,10,16,17},
		
		{7,13,18},
		{7,8,12,14,18,19},
		{8,9,13,15,19,20},
		{9,10,14,16,20,21},
		{10,11,15,17,21,22},
		{11,16,22},
		
		{12,13,19,23},
		{13,14,18,20,23,24},
		{14,15,19,21,24,25},
		{15,16,20,22,25,26},
		{16,17,21,26},
		
		{18,19,24,27},
		{19,20,23,25,27,28},
		{20,21,24,26,28,29},
		{21,22,25,29},
		
		{23,24,28},
		{24,25,27,29},
		{25,26,28}
	};

	/**
	 * XLARGE BOARD (6 ppl) This list of lists contains the land tiles that each ocean tile is neighbors with.  The ocean tiles are (similar to everywhere else) numbered starting at the TL ocean tile and going around clockwise 0-17.  The land tiles are (similar to everywhere else) numbered 0-18 starting at the TL land tile and going L -> R, T -> B.  Note that the numbers are not small to large but are numbered clockwise by whichever land tile is earliest on in the clockwise rotation (starting in the TL corner).
	 */
	protected static final int[][] XLARGE_WATER_NEIGHBORS =
	{
		{0},
		{1,0},
		{2,1},
		{2},
		{6,2},
		{11,6},
		{17,11},
		{17},
		{22,17},
		{26,22},
		{29,26},
		{29},
		{28,29},
		{27,28},
		{27},
		{23,27},
		{18,23},
		{12,18},
		{12},
		{7,12},
		{3,7},
		{0,3}
	};

	/**
	 * XLARGE BOARD (6 ppl) "Triplets" are defined as three terrain tiles that come together at an intersection (ports do not count). These are ordered starting in the TL corner going L -> R, T -> B (going straight across such that the top three terrain tiles are the "top two" for the first triplets. These are defined so that we can make sure no single settlement placement is too amazing.
	 * 2,3,3,4,4,5,5,4,4,3,3,2
	 */
	protected static final int[][] XLARGE_LAND_INTERSECTIONS =
	{
		{0,1,4}, // 0
		{1,2,5}, // 1
		
		{0,3,4}, // 2
		{1,4,5}, // 3
		{2,5,6}, // 4
		
		{3,4,8}, // 5
		{4,5,9}, // 6
		{5,6,10}, // 7
		
		{3,7,8}, // 8
		{4,8,9}, // 9
		{5,9,10}, // 10
		{6,10,11}, // 11
		
		{7,8,13}, // 12
		{8,9,14}, // 13
		{9,10,15}, // 14
		{10,11,16}, // 15
		
		{7,12,13}, // 16
		{8,13,14}, // 17
		{9,14,15}, // 18
		{10,15,16}, // 19
		{11,16,17}, // 20
		
		{12,13,18}, // 21
		{13,14,19}, // 22
		{14,15,20}, // 23
		{15,16,21}, // 24
		{16,17,22}, // 25
		
		{13,18,19}, // 26
		{14,19,20}, // 27
		{15,20,21}, // 28
		{16,21,22}, // 29
		
		{18,19,23}, // 30
		{19,20,24}, // 31
		{20,21,25}, // 32
		{21,22,26}, // 33
		
		{19,23,24}, // 34
		{20,24,25}, // 35
		{21,25,26}, // 36
		
		{23,24,27}, // 37
		{24,25,28}, // 38
		{25,26,29}, // 39
		
		{24,27,28}, // 40
		{25,28,29}, // 41
		
		{0},        // 42
		{0,1},      // 43
		{1,2},      // 44
		
		{2},        // 45
		{2,6},      // 46
		{6,11},     // 47
		{11,17},    // 48
		
		{17},       // 49
		{17,22},    // 50
		{22,26},    // 51
		{26,29},    // 52
		
		{29},       // 53
		{28,29},    // 54
		{27,28},    // 55

		{27},       // 56
		{23,27},    // 57
		{18,23},    // 58
		{12,18},    // 59

		{12},       // 60
		{7,12},     // 61
		{3,7},      // 62
		{0,3}       // 63
		
	};
	
	/**
	 * XLARGE BOARD (6 ppl) This is a mapping between XLARGE_LAND_GRID and XLARGE_LAND_INTERSECTIONS
	 * 
	 * TODO(flynn): fill in
	 */
	protected static final int[][] XLARGE_LAND_INTERSECTION_INDEXES = {
		{ 0, 2, 42, 43, 63 }, // 0
		{ 0, 1, 3, 43, 44 }, // 1
		{ 1, 4, 44, 45, 46 }, // 2
		{ 2, 5, 8, 62, 63 }, // 3
		{ 0, 2, 3, 5, 6, 9 }, // 4
		{ 1, 3, 4, 6, 7, 10 }, // 5
		{ 4, 7, 11, 46, 47 }, // 6
		{ 8, 12, 16, 61, 62 }, // 7
		{ 5, 8, 9, 12, 13, 17 }, // 8
		{ 6, 9, 10, 13, 14, 18 }, // 9
		{ 7, 10, 11, 14, 15, 19 }, // 10
		{ 11, 15, 20, 47, 48 }, // 11
		{ 16, 21, 59, 60, 61 }, // 12
		{ 12, 16, 17, 21, 22, 26 }, // 13
		{ 13, 17, 18, 22, 23, 27 }, // 14
		{ 14, 18, 19, 23, 24, 28 }, // 15
		{ 15, 19, 20, 24, 25, 29 }, // 16
		{ 20, 25, 48, 49, 50 }, // 17
		{ 21, 26, 30, 58, 59 }, // 18
		{ 22, 26, 27, 30, 31, 34 }, // 19
		{ 23, 27, 28, 31, 32, 35 }, // 20
		{ 24, 28, 29, 32, 33, 36 }, // 21
		{ 25, 29, 33, 50, 51 }, // 22
		{ 30, 34, 37, 57, 58 }, // 23
		{ 31, 34, 35, 37, 38, 40 }, // 24
		{ 32, 35, 36, 38, 39, 41 }, // 25
		{ 33, 36, 39, 51, 52 }, // 26
		{ 37, 40, 55, 56, 57 }, // 27
		{ 38, 40, 41, 54, 55 }, // 28
		{ 39, 41, 52, 53, 54 } // 29
	};
	
	protected static final int[][] XLARGE_PLACEMENT_INDEXES = {
		{ 0, 3 }, // 0
		{ 1, 3 }, // 1
		
		{ 0, 4 }, // 2
		{ 1, 4 }, // 3
		{ 2, 4 }, // 4
		
		{ 3, 3 }, // 5
		{ 4, 3 }, // 6
		{ 5, 3 }, // 7
		
		{ 3, 4 }, // 8
		{ 4, 4 }, // 9
		{ 5, 4 }, // 10
		{ 6, 4 }, // 11
		
		{ 7, 3 }, // 12
		{ 8, 3 }, // 13
		{ 9, 3 }, // 14
		{ 10, 3 }, // 15
		
		{ 7, 4 }, // 16
		{ 8, 4 }, // 17
		{ 9, 4 }, // 18
		{ 10, 4 }, // 19
		{ 11, 4 }, // 20

		{ 12, 3 }, // 21
		{ 13, 3 }, // 22
		{ 14, 3 }, // 23
		{ 15, 3 }, // 24
		{ 16, 3 }, // 25

		{ 13, 4 }, // 26
		{ 14, 4 }, // 27
		{ 15, 4 }, // 28
		{ 16, 4 }, // 29

		{ 18, 3 }, // 30
		{ 19, 3 }, // 31
		{ 20, 3 }, // 32
		{ 21, 3 }, // 33

		{ 19, 4 }, // 34
		{ 20, 4 }, // 35
		{ 21, 4 }, // 36

		{ 23, 3 }, // 37
		{ 24, 3 }, // 38
		{ 25, 3 }, // 39

		{ 24, 4 }, // 40
		{ 25, 4 }, // 41
		
		{}, // 42
		{ 0, 2 }, // 43
		{ 1, 2 }, // 44
		{}, // 45
		{ 2, 3 }, // 46
		{ 6, 3 }, // 47
		{ 11, 3 }, // 48
		{}, // 49
		{ 17, 4 }, // 50
		{ 22, 4 }, // 51
		{ 26, 4 }, // 52
		{}, // 53
		{ 29, 5 }, // 54
		{ 28, 5 }, // 55
		{}, // 56
		{ 27, 0 }, // 57
		{ 23, 0 }, // 58
		{ 18, 0 }, // 59
		{}, // 60
		{ 7, 5 }, // 61
		{ 3, 5 }, // 62
		{ 0, 5 }, // 63
	};
}
