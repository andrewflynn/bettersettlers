package com.nut.bettersettlers.data;

import com.nut.bettersettlers.util.Util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The simple class that represents a harbor.  Position is a number
 * from 0-n for which harbor it is (0 is TL corner and goes Clockwise). 
 * Note that "desert"==3:1 trading, water=no harbor and resource=2:1
 * of that resource.  Facing is a variable referring to which tile the
 * harbor's arms are facing, according to the numbering of the land tiles.
 */
public final class Harbor implements Parcelable {
	public final int position;
	public final Resource resource;
	public final int facing;
	
	public Harbor(int position, Resource resource, int facing) {
		this.position = position;
		this.resource = resource;
		this.facing = facing;
	}
	
	private Harbor(Parcel in) {
		position = in.readInt();
		resource = in.readParcelable(getClass().getClassLoader());
		facing = in.readInt();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Harbor)) {
			return false;
		}
		Harbor that = (Harbor) other;
		
		return Util.equal(this.position, that.position)
		    && Util.equal(this.resource, that.resource)
		    && Util.equal(this.facing, that.facing);
	}
	
	@Override
	public int hashCode() {
		return Util.hashCode(position, resource, facing);
	}

    public static final Parcelable.Creator<Harbor> CREATOR = new Parcelable.Creator<Harbor>() {
        public Harbor createFromParcel(Parcel in) {
            return new Harbor(in);
        }

        public Harbor[] newArray(int size) {
            return new Harbor[size];
        }
    };
	
	@Override
	public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(position);
        out.writeParcelable(resource, flags);
        out.writeInt(facing);
    }
	
	@Override
	public String toString() {
		return "[Harbor:" +
				" position: " + position +
				" resource: " + resource +
				" facing:   " + facing +
				" ]"+ "\n";
	}
}
