package com.nut.bettersettlers.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;
import android.util.Log;

import com.nut.bettersettlers.data.MapConsts.Resource;

public class JsonCatanMap extends CatanMap {
	private static final String LOG_TAG = JsonCatanMap.class.getSimpleName();
	
	private static final Random RAND = new Random();
	
	private static final String NAME = "name";
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
	
	private ArrayList<Integer> theftOrder;
	
	public JsonCatanMap(InputStream is) {
		this(is, null);
	}
	
	public JsonCatanMap(InputStream is, ArrayList<Integer> theftOrder) {
		this.theftOrder = theftOrder;
		
		try {
			init(is);
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException parsing JsonMap");
			e.printStackTrace();
			return;
		} catch (JSONException e) {
			Log.e(LOG_TAG, "JSONException parsing JsonMap");
			e.printStackTrace();
			return;
		}
		
		if (getName() == null
				|| getLowResourceNumber() <= 0
				|| getHighResourceNumber() <= 0
				|| getLandGrid() == null
				|| getLandGridWhitelists() == null
				|| getLandResourceWhitelists() == null
				|| getLandProbabilityWhitelists() == null
				|| getWaterGrid() == null
				|| getHarborLines() == null
				|| getLandNeighbors() == null
				|| getWaterNeighbors() == null
				|| getLandIntersections() == null
				|| getLandIntersectionIndexes() == null
				|| getPlacementIndexes() == null
				|| getAvailableResources() == null
				|| getAvailableProbabilities() == null
				|| getAvailableHarbors() == null) {
			throw new IllegalArgumentException("init() didn't run properly.");
		}
	}
	
	private void init(InputStream is) throws IOException, JSONException {		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder str = new StringBuilder("");
		String line;
		
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		
		JSONObject json = new JSONObject(str.toString());
		
		setName(json.getString(NAME));
		
		boolean[] landWithHarbors = setLandGridAndLandWithHarborsGridFromJson(json.getJSONArray(LAND));
		setLandWhitelists(json.has(LAND_WHITELIST) ? json.getJSONArray(LAND_WHITELIST) : null);
		setUnknownGridFromJson(json.has(UNKNOWN_LANDWATER) ? json.getJSONArray(UNKNOWN_LANDWATER) : null);
		setWaterGridFromJson(json.getJSONArray(WATER));
		if (json.has(LAND_WATER)) {
			convertLandToWater(json.getInt(LAND_WATER));
		}
		setResourcesFromJson(json.getJSONArray(RESOURCES));
		setUnknownResourcesFromJson(json.has(UNKNOWN_RESOURCES) ? json.getJSONArray(UNKNOWN_RESOURCES) : null);
		setAvailableProbabilitiesFromJson(json.getJSONArray(PROBABILITIES));
		setAvailableHarborsFromJson(json.getJSONArray(HARBORS));
		setUnknownAvailableProbabilitiesFromJson(json.has(UNKNOWN_PROBABILITIES) ? json.getJSONArray(UNKNOWN_PROBABILITIES) : null);

		// "ordered" map SPECIFIC
		if (json.has(ORDERED_LAND)) {
			setLandGridOrderFromJson(json.getJSONArray(ORDERED_LAND));
		}
		if (json.has(ORDERED_PROBABILITIES)) {
			setAvailableOrderedProbabilitiesFromJson(json.getJSONArray(ORDERED_PROBABILITIES));
		}
		if (json.has(ORDERED_HARBORS)) {
			setOrderedHarborsFromJson(json.getJSONArray(ORDERED_HARBORS));
		}
		
		// Stuff everyone will have to do (post-JSON)
		setHarborLinesAndWaterNeighborsHelper(landWithHarbors);
		setLandNeighborsAndIntersectionsAndPlacementIndexesHelper();
		setLandIntersectionIndexesAfterIntersectionsHelper();
	}
	
	private void setLandGridOrderFromJson(JSONArray array) throws JSONException {
		int[] order = new int[array.length()];
		for (int i = 0; i < array.length(); i++) {
			order[i] = array.getInt(i);
		}
		setLandGridOrder(order);
	}
	
	private void setAvailableOrderedProbabilitiesFromJson(JSONArray array) throws JSONException {
		if (array == null) {
			return;
		}
		
		int[] order = new int[array.length()];
		for (int i = 0; i < array.length(); i++) {
			order[i] = array.getInt(i);
		}
		setAvailableOrderedProbabilities(order);
	}
	
	private void setOrderedHarborsFromJson(JSONArray array) throws JSONException {
		if (array == null) {
			return;
		}
		
		int[] order = new int[array.length()];
		for (int i = 0; i < array.length(); i++) {
			order[i] = array.getInt(i);
		}
		setOrderedHarbors(order);
	}
	
	// IMPORTANT: Must be run after the intersections have been set up
	// TODO(flynn): Make intersections a function argument
	private void setLandIntersectionIndexesAfterIntersectionsHelper() {
		List<List<Integer>> indexList = new ArrayList<List<Integer>>();
		for (int i = 0; i < getLandGrid().length; i++) {
			indexList.add(i, new ArrayList<Integer>());
		}
		
		for (int i = 0; i < getLandIntersections().length; i++) {
			int[] inters = getLandIntersections()[i];
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
		setLandIntersectionIndexes(indexes);
	}
	
	private void setLandNeighborsAndIntersectionsAndPlacementIndexesHelper() {
		List<int[]> uberNeighborList = new ArrayList<int[]>();
		int[][] lands = new int[getLandGrid().length][];
		List<int[]> uberIndexes = new ArrayList<int[]>();
		
		for (int i = 0; i < getLandGrid().length; i++) {
			Point land = getLandGrid()[i];
			List<Integer> smallList = new ArrayList<Integer>();
			for (int j = 0; j < getLandGrid().length; j++) {
				Point land2 = getLandGrid()[j];
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

			List<Point> landList = Arrays.asList(getLandGrid());
			if (landList.contains(getNeighbor(land, 3)) && landList.contains(getNeighbor(land, 2))) {
				int[] triplet = new int[3];
				triplet[0] = i;
				triplet[1] = landList.indexOf(getNeighbor(land, 2));
				triplet[2] = landList.indexOf(getNeighbor(land, 3));
				uberNeighborList.add(triplet);
				
				int[] duple = new int[2];
				duple[0] = i;
				duple[1] = 3;
				
				if (contains(getPlacementBlacklists(), duple)) {
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

				if (contains(getPlacementBlacklists(), duple)) {
					uberIndexes.add(new int[0]);
				} else {
					uberIndexes.add(duple);
				}
			}
		}
		
		setLandNeighbors(lands);
		
		for (int i = 0; i < getWaterGrid().length; i++) {
			Point water = getWaterGrid()[i];
			List<Point> landList = Arrays.asList(getLandGrid());
			
			List<Integer> smallList = new ArrayList<Integer>();
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
			
			List<int[]> tuples = new ArrayList<int[]>();
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
				
				Point land1 = getLandGrid()[realTuple[0]];
				Point land2 = getLandGrid()[realTuple[1]];
				
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

				//System.out.println(getName() + " 0000 " + String.format("(%d,%d)", tuple[0], tuple[1]));
				int[] placement = new int[2];
				if (land1.y == land2.y) {
					if (land1.x > land2.x) {
						if (water.y > land1.y && water.y > land2.y) {
							//System.out.println(getName() + " 1111aaaa");
							placement[0] = realTuple[1];
							placement[1] = 3;
							added = true;
						} else if (water.y < land1.y && water.y < land2.y) {
							//System.out.println(getName() + " 2222aaaa");
							placement[0] = realTuple[1];
							placement[1] = 2;
							added = true;
						}
					} else if (land1.x < land2.x){
						if (water.y > land1.y && water.y > land2.y) {
							//System.out.println(getName() + " 1111bbbb");
							placement[0] = realTuple[0];
							placement[1] = 3;
							added = true;
						} else if (water.y < land1.y && water.y < land2.y) {
							//System.out.println(getName() + " 2222bbbb");
							placement[0] = realTuple[0];
							placement[1] = 2;
							added = true;
						}
					}
				} else if (land1.y < land2.y) {
					if (water.y == land1.y) {
						if (water.x < land1.x && water.x < land2.x) {
							//System.out.println(getName() + " 3333");
							placement[0] = realTuple[0];
							placement[1] = 5;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							//System.out.println(getName() + " 4444");
							placement[0] = realTuple[0];
							placement[1] = 3;
							added = true;
						}
					} else if (water.y == land2.y) {
						if (water.x < land1.x && water.x < land2.x) {
							//System.out.println(getName() + " 5555");
							placement[0] = realTuple[0];
							placement[1] = 4;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							//System.out.println(getName() + " 6666");
							placement[0] = realTuple[0];
							placement[1] = 4;
							added = true;
						}
					}
				} else if (land1.y > land2.y) {
					if (water.y == land1.y) {
						if (water.x < land1.x && water.x < land2.x) {
							//System.out.println(getName() + " 7777");
							placement[0] = realTuple[0];
							placement[1] = 2;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							//System.out.println(getName() + " 8888");
							placement[0] = realTuple[0];
							placement[1] = 0;
							added = true;
						}
					} else if (water.y == land2.y) {
						if (water.x < land1.x && water.x < land2.x) {
							//System.out.println(getName() + " 9999");
							placement[0] = realTuple[0];
							placement[1] = 1;
							added = true;
						} else if (water.x > land1.x && water.x > land1.x) {
							//System.out.println(getName() + " AAAA");
							placement[0] = realTuple[0];
							placement[1] = 1;
							added = true;
						}
					}
				}
				if (!added || contains(getPlacementBlacklists(), placement)) {
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
		setPlacementIndexes(indexes);
		
		int[][] inters = new int[uberNeighborList.size()][];
		for (int i = 0; i < uberNeighborList.size(); i++) {
			inters[i] = uberNeighborList.get(i);
		}
		setLandIntersections(inters);
	}
	
	private Point getNeighbor(Point refPoint, int index) {
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
	
	private boolean contains(List<int[]> haystack, int[] needle) {
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
	private void setHarborLinesAndWaterNeighborsHelper(boolean[] landWithHarbors) {
		int[][] harborLines = new int[getWaterGrid().length][];
		int[][] neighborLines = new int[getWaterGrid().length][];
		int[][] waterNeighborLines = new int[getWaterGrid().length][];

		List<Point> landList = Arrays.asList(getLandGrid());
		List<Point> waterList = Arrays.asList(getWaterGrid());
		for (int i = 0; i < getWaterGrid().length; i++) {
			Point water = getWaterGrid()[i];

			Map<Integer, Boolean> has = new HashMap<Integer, Boolean>();
			Map<Integer, Boolean> waterHas = new HashMap<Integer, Boolean>();
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

			List<Integer> neighborList = new ArrayList<Integer>();
			List<Integer> waterNeighborList = new ArrayList<Integer>();
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

			List<Integer> harborList = new ArrayList<Integer>();
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
		setWaterNeighbors(neighborLines);
		setWaterWaterNeighbors(waterNeighborLines);
		setHarborLines(harborLines);
	}
	
	private void setAvailableHarborsFromJson(JSONArray harbors) throws JSONException {
		Resource[] avails = new Resource[harbors.length()];
		for (int i = 0; i < harbors.length(); i++) {
			String harbor = harbors.getString(i);
			avails[i] = Resource.findResourceByJson(harbor);
		}
		setAvailableHarbors(avails);
	}
	
	private void setAvailableProbabilitiesFromJson(JSONArray probs) throws JSONException {
		int[] avails = new int[probs.length()];
		for (int i = 0; i < probs.length(); i++) {
			avails[i] = probs.getInt(i);
		}
		setAvailableProbabilities(avails);
	}
	
	private void setUnknownAvailableProbabilitiesFromJson(JSONArray probs) throws JSONException {
		if (probs == null) {
			setAvailableUnknownProbabilities(new int[] {});
			return;
		}
		
		int[] avails = new int[probs.length()];
		for (int i = 0; i < probs.length(); i++) {
			avails[i] = probs.getInt(i);
		}
		setAvailableUnknownProbabilities(avails);
	}
	
	private boolean[] setLandGridAndLandWithHarborsGridFromJson(JSONArray land) throws JSONException {
		Point[] landGrid = new Point[land.length()];
		String[] whitelistGrid = new String[land.length()];
		int[] probGrid = new int[land.length()];
		Resource[] resGrid = new Resource[land.length()];
		boolean[] harborGrid = new boolean[land.length()];
		List<int[]> placementBlacklist = new ArrayList<int[]>();
		
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
				resGrid[i] = Resource.findResourceByJson(landPiece.getString(RESOURCE));
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
		setLandGrid(landGrid);
		setLandGridWhitelists(whitelistGrid);
		setLandGridProbabilities(probGrid);
		setLandGridResources(resGrid);
		setPlacementBlacklists(placementBlacklist);
		
		return harborGrid;
	}
	
	private void setUnknownGridFromJson(JSONArray array) throws JSONException {
		if (array == null) {
			setUnknownGrid(new Point[] {});
			return;
		}
		
		Point[] unknownGrid = new Point[array.length()];
		
		for (int i = 0; i < array.length(); i++) {
			JSONObject unknownPiece = array.getJSONObject(i);
			unknownGrid[i] = new Point(unknownPiece.getInt(X), unknownPiece.getInt(Y));
		}
		
		setUnknownGrid(unknownGrid);
	}
	
	private void setLandWhitelists(JSONArray array) throws JSONException {
		if (array == null) {
			setLandResourceWhitelists(new HashMap<String, List<Resource>>());
			setLandProbabilityWhitelists(new HashMap<String, List<Integer>>());
			return;
		}
		
		Map<String, List<Resource>> resourceWhitelists = new HashMap<String, List<Resource>>();
		Map<String, List<Integer>> probsWhitelists = new HashMap<String, List<Integer>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject whitelist = array.getJSONObject(i);
			
			if (whitelist.getString(TYPE).equals("resource")) {
				List<Resource> resources = new ArrayList<Resource>();

				JSONArray resourcesJson = whitelist.getJSONArray(VALUE);
				for (int j = 0; j < resourcesJson.length(); j++) {
					resources.add(Resource.findResourceByJson(resourcesJson.getString(j)));
				}

				resourceWhitelists.put(whitelist.getString(KEY), resources);
			} else if (whitelist.getString(TYPE).equals("probability")) {
				List<Integer> probs = new ArrayList<Integer>();
				
				JSONArray probsJson = whitelist.getJSONArray(VALUE);
				for (int j = 0; j < probsJson.length(); j++) {
					probs.add(probsJson.getInt(j));
				}

				probsWhitelists.put(whitelist.getString(KEY), probs);
			}
		}
		
		setLandResourceWhitelists(resourceWhitelists);
		setLandProbabilityWhitelists(probsWhitelists);
	}
	
	private void setWaterGridFromJson(JSONArray water) throws JSONException {
		Point[] waterGrid = new Point[water.length()];
		for (int i = 0; i < water.length(); i++) {
			JSONObject waterPiece = water.getJSONObject(i);
			waterGrid[i] = new Point(waterPiece.getInt(X), waterPiece.getInt(Y));
		}
		setWaterGrid(waterGrid);
	}
	
	private void convertLandToWater(int num) {
		Point[] landGrid = getLandGrid();
		Point[] waterGrid = getWaterGrid();
		
		int newWaterLength = waterGrid.length + num;
		int newLandLength = landGrid.length - num;
		
		Point[] newWaterGrid = new Point[newWaterLength];
		
		List<Point> landList = new ArrayList<Point>();
		for (int i = 0; i < landGrid.length; i++) {
			landList.add(landGrid[i]);
		}
		List<Point> waterList = new ArrayList<Point>();
		for (int i = 0; i < waterGrid.length; i++) {
			waterList.add(waterGrid[i]);
		}
		
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
			theftOrder = new ArrayList<Integer>();

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
		
		setTheftOrder(theftOrder);
		setLandGrid(newLandGrid);
		setWaterGrid(newWaterGrid);
	}
	
	private void setResourcesFromJson(JSONArray resources) throws JSONException {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		
		Resource[] resourceArray = new Resource[resources.length()];
		for (int i = 0; i < resources.length(); i++) {
			String resource = resources.getString(i);
			resourceArray[i] = Resource.findResourceByJson(resource);
			
			if (counts.containsKey(resource)) {
				counts.put(resource, counts.get(resource) + 1);
			} else {
				counts.put(resource, 1);
			}
		}
		setAvailableResources(resourceArray);
		
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
		setLowResourceNumber(low);
		setHighResourceNumber(high);
	}
	
	private void setUnknownResourcesFromJson(JSONArray array) throws JSONException {
		if (array == null) {
			setAvailableUnknownResources(new Resource[] {});
			return;
		}
		
		Resource[] unknownResources = new Resource[array.length()];
		for (int i = 0; i < array.length(); i++) {
			String resource = array.getString(i);
			unknownResources[i] = Resource.findResourceByJson(resource);
		}
		
		setAvailableUnknownResources(unknownResources);
	}
}
