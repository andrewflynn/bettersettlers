package com.nut.bettersettlers.data.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import android.graphics.Point;

import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.Resource;

public class XLarge extends CatanMapProvider {
    @Override
    public CatanMap create() {
        CatanMap.Builder builder = CatanMap.newBuilder()
            .setName("xlarge")
            .setTitle("X Large")
            .setLowResourceNumber(5)
            .setHighResourceNumber(6)
            .setLandGrid(new Point[] {
                new Point(5, 1),
                new Point(7, 1),
                new Point(9, 1),
                new Point(4, 2),
                new Point(6, 2),
                new Point(8, 2),
                new Point(10, 2),
                new Point(3, 3),
                new Point(5, 3),
                new Point(7, 3),
                new Point(9, 3),
                new Point(11, 3),
                new Point(2, 4),
                new Point(4, 4),
                new Point(6, 4),
                new Point(8, 4),
                new Point(10, 4),
                new Point(12, 4),
                new Point(3, 5),
                new Point(5, 5),
                new Point(7, 5),
                new Point(9, 5),
                new Point(11, 5),
                new Point(4, 6),
                new Point(6, 6),
                new Point(8, 6),
                new Point(10, 6),
                new Point(5, 7),
                new Point(7, 7),
                new Point(9, 7)
            })
            .setLandGridWhitelists(new String[] {
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            })
            .setLandGridProbabilities(new int[] {
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647,
                2147483647
            })
            .setLandGridResources(new Resource[] {
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            })
            .setWaterGrid(new Point[] {
                new Point(4, 0),
                new Point(6, 0),
                new Point(8, 0),
                new Point(10, 0),
                new Point(11, 1),
                new Point(12, 2),
                new Point(13, 3),
                new Point(14, 4),
                new Point(13, 5),
                new Point(12, 6),
                new Point(11, 7),
                new Point(10, 8),
                new Point(8, 8),
                new Point(6, 8),
                new Point(4, 8),
                new Point(3, 7),
                new Point(2, 6),
                new Point(1, 5),
                new Point(0, 4),
                new Point(1, 3),
                new Point(2, 2),
                new Point(3, 1)
            })
            .setHarborLines(new int[][] {
                new int[] { 3, 4 },
                new int[] { 3, 4, 5 },
                new int[] { 3, 4, 5 },
                new int[] { 4, 5 },
                new int[] { 4, 5, 0 },
                new int[] { 4, 5, 0 },
                new int[] { 4, 5, 0 },
                new int[] { 5, 0 },
                new int[] { 5, 0, 1 },
                new int[] { 5, 0, 1 },
                new int[] { 5, 0, 1 },
                new int[] { 0, 1 },
                new int[] { 0, 1, 2 },
                new int[] { 0, 1, 2 },
                new int[] { 1, 2 },
                new int[] { 1, 2, 3 },
                new int[] { 1, 2, 3 },
                new int[] { 1, 2, 3 },
                new int[] { 2, 3 },
                new int[] { 2, 3, 4 },
                new int[] { 2, 3, 4 },
                new int[] { 2, 3, 4 }
            })
            .setLandNeighbors(new int[][] {
                new int[] { 1, 3, 4 },
                new int[] { 0, 2, 4, 5 },
                new int[] { 1, 5, 6 },
                new int[] { 0, 4, 7, 8 },
                new int[] { 0, 1, 3, 5, 8, 9 },
                new int[] { 1, 2, 4, 6, 9, 10 },
                new int[] { 2, 5, 10, 11 },
                new int[] { 3, 8, 12, 13 },
                new int[] { 3, 4, 7, 9, 13, 14 },
                new int[] { 4, 5, 8, 10, 14, 15 },
                new int[] { 5, 6, 9, 11, 15, 16 },
                new int[] { 6, 10, 16, 17 },
                new int[] { 7, 13, 18 },
                new int[] { 7, 8, 12, 14, 18, 19 },
                new int[] { 8, 9, 13, 15, 19, 20 },
                new int[] { 9, 10, 14, 16, 20, 21 },
                new int[] { 10, 11, 15, 17, 21, 22 },
                new int[] { 11, 16, 22 },
                new int[] { 12, 13, 19, 23 },
                new int[] { 13, 14, 18, 20, 23, 24 },
                new int[] { 14, 15, 19, 21, 24, 25 },
                new int[] { 15, 16, 20, 22, 25, 26 },
                new int[] { 16, 17, 21, 26 },
                new int[] { 18, 19, 24, 27 },
                new int[] { 19, 20, 23, 25, 27, 28 },
                new int[] { 20, 21, 24, 26, 28, 29 },
                new int[] { 21, 22, 25, 29 },
                new int[] { 23, 24, 28 },
                new int[] { 24, 25, 27, 29 },
                new int[] { 25, 26, 28 }
            })
            .setWaterNeighbors(new int[][] {
                new int[] { 0 },
                new int[] { 1, 0 },
                new int[] { 2, 1 },
                new int[] { 2 },
                new int[] { 6, 2 },
                new int[] { 11, 6 },
                new int[] { 17, 11 },
                new int[] { 17 },
                new int[] { 22, 17 },
                new int[] { 26, 22 },
                new int[] { 29, 26 },
                new int[] { 29 },
                new int[] { 28, 29 },
                new int[] { 27, 28 },
                new int[] { 27 },
                new int[] { 23, 27 },
                new int[] { 18, 23 },
                new int[] { 12, 18 },
                new int[] { 12 },
                new int[] { 7, 12 },
                new int[] { 3, 7 },
                new int[] { 0, 3 }
            })
            .setWaterWaterNeighbors(new int[][] {
                new int[] { 1, 21 },
                new int[] { 2, 0 },
                new int[] { 3, 1 },
                new int[] { 4, 2 },
                new int[] { 5, 3 },
                new int[] { 6, 4 },
                new int[] { 7, 5 },
                new int[] { 8, 6 },
                new int[] { 9, 7 },
                new int[] { 10, 8 },
                new int[] { 11, 9 },
                new int[] { 12, 10 },
                new int[] { 13, 11 },
                new int[] { 14, 12 },
                new int[] { 15, 13 },
                new int[] { 16, 14 },
                new int[] { 17, 15 },
                new int[] { 18, 16 },
                new int[] { 19, 17 },
                new int[] { 20, 18 },
                new int[] { 21, 19 },
                new int[] { 0, 20 }
            })
            .setLandIntersections(new int[][] {
                new int[] { 0, 1, 4 },
                new int[] { 0, 3, 4 },
                new int[] { 1, 2, 5 },
                new int[] { 1, 4, 5 },
                new int[] { 2, 5, 6 },
                new int[] { 3, 4, 8 },
                new int[] { 3, 7, 8 },
                new int[] { 4, 5, 9 },
                new int[] { 4, 8, 9 },
                new int[] { 5, 6, 10 },
                new int[] { 5, 9, 10 },
                new int[] { 6, 10, 11 },
                new int[] { 7, 8, 13 },
                new int[] { 7, 12, 13 },
                new int[] { 8, 9, 14 },
                new int[] { 8, 13, 14 },
                new int[] { 9, 10, 15 },
                new int[] { 9, 14, 15 },
                new int[] { 10, 11, 16 },
                new int[] { 10, 15, 16 },
                new int[] { 11, 16, 17 },
                new int[] { 12, 13, 18 },
                new int[] { 13, 14, 19 },
                new int[] { 13, 18, 19 },
                new int[] { 14, 15, 20 },
                new int[] { 14, 19, 20 },
                new int[] { 15, 16, 21 },
                new int[] { 15, 20, 21 },
                new int[] { 16, 17, 22 },
                new int[] { 16, 21, 22 },
                new int[] { 18, 19, 23 },
                new int[] { 19, 20, 24 },
                new int[] { 19, 23, 24 },
                new int[] { 20, 21, 25 },
                new int[] { 20, 24, 25 },
                new int[] { 21, 22, 26 },
                new int[] { 21, 25, 26 },
                new int[] { 23, 24, 27 },
                new int[] { 24, 25, 28 },
                new int[] { 24, 27, 28 },
                new int[] { 25, 26, 29 },
                new int[] { 25, 28, 29 },
                new int[] { 0, 1 },
                new int[] { 1, 2 },
                new int[] { 2, 6 },
                new int[] { 6, 11 },
                new int[] { 11, 17 },
                new int[] { 17, 22 },
                new int[] { 22, 26 },
                new int[] { 26, 29 },
                new int[] { 28, 29 },
                new int[] { 27, 28 },
                new int[] { 23, 27 },
                new int[] { 18, 23 },
                new int[] { 12, 18 },
                new int[] { 7, 12 },
                new int[] { 3, 7 },
                new int[] { 0, 3 }
            })
            .setLandIntersectionIndexes(new int[][] {
                new int[] { 0, 1, 42, 57 },
                new int[] { 0, 2, 3, 42, 43 },
                new int[] { 2, 4, 43, 44 },
                new int[] { 1, 5, 6, 56, 57 },
                new int[] { 0, 1, 3, 5, 7, 8 },
                new int[] { 2, 3, 4, 7, 9, 10 },
                new int[] { 4, 9, 11, 44, 45 },
                new int[] { 6, 12, 13, 55, 56 },
                new int[] { 5, 6, 8, 12, 14, 15 },
                new int[] { 7, 8, 10, 14, 16, 17 },
                new int[] { 9, 10, 11, 16, 18, 19 },
                new int[] { 11, 18, 20, 45, 46 },
                new int[] { 13, 21, 54, 55 },
                new int[] { 12, 13, 15, 21, 22, 23 },
                new int[] { 14, 15, 17, 22, 24, 25 },
                new int[] { 16, 17, 19, 24, 26, 27 },
                new int[] { 18, 19, 20, 26, 28, 29 },
                new int[] { 20, 28, 46, 47 },
                new int[] { 21, 23, 30, 53, 54 },
                new int[] { 22, 23, 25, 30, 31, 32 },
                new int[] { 24, 25, 27, 31, 33, 34 },
                new int[] { 26, 27, 29, 33, 35, 36 },
                new int[] { 28, 29, 35, 47, 48 },
                new int[] { 30, 32, 37, 52, 53 },
                new int[] { 31, 32, 34, 37, 38, 39 },
                new int[] { 33, 34, 36, 38, 40, 41 },
                new int[] { 35, 36, 40, 48, 49 },
                new int[] { 37, 39, 51, 52 },
                new int[] { 38, 39, 41, 50, 51 },
                new int[] { 40, 41, 49, 50 }
            })
            .setPlacementIndexes(new int[][] {
                new int[] { 0, 3 },
                new int[] { 0, 4 },
                new int[] { 1, 3 },
                new int[] { 1, 4 },
                new int[] { 2, 4 },
                new int[] { 3, 3 },
                new int[] { 3, 4 },
                new int[] { 4, 3 },
                new int[] { 4, 4 },
                new int[] { 5, 3 },
                new int[] { 5, 4 },
                new int[] { 6, 4 },
                new int[] { 7, 3 },
                new int[] { 7, 4 },
                new int[] { 8, 3 },
                new int[] { 8, 4 },
                new int[] { 9, 3 },
                new int[] { 9, 4 },
                new int[] { 10, 3 },
                new int[] { 10, 4 },
                new int[] { 11, 4 },
                new int[] { 12, 3 },
                new int[] { 13, 3 },
                new int[] { 13, 4 },
                new int[] { 14, 3 },
                new int[] { 14, 4 },
                new int[] { 15, 3 },
                new int[] { 15, 4 },
                new int[] { 16, 3 },
                new int[] { 16, 4 },
                new int[] { 18, 3 },
                new int[] { 19, 3 },
                new int[] { 19, 4 },
                new int[] { 20, 3 },
                new int[] { 20, 4 },
                new int[] { 21, 3 },
                new int[] { 21, 4 },
                new int[] { 23, 3 },
                new int[] { 24, 3 },
                new int[] { 24, 4 },
                new int[] { 25, 3 },
                new int[] { 25, 4 },
                new int[] { 0, 2 },
                new int[] { 1, 2 },
                new int[] { 2, 3 },
                new int[] { 6, 3 },
                new int[] { 11, 3 },
                new int[] { 17, 4 },
                new int[] { 22, 4 },
                new int[] { 26, 4 },
                new int[] { 28, 3 },
                new int[] { 27, 3 },
                new int[] { 23, 4 },
                new int[] { 18, 4 },
                new int[] { 12, 4 },
                new int[] { 7, 5 },
                new int[] { 3, 5 },
                new int[] { 0, 5 }
            })
            .setAvailableResources(new Resource[] {
                Resource.WOOD,
                Resource.WOOD,
                Resource.WOOD,
                Resource.WOOD,
                Resource.WOOD,
                Resource.WOOD,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.CLAY,
                Resource.CLAY,
                Resource.CLAY,
                Resource.CLAY,
                Resource.CLAY,
                Resource.ROCK,
                Resource.ROCK,
                Resource.ROCK,
                Resource.ROCK,
                Resource.ROCK,
                Resource.DESERT,
                Resource.DESERT
            })
            .setAvailableProbabilities(new int[] {
                0,
                0,
                2,
                2,
                3,
                3,
                3,
                4,
                4,
                4,
                5,
                5,
                5,
                6,
                6,
                6,
                8,
                8,
                8,
                9,
                9,
                9,
                10,
                10,
                10,
                11,
                11,
                11,
                12,
                12
            })
            .setAvailableHarbors(new Resource[] {
                Resource.WOOD,
                Resource.SHEEP,
                Resource.WHEAT,
                Resource.CLAY,
                Resource.ROCK,
                Resource.SHEEP,
                Resource.DESERT,
                Resource.DESERT,
                Resource.DESERT,
                Resource.DESERT,
                Resource.DESERT
            })
            .setAvailableUnknownResources(new Resource[] {})
            .setAvailableUnknownProbabilities(new int[] {})
            .setUnknownGrid(new Point[] {});

        Map<String, List<Resource>> landResourceWhitelists = new HashMap<String, List<Resource>>(0);
        builder.setLandResourceWhitelists(landResourceWhitelists);


        Map<String, List<Integer>> landProbabilityWhitelists = new HashMap<String, List<Integer>>(0);
        builder.setLandProbabilityWhitelists(landProbabilityWhitelists);


        List<int[]> placementBlacklists = new ArrayList<int[]>(0);
        builder.setPlacementBlacklists(placementBlacklists);

        builder.setLandGridOrder(new int[] {
                27,
                28,
                29,
                26,
                22,
                17,
                11,
                6,
                2,
                1,
                0,
                3,
                7,
                12,
                18,
                23,
                24,
                25,
                21,
                16,
                10,
                5,
                4,
                8,
                13,
                19,
                20,
                15,
                9,
                14
            });
        builder.setAvailableOrderedProbabilities(new int[] {
                2,
                5,
                4,
                6,
                3,
                9,
                8,
                11,
                11,
                10,
                6,
                3,
                8,
                4,
                8,
                10,
                11,
                12,
                10,
                5,
                4,
                9,
                5,
                9,
                12,
                3,
                2,
                6
            });
        builder.setOrderedHarbors(new int[] {
                0,
                -1,
                1,
                -1,
                0,
                -1,
                -1,
                0,
                -1,
                1,
                0,
                -1,
                0,
                -1,
                0,
                -1,
                1,
                0,
                -1,
                0,
                -1,
                -1
            });
        return builder.build();
    }
}