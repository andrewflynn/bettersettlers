package com.nut.bettersettlers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.graphics.Point;

import com.nut.bettersettlers.data.MapConsts.Resource;

public abstract class CatanMap {
	private String name;
	private int lowResourceNumber;
	private int highResourceNumber;
	private int landWaterNumber;
	private int[] landGridProbabilities;
	private Resource[] landGridResources;
	private Point[] landGrid;
	private String[] landGridWhitelists;
	private Map<String, List<Resource>> landResourceWhitelists;
	private Map<String, List<Integer>> landProbabilityWhitelists;
	private int[] landGridOrder;
	private Point[] waterGrid;
	private int[][] harborLines;
	private int[][] landNeighbors;
	private int[][] waterNeighbors;
	private int[][] waterWaterNeighbors;
	private int[][] landIntersections;
	private int[][] landIntersectionIndexes;
	private int[][] placementIndexes;
	private Resource[] availableResources;
	private int[] availableProbabilities;
	private int[] availableOrderedProbabilities;
	private Resource[] availableHarbors;
	private int[] orderedHarbors;
	private Point[] unknownGrid;
	private Resource[] availableUnknownResources;
	private int[] availableUnknownProbabilities;
	private List<int[]> placementBlacklists;
	private ArrayList<Integer> theftOrder;
	
	/** The name of this map. */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/** How many rocks/clays there are available on to distribute. */
	public int getLowResourceNumber() {
		return lowResourceNumber;
	}
	public void setLowResourceNumber(int lowResourceNumber) {
		this.lowResourceNumber = lowResourceNumber;
	}

	/** How many wheats/woods/sheep there are available on to distribute. */
	public int getHighResourceNumber() {
		return highResourceNumber;
	}
	public void setHighResourceNumber(int highResourceNumber) {
		this.highResourceNumber = highResourceNumber;
	}
	
	/** How many land pieces should actually be ocean. */
	public int getLandWaterNumber() {
		return landWaterNumber;
	}
	public void setLandWaterNumber(int landWaterNumber) {
		this.landWaterNumber = landWaterNumber;
	}
	
	/** The (x,y) coordinates of the each land hexagon. */
	public Point[] getLandGrid() {
		return landGrid;
	}
	public void setLandGrid(Point[] landGrid) {
		this.landGrid = landGrid;
	}
	
	/** The list of whitelists for each point (if it exists). */
	public void setLandGridWhitelists(String[] landGridWhitelists) {
		this.landGridWhitelists = landGridWhitelists;
	}
	public String[] getLandGridWhitelists() {
		return landGridWhitelists;
	}
	
	/** The assigned probability for each point (if it exists). */
	public int[] getLandGridProbabilities() {
		return landGridProbabilities;
	}
	public void setLandGridProbabilities(int[] landGridProbabilities) {
		this.landGridProbabilities = landGridProbabilities;
	}
	
	/** The assigned resource for each point (if it exists). */
	public Resource[] getLandGridResources() {
		return landGridResources;
	}
	public void setLandGridResources(Resource[] landGridResources) {
		this.landGridResources = landGridResources;
	}
	
	/** The whitelist of resources that can be on a certain land grid piece */
	public Map<String, List<Resource>> getLandResourceWhitelists() {
		return landResourceWhitelists;
	}
	public void setLandResourceWhitelists(Map<String, List<Resource>> landResourceWhitelists) {
		this.landResourceWhitelists = landResourceWhitelists;
	}
	
	/** The whitelist of probabilities that can be on a certain land grid piece */
	public Map<String, List<Integer>> getLandProbabilityWhitelists() {
		return landProbabilityWhitelists;
	}
	public void setLandProbabilityWhitelists(Map<String, List<Integer>> landProbabilityWhitelists) {
		this.landProbabilityWhitelists = landProbabilityWhitelists;
	}
	
	/** The order in which the land grid is laid out. */
	public int[] getLandGridOrder() {
		return landGridOrder;
	}
	public void setLandGridOrder(int[] landGridOrder) {
		this.landGridOrder = landGridOrder;
	}

	/** The (x,y) coordinates of each water hexagon. */
	public Point[] getWaterGrid() {
		return waterGrid;
	}
	public void setWaterGrid(Point[] waterGrid) {
		this.waterGrid = waterGrid;
	}

	/**
	 * This list contains 2 or 3 numbers that are the possible corners that the lines can go to
	 * (half only touch two at 2 corners and half touch at 3 corners).  0 is the TL corner, 1
	 * is the top corner, 2 is the TR corner, and so forth clockwise around the hexagon to 5 (BL).
	 */
	public int[][] getHarborLines() {
		return harborLines;
	}
	public void setHarborLines(int[][] harborLines) {
		this.harborLines = harborLines;
	}
	
	/**
	 * This list of lists contains the land tiles that each land tile is neighbors with.
	 * The land tiles are numbered 0-18 starting at the TL corner and going L -> R, T -> B.
	 */
	public int[][] getLandNeighbors() {
		return landNeighbors;
	}
	public void setLandNeighbors(int[][] landNeighbors) {
		this.landNeighbors = landNeighbors;
	}

	/**
	 * This list of lists contains the land tiles that each ocean tile is neighbors with.
	 * The ocean tiles are (similar to everywhere else) numbered starting at the TL ocean
	 * tile and going around clockwise 0-17.  The land tiles are (similar to everywhere else)
	 * numbered 0-18 starting at the TL land tile and going L -> R, T -> B.
	 * Note that the numbers are not small to large but are numbered clockwise by whichever land
	 * tile is earliest on in the clockwise rotation (starting in the TL corner).
	 */
	public int[][] getWaterNeighbors() {
		return waterNeighbors;
	}
	public void setWaterNeighbors(int[][] waterNeighbors) {
		this.waterNeighbors = waterNeighbors;
	}

	/**
	 * This list of list is like waterNeighbors except it contains the ocean tiles each ocean tile
	 * is neighbors with (not including itself)
	 */
	public int[][] getWaterWaterNeighbors() {
		return waterWaterNeighbors;
	}
	public void setWaterWaterNeighbors(int[][] waterWaterNeighbors) {
		this.waterWaterNeighbors = waterWaterNeighbors;
	}
	
	/**
	 * "Triplets" are defined as three terrain tiles that come together at an intersection
	 * (ports do not count). These are ordered starting in the TL corner going L -> R, T -> B
	 * (going straight across such that the top three terrain tiles are the "top two" for the
	 * first triplets. These are defined so that we can make sure no single settlement placement
	 * is too amazing.
	 */
	public int[][] getLandIntersections() {
		return landIntersections;
	}
	public void setLandIntersections(int[][] landIntersections) {
		this.landIntersections = landIntersections;
	}

	/** This is a mapping between STANDARD_LAND_GRID and STANDARD_LAND_INTERSECTIONS. */
	public int[][] getLandIntersectionIndexes() {
		return landIntersectionIndexes;
	}
	public void setLandIntersectionIndexes(int[][] landIntersectionIndexes) {
		this.landIntersectionIndexes = landIntersectionIndexes;
	}

	/** 
	 * This is a way to identify places in between the hexes. This is not a unique mapping, but
	 * the X represents which hex its referring to and the Y represents which direction off of
	 * the hex its pointing.
	 */
	public int[][] getPlacementIndexes() {
		return placementIndexes;
	}
	public void setPlacementIndexes(int[][] placementIndexes) {
		this.placementIndexes = placementIndexes;
	}

	/** List of how many of each resource this type of board contains. */
	public Resource[] getAvailableResources() {
		return availableResources;
	}
	public void setAvailableResources(Resource[] availableResources) {
		this.availableResources = availableResources;
	}

	/** List of how many of each probability this type of board contains. */
	public int[] getAvailableProbabilities() {
		return availableProbabilities;
	}
	public void setAvailableProbabilities(int[] availableProbabilities) {
		this.availableProbabilities = availableProbabilities;
	}

	/** List of how many of each probability this type of board contains. */
	public int[] getAvailableOrderedProbabilities() {
		return availableOrderedProbabilities;
	}
	public void setAvailableOrderedProbabilities(int[] availableOrderedProbabilities) {
		this.availableOrderedProbabilities = availableOrderedProbabilities;
	}

	/** List of how many of harbors there are (desert is 3:1). */
	public Resource[] getAvailableHarbors() {
		return availableHarbors;
	}
	public void setAvailableHarbors(Resource[] availableHarbors) {
		this.availableHarbors = availableHarbors;
	}

	/** Hardcoded directions of harbors on "traditional" maps.  Hardcoded since they're weird. */
	public int[] getOrderedHarbors() {
		return orderedHarbors;
	}
	public void setOrderedHarbors(int[] orderedHarbors) {
		this.orderedHarbors = orderedHarbors;
	}
	
	/** The (x,y) coordinates of the each unknown hexagon. */
	public Point[] getUnknownGrid() {
		return unknownGrid;
	}
	public void setUnknownGrid(Point[] unknownGrid) {
		this.unknownGrid = unknownGrid;
	}

	/** List of how many of each resource this type of board contains for unknown only. */
	public Resource[] getAvailableUnknownResources() {
		return availableUnknownResources;
	}
	public void setAvailableUnknownResources(Resource[] availableUnknownResources) {
		this.availableUnknownResources = availableUnknownResources;
	}

	/** List of how many of each probability this type of board contains for unknown only. */
	public int[] getAvailableUnknownProbabilities() {
		return availableUnknownProbabilities;
	}
	public void setAvailableUnknownProbabilities(int[] availableUnknownProbabilities) {
		this.availableUnknownProbabilities = availableUnknownProbabilities;
	}
	
	/** List of which placement degrees around each piece are disallowed. Null if no blacklist. */
	public List<int[]> getPlacementBlacklists() {
		return placementBlacklists;
	}
	public void setPlacementBlacklists(List<int[]> placementBlacklists) {
		this.placementBlacklists = placementBlacklists;
	}
	
	/** Order of any land converted to water so we can keep the same map for New World on a rotation. */
	public ArrayList<Integer> getTheftOrder() {
		return theftOrder;
	}
	public void setTheftOrder(ArrayList<Integer> theftOrder) {
		this.theftOrder = theftOrder;
	}
	
	protected String deepToString(List<int[]> array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int[] arr : array) {
			sb.append(Arrays.toString(arr)).append(", ");
		}
		sb.append(" ]");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return new StringBuilder("[Settlers Map: (").append(getName()).append(")").append("\n")
			.append("  Low Resource Number: ").append(getLowResourceNumber()).append("\n")
			.append("  High Resource Number: ").append(getHighResourceNumber()).append("\n")
			.append("  Land Grid: ").append(Arrays.toString(getLandGrid())).append("\n")
			.append("  Land Grid Whitelist: ").append(Arrays.toString(getLandGridWhitelists())).append("\n")
			.append("  Land Grid Probabilities: ").append(Arrays.toString(getLandGridProbabilities())).append("\n")
			.append("  Land Grid Resources: ").append(Arrays.toString(getLandGridResources())).append("\n")
			.append("  Land Resource Whitelist: ").append(getLandResourceWhitelists()).append("\n")
			.append("  Land Probability Whitelist: ").append(getLandProbabilityWhitelists()).append("\n")
			.append("  Land Grid Order: ").append(Arrays.toString(getLandGridOrder())).append("\n")
			.append("  Water Grid: ").append(Arrays.toString(getWaterGrid())).append("\n")
			.append("  Harbor Lines: ").append(Arrays.deepToString(getHarborLines())).append("\n")
			.append("  Land Neighbors: ").append(Arrays.deepToString(getLandNeighbors())).append("\n")
			.append("  Water Neighbors: ").append(Arrays.deepToString(getWaterNeighbors())).append("\n")
			.append("  Water Water Neighbors: ").append(Arrays.deepToString(getWaterWaterNeighbors())).append("\n")
			.append("  Land Intersections: ").append(Arrays.deepToString(getLandIntersections())).append("\n")
			.append("  Land Intersections Size: ").append(getLandIntersections().length).append("\n")
			.append("  Land Intersection Indexes: ").append(Arrays.deepToString(getLandIntersectionIndexes())).append("\n")
			.append("  Placement Indexes: ").append(Arrays.deepToString(getPlacementIndexes())).append("\n")
			.append("  Placement Indexes Size: ").append(getPlacementIndexes().length).append("\n")
			.append("  Available Resources: ").append(Arrays.toString(getAvailableResources())).append("\n")
			.append("  Available Probabilities: ").append(Arrays.toString(getAvailableProbabilities())).append("\n")
			.append("  Available Ordered Probabilities: ").append(Arrays.toString(getAvailableOrderedProbabilities())).append("\n")
			.append("  Available Harbors: ").append(Arrays.toString(getAvailableHarbors())).append("\n")
			.append("  Ordered Harbors: ").append(Arrays.toString(getOrderedHarbors())).append("\n")
			.append("  Placement Blacklists: ").append(deepToString(getPlacementBlacklists())).append("\n")
			.append("]")
			.toString();
	}
}
