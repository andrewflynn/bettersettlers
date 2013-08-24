package com.nut.bettersettlers.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.graphics.Point;

import com.nut.bettersettlers.util.Util;

public final class CatanMap {
	/** The name of this map. */
	public final String name;
	
	/** The display title of this map. */
	public final String title;

	/** How many rocks/clays there are available on to distribute. */
	public final int lowResourceNumber;

	/** How many wheats/woods/sheep there are available on to distribute. */
	public final int highResourceNumber;

	/** The assigned probability for each point (if it exists). */
	public final int[] landGridProbabilities;

	/** The assigned resource for each point (if it exists). */
	public final Resource[] landGridResources;

	/** The (x,y) coordinates of the each land hexagon. */
	public final Point[] landGrid;

	/** The list of whitelists for each point (if it exists). */
	public final String[] landGridWhitelists;

	/** The whitelist of resources that can be on a certain land grid piece */
	public final Map<String, List<Resource>> landResourceWhitelists;

	/** The whitelist of probabilities that can be on a certain land grid piece */
	public final Map<String, List<Integer>> landProbabilityWhitelists;

	/** The order in which the land grid is laid out. */
	public final int[] landGridOrder;

	/** The (x,y) coordinates of each water hexagon. */
	public final Point[] waterGrid;

	/**
	 * This list contains 2 or 3 numbers that are the possible corners that the lines can go to
	 * (half only touch two at 2 corners and half touch at 3 corners).  0 is the TL corner, 1
	 * is the top corner, 2 is the TR corner, and so forth clockwise around the hexagon to 5 (BL).
	 */
	public final int[][] harborLines;

	/**
	 * This list of lists contains the land tiles that each land tile is neighbors with.
	 * The land tiles are numbered 0-18 starting at the TL corner and going L -> R, T -> B.
	 */
	public final int[][] landNeighbors;

	/**
	 * This list of lists contains the land tiles that each ocean tile is neighbors with.
	 * The ocean tiles are (similar to everywhere else) numbered starting at the TL ocean
	 * tile and going around clockwise 0-17.  The land tiles are (similar to everywhere else)
	 * numbered 0-18 starting at the TL land tile and going L -> R, T -> B.
	 * Note that the numbers are not small to large but are numbered clockwise by whichever land
	 * tile is earliest on in the clockwise rotation (starting in the TL corner).
	 */
	public final int[][] waterNeighbors;

	/**
	 * This list of list is like waterNeighbors except it contains the ocean tiles each ocean tile
	 * is neighbors with (not including itself)
	 */
	public final int[][] waterWaterNeighbors;

	/**
	 * "Triplets" are defined as three terrain tiles that come together at an intersection
	 * (ports do not count). These are ordered starting in the TL corner going L -> R, T -> B
	 * (going straight across such that the top three terrain tiles are the "top two" for the
	 * first triplets. These are defined so that we can make sure no single settlement placement
	 * is too amazing.
	 */
	public final int[][] landIntersections;

	/** This is a mapping between STANDARD_LAND_GRID and STANDARD_LAND_INTERSECTIONS. */
	public final int[][] landIntersectionIndexes;

	/** 
	 * This is a way to identify places in between the hexes. This is not a unique mapping, but
	 * the X represents which hex its referring to and the Y represents which direction off of
	 * the hex its pointing.
	 */
	public final int[][] placementIndexes;

	/** List of how many of each resource this type of board contains. */
	public final Resource[] availableResources;

	/** List of how many of each probability this type of board contains. */
	public final int[] availableProbabilities;

	/** List of how many of each probability this type of board contains. */
	public final int[] availableOrderedProbabilities;

	/** List of how many of harbors there are (desert is 3:1). */
	public final Resource[] availableHarbors;

	/** Hardcoded directions of harbors on "traditional" maps.  Hardcoded since they're weird. */
	public final int[] orderedHarbors;

	/** The (x,y) coordinates of the each unknown hexagon. */
	public final Point[] unknownGrid;

	/** List of how many of each resource this type of board contains for unknown only. */
	public final Resource[] availableUnknownResources;

	/** List of how many of each probability this type of board contains for unknown only. */
	public final int[] availableUnknownProbabilities;

	/** List of which placement degrees around each piece are disallowed. Null if no blacklist. */
	public final List<int[]> placementBlacklists;

	/** Order of any land converted to water so we can keep the same map for New World on a rotation. */
	public final ArrayList<Integer> theftOrder;
	
	private CatanMap(Builder builder) {
		this.name = builder.name;
		this.title = builder.title;
		this.lowResourceNumber = builder.lowResourceNumber;
		this.highResourceNumber = builder.highResourceNumber;
		this.landGridProbabilities = builder.landGridProbabilities;
		this.landGridResources = builder.landGridResources;
		this.landGrid = builder.landGrid;
		this.landGridWhitelists = builder.landGridWhitelists;
		this.landResourceWhitelists = builder.landResourceWhitelists;
		this.landProbabilityWhitelists = builder.landProbabilityWhitelists;
		this.landGridOrder = builder.landGridOrder;
		this.waterGrid = builder.waterGrid;
		this.harborLines = builder.harborLines;
		this.landNeighbors = builder.landNeighbors;
		this.waterNeighbors = builder.waterNeighbors;
		this.waterWaterNeighbors = builder.waterWaterNeighbors;
		this.landIntersections = builder.landIntersections;
		this.landIntersectionIndexes = builder.landIntersectionIndexes;
		this.placementIndexes = builder.placementIndexes;
		this.availableResources = builder.availableResources;
		this.availableProbabilities = builder.availableProbabilities;
		this.availableOrderedProbabilities = builder.availableOrderedProbabilities;
		this.availableHarbors = builder.availableHarbors;
		this.orderedHarbors = builder.orderedHarbors;
		this.unknownGrid = builder.unknownGrid;
		this.availableUnknownResources = builder.availableUnknownResources;
		this.availableUnknownProbabilities = builder.availableUnknownProbabilities;
		this.placementBlacklists = builder.placementBlacklists;
		this.theftOrder = builder.theftOrder;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CatanMap)) {
			return false;
		}
		CatanMap that = (CatanMap) other;
		
		return Util.equal(this.name, that.name)
		    && Util.equal(this.title, that.title)
		    && Util.equal(this.lowResourceNumber, that.lowResourceNumber)
		    && Util.equal(this.highResourceNumber, that.highResourceNumber)
		    && Util.equal(this.landGridProbabilities, that.landGridProbabilities)
		    && Util.equal(this.landGridResources, that.landGridResources)
		    && Util.equal(this.landGrid, that.landGrid)
		    && Util.equal(this.landGridWhitelists, that.landGridWhitelists)
		    && Util.equal(this.landResourceWhitelists, that.landResourceWhitelists)
		    && Util.equal(this.landProbabilityWhitelists, that.landProbabilityWhitelists)
		    && Util.equal(this.landGridOrder, that.landGridOrder)
		    && Util.equal(this.waterGrid, that.waterGrid)
		    && Util.equal(this.harborLines, that.harborLines)
		    && Util.equal(this.landNeighbors, that.landNeighbors)
		    && Util.equal(this.waterNeighbors, that.waterNeighbors)
		    && Util.equal(this.waterWaterNeighbors, that.waterWaterNeighbors)
		    && Util.equal(this.landIntersections, that.landIntersections)
		    && Util.equal(this.landIntersectionIndexes, that.landIntersectionIndexes)
		    && Util.equal(this.placementIndexes, that.placementIndexes)
		    && Util.equal(this.availableResources, that.availableResources)
		    && Util.equal(this.availableProbabilities, that.availableProbabilities)
		    && Util.equal(this.availableOrderedProbabilities, that.availableOrderedProbabilities)
		    && Util.equal(this.availableHarbors, that.availableHarbors)
		    && Util.equal(this.orderedHarbors, that.orderedHarbors)
		    && Util.equal(this.unknownGrid, that.unknownGrid)
		    && Util.equal(this.availableUnknownResources, that.availableUnknownResources)
		    && Util.equal(this.availableUnknownProbabilities, that.availableUnknownProbabilities)
		    && Util.equal(this.placementBlacklists, that.placementBlacklists)
		    && Util.equal(this.theftOrder, that.theftOrder);
	}
	
	@Override
	public int hashCode() {
		return Util.hashCode(name, title, lowResourceNumber, highResourceNumber,
				landGridProbabilities, landGridResources, landGrid, landGridWhitelists,
				landResourceWhitelists, landGridOrder, waterGrid, harborLines, landNeighbors,
				waterNeighbors, waterWaterNeighbors, landIntersections, landIntersectionIndexes,
				placementIndexes, availableResources, availableProbabilities,
				availableOrderedProbabilities, availableHarbors, orderedHarbors,
				unknownGrid, availableUnknownResources, availableUnknownProbabilities,
				placementBlacklists, theftOrder);
	}
	
	private String deepToString(List<int[]> array) {
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
		return new StringBuilder("[Settlers Map: (").append(name).append(")").append("\n")
			.append("  Low Resource Number: ").append(lowResourceNumber).append("\n")
			.append("  High Resource Number: ").append(highResourceNumber).append("\n")
			.append("  Land Grid: ").append(Arrays.toString(landGrid)).append("\n")
			.append("  Land Grid Whitelist: ").append(Arrays.toString(landGridWhitelists)).append("\n")
			.append("  Land Grid Probabilities: ").append(Arrays.toString(landGridProbabilities)).append("\n")
			.append("  Land Grid Resources: ").append(Arrays.toString(landGridResources)).append("\n")
			.append("  Land Resource Whitelist: ").append(landResourceWhitelists).append("\n")
			.append("  Land Probability Whitelist: ").append(landProbabilityWhitelists).append("\n")
			.append("  Land Grid Order: ").append(Arrays.toString(landGridOrder)).append("\n")
			.append("  Water Grid: ").append(Arrays.toString(waterGrid)).append("\n")
			.append("  Harbor Lines: ").append(Arrays.deepToString(harborLines)).append("\n")
			.append("  Land Neighbors: ").append(Arrays.deepToString(landNeighbors)).append("\n")
			.append("  Water Neighbors: ").append(Arrays.deepToString(waterNeighbors)).append("\n")
			.append("  Water Water Neighbors: ").append(Arrays.deepToString(waterWaterNeighbors)).append("\n")
			.append("  Land Intersections: ").append(Arrays.deepToString(landIntersections)).append("\n")
			.append("  Land Intersections Size: ").append(landIntersections.length).append("\n")
			.append("  Land Intersection Indexes: ").append(Arrays.deepToString(landIntersectionIndexes)).append("\n")
			.append("  Placement Indexes: ").append(Arrays.deepToString(placementIndexes)).append("\n")
			.append("  Placement Indexes Size: ").append(placementIndexes.length).append("\n")
			.append("  Available Resources: ").append(Arrays.toString(availableResources)).append("\n")
			.append("  Available Probabilities: ").append(Arrays.toString(availableProbabilities)).append("\n")
			.append("  Available Ordered Probabilities: ").append(Arrays.toString(availableOrderedProbabilities)).append("\n")
			.append("  Available Harbors: ").append(Arrays.toString(availableHarbors)).append("\n")
			.append("  Ordered Harbors: ").append(Arrays.toString(orderedHarbors)).append("\n")
			.append("  Placement Blacklists: ").append(deepToString(placementBlacklists)).append("\n")
			.append("]")
			.toString();
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		private String name;
		private String title;
		private int lowResourceNumber;
		private int highResourceNumber;
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
		
		private Builder() {}
		
		public CatanMap build() {
			return new CatanMap(this);
		}
		
		public String getName() {
			return name;
		}

		public String getTitle() {
			return title;
		}

		public int getLowResourceNumber() {
			return lowResourceNumber;
		}

		public int getHighResourceNumber() {
			return highResourceNumber;
		}

		public int[] getLandGridProbabilities() {
			return landGridProbabilities;
		}

		public Resource[] getLandGridResources() {
			return landGridResources;
		}

		public Point[] getLandGrid() {
			return landGrid;
		}

		public String[] getLandGridWhitelists() {
			return landGridWhitelists;
		}

		public Map<String, List<Resource>> getLandResourceWhitelists() {
			return landResourceWhitelists;
		}

		public Map<String, List<Integer>> getLandProbabilityWhitelists() {
			return landProbabilityWhitelists;
		}

		public int[] getLandGridOrder() {
			return landGridOrder;
		}

		public Point[] getWaterGrid() {
			return waterGrid;
		}

		public int[][] getHarborLines() {
			return harborLines;
		}

		public int[][] getLandNeighbors() {
			return landNeighbors;
		}

		public int[][] getWaterNeighbors() {
			return waterNeighbors;
		}

		public int[][] getWaterWaterNeighbors() {
			return waterWaterNeighbors;
		}

		public int[][] getLandIntersections() {
			return landIntersections;
		}

		public int[][] getLandIntersectionIndexes() {
			return landIntersectionIndexes;
		}

		public int[][] getPlacementIndexes() {
			return placementIndexes;
		}

		public Resource[] getAvailableResources() {
			return availableResources;
		}

		public int[] getAvailableProbabilities() {
			return availableProbabilities;
		}

		public int[] getAvailableOrderedProbabilities() {
			return availableOrderedProbabilities;
		}

		public Resource[] getAvailableHarbors() {
			return availableHarbors;
		}

		public int[] getOrderedHarbors() {
			return orderedHarbors;
		}

		public Point[] getUnknownGrid() {
			return unknownGrid;
		}

		public Resource[] getAvailableUnknownResources() {
			return availableUnknownResources;
		}

		public int[] getAvailableUnknownProbabilities() {
			return availableUnknownProbabilities;
		}

		public List<int[]> getPlacementBlacklists() {
			return placementBlacklists;
		}

		public ArrayList<Integer> getTheftOrder() {
			return theftOrder;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setLowResourceNumber(int lowResourceNumber) {
			this.lowResourceNumber = lowResourceNumber;
			return this;
		}

		public Builder setHighResourceNumber(int highResourceNumber) {
			this.highResourceNumber = highResourceNumber;
			return this;
		}

		public Builder setLandGridProbabilities(int[] landGridProbabilities) {
			this.landGridProbabilities = landGridProbabilities;
			return this;
		}

		public Builder setLandGridResources(Resource[] landGridResources) {
			this.landGridResources = landGridResources;
			return this;
		}

		public Builder setLandGrid(Point[] landGrid) {
			this.landGrid = landGrid;
			return this;
		}

		public Builder setLandGridWhitelists(String[] landGridWhitelists) {
			this.landGridWhitelists = landGridWhitelists;
			return this;
		}

		public Builder setLandResourceWhitelists(
				Map<String, List<Resource>> landResourceWhitelists) {
			this.landResourceWhitelists = landResourceWhitelists;
			return this;
		}

		public Builder setLandProbabilityWhitelists(
				Map<String, List<Integer>> landProbabilityWhitelists) {
			this.landProbabilityWhitelists = landProbabilityWhitelists;
			return this;
		}

		public Builder setLandGridOrder(int[] landGridOrder) {
			this.landGridOrder = landGridOrder;
			return this;
		}

		public Builder setWaterGrid(Point[] waterGrid) {
			this.waterGrid = waterGrid;
			return this;
		}

		public Builder setHarborLines(int[][] harborLines) {
			this.harborLines = harborLines;
			return this;
		}

		public Builder setLandNeighbors(int[][] landNeighbors) {
			this.landNeighbors = landNeighbors;
			return this;
		}

		public Builder setWaterNeighbors(int[][] waterNeighbors) {
			this.waterNeighbors = waterNeighbors;
			return this;
		}

		public Builder setWaterWaterNeighbors(int[][] waterWaterNeighbors) {
			this.waterWaterNeighbors = waterWaterNeighbors;
			return this;
		}

		public Builder setLandIntersections(int[][] landIntersections) {
			this.landIntersections = landIntersections;
			return this;
		}

		public Builder setLandIntersectionIndexes(int[][] landIntersectionIndexes) {
			this.landIntersectionIndexes = landIntersectionIndexes;
			return this;
		}

		public Builder setPlacementIndexes(int[][] placementIndexes) {
			this.placementIndexes = placementIndexes;
			return this;
		}

		public Builder setAvailableResources(Resource[] availableResources) {
			this.availableResources = availableResources;
			return this;
		}

		public Builder setAvailableProbabilities(int[] availableProbabilities) {
			this.availableProbabilities = availableProbabilities;
			return this;
		}

		public Builder setAvailableOrderedProbabilities(int[] availableOrderedProbabilities) {
			this.availableOrderedProbabilities = availableOrderedProbabilities;
			return this;
		}

		public Builder setAvailableHarbors(Resource[] availableHarbors) {
			this.availableHarbors = availableHarbors;
			return this;
		}

		public Builder setOrderedHarbors(int[] orderedHarbors) {
			this.orderedHarbors = orderedHarbors;
			return this;
		}

		public Builder setUnknownGrid(Point[] unknownGrid) {
			this.unknownGrid = unknownGrid;
			return this;
		}

		public Builder setAvailableUnknownResources(Resource[] availableUnknownResources) {
			this.availableUnknownResources = availableUnknownResources;
			return this;
		}

		public Builder setAvailableUnknownProbabilities(int[] availableUnknownProbabilities) {
			this.availableUnknownProbabilities = availableUnknownProbabilities;
			return this;
		}

		public Builder setPlacementBlacklists(List<int[]> placementBlacklists) {
			this.placementBlacklists = placementBlacklists;
			return this;
		}

		public Builder setTheftOrder(ArrayList<Integer> theftOrder) {
			this.theftOrder = theftOrder;
			return this;
		}
	}
}
