package com.nut.bettersettlers.data;

import android.graphics.Point;

import com.nut.bettersettlers.data.MapSpecs.Resource;

public class LargeMap implements SettlersMap {
	@Override
	public int getLowResourceNumber() {
		return 4;
	}

	@Override
	public int getHighResourceNumber() {
		return 5;
	}

	@Override
	public Point[] getLandGrid() {
		return new Point[] {
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
	}

	@Override
	public int[] getLandGridOrder() {
		return new int[] { 20,21,22, 23,19,14, 8,3,2, 1,0,4, 9,15,16, 17,18,13, 7,6,5, 10,11,12 };
	}

	@Override
	public Point[] getWaterGrid() {
		return new Point[] {
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
	}

	@Override
	public int[][] getHarborLines() {
		return new int[][] {
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
	}

	@Override
	public int[][] getLandNeighbors() {
		return new int[][] {
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
	}

	@Override
	public int[][] getWaterNeighbors() {
		return new int[][] {
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
	}

	@Override
	public int[][] getLandIntersections() {
		return new int[][] {
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
	}

	@Override
	public int[][] getLandIntersectionIndexes() {
		return new int[][] {
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
	}

	@Override
	public int[][] getPlacementIndexes() {
		return new int[][] {
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
	}

	@Override
	public Resource[] getAvailableResources() {
		return new Resource[] {
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
	}

	@Override
	public int[] getAvailableProbabilities() {
		return new int[] { 0,2,3, 3,4,4, 5,5,6, 6,8,8, 8,9,9, 9,10,10, 10,11,11, 11,12,12 };
	}

	@Override
	public int[] getAvailableOrderedProbabilities() {
		return new int[] { 2,5,4, 6,3,9, 8,11,11, 10,6,3, 8,4,8, 10,11,12, 10,5,4, 9,5 };
	}

	@Override
	public Resource[] getAvailableHarbors() {
		return new Resource[] {
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
	}
}
