package com.nut.bettersettlers.logic;

import static com.nut.bettersettlers.data.MapConsts.PROBABILITY_MAPPING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.nut.bettersettlers.data.CatanMap;
import com.nut.bettersettlers.data.MapConsts.Harbor;
import com.nut.bettersettlers.data.MapConsts.MapType;
import com.nut.bettersettlers.data.MapConsts.NumberOfResource;
import com.nut.bettersettlers.data.MapConsts.Resource;


public class MapLogic {
	// Prevent instantiation
	private MapLogic() {}
	
	private static final Random RAND = new Random();
	
	protected static ArrayList<Integer> getTesterProbabilities() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(9);
		ret.add(11);
		ret.add(4);
		ret.add(6);
		ret.add(12);
		ret.add(5);
		ret.add(10);
		ret.add(8);
		ret.add(2);
		ret.add(10);
		ret.add(9);
		ret.add(11);
		ret.add(3);
		ret.add(6);
		ret.add(3);
		ret.add(5);
		ret.add(4);
		ret.add(0);
		ret.add(8);

		//System.out.println("Finished: " + ret);
		return ret;
	}
	
	public static ArrayList<Integer> getProbabilities(CatanMap currentMap, MapType currentType, ArrayList<Resource> resources) {
		switch(currentType) {
			case TRADITIONAL:
				return null; // Doesn't make any sense
			case FAIR:
				return getOrderedProbabilities(currentMap, resources);
			case RANDOM:
				return getRandomProbabilities(currentMap, resources);
			default:
				return null;
		}
	}
	
	public static ArrayList<Integer> getProbabilities(CatanMap currentMap, MapType currentType) {
		switch(currentType) {
			case TRADITIONAL:
				return getTraditionalProbabilities(currentMap);
			case FAIR:
				return getOrderedProbabilities(currentMap);
			case RANDOM:
				return getRandomProbabilities(currentMap);
			default:
				return null;
		}
	}
	
	private static ArrayList<Integer> getTraditionalProbabilities(CatanMap currentMap) {
		ArrayList<Integer> avail = initOrderedProbabilitiesNoZeros(currentMap);
		
		// Insert random desert locations
		while (avail.size() < currentMap.getLandGrid().length) {
			avail.add(RAND.nextInt(avail.size()), 0);
		}
		
		// Map it to the left to right top to bottom we expect
		int[] set = new int[avail.size()];
		for (int i : currentMap.getLandGridOrder()) {
			set[i] = avail.remove(0);
		}
		
		ArrayList<Integer> setList = new ArrayList<Integer>();
		for (int i : set) {
			setList.add(i);
		}

		return setList;
	}
	
	private static ArrayList<Integer> getRandomProbabilities(CatanMap currentMap) {
		ArrayList<Integer> set = new ArrayList<Integer>();
		ArrayList<Integer> avail = initProbabilities(currentMap);
		while (!avail.isEmpty()) {
			// If we have an assigned prob, use it
			if (currentMap.getLandGridProbabilities()[set.size()] != Integer.MAX_VALUE) {
				//System.out.println(" pulling special " + counter);
				int nextProb = currentMap.getLandGridProbabilities()[set.size()];
				avail.remove((Integer) nextProb);
				set.add(nextProb);
			} else {
				set.add(avail.remove(RAND.nextInt(avail.size())));
			}
		}
		return set;
	}
	
	public static ArrayList<Integer> getUnknownProbabilities(CatanMap currentMap) {
		ArrayList<Integer> set = new ArrayList<Integer>();
		ArrayList<Integer> avail = new ArrayList<Integer>();
		for (int i : currentMap.getAvailableUnknownProbabilities()) {
			avail.add(i);
		}
		
		while (!avail.isEmpty()) {
			set.add(avail.remove(RAND.nextInt(avail.size())));
		}
		return set;
	}
	
	private static ArrayList<Integer> getRandomProbabilities(CatanMap currentMap, ArrayList<Resource> resources) {
		ArrayList<Integer> set = new ArrayList<Integer>();
		ArrayList<Integer> avail = initProbabilitiesNoZeros(currentMap);
		int counter = 0;
		while (!avail.isEmpty()) {
			// If we have an assigned prob, use it
			if (currentMap.getLandGridProbabilities()[counter] != Integer.MAX_VALUE) {
				//System.out.println(" pulling special " + counter);
				int nextProb = currentMap.getLandGridProbabilities()[counter];
				avail.remove((Integer) nextProb);
				set.add(nextProb);
			} else {
				if (resources.get(counter) == Resource.DESERT) {
					set.add(0);
				} else {
					set.add(avail.remove(RAND.nextInt(avail.size())));
				}
			}
			//System.out.println("Check " + counter + ", set " + set.get(counter));
			counter++;
		}
		
		// Check for deserts at the end
		while (set.size() < resources.size()) {
			set.add(0);
		}
		
		return set;
	}
	
	private static ArrayList<Integer> getOrderedProbabilities(CatanMap currentMap) {
		// Contains the probabilities already set
		ArrayList<Integer> set = new ArrayList<Integer>();
		// Contains the probabilities already consumed in this iteration
		ArrayList<Integer> tried = new ArrayList<Integer>();
		// Contains the probabilities yet to be consumed
		ArrayList<Integer> avail = initProbabilities(currentMap);
		boolean placedRecently = false;
		
		while (!tried.isEmpty() || !avail.isEmpty()) {
			if (placedRecently && !tried.isEmpty()) {
				avail.addAll(tried);
				tried.clear();
				placedRecently = false;
			}
			// Game over: avail is empty and we still have un-set probabilities. Start over.
			if (avail.isEmpty()) {
				//System.out.println("Restart: " + set);
				set.clear();
				tried.clear();
				avail = initProbabilities(currentMap);
				placedRecently = false;
			} else {
				//System.out.println("  Set      : " + set);
				//System.out.println("  Tried    : " + tried);
				//System.out.println("  Avail    : " + avail);
				
				String whitelistName = currentMap.getLandGridWhitelists()[set.size()];
				
				int nextProb;
				if (currentMap.getLandGridProbabilities()[set.size()] != Integer.MAX_VALUE) {
					// If we have an assigned prob, use it
					nextProb = currentMap.getLandGridProbabilities()[set.size()];
					avail.remove((Integer) nextProb);
					set.add(nextProb);
					placedRecently = true;
					continue;
				} else if (whitelistName != null
						&& currentMap.getLandProbabilityWhitelists().containsKey(whitelistName)) {
					// If we have a prob whitelist, pull one from it.
					// Grab a random one
					List<Integer> probChoices = currentMap.getLandProbabilityWhitelists().get(whitelistName);
					nextProb = -1;
					while (nextProb == -1) {
						int maybeNextProb = probChoices.get(RAND.nextInt(probChoices.size()));
						if (avail.contains(maybeNextProb)) {
							nextProb = maybeNextProb;
							avail.remove((Integer) maybeNextProb);
						}
					}
				} else {
					nextProb = avail.remove(RAND.nextInt(avail.size()));
				}

				//System.out.println("  Consuming: " + nextProb);
				boolean canPlaceHere = true;
				
				// If it's a desert (picking a 0), make sure the whitelist (if it has one) has desert
				if (nextProb == 0 && whitelistName != null
						&& currentMap.getLandResourceWhitelists().containsKey(whitelistName)
						&& !currentMap.getLandResourceWhitelists().get(whitelistName).contains(Resource.DESERT)) {
					canPlaceHere = false;
				}
				
				// Check all intersections that contain this piece
				for (int tripletIndex : currentMap.getLandIntersectionIndexes()[set.size()]) {
					int[] triplet = currentMap.getLandIntersections()[tripletIndex];
					if (triplet.length < 3) {
						continue; // Skip shoreline
					}
					
					ArrayList<Integer> tempTriplets = new ArrayList<Integer>();
					tempTriplets.add(nextProb);
					boolean goAhead = true;
					for (int trip : triplet) {
						if (trip < set.size()) {
							int num = set.get(trip);
							if (num < 0) {
								goAhead = false; // Skip empty probs
								break;
							}
							tempTriplets.add(num);
						}
					}
					if (!goAhead) {
						continue;
					}
					
					if (!noDuplicates(currentMap, tempTriplets)) {
						canPlaceHere = false;
					} else if (tempTriplets.size() == 3) {
						if (tempTriplets.contains(0)) {
							// Ignore if it has more than one desert (only for customs)
							tempTriplets.remove((Integer) 0);
							if (!tempTriplets.contains(0)) {
								tempTriplets.add(0);
								// Has a desert has to be 4<=x<=8
								if (sumProbability(tempTriplets) < 4 || sumProbability(tempTriplets) > 8) {
									canPlaceHere = false;
								}
							}
						} else {
							if (sumProbability(tempTriplets) < 5 || sumProbability(tempTriplets) > 11) {
								canPlaceHere = false;
							}
						}
					}
				}
				if (canPlaceHere) {
					set.add(nextProb);
					placedRecently = true;
				} else {
					tried.add(nextProb);
				}				
			}
		}

		//System.out.println("Finished: " + set);
		return set;
	}
	
	private static ArrayList<Integer> getOrderedProbabilities(CatanMap currentMap, ArrayList<Resource> resources) {
		// Contains a mapping to which resources have what so far
		Map<Resource, ArrayList<Integer>> resourceMap = initResourceMap(currentMap);
		// Contains the resources already set
		ArrayList<Integer> set = new ArrayList<Integer>();
		// Contains the resources already consumed in this iteration
		ArrayList<Integer> tried = new ArrayList<Integer>();
		// Contains the (non desert) resources yet to be consumed
		ArrayList<Integer> avail = initProbabilitiesNoZeros(currentMap);
		boolean placedRecently = false;
		
		while (!tried.isEmpty() || !avail.isEmpty()) {
			if (placedRecently && !tried.isEmpty()) {
				avail.addAll(tried);
				tried.clear();
				placedRecently = false;
			}
			// Game over: avail is empty and we still have un-set probabilities. Start over.
			if (avail.isEmpty()) {
				//System.out.println("Restart: " + set);
				resourceMap = initResourceMap(currentMap);
				set.clear();
				tried.clear();
				avail = initProbabilitiesNoZeros(currentMap);
				placedRecently = false;
			} else {
				int nextIndex = set.size();
				Resource nextResource = resources.get(nextIndex);
				String whitelistName = currentMap.getLandGridWhitelists()[set.size()];
				
				// Consume
				// Always place a 0 on a desert
				int nextProb;
				if (currentMap.getLandGridProbabilities()[set.size()] != Integer.MAX_VALUE) {
					// If we have an assigned prob, use it
					nextProb = currentMap.getLandGridProbabilities()[set.size()];
					avail.remove((Integer) nextProb);
					set.add(nextProb);
					placedRecently = true;
					continue;
				} else if (nextResource == Resource.DESERT) {
					set.add(0);
					placedRecently = true;
					continue;
				} else if (whitelistName != null
						&& currentMap.getLandProbabilityWhitelists().containsKey(whitelistName)) {
					// If we have a prob whitelist, pull one from it.
					// Grab a random one
					List<Integer> probChoices = currentMap.getLandProbabilityWhitelists().get(whitelistName);
					nextProb = -1;
					while (nextProb == -1) {
						int maybeNextProb = probChoices.get(RAND.nextInt(probChoices.size()));
						if (avail.contains(maybeNextProb)) {
							nextProb = maybeNextProb;
							avail.remove((Integer) maybeNextProb);
						}
					}
				} else {
					nextProb = avail.remove(RAND.nextInt(avail.size()));
				}
				
				//System.out.println("  Set: " + set);
				//System.out.println("  Avail: " + avail);
				//System.out.println("  Tried: " + tried);
				//System.out.println("  Consuming: " + nextResource);
				boolean canPlaceHere = true;
				
				// If it's a desert (picking a 0), make sure the whitelist (if it has one) has desert
				if (nextProb == 0 && whitelistName != null
						&& currentMap.getLandResourceWhitelists().containsKey(whitelistName)
						&& !currentMap.getLandResourceWhitelists().get(whitelistName).contains(Resource.DESERT)) {
					canPlaceHere = false;
				}

				// Check all intersections that contain this piece
				for (int tripletIndex : currentMap.getLandIntersectionIndexes()[set.size()]) {
					int[] triplet = currentMap.getLandIntersections()[tripletIndex];
					if (triplet.length < 3) {
						continue; // Skip shoreline
					}
					
					ArrayList<Integer> tempTriplets = new ArrayList<Integer>();
					tempTriplets.add(nextProb);
					boolean goAhead = true;
					for (int trip : triplet) {
						if (trip < set.size()) {
							int num = set.get(trip);
							if (num < 0) {
								goAhead = false; // Skip empty probs
								break;
							}
							tempTriplets.add(num);
						}
					}
					if (!goAhead) {
						continue;
					}
					
					if (!noDuplicates(currentMap, tempTriplets)) {
						canPlaceHere = false;
					} else if (tempTriplets.size() == 3) {
						if (tempTriplets.contains(0)) {
							// Ignore if it has more than one desert (only for customs)
							tempTriplets.remove((Integer) 0);
							if (!tempTriplets.contains(0)) {
								tempTriplets.add(0);
								// Has a desert has to be 4<=x<=8
								if (sumProbability(tempTriplets) < 4 || sumProbability(tempTriplets) > 8) {
									canPlaceHere = false;
								}
							}
						} else {
							if (sumProbability(tempTriplets) < 5 || sumProbability(tempTriplets) > 11) {
								canPlaceHere = false;
							}
						}
					}
				}
				
				// If this number is already used by the same resource
				if (resourceMap.get(nextResource).contains(nextProb)) {
					canPlaceHere = false;
				}
				

				// If this is the last resource, check the probs of this resource
				NumberOfResource numOfResource = nextResource.getNumOfResource();
				ArrayList<Integer> tmpProbs = new ArrayList<Integer>();
				tmpProbs.addAll(resourceMap.get(nextResource));
				tmpProbs.add(nextProb);
				if (numOfResource == NumberOfResource.LOW
						&& tmpProbs.size() == currentMap.getLowResourceNumber()) {
					if (sumProbability(tmpProbs) < 3*currentMap.getLowResourceNumber()
							|| sumProbability(tmpProbs) > 4*currentMap.getLowResourceNumber()) {
						canPlaceHere = false;
					}
					if (!isBalanced(tmpProbs)) {
						canPlaceHere = false;
					}
				} else if (numOfResource == NumberOfResource.HIGH
						&& tmpProbs.size() == currentMap.getHighResourceNumber()) {
					if (sumProbability(tmpProbs) < 3*currentMap.getHighResourceNumber()
							|| sumProbability(tmpProbs) > 4*currentMap.getHighResourceNumber()) {
						canPlaceHere = false;
					}
				}
				
				if (canPlaceHere) {
					set.add(nextProb);
					resourceMap.get(nextResource).add(nextProb);
					placedRecently = true;
				} else {
					tried.add(nextProb);
				}				
			}
		}
		
		// Check for deserts at the end
		while (set.size() < resources.size()) {
			set.add(0);
		}

		//System.out.println("Finished: " + set);
		return set;
	}
	
	public static ArrayList<Resource> getResources(CatanMap currentMap, MapType currentType, ArrayList<Integer> probs) {
		switch(currentType) {
			case TRADITIONAL:
				// Normal actually uses random resource distribution (prob setup determines "normalness")
				return getRandomResources(currentMap, probs);
			case FAIR:
				return getOrderedResources(currentMap, probs);
			case RANDOM:
				return getRandomResources(currentMap, probs);
			default:
				return null;
		}
	}
	
	public static ArrayList<Resource> getUnknowns(CatanMap currentMap, ArrayList<Integer> probs) {
		ArrayList<Resource> set = new ArrayList<Resource>();
		ArrayList<Resource> avail = new ArrayList<Resource>();
		for (Resource resource : currentMap.getAvailableUnknownResources()) {
			avail.add(resource);
		}
		
		while (!avail.isEmpty()) {
			int nextProb = probs.get(set.size());
			
			if (nextProb == 0) {
				avail.remove(Resource.DESERT);
				set.add(Resource.DESERT);
			} else if (nextProb == -1) {
				avail.remove(Resource.WATER);
				set.add(Resource.WATER);
			} else {
				Resource next = avail.remove(RAND.nextInt(avail.size()));
				if (next == Resource.DESERT || next == Resource.WATER){
					avail.add(next); // put it back
				} else {
					set.add(next);
				}
			}
		}
		
		return set;
	}
	
	private static ArrayList<Resource> getRandomResources(CatanMap currentMap, ArrayList<Integer> probs) {
		ArrayList<Resource> set = new ArrayList<Resource>();
		ArrayList<Resource> avail = initAvailResourcesNoDesert(currentMap);
		Map<String, List<Resource>> allowedWhitelists = getAllowedWhitelists(currentMap);
		
		while (!avail.isEmpty()) {
			int nextIndex = set.size();
			int nextProb = probs.get(nextIndex);

			String whitelistName = currentMap.getLandGridWhitelists()[nextIndex];
			
			// Consume
			// Always place a random resource on a -1
			// Always place a desert on a 0
			Resource nextResource;
			if (nextProb == 0) {
				set.add(Resource.DESERT);
			} else if (nextProb < -1 && whitelistName != null && allowedWhitelists.containsKey(whitelistName)
					&& allowedWhitelists.get(whitelistName).contains(Resource.DESERT)) {
				// Special case deserts with probs
				set.add(Resource.DESERT);
			} else if (currentMap.getLandGridResources()[nextIndex] != null) {
				nextResource = currentMap.getLandGridResources()[nextIndex];
				// If we have an assigned resource, use it
				set.add(nextResource);
				avail.remove(nextResource);
			} else {
				set.add(avail.remove(RAND.nextInt(avail.size())));
			}
		}
		
		// Check for deserts at the end
		while (set.size() < probs.size()) {
			set.add(Resource.DESERT);
		}
		
		return set;
	}
	
	private static ArrayList<Resource> getOrderedResources(CatanMap currentMap, ArrayList<Integer> probs) {
		// Contains a mapping to which resources have what so far
		Map<Resource, ArrayList<Integer>> resourceMap = initResourceMap(currentMap);
		// Contains the resources already set
		ArrayList<Resource> set = new ArrayList<Resource>();
		// Contains the resources already consumed in this iteration
		ArrayList<Resource> tried = new ArrayList<Resource>();
		// Contains the (non desert) resources yet to be consumed
		ArrayList<Resource> avail = initAvailResourcesNoDesert(currentMap);
		boolean placedRecently = false;
		
		Map<String, List<Resource>> allowedWhitelists = getAllowedWhitelists(currentMap);
		
		while (!tried.isEmpty() || !avail.isEmpty()) {
			if (placedRecently && !tried.isEmpty()) {
				avail.addAll(tried);
				tried.clear();
				placedRecently = false;
			}
			// Game over: avail is empty and we still have un-set resources. Start over.
			if (avail.isEmpty()) {
				//System.out.println("Restart");
				//System.out.println("  set (" + set.size() + "): " + set);
				//System.out.println("  probs: " + probs);
				resourceMap = initResourceMap(currentMap);
				set.clear();
				tried.clear();
				avail = initAvailResourcesNoDesert(currentMap);
				placedRecently = false;
				allowedWhitelists = getAllowedWhitelists(currentMap);
			} else {
				//System.out.println("Set: " + set);
				//System.out.println("Avail: " + avail);
				//System.out.println("Tried: " + tried);
				//System.out.println("Probs: " + probs);
				
				int nextIndex = set.size();
				int nextProb = probs.get(nextIndex);

				String whitelistName = currentMap.getLandGridWhitelists()[nextIndex];
				
				// Consume
				// Always place a random resource on a -1
				// Always place a desert on a 0
				Resource nextResource;
				if (nextProb == 0) {
					set.add(Resource.DESERT);
					placedRecently = true;
					continue;
				} else if (nextProb < -1 && whitelistName != null && allowedWhitelists.containsKey(whitelistName)
						&& allowedWhitelists.get(whitelistName).contains(Resource.DESERT)) {
					// Special case deserts with probs
					set.add(Resource.DESERT);
					placedRecently = true;
					continue;
				} else if (currentMap.getLandGridResources()[nextIndex] != null) {
					nextResource = currentMap.getLandGridResources()[nextIndex];
					// If we have an assigned resource, use it
					set.add(nextResource);
					resourceMap.get(nextResource).add(nextProb);
					avail.remove(nextResource);
					placedRecently = true;
					continue;
				} else {
					nextResource = avail.remove(RAND.nextInt(avail.size()));
				}
				
				//System.out.println("Consuming: " + nextResource);
				boolean canPlaceHere = true;
				
				// Check to see if there's a whitelist
				if (whitelistName != null && allowedWhitelists.containsKey(whitelistName)
						&& !allowedWhitelists.get(whitelistName).contains(nextResource)) {
					canPlaceHere = false;
				}
				
				if (nextProb == -1 && canPlaceHere) { // Just use it
					set.add(nextResource);
					resourceMap.get(nextResource).add(nextProb);
					placedRecently = true;
					if (whitelistName != null && allowedWhitelists.containsKey(whitelistName)) {
						allowedWhitelists.get(whitelistName).remove(nextResource);
					}
					continue;
				}
				
				// Check neighbors for same resource.
				if (!currentMap.getName().equals("the_pirate_islands")) {
					for (int neighbor : currentMap.getLandNeighbors()[nextIndex]) {
						//Log.i("XXX XXX XXX", "    " + neighbor);
						if (neighbor >= set.size()) {
							// Do nothing, it is not yet occupied
						} else {
							if (set.get(neighbor) == nextResource) {
								canPlaceHere = false;
								break;
							} else {
								// Do nothing, at least this neighbor isn't the same
							}
						}
					}
				}
				
				// If this number is already used by the same resource
				if (resourceMap.get(nextResource).contains(nextProb) && !currentMap.getName().equals("the_pirate_islands")) {
					canPlaceHere = false;
				}
				
				// No 6s or 8s on golds
				if (nextResource == Resource.GOLD && (probs.get(nextIndex) == 6 || probs.get(nextIndex) == 8)) {
					canPlaceHere = false;
				}
				

				// If this is the last resource, check the probs of this resource
				NumberOfResource numOfResource = nextResource.getNumOfResource();
				ArrayList<Integer> tmpProbs = new ArrayList<Integer>();
				tmpProbs.addAll(resourceMap.get(nextResource));
				tmpProbs.add(nextProb);
				if (!tmpProbs.contains(-1)) {
					// Skip missing probs
					if (numOfResource == NumberOfResource.LOW
							&& tmpProbs.size() == currentMap.getLowResourceNumber()) {
						if (sumProbability(tmpProbs) < 3*currentMap.getLowResourceNumber()
								|| sumProbability(tmpProbs) > 4*currentMap.getLowResourceNumber()) {
							canPlaceHere = false;
						}
						if (!isBalanced(tmpProbs)) {
							canPlaceHere = false;
						}
					} else if (numOfResource == NumberOfResource.HIGH
							&& tmpProbs.size() == currentMap.getHighResourceNumber()) {
						if (sumProbability(tmpProbs) < 3*currentMap.getHighResourceNumber()
								|| sumProbability(tmpProbs) > 4*currentMap.getHighResourceNumber()) {
							canPlaceHere = false;
						}
					}
				}
				
				if (canPlaceHere) {
					set.add(nextResource);
					resourceMap.get(nextResource).add(nextProb);
					placedRecently = true;
					if (whitelistName != null && allowedWhitelists.containsKey(whitelistName)) {
						allowedWhitelists.get(whitelistName).remove(nextResource);
					}
				} else {
					tried.add(nextResource);
				}				
			}
		}
		
		// Check for deserts at the end
		while (set.size() < probs.size()) {
			set.add(Resource.DESERT);
		}

		//System.out.println("Finished: " + set);
		return set;
	}
	
	public static ArrayList<Harbor> getHarbors(CatanMap currentMap, MapType currentType,
			ArrayList<Resource> resourceList,	ArrayList<Integer> numberList) {
		switch(currentType) {
			case TRADITIONAL:
				return getTraditionalHarbors(currentMap);
			case FAIR:
				//return getRandomHarbors(currentMap);
				return getOrderedHarbors(currentMap, resourceList, numberList);
			case RANDOM:
				return getRandomHarbors(currentMap);
			default:
				return null;
		}
	}

	/**
	 * These have to be hardcoded since they're so ugly
	 */
	private static ArrayList<Harbor> getTraditionalHarbors(CatanMap currentMap) {
		ArrayList<Resource> toTake = initHarbors(currentMap);
		ArrayList<Harbor> harbors = new ArrayList<Harbor>();

		int i = 0;
		for (int dir : currentMap.getOrderedHarbors()) {
			if (dir == -1) { // Empty water
				harbors.add(new Harbor(i, Resource.WATER, currentMap.getWaterNeighbors()[i][0]));
			} else {
				harbors.add(new Harbor(i, toTake.remove(RAND.nextInt(toTake.size())), currentMap.getWaterNeighbors()[i][dir]));
			}
			i++;
		}
    
    return harbors;
	}
	
	private static ArrayList<Harbor> getRandomHarbors(CatanMap currentMap) {
		ArrayList<Resource> toTake = initHarbors(currentMap);
		ArrayList<Harbor> harbors = new ArrayList<Harbor>();
		
		// First fill with water
		for (int i = 0; i < currentMap.getWaterGrid().length; i++) {
			harbors.add(new Harbor(i, Resource.WATER, -1));      
		}
		
		while (!toTake.isEmpty()) {
			// Make sure that we're not overwriting a good harbor already placed
			int rand = RAND.nextInt(currentMap.getWaterGrid().length);
			while (harbors.get(rand).getResource() != Resource.WATER) {
				rand = RAND.nextInt(currentMap.getWaterGrid().length);
			}
			
			// If no land, skip it
			if (currentMap.getWaterNeighbors()[rand] == null) {
				continue;
			}

			harbors.get(rand).setResource(toTake.remove(0));
			if (currentMap.getWaterNeighbors()[rand].length > 1) {
				if (isCustom(currentMap)) {
					harbors.get(rand).setFacing(currentMap.getWaterNeighbors()[rand][0]);
				} else {
					harbors.get(rand).setFacing(currentMap.getWaterNeighbors()[rand][RAND.nextInt(2)]);
				}
			} else {
				harbors.get(rand).setFacing(currentMap.getWaterNeighbors()[rand][0]);
			}
		}
		return harbors;
	}

	/**
	 * Given the current resource/probabilities map of Catan, figure out
	 * fair harbors and return a list of them in order. Basic algorithm is
	 * to dole out random harbors and see if it's fair.  If not, try again.
	 */
	private static ArrayList<Harbor> getOrderedHarbors(CatanMap currentMap, ArrayList<Resource> resourceList,
			ArrayList<Integer> numberList) {
		ArrayList<Resource> resources;
		ArrayList<Harbor> harbors;

		while (true) {
			int pos = 0;
			harbors = new ArrayList<Harbor>();
			resources = initHarbors(currentMap);

			// Quick coin-flip to see to start with open ocean or not
			boolean coinFlip = RAND.nextBoolean();
			while (harbors.size() < currentMap.getWaterGrid().length) {
				// If we're out of harbors, just fill the rest in with water
				if (resources.isEmpty()) {
					harbors.add(new Harbor(pos, Resource.WATER, -1));
					pos++;
					continue;
				}
				
				int nextResourceIndex = RAND.nextInt(resources.size());
				if (coinFlip) {
					if (currentMap.getName().equals("new_world")) {
						boolean skip = false;
						while (neighborConflict(currentMap, harbors, pos)) {
							harbors.add(new Harbor(pos, Resource.WATER, -1));
							pos++;
							if (pos >= currentMap.getWaterGrid().length) {
								skip = true;
								break;
							}
						}
						if (skip) {
							break;
						}
					}
					
					// no land around it
					if (currentMap.getWaterNeighbors()[pos] == null) {
						harbors.add(new Harbor(pos, Resource.WATER, -1));
					} else {
						if (currentMap.getWaterNeighbors()[pos].length > 1) {
							if (isCustom(currentMap)) {
								harbors.add(new Harbor(pos, resources.remove(nextResourceIndex),
										currentMap.getWaterNeighbors()[pos][0]));
							} else {
								harbors.add(new Harbor(pos, resources.remove(nextResourceIndex),
										currentMap.getWaterNeighbors()[pos][RAND.nextInt(2)]));
							}
						} else {
							harbors.add(new Harbor(pos, resources.remove(nextResourceIndex),
									currentMap.getWaterNeighbors()[pos][0]));
						}
					}
					pos++;

					if (harbors.size() >= currentMap.getWaterGrid().length) {
						break;
					}

					// no land around it
					if (currentMap.getWaterNeighbors()[pos] == null) {
						harbors.add(new Harbor(pos, Resource.WATER, -1));
					} else {
						harbors.add(new Harbor(pos, Resource.WATER, currentMap.getWaterNeighbors()[pos][0]));
					}
					pos++;
				} else {
					// no land around it
					if (currentMap.getWaterNeighbors()[pos] == null) {
						harbors.add(new Harbor(pos, Resource.WATER, -1));
					} else {
						harbors.add(new Harbor(pos, Resource.WATER, currentMap.getWaterNeighbors()[pos][0]));
					}
					pos++;

					if (harbors.size() >= currentMap.getWaterGrid().length) {
						break;
					}
					
					if (currentMap.getName().equals("new_world")) {
						boolean skip = false;
						while (neighborConflict(currentMap, harbors, pos)) {
							harbors.add(new Harbor(pos, Resource.WATER, -1));
							pos++;
							if (pos >= currentMap.getWaterGrid().length) {
								skip = true;
								break;
							}
						}
						if (skip) {
							break;
						}
					}

					// no land around it
					if (currentMap.getWaterNeighbors()[pos] == null) {
						harbors.add(new Harbor(pos, Resource.WATER, -1));
					} else {
						if (currentMap.getWaterNeighbors()[pos].length > 1) {
							if (isCustom(currentMap)) {
								harbors.add(new Harbor(pos, resources.remove(nextResourceIndex),
										currentMap.getWaterNeighbors()[pos][0]));
							} else {
								harbors.add(new Harbor(pos, resources.remove(nextResourceIndex),
										currentMap.getWaterNeighbors()[pos][RAND.nextInt(2)]));
							}
						} else {
							harbors.add(new Harbor(pos, resources.remove(nextResourceIndex),
									currentMap.getWaterNeighbors()[pos][0]));
						}
					}
					pos++;
				}
			}

			// Check to see if things are fair. A 2:1 harbor can't touch one of it's own terrain
			// tiles of probability 5, 6, 8, or 9.
			boolean goAhead = true;
			for (int i = 0; i < harbors.size(); i++) {
				Harbor harbor = harbors.get(i);
				Resource harborResource = harbor.getResource();

				if (harborResource == Resource.DESERT || harborResource == Resource.WATER) {
					continue;
				} else {
					int[] neighbors = currentMap.getWaterNeighbors()[i];
					for (int neighbor : neighbors) {
						Resource landResource = resourceList.get(neighbor);
						int landNumber = numberList.get(neighbor);
						if (harborResource == landResource && (landNumber >= 5 && landNumber <= 9)) {
							//Log.i("BS", "    Skipping " + harborResource + ", " + landNumber);
							goAhead = false;
							break;
						} else {
							//Log.i("BS", "NOT SKipping " + harborResource + ", " + landNumber);
						}
					}
				}
			}
			if (goAhead) {
				break;
			}
		}

		return harbors;
	}
	
	/**
	 * Helper for getOrderdHarbors for New World
	 */
	private static boolean neighborConflict(CatanMap currentMap, ArrayList<Harbor> harbors, int position) {
		Set<Integer> seenNeighbors = new HashSet<Integer>();
		for (int i = 0; i < harbors.size(); i++) {
			if (harbors.get(i).getResource() != Resource.WATER && currentMap.getWaterNeighbors()[i] != null) {
				seenNeighbors.add(harbors.get(i).getPosition());
				for (int neighbor : currentMap.getWaterWaterNeighbors()[i]) {
					seenNeighbors.add(neighbor);
				}
			}
		}

		// Check neighbors conflicting with neighbors
		if (currentMap.getWaterWaterNeighbors()[position] != null) {
			for (int positionNeighbor : currentMap.getWaterWaterNeighbors()[position]) {
				if (seenNeighbors.contains(positionNeighbor)) {
					return true;
				}
			}
		}
		// Also check itself
		return seenNeighbors.contains(position);
	}

	/**
	 * Private helper function that takes in a harbor and returns the
	 * orientation of its two arms facing inland.  In other words, 0
	 * if it is facing "left" (according to the harbor facing inward
	 * to the land tiles) or 1 for "right".  Or 0 if it has no choice.
	 * Used by the paint function.
	 */
	public static int whichWayHarborFaces(CatanMap currentMap, Harbor harbor) {
		int pos = harbor.getPosition();
		
		if (currentMap.getWaterNeighbors()[pos] == null) {
			return -1;
		}
		
		int len = currentMap.getWaterNeighbors()[pos].length;
		if (len == 1) {
			return 0;
		} else { // len = 2
			if (harbor.getFacing() == currentMap.getWaterNeighbors()[pos][0]) {
				return 0;
			} else {
				return 1;
			}
		}
	}
	
	/**
	 * Helper function for getOrderedResources
	 */
	private static Map<String, List<Resource>> getAllowedWhitelists(CatanMap currentMap) {
		Map<String, List<Resource>> allowedWhitelists = new HashMap<String, List<Resource>>();
		if (currentMap.getLandResourceWhitelists() != null) {
			for (Map.Entry<String, List<Resource>> entry : currentMap.getLandResourceWhitelists().entrySet()) {
				allowedWhitelists.put(entry.getKey(), new ArrayList<Resource>());
				for (Resource resource : entry.getValue()) {
					allowedWhitelists.get(entry.getKey()).add(resource);
				}
			}
		}
		return allowedWhitelists;
	}
	
	/**
	 * Helper function for getOrderedResources
	 */
	private static Map<Resource, ArrayList<Integer>> initResourceMap(CatanMap currentMap) {
		Map<Resource, ArrayList<Integer>> resourceMap = new HashMap<Resource, ArrayList<Integer>>();
		for (Resource resource : currentMap.getAvailableResources()) {
			ArrayList<Integer> resourceList = new ArrayList<Integer>();
			resourceMap.put(resource, resourceList);
		}
		return resourceMap;
	}
		
	/**
	 * Helper function for getOrderedResources
	 */
	private static ArrayList<Resource> initAvailResourcesNoDesert(CatanMap currentMap) {
		ArrayList<Resource> avail = new ArrayList<Resource>();
		for (Resource resource : currentMap.getAvailableResources()) {
			if (resource != Resource.DESERT) {
				avail.add(resource);
			}
		}
		return avail;
	}
	
	/**
	 * Add in each of the probability pieces into an array and return it.
	 * Helper function for getOrderedProbabilities()
	 */
	private static ArrayList<Integer> initOrderedProbabilitiesNoZeros(CatanMap currentMap) {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int i : currentMap.getAvailableOrderedProbabilities()) {
			numbers.add(i);
		}
		return numbers;
	}
	
	/**
	 * Add in each of the probability pieces into an array and return it.
	 * Helper function for getOrderedProbabilities()
	 */
	private static ArrayList<Integer> initProbabilities(CatanMap currentMap) {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int i : currentMap.getAvailableProbabilities()) {
			numbers.add(i);
		}
		return numbers;
	}
	
	/**
	 * Add in each of the probability pieces into an array and return it.
	 * Helper function for getOrderedProbabilities()
	 */
	private static ArrayList<Integer> initProbabilitiesNoZeros(CatanMap currentMap) {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int i : currentMap.getAvailableProbabilities()) {
			if (i != 0) {
				numbers.add(i);
			}
		}
		return numbers;
	}

	/**
	 * Make sure that the given array list contains no duplicates.  Returns
	 * true for no duplicates; false otherwise.
	 */
	private static boolean noDuplicates(CatanMap currentMap, ArrayList<Integer> numbers) {
		for (int i = 0; i < numbers.size(); i++) {
			int num = numbers.remove(i);
			if (numbers.contains(num)
					&& !(currentMap.getName().equals("through_the_desert") && num == 0)
					&& !(currentMap.getName().equals("the_wonders_of_catan") && num == 0)) {
				// Be sure to put back at the proper place in the array so
				// that we actually go through all elements
				numbers.add(i, num);
				return false;
			} else {
				numbers.add(i, num);
			}
		}
		return true;
	}

	/**
	 * Make sure that within the given array list, no one tile has more than
	 * half the probability (is not more than half the sum of the rest).
	 */
	private static boolean isBalanced(ArrayList<Integer> numbers) {
		for (int i = 0; i < numbers.size(); i++) {
			int currentNumber = numbers.remove(i);
			if (PROBABILITY_MAPPING[currentNumber]
			                                 > sumProbability(numbers)) {
				return false;
			}
			numbers.add(i, currentNumber);
		}
		return true;
	}
	
	/**
	 * Sums up the integers in the array list and returns it the sum.
	 */
	private static int sumProbability(ArrayList<Integer> numbers) {
		int sum = 0;
		for (int number : numbers) {
			sum += PROBABILITY_MAPPING[number];
		}
		return sum;
	}
	
	/**
	 * Add in each of the harbor pieces into an array and return it.
	 * Helper function for getHarbors()
	 */
	private static ArrayList<Resource> initHarbors(CatanMap currentMap) {
		ArrayList<Resource> harbors = new ArrayList<Resource>();
		for (Resource resource : currentMap.getAvailableHarbors()) {
			harbors.add(resource);
		}
		return harbors;
	}
	
	private static boolean isCustom(CatanMap currentMap) {
		Set<String> nonCustomNames = new HashSet<String>();
		nonCustomNames.add("standard");
		nonCustomNames.add("large");
		nonCustomNames.add("xlarge");
		return !nonCustomNames.contains(currentMap.getName());
	}
}
