package com.nut.bettersettlers.data;

import java.io.Serializable;

public final class MapConsts {
	private MapConsts() {}
	
	public static enum MapType { TRADITIONAL, FAIR, RANDOM; }
	
	public static enum NumberOfResource {
		DESERT, LOW, HIGH, WATER, GOLD;
	}
	
	public static enum Resource {
		DESERT(0xFFf0dc82, NumberOfResource.DESERT, "desert"),
		WHEAT(0xFFfad111, NumberOfResource.HIGH, "wheat"),
		CLAY(0xFFb22222, NumberOfResource.LOW, "clay"),
		ROCK(0xFF9e9e9e, NumberOfResource.LOW, "rock"),
		SHEEP(0xFF66ce5f, NumberOfResource.HIGH, "sheep"),
		WOOD(0xFF0c9302, NumberOfResource.HIGH, "wood"),
		WATER(0xFF00aeef, NumberOfResource.WATER, "water"),
		GOLD(0xFFAF7817, NumberOfResource.GOLD, "gold");
		
		private final int color;
		private final NumberOfResource num;
		private final String jsonKey;
		
		private Resource(int color, NumberOfResource num, String jsonKey) {
			this.color = color;
			this.num = num;
			this.jsonKey = jsonKey;
		}
		public int getColor() {
			return color;
		}
		public NumberOfResource getNumOfResource() {
			return num;
		}
		public String getJsonKey() {
			return jsonKey;
		}
		
		public static Resource findResourceByJson(String str) {
			for (Resource resource : Resource.values()) {
				if (str.equals(resource.getJsonKey())) {
					return resource;
				}
			}
			return null;
		}
		
		@Override
		public String toString() {
			return getJsonKey();
		}
	}
	
	/**
	 * The simple class that represents a harbor.  Position is a number
	 * from 0-n for which harbor it is (0 is TL corner and goes Clockwise). 
	 * Note that "desert"==3:1 trading, water=no harbor and resource=2:1
	 * of that resource.  Facing is a variable referring to which tile the
	 * harbor's arms are facing, according to the numbering of the land tiles.
	 */
	public static class Harbor implements Serializable {
		private int position;
		private Resource resource;
		private int facing;
		
		public Harbor(int position, Resource resource, int facing) {
			this.position = position;
			this.resource = resource;
			this.facing = facing;
		}

		public int getPosition() {
			return position;
		}
		public void setPosition(int position) {
			this.position = position;
		}
		public Resource getResource() {
			return resource;
		}
		public void setResource(Resource resource) {
			this.resource = resource;
		}
		public int getFacing() {
			return facing;
		}
		public void setFacing(int facing) {
			this.facing = facing;
		}
		
		@Override
		public String toString() {
			return new StringBuilder("[Harbor: ").append("\n")
				.append("  position: ").append(position)
				.append("  resource: ").append(resource)
				.append("  facing:   ").append(facing)
				.toString();
		}
	}
	
	public static class Piece {
		private final int gridX;
		private final int gridY;
		private final int color;
		
		public Piece(int gridX, int gridY, int color) {
			this.gridX = gridX;
			this.gridY = gridY;
			this.color = color;
		}

		public int getGridX() {
			return gridX;
		}
		public int getGridY() {
			return gridY;
		}
		public int getColor() {
			return color;
		}
	}
	
	/**
	 * Mapping between the numbers that are shown (rolled on the dice) and the probability of each
	 * being rolled.
	 */
	public static final int[] PROBABILITY_MAPPING =
	  { 0,  // 0
        0,  // 1
        1,  // 2
        2,  // 3
        3,  // 4
        4,  // 5
        5,  // 6
        0,  // 7
        5,  // 8
        4,  // 9
        3,  // 10
        2,  // 11
        1}; // 12
	
	public static final int BOARD_RANGE_X = 19;
	public static final int BOARD_RANGE_HALF_X = 9;
	public static final int BOARD_RANGE_Y = 11;
	public static final int BOARD_RANGE_HALF_Y = 6;
}
