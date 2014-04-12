package com.nut.bettersettlers.data.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import android.graphics.Point;

import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.Resource;

public class Standard extends CatanMapProvider {
    @Override
    public CatanMap create() {
        CatanMap.Builder builder = CatanMap.newBuilder()
            .setName("standard")
            .setTitle("Standard")
            .setLowResourceNumber(3)
            .setHighResourceNumber(4)
            .setLandGrid(new Point[] {
                new Point(4, 2),
                new Point(6, 2),
                new Point(8, 2),
                new Point(3, 3),
                new Point(5, 3),
                new Point(7, 3),
                new Point(9, 3),
                new Point(2, 4),
                new Point(4, 4),
                new Point(6, 4),
                new Point(8, 4),
                new Point(10, 4),
                new Point(3, 5),
                new Point(5, 5),
                new Point(7, 5),
                new Point(9, 5),
                new Point(4, 6),
                new Point(6, 6),
                new Point(8, 6)
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
                null
            })
            .setWaterGrid(new Point[] {
                new Point(3, 1),
                new Point(5, 1),
                new Point(7, 1),
                new Point(9, 1),
                new Point(10, 2),
                new Point(11, 3),
                new Point(12, 4),
                new Point(11, 5),
                new Point(10, 6),
                new Point(9, 7),
                new Point(7, 7),
                new Point(5, 7),
                new Point(3, 7),
                new Point(2, 6),
                new Point(1, 5),
                new Point(0, 4),
                new Point(1, 3),
                new Point(2, 2)
            })
            .setHarborLines(new int[][] {
                new int[] { 3, 4 },
                new int[] { 3, 4, 5 },
                new int[] { 3, 4, 5 },
                new int[] { 4, 5 },
                new int[] { 4, 5, 0 },
                new int[] { 4, 5, 0 },
                new int[] { 5, 0 },
                new int[] { 5, 0, 1 },
                new int[] { 5, 0, 1 },
                new int[] { 0, 1 },
                new int[] { 0, 1, 2 },
                new int[] { 0, 1, 2 },
                new int[] { 1, 2 },
                new int[] { 1, 2, 3 },
                new int[] { 1, 2, 3 },
                new int[] { 2, 3 },
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
                new int[] { 3, 8, 12 },
                new int[] { 3, 4, 7, 9, 12, 13 },
                new int[] { 4, 5, 8, 10, 13, 14 },
                new int[] { 5, 6, 9, 11, 14, 15 },
                new int[] { 6, 10, 15 },
                new int[] { 7, 8, 13, 16 },
                new int[] { 8, 9, 12, 14, 16, 17 },
                new int[] { 9, 10, 13, 15, 17, 18 },
                new int[] { 10, 11, 14, 18 },
                new int[] { 12, 13, 17 },
                new int[] { 13, 14, 16, 18 },
                new int[] { 14, 15, 17 }
            })
            .setWaterNeighbors(new int[][] {
                new int[] { 0 },
                new int[] { 1, 0 },
                new int[] { 2, 1 },
                new int[] { 2 },
                new int[] { 6, 2 },
                new int[] { 11, 6 },
                new int[] { 11 },
                new int[] { 15, 11 },
                new int[] { 18, 15 },
                new int[] { 18 },
                new int[] { 17, 18 },
                new int[] { 16, 17 },
                new int[] { 16 },
                new int[] { 12, 16 },
                new int[] { 7, 12 },
                new int[] { 7 },
                new int[] { 3, 7 },
                new int[] { 0, 3 }
            })
            .setWaterWaterNeighbors(new int[][] {
                new int[] { 1, 17 },
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
                new int[] { 0, 16 }
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
                new int[] { 7, 8, 12 },
                new int[] { 8, 9, 13 },
                new int[] { 8, 12, 13 },
                new int[] { 9, 10, 14 },
                new int[] { 9, 13, 14 },
                new int[] { 10, 11, 15 },
                new int[] { 10, 14, 15 },
                new int[] { 12, 13, 16 },
                new int[] { 13, 14, 17 },
                new int[] { 13, 16, 17 },
                new int[] { 14, 15, 18 },
                new int[] { 14, 17, 18 },
                new int[] { 0, 1 },
                new int[] { 1, 2 },
                new int[] { 2, 6 },
                new int[] { 6, 11 },
                new int[] { 11, 15 },
                new int[] { 15, 18 },
                new int[] { 17, 18 },
                new int[] { 16, 17 },
                new int[] { 12, 16 },
                new int[] { 7, 12 },
                new int[] { 3, 7 },
                new int[] { 0, 3 }
            })
            .setLandIntersectionIndexes(new int[][] {
                new int[] { 0, 1, 24, 35 },
                new int[] { 0, 2, 3, 24, 25 },
                new int[] { 2, 4, 25, 26 },
                new int[] { 1, 5, 6, 34, 35 },
                new int[] { 0, 1, 3, 5, 7, 8 },
                new int[] { 2, 3, 4, 7, 9, 10 },
                new int[] { 4, 9, 11, 26, 27 },
                new int[] { 6, 12, 33, 34 },
                new int[] { 5, 6, 8, 12, 13, 14 },
                new int[] { 7, 8, 10, 13, 15, 16 },
                new int[] { 9, 10, 11, 15, 17, 18 },
                new int[] { 11, 17, 27, 28 },
                new int[] { 12, 14, 19, 32, 33 },
                new int[] { 13, 14, 16, 19, 20, 21 },
                new int[] { 15, 16, 18, 20, 22, 23 },
                new int[] { 17, 18, 22, 28, 29 },
                new int[] { 19, 21, 31, 32 },
                new int[] { 20, 21, 23, 30, 31 },
                new int[] { 22, 23, 29, 30 }
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
                new int[] { 8, 3 },
                new int[] { 8, 4 },
                new int[] { 9, 3 },
                new int[] { 9, 4 },
                new int[] { 10, 3 },
                new int[] { 10, 4 },
                new int[] { 12, 3 },
                new int[] { 13, 3 },
                new int[] { 13, 4 },
                new int[] { 14, 3 },
                new int[] { 14, 4 },
                new int[] { 0, 2 },
                new int[] { 1, 2 },
                new int[] { 2, 3 },
                new int[] { 6, 3 },
                new int[] { 11, 4 },
                new int[] { 15, 4 },
                new int[] { 17, 3 },
                new int[] { 16, 3 },
                new int[] { 12, 4 },
                new int[] { 7, 4 },
                new int[] { 3, 5 },
                new int[] { 0, 5 }
            })
            .setAvailableResources(new Resource[] {
                Resource.WOOD,
                Resource.WOOD,
                Resource.WOOD,
                Resource.WOOD,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.SHEEP,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.CLAY,
                Resource.CLAY,
                Resource.CLAY,
                Resource.ROCK,
                Resource.ROCK,
                Resource.ROCK,
                Resource.DESERT
            })
            .setAvailableProbabilities(new int[] {
                0,
                2,
                3,
                3,
                4,
                4,
                5,
                5,
                6,
                6,
                8,
                8,
                9,
                9,
                10,
                10,
                11,
                11,
                12
            })
            .setAvailableHarbors(new Resource[] {
                Resource.WOOD,
                Resource.SHEEP,
                Resource.WHEAT,
                Resource.CLAY,
                Resource.ROCK,
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
                16,
                17,
                18,
                15,
                11,
                6,
                2,
                1,
                0,
                3,
                7,
                12,
                13,
                14,
                10,
                5,
                4,
                8,
                9
            });
        builder.setAvailableOrderedProbabilities(new int[] {
                5,
                2,
                6,
                3,
                8,
                10,
                9,
                12,
                11,
                4,
                8,
                10,
                9,
                4,
                5,
                6,
                3,
                11
            });
        builder.setOrderedHarbors(new int[] {
                0,
                -1,
                1,
                -1,
                0,
                -1,
                0,
                -1,
                1,
                -1,
                0,
                -1,
                0,
                -1,
                1,
                -1,
                0,
                -1
            });
        return builder.build();
    }
}
