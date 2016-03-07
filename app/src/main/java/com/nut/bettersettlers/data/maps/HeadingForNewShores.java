package com.nut.bettersettlers.data.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Point;

import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.Resource;

public class HeadingForNewShores extends CatanMapProvider {
    @Override
    public CatanMap create() {
        CatanMap.Builder builder = CatanMap.newBuilder()
            .setName("heading_for_new_shores")
            .setTitle("Heading for New Shores")
            .setLowResourceNumber(4)
            .setHighResourceNumber(5)
            .setLandGrid(new Point[] {
                new Point(6, 8),
                new Point(10, 8),
                new Point(11, 7),
                new Point(14, 6),
                new Point(15, 5),
                new Point(14, 4),
                new Point(12, 2),
                new Point(11, 1),
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
                "islands",
                "islands",
                "islands",
                "islands",
                "islands",
                "islands",
                "islands",
                "islands",
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
                new Point(2, 2),
                new Point(4, 8),
                new Point(5, 9),
                new Point(7, 9),
                new Point(8, 8),
                new Point(9, 9),
                new Point(11, 9),
                new Point(12, 8),
                new Point(13, 7),
                new Point(12, 6),
                new Point(13, 5),
                new Point(15, 7),
                new Point(16, 6),
                new Point(17, 5),
                new Point(16, 4),
                new Point(15, 3),
                new Point(13, 3),
                new Point(14, 2),
                new Point(13, 1),
                new Point(12, 0),
                new Point(10, 0)
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
                new int[] { 2, 3, 4 },
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
            .setLandNeighbors(new int[][] {
                new int[] {},
                new int[] { 2 },
                new int[] { 1 },
                new int[] { 4 },
                new int[] { 3, 5 },
                new int[] { 4 },
                new int[] { 7 },
                new int[] { 6 },
                new int[] { 9, 11, 12 },
                new int[] { 8, 10, 12, 13 },
                new int[] { 9, 13, 14 },
                new int[] { 8, 12, 15, 16 },
                new int[] { 8, 9, 11, 13, 16, 17 },
                new int[] { 9, 10, 12, 14, 17, 18 },
                new int[] { 10, 13, 18, 19 },
                new int[] { 11, 16, 20 },
                new int[] { 11, 12, 15, 17, 20, 21 },
                new int[] { 12, 13, 16, 18, 21, 22 },
                new int[] { 13, 14, 17, 19, 22, 23 },
                new int[] { 14, 18, 23 },
                new int[] { 15, 16, 21, 24 },
                new int[] { 16, 17, 20, 22, 24, 25 },
                new int[] { 17, 18, 21, 23, 25, 26 },
                new int[] { 18, 19, 22, 26 },
                new int[] { 20, 21, 25 },
                new int[] { 21, 22, 24, 26 },
                new int[] { 22, 23, 25 }
            })
            .setWaterNeighbors(new int[][] {
                new int[] { 8 },
                new int[] { 9, 8 },
                new int[] { 10, 9 },
                new int[] { 10 },
                new int[] { 14, 10 },
                new int[] { 19, 14 },
                new int[] { 19 },
                new int[] { 23, 19 },
                new int[] { 26, 23 },
                new int[] { 26 },
                new int[] { 25, 26 },
                new int[] { 24, 25 },
                new int[] { 24 },
                new int[] { 20, 24 },
                new int[] { 15, 20 },
                new int[] { 15 },
                new int[] { 11, 15 },
                new int[] { 8, 11 },
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
            .setWaterWaterNeighbors(new int[][] {
                new int[] { 1, 17 },
                new int[] { 2, 0 },
                new int[] { 3, 1 },
                new int[] { 37, 4, 2 },
                new int[] { 5, 3 },
                new int[] { 33, 6, 4 },
                new int[] { 33, 27, 7, 5 },
                new int[] { 27, 26, 8, 6 },
                new int[] { 26, 9, 7 },
                new int[] { 21, 10, 8 },
                new int[] { 21, 11, 9 },
                new int[] { 18, 12, 10 },
                new int[] { 18, 13, 11 },
                new int[] { 14, 12 },
                new int[] { 15, 13 },
                new int[] { 16, 14 },
                new int[] { 17, 15 },
                new int[] { 0, 16 },
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
            .setLandIntersections(new int[][] {
                new int[] { 8, 9, 12 },
                new int[] { 8, 11, 12 },
                new int[] { 9, 10, 13 },
                new int[] { 9, 12, 13 },
                new int[] { 10, 13, 14 },
                new int[] { 11, 12, 16 },
                new int[] { 11, 15, 16 },
                new int[] { 12, 13, 17 },
                new int[] { 12, 16, 17 },
                new int[] { 13, 14, 18 },
                new int[] { 13, 17, 18 },
                new int[] { 14, 18, 19 },
                new int[] { 15, 16, 20 },
                new int[] { 16, 17, 21 },
                new int[] { 16, 20, 21 },
                new int[] { 17, 18, 22 },
                new int[] { 17, 21, 22 },
                new int[] { 18, 19, 23 },
                new int[] { 18, 22, 23 },
                new int[] { 20, 21, 24 },
                new int[] { 21, 22, 25 },
                new int[] { 21, 24, 25 },
                new int[] { 22, 23, 26 },
                new int[] { 22, 25, 26 },
                new int[] { 8, 9 },
                new int[] { 9, 10 },
                new int[] { 6, 7 },
                new int[] { 10, 14 },
                new int[] { 14, 19 },
                new int[] { 19, 23 },
                new int[] { 23, 26 },
                new int[] { 1, 2 },
                new int[] { 25, 26 },
                new int[] { 24, 25 },
                new int[] { 20, 24 },
                new int[] { 15, 20 },
                new int[] { 11, 15 },
                new int[] { 8, 11 },
                new int[] { 1, 2 },
                new int[] { 4, 5 },
                new int[] { 3, 4 },
                new int[] { 3, 4 },
                new int[] { 4, 5 },
                new int[] { 6, 7 }
            })
            .setLandIntersectionIndexes(new int[][] {
                new int[] {},
                new int[] { 31, 38 },
                new int[] { 31, 38 },
                new int[] { 40, 41 },
                new int[] { 39, 40, 41, 42 },
                new int[] { 39, 42 },
                new int[] { 26, 43 },
                new int[] { 26, 43 },
                new int[] { 0, 1, 24, 37 },
                new int[] { 0, 2, 3, 24, 25 },
                new int[] { 2, 4, 25, 27 },
                new int[] { 1, 5, 6, 36, 37 },
                new int[] { 0, 1, 3, 5, 7, 8 },
                new int[] { 2, 3, 4, 7, 9, 10 },
                new int[] { 4, 9, 11, 27, 28 },
                new int[] { 6, 12, 35, 36 },
                new int[] { 5, 6, 8, 12, 13, 14 },
                new int[] { 7, 8, 10, 13, 15, 16 },
                new int[] { 9, 10, 11, 15, 17, 18 },
                new int[] { 11, 17, 28, 29 },
                new int[] { 12, 14, 19, 34, 35 },
                new int[] { 13, 14, 16, 19, 20, 21 },
                new int[] { 15, 16, 18, 20, 22, 23 },
                new int[] { 17, 18, 22, 29, 30 },
                new int[] { 19, 21, 33, 34 },
                new int[] { 20, 21, 23, 32, 33 },
                new int[] { 22, 23, 30, 32 }
            })
            .setPlacementIndexes(new int[][] {
                new int[] { 8, 3 },
                new int[] { 8, 4 },
                new int[] { 9, 3 },
                new int[] { 9, 4 },
                new int[] { 10, 4 },
                new int[] { 11, 3 },
                new int[] { 11, 4 },
                new int[] { 12, 3 },
                new int[] { 12, 4 },
                new int[] { 13, 3 },
                new int[] { 13, 4 },
                new int[] { 14, 4 },
                new int[] { 15, 3 },
                new int[] { 16, 3 },
                new int[] { 16, 4 },
                new int[] { 17, 3 },
                new int[] { 17, 4 },
                new int[] { 18, 3 },
                new int[] { 18, 4 },
                new int[] { 20, 3 },
                new int[] { 21, 3 },
                new int[] { 21, 4 },
                new int[] { 22, 3 },
                new int[] { 22, 4 },
                new int[] { 8, 2 },
                new int[] { 9, 2 },
                new int[] {},
                new int[] { 10, 3 },
                new int[] { 14, 3 },
                new int[] { 19, 4 },
                new int[] { 23, 4 },
                new int[] {},
                new int[] { 25, 3 },
                new int[] { 24, 3 },
                new int[] { 20, 4 },
                new int[] { 15, 4 },
                new int[] { 11, 5 },
                new int[] { 8, 5 },
                new int[] {},
                new int[] {},
                new int[] {},
                new int[] {},
                new int[] {},
                new int[] {}
            })
            .setAvailableResources(new Resource[] {
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
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
                Resource.WHEAT,
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
                Resource.GOLD,
                Resource.GOLD
            })
            .setAvailableProbabilities(new int[] {
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

        Map<String, List<Resource>> landResourceWhitelists = new HashMap<>(1);
        List<Resource> islands = new ArrayList<>(8);
        islands.add(Resource.GOLD);
        islands.add(Resource.GOLD);
        islands.add(Resource.SHEEP);
        islands.add(Resource.ROCK);
        islands.add(Resource.ROCK);
        islands.add(Resource.WHEAT);
        islands.add(Resource.WOOD);
        islands.add(Resource.CLAY);
        landResourceWhitelists.put("islands", islands);
        builder.setLandResourceWhitelists(landResourceWhitelists);


        Map<String, List<Integer>> landProbabilityWhitelists = new HashMap<>(0);
        builder.setLandProbabilityWhitelists(landProbabilityWhitelists);


        List<int[]> placementBlacklists = new ArrayList<>(48);
        placementBlacklists.add(new int[] { 0, 0 });
        placementBlacklists.add(new int[] { 0, 1 });
        placementBlacklists.add(new int[] { 0, 2 });
        placementBlacklists.add(new int[] { 0, 3 });
        placementBlacklists.add(new int[] { 0, 4 });
        placementBlacklists.add(new int[] { 0, 5 });
        placementBlacklists.add(new int[] { 1, 0 });
        placementBlacklists.add(new int[] { 1, 1 });
        placementBlacklists.add(new int[] { 1, 2 });
        placementBlacklists.add(new int[] { 1, 3 });
        placementBlacklists.add(new int[] { 1, 4 });
        placementBlacklists.add(new int[] { 1, 5 });
        placementBlacklists.add(new int[] { 2, 0 });
        placementBlacklists.add(new int[] { 2, 1 });
        placementBlacklists.add(new int[] { 2, 2 });
        placementBlacklists.add(new int[] { 2, 3 });
        placementBlacklists.add(new int[] { 2, 4 });
        placementBlacklists.add(new int[] { 2, 5 });
        placementBlacklists.add(new int[] { 3, 0 });
        placementBlacklists.add(new int[] { 3, 1 });
        placementBlacklists.add(new int[] { 3, 2 });
        placementBlacklists.add(new int[] { 3, 3 });
        placementBlacklists.add(new int[] { 3, 4 });
        placementBlacklists.add(new int[] { 3, 5 });
        placementBlacklists.add(new int[] { 4, 0 });
        placementBlacklists.add(new int[] { 4, 1 });
        placementBlacklists.add(new int[] { 4, 2 });
        placementBlacklists.add(new int[] { 4, 3 });
        placementBlacklists.add(new int[] { 4, 4 });
        placementBlacklists.add(new int[] { 4, 5 });
        placementBlacklists.add(new int[] { 5, 0 });
        placementBlacklists.add(new int[] { 5, 1 });
        placementBlacklists.add(new int[] { 5, 2 });
        placementBlacklists.add(new int[] { 5, 3 });
        placementBlacklists.add(new int[] { 5, 4 });
        placementBlacklists.add(new int[] { 5, 5 });
        placementBlacklists.add(new int[] { 6, 0 });
        placementBlacklists.add(new int[] { 6, 1 });
        placementBlacklists.add(new int[] { 6, 2 });
        placementBlacklists.add(new int[] { 6, 3 });
        placementBlacklists.add(new int[] { 6, 4 });
        placementBlacklists.add(new int[] { 6, 5 });
        placementBlacklists.add(new int[] { 7, 0 });
        placementBlacklists.add(new int[] { 7, 1 });
        placementBlacklists.add(new int[] { 7, 2 });
        placementBlacklists.add(new int[] { 7, 3 });
        placementBlacklists.add(new int[] { 7, 4 });
        placementBlacklists.add(new int[] { 7, 5 });
        builder.setPlacementBlacklists(placementBlacklists);

        return builder.build();
    }
}
