package com.nut.bettersettlers.data.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.Resource;

public final class CatanMapGenerator {
	private static final String TAG = "CatanMapGenerator";
	
	private static final Random RAND = new Random();

	private static final String NAME = "name";
	private static final String TITLE = "title";
	private static final String LAND = "land";
	private static final String ORDERED_LAND = "ordered_land";
	private static final String WATER = "water";
	private static final String LAND_WATER = "land_water";
	private static final String RESOURCE = "resource";
	private static final String RESOURCES = "resources";
	private static final String PROBABILITY = "probability";
	private static final String PROBABILITIES = "probabilities";
	private static final String ORDERED_PROBABILITIES = "ordered_probabilities";
	private static final String HARBORS = "harbors";
	private static final String HARBOR = "harbor";
	private static final String ORDERED_HARBORS = "ordered_harbors";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String WHITELIST = "whitelist";
	private static final String LAND_WHITELIST = "land_whitelist";
	private static final String KEY = "key";
	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private static final String UNKNOWN_LANDWATER = "unknown_landwater";
	private static final String UNKNOWN_RESOURCES = "unknown_resources";
	private static final String UNKNOWN_PROBABILITIES = "unknown_probabilities";
	private static final String PLACEMENT_BLACKLIST = "placement_blacklist";
	
	// Prevent instantiation
	private CatanMapGenerator() {}
	
	public static CatanMap generateFromJson(InputStream is) {
		return generateFromJson(is, null);
	}
	
	public static CatanMap generateFromJson(InputStream is, ArrayList<Integer> theftOrder) {
		CatanMap map;
		try {
			map = generateFromJsonInternal(is, theftOrder);
		} catch (IOException e) {
			Log.e(TAG, "IOException parsing JsonMap", e);
			return null;
		} catch (JSONException e) {
			Log.e(TAG, "JSONException parsing JsonMap", e);
			return null;
		}
		
		if (!validate(map)) {
			throw new IllegalArgumentException("init() didn't run properly.");
		}
		
		return map;
	}
	
	private static CatanMap generateFromJsonInternal(InputStream is, ArrayList<Integer> theftOrder) throws IOException, JSONException {		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder str = new StringBuilder("");
		String line;
		
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		
		JSONObject json = new JSONObject(str.toString());
		
		//BetterLog.v("Name " + json.getString(NAME));
		//BetterLog.v("land.length " + json.getJSONArray(LAND).length());
		//BetterLog.v("resources.length " + json.getJSONArray(RESOURCES).length());
		//BetterLog.v("probs.length " + json.getJSONArray(PROBABILITIES).length());
		//BetterLog.v("");
		
		CatanMap.Builder mapBuilder = CatanMap.newBuilder();
		
		mapBuilder.setName(json.getString(NAME))
		        .setTitle(json.getString(TITLE));
		
		boolean[] landWithHarbors = setLandGridAndLandWithHarborsGridFromJson(mapBuilder, json.getJSONArray(LAND));
		setLandWhitelists(mapBuilder, json.has(LAND_WHITELIST) ? json.getJSONArray(LAND_WHITELIST) : null);
		setUnknownGridFromJson(mapBuilder, json.has(UNKNOWN_LANDWATER) ? json.getJSONArray(UNKNOWN_LANDWATER) : null);
		setWaterGridFromJson(mapBuilder, json.getJSONArray(WATER));
		if (json.has(LAND_WATER)) {
			convertLandToWater(mapBuilder, json.getInt(LAND_WATER), theftOrder);
		}
		setResourcesFromJson(mapBuilder, json.getJSONArray(RESOURCES));
		setUnknownResourcesFromJson(mapBuilder, json.has(UNKNOWN_RESOURCES) ? json.getJSONArray(UNKNOWN_RESOURCES) : null);
		setAvailableProbabilitiesFromJson(mapBuilder, json.getJSONArray(PROBABILITIES));
		setAvailableHarborsFromJson(mapBuilder, json.getJSONArray(HARBORS));
		setUnknownAvailableProbabilitiesFromJson(mapBuilder, json.has(UNKNOWN_PROBABILITIES) ? json.getJSONArray(UNKNOWN_PROBABILITIES) : null);

		// "ordered" map SPECIFIC
		if (json.has(ORDERED_LAND)) {
			setLandGridOrderFromJson(mapBuilder, json.getJSONArray(ORDERED_LAND));
		}
		if (json.has(ORDERED_PROBABILITIES)) {
			setAvailableOrderedProbabilitiesFromJson(mapBuilder, json.getJSONArray(ORDERED_PROBABILITIES));
		}
		if (json.has(ORDERED_HARBORS)) {
			setOrderedHarborsFromJson(mapBuilder, json.getJSONArray(ORDERED_HARBORS));
		}
		
		// Stuff everyone will have to do (post-JSON)
		setHarborLinesAndWaterNeighborsHelper(mapBuilder, landWithHarbors);
		setLandNeighborsAndIntersectionsAndPlacementIndexesHelper(mapBuilder);
		setLandIntersectionIndexesAfterIntersectionsHelper(mapBuilder);
		
		return mapBuilder.build();
	}
	
	private static boolean validate(CatanMap map) {
		return map.name != null
				&& map.lowResourceNumber > 0
				&& map.highResourceNumber > 0
				&& map.landGrid != null
				&& map.landGridWhitelists != null
				&& map.landResourceWhitelists != null
				&& map.landProbabilityWhitelists != null
				&& map.waterGrid != null
				&& map.harborLines != null
				&& map.landNeighbors != null
				&& map.waterNeighbors != null
				&& map.landIntersections != null
				&& map.landIntersectionIndexes != null
				&& map.placementIndexes != null
				&& map.availableResources != null
				&& map.availableProbabilities != null
				&& map.availableHarbors != null;
	}
	
	private static void setLandGridOrderFromJson(CatanMap.Builder mapBuilder, JSONArray array) throws JSONException {
		int[] order = new int[array.length()];
		for (int i = 0; i < array.length(); i++) {
			order[i] = array.getInt(i);
		}
		mapBuilder.setLandGridOrder(order);
	}
	
	private static void setAvailableOrderedProbabilitiesFromJson(CatanMap.Builder mapBuilder, JSONArray array) throws JSONException {
		if (array == null) {
			return;
		}
		
		int[] order = new int[array.length()];
		for (int i = 0; i < array.length(); i++) {
			order[i] = array.getInt(i);
		}
		mapBuilder.setAvailableOrderedProbabilities(order);
	}
	
	private static void setOrderedHarborsFromJson(CatanMap.Builder mapBuilder, JSONArray array) throws JSONException {
		if (array == null) {
			return;
		}
		
		int[] order = new int[array.length()];
		for (int i = 0; i < array.length(); i++) {
			order[i] = array.getInt(i);
		}
		mapBuilder.setOrderedHarbors(order);
	}
	
	// IMPORTANT: Must be run after the intersections have been set up
	// TODO(flynn): Make intersections a function argument
	private static void setLandIntersectionIndexesAfterIntersectionsHelper(CatanMap.Builder mapBuilder) {
		List<List<Integer>> indexList = new ArrayList<>();
		for (int i = 0; i < mapBuilder.getLandGrid().length; i++) {
			indexList.add(i, new ArrayList<Integer>());
		}
		
		for (int i = 0; i < mapBuilder.getLandIntersections().length; i++) {
			int[] inters = mapBuilder.getLandIntersections()[i];
			for (int inter : inters) {
				indexList.get(inter).add(i);
			}
		}

		int[][] indexes = new int[indexList.size()][];
		for (int i = 0; i < indexList.size(); i++) {
			int[] subIndexes = new int[indexList.get(i).size()];
			for (int j = 0; j < indexList.get(i).size(); j++) {
				subIndexes[j] = indexList.get(i).get(j);
			}
			indexes[i] = subIndexes;
		}
		mapBuilder.setLandIntersectionIndexes(indexes);
	}
	
	private static void setLandNeighborsAndIntersectionsAndPlacementIndexesHelper(CatanMap.Builder mapBuilder) {
		List<int[]> uberNeighborList = new ArrayList<>();
		int[][] lands = new int[mapBuilder.getLandGrid().length][];
		List<int[]> uberIndexes = new ArrayList<>();
		
		for (int i = 0; i < mapBuilder.getLandGrid().length; i++) {
			Point land = mapBuilder.getLandGrid()[i];
			List<Integer> smallList = new ArrayList<>();
			for (int j = 0; j < mapBuilder.getLandGrid().length; j++) {
				Point land2 = mapBuilder.getLandGrid()[j];
				if (land.x - 1 == land2.x && land.y - 1 == land2.y
						|| land.x + 1 == land2.x && land.y - 1 == land2.y
						|| land.x + 2 == land2.x && land.y == land2.y
						|| land.x + 1 == land2.x && land.y + 1 == land2.y
						|| land.x - 1 == land2.x && land.y + 1 == land2.y
						|| land.x - 2 == land2.x && land.y == land2.y) {
					smallList.add(j);
				}
			}
			lands[i] = new int[smallList.size()];
			for (int j = 0; j < smallList.size(); j++) {
				lands[i][j] = smallList.get(j);
			}

			List<Point> landList = Arrays.asList(mapBuilder.getLandGrid());
			if (landList.contains(getNeighbor(land, 3)) && landList.contains(getNeighbor(land, 2))) {
				int[] triplet = new int[3];
				triplet[0] = i;
				triplet[1] = landList.indexOf(getNeighbor(land, 2));
				triplet[2] = landList.indexOf(getNeighbor(land, 3));
				uberNeighborList.add(triplet);
				
				int[] duple = new int[2];
				duple[0] = i;
				duple[1] = 3;
				
				if (contains(mapBuilder.getPlacementBlacklists(), duple)) {
					uberIndexes.add(new int[0]);
				} else {
					uberIndexes.add(duple);
				}
			}
			
			if (landList.contains(getNeighbor(land, 4)) && landList.contains(getNeighbor(land, 3))) {
				int[] triplet = new int[3];
				triplet[0] = i;
				triplet[1] = landList.indexOf(getNeighbor(land, 4));
				triplet[2] = landList.indexOf(getNeighbor(land, 3));
				uberNeighborList.add(triplet);
				
				int[] duple = new int[2];
				duple[0] = i;
				duple[1] = 4;

				if (contains(mapBuilder.getPlacementBlacklists(), duple)) {
					uberIndexes.add(new int[0]);
				} else {
					uberIndexes.add(duple);
				}
			}
		}
		
		mapBuilder.setLandNeighbors(lands);
		
		for (int i = 0; i < mapBuilder.getWaterGrid().length; i++) {
			Point water = mapBuilder.getWaterGrid()[i];
			List<Point> landList = Arrays.asList(mapBuilder.getLandGrid());
			
			List<Integer> smallList = new ArrayList<>();
			for (int j = 0; j < 6; j++) {
				Point neighbor = getNeighbor(water, j);
				if (landList.contains(neighbor)) {
					smallList.add(landList.indexOf(getNeighbor(water, j)));
				}
			}
			int[] tuple = new int[smallList.size()];
			for (int j = 0; j < smallList.size(); j++) {
				tuple[j] = smallList.get(j);
			}
			
			List<int[]> tuples = new ArrayList<>();
			if (tuple.length > 2) {
				// Split 3+ arrays into small two-somes
				for (int j = 0; j < tuple.length; j++) {
					int k = (j + 1) % tuple.length;
					int[] newTuple = new int[2];
					newTuple[0] = tuple[j];
					newTuple[1] = tuple[k];
					tuples.add(newTuple);
				}
			} else if (tuple.length == 2) {
				// Keep two-somes as is
				tuples.add(tuple);
			} // Ignore all others
			
			for (int[] realTuple : tuples) {
				Arrays.sort(realTuple);
				
				Point land1 = mapBuilder.getLandGrid()[realTuple[0]];
				Point land2 = mapBuilder.getLandGrid()[realTuple[1]];
				
				// Only store consecutive coastline
				if (!(land1.x - 1 == land2.x && land1.y - 1 == land2.y
						|| land1.x + 1 == land2.x && land1.y - 1 == land2.y
						|| land1.x + 2 == land2.x && land1.y == land2.y
						|| land1.x + 1 == land2.x && land1.y + 1 == land2.y
						|| land1.x - 1 == land2.x && land1.y + 1 == land2.y
						|| land1.x - 2 == land2.x && land1.y == land2.y)) {
					continue;
				}
				
				if (!uberNeighborList.contains(realTuple)) {
					uberNeighborList.add(realTuple);
				}

				boolean added = false;

				int[] placement = new int[2];
				if (land1.y == land2.y) {
					if (land1.x > land2.x) {
						if (water.y > land1.y && water.y > land2.y) {
							placement[0] = realTuple[1];
							placement[1] = 3;
							added = true;
						} else if (water.y < land1.y && water.y < land2.y) {
							placement[0] = realTuple[1];
							placement[1] = 2;
							added = true;
						}
					} else if (land1.x < land2.x){
						if (water.y > land1.y && water.y > land2.y) {
							placement[0] = realTuple[0];
							placement[1] = 3;
							added = true;
						} else if (water.y < land1.y && water.y < land2.y) {
							placement[0] = realTuple[0];
							placement[1] = 2;
							added = true;
						}
					}
				} else if (land1.y < land2.y) {
					if (water.y == land1.y) {
						if (water.x < land1.x && water.x < land2.x) {
							placement[0] = realTuple[0];
							placement[1] = 5;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							placement[0] = realTuple[0];
							placement[1] = 3;
							added = true;
						}
					} else if (water.y == land2.y) {
						if (water.x < land1.x && water.x < land2.x) {
							placement[0] = realTuple[0];
							placement[1] = 4;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							placement[0] = realTuple[0];
							placement[1] = 4;
							added = true;
						}
					}
				} else if (land1.y > land2.y) {
					if (water.y == land1.y) {
						if (water.x < land1.x && water.x < land2.x) {
							placement[0] = realTuple[0];
							placement[1] = 2;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							placement[0] = realTuple[0];
							placement[1] = 0;
							added = true;
						}
					} else if (water.y == land2.y) {
						if (water.x < land1.x && water.x < land2.x) {
							placement[0] = realTuple[0];
							placement[1] = 1;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							placement[0] = realTuple[0];
							placement[1] = 1;
							added = true;
						}
					}
				}
				if (!added || contains(mapBuilder.getPlacementBlacklists(), placement)) {
					uberIndexes.add(new int[0]);
				} else {
					uberIndexes.add(placement);
				}
			}
		}
		
		int[][] indexes = new int[uberIndexes.size()][];
		for (int i = 0; i < uberIndexes.size(); i++) {
			indexes[i] = uberIndexes.get(i);
		}
		mapBuilder.setPlacementIndexes(indexes);
		
		int[][] inters = new int[uberNeighborList.size()][];
		for (int i = 0; i < uberNeighborList.size(); i++) {
			inters[i] = uberNeighborList.get(i);
		}
		mapBuilder.setLandIntersections(inters);
	}
	
	private static Point getNeighbor(Point refPoint, int index) {
		switch (index) {
		case 0:
			return new Point(refPoint.x - 1, refPoint.y - 1);
		case 1:
			return new Point(refPoint.x + 1, refPoint.y - 1);
		case 2:
			return new Point(refPoint.x + 2, refPoint.y);
		case 3:
			return new Point(refPoint.x + 1, refPoint.y + 1);
		case 4:
			return new Point(refPoint.x - 1, refPoint.y + 1);
		case 5:
			return new Point(refPoint.x - 2, refPoint.y);
		default:
			return refPoint;
		}
	}
	
	private static boolean contains(List<int[]> haystack, int[] needle) {
		for (int[] hay : haystack) {
			if (hay.length == needle.length) {
				boolean allEqual = true;
				for (int i = 0; i < hay.length; i++) {
					if (hay[i] != needle[i]) {
						allEqual = false;
						break;
					}
				}
				if (allEqual) {
					return true;
				}
			}
		}
		return false;
	}
	
	// super ugly, clean up
	private static void setHarborLinesAndWaterNeighborsHelper(CatanMap.Builder mapBuilder, boolean[] landWithHarbors) {
		int[][] harborLines = new int[mapBuilder.getWaterGrid().length][];
		int[][] neighborLines = new int[mapBuilder.getWaterGrid().length][];
		int[][] waterNeighborLines = new int[mapBuilder.getWaterGrid().length][];

		List<Point> landList = Arrays.asList(mapBuilder.getLandGrid());
		List<Point> waterList = Arrays.asList(mapBuilder.getWaterGrid());
		for (int i = 0; i < mapBuilder.getWaterGrid().length; i++) {
			Point water = mapBuilder.getWaterGrid()[i];

			SparseBooleanArray has = new SparseBooleanArray();
			SparseBooleanArray waterHas = new SparseBooleanArray();
			for (int j = 0; j < 6; j++) {
				Point neighbor = getNeighbor(water, j);
				if (landList.contains(neighbor)) {
					has.put(j, landWithHarbors[landList.indexOf(neighbor)]);
				} else {
					has.put(j, false);
				}
				
				if (waterList.contains(neighbor)) {
					waterHas.put(j, true);
				} else {
					waterHas.put(j, false);
				}
			}
			
			int leftMost = -1;
			for (int j = 5; j >= 0; j--) {
				if (!has.get(j) && has.get((j + 5) % 6)) {
					leftMost = (j + 1) % 6;
				}
			}
			if (leftMost == -1) {
				continue;
			}

			List<Integer> neighborList = new ArrayList<>();
			List<Integer> waterNeighborList = new ArrayList<>();
			for (int j = 0; j < 6; j++) {
				int k = (leftMost + j) % 6;
				if (has.get(k)) {
					neighborList.add(landList.indexOf(getNeighbor(water, k)));
				} else if (waterHas.get(k)) {
					waterNeighborList.add(waterList.indexOf(getNeighbor(water, k)));
				}
			}

			neighborLines[i] = new int[neighborList.size()];
			for (int j = 0; j < neighborList.size(); j++) {
				neighborLines[i][j] = neighborList.get(j);
			}
			waterNeighborLines[i] = new int[waterNeighborList.size()];
			for (int j = 0; j < waterNeighborList.size(); j++) {
				waterNeighborLines[i][j] = waterNeighborList.get(j);
			}

			List<Integer> harborList = new ArrayList<>();
			if (has.get((leftMost + 5) % 6) || has.get((leftMost + 0) % 6)) {
				harborList.add((leftMost + 0) % 6);
			}
			if (has.get((leftMost + 0) % 6) || has.get((leftMost + 1) % 6)) {
				harborList.add((leftMost + 1) % 6);
			}
			if (has.get((leftMost + 1) % 6) || has.get((leftMost + 2) % 6)) {
				harborList.add((leftMost + 2) % 6);
			}
			if (has.get((leftMost + 2) % 6) || has.get((leftMost + 3) % 6)) {
				harborList.add((leftMost + 3) % 6);
			}
			if (has.get((leftMost + 3) % 6) || has.get((leftMost + 4) % 6)) {
				harborList.add((leftMost + 4) % 6);
			}
			if (has.get((leftMost + 4) % 6) || has.get((leftMost + 5) % 6)) {
				harborList.add((leftMost + 5) % 6);
			}
			
			harborLines[i] = new int[harborList.size()];
			for (int j = 0; j < harborList.size(); j++) {
				harborLines[i][j] = harborList.get(j);
			}
		}
		mapBuilder.setWaterNeighbors(neighborLines)
		        .setWaterWaterNeighbors(waterNeighborLines)
		        .setHarborLines(harborLines);
	}
	
	private static void setAvailableHarborsFromJson(CatanMap.Builder mapBuilder, JSONArray harbors) throws JSONException {
		Resource[] avails = new Resource[harbors.length()];
		for (int i = 0; i < harbors.length(); i++) {
			String harbor = harbors.getString(i);
			avails[i] = Resource.getResourceByJson(harbor);
		}
		mapBuilder.setAvailableHarbors(avails);
	}
	
	private static void setAvailableProbabilitiesFromJson(CatanMap.Builder mapBuilder, JSONArray probs) throws JSONException {
		int[] avails = new int[probs.length()];
		for (int i = 0; i < probs.length(); i++) {
			avails[i] = probs.getInt(i);
		}
		mapBuilder.setAvailableProbabilities(avails);
	}
	
	private static void setUnknownAvailableProbabilitiesFromJson(CatanMap.Builder mapBuilder, JSONArray probs) throws JSONException {
		if (probs == null) {
			mapBuilder.setAvailableUnknownProbabilities(new int[] {});
			return;
		}
		
		int[] avails = new int[probs.length()];
		for (int i = 0; i < probs.length(); i++) {
			avails[i] = probs.getInt(i);
		}
		mapBuilder.setAvailableUnknownProbabilities(avails);
	}
	
	private static boolean[] setLandGridAndLandWithHarborsGridFromJson(CatanMap.Builder mapBuilder, JSONArray land) throws JSONException {
		Point[] landGrid = new Point[land.length()];
		String[] whitelistGrid = new String[land.length()];
		int[] probGrid = new int[land.length()];
		Resource[] resGrid = new Resource[land.length()];
		boolean[] harborGrid = new boolean[land.length()];
		List<int[]> placementBlacklist = new ArrayList<>();
		
		for (int i = 0; i < land.length(); i++) {
			JSONObject landPiece = land.getJSONObject(i);
			landGrid[i] = new Point(landPiece.getInt(X), landPiece.getInt(Y));

			if (landPiece.has(HARBOR)) {
				harborGrid[i] = landPiece.getBoolean(HARBOR);
			} else {
				harborGrid[i] = true; // default to true
			}

			if (landPiece.has(WHITELIST)) {
				whitelistGrid[i] = landPiece.getString(WHITELIST);
			}

			if (landPiece.has(PROBABILITY)) {
				probGrid[i] = landPiece.getInt(PROBABILITY);
			} else {
				probGrid[i] = Integer.MAX_VALUE;
			}

			if (landPiece.has(RESOURCE)) {
				resGrid[i] = Resource.getResourceByJson(landPiece.getString(RESOURCE));
			} else {
				resGrid[i] = null;
			}

			if (landPiece.has(PLACEMENT_BLACKLIST)) {
				JSONArray nos = landPiece.getJSONArray(PLACEMENT_BLACKLIST);
				for (int j = 0; j < nos.length(); j++) {
					int[] these = new int[2];
					these[0] = i;
					these[1] = nos.getInt(j);
					placementBlacklist.add(these);
				}
			}
		}
		mapBuilder.setLandGrid(landGrid)
		        .setLandGridWhitelists(whitelistGrid)
		        .setLandGridProbabilities(probGrid)
		        .setLandGridResources(resGrid)
		        .setPlacementBlacklists(placementBlacklist);
		
		return harborGrid;
	}
	
	private static void setUnknownGridFromJson(CatanMap.Builder mapBuilder, JSONArray array) throws JSONException {
		if (array == null) {
			mapBuilder.setUnknownGrid(new Point[] {});
			return;
		}
		
		Point[] unknownGrid = new Point[array.length()];
		
		for (int i = 0; i < array.length(); i++) {
			JSONObject unknownPiece = array.getJSONObject(i);
			unknownGrid[i] = new Point(unknownPiece.getInt(X), unknownPiece.getInt(Y));
		}
		
		mapBuilder.setUnknownGrid(unknownGrid);
	}
	
	private static void setLandWhitelists(CatanMap.Builder mapBuilder, JSONArray array) throws JSONException {
		if (array == null) {
			mapBuilder.setLandResourceWhitelists(new HashMap<String, List<Resource>>())
			        .setLandProbabilityWhitelists(new HashMap<String, List<Integer>>());
			return;
		}
		
		Map<String, List<Resource>> resourceWhitelists = new HashMap<>();
		Map<String, List<Integer>> probsWhitelists = new HashMap<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject whitelist = array.getJSONObject(i);
			
			if (whitelist.getString(TYPE).equals("resource")) {
				List<Resource> resources = new ArrayList<>();

				JSONArray resourcesJson = whitelist.getJSONArray(VALUE);
				for (int j = 0; j < resourcesJson.length(); j++) {
					resources.add(Resource.getResourceByJson(resourcesJson.getString(j)));
				}

				resourceWhitelists.put(whitelist.getString(KEY), resources);
			} else if (whitelist.getString(TYPE).equals("probability")) {
				List<Integer> probs = new ArrayList<>();
				
				JSONArray probsJson = whitelist.getJSONArray(VALUE);
				for (int j = 0; j < probsJson.length(); j++) {
					probs.add(probsJson.getInt(j));
				}

				probsWhitelists.put(whitelist.getString(KEY), probs);
			}
		}
		
		mapBuilder.setLandResourceWhitelists(resourceWhitelists)
		        .setLandProbabilityWhitelists(probsWhitelists);
	}
	
	private static void setWaterGridFromJson(CatanMap.Builder mapBuilder, JSONArray water) throws JSONException {
		Point[] waterGrid = new Point[water.length()];
		for (int i = 0; i < water.length(); i++) {
			JSONObject waterPiece = water.getJSONObject(i);
			waterGrid[i] = new Point(waterPiece.getInt(X), waterPiece.getInt(Y));
		}
		mapBuilder.setWaterGrid(waterGrid);
	}
	
	private static void convertLandToWater(CatanMap.Builder mapBuilder, int num, ArrayList<Integer> theftOrder) {
		Point[] landGrid = mapBuilder.getLandGrid();
		Point[] waterGrid = mapBuilder.getWaterGrid();
		
		int newWaterLength = waterGrid.length + num;
		int newLandLength = landGrid.length - num;
		
		Point[] newWaterGrid = new Point[newWaterLength];
		
		List<Point> landList = new ArrayList<>();
		Collections.addAll(landList, landGrid);
		List<Point> waterList = new ArrayList<>();
		Collections.addAll(waterList, waterGrid);
		
		if (theftOrder != null) {
			// Use it
			int counter = 0;
			while (num > 0 || !waterList.isEmpty()) {
				if (theftOrder.get(counter) >= 0) {
					int chosen = theftOrder.get(counter);
					newWaterGrid[counter] = landList.remove(chosen);
					num--;
					counter++;
				} else {
					newWaterGrid[counter] = waterList.remove(0);
					counter++;
				}
			}
		} else {
			// Keep track of the order for same map restoration
			theftOrder = new ArrayList<>();

			// Randomly choose either an existing water or steal one from land
			boolean choice;
			int counter = 0;
			while (num > 0 || !waterList.isEmpty()) {
				choice = RAND.nextBoolean();
				if (choice) {
					// Use existing water
					if (!waterList.isEmpty()) {
						newWaterGrid[counter] = waterList.remove(0);
						counter++;
						theftOrder.add(-18);
					}
				} else {
					// Steal from land
					if (num > 0) {
						int chosen = RAND.nextInt(landList.size());
						newWaterGrid[counter] = landList.remove(chosen);
						num--;
						counter++;
						theftOrder.add(chosen);
					}
				}
			}
		}

		Point[] newLandGrid = new Point[newLandLength];
		for (int i = 0; i < newLandLength; i++) {
			newLandGrid[i] = landList.remove(0);
		}
		
		mapBuilder.setTheftOrder(theftOrder)
		        .setLandGrid(newLandGrid)
		        .setWaterGrid(newWaterGrid);
	}
	
	private static void setResourcesFromJson(CatanMap.Builder mapBuilder, JSONArray resources) throws JSONException {
		Map<String, Integer> counts = new HashMap<>();
		
		Resource[] resourceArray = new Resource[resources.length()];
		for (int i = 0; i < resources.length(); i++) {
			String resource = resources.getString(i);
			resourceArray[i] = Resource.getResourceByJson(resource);
			
			if (counts.containsKey(resource)) {
				counts.put(resource, counts.get(resource) + 1);
			} else {
				counts.put(resource, 1);
			}
		}
		mapBuilder.setAvailableResources(resourceArray);
		
		int low = Integer.MAX_VALUE;
		int high = Integer.MIN_VALUE;
		for (Map.Entry<String, Integer> count : counts.entrySet()) {
			if (count.getKey().equals("desert") || count.getKey().equals("gold")) {
				continue;
			}
			
			if (count.getValue() > high) {
				high = count.getValue();
			}
			if (count.getValue() < low) {
				low = count.getValue();
			}
		}
        mapBuilder.setLowResourceNumber(low)
		        .setHighResourceNumber(high);
	}
	
	private static void setUnknownResourcesFromJson(CatanMap.Builder mapBuilder, JSONArray array) throws JSONException {
		if (array == null) {
			mapBuilder.setAvailableUnknownResources(new Resource[] {});
			return;
		}
		
		Resource[] unknownResources = new Resource[array.length()];
		for (int i = 0; i < array.length(); i++) {
			String resource = array.getString(i);
			unknownResources[i] = Resource.getResourceByJson(resource);
		}
		
		mapBuilder.setAvailableUnknownResources(unknownResources);
	}
}
