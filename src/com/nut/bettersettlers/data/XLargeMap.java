package com.nut.bettersettlers.data;

import android.graphics.Point;

import com.nut.bettersettlers.data.MapSpecs.Resource;

public class XLargeMap implements SettlersMap {
	@Override
	public int getLowResourceNumber() {
		return 5;
	}

	@Override
	public int getHighResourceNumber() {
		return 6;
	}

	@Override
	public Point[] getLandGrid() {
		return new Point[] {
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
	}

	@Override
	public int[] getLandGridOrder() {
		return new int[] { 27,28,29, 26,22,17, 11,6,2, 1,0,3, 7,12,18, 23,24,25, 21,16,10, 5,4,8, 13,19,20, 15,9,14 };
	}

	@Override
	public Point[] getWaterGrid() {
		return new Point[] {
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
	}

	@Override
	public int[][] getHarborLines() {
		return new int[][] {
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
	}

	@Override
	public int[][] getLandNeighbors() {
		return new int[][] {
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
	}

	@Override
	public int[][] getWaterNeighbors() {
		return new int[][] {
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
	}

	@Override
	public int[][] getLandIntersections() {
		return new int[][] {
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
	}

	@Override
	public int[][] getLandIntersectionIndexes() {
		return new int[][] {
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
	}

	@Override
	public int[][] getPlacementIndexes() {
		return new int[][] {
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

	@Override
	public Resource[] getAvailableResources() {
		return new Resource[] {
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
	}

	@Override
	public int[] getAvailableProbabilities() {
		return new int[] { 0,0,2,2,3,3,3,4,4,4,5,5,5,6,6,6,8,8,8,9,9,9,10,10,10,11,11,11,12,12 };
	}

	@Override
	public int[] getAvailableOrderedProbabilities() {
		return new int[] { 2,5,4,6,3,9,8,11,11,10,6,3,8,4,8,10,11,12,10,5,4,9,5,9,12,3,2,6 };
	}

	@Override
	public Resource[] getAvailableHarbors() {
		return new Resource[] {
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
	}

	@Override
	public int[] getOrderedHarbors() {
		return new int[] {
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
	}
}
