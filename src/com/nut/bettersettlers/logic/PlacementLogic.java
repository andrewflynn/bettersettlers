package com.nut.bettersettlers.logic;

import static com.nut.bettersettlers.util.Consts.PROBABILITY_MAPPING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.Harbor;
import com.nut.bettersettlers.data.Resource;
import com.nut.bettersettlers.data.Trait;


/**
 * [][][] Single placement
 * 1. Very valuable (num of dots of all 3 resources)
 * 2. Rare resource (not a lot of resource on entire map)
 * 3. Good resource on a 2:1 harbor
 * 4. Two good resources with a 3:1 harbor
 * 5. Good combos (Wood/Clay, Grain/Rock) [doubly so if they have the same number]
 * 6. High probability w/o a 6 or 8 (low chance of robber)
 *
 * [][][] Double placement
 * 1. Two or three of the same good resource and on or near a 2:1 harbor
 * 2. Good representation of all resources
 */
public final class PlacementLogic {
	// Prevent instantiation
	private PlacementLogic() {}

	private static int removeMax(SparseIntArray sums) {
		int maxIndex = 0;
		for (int i = 1; i < sums.size(); i++) {
			if (sums.valueAt(i) > sums.valueAt(maxIndex)) {
				maxIndex = i;
			}
		}
		int ret = sums.keyAt(maxIndex);
		sums.removeAt(maxIndex);
		return ret;
	}

	private static void addValue(SparseIntArray array, int key, int more) {
		array.put(key, array.get(key) + more);
	}

	/**
	 * Overall function returning the aggregate of all "best" placements.
	 * 
	 * @param currentMap Size of the map
	 * @param resources The list of ordered resources.
	 * @param probs The list of ordered probabilities.
	 * @param harbors The list of ordered harbors.
	 * @return A list of best intersections
	 */	
	public static Pair<ArrayList<Integer>, SparseArray<ArrayList<String>>> getBestPlacements(CatanMap currentMap,
			ArrayList<Resource> resources, ArrayList<Integer> probs, ArrayList<Harbor> harbors) {
		ArrayList<Integer> order = new ArrayList<Integer>();
		SparseArray<ArrayList<String>> set = new SparseArray<ArrayList<String>>();
		SparseIntArray sums = new SparseIntArray();

		// 5pts x # of resources
		SparseIntArray highProbs = PlacementLogic.getHighProbabilities(currentMap, 0, probs, false);
		for (int i = 0; i < highProbs.size(); i++) {
			addValue(sums, highProbs.keyAt(i), 5 * highProbs.valueAt(i));
		}

		// 2 pts 25-50%, 4 pts 50-75%, 8 pts 75+%
		SparseIntArray rareResources = PlacementLogic.getRareResources(currentMap, 50, probs, resources);
		for (int i = 0; i < rareResources.size(); i++) {
			int key = rareResources.keyAt(i);
			int value = rareResources.valueAt(i);
			
			if (value > 25 && value < 50) {
				addValue(sums, key, 2);
			} else if (value > 50 && value < 75) {
				addValue(sums, key, 4);
			} else {
				addValue(sums, key, 8);
			}
		}

		// 1 pt x resource for a factory
		SparseIntArray factories = PlacementLogic.getFactoryPlacements(currentMap, 0, probs, resources, harbors);
		for (int i = 0; i < factories.size(); i++) {
			addValue(sums, factories.keyAt(i), factories.valueAt(i));
		}

		// 3 pts for a trader
		SparseIntArray traders = PlacementLogic.getTraderPlacements(currentMap, 5, probs, resources, harbors);
		for (int i = 0; i < traders.size(); i++) {
			addValue(sums, traders.keyAt(i), 4);
		}

		// 1 pt x resource for a road
		SparseIntArray roads = PlacementLogic.getRoadBuilder(currentMap, 3, probs, resources);
		for (int i = 0; i < roads.size(); i++) {
			addValue(sums, roads.keyAt(i), roads.valueAt(i));
		}

		// 1 pt x resource for a city
		SparseIntArray cities = PlacementLogic.getCityBuilder(currentMap, 3, probs, resources);
		for (int i = 0; i < cities.size(); i++) {
			addValue(sums, cities.keyAt(i), cities.valueAt(i));
		}

		// 1/2 pt x resource for a dev card
		SparseIntArray devCards = PlacementLogic.getDevCardBuilder(currentMap, 3, probs, resources);
		for (int i = 0; i < devCards.size(); i++) {
			addValue(sums, devCards.keyAt(i), devCards.valueAt(i) / 2);
		}

		// 3 pts for a ninja spot
		SparseIntArray ninjas = PlacementLogic.getHighProbabilities(currentMap, 10, probs, true);
		for (int i = 0; i < ninjas.size(); i++) {
			addValue(sums, ninjas.keyAt(i), 3);
		}

		// 2x the lowest probability of the variety
		SparseIntArray varieties = PlacementLogic.getVarietyIndexes(currentMap, resources, probs);
		for (int i = 0; i < varieties.size(); i++) {
			addValue(sums, varieties.keyAt(i), 2 * varieties.valueAt(i));
		}

		// Add reasons
		SparseArray<ArrayList<String>> reasons = new SparseArray<ArrayList<String>>();
		for (int i = 0; i < sums.size(); i++) {
			int key = sums.keyAt(i);
			reasons.put(key, new ArrayList<String>());
			if (highProbs.get(key) != 0) { reasons.get(key).add(Trait.RICH); }
			if (rareResources.get(key) != 0) { reasons.get(key).add(Trait.RARE); }
			if (factories.get(key) != 0) { reasons.get(key).add(Trait.FACTORY); }
			if (traders.get(key) != 0) { reasons.get(key).add(Trait.TRADER); }
			if (roads.get(key) != 0) { reasons.get(key).add(Trait.ROAD_BUILDER); }
			if (cities.get(key) != 0) { reasons.get(key).add(Trait.CITY_BUILDER); }
			if (devCards.get(key) != 0) { reasons.get(key).add(Trait.DEV_CARD_BUILDER); }
			if (ninjas.get(key) != 0) { reasons.get(key).add(Trait.NINJA); }
			if (varieties.get(key) != 0) { reasons.get(key).add(Trait.VARIETY); }
		}

		//BetterLog.i("   RICH    : " + highProbs);
		//BetterLog.i("   RARE    : " + rareResources);
		//BetterLog.i("   FACTORY : " + factories);
		//BetterLog.i("   TRADER  : " + traders);
		//BetterLog.i("   ROAD    : " + roads);
		//BetterLog.i("   CITY    : " + cities);
		//BetterLog.i("   DEV CARD: " + devCards);
		//BetterLog.i("   NINJA   : " + ninjas);
		//BetterLog.i("   VARIETY : " + varieties);

		//BetterLog.i("Map: " + sums);
		int sumSize = sums.size();
		for (int i = 0; i < sumSize; i++) {
			int index = removeMax(sums);
			// Don't count single resource hexes
			if (currentMap.landIntersections[index].length >= 2) {
				order.add(index);
				set.put(index, reasons.get(index));
			}
		}

		//BetterLog.i("List: " + set);

		return Pair.create(order, set);
	}

	/**
	 * Takes in a list of resources and returns which intersections have 3 different
	 * resources (no two of the same resource at an intersection).
	 * 
	 * @param currentMap Size of the map
	 * @param resources The list of ordered resources.
	 * @param probs The list of ordered probabilities
	 * @return A list of indexes mapped to the resource with the least probability
	 */
	private static SparseIntArray getVarietyIndexes(CatanMap currentMap,
			ArrayList<Resource> resources,	ArrayList<Integer> probs) {
		SparseIntArray set = new SparseIntArray();

		for (int i = 0; i < currentMap.landIntersections.length; i++) {
			if (currentMap.placementIndexes[i] == null || currentMap.placementIndexes[i].length == 0) {
				continue;
			}
			
			int[] triplet = currentMap.landIntersections[i];
			// Don't count shoreline
			if (triplet.length < 3) {
				continue;
			}

			Set<Resource> seen = new HashSet<Resource>();
			boolean containsDuplicates = false;
			int lowestProb = Integer.MAX_VALUE;
			for (int trip : triplet) {
				Resource res = resources.get(trip);
				int prob = probs.get(trip);

				// Deserts should not be considered for variety
				if (res == Resource.DESERT) {
					containsDuplicates = true;
					break;
				}

				if (seen.contains(res)) {
					containsDuplicates = true;
					break;
				} else {
					if (prob >= 0 && PROBABILITY_MAPPING[prob] < lowestProb) {
						lowestProb = PROBABILITY_MAPPING[prob];
					}
					seen.add(resources.get(trip));
				}
			}

			if (!containsDuplicates) {
				set.put(i, lowestProb);
			}
		}

		return set;
	}

	private static SparseIntArray getRoadBuilder(CatanMap currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources) {
		ArrayList<Resource> goal = new ArrayList<Resource>();
		goal.add(Resource.WOOD);
		goal.add(Resource.CLAY);

		return getGoodCombinationProbabilities(currentMap, n, probs, resources, goal);
	}

	private static SparseIntArray getCityBuilder(CatanMap currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources) {
		ArrayList<Resource> goal = new ArrayList<Resource>();
		goal.add(Resource.ROCK);
		goal.add(Resource.WHEAT);

		return getGoodCombinationProbabilities(currentMap, n, probs, resources, goal);
	}

	private static SparseIntArray getDevCardBuilder(CatanMap currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources) {
		ArrayList<Resource> goal = new ArrayList<Resource>();
		goal.add(Resource.ROCK);
		goal.add(Resource.WHEAT);
		goal.add(Resource.SHEEP);

		return getGoodCombinationProbabilities(currentMap, n, probs, resources, goal);
	}

	/**
	 * Takes in a list of established probabilities and resources for a map and returns
	 * a list of all intersections that have decent (at least n) combination of the
	 * resources provided.
	 * 
	 * @param currentMap Size of the map
	 * @param n How much of each resource it needs to be returned
	 * @param probs The list of ordered probabilities.
	 * @param resources The list of ordered resources.
	 * @param goal The resources needed for this good combination.
	 * @return A list of indexes to the sum of those resources
	 */
	private static SparseIntArray getGoodCombinationProbabilities(CatanMap currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources, ArrayList<Resource> goal) {
		SparseIntArray set = new SparseIntArray();

		for (int i = 0; i < currentMap.landIntersections.length; i++) {
			if (currentMap.placementIndexes[i] == null || currentMap.placementIndexes[i].length == 0) {
				continue;
			}
			
			int totalSum = 0;
			int[] triplet = currentMap.landIntersections[i];

			Map<Resource, Integer> resourceSums = new HashMap<Resource, Integer>();
			for (Resource res : goal) {
				resourceSums.put(res, 0);
			}

			for (int trip : triplet) {
				Resource neighborResource = resources.get(trip);
				int neighborProb = probs.get(trip);

				if (resourceSums.containsKey(resources.get(trip)) && neighborProb >= 0) {
					// Skip no prob resources
					resourceSums.put(neighborResource,
							resourceSums.get(neighborResource) + PROBABILITY_MAPPING[neighborProb]);
					totalSum += PROBABILITY_MAPPING[neighborProb];
				}
			}

			boolean hasEnough = true;
			for (Map.Entry<Resource, Integer> resourceSum : resourceSums.entrySet()) {
				if (resourceSum.getValue() < n) {
					hasEnough = false;
					break;
				}
			}

			if (hasEnough) {
				set.put(i, totalSum);
			}
		}

		return set;
	}

	/**
	 * Takes in a list of established probabilities for a map and returns a mapping
	 * of the top probabilities that are at least as big as n when adding up the dots.  Map 
	 * is keyed by their index in the ordered probabilities and value is how many dots.
	 * 
	 * @param currentMap Size of the map
	 * @param n The value of how many dots a probability must have.
	 * @param probs The list of ordered probabilities
	 * @param ninja Option as to whether to ignore intersections with 6/8s.
	 * @return A map of indexes to their sum of dots
	 */
	private static SparseIntArray getHighProbabilities(CatanMap currentMap, int n,
			ArrayList<Integer> probs, boolean ninja) {
		SparseIntArray set = new SparseIntArray();

		for (int i = 0; i < currentMap.landIntersections.length; i++) {
			if (currentMap.placementIndexes[i] == null || currentMap.placementIndexes[i].length == 0) {
				continue;
			}
			
			int[] triplet = currentMap.landIntersections[i];
			if (ninja) {
				boolean validTarget = true;
				for (int trip : triplet) {
					// Ignore 6/8s
					if (probs.get(trip) >= 0 && PROBABILITY_MAPPING[probs.get(trip)] == 5) {
						validTarget = false;
						break;
					}
				}
				if (!validTarget) {
					continue;
				}
			}

			int sum = 0;
			for (int trip : triplet) {
				// Skip no prob resources
				if (probs.get(trip) >= 0) {
					sum += PROBABILITY_MAPPING[probs.get(trip)];
				}
			}

			if (sum >= n) {
				set.put(i, sum);
			}
		}

		return set;
	}

	/**
	 * Takes in a full map and returns a mapping of the top placements that have
	 * good probability of resources and are on a 3:1.
	 * 
	 * @param currentMap Size of the map
	 * @param n The number of probability dots to be good enough
	 * @param probs The list of ordered probabilities
	 * @param resources The list of ordered resources
	 * @param harbors The list of ordered harbors
	 * @return A map of indexes to their number of dots
	 */
	private static SparseIntArray getTraderPlacements(CatanMap currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources, ArrayList<Harbor> harbors) {
		SparseIntArray set = new SparseIntArray();

		// Find out how many land only intersections first
		int landOnly = 0;		
		for (int i = 0; i < currentMap.landIntersections.length; i++) {
			if (currentMap.placementIndexes[i] == null || currentMap.placementIndexes[i].length == 0) {
				continue;
			}
			
			int[] landIntersections = currentMap.landIntersections[i];
			if (landIntersections.length == 3) {
				landOnly = i + 1; // 0 indexing
				continue;
			}
			
			// Avoid NPE
			if ((i - landOnly) >= harbors.size()) {
				continue;
			}

			Resource harborResource = harbors.get(i - landOnly).resource;
			if (harborResource != Resource.DESERT) {
				continue; // Only 3:1 harbors
			}
			int landSum = 0;
			int[] landIndexes = currentMap.waterNeighbors[i - landOnly];
			//if (landIndexes.length < 2) {
			//	continue; // We only want harbors with two resources
			//}
			for (int landIndex : landIndexes) {
				// Skip no prob resources
				if (probs.get(landIndex) < 0) {
					continue;
				}
				landSum += PROBABILITY_MAPPING[probs.get(landIndex)];
			}
			if (landSum >= n) {
				set.put(i, landSum);
			}
		}		

		return set;
	}

	/**
	 * Takes in a full map and returns a mapping of the top placements that have
	 * good probability of a resource and are on a 2:1.
	 * 
	 * @param currentMap Size of the map
	 * @param n The number of probability dots to be good enough
	 * @param probs The list of ordered probabilities
	 * @param resources The list of ordered resources
	 * @param harbors The list of ordered harbors
	 * @return A map of indexes to their number of dots
	 */
	private static SparseIntArray getFactoryPlacements(CatanMap currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources, ArrayList<Harbor> harbors) {
		SparseIntArray set = new SparseIntArray();

		// Find out how many land only intersections first
		int landOnly = 0;		
		for (int i = 0; i < currentMap.landIntersections.length; i++) {
			if (currentMap.placementIndexes[i] == null || currentMap.placementIndexes[i].length == 0) {
				continue;
			}
			
			int[] landIntersections = currentMap.landIntersections[i];
			if (landIntersections.length == 3) {
				landOnly = i + 1; // 0 indexing
				continue;
			}
			
			// Avoid NPE
			if ((i - landOnly) >= harbors.size()) {
				continue;
			}
			
			Resource harborResource = harbors.get(i - landOnly).resource;
			if (harborResource == Resource.DESERT || harborResource == Resource.WATER) {
				continue; // Only harbors
			}
			int landSum = 0;
			int[] landIndexes = currentMap.waterNeighbors[i - landOnly];
			//if (landIndexes.length < 2) {
			//	continue; // We only want harbors with two resources
			//}
			for (int landIndex : landIndexes) {
				// Skip no prob resources
				if (probs.get(landIndex) < 0) {
					continue;
				}
				Resource landResource = resources.get(landIndex);
				int landProb = PROBABILITY_MAPPING[probs.get(landIndex)];

				if (landResource == harborResource) {
					landSum += landProb;
				}
			}
			if (landSum >= n) {
				set.put(i, landSum);
			}
		}		

		return set;
	}

	/**
	 * Takes in a list of established probabilities/resources for a map and returns a mapping
	 * of the top resources that are at least n% of the full resource on the map.  Map 
	 * is keyed by their index in the ordered probabilities and value is what percentage.
	 * 
	 * @param currentMap Size of the map
	 * @param n The percentage of which the resource must be of the full value
	 * @param probs The list of ordered probabilities
	 * @param resources The list of ordered resources
	 * @return A map of indexes to their percentage
	 */
	private static SparseIntArray getRareResources(CatanMap currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources) {
		SparseIntArray set = new SparseIntArray();

		Map<Resource, Integer> sums = new HashMap<Resource, Integer>();
		for (int i = 0; i < probs.size(); i++) {
			// Skip no prob resources
			if (probs.get(i) < 0) {
				continue;
			}
			int prob = PROBABILITY_MAPPING[probs.get(i)];
			Resource res = resources.get(i);

			if (sums.get(res) == null) {
				sums.put(res, 0);
			}
			sums.put(res, sums.get(res) + prob);
		}

		for (int i = 0; i < probs.size(); i++) {
			// Skip no prob resources
			if (probs.get(i) < 0) {
				continue;
			}
			int prob = PROBABILITY_MAPPING[probs.get(i)];
			Resource res = resources.get(i);

			if (res == Resource.DESERT) {
				continue;
			}

			int percent = prob * 100 / sums.get(res);
			if (percent >= n) {
				for (int tripletIndex : currentMap.landIntersectionIndexes[i]) {
					if (currentMap.placementIndexes[tripletIndex] != null && currentMap.placementIndexes[tripletIndex].length != 0) {
						set.put(tripletIndex, percent);
					}
				}
			}			
		}

		return set;
	}
}
