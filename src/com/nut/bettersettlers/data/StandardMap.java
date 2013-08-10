package com.nut.bettersettlers.data;

import android.graphics.Point;

import com.nut.bettersettlers.data.MapSpecs.Resource;

public class StandardMap implements SettlersMap {
	@Override
	public int getLowResourceNumber() {
		return 3;
	}

	@Override
	public int getHighResourceNumber() {
		return 4;
	}

	@Override
	public Point[] getLandGrid() {
		return new Point[] { new Point(4, 2),
				new Point(6, 2), new Point(8, 2), new Point(3, 3), new Point(5, 3),
				new Point(7, 3), new Point(9, 3), new Point(2, 4), new Point(4, 4),
				new Point(6, 4), new Point(8, 4), new Point(10, 4),
				new Point(3, 5), new Point(5, 5), new Point(7, 5), new Point(9, 5),
				new Point(4, 6), new Point(6, 6), new Point(8, 6) };
	}

	@Override
	public int[] getLandGridOrder() {
		return new int[] { 16,17,18,15,11,6,2,1,0,3,7,12,13,14,10,5,4,8,9 };
	}

	@Override
	public Point[] getWaterGrid() {
		return new Point[] { new Point(3, 1),
				new Point(5, 1), new Point(7, 1), new Point(9, 1),
				new Point(10, 2), new Point(11, 3), new Point(12, 4),
				new Point(11, 5), new Point(10, 6), new Point(9, 7),
				new Point(7, 7), new Point(5, 7), new Point(3, 7), new Point(2, 6),
				new Point(1, 5), new Point(0, 4), new Point(1, 3), new Point(2, 2) };
	}

	@Override
	public int[][] getHarborLines() {
		return new int[][] { { 3, 4 },
				{ 3, 4, 5 }, { 3, 4, 5 }, { 4, 5 }, { 4, 5, 0 }, { 4, 5, 0 },
				{ 5, 0 }, { 5, 0, 1 }, { 5, 0, 1 }, { 0, 1 }, { 0, 1, 2 },
				{ 0, 1, 2 }, { 1, 2 }, { 1, 2, 3 }, { 1, 2, 3 }, { 2, 3 },
				{ 2, 3, 4 }, { 2, 3, 4 } };
	}

	@Override
	public int[][] getLandNeighbors() {
		return new int[][] { { 1, 3, 4 },
				{ 0, 2, 4, 5 }, { 1, 5, 6 }, { 0, 4, 7, 8 }, { 0, 1, 3, 5, 8, 9 },
				{ 1, 2, 4, 6, 9, 10 }, { 2, 5, 10, 11 }, { 3, 8, 12 },
				{ 3, 4, 7, 9, 12, 13 }, { 4, 5, 8, 10, 13, 14 },
				{ 5, 6, 9, 11, 14, 15 }, { 6, 10, 15 }, { 7, 8, 13, 16 },
				{ 8, 9, 12, 14, 16, 17 }, { 9, 10, 13, 15, 17, 18 },
				{ 10, 11, 14, 18 }, { 12, 13, 17 }, { 13, 14, 16, 18 },
				{ 14, 15, 17 } };
	}

	@Override
	public int[][] getWaterNeighbors() {
		return new int[][] { { 0 },
				{ 1, 0 }, { 2, 1 }, { 2 }, { 6, 2 }, { 11, 6 }, { 11 }, { 15, 11 },
				{ 18, 15 }, { 18 }, { 17, 18 }, { 16, 17 }, { 16 }, { 12, 16 },
				{ 7, 12 }, { 7 }, { 3, 7 }, { 0, 3 } };
	}

	@Override
	public int[][] getLandIntersections() {
		return new int[][] {
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
	}

	@Override
	public int[][] getLandIntersectionIndexes() {
		return new int[][] {
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
	}

	@Override
	public int[][] getPlacementIndexes() {
		return new int[][] { { 0, 3 }, // 0
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
	}

	@Override
	public Resource[] getAvailableResources() {
		return new Resource[] {
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
	}

	@Override
	public int[] getAvailableProbabilities() {
		return new int[] { 0, 2, 3, 3,
				4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12 };
	}

	@Override
	public int[] getAvailableOrderedProbabilities() {
		return new int[] { 5, 2, 6,
				3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11 };
	}

	@Override
	public Resource[] getAvailableHarbors() {
		return new Resource[] {
				Resource.SHEEP, Resource.WHEAT,
				Resource.WOOD, Resource.ROCK,
				Resource.CLAY, Resource.DESERT,
				Resource.DESERT, Resource.DESERT,
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
	}
}
