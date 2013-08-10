package com.nut.bettersettlers.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;
import android.util.Log;

import com.nut.bettersettlers.data.MapConsts.Resource;

public class JsonCatanMap extends CatanMap {
	private static final String LOG_TAG = JsonCatanMap.class.getSimpleName();
	
	private static final String NAME = "name";
	private static final String LAND = "land";
	private static final String ORDERED_LAND = "ordered_land";
	private static final String WATER = "water";
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
	
	public JsonCatanMap(InputStream is) {
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
		
		Log.i("XXX", toString());
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
				uberIndexes.add(duple);
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
				uberIndexes.add(duple);
			}
		}
		
		setLandNeighbors(lands);
		
		int[][] indexes = new int[uberIndexes.size()][];
		for (int i = 0; i < uberIndexes.size(); i++) {
			indexes[i] = uberIndexes.get(i);
		}
		setPlacementIndexes(indexes);
		
		// TODO(flynn): This probably won't work for custom maps
		// TODO(flynn): We need to get the rest of the placement indexes.
		for (int i = 0; i < getWaterGrid().length; i++) {
			Point water = getWaterGrid()[i];
			List<Point> landList = Arrays.asList(getLandGrid());
			
			List<Integer> smallList = new ArrayList<Integer>();
			for (int j = 0; j < 6; j++) {
				if (landList.contains(getNeighbor(water, j))) {
					smallList.add(landList.indexOf(getNeighbor(water, j)));
				}
			}
			int[] tuple = new int[smallList.size()];
			for (int j = 0; j < smallList.size(); j++) {
				tuple[j] = smallList.get(j);
			}
			Arrays.sort(tuple);
			uberNeighborList.add(tuple);
		}
		
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
	
	// super ugly, clean up
	private void setHarborLinesAndWaterNeighborsHelper(boolean[] landWithHarbors) {
		int[][] harborLines = new int[getWaterGrid().length][];
		int[][] neighborLines = new int[getWaterGrid().length][];
		for (int i = 0; i < getWaterGrid().length; i++) {
			Point water = getWaterGrid()[i];
			List<Point> landList = Arrays.asList(getLandGrid());
			
			Map<Integer, Boolean> has = new HashMap<Integer, Boolean>();
			for (int j = 0; j < 6; j++) {
				boolean close = false;
				if (landList.contains(getNeighbor(water, j))) {
					close = landWithHarbors[landList.indexOf(getNeighbor(water, j))];
				}
				has.put(j, close);
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
			for (int j = 0; j < 6; j++) {
				int k = (leftMost + j) % 6;
				if (has.get(k)) {
					neighborList.add(landList.indexOf(getNeighbor(water, k)));
				}
			}
			
			neighborLines[i] = new int[neighborList.size()];
			for (int j = 0; j < neighborList.size(); j++) {
				neighborLines[i][j] = neighborList.get(j);
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
		boolean[] harborGrid = new boolean[land.length()];
		
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
		}
		setLandGrid(landGrid);
		setLandGridWhitelists(whitelistGrid);
		setLandGridProbabilities(probGrid);
		
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
