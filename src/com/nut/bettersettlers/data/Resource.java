package com.nut.bettersettlers.data;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public enum Resource implements Parcelable {
	DESERT(0xFFf0dc82, NumberOfResource.DESERT, "desert"),
	WHEAT(0xFFfad111, NumberOfResource.HIGH, "wheat"),
	CLAY(0xFFb22222, NumberOfResource.LOW, "clay"),
	ROCK(0xFF9e9e9e, NumberOfResource.LOW, "rock"),
	SHEEP(0xFF66ce5f, NumberOfResource.HIGH, "sheep"),
	WOOD(0xFF0c9302, NumberOfResource.HIGH, "wood"),
	WATER(0xFF00aeef, NumberOfResource.WATER, "water"),
	GOLD(0xFFAF7817, NumberOfResource.GOLD, "gold");

	private static final Map<String, Resource> JSON_KEY_MAP = new HashMap<String, Resource>();
	static {
		for (Resource resource : Resource.values()) {
			JSON_KEY_MAP.put(resource.jsonKey, resource);
		}
	}
	public static Resource getResourceByJson(String key) {
		return JSON_KEY_MAP.get(key);
	}
	
	public final int color;
	public final int numOfResource;
	public final String jsonKey;
	
	private Resource(int color, int numOfResource, String jsonKey) {
		this.color = color;
		this.numOfResource = numOfResource;
		this.jsonKey = jsonKey;
	}

    public static final Parcelable.Creator<Resource> CREATOR = new Parcelable.Creator<Resource>() {
        public Resource createFromParcel(Parcel in) {
            return Resource.values()[in.readInt()];
        }

        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };
	
	@Override
	public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
		out.writeInt(ordinal());
    }
	
	@Override
	public String toString() {
		return jsonKey;
	}
}
