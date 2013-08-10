package com.nut.bettersettlers.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.nut.bettersettlers.data.MapSpecs;
import com.nut.bettersettlers.data.MapSpecs.Harbor;
import com.nut.bettersettlers.data.MapSpecs.MapSize;
import com.nut.bettersettlers.data.MapSpecs.Resource;

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
public class PlacementLogic {
	// Prevent instantiation
	private PlacementLogic() {}

	private enum Trait {
		RICH("Wealthy - High probabilities"),
		RARE("Monopoly - Rare resource(s)"),
		FACTORY("Factory - 2:1 resource harbor"),
		TRADER("Trader - 3:1 harbor"),
		ROAD_BUILDER("Road Builder - Wood/Brick"),
		CITY_BUILDER("City Builder - Rock/Grain"),
		DEV_CARD_BUILDER("Dev Cards - Sheep/Rock/Grain"),
		NINJA("Ninja - Avoid the robber (no 6/8)"),
		VARIETY("Variety - 3 different resources");
		//NEAR_FACTORY("Good probability and a 2:1 harbor nearby for a resource"),
		//RENAISSANCE_MAN("Good representation of all 5 resources");

		private final String description;

		private Trait(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	private static void addValueToMap(Map<Integer, Integer> map, int key, int more) {
		if (map.get(key) == null) {
			map.put(key, more);
		} else {
			map.put(key, map.get(key) + more);
		}
	}

	private static int removeMax(Map<Integer, Integer> sums) {
		int maxIndex = -1;
		for (Map.Entry<Integer, Integer> entry : sums.entrySet()) {
			if (maxIndex == -1) {
				maxIndex = entry.getKey();
			} else if (entry.getValue() > sums.get(maxIndex)) {
				maxIndex = entry.getKey();
			}
		}
		sums.remove(maxIndex);
		return maxIndex;
	}

	/**
	 * Overall function returning the aggregate of all "best" placements.
	 * 
	 * @param currentMap Size of the map
	 * @param n What size list to return (pass in zero to get all returned)
	 * @param resources The list of ordered resources.
	 * @param probs The list of ordered probabilities.
	 * @param harbors The list of ordered harbors.
	 * @return A list of best intersections
	 */	
	public static LinkedHashMap<Integer, List<String>> getBestPlacements(MapSize currentMap, int n,
			ArrayList<Resource> resources, ArrayList<Integer> probs, ArrayList<Harbor> harbors) {
		LinkedHashMap<Integer, List<String>> initial =
			getAllBestPlacements(currentMap, resources, probs, harbors);
		LinkedHashMap<Integer, List<String>> set = new LinkedHashMap<Integer, List<String>>();

		// Check for zero passed in meaning they want all of them
		if (n == 0) {
			return initial;
		}
		for (Map.Entry<Integer, List<String>> entry : initial.entrySet()) {
			set.put(entry.getKey(), entry.getValue());
		}

		return set;
	}

	private static LinkedHashMap<Integer, List<String>> getAllBestPlacements(MapSize currentMap,
			ArrayList<Resource> resources, ArrayList<Integer> probs, ArrayList<Harbor> harbors) {
		LinkedHashMap<Integer, List<String>> set = new LinkedHashMap<Integer, List<String>>();
		Map<Integer, Integer> sums = new HashMap<Integer, Integer>();

		// 5pts x # of resources
		Map<Integer, Integer> highProbs = PlacementLogic.getHighProbabilities(currentMap, 0, probs, false);
		for (Map.Entry<Integer, Integer> entry : highProbs.entrySet()) {
			addValueToMap(sums, entry.getKey(), 5 * entry.getValue());
		}

		// 2 pts 25-50%, 4 pts 50-75%, 8 pts 75+%
		Map<Integer, Integer> rareResources = PlacementLogic.getRareResources(currentMap, 50, probs, resources);
		for (Map.Entry<Integer, Integer> entry : rareResources.entrySet()) {
			if (entry.getValue() > 25 && entry.getValue() < 50) {
				addValueToMap(sums, entry.getKey(), 2);
			} else if (entry.getValue() > 50 && entry.getValue() < 75) {
				addValueToMap(sums, entry.getKey(), 4);
			} else {
				addValueToMap(sums, entry.getKey(), 8);
			}
		}

		// 1 pt x resource for a factory
		Map<Integer, Integer> factories = PlacementLogic.getFactoryPlacements(currentMap, 0, probs, resources, harbors);
		for (Map.Entry<Integer, Integer> entry : factories.entrySet()) {
			addValueToMap(sums, entry.getKey(), entry.getValue());
		}

		// 3 pts for a trader
		Map<Integer, Integer> traders = PlacementLogic.getTraderPlacements(currentMap, 5, probs, resources, harbors);
		for (Map.Entry<Integer, Integer> entry : traders.entrySet()) {
			addValueToMap(sums, entry.getKey(), 4);
		}

		// 1 pt x resource for a road
		Map<Integer, Integer> roads = PlacementLogic.getRoadBuilder(currentMap, 3, probs, resources);
		for (Map.Entry<Integer, Integer> entry : roads.entrySet()) {
			addValueToMap(sums, entry.getKey(), entry.getValue());
		}

		// 1 pt x resource for a city
		Map<Integer, Integer> cities = PlacementLogic.getCityBuilder(currentMap, 3, probs, resources);
		for (Map.Entry<Integer, Integer> entry : cities.entrySet()) {
			addValueToMap(sums, entry.getKey(), entry.getValue());
		}

		// 1/2 pt x resource for a dev card
		Map<Integer, Integer> devCards = PlacementLogic.getDevCardBuilder(currentMap, 3, probs, resources);
		for (Map.Entry<Integer, Integer> entry : devCards.entrySet()) {
			addValueToMap(sums, entry.getKey(), entry.getValue() / 2);
		}

		// 3 pts for a ninja spot
		Map<Integer, Integer> ninjas = PlacementLogic.getHighProbabilities(currentMap, 10, probs, true);
		for (Map.Entry<Integer, Integer> entry : ninjas.entrySet()) {
			addValueToMap(sums, entry.getKey(), 3);
		}

		// 2x the lowest probability of the variety
		Map<Integer, Integer> varieties = PlacementLogic.getVarietyIndexes(currentMap, resources, probs);
		for (Map.Entry<Integer, Integer> entry : varieties.entrySet()) {
			addValueToMap(sums, entry.getKey(), 2 * entry.getValue());
		}

		// Add reasons
		Map<Integer, List<String>> reasons = new HashMap<Integer, List<String>>();
		for (int key : sums.keySet()) {
			reasons.put(key, new ArrayList<String>());
			if (highProbs.containsKey(key)) { reasons.get(key).add(Trait.RICH.getDescription()); }
			if (rareResources.containsKey(key)) { reasons.get(key).add(Trait.RARE.getDescription()); }
			if (factories.containsKey(key)) { reasons.get(key).add(Trait.FACTORY.getDescription()); }
			if (traders.containsKey(key)) { reasons.get(key).add(Trait.TRADER.getDescription()); }
			if (roads.containsKey(key)) { reasons.get(key).add(Trait.ROAD_BUILDER.getDescription()); }
			if (cities.containsKey(key)) { reasons.get(key).add(Trait.CITY_BUILDER.getDescription()); }
			if (devCards.containsKey(key)) { reasons.get(key).add(Trait.DEV_CARD_BUILDER.getDescription()); }
			if (ninjas.containsKey(key)) { reasons.get(key).add(Trait.NINJA.getDescription()); }
			if (varieties.containsKey(key)) { reasons.get(key).add(Trait.VARIETY.getDescription()); }
		}

		//Log.i("BS", "   RICH    : " + highProbs);
		//Log.i("BS", "   RARE    : " + rareResources);
		//Log.i("BS", "   FACTORY : " + factories);
		//Log.i("BS", "   TRADER  : " + traders);
		//Log.i("BS", "   ROAD    : " + roads);
		//Log.i("BS", "   CITY    : " + cities);
		//Log.i("BS", "   DEV CARD: " + devCards);
		//Log.i("BS", "   NINJA   : " + ninjas);
		//Log.i("BS", "   VARIETY : " + varieties);

		//Log.i("BS", "Map: " + sums);
		int sumSize = sums.size();
		for (int i = 0; i < sumSize; i++) {
			int index = removeMax(sums);
			// Don't count single resource hexes
			if (currentMap.getLandIntersections()[index].length >= 2) {
				set.put(index, reasons.get(index));
			}
		}

		//Log.i("BS", "List: " + set);

		return set;
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
	private static Map<Integer, Integer> getVarietyIndexes(MapSize currentMap,
			ArrayList<Resource> resources,	ArrayList<Integer> probs) {
		Map<Integer, Integer> set = new HashMap<Integer, Integer>();

		for (int i = 0; i < currentMap.getLandIntersections().length; i++) {
			int[] triplet = currentMap.getLandIntersections()[i];
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
					if (MapSpecs.PROBABILITY_MAPPING[prob] < lowestProb) {
						lowestProb = MapSpecs.PROBABILITY_MAPPING[prob];
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

	private static Map<Integer, Integer> getRoadBuilder(MapSize currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources) {
		ArrayList<Resource> goal = new ArrayList<Resource>();
		goal.add(Resource.WOOD);
		goal.add(Resource.CLAY);

		return getGoodCombinationProbabilities(currentMap, n, probs, resources, goal);
	}

	private static Map<Integer, Integer> getCityBuilder(MapSize currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources) {
		ArrayList<Resource> goal = new ArrayList<Resource>();
		goal.add(Resource.ROCK);
		goal.add(Resource.WHEAT);

		return getGoodCombinationProbabilities(currentMap, n, probs, resources, goal);
	}

	private static Map<Integer, Integer> getDevCardBuilder(MapSize currentMap, int n,
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
	private static Map<Integer, Integer> getGoodCombinationProbabilities(MapSize currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources, ArrayList<Resource> goal) {
		Map<Integer, Integer> set = new HashMap<Integer, Integer>();

		for (int i = 0; i < currentMap.getLandIntersections().length; i++) {
			int totalSum = 0;
			int[] triplet = currentMap.getLandIntersections()[i];

			Map<Resource, Integer> resourceSums = new HashMap<Resource, Integer>();
			for (Resource res : goal) {
				resourceSums.put(res, 0);
			}

			for (int trip : triplet) {
				Resource neighborResource = resources.get(trip);
				int neighborProb = probs.get(trip);

				if (resourceSums.containsKey(resources.get(trip))) {
					resourceSums.put(neighborResource,
							resourceSums.get(neighborResource) + MapSpecs.PROBABILITY_MAPPING[neighborProb]);
					totalSum += MapSpecs.PROBABILITY_MAPPING[neighborProb];
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
	private static Map<Integer, Integer> getHighProbabilities(MapSize currentMap, int n,
			ArrayList<Integer> probs, boolean ninja) {
		Map<Integer, Integer> set = new HashMap<Integer, Integer>();

		for (int i = 0; i < currentMap.getLandIntersections().length; i++) {
			int[] triplet = currentMap.getLandIntersections()[i];
			if (ninja) {
				boolean validTarget = true;
				for (int trip : triplet) {
					// Ignore 6/8s
					if (MapSpecs.PROBABILITY_MAPPING[probs.get(trip)] == 5) {
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
				sum += MapSpecs.PROBABILITY_MAPPING[probs.get(trip)];
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
	private static Map<Integer, Integer> getTraderPlacements(MapSize currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources, ArrayList<Harbor> harbors) {
		Map<Integer, Integer> set = new HashMap<Integer, Integer>();

		// Find out how many land only intersections first
		int landOnly = 0;		
		for (int i = 0; i < currentMap.getLandIntersections().length; i++) {
			int[] landIntersections = currentMap.getLandIntersections()[i];
			if (landIntersections.length == 3) {
				landOnly = i + 1; // 0 indexing
				continue;
			}

			Resource harborResource = harbors.get(i - landOnly).getResource();
			if (harborResource != Resource.DESERT) {
				continue; // Only 3:1 harbors
			}
			int landSum = 0;
			int[] landIndexes = currentMap.getWaterNeighbors()[i - landOnly];
			//if (landIndexes.length < 2) {
			//	continue; // We only want harbors with two resources
			//}
			for (int landIndex : landIndexes) {
				landSum += MapSpecs.PROBABILITY_MAPPING[probs.get(landIndex)];
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
	private static Map<Integer, Integer> getFactoryPlacements(MapSize currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources, ArrayList<Harbor> harbors) {
		Map<Integer, Integer> set = new HashMap<Integer, Integer>();

		// Find out how many land only intersections first
		int landOnly = 0;		
		for (int i = 0; i < currentMap.getLandIntersections().length; i++) {
			int[] landIntersections = currentMap.getLandIntersections()[i];
			if (landIntersections.length == 3) {
				landOnly = i + 1; // 0 indexing
				continue;
			}

			Resource harborResource = harbors.get(i - landOnly).getResource();
			if (harborResource == Resource.DESERT || harborResource == Resource.WATER) {
				continue; // Only harbors
			}
			int landSum = 0;
			int[] landIndexes = currentMap.getWaterNeighbors()[i - landOnly];
			//if (landIndexes.length < 2) {
			//	continue; // We only want harbors with two resources
			//}
			for (int landIndex : landIndexes) {
				Resource landResource = resources.get(landIndex);
				int landProb = MapSpecs.PROBABILITY_MAPPING[probs.get(landIndex)];

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
	private static Map<Integer, Integer> getRareResources(MapSize currentMap, int n,
			ArrayList<Integer> probs, ArrayList<Resource> resources) {
		Map<Integer, Integer> set = new HashMap<Integer, Integer>();

		Map<Resource, Integer> sums = new HashMap<Resource, Integer>();
		for (int i = 0; i < probs.size(); i++) {
			int prob = MapSpecs.PROBABILITY_MAPPING[probs.get(i)];
			Resource res = resources.get(i);

			if (sums.get(res) == null) {
				sums.put(res, 0);
			}
			sums.put(res, sums.get(res) + prob);
		}

		for (int i = 0; i < probs.size(); i++) {
			int prob = MapSpecs.PROBABILITY_MAPPING[probs.get(i)];
			Resource res = resources.get(i);

			if (res == Resource.DESERT) {
				continue;
			}

			int percent = prob * 100 / sums.get(res);
			if (percent >= n) {
				for (int tripletIndex : currentMap.getLandIntersectionIndexes()[i]) {
					set.put(tripletIndex, percent);
				}
			}			
		}

		return set;
	}
}
